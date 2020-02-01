package com.salabon.lazycards;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import NetworkScanner.NetworkScannerActivity;

public class MainFragment extends Fragment {
    private static final String TAG = "MainFragment";

    private static final String DECK_DIALOG = "deck_select_dialog";
    private static final String SERVER_ERROR_DIALOG = "server_error_dialog";
    private static final String WORD_OPTIONS_DIALOG = "word_options_dialog";

    private static final int REQUEST_DECK = 0;
    private static final int REQUEST_OPTIONS = 1;

    private EditText mVocabWord;
    private EditText mTags;
    private Button mCurrentDeckButton;
    private Button mGetDecksButton;
    private Button mOptionsButton;
    private Button mSubmitButton;
    private Button mTMPNetScanButton;

    private ArrayList<String> mSelectedOptions = new ArrayList<>();
    private int mCurrentApi = Json_Keys.APIs.WORDS;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.main_fragment, container, false);

        mVocabWord = v.findViewById(R.id.vocab_word_edit_text);
        mTags = v.findViewById(R.id.main_fragment_tags_edit_text);

        mCurrentDeckButton = createCurrentDeckButton(v);

        mGetDecksButton = v.findViewById(R.id.get_decks_button);
        mGetDecksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = DeckService.newIntentGetDecks(getActivity());
                getActivity().startService(i);
            }
        });

        mSubmitButton = v.findViewById(R.id.submit_word_button);
        // TODO implement the async to submit to server
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitWord();
            }
        });

        mOptionsButton = v.findViewById(R.id.options_button);
        mOptionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                WordOptionsDialog dialog = WordOptionsDialog.newInstance(mCurrentApi);
                dialog.setTargetFragment(MainFragment.this, REQUEST_OPTIONS);
                dialog.show(manager, WORD_OPTIONS_DIALOG);
            }
        });


        // TODO Delete once network scanner is fully implemented
        // Should be replaced with a dialog
        mTMPNetScanButton = v.findViewById(R.id.TMP_network_scan_button);
        mTMPNetScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = NetworkScannerActivity.newIntent(getActivity());
                startActivity(intent);
            }
        });

        setSelectedOptionsDefault();

        return v;
    }

    @Override
    public void onResume(){
        super.onResume();
        IntentFilter filter = new IntentFilter(DeckService.ACTION_FINISHED);
        getActivity().registerReceiver(mOnDeckServiceFinished, filter, DeckService.PERM_PRIVATE, null);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mOnDeckServiceFinished);
    }


    /**
     * Here we are either setting the current deck or the user options
     * @param requestCode : The request code currently comes from dialogs (DeckSelectDialog
     *                    and WordOptionsDialog
     * @param resultCode : Origin of param is same as above
     * @param data : Origin of param is same as above
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode != Activity.RESULT_OK) return;

        if(requestCode == REQUEST_DECK){
            String selectedDeck = data.getStringExtra(DeckSelectDialog.EXTRA_SELECTED_DECK);
            DefaultPreferences.setCurrentDeck(getActivity(), selectedDeck);
            mCurrentDeckButton.setText(selectedDeck);
        }
        else if(requestCode == REQUEST_OPTIONS){
            ArrayList<String> selectedOptions = data.getStringArrayListExtra(WordOptionsDialog.EXTRA_SELECTED_OPTIONS);
            if(selectedOptions != null && !selectedOptions.isEmpty()){
                mSelectedOptions = selectedOptions;
            }
            else{
                setSelectedOptionsDefault();
            }
        }
    }

    private BroadcastReceiver mOnDeckServiceFinished = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int status = intent.getIntExtra(DeckService.ACTION_STATUS, 0);

            if(status == Anki.ActionResult.SUCCESS){
                Toast.makeText(getActivity(), getString(R.string.updated_deck_list), Toast.LENGTH_SHORT).show();
            }
            else {
                createErrorDialog(status);
            }
        }
    };

    // Before committing to a submit, we check the length first, then create the async
    private void submitWord(){
<<<<<<< Updated upstream
        String editWord = mVocabWord.getText().toString().toLowerCase();
        if (!editWord.isEmpty()){
            String[] tags = splitAndCLeanTags();
            String[] ops = mSelectedOptions.toArray(new String[0]);
            String currDeck = DefaultPreferences.getCurrentDeck(getActivity());

            Intent i = UploadCardService.newIntentCreateCard(getActivity(), editWord,
                    currDeck, mCurrentApi, tags, ops);

            getActivity().startService(i);
=======
        String word = mVocabWord.getText().toString().toLowerCase();
        if(!word.isEmpty()){
            JSONObject payload = createJsonBody(word);
            if(payload != null) {
                Intent i = CardService.newIntentCreate(getActivity(), payload);
                getActivity().startService(i);
            }
>>>>>>> Stashed changes
        }
        else{
            Toast.makeText(getActivity(), R.string.no_word_toast, Toast.LENGTH_SHORT).show();
        }
    }

    private String[] splitAndCleanTags(){
        String tagStr = mTags.getText().toString();
        String[] tags = tagStr.split(" ");

        for(int i = 0; i < tags.length; i++){
            // Get rid of all non-alphabetic characters, should work with unicode
            tags[i] = tags[i].replaceAll("[^\\p{L}]", "");
        }
        return tags;
    }

    private JSONObject createJsonBody(String word){
        JSONObject payload = new JSONObject();
        String currDeck = DefaultPreferences.getCurrentDeck(getActivity());
        String[] tags = splitAndCleanTags();
        String[] options = mSelectedOptions.toArray(new String[0]);

        try {
            payload.put(Json_Keys.WORD, word);
            payload.put(Json_Keys.DECK, currDeck);
            payload.put(Json_Keys.API, mCurrentApi);

            addArrayToPayload(payload, Json_Keys.TAGS, tags);
            addArrayToPayload(payload, Json_Keys.OPTIONS, options);

        } catch (JSONException e) {
            return null;
        }
        return payload;
    }

    private void addArrayToPayload(JSONObject payload, String jsonKey, String[] args) throws JSONException {
        if(args == null) return;

        JSONArray arr = new JSONArray();
        for(String s : args){
            arr.put(s);
        }
        payload.put(jsonKey, arr);
    }


    private void createErrorDialog(int errorCode){
        FragmentManager fragmentManager = getFragmentManager();
        ServerErrorDialog errorDialog = ServerErrorDialog.newInstance(errorCode);
        errorDialog.show(fragmentManager, SERVER_ERROR_DIALOG);
    }
    
    private Button createCurrentDeckButton(View view){
        Button currentDeckButton = view.findViewById(R.id.current_deck_text);

        String currDeck = DefaultPreferences.getCurrentDeck(getActivity());
        if(currDeck != null){
            currentDeckButton.setText(currDeck);
        }

        currentDeckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(DefaultPreferences.getDecks(getActivity()) != null) {
                    FragmentManager fragmentManager = getFragmentManager();
                    DeckSelectDialog dialog = new DeckSelectDialog();
                    dialog.setTargetFragment(MainFragment.this, REQUEST_DECK);
                    dialog.show(fragmentManager, DECK_DIALOG);
                }
                else{
                    Toast.makeText(getActivity(), getString(R.string.no_decks), Toast.LENGTH_LONG).show();
                }
            }
        });

        return currentDeckButton;
    }

    /**
     * There must always be at least ONE default option
     */
    private void setSelectedOptionsDefault(){
        mSelectedOptions.clear();
        if(mCurrentApi == Json_Keys.APIs.WORDS){
            mSelectedOptions.add(Words_API.DEFAULT);
        }
    }
}
