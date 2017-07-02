package simple;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPoolTest {
	
	public static void main(String[] args) throws InterruptedException {
		
	}


	/**
	 * 测试数组复制方法
	 */
	private static void TestArraysCopy() {
		T1[] queue = new T1[]{new T1(1), new T1(2), new T1(3), new T1(4)};
		int oldCapacity = 4;
        int newCapacity = oldCapacity + (oldCapacity >> 1); // grow 50%
        if (newCapacity < 0) // overflow
            newCapacity = Integer.MAX_VALUE;
        System.out.println(newCapacity);
        System.out.println(queue);
        T1[] queue1 = Arrays.copyOf(queue, 2);
        queue[1].a = 20;
        queue1[1].a = 200;
        System.out.println(queue1);
	}
	
	static class T1{
		int a = 0;
		
		public T1(int a){
			this.a = a;
		}
	}
	
	/**
	 * 测试固定线程池
	 */
	private static void testFixedThreadPool() {
		ExecutorService es = Executors.newFixedThreadPool(4, new ThreadFactory() {
            private AtomicInteger threadIndex = new AtomicInteger(0);
            
            public Thread newThread(Runnable r) {
                return new Thread(r, "NettyClientPublicExecutor_" + this.threadIndex.incrementAndGet());
            }
        });
		while (true) {
//			Thread.sleep(100);
			es.execute(new Runnable() {
				public void run() {
					int len = new Random().nextInt(10) * 100000;
					for (int i = 0; i < len; i++) {
						System.out.println(Thread.currentThread().getName() + "正在执行任务" + i + "...");
					}
				}
			});
//			es.submit(new Runnable() {
//				public void run() {
//					int len = new Random().nextInt(10) * 100000;
//					for (int i = 0; i < len; i++) {
//						System.out.println(Thread.currentThread().getName() + "正在执行任务" + i + "...");
//					}
//				}
//			});
		}
	}
}
