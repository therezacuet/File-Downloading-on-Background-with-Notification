package com.thereza.cmedtask01.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import com.thereza.cmedtask01.R;

public class BaseActivity extends AppCompatActivity {
    private Context mContext;
    private Activity mActivity;
    private Snackbar mSnackBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVariable();
    }

    private void initVariable() {
        mContext = getApplicationContext();
        mActivity = BaseActivity.this;
    }

    public void showSnackBar(String msg, final String action) {
        CoordinatorLayout mainLayout = findViewById(R.id.coordinatorLayout);
        mSnackBar = Snackbar.make(mainLayout, msg, Snackbar.LENGTH_LONG)
                .setAction(action, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (action.equals(mContext.getString(R.string.action_connect)))
                            startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));

                    }
                });

        mSnackBar.setDuration(3000)
                .show();
        mSnackBar.getView().setBackgroundColor((ContextCompat.getColor(mActivity, R.color.colorPrimaryDark)));
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
