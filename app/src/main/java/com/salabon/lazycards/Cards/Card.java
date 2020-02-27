package com.salabon.lazycards.Cards;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Card {
    private String mVocabWord;
    private String mBackofCard;
    private String mDeck;
    private String mTags;
    private String mSelectedOptions;

    private int mApi; // the api used
    
    public String getVocabWord() {
        return mVocabWord;
    }

    public void setVocabWord(String vocabWord) {
        mVocabWord = vocabWord;
    }

    public String getBackofCard() {
        return mBackofCard;
    }

    public void setBackofCard(String backofCard) {
        mBackofCard = backofCard;
    }

    public String getDeck() {
        return mDeck;
    }

    public void setDeck(String deck) {
        mDeck = deck;
    }

    public String getTags() {
        return mTags;
    }

    public void setTags(String tags) {
        mTags = tags;
    }

    public int getApi() {
        return mApi;
    }

    public void setApi(int api) {
        mApi = api;
    }

    public List<String> getSelectedOptionsAsList() {
        return Arrays.asList(mSelectedOptions.split(","));
    }

    // for interacting with database
    public String getSelectOptionsAsString(){
        return mSelectedOptions;
    }

    public void setSelectedOptionsFromList(List<String> selectedOptions) {
        StringBuilder build = new StringBuilder();
        for(String op : selectedOptions){
            build.append(op).append(',');
        }
        mSelectedOptions = build.toString();

        // remove the last comma
        if(mSelectedOptions.length() > 0) {
            mSelectedOptions = mSelectedOptions.substring(0, mSelectedOptions.length() - 1);
        }
    }

    public void setSelectedOptions(String options){
        mSelectedOptions = options;
    }
}
