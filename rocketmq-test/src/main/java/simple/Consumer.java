package simple;

import java.text.SimpleDateFormat;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.message.MessageExt;

public class Consumer {
	 private static final Logger log = LoggerFactory.getLogger(Consumer.class);

	  public static void main(String[] args) throws InterruptedException, MQClientException {
	        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("YOUR_CONSUMER_GROUP3");
	        consumer.setNamesrvAddr("192.168.0.179:9876");
//	        consumer.setNamesrvAddr("192.168.0.118:9876");
	        //consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_TIMESTAMP);
//	        consumer.setConsumeMessageBatchMaxSize(10);
	        consumer.subscribe("Topic_test1ddxxxdddd", "*");
	        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        consumer.registerMessageListener(new MessageListenerConcurrently() {
	            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
	                                                            ConsumeConcurrentlyContext context) {
	                //System.out.println("当前时间：" + sdf.format(new Date()) + " " + msgs.size());
	                for(MessageExt msg : msgs){
	                	try {
//							System.out.println("msgId=" + msg.getMsgId() + ", body=" + new String(msg.getBody(), "UTF-8"));
//	                		System.out.println("当前时间：" + sdf.format(new Date()) + " " + msg);
//	                		Thread.sleep(new Random().nextInt(1000));
	                		log.info(msg.toString());
						} catch (Exception e) {
							e.printStackTrace();
						}
	                }
//	                context.setAckIndex(2);
	                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
	            }
	        });
	        
	        consumer.start();
	        
	        log.debug("Consumer Started.");
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
