import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Five {

    final static String stopWordPath = "../stop_words.txt";

    //regex for parsing
    private static final String WORD_REGEX = "^[a-zA-Z]*$";
    private static final String SPLIT_WORD_REGEX = "[^a-zA-Z0-9]";
    private static final String STOP_WORD_SEPARATOR = ",";

    //constants
    private static final int WORD_LIST_LIMIT = 25;

    public static void main(String[] args) {

         try {
            if (args.length == 0)
                throw new Exception("File Name not entered!");
            String inputPath = String.valueOf(args[0]);

            //reads the file and populates the wordList
            List<String> wordList = readFile(inputPath);

            //loads the stopWords and populate the stopWords
            HashSet<String> stopWords = loadStopWords();

            //filters the list 
            wordList = filterCharactersAndNormalise(wordList);

            //remove stop words from the list
            wordList = removeStopWords(wordList, stopWords);

            //creates a map with each word being the key and their frequency being the value
            Map<String, Integer> wordCount = frequencies(wordList);
            
            //sort the word list
            wordCount = sort(wordCount);

            //stores the top 25 words in the list
            List<Map.Entry<String, Integer>> printList = wordCount.entrySet().stream()
                    .limit(WORD_LIST_LIMIT)
                    .collect(Collectors.toList());

            //prints the list
            for (Map.Entry<String, Integer> en : printList) {
                System.out.println(en.getKey() + " - " + en.getValue());
            }
        } catch (FileNotFoundException ex) {
            System.out.println("Input file not found!");
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }


    }

    //function to sort the map
    //input: wordCount - map to be sorted
    //output: sortedMap - sorted map 
    private static Map<String, Integer> sort(Map<String, Integer> wordCount) {
        List<Map.Entry<String, Integer>> list = new LinkedList<>(wordCount.entrySet());
        list.sort((o1, o2) -> (o2.getValue() - o1.getValue()));
        HashMap<String, Integer> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    //function to count the frequency of each word
    //input: wordList - list of words
    //output: wordCount - map with each word as the key and their frequency as the value
    private static Map<String, Integer> frequencies(List<String> wordList) {
        Map<String, Integer> wordCount = new HashMap<>();
        for (String word : wordList) {
            wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
        }
        return wordCount;
    }

    //function to load stop word from the given file
    //output: stopWords - set of all stop words
    private static HashSet<String> loadStopWords() {
        HashSet<String> stopWords = new HashSet<>();
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
        return stopWords;
    }

    //function to filter out the stops words 
    //input: wordList - the list of all the words
    //input: stopWords - the set of the stop words to be removed
    //output: updatedWordList - filtered list 
    private static List<String> removeStopWords(List<String> wordList, HashSet<String> stopWords) {
        List<String> updatedWordList = new ArrayList<>();

        for (String word : wordList) {
            if (!word.isEmpty() && !stopWords.contains(word)) {
                updatedWordList.add(word);
            }
        }
        return updatedWordList;
    }

    //filters words according the regex WORD_REGEX
    //input: wordList - list of all the words
    //output: wordList - filtered updated list
    private static List<String> filterCharactersAndNormalise(List<String> wordList) {
        for (int i = 0; i < wordList.size(); i++) {
            String word = wordList.get(i);
            if (word.matches(WORD_REGEX)) {
                wordList.set(i, word.toLowerCase());
            } else {
                wordList.set(i, "");
            }
        }
        return wordList;
    }

    //function to read the file from the given input path and store the words in the list
    //input: inputPath - the path for the input file
    //output: wordList - word list extracted from the input file
    private static List<String> readFile(String inputPath) {
        List<String> wordList = new ArrayList<>();
        try {
            File inputFile = new File(inputPath);
            Scanner input = new Scanner(inputFile);
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
        return wordList;
    }
}
