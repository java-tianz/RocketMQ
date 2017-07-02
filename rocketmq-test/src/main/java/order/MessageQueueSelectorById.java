package order;

import java.util.List;

import com.alibaba.rocketmq.client.producer.MessageQueueSelector;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.common.message.MessageQueue;

/**
 * ͬһ��ҵ��Id����Ϣ�����͵�ͬһ�����д���
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