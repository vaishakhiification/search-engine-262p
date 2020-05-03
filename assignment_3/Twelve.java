import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Twelve {
    //closed maps
    static Map<String, Object> dataStorageObj = new HashMap<String, Object>() {
        {
            put("data", new ArrayList<>());
            put("init", new ExtractWords());
            put("words", (Command) (obj, params) -> dataStorageObj.get("data"));
        }
    };
    static Map<String, Object> stopWordsObj = new HashMap<String, Object>() {
        {
            put("data", new HashSet<>());
            put("init", new LoadStopWords());
            put("is_stop_word", (Command) (obj, params) -> {
                String word = params[0].toString();
                return word.isEmpty() || ((HashSet<String>) stopWordsObj.get("data")).contains(word);
            });
        }

    };
    static Map<String, Object> wordFrequencyObj = new HashMap<String, Object>() {
        {
            put("freqs", new HashMap<>());
            put("increment_count", (Command) (obj, params) -> {
                String word = params[0].toString();
                HashMap<String, Integer> wc = (HashMap<String, Integer>) wordFrequencyObj.get("freqs");
                wc.put(word, wc.getOrDefault(word, 0) + 1);
                return null;
            });
            put("top25", new Top25());
        }
    };

    interface Command {
        Object execute(Map<String, Object> obj, Object params[]);
    }

    static class ExtractWords implements Command {
        public Object execute(Map<String, Object> obj, Object params[]) {
            String pathToFile = params[0].toString();
            List<String> wordList = (ArrayList<String>) obj.get("data");
            String SPLIT_WORD_REGEX = "[^a-zA-Z0-9]";
            String WORD_REGEX = "^[a-zA-Z]*$";
            try {
                File inputFile = new File(pathToFile);
                Scanner input = new Scanner(inputFile);
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
            return null;
        }
    }

    static class LoadStopWords implements Command {

        @Override
        public Object execute(Map<String, Object> obj, Object[] params) {
            String stopWordPath = "../stop_words.txt";
            HashSet<String> stop_words = (HashSet<String>) obj.get("data");
            String STOP_WORD_SEPARATOR = ",";
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
            return null;
        }
    }

    static class Top25 implements Command {
        private static final int WORD_LIST_LIMIT = 25;

        @Override
        public Object execute(Map<String, Object> obj, Object[] params) {
            HashMap<String, Integer> wordCount = (HashMap<String, Integer>) obj.get("freqs");
            List<Map.Entry<String, Integer>> list = new LinkedList<>(wordCount.entrySet());
            list.sort((o1, o2) -> (o2.getValue() - o1.getValue()));
            HashMap<String, Integer> sortedMap = new LinkedHashMap<>();
            for (Map.Entry<String, Integer> entry : list) {
                sortedMap.put(entry.getKey(), entry.getValue());
            }
            List<Map.Entry<String, Integer>> printList = sortedMap.entrySet().stream()
                    .limit(WORD_LIST_LIMIT)
                    .collect(Collectors.toList());
            for (Map.Entry<String, Integer> en : printList) {
                System.out.println(en.getKey() + " - " + en.getValue());
            }
            return null;
        }
    }

    public static void main(String args[]) {
        ((Command) dataStorageObj.get("init")).execute(dataStorageObj, args);
        ((Command) stopWordsObj.get("init")).execute(stopWordsObj, null);

        List<String> wordList = (ArrayList<String>) ((Command) dataStorageObj.get("words")).execute(dataStorageObj, null);
        for (String word : wordList) {
            boolean flag = (boolean) (((Command) stopWordsObj.get("is_stop_word")).execute(stopWordsObj, new Object[]{word}));
            if (!flag) {
                ((Command) wordFrequencyObj.get("increment_count")).execute(wordFrequencyObj, new Object[]{word});
            }
        }
        ((Command) wordFrequencyObj.get("top25")).execute(wordFrequencyObj, null);
    }
}
