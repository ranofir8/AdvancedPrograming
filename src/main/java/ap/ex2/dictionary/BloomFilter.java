package ap.ex2.dictionary;
// 213630171
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class BloomFilter {
	private final int setSize;
	private final BitSet bitSet;
	private final ArrayList<MessageDigest> hashDigests;
	
	public BloomFilter(int size, String...algs) {
		this.setSize = size;
		this.bitSet = new BitSet();
		this.hashDigests = new ArrayList<MessageDigest>();
		for (String alg : algs)
			try {
				this.hashDigests.add(MessageDigest.getInstance(alg));
			} catch (NoSuchAlgorithmException e) {}
	}
	
	public void add(String word) {
		word = word.toLowerCase();
		this.getSetBitsStream(word).forEach(bitIndex -> bitSet.set(bitIndex));
	}
	
	public boolean contains(String word) {
		return this.getSetBitsStream(word).allMatch(bitIndex -> bitSet.get(bitIndex));
	}
	
	private Stream<Integer> getSetBitsStream(String word) {
		return this.hashDigests.stream()
				.map(md -> md.digest(word.getBytes()))
				.map(bytes -> new BigInteger(bytes))
				.map(bigInt -> bigInt.intValue())
				.map(x -> Math.abs(x))
				.map(x -> x%setSize);
		
		/* not working as expected, but a nicer solution
		 return this.hashDigests.stream()
				.map(md -> md.digest(word.getBytes()))
				.map(bytes -> new BigInteger(bytes))
				.map(bigInt -> bigInt.remainder(BigInteger.valueOf(setSize)))
				.map(bigInt -> bigInt.intValue())
				.map(x -> (x + setSize)%setSize);
		 */
	}
	
	@Override
	public String toString() {
		return IntStream.range(0, this.bitSet.length()).mapToObj(x -> bitSet.get(x)).map(b -> b ? "1" : "0").collect(Collectors.joining());
	}

	public int sum() {
		return IntStream.range(0, this.bitSet.length()).mapToObj(x -> bitSet.get(x)).mapToInt(b -> b ? 1 : 0).sum();
	}
}