package com.company;

import java.util.HashMap;

/**
 * Created by hagainuriel on 06/08/2018.
 */
public class ArticleLine {

    public Integer getMaxTagFrequency() {
        return maxTagFrequency;
    }

    public void setMaxTagFrequency(Integer maxTagFrequency) {
        this.maxTagFrequency = maxTagFrequency;
    }

    public enum e_tagType{
        MISC,
        AIMX,
        OWNX,
        CONT,
        BASE
    }

    public enum e_tagger {
        first(1),
        second(2),
        third(3);

        Integer suffix;
        e_tagger(Integer suffix){this.suffix = suffix;}
    }

    private String text;
    //private HashMap<e_tagger,e_tagType> tags;

    private HashMap<e_tagType, Integer> tags;
    private e_tagType lastTag;
    private e_tagType maxTag;
    private Integer maxTagFrequency;


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public HashMap<e_tagType, Integer> getTags() {
        return tags;
    }

    public void setTags(HashMap<e_tagType, Integer> tags) {
        this.tags = tags;
    }

    public e_tagType getProminentTag(){
        if(prominentTagExists()) {
            return maxTag;
        }
        return lastTag;
    }

    public void addTag(e_tagType tagType){
        Integer frequency = tags.containsKey(tagType) ? tags.get(tagType) + 1 : 1;
        tags.put(tagType, frequency);

        if(maxTag == null){
            maxTag = tagType;
            maxTagFrequency = frequency;
        }else{
            if(frequency > maxTagFrequency){
                maxTag = tagType;
                maxTagFrequency = frequency;
            }
        }
    }

    private Boolean prominentTagExists() {
        return tags.values().stream().filter(val -> val >= 2).count() > 0;
    }
}
