package com.health.pengfei.chestlearn2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Created by pengfei on 9/19/17.
 */

public class NoticeDialogFragment extends DialogFragment
{
    public static final String RESPONSE_EVALUATE = "response_evaluate";

    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Not 3 photos, still want to upload?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        setResult(id);
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        setResult(id);
                    }
                });
        return builder.create();
    }

    // 设置返回数据
    protected void setResult(int which)
    {
        // 判断是否设置了targetFragment
        if (getTargetFragment() == null)
            return;
        System.out.println(which);
        Intent intent = new Intent();
        if (which == -1){
            intent.putExtra(RESPONSE_EVALUATE, "YES");
        } else if (which == -2){
            intent.putExtra(RESPONSE_EVALUATE, "CANCEL");
        }
        getTargetFragment().onActivityResult(NurseFragment.REQUEST_EVALUATE,
                Activity.RESULT_OK, intent);

    }
}