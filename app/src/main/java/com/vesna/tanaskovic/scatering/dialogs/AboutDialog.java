package com.vesna.tanaskovic.scatering.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import com.vesna.tanaskovic.scatering.R;

/**
 * Created by Tanaskovic on 6/16/2017.
 */

public class AboutDialog extends AlertDialog.Builder {
    public AboutDialog(@NonNull Context context) {
        super(context);


        setTitle(R.string.dialog_about_title);
        setMessage(R.string.dialog_about_message);
        setCancelable(false);

        setPositiveButton(R.string.dialog_about_yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();

            }
        });

    }

    public AlertDialog prepareDialog(){
        AlertDialog dialog = create();
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }
}
