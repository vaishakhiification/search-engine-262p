import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Four {

    final static String stopWordPath = "../stop_words.txt";

    private static final HashSet<String> stop_words = new HashSet<>();

    private static final String STOP_WORD_SEPARATOR = ",";
    private static final String WORD_REGEX = "^[a-zA-Z]*$";
    private static final String SPLIT_WORD_REGEX = "[^a-zA-Z0-9]";
    private static final int WORD_LIST_LIMIT = 25;

    private static List<String> wordList;
    private static Map<String, Integer> wordCount;

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

    private static void readFile(String inputPath) {
        try {
            File inputFile = new File(inputPath);
            Scanner input = new Scanner(inputFile);
            wordList = new ArrayList<>();
            while (input.hasNext()) {
                String inputString = input.next();
                String[] inputList = inputString.split(SPLIT_WORD_REGEX);
                for (String word : inputList) {
                    if (word.trim().length() >= 2) {
                        wordList.add(word);
                    }
                }
            }

        } catch (FileNotFoundException e) {
            System.out.println("Input File not found!");
        }
    }

    private static void filterCharactersAndNormalise() {

        for (int i = 0; i < wordList.size(); i++) {
            String word = wordList.get(i);
            if (word.matches(WORD_REGEX)) {
                wordList.set(i, word.toLowerCase());
            } else {
                wordList.set(i, "");
            }
        }
    }

    private static void removeStopWords() {
        List<String> updatedWordList = new ArrayList<>();
        loadStopWords();
        for (String word : wordList) {
            if (!word.isEmpty() && !stop_words.contains(word)) {
                updatedWordList.add(word);
            }
        }
        wordList = updatedWordList;
    }

    private static void frequencies() {
        wordCount = new HashMap<>();
        for (String word : wordList) {
            wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
        }
    }

    public static void sort() {
        List<Map.Entry<String, Integer>> list = new LinkedList<>(wordCount.entrySet());
        list.sort((o1, o2) -> (o2.getValue() - o1.getValue()));
        HashMap<String, Integer> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        wordCount = sortedMap;
    }

    public static void main(String[] args) {
        try {
            if (args.length == 0)
                throw new Exception("File Name not entered!");
            String inputPath = String.valueOf(args[0]);

            readFile(inputPath);
            filterCharactersAndNormalise();
            removeStopWords();
            frequencies();
            sort();

            List<Map.Entry<String, Integer>> printList = wordCount.entrySet().stream()
                    .limit(WORD_LIST_LIMIT)
                    .collect(Collectors.toList());

            for (Map.Entry<String, Integer> en : printList) {
                System.out.println(en.getKey() + " - " + en.getValue());
            }
        } catch (FileNotFoundException ex) {
            System.out.println("Input file not found!");
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }
}
