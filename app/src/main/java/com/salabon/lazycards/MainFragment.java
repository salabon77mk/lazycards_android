package com.salabon.lazycards;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import NetworkScanner.NetworkScannerActivity;

public class MainFragment extends Fragment {
    private static final String TAG = "MainFragment";

    private static final String DECK_DIALOG = "deck_select_dialog";
    private static final String SERVER_ERROR_DIALOG = "server_error_dialog";

    private static final int REQUEST_DECK = 0;

    private EditText mVocabWord;
    private EditText mTags;
    private Button mCurrentDeckButton;
    private Button mGetDecksButton;
    private Button mSubmitButton;
    private Button mTMPNetScanButton;

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

        mCurrentDeckButton = v.findViewById(R.id.current_deck_text);
        String currDeck = DefaultPreferences.getCurrentDeck(getActivity());
        if(currDeck != null){
            mCurrentDeckButton.setText(currDeck);
        }
        mCurrentDeckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                DeckSelectDialog dialog = new DeckSelectDialog();
                dialog.setTargetFragment(MainFragment.this, REQUEST_DECK);
                dialog.show(fragmentManager, DECK_DIALOG);
            }
        });

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
                Log.i(TAG, "VocabWord: " + mVocabWord.getText().toString());
                Log.i(TAG, "VocabWord Length: " +
                        mVocabWord.getText().toString().length());
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

    private BroadcastReceiver mOnDeckServiceFinished = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(getActivity(), "WOOO", Toast.LENGTH_SHORT).show();
            int status = intent.getIntExtra(DeckService.ACTION_STATUS, 0);

            if(status == Anki.ActionResult.SUCCESS){

            }
            else {
                createErrorDialog(status);
            }
        }
    };

    // Before committing to a submit, we check the length first, then create the async
    private void submitWord(){
        String editWord = mVocabWord.getText().toString();
        if (!editWord.isEmpty()){
            String[] tags = splitAndCLeanTags();

            //Construct the JSON query could probably made into its own method??
            // TODO forward to service
        }

        Toast.makeText(getActivity(), R.string.no_word_toast, Toast.LENGTH_SHORT).show();
    }

    private String[] splitAndCLeanTags(){
        String tagStr = mTags.getText().toString();
        String[] tags = tagStr.split(" ");

        for(int i = 0; i < tags.length; i++){
            // Get rid of all non-alphabetic characters, should work with unicode
            tags[i] = tags[i].replaceAll("[^\\p{L}]", "");
        }
        return tags;
    }

    // If the IP is not set, create a dialog asking user if they'd like to perform a netscan
    private boolean isIpSet(){
        return DefaultPreferences.getIp(getActivity()) != null;
    }

    public void createErrorDialog(int errorCode){
        FragmentManager fragmentManager = getFragmentManager();
        ServerErrorDialog errorDialog = ServerErrorDialog.newInstance(errorCode);
        errorDialog.show(fragmentManager, SERVER_ERROR_DIALOG);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode != Activity.RESULT_OK) return;

        if(requestCode == REQUEST_DECK){
            String selectedDeck = data.getStringExtra(DeckSelectDialog.EXTRA_SELECTED_DECK);
            DefaultPreferences.setCurrentDeck(getActivity(), selectedDeck);
            mCurrentDeckButton.setText(selectedDeck);
        }
    }


}
