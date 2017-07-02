/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.alibaba.rocketmq.broker.transaction;

import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.rocketmq.broker.BrokerController;
import com.alibaba.rocketmq.broker.client.ClientChannelInfo;
import com.alibaba.rocketmq.common.constant.LoggerName;
import com.alibaba.rocketmq.common.protocol.header.CheckTransactionStateRequestHeader;
import com.alibaba.rocketmq.store.SelectMapedBufferResult;

public class DefaultTransactionStateChecker implements TransactionStateChecker {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggerName.TransactionLoggerName);

    private final BrokerController brokerController;

    public DefaultTransactionStateChecker(BrokerController brokerController) {
        this.brokerController = brokerController;
    }

    public void check() {
        LOGGER.info("Start to handle lagged transactions");
        Map<String, Set<Long>> laggedTransaction = brokerController.getJdbcTransactionStore().getLaggedTransaction();
        if (null == laggedTransaction || laggedTransaction.isEmpty()) {
            LOGGER.info("No lagged transactions found.");
            return;
        } else {
            LOGGER.info("Lagged transactions found");
        }

        for (final Map.Entry<String, Set<Long>> next : laggedTransaction.entrySet()) {
            Set<Long> offsets = next.getValue();
            for (final Long offset : offsets) {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // Choose a producer to callback.
                            ClientChannelInfo clientChannelInfo = brokerController.getProducerManager()
                                    .pickProducerChannelRandomly(next.getKey());
                            if (clientChannelInfo == null) {
                                LOGGER.warn("No online producer instances of {} is found.", next.getKey());
                                return;
                            }
                            
                            // Retrieve the prepared message.
                            SelectMapedBufferResult selectMapedBufferResult = brokerController.getMessageStore().selectOneMessageByOffset(offset);
                            if (null == selectMapedBufferResult) {
                                LOGGER.error("Try to check transaction state, but found no matched message by commit log offset.");
                                return;
                            }

                            // Send check transaction state request to the chosen producer.
                            CheckTransactionStateRequestHeader requestHeader = new CheckTransactionStateRequestHeader();
                            requestHeader.setCommitLogOffset(offset);
                            //requestHeader.setTranStateTableOffset();
                            
                            LOGGER.info("Try to check producer transaction state against Producer ID: {}, Remoting Address: {}, for Message commit log offset: {}",
                                    clientChannelInfo.getClientId(),
                                    clientChannelInfo.getChannel().remoteAddress(),
                                    offset);
                            
                            brokerController.getBroker2Client().checkProducerTransactionState(
                                    clientChannelInfo.getChannel(), // SocketChannel
                                    requestHeader, //Request header
                                    selectMapedBufferResult // Message body
                            );
                            LOGGER.info("Check-transaction-state request is sent to {}.", clientChannelInfo.getChannel().remoteAddress());
                        } catch (Throwable e) {
                            LOGGER.error("Error while performing check-transaction-state callback.", e);
                        }
                    }
                };
                brokerController.getCheckTransactionStateExecutorService().submit(runnable);
            }
        }

        LOGGER.info("Broker2Client tasks submitted");
    }

}
