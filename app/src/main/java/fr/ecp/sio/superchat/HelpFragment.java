package fr.ecp.sio.superchat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;

/**
 * Help Fragment
 * @author jmikolaj
 */
public class HelpFragment extends DialogFragment implements DialogInterface.OnShowListener {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.help_fragment, null);

        Dialog dialog =
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.help_title)
                        .setView(view)
                        .setMessage(R.string.help_message)
                        .setPositiveButton(android.R.string.yes, null
                        )
                .create();
        dialog.setOnShowListener(this);
        return dialog;
    }

    @Override
    public void onShow(DialogInterface dialog) {
        ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }



}