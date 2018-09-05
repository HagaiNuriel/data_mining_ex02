package com.company;

import java.util.HashMap;

/**
 * Created by hagainuriel on 04/09/2018.
 */
public class ArticleLineVector {
    private String prominentWord;
    private HashMap<String,Double> wordsCount = new HashMap<>();

    public void countWord(String word){
        Double count = wordsCount.containsKey(word)? wordsCount.get(word) + 1 : 1;
        wordsCount.put(word, count);

        if(prominentWord == null || prominentWord.isEmpty()){
            prominentWord = word;
        }else{
            if(count > wordsCount.get(prominentWord)) {
                prominentWord = word;
            }
        }
    }

    public String getProminentWord(){
        return this.prominentWord;
    }

    public Double getWordCount(String word){
        return wordsCount.containsKey(word) ? wordsCount.get(word) : 0.0;
    }
}
