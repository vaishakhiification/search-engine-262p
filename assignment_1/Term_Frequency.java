import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class Term_Frequency {

    final static String stopWordPath = "../stop_words.txt";

    private static final HashSet<String> stop_words = new HashSet<>();
    private static final int WORD_LIST_LIMIT = 25;
    private static final String STOP_WORD_SEPARATOR = ",";
    private static final String WORD_REGEX = "[^a-zA-Z0-9]";

    public static void main(String[] args) {
        try {
            if (args.length == 0)
                throw new Exception("File Name not entered!");
            String inputPath = String.valueOf(args[0]);
            loadStopWords();

            File inputFile = new File(inputPath);
            Scanner input = new Scanner(inputFile);
            Comparator<WordFrequency> comparator = (o1, o2) -> {
                if (o1.count == o2.count)
                    return o1.word.compareTo(o2.word);
                return o2.count - o1.count;
            };
            PriorityQueue<WordFrequency> pq = new PriorityQueue<>(comparator);
            Map<String, Integer> wordCount = new HashMap<>();
            while (input.hasNext()) {
                String inputString = input.next();
                String[] wordList = inputString.split(WORD_REGEX);
                for (String word : wordList) {
                    word = word.trim();
                    word = word.toLowerCase();
                    if (word.length() < 2 || stop_words.contains(word)) {
                        continue;
                    }
                    wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
                }
            }

            for (Map.Entry<String, Integer> entry : wordCount.entrySet()) {
                pq.add(new WordFrequency(entry.getKey(), entry.getValue()));
            }

            int count = 0;
            while (pq.size() > 0 && count < WORD_LIST_LIMIT) {
                WordFrequency word = pq.poll();
                System.out.println(word.word + " - " + word.count);
                count++;
            }
        } catch (FileNotFoundException ex) {
            System.out.println("Input file not found!");
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    private static void loadStopWords() {
        try {
            File stopWord = new File(stopWordPath);
            Scanner input = new Scanner(stopWord);
            while (input.hasNext()) {
                String inputString = input.next();
                String[] inputArr = inputString.split(STOP_WORD_SEPARATOR);
                stop_words.addAll(Arrays.asList(inputArr));
            }
        } catch (IOException e) {
            System.out.println("Error reading stop_words");
        }
    }

    static class WordFrequency {
        final int count;
        final String word;

        WordFrequency(String word, int count) {
            this.count = count;
            this.word = word;
        }
    }
}
