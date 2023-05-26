package ap.ex2.BookScrabbleServer;

import ap.ex2.dictionary.Dictionary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;


// ************************
public class DictionaryManager {
    private static DictionaryManager staticDict = new DictionaryManager();
    private static final String[] allFiles = "alice_in_wonderland.txt,Frank Herbert - Dune.txt,mobydick.txt,shakespeare.txt,The Matrix.txt,scrubble-sowpods.txt".split(","); // ,bible.txt
    private HashMap<String, Dictionary> dictMap; //map between name of .txt file and a dictionary

    private DictionaryManager() {
        this.dictMap = new HashMap<>();
    }

    public static DictionaryManager get() {
        return staticDict;
    }

    // applys Query or Challenge to all files
    private boolean applyInAllBooks(Function<String, Function<Dictionary, Boolean>> func, String... files) {
        ArrayList<String> filesArrayList = new ArrayList<>(Arrays.asList(files));
        // the last one is the word to search
        String queryWord = filesArrayList.remove(filesArrayList.size() - 1);

        // if the client wants 0 books, put all of the books here:
        if (filesArrayList.size() == 0)
            filesArrayList = new ArrayList<>(Arrays.asList(allFiles));

        for (String fileName : filesArrayList) {
            if (!this.dictMap.containsKey(fileName))
                this.dictMap.put(fileName, new Dictionary(fileName));
        }

        // Stream over files and get the relevant Dictionaries. Apply to them query/challenge, collect all the true/false results into a set
        Set<Boolean> booleanResultsSet = filesArrayList.stream()
                .map(book -> this.dictMap.get(book))
                .map(func.apply(queryWord))
                .collect(Collectors.toSet());
        // Does the resulting set contain 'true'?
        return booleanResultsSet.contains(true);
    }

    // Gets a list of Strings.
    // the last string is the Query, and the others are file names of ap.ex2.bookscrabble.books
    public boolean query(String... files) {
        return this.applyInAllBooks(word -> (dictionary -> dictionary.query(word)), files);
    }

    public boolean challenge(String... files) {
        return this.applyInAllBooks(word -> (dictionary -> dictionary.challenge(word)), files);
    }


    public int getSize() {
        return dictMap.size();
    }
}


//
//        Set<Boolean> t = filesArrayList.stream().map(book -> this.dictMap.get(book)).map(a.apply(queryWord)).collect(Collectors.toSet());
//        return t.contains(true);

        /*
        boolean isFound2 = false;
        for (String file:filesArrayList) {
            Dictionary dict = dictMap.get(file);
           if(false && map.get(queryWord)!=null){
                isFound = true;
                continue;
            }
            if(dict.query(queryWord)){
                isFound2 = true;
            }
        }


        return isFound;

         */