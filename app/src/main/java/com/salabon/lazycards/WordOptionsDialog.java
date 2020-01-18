package com.salabon.lazycards;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;

class WordOptionsDialog extends DialogFragment {
    static final String EXTRA_SELECTED_OPTIONS =
            "com.salabon.lazycards.selected_options";

    private static final String ARG_API = "api";

    private static final String KEY_PREV_API = "prev_api";
    private static final String KEY_PREV_SELECTED = "prev_selected";

    private int mPreviouslyUsedApi = -1; // -1 ~ no api was previously selected
    private boolean[] mPreviouslySelectedItems;
    private ArrayList<String> mSelectedItems = new ArrayList<>();

    static WordOptionsDialog newWordOptionsDialog(int api){
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_API, api);

        WordOptionsDialog dialog = new WordOptionsDialog();
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        if(savedInstanceState != null){
            mPreviouslyUsedApi = savedInstanceState.getInt(KEY_PREV_API);
            mPreviouslySelectedItems = savedInstanceState.getBooleanArray(KEY_PREV_SELECTED);
        }

        int api = getArguments().getInt(ARG_API);
        final CharSequence[] options = getApiOptions(api);

        // We get the options for the newly selected API and set the default option
        if(mPreviouslyUsedApi != api){
            mPreviouslySelectedItems = new boolean[options.length];
            mPreviouslySelectedItems[0] = true;
            mSelectedItems.add(options[0].toString());
        }


        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_word_options_select, null);

        Button everything = v.findViewById(R.id.word_options_everything_button);
        everything.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelectedItems = new ArrayList<>();
                for(int i = 0; i < options.length; i++){
                    mPreviouslySelectedItems[i] = true;
                    mSelectedItems.add(options[i].toString());
                }
            }
        });

        Button deselectAll = v.findViewById(R.id.word_options_deselect_all_button);
        deselectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(boolean b: mPreviouslySelectedItems) b = false;
                mSelectedItems = new ArrayList<>();
            }
        });


        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.word_options_dialog_title)
                .setMultiChoiceItems(options, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if(isChecked){
                            mSelectedItems.add(options[which].toString());
                        }
                        else if(mSelectedItems.contains(options[which].toString())){
                            mSelectedItems.remove(which);
                        }
                    }
                })
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_OK, mSelectedItems);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putBooleanArray(KEY_PREV_SELECTED, mPreviouslySelectedItems);
        outState.putInt(KEY_PREV_API, mPreviouslyUsedApi);
    }

    // TODO Expand if we ever increase the amount of APIs
    private CharSequence[] getApiOptions(int api){
        final ArrayList<String> api_options;

        if(api == Json_Keys.APIs.WORDS){
            api_options = Words_API.getOptions();
        }
        else{
            api_options = new ArrayList<>();
        }

        return api_options.toArray(new CharSequence[0]);
    }

    private void sendResult(int resultCode, ArrayList<String> options){
        if (getTargetFragment() == null) return;

        Intent intent = new Intent();
        intent.putStringArrayListExtra(EXTRA_SELECTED_OPTIONS, options);

        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
