package simple;

import com.alibaba.rocketmq.common.ServiceThread;

public class ServiceThreadTest extends ServiceThread {

	@Override
	public void run() {
		while(true){
			System.out.println("子线程：" + System.currentTimeMillis());
			try {
				waitForRunning(60000);
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public String getServiceName() {
		return "ServiceThreadTest";
	}
	
	public static void main(String[] args) {
		ServiceThreadTest st = new ServiceThreadTest();
		st.start();
		while(true){
			System.out.println("主线程：" + System.currentTimeMillis());
			try {
				Thread.sleep(3000);
				st.wakeup();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
