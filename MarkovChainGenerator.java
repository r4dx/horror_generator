import java.io.BufferedReader;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.ArrayList;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.lang.Math;
import java.io.IOException;

public class MarkovChainGenerator {

	private static class Pair<K, V> {
		public K k;
		public V v;
		public Pair(K k, V v) {
			this.k = k;
			this.v = v;
		}
	}

	private Path inputPath;
	private Map<String, ArrayList<Pair<String, Integer>>> wordNextWordSorted;
	private int estimatedWords;
	private long sentences;
	private int minWordsInSentence;
	private int maxWordsInSentence;
	private Random r;

	private final char SENTENCE_END = '.';
	private final String SENTENCE_START = "";

	public MarkovChainGenerator(String filename, long sentences, int minWordsInSentence, int maxWordsInSentence) throws IOException {
		this.sentences = sentences;
		this.minWordsInSentence = minWordsInSentence;
		this.maxWordsInSentence = maxWordsInSentence;
		inputPath = Paths.get(filename);
		final int typicalWordSize = 5;
		estimatedWords = (int)Files.size(inputPath) / typicalWordSize;
		r = new Random();
	}

	private Map<String, Map<String, Integer>> buildFreqMap() throws IOException {
		Map<String, Map<String, Integer>> wordNextWordFreq = new HashMap<>(estimatedWords);
		try (BufferedReader br = Files.newBufferedReader(inputPath, StandardCharsets.UTF_8)) {
			StringBuilder word = new StringBuilder();
			String prev = SENTENCE_START;
			while (true) {
				int r = br.read();
				if (r == -1)
					return wordNextWordFreq;
				char c = toSentenceDelimiterIfEnd((char)r);
				if (isWordDelimiter(c)) {
					String ws = word.toString();
					if (ws.equals(""))
						continue;

					Map<String, Integer> nextWordFreq = wordNextWordFreq.get(prev);
					if (nextWordFreq == null)
						nextWordFreq = new HashMap<>();

					nextWordFreq.put(ws, nextWordFreq.getOrDefault(ws, 0) + 1);
					wordNextWordFreq.put(prev, nextWordFreq);
					if (c == SENTENCE_END) {
						prev = SENTENCE_START;
						String se = String.valueOf(SENTENCE_END);
						nextWordFreq.put(se, nextWordFreq.getOrDefault(se, 0) + 1);
					}
					else
						prev = ws;
					word.setLength(0);
					continue;
				}
				word.append(c);
			}
		}
	}

	public void build() throws IOException {
		Map<String, Map<String, Integer>> wordNextWordFreq = buildFreqMap();
		wordNextWordSorted = new HashMap<>(estimatedWords);
		for (String k : wordNextWordFreq.keySet())
			wordNextWordSorted.put(k, sortWeighted(wordNextWordFreq.get(k), null));
	}

	public String generate() {
		if (wordNextWordSorted == null)
			throw new RuntimeException("Trying to generate without build");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < sentences; i++) {
			String w = randomWord(SENTENCE_START, null);
			sb.append(w);
			int counter = 0;
			String se = String.valueOf(SENTENCE_END);
			while (counter < maxWordsInSentence) {
				w = randomWord(w, se);
				if (w.equals(se))
					break;
				sb.append(" ");
				sb.append(w);
				counter++;
				if (w == null || w.equals(se) && counter >= minWordsInSentence)
					break;
			}
			sb.append(". ");
		}
		return sb.toString();
	}


	private ArrayList<Pair<String, Integer>> sortWeighted(Map<String, Integer> map, Integer topK) {
		PriorityQueue<Pair<String, Integer>> top = new PriorityQueue<>( (a, b) -> a.v - b.v );
		for (String k : map.keySet()) {
			int v = map.get(k);
			top.add(new Pair<>(k, v));
			if (topK != null && topK <= top.size())
				top.poll();
		}
		ArrayList<Pair<String, Integer>> r = new ArrayList<>(top.size());
		int s = 0;
		while (top.size() > 0) {
			Pair<String, Integer> kv = top.poll();
			r.add(new Pair<>(kv.k, kv.v + s));
			s += kv.v;
		}
		return r;
	}

	private String randomWord(String prev, String ignore) {
		String result = null;
		ArrayList<Pair<String, Integer>> words = wordNextWordSorted.get(prev);
		if (words == null || (words.size() == 1 && words.get(0).equals(ignore)))
			return String.valueOf(SENTENCE_END);
		while (result == null || result.equals(ignore)) {
			int i = r.nextInt(words.get(words.size() - 1).v) + 1;
			i = Collections.binarySearch(words, new Pair<String, Integer>("", i), (a, b) -> (int)a.v - (int)b.v);
			if (i < 0)
				i = Math.abs(i) - 1;
			result = words.get(i).k;
		}
		return result;
	}

	private boolean isWordDelimiter(char c) {
		char[] delimiters = new char[] { '.', ',', '!', '/', '?', '(', ')', ' ', '-', '\\', '-', '_', '=', '+', '[', '{', ']', '}', ';', ':', /*'\'',*/ '"', '\n', '\r', '\t' };
		return inArray(delimiters, c);
	}
	private char toSentenceDelimiterIfEnd(char c) {
		char[] delimiters = new char[] { '.', '!', '?', '(', ')', ';', '\n', '\r' };
		if (inArray(delimiters, c))
			return SENTENCE_END;
		return c;
	}

	private boolean inArray(char[] arr, char c) {
		for (int i = 0; i < arr.length; i++)
			if (c == arr[i])
				return true;
		return false;
	}

	public static void main(String[] args) throws IOException {
		MarkovChainGenerator g = new MarkovChainGenerator(args[0], 20, 3, 20);
		g.build();
		System.out.println(g.generate());
	};
}
