package com.oilpalm3f.gradingapp.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.oilpalm3f.gradingapp.R;
import com.oilpalm3f.gradingapp.cloudhelper.ApplicationThread;
import com.oilpalm3f.gradingapp.common.CommonUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedHashMap;

public class UiUtils {

    public static final String LOG_TAG = UiUtils.class.getName();


    /*
     * If backgrount type 0(Zero) = Green
     *                    1        = Red*/

    //Shows Custom Toast Message
    public static void showCustomToastMessage(final String message, final Context context, final int backgroundColorType) {
        showCustomToastMessageLong(message,context, backgroundColorType, Toast.LENGTH_SHORT);
    }


    public static void showCustomToastMessageLong(final String message, final Context context, final int backgroundColorType, final int length) {
        ApplicationThread.uiPost(LOG_TAG, "show custom toast", new Runnable() {
            @Override
            public void run() {

                if (null == context)
                    return;

                LayoutInflater inflater = LayoutInflater.from(context);
                View toastRoot = inflater.inflate(R.layout.custom_toast, null);
                TextView messageToDisplay = (TextView) toastRoot.findViewById(R.id.toast_message);
                messageToDisplay.setBackground(context.getDrawable(backgroundColorType == 0 ? R.drawable.toast_msg_green : R.drawable.toast_bg));
                messageToDisplay.setText(message);
                Toast toast = new Toast(context);
                // Set layout to toast
                toast.setView(toastRoot);
                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_VERTICAL, 0, 0);
                toast.setDuration(length);
                toast.show();
            }
        });
    }

}


