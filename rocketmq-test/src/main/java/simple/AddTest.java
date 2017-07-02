package simple;

import java.util.concurrent.atomic.AtomicInteger;

public class AddTest {
	public static void main(String[] args) {
		final Num num = new Num();
		
		for (int i = 0; i < 10000; i++) {
			new Thread(new Runnable() {
				public void run() {
					num.increament();
				}
			}, "t-" + i).start();
		}
	}
}

class Num{
	public int x = 0;
	public AtomicInteger x1 = new AtomicInteger(0);
	
	public void increament(){
		System.out.println("[" + Thread.currentThread().getName() + "]修改前：" + x);
		x++;
		System.out.println("[" + Thread.currentThread().getName() + "]修改后：" + x);
		System.out.println("[" + Thread.currentThread().getName() + "]结束�?...");
	}
	
	public synchronized void increament1(){
		System.out.println("[" + Thread.currentThread().getName() + "]修改前：" + x);
		x++;
		System.out.println("[" + Thread.currentThread().getName() + "]修改后：" + x);
		System.out.println("[" + Thread.currentThread().getName() + "]结束�?...");
	}
	
	public void increament2(){
		System.out.println("[" + Thread.currentThread().getName() + "]修改前：" + x1.get());
		x1.incrementAndGet();
		System.out.println("[" + Thread.currentThread().getName() + "]修改后：" + x1.get());
		System.out.println("[" + Thread.currentThread().getName() + "]结束�?...");
	}
}