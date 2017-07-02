package order;


import java.util.concurrent.atomic.AtomicInteger;

import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.remoting.common.RemotingHelper;

/**
 * 同一个业务id的三条消息，按顺序发送到同一个队列
 * @author Administrator
 *
 */
public class Producer {
	private static AtomicInteger finishNum = new AtomicInteger(0);
	
	 public static void main(String[] args) throws MQClientException, InterruptedException {
		 	final long s = System.currentTimeMillis();
	        final DefaultMQProducer producer = new DefaultMQProducer("YOUR_PRODUCER_GROUP"); // (1)
	        producer.setNamesrvAddr("192.168.0.118:9876;192.168.0.119:9876");
	        producer.start(); // (2)
	        final int count;
	        //if(args != null){
//		        producer.setNamesrvAddr(args[0]);
		        //count = Integer.parseInt(args[0]);
	        //}else{
	        	count = 1000;
	        //}
	        final int orderId = 123456; //订单id
	        final MessageQueueSelectorById messageQueueSelector = new MessageQueueSelectorById();
	        for (int i = 0; i < count; i++) {
	        	final int ii = i;
//                Thread.currentThread().sleep(1000);
//	        	new Thread(new Runnable() {
//					public void run() {
						 try {
			                Message msg = new Message("TopicABC6",// topic // (3)
			                        "TagA",// tag (4)
			                        "id-test-" + ii   ,// key：自定义Key，可以用于去重，可为null
			                        ("Hello RocketMQ " + ii).getBytes(RemotingHelper.DEFAULT_CHARSET)// body (5)
			                );
			                SendResult sendResult = producer.send(msg, messageQueueSelector, orderId); // (6)
			                System.out.println(sendResult);
			                finishNum.incrementAndGet();
			            } catch (Exception e) {
			                e.printStackTrace();
			            }
//					}
//				}).start();
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
						if(finishNum.intValue() == count){
							System.out.println("并发发送" + count + "条消息，耗时：" + (System.currentTimeMillis() - s) + "ms");
							System.out.println("任务执行完毕，准备结束了...");
							producer.shutdown();
							t = false;
						}
					}
				}
			}).start();
	    }
}
