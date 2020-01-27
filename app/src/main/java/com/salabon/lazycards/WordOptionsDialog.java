package com.salabon.lazycards;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;

public class WordOptionsDialog extends DialogFragment {
    static final String EXTRA_SELECTED_OPTIONS =
            "com.salabon.lazycards.selected_options";

    private static final String ARG_API = "api";

    private static final String KEY_PREV_API = "prev_api";
    private static final String KEY_PREV_SELECTED = "prev_selected";

    private static int sPreviouslyUsedApi = -1; // -1 ~ no api was previously selected
    private static boolean[] mPreviouslySelectedItems;
    private static ArrayList<String> mSelectedItems = new ArrayList<>();

    static WordOptionsDialog newInstance(int api){
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_API, api);

        WordOptionsDialog dialog = new WordOptionsDialog();
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        if(savedInstanceState != null){
            sPreviouslyUsedApi = savedInstanceState.getInt(KEY_PREV_API);
            mPreviouslySelectedItems = savedInstanceState.getBooleanArray(KEY_PREV_SELECTED);
        }

        int api = getArguments().getInt(ARG_API);
        final CharSequence[] options = getApiOptions(api);

        // We get the options for the newly selected API and set the default option
        if(sPreviouslyUsedApi != api){
            sPreviouslyUsedApi = api;
            mPreviouslySelectedItems = new boolean[options.length];
            mPreviouslySelectedItems[0] = true;
            mSelectedItems.add(options[0].toString());
        }

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.word_options_dialog_title)
                .setMultiChoiceItems(options, mPreviouslySelectedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if(isChecked){
                            mSelectedItems.add(options[which].toString());
                            mPreviouslySelectedItems[which] = true;
                        }
                        else if(mSelectedItems.contains(options[which].toString())){
                            mSelectedItems.remove(which);
                            mPreviouslySelectedItems[which] = false;
                        }
                    }
                })
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_OK, mSelectedItems);
                        dismiss();
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

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putBooleanArray(KEY_PREV_SELECTED, mPreviouslySelectedItems);
        outState.putInt(KEY_PREV_API, sPreviouslyUsedApi);
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
