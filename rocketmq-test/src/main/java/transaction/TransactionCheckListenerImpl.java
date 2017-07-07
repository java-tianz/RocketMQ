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
package transaction;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import com.alibaba.rocketmq.client.producer.LocalTransactionState;
import com.alibaba.rocketmq.client.producer.TransactionCheckListener;
import com.alibaba.rocketmq.common.message.MessageExt;


public class TransactionCheckListenerImpl implements TransactionCheckListener {
    private AtomicInteger transactionIndex = new AtomicInteger(0);
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public LocalTransactionState checkLocalTransactionState(MessageExt msg) {
    	String str = "检测到本地事务状态为：未知";
        int value = transactionIndex.getAndIncrement();
//        if ((value % 6) == 0) {
//            System.out.println("当前时间：" + sdf.format(new Date()) + ", 检测事务状态异常, 消息详情为：" + msg.toString());
//            throw new RuntimeException("Could not find db");
//        } else if ((value % 3) == 0) {
//        	str = "检测到本地事务状态为：回滚";
//            System.out.println("当前时间：" + sdf.format(new Date()) + ", " + str + ", 消息详情为：" + msg.toString());
//            return LocalTransactionState.ROLLBACK_MESSAGE;
//        } else if ((value % 2) == 0) {
        	str = "检测到本地事务状态为：提交";
            System.out.println("当前时间：" + sdf.format(new Date()) + ", " + str + ", 消息详情为：" + msg.toString());
            return LocalTransactionState.COMMIT_MESSAGE;
//        }
//        System.out.println("当前时间：" + sdf.format(new Date()) + ", " + str + ", 消息详情为：" + msg.toString());
//        return LocalTransactionState.UNKNOW;
    }
}
