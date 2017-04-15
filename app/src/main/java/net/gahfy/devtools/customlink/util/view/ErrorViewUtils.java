package net.gahfy.devtools.customlink.util.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.crash.FirebaseCrash;

import net.gahfy.devtools.customlink.R;

public class ErrorViewUtils {
    public static void showExceptionToast(Context context, Throwable e){
        showErrorToast(context, e.getClass().getSimpleName());
    }

    public static void showErrorToast(Context context, int messageResId, Throwable error){
        if(error != null)
            FirebaseCrash.report(error);
        if(context != null)
            showErrorToast(context, context.getString(messageResId));
    }

    private static void showErrorToast(Context context, String message){
        if(context != null) {
            Toast toast = new Toast(context);
            toast.setDuration(Toast.LENGTH_SHORT);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            @SuppressLint("InflateParams")
            View view = inflater.inflate(R.layout.toast_exception, null);
            toast.setView(view);
            toast.setText(message);
            toast.show();
        }
    }
}
