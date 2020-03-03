package com.salabon.lazycards.Cards;

import com.salabon.lazycards.Cards.Constants.Json_Keys;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;


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

    JSONObject toJson(){
        JSONObject payload = new JSONObject();
        String[] tags = splitAndCleanTags();
        String[] options = getSelectedOptionsAsList().toArray(new String[0]);

        try {
            payload.put(Json_Keys.WORD, mVocabWord);
            payload.put(Json_Keys.BACK_CARD, mBackofCard);
            payload.put(Json_Keys.DECK, mDeck);
            payload.put(Json_Keys.API, mApi);

            addArrayToPayload(payload, Json_Keys.TAGS, tags);
            addArrayToPayload(payload, Json_Keys.OPTIONS, options);

        } catch (JSONException e) {
            return null;
        }
        return payload;
    }

    private String[] splitAndCleanTags(){
        String[] tags = mTags.split(" ");

        for(int i = 0; i < tags.length; i++){
            // Get rid of all non-alphabetic characters, should work with unicode
            tags[i] = tags[i].replaceAll("[^\\p{L}]", "");
        }
        return tags;
    }

    private void addArrayToPayload(JSONObject payload, String jsonKey, String[] args) throws JSONException {
        if(args == null) return;

        JSONArray arr = new JSONArray();
        for(String s : args){
            arr.put(s);
        }
        payload.put(jsonKey, arr);
    }
}
