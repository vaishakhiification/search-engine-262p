import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Eight {
    //kick forward

    public static void main(String args[]) {
        Command filterCharacters = new FilterCharacters();
        readFile(args[0], filterCharacters);
    }

    private static void readFile(String inputPath, Command filterCharacters) {
        File inputFile = new File(inputPath);
        List<String> wordList = new ArrayList<>();
        String SPLIT_WORD_REGEX = "[^a-zA-Z0-9]";
        try {
            Scanner input = new Scanner(inputFile);
            while (input.hasNext()) {
                String inputString = input.next();
                String[] inputList = inputString.split(SPLIT_WORD_REGEX);
                wordList.addAll(Arrays.asList(inputList));
            }
            Command normalise = new Normalise();
            filterCharacters.execute(wordList, normalise);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    interface Command {
        void execute(Object obj, Command command);
    }

    @SuppressWarnings("unchecked")
    static class FilterCharacters implements Command {
        String WORD_REGEX = "^[a-zA-Z]*$";

        @Override
        public void execute(Object obj, Command command) {
            List<String> wordList = (ArrayList<String>) obj;
            List<String> filteredList = new ArrayList<>();
            for (int i = 0; i < wordList.size(); i++) {
                String word = wordList.get(i);
                if (word.matches(WORD_REGEX)) {
                    filteredList.add(word);
                    wordList.set(i, word);
                }
            }
            Command removeStopWords = new RemoveStopWords();
            command.execute(wordList, removeStopWords);
        }
    }

    @SuppressWarnings("unchecked")
    static class Normalise implements Command {

        @Override
        public void execute(Object obj, Command command) {
            List<String> wordList = (ArrayList<String>) obj;
            for (int i = 0; i < wordList.size(); i++) {
                String word = wordList.get(i);
                wordList.set(i, word.toLowerCase());
            }
            Command frequencies = new Frequencies();
            command.execute(wordList, frequencies);
        }
    }

    @SuppressWarnings("unchecked")
    static class RemoveStopWords implements Command {
        final static String stopWordPath = "../stop_words.txt";
        private static final String STOP_WORD_SEPARATOR = ",";

        @Override
        public void execute(Object obj, Command command) {
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
                Command sort = new Sort();
                command.execute(filteredList, sort);
            } catch (IOException e) {
                System.out.println("Error reading stop_words");
            }
        }
    }

    @SuppressWarnings("unchecked")
    static class Frequencies implements Command {

        @Override
        public void execute(Object obj, Command command) {
            List<String> wordList = (ArrayList<String>) obj;
            Map<String, Integer> wordCount = new HashMap<>();
            for (String word : wordList) {
                wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
            }
            Command printText = new PrintText();
            command.execute(wordCount, printText);
        }
    }

    @SuppressWarnings("unchecked")
    static class Sort implements Command {

        @Override
        public void execute(Object obj, Command command) {
            Map<String, Integer> wordCount = (HashMap<String, Integer>) obj;
            List<Map.Entry<String, Integer>> list = new LinkedList<>(wordCount.entrySet());
            list.sort((o1, o2) -> (o2.getValue() - o1.getValue()));
            HashMap<String, Integer> sortedMap = new LinkedHashMap<>();
            for (Map.Entry<String, Integer> entry : list) {
                sortedMap.put(entry.getKey(), entry.getValue());
            }
            command.execute(sortedMap, null);
        }
    }

    @SuppressWarnings("unchecked")
    static class PrintText implements Command {
        private static final int WORD_LIST_LIMIT = 25;

        @Override
        public void execute(Object obj, Command command) {
            Map<String, Integer> wordCount = (HashMap<String, Integer>) obj;
            List<Map.Entry<String, Integer>> printList = wordCount.entrySet().stream()
                    .limit(WORD_LIST_LIMIT)
                    .collect(Collectors.toList());

            for (Map.Entry<String, Integer> en : printList) {
                System.out.println(en.getKey() + " - " + en.getValue());
            }
        }
    }
}
