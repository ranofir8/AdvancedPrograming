package ap.ex2.dictionary;
//213630171
import java.util.HashMap;
import java.util.PriorityQueue;

public class LFU implements CacheReplacementPolicy {
	private static class ItemLFU<T> {
		public final T value;
		private int priority;
		
		private ItemLFU(T value) {
			this.value = value;
			this.priority = 0;
		}
		
		public int getPriority() {
			return this.priority;
		}
		
		public void increasePriority() {
			this.priority++;
		}
	}
	
	private final PriorityQueue<ItemLFU<String>> q;
	private final HashMap<String, ItemLFU<String>> map;
	
	public LFU() {
		this.q = new PriorityQueue<>((ItemLFU<String> arg0, ItemLFU<String> arg1) -> arg0.getPriority() - arg1.getPriority());
		this.map = new HashMap<>();
	}
	
	public void add(String word) {
		ItemLFU<String> item = this.map.get(word);
		if (item != null) {
			item.increasePriority();
			this.q.remove(item);
		} else {
			item = new ItemLFU<String>(word);
			this.map.put(word, item);
		}
		this.q.add(item);
	}
	
	public String remove() {
		if (this.q.isEmpty())
			return null;
		
		ItemLFU<String> item = this.q.remove();
		this.map.remove(item.value);
		return item.value;
	}
}
