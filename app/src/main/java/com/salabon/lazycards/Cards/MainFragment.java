package com.salabon.lazycards.Cards;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.salabon.lazycards.Cards.Constants.Anki;
import com.salabon.lazycards.Cards.Constants.Json_Keys;
import com.salabon.lazycards.Cards.Constants.Words_API;
import com.salabon.lazycards.Cards.Dialogs.DeckSelectDialog;
import com.salabon.lazycards.Cards.Dialogs.ServerErrorDialog;
import com.salabon.lazycards.Cards.Dialogs.WordOptionsDialog;
import com.salabon.lazycards.Cards.Services.CardService;
import com.salabon.lazycards.Cards.Services.DeckService;
import com.salabon.lazycards.Cards.Services.ServerCommService;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.salabon.lazycards.Database.CardDbManager;
import com.salabon.lazycards.R;

public class MainFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "MainFragment";

    private static final String DECK_DIALOG = "deck_select_dialog";
    private static final String SERVER_ERROR_DIALOG = "server_error_dialog";
    private static final String WORD_OPTIONS_DIALOG = "word_options_dialog";

    private static final int REQUEST_DECK = 0;
    private static final int REQUEST_OPTIONS = 1;

    private EditText mVocabWord;
    private EditText mBackOfCard;
    private EditText mTags;
    private Button mCurrentDeckButton;
    private Button mGetDecksButton;
    private Button mOptionsButton;
    private Spinner mApiSpinner;
    private Button mSubmitButton;

    private CheckBox mQueueCheckBox;

    private List<String> mSelectedOptions = new ArrayList<>();
    private int mCurrentApi = Json_Keys.APIs.WORDS;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        mVocabWord = v.findViewById(R.id.vocab_word_edit_text);
        mBackOfCard = v.findViewById(R.id.back_of_card_note);
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

        mSubmitButton = v.findViewById(R.id.submit_word_button);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitWord();
            }
        });


        mApiSpinner = v.findViewById(R.id.api_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.apis, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mApiSpinner.setAdapter(adapter);
        mApiSpinner.setOnItemSelectedListener(this);

        mQueueCheckBox = v.findViewById(R.id.send_to_queue_checkbox);
        if(mQueueCheckBox.isChecked()){
            mQueueCheckBox.setChecked(false);
        }

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
            int status = intent.getIntExtra(ServerCommService.ACTION_STATUS, 0);

            if(status == Anki.ActionResult.SUCCESS){
                Toast.makeText(getActivity(), getString(R.string.success), Toast.LENGTH_SHORT).show();
                clearFields();
            }
            else{
                FragmentManager fragmentManager = getFragmentManager();
                ServerErrorDialog dialog;
                // These handle the custom messages received from either the currently selected API
                // or AnkiConnect
                if(status == Anki.ActionResult.ANKI_CONNECT_ERROR
                        || status == Anki.ActionResult.API_ERROR){
                    String body = intent.getStringExtra(ServerCommService.ERROR_BODY);
                    dialog = ServerErrorDialog.newInstance(status, body);
                }
                else{
                    dialog = ServerErrorDialog.newInstance(status);
                }
                dialog.show(fragmentManager, SERVER_ERROR_DIALOG);
            }
        }
    };

    // Before committing to a submit, we check the length first, then create the async
    private void submitWord(){
        Card card = createCard();
        if(!card.getVocabWord().isEmpty() && !mQueueCheckBox.isChecked()){
            //TODO check if checkbox is checked, if so send straight to queue
            JSONObject payload = card.toJson();
            if(payload != null){
                Intent i = CardService.newIntentCreate(getActivity(), payload);
                getActivity().startService(i);
            }
        }
        else if(!card.getVocabWord().isEmpty() && mQueueCheckBox.isChecked()){
            CardDbManager cardDbManager = CardDbManager.getInstance(getActivity());
            cardDbManager.addCard(card);
            clearFields();
        }
        else{
            Toast.makeText(getActivity(), R.string.no_word_toast, Toast.LENGTH_SHORT).show();
        }
    }

    private Button createCurrentDeckButton(View view){
        Button currentDeckButton = view.findViewById(R.id.current_deck_text);

        String currDeck = DefaultPreferences.getCurrentDeck(getActivity());
        currentDeckButton.setText(currDeck);

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

    private Card createCard(){
        Card card = new Card();
        card.setVocabWord(mVocabWord.getText().toString());
        card.setBackofCard(mBackOfCard.getText().toString());
        card.setDeck(DefaultPreferences.getCurrentDeck(getActivity()));
        card.setTags(mTags.getText().toString());
        card.setApi(mCurrentApi);
        card.setSelectedOptionsFromList(mSelectedOptions);

        return card;
    }

    private void clearFields(){
        mVocabWord.getText().clear();
        mBackOfCard.getText().clear();
        mTags.getText().clear();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mCurrentApi = position;
        if(mCurrentApi == Json_Keys.APIs.NONE){
            mOptionsButton.setEnabled(false);
        }
        else{
            mOptionsButton.setEnabled(true);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
