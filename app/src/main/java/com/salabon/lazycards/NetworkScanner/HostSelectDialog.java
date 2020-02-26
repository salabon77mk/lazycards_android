package com.salabon.lazycards.NetworkScanner;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.salabon.lazycards.R;

public class HostSelectDialog extends DialogFragment {
    private static final String ARG_HOST = "host";

    HostSelectDialogListener mListener;

    public interface HostSelectDialogListener{
        void onDialogPositiveClick(String host);
    }

    public static HostSelectDialog newInstance(String host){
        Bundle args = new Bundle();
        args.putString(ARG_HOST, host);

        HostSelectDialog dialog = new HostSelectDialog();
        dialog.setArguments(args);
        return dialog;
    }

    // Lets us send back events back to the hosting activity
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (HostSelectDialogListener) context;
    }


    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState){
        final String host = (String) getArguments().getSerializable(ARG_HOST);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_host_select, null);
        TextView host_select = v.findViewById(R.id.host_select_text);
        String host_text = getString(R.string.host_select_text, host);
        host_select.setText(host_text);


        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.host_select_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onDialogPositiveClick(host);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .create();
    }
}
