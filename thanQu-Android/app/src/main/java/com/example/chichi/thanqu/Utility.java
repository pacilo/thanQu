package com.example.chichi.thanqu;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by pacilo on 2017. 6. 14..
 */

public class Utility {

    private static ProgressDialog dialog = null;

    public static void showProgressDlg(Context context) {
        dismissProgressDlg();

        if (dialog == null) {
            dialog = new ProgressDialog(context);
            dialog.show(context, "", "잠시만 기다려주세요!", false, true);
        }
    }

    public static void dismissProgressDlg() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }
}
