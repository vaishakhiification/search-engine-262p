import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Eleven {
    //letterbox style
    final static String[] messages = {"init", "run", "words", "sorted", "is_stop_word", "increment_count"};

    //base class for Request Message
    public static class Message {
        public String getMessage() {
            return message;
        }

        String message;
    }

    //Request object for DataStorageManager
    private static class DataStorageManagerRequest extends Message {
        private String inputPath;

        public String getInputPath() {
            return inputPath;
        }

        public DataStorageManagerRequest(String message, String inputPath) {
            this.message = message;
            this.inputPath = inputPath;
        }

        public DataStorageManagerRequest(String message) {
            this.message = message;
        }
    }

    //Response object for DataStorageManger
    private static class DataStorageManagerResponse {
        List<String> wordList;

        public List<String> getWordList() {
            return wordList;
        }
    }

    private static class DataStorageManager {
        private static List<String> wordList;
        private static final String SPLIT_WORD_REGEX = "[^a-zA-Z0-9]";
        private static final String WORD_REGEX = "^[a-zA-Z]*$";

        DataStorageManagerResponse dispatch(DataStorageManagerRequest request) {
            DataStorageManagerResponse response = new DataStorageManagerResponse();
            try {
                if (request.getMessage().equals(messages[0])) {
                    initialise(request.getInputPath());
                } else if (request.getMessage().equals(messages[2])) {
                    response.wordList = words();
                } else {
                    throw new Exception("Message not understood");
                }
            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
            }
            return response;
        }

        private void initialise(String inputPath) {
            try {
                File inputFile = new File(inputPath);
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

        List<String> words() {
            return wordList;
        }
    }

    //Request object for StopWordManager
    private static class StopWordManagerRequest extends Message {
        private String currentWord;

        public String getCurrentWord() {
            return currentWord;
        }

        public StopWordManagerRequest(String message) {
            this.message = message;
        }

        public StopWordManagerRequest(String message, String word) {
            this.message = message;
            this.currentWord = word;
        }
    }

    //Response object for StopWordManager
    private static class StopWordManagerResponse {
        private boolean isStopWord;

        public boolean isStopWord() {
            return isStopWord;
        }
    }

    private static class StopWordManager {
        HashSet<String> stopWords;
        final static String stopWordPath = "../stop_words.txt";
        private static final String STOP_WORD_SEPARATOR = ",";

        public StopWordManager() {
            this.stopWords = new HashSet<>();
        }

        public StopWordManagerResponse dispatch(StopWordManagerRequest stopWordManagerRequest) {
            StopWordManagerResponse response = new StopWordManagerResponse();
            try {
                if (stopWordManagerRequest.getMessage().equals(messages[0])) {
                    initialise();
                } else if (stopWordManagerRequest.getMessage().equals(messages[4])) {
                    response.isStopWord = isStopWord(stopWordManagerRequest.getCurrentWord());
                } else {
                    throw new Exception("Message not understood");
                }
            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
            }
            return response;
        }

        private boolean isStopWord(String word) {
            return (word.isEmpty() || stopWords.contains(word));
        }

        private void initialise() {
            try {
                File stopWord = new File(stopWordPath);
                Scanner input = new Scanner(stopWord);
                while (input.hasNext()) {
                    String inputString = input.next();
                    String[] inputArr = inputString.split(STOP_WORD_SEPARATOR);
                    stopWords.addAll(Arrays.asList(inputArr));
                }
            } catch (IOException e) {
                System.out.println("Error reading stop_words");
            }
        }
    }

    //Request object for WordFrequencyManager
    private static class WordFrequencyManagerRequest extends Message {
        String word;

        public String getWord() {
            return word;
        }

        public WordFrequencyManagerRequest(String message) {
            this.message = message;
        }

        public WordFrequencyManagerRequest(String message, String word) {
            this.message = message;
            this.word = word;
        }
    }

    //Response object for WordFrequencyManager
    private static class WordFrequencyManagerResponse {
        private Map<String, Integer> wordCount;

        public Map<String, Integer> getWordCount() {
            return wordCount;
        }
    }

    private static class WordFrequencyManager {
        Map<String, Integer> wordCount;

        public WordFrequencyManager() {
            this.wordCount = new HashMap<>();
        }

        public WordFrequencyManagerResponse dispatch(WordFrequencyManagerRequest wordFrequencyManagerRequest) {
            WordFrequencyManagerResponse response = new WordFrequencyManagerResponse();
            try {
                if (wordFrequencyManagerRequest.getMessage().equals(messages[5])) {
                    incrementCount(wordFrequencyManagerRequest.getWord());
                } else if (wordFrequencyManagerRequest.getMessage().equals(messages[3])) {
                    response.wordCount = sorted();
                } else {
                    throw new Exception("Message not understood");
                }
            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
            }
            return response;
        }

        private Map<String, Integer> sorted() {
            List<Map.Entry<String, Integer>> list = new LinkedList<>(wordCount.entrySet());
            list.sort((o1, o2) -> (o2.getValue() - o1.getValue()));
            HashMap<String, Integer> sortedMap = new LinkedHashMap<>();
            for (Map.Entry<String, Integer> entry : list) {
                sortedMap.put(entry.getKey(), entry.getValue());
            }
            return sortedMap;
        }

        private void incrementCount(String word) {
            wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
        }
    }

    //Request object for WordFrequencyController
    private static class WordFrequencyControllerRequest extends Message {
        private String inputPath;

        public String getInputPath() {
            return inputPath;
        }

        public WordFrequencyControllerRequest(String message) {
            this.message = message;
        }

        public WordFrequencyControllerRequest(String message, String inputPath) {
            this.message = message;
            this.inputPath = inputPath;
        }
    }

    private static class WordFrequencyController {
        private DataStorageManager dataStorageManager;
        private StopWordManager stopWordManager;
        private WordFrequencyManager wordFrequencyManager;
        private static final int WORD_LIST_LIMIT = 25;

        public void dispatch(WordFrequencyControllerRequest request) {
            try {
                if (request.getMessage().equals(messages[0])) {
                    initialise(request.getInputPath());
                } else if (request.getMessage().equals(messages[1])) {
                    run();
                } else {
                    throw new Exception("Message not understood");
                }

            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }

        private void initialise(String inputPath) {
            dataStorageManager = new DataStorageManager();
            stopWordManager = new StopWordManager();
            wordFrequencyManager = new WordFrequencyManager();
            DataStorageManagerRequest dataStorageManagerRequest = new DataStorageManagerRequest(messages[0], inputPath);
            dataStorageManager.dispatch(dataStorageManagerRequest);
            StopWordManagerRequest stopWordManagerRequest = new StopWordManagerRequest(messages[0]);
            stopWordManager.dispatch(stopWordManagerRequest);
        }

        private void run() {
            DataStorageManagerRequest dataStorageManagerRequest = new DataStorageManagerRequest(messages[2]);
            List<String> wordList = dataStorageManager.dispatch(dataStorageManagerRequest).getWordList();

            for (String word : wordList) {
                StopWordManagerRequest stopWordManagerRequest = new StopWordManagerRequest(messages[4], word);
                if (!stopWordManager.dispatch(stopWordManagerRequest).isStopWord()) {
                    WordFrequencyManagerRequest wordFrequencyManagerRequest = new WordFrequencyManagerRequest(messages[5], word);
                    wordFrequencyManager.dispatch(wordFrequencyManagerRequest);
                }
            }

            WordFrequencyManagerRequest wordFrequencyManagerRequest = new WordFrequencyManagerRequest(messages[3]);
            Map<String, Integer> wordFrequencies = wordFrequencyManager.dispatch(wordFrequencyManagerRequest).getWordCount();
            List<Map.Entry<String, Integer>> printList = wordFrequencies.entrySet().stream()
                    .limit(WORD_LIST_LIMIT)
                    .collect(Collectors.toList());
            for (Map.Entry<String, Integer> en : printList) {
                System.out.println(en.getKey() + " - " + en.getValue());
            }
        }
    }

    public static void main(String args[]) {
        WordFrequencyController wordFrequencyController = new WordFrequencyController();
        WordFrequencyControllerRequest request = new WordFrequencyControllerRequest(messages[0], args[0]);
        wordFrequencyController.dispatch(request);
        request = new WordFrequencyControllerRequest(messages[1]);
        wordFrequencyController.dispatch(request);
    }
}
