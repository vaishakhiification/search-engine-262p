import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Nine {
    //the one
    public static void main(String[] args) {
        TFTheOne one = new TFTheOne(args[0]);
        one.bind(new ReadFile())
                .bind(new FilterCharacters())
                .bind(new Normalise())
                .bind(new RemoveStopWords())
                .bind(new Frequencies())
                .bind(new Sort())
                .bind(new Top25Frequency())
                .printMe();
    }

    interface IFunction {
        Object call(Object obj);
    }

    static class ReadFile implements IFunction {
        public Object call(Object obj) {
            String fileName = (String) obj;
            File inputFile = new File(fileName);
            List<String> wordList = new ArrayList<>();
            String SPLIT_WORD_REGEX = "[^a-zA-Z0-9]";
            try {
                Scanner input = new Scanner(inputFile);
                while (input.hasNext()) {
                    String inputString = input.next();
                    String[] inputList = inputString.split(SPLIT_WORD_REGEX);
                    wordList.addAll(Arrays.asList(inputList));
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return wordList;
        }
    }

    @SuppressWarnings("unchecked")
    static class FilterCharacters implements IFunction {
        String WORD_REGEX = "^[a-zA-Z]*$";

        @Override
        public Object call(Object obj) {
            List<String> wordList = (ArrayList<String>) obj;
            List<String> filteredList = new ArrayList<>();
            for (int i = 0; i < wordList.size(); i++) {
                String word = wordList.get(i);
                if (word.matches(WORD_REGEX)) {
                    filteredList.add(word);
                    wordList.set(i, word);
                }
            }
            return filteredList;
        }
    }

    @SuppressWarnings("unchecked")
    static class Normalise implements IFunction {

        @Override
        public Object call(Object obj) {
            List<String> wordList = (ArrayList<String>) obj;
            for (int i = 0; i < wordList.size(); i++) {
                String word = wordList.get(i);
                wordList.set(i, word.toLowerCase());
            }
            return wordList;
        }
    }

    @SuppressWarnings("unchecked")
    static class RemoveStopWords implements IFunction {
        final static String stopWordPath = "../stop_words.txt";
        private static final String STOP_WORD_SEPARATOR = ",";

        @Override
        public Object call(Object obj) {
            List<String> wordList = (ArrayList<String>) obj;
            List<String> filteredList = new ArrayList<>();
            HashSet<String> stopWords = new HashSet<>();
            try {
                File stopWord = new File(stopWordPath);
                Scanner input = new Scanner(stopWord);
                while (input.hasNext()) {
                    String inputString = input.next();
                    String[] inputArr = inputString.split(STOP_WORD_SEPARATOR);
                    stopWords.addAll(Arrays.asList(inputArr));
                }
                for (String word : wordList) {
                    if (!word.isEmpty() && word.length() >= 2 && !stopWords.contains(word)) {
                        filteredList.add(word);
                    }
                }

            } catch (IOException e) {
                System.out.println("Error reading stop_words");
            }
            return filteredList;
        }
    }

    @SuppressWarnings("unchecked")
    static class Frequencies implements IFunction {

        @Override
        public Object call(Object obj) {
            List<String> wordList = (ArrayList<String>) obj;
            Map<String, Integer> wordCount = new HashMap<>();
            for (String word : wordList) {
                wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
            }
            return wordCount;
        }
    }

    @SuppressWarnings("unchecked")
    static class Sort implements IFunction {

        @Override
        public Object call(Object obj) {
            Map<String, Integer> wordCount = (HashMap<String, Integer>) obj;
            List<Map.Entry<String, Integer>> list = new LinkedList<>(wordCount.entrySet());
            list.sort((o1, o2) -> (o2.getValue() - o1.getValue()));
            HashMap<String, Integer> sortedMap = new LinkedHashMap<>();
            for (Map.Entry<String, Integer> entry : list) {
                sortedMap.put(entry.getKey(), entry.getValue());
            }
            return sortedMap;
        }
    }

    @SuppressWarnings("unchecked")
    static class Top25Frequency implements IFunction {
        private static final int WORD_LIST_LIMIT = 25;

        @Override
        public Object call(Object obj) {
            StringBuilder sb = new StringBuilder();
            Map<String, Integer> wordCount = (HashMap<String, Integer>) obj;
            List<Map.Entry<String, Integer>> printList = wordCount.entrySet().stream()
                    .limit(WORD_LIST_LIMIT)
                    .collect(Collectors.toList());

            for (Map.Entry<String, Integer> en : printList) {
                sb.append(en.getKey() + " - " + en.getValue());
                sb.append("\n");
            }
            return sb.toString();
        }
    }

    private static class TFTheOne {
        private Object value;

        TFTheOne(Object v) {
            value = v;
        }

        public TFTheOne bind(IFunction func) {
            value = func.call(value);
            return this;
        }

        public void printMe() {
            System.out.println(value);
        }
    }
}
