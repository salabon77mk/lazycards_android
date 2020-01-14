package com.salabon.lazycards;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class ServerErrorDialog extends DialogFragment {
    private static final String ARG_ERROR = "error";

    static ServerErrorDialog newInstance(int errorCode){
        Bundle args = new Bundle();
        args.putInt(ARG_ERROR, errorCode);

        ServerErrorDialog serverErrorDialog = new ServerErrorDialog();
        serverErrorDialog.setArguments(args);
        return serverErrorDialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        int errorCode = getArguments().getInt(ARG_ERROR);
        String errorMessage;
        switch (errorCode){
            case Anki.ActionResult.APACHE_SERVER_DOWN:
                errorMessage = getString(R.string.apache_server_down);
                break;
            case Anki.ActionResult.ANKI_SERVER_DOWN:
                errorMessage = getString(R.string.anki_server_down);
                break;
            case Anki.ActionResult.OTHER_ERROR:
                errorMessage = getString(R.string.other_server_error);
                break;
            default:
                // This one should never happen unless new error states were introduced
                // but not accounted for in the switch statement
                errorMessage = "A new error was introduced but not implemented";
        }

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.error_dialog_title)
                .setMessage(errorMessage)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                })
                .create();
    }
}
