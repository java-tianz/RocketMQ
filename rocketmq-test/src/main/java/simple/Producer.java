package simple;


import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.remoting.common.RemotingHelper;

public class Producer {
	private static AtomicInteger finishNum = new AtomicInteger(0);
	
	 public static void main(String[] args) throws MQClientException, InterruptedException {
	        final DefaultMQProducer producer = new DefaultMQProducer("YOUR_PRODUCER_GROUP"); // (1)
//	        producer.setNamesrvAddr("192.168.0.118:9876;192.168.0.119:9876");
//	        producer.setNamesrvAddr("192.168.0.118:9876");
	        producer.setNamesrvAddr("127.0.0.1:9876");
//	        producer.setNamesrvAddr("192.168.0.149:9876");
//	        producer.setNamesrvAddr("10.0.1.8:9876");
//	        producer.setSendLatencyFaultEnable(true);
//	        producer.setRetryAnotherBrokerWhenNotStoreOK(true);
	        producer.setClientCallbackExecutorThreads(8);
	        producer.start(); // (2)
	        final int count;
	        final int c2;
	        if(args != null && args.length > 0){
//		        producer.setNamesrvAddr(args[0]);
		        count = Integer.parseInt(args[0]);
		        c2 = Integer.parseInt(args[1]);
	        }else{
	        	count = 1;
	        	c2 = 1;
	        }
	        final long tc = count * c2;
	        final CyclicBarrier barrier = new CyclicBarrier(count);
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
//			                try {
//								Thread.sleep(2000);
//							} catch (InterruptedException e1) {
//								e1.printStackTrace();
//							}
							 try {
				                Message msg = new Message("Topic_test2",// topic // (3)
				                        "TagA",// tag (4)
				                         "id-test-" + ii + "_" + j   ,// key：自定义Key，可以用于去重，可为null
				                        ("TopicABC_MSG_" + ii).getBytes(RemotingHelper.DEFAULT_CHARSET)// body (5)
				                );
				                SendResult sendResult = producer.send(msg); // (6)
				                System.out.println(sendResult);
				                finishNum.incrementAndGet();
				            } catch (Exception e) {
				                e.printStackTrace();
				            }
						}
					}
				}).start();
	        }
	        
	        new Thread(new Runnable() {
				public void run() {
					boolean t = true;
					while(t){
//						try {
//							Thread.sleep(500);
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}
						if(finishNum.intValue() == tc){
							long us = System.currentTimeMillis() - s;
							double ts = (double) us / 1000;
							if(ts == 0){
								System.out.println("并发发送" + tc + "条消息，耗时：" + us + "ms，tps由于样本太少、时间太短无法计算");
							}else{
								System.out.println("并发发送" + tc + "条消息，耗时：" + us + "ms，tps=" + (tc / ts));
							}
							System.out.println("任务执行完毕，准备结束了...");
							producer.shutdown();
							t = false;
						}
					}
				}
			}).start();
	    }
}
