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

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.client.producer.TransactionCheckListener;
import com.alibaba.rocketmq.client.producer.TransactionMQProducer;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.remoting.common.RemotingHelper;

public class TransactionProducer {
	private static AtomicInteger finishNum = new AtomicInteger(0);
	
    public static void main(String[] args) throws MQClientException, InterruptedException {

        TransactionCheckListener transactionCheckListener = new TransactionCheckListenerImpl();
        final TransactionMQProducer producer = new TransactionMQProducer("please_rename_unique_group_name");
        producer.setNamesrvAddr("127.0.0.1:9876");
        producer.setCheckThreadPoolMinSize(2);

        producer.setCheckThreadPoolMaxSize(2);

        producer.setCheckRequestHoldMax(2000);
        producer.setTransactionCheckListener(transactionCheckListener);
        producer.start();

        final String[] tags = new String[]{"TagA", "TagB", "TagC", "TagD", "TagE"};
        final TransactionExecuterImpl tranExecuter = new TransactionExecuterImpl();
        
        final int count;
        final int c2;
        if(args != null && args.length > 0){
//	        producer.setNamesrvAddr(args[0]);
	        count = Integer.parseInt(args[0]);
	        c2 = Integer.parseInt(args[1]);
        }else{
        	count = 1000;
        	c2 = 100;
        }
        final long tc = count * c2;
        final CyclicBarrier barrier = new CyclicBarrier(count);
        final CountDownLatch countDownLatch = new CountDownLatch((int) tc);
	 	final long s = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
        	final int ii = i;
        	new Thread(new Runnable() {
				public void run() {
					try {
						barrier.await();
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					} catch (BrokenBarrierException e1) {
						e1.printStackTrace();
					}
					for (int j = 0; j < c2; j++) {
//		                try {
//							Thread.sleep(2000);
//						} catch (InterruptedException e1) {
//							e1.printStackTrace();
//						}
						 try {
							 Message msg =
				                        new Message("Topic_test2", tags[ii % tags.length], /*"KEY" + i,*/
				                                ("Hello RocketMQ " + ii).getBytes(RemotingHelper.DEFAULT_CHARSET));
							 SendResult sendResult = producer.sendMessageInTransaction(msg, tranExecuter, null);
							 System.out.println(sendResult);
							 finishNum.incrementAndGet();
							 countDownLatch.countDown();
			            } catch (Exception e) {
			                e.printStackTrace();
			            }
					}
				}
			}).start();
        }
        
        countDownLatch.await();
        
		if(finishNum.intValue() == tc){
			long us = System.currentTimeMillis() - s;
			double ts = (double) us / 1000;
			if(ts == 0){
				System.out.println("并发发送" + tc + "条消息，耗时：" + us + "ms，tps由于样本太少、时间太短无法计算");
			}else{
				System.out.println("并发发送" + tc + "条消息，耗时：" + us + "ms，tps=" + (tc / ts));
			}
			System.out.println("本次测试任务执行完毕...");
		}
    }
    
}
