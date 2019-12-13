package com.salabon.lazycards;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import NetworkScanner.NetworkScannerActivity;

public class MainFragment extends Fragment {
    private static final String TAG = "MainFragment";

    private EditText mVocabWord;
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
        // TODO send intent to create a netscanner activity
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

    // Before committing to a submit, we check the length first, then create the async
    private void submitWord(){
        String editWord = mVocabWord.getText().toString();
        if (editWord != null && !editWord.isEmpty()){
            //execute async
            return;
        }

        Toast.makeText(getActivity(), R.string.no_word_toast, Toast.LENGTH_SHORT).show();
    }

    // If the IP is not set, create a dialog asking user if they'd like to perform a netscan
    private boolean isIpSet(){
        return DefaultPreferences.getIp(getActivity()) != null;
    }
}
