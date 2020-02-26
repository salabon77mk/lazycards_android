package com.salabon.lazycards.Cards.Dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.salabon.lazycards.Cards.Constants.Anki;
import com.salabon.lazycards.R;

public class ServerErrorDialog extends DialogFragment {
    private static final String ARG_ERROR = "error";
    private static final String STR_ERROR = "str_err";

    public static ServerErrorDialog newInstance(int errorCode){
        Bundle args = new Bundle();
        args.putInt(ARG_ERROR, errorCode);

        ServerErrorDialog serverErrorDialog = new ServerErrorDialog();
        serverErrorDialog.setArguments(args);
        return serverErrorDialog;
    }

    public static ServerErrorDialog newInstance(int errorCode, String error){
        Bundle args = new Bundle();
        args.putString(STR_ERROR, error);
        args.putInt(ARG_ERROR, errorCode);

        ServerErrorDialog dialog = new ServerErrorDialog();
        dialog.setArguments(args);
        return dialog;
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
            case Anki.ActionResult.APACHE_UNREACHABLE:
                errorMessage = getString(R.string.apache_server_unreachable);
                break;
            case Anki.ActionResult.ANKI_CONNECT_ERROR:
                errorMessage = getString(R.string.anki_connect_error)
                    + getArguments().getString(STR_ERROR);
                break;
            case Anki.ActionResult.OTHER_ERROR:
                errorMessage = getString(R.string.other_server_error);
                break;
            case Anki.ActionResult.API_ERROR:
                errorMessage = getString(R.string.api_error) +
                        getArguments().getString(STR_ERROR);
                break;

            default:
                // Custom error message
                errorMessage = getArguments().getString(STR_ERROR);
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
