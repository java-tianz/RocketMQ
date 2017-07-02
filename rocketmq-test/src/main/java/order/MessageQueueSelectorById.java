package order;

import java.util.List;

import com.alibaba.rocketmq.client.producer.MessageQueueSelector;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.common.message.MessageQueue;

/**
 * 同一个业务Id的消息，发送到同一个队列处理
 * 
 * @author Administrator
 *
 */
public class MessageQueueSelectorById implements MessageQueueSelector {
	public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
		Integer id = (Integer) arg;
		int index = id % mqs.size();
		return mqs.get(index);
	}
}