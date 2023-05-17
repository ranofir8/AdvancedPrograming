package ap.ex2.dictionary;
//213630171

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class ParIOSearcher implements FileSearcher {
	private ExecutorService es;
	private List<IOSearcher> searchers;
	
	public ParIOSearcher() {
		this.searchers = new LinkedList<IOSearcher>();
		this.es = Executors.newCachedThreadPool();		
	}
	
	
	@Override
	public boolean search(String word, String... fileNames) {
		ParIOSearcher pIOs = this;
		final int totalCount = fileNames.length;  // how many threads will be run
		
		AtomicInteger finishedCount = new AtomicInteger(0);  // how many threads have finished running
		CompletableFuture<Boolean> hasFoundMatch = new CompletableFuture<>();  // the result of the search
		
		// when found, stop all other threads
		hasFoundMatch.thenAccept(hasFound -> {
			if (hasFound)
				pIOs.stop();
		});
		
		// first populate the searcher's list, and then run all tasks
		List<Runnable> tasks = new LinkedList<>();
		for (String fileName : fileNames) {
			IOSearcher searcher = new IOSearcher();
			this.searchers.add(searcher);
			tasks.add(() -> {
				boolean b = searcher.search(word, fileName);
				
				// publish answer to Future if necessary
				synchronized (hasFoundMatch) {
					// if we are the last thread, complete future anyway
					if ((finishedCount.incrementAndGet() == totalCount && !hasFoundMatch.isDone()) || b) {
						hasFoundMatch.complete(b); // this action will stop all threads, and set ans to true
					}
				}
			});
		}
		
		// add tasks if not stopped already
		synchronized (this) {
			if (es.isTerminated())  // enters here if shutDownNow was called before
				return false;
			else
				tasks.forEach(this.es::execute);  // else, run tasks
			this.es.shutdown();  // no more tasks are allowed in
		}
		
		
		try {
			try {
				return hasFoundMatch.get();  // wait for result and return it
			} catch (ExecutionException e) {
			}
		} catch (InterruptedException e) {}
		return false;
	}
	
	@Override
	public void stop() {
		this.searchers.forEach(IOSearcher::stop);
		synchronized (this) {  // don't get in if we are now adding tasks to this.es
			this.es.shutdownNow();
		}
	}
	
	@Override
	protected void finalize() throws Throwable {
		this.es.shutdown();
	}
}
