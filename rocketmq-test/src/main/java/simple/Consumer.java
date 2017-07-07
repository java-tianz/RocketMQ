package simple;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.message.MessageExt;

public class Consumer {
	  public static void main(String[] args) throws InterruptedException, MQClientException {
	        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("YOUR_CONSUMER_GROUP3");
//	        consumer.setNamesrvAddr("127.0.0.1:9876");
	        consumer.setNamesrvAddr("192.168.0.118:9876");
	        //consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_TIMESTAMP);
//	        consumer.setConsumeMessageBatchMaxSize(10);
	        consumer.subscribe("Topic_test1", "*");
	        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        consumer.registerMessageListener(new MessageListenerConcurrently() {
	            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
	                                                            ConsumeConcurrentlyContext context) {
	                System.out.println(msgs.size());
	                for(MessageExt msg : msgs){
	                	try {
//							System.out.println("msgId=" + msg.getMsgId() + ", body=" + new String(msg.getBody(), "UTF-8"));
	                		System.out.println("当前时间：" + sdf.format(new Date()) + " " + msgs);
						} catch (Exception e) {
							e.printStackTrace();
						}
	                }
//	                context.setAckIndex(2);
	                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
	            }
	        });
	        
	        consumer.start();
	        
	        System.out.println("Consumer Started.");
//	        
//	        try {
//				System.out.println("查询msg结果：" + consumer.getDefaultMQPushConsumerImpl().getmQClientFactory().getMQAdminImpl().viewMessage("C0A8007600002A9F0000000007B20C5D"));
//			} catch (RemotingException e) {
//				e.printStackTrace();
//			} catch (MQBrokerException e) {
//				e.printStackTrace();
//			}
	  }
}
