package ir.archroid.ponyexpress;

import android.content.Context;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

public class makeSnackbar {

    public makeSnackbar() {
    }

    public static void info(Context context ,CoordinatorLayout coordinatorLayout , String text ){
        Snackbar snackbar = Snackbar.make(coordinatorLayout , text , BaseTransientBottomBar.LENGTH_LONG);
        snackbar.setTextColor(context.getResources().getColor(R.color.white));
        snackbar.getView().setBackgroundColor(context.getResources().getColor(R.color.blue));
        snackbar.show();
    }
    public static void Warning(Context context ,CoordinatorLayout coordinatorLayout , String text ){
        Snackbar snackbar = Snackbar.make(coordinatorLayout , text , BaseTransientBottomBar.LENGTH_LONG);
        snackbar.setTextColor(context.getResources().getColor(R.color.white));
        snackbar.getView().setBackgroundColor(context.getResources().getColor(R.color.yellow));
        snackbar.show();
    }
    public static void Alert(Context context ,CoordinatorLayout coordinatorLayout , String text ){
        Snackbar snackbar = Snackbar.make(coordinatorLayout , text , BaseTransientBottomBar.LENGTH_LONG);
        snackbar.setTextColor(context.getResources().getColor(R.color.white));
        snackbar.getView().setBackgroundColor(context.getResources().getColor(R.color.red));
        snackbar.show();
    }
    public static void message(Context context ,CoordinatorLayout coordinatorLayout , String text ){
        Snackbar snackbar = Snackbar.make(coordinatorLayout , text , BaseTransientBottomBar.LENGTH_LONG);
        snackbar.setTextColor(context.getResources().getColor(R.color.white));
        snackbar.getView().setBackgroundColor(context.getResources().getColor(R.color.green));
        snackbar.show();
    }
}

