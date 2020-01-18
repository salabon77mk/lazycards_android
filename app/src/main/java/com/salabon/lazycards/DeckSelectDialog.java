package com.salabon.lazycards;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Set;

public class DeckSelectDialog extends DialogFragment {
    static final String EXTRA_SELECTED_DECK =
            "com.salabon.lazycards.selected_deck";

    private String mSelectedDeck;

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState){
        Set<String> decks = DefaultPreferences.getDecks(getActivity());
        final CharSequence[] mDecks = decks.toArray(new CharSequence[0]);
        // In Anki, the Default deck cannot be deleted, so there will always be ONE deck available
        mSelectedDeck = mDecks[0].toString();

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.deck_select_dialog_title)
                .setSingleChoiceItems(mDecks, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mSelectedDeck = mDecks[which].toString();
                    }
                })
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_OK, mSelectedDeck);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                })
                .create();
    }

    private void sendResult(int resultCode, String deck){
        if(getTargetFragment() == null) return;

        Intent intent = new Intent();
        intent.putExtra(EXTRA_SELECTED_DECK, deck);

        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, intent);

    }
}
