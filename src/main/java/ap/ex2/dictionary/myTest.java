package ap.ex2.dictionary;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class myTest {
	
	public static void main(String[] args) {
		ParIOSearcher s=new ParIOSearcher();
		new Thread(() -> {try {
				Thread.sleep(2);
			} catch (InterruptedException e) {
			}
			s.stop();
		});
		boolean b = s.search("Bloom", "text1.txt", "text2.txt");
		
		System.out.println("search finished with: " + b);
		s.stop();
	}
	
	
	public static void main2(String[] args) {
		ExecutorService es = Executors.newCachedThreadPool();
		
		es.execute((() -> {
			try {
				
				Thread.sleep(0);  // in order to catch InterruptedException when interrupted later on
				System.out.println("starting task");
				for (int i =0;true;i++) // task is running..
				{
					if (Thread.interrupted())
						System.out.println("i is " + i + "; " + Thread.interrupted());
					
				}
			} catch (InterruptedException e) {
				System.out.println("interrupted :(");
			} finally {
				System.out.println("done task");
			}
		}));
		
		try {
			Thread.sleep(2000);
			System.out.println("2 sec");
			Thread.sleep(2000);
			System.out.println("4 sec");
		} catch (InterruptedException e) {}
		
		System.out.println("is terminated (before): " + es.isTerminated());
		System.out.println("# stopped threads: " + es.shutdownNow().size());
		System.out.println("is terminated (after): " + es.isTerminated());
		
		System.out.println("done main");
	}
	
	/*
	try {
		es.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
	} catch (InterruptedException e) {
	}
	*/

}
