import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Thirteen {

    //abstract things

    //region Interfaces
    interface IDataStorage {
        final static String SPLIT_WORD_REGEX = "[^a-zA-Z0-9]";
        final static String WORD_REGEX = "^[a-zA-Z]*$";

        List<String> words();
    }

    interface IStopWordFilter {
        final static String stopWordPath = "../stop_words.txt";
        final static String STOP_WORD_SEPARATOR = ",";

        boolean isStopWord(String word);
    }

    interface IWordFrequencyCounter {
        void incrementCount(String word);

        Map<String, Integer> sorted();
    }
    //endregion

    //region Concrete Classes
    static class DataStorageManager implements IDataStorage {
        private List<String> wordList;

        public DataStorageManager(String pathToFile) {
            try {
                File inputFile = new File(pathToFile);
                Scanner input = new Scanner(inputFile);
                wordList = new ArrayList<>();
                while (input.hasNext()) {
                    String inputString = input.next();
                    String[] inputList = inputString.split(SPLIT_WORD_REGEX);
                    for (String word : inputList) {
                        if (word.trim().length() >= 2 && word.matches(WORD_REGEX)) {
                            wordList.add(word.toLowerCase());
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                System.out.println("Input File not found!");
            }
        }

        @Override
        public List<String> words() {
            return wordList;
        }
    }

    static class StopWordManager implements IStopWordFilter {
        HashSet<String> stopWords;

        public StopWordManager() {
            try {
                File stopWord = new File(stopWordPath);
                Scanner input = new Scanner(stopWord);
                stopWords = new HashSet<>();
                while (input.hasNext()) {
                    String inputString = input.next();
                    String[] inputArr = inputString.split(STOP_WORD_SEPARATOR);
                    stopWords.addAll(Arrays.asList(inputArr));
                }
            } catch (IOException e) {
                System.out.println("Error reading stop_words");
            }
        }

        @Override
        public boolean isStopWord(String word) {
            return (word.isEmpty() || stopWords.contains(word));
        }
    }

    static class WordFrequencyManager implements IWordFrequencyCounter {
        Map<String, Integer> wordCount;

        public WordFrequencyManager() {
            wordCount = new HashMap<>();
        }

        @Override
        public void incrementCount(String word) {
            wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
        }

        @Override
        public Map<String, Integer> sorted() {
            List<Map.Entry<String, Integer>> list = new LinkedList<>(wordCount.entrySet());
            list.sort((o1, o2) -> (o2.getValue() - o1.getValue()));
            HashMap<String, Integer> sortedMap = new LinkedHashMap<>();
            for (Map.Entry<String, Integer> entry : list) {
                sortedMap.put(entry.getKey(), entry.getValue());
            }
            return sortedMap;
        }
    }

    private static class WordFrequencyController {
        private static final int WORD_LIST_LIMIT = 25;
        IDataStorage dataStorage;
        IStopWordFilter stopWordFilter;
        IWordFrequencyCounter wordFrequencyCounter;

        public WordFrequencyController(String inputPath) {
            dataStorage = new DataStorageManager(inputPath);
            stopWordFilter = new StopWordManager();
            wordFrequencyCounter = new WordFrequencyManager();
        }

        public void run() {
            List<String> wordList = dataStorage.words();
            for (String word : wordList) {
                if (!stopWordFilter.isStopWord(word)) {
                    wordFrequencyCounter.incrementCount(word);
                }
            }

            Map<String, Integer> wordFrequencies = wordFrequencyCounter.sorted();
            List<Map.Entry<String, Integer>> printList = wordFrequencies.entrySet().stream()
                    .limit(WORD_LIST_LIMIT)
                    .collect(Collectors.toList());
            for (Map.Entry<String, Integer> en : printList) {
                System.out.println(en.getKey() + " - " + en.getValue());
            }
        }
    }
    //endregion

    public static void main(String args[]) {
        new WordFrequencyController(args[0]).run();
    }
}
