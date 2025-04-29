package com.oilpalm3f.gradingapp.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.oilpalm3f.gradingapp.MainActivity;
import com.oilpalm3f.gradingapp.R;
import com.oilpalm3f.gradingapp.cloudhelper.ApplicationThread;
import com.oilpalm3f.gradingapp.common.CommonConstants;
import com.oilpalm3f.gradingapp.common.CommonUtils;
import com.oilpalm3f.gradingapp.database.DataAccessHandler;
import com.oilpalm3f.gradingapp.database.Palm3FoilDatabase;
import com.oilpalm3f.gradingapp.database.Queries;
import com.oilpalm3f.gradingapp.datasync.helpers.DataSyncHelper;
import com.oilpalm3f.gradingapp.helper.PrefUtil;
import com.oilpalm3f.gradingapp.uihelper.ProgressBar;
import com.oilpalm3f.gradingapp.utils.UiUtils;

public class SplashScreen extends AppCompatActivity {

    public static final String LOG_TAG = SplashScreen.class.getName();
    private static int SPLASH_TIME_OUT = 3000;

    private Palm3FoilDatabase palm3FoilDatabase;
    private String[] PERMISSIONS_REQUIRED = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.FOREGROUND_SERVICE
    };
    private SharedPreferences sharedPreferences;

    //Creating DB and Master Sync
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        sharedPreferences = getSharedPreferences("appprefs", MODE_PRIVATE);


        if (!CommonUtils.isNetworkAvailable(this)) {
            UiUtils.showCustomToastMessage("Please check your network connection", SplashScreen.this, 1);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !CommonUtils.areAllPermissionsAllowedNew(this, PERMISSIONS_REQUIRED)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_REQUIRED, CommonUtils.PERMISSION_CODE);
        } else {
            try {
                palm3FoilDatabase = Palm3FoilDatabase.getPalm3FoilDatabase(this);
                palm3FoilDatabase.createDataBase();
                dbUpgradeCall();

            } catch (Exception e) {
                e.getMessage();
            }
            startMasterSync();
        }


    }

    //Request Permissions Result
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CommonUtils.PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v(LOG_TAG, "permission granted");
                    try {
                        palm3FoilDatabase = Palm3FoilDatabase.getPalm3FoilDatabase(this);
                        palm3FoilDatabase.createDataBase();
                        dbUpgradeCall();
                    } catch (Exception e) {
                        Log.e(LOG_TAG, "@@@ Error while getting master data " + e.getMessage());
                    }
                    startMasterSync();
                }
                break;
        }
    }

    //Perform Master Sync
    public void startMasterSync() {

        if (CommonUtils.isNetworkAvailable(this)) {
            DataSyncHelper.performMasterSync(this, PrefUtil.getBool(this, CommonConstants.IS_MASTER_SYNC_SUCCESS), new ApplicationThread.OnComplete() {
                @Override
                public void execute(boolean success, Object result, String msg) {
                    ProgressBar.hideProgressBar();
                    if (success) {
                        Log.d("MasterSyncSuccess", "true");
                        UiUtils.showCustomToastMessage("Master Sync Success", SplashScreen.this, 0);
                        sharedPreferences.edit().putBoolean(CommonConstants.IS_MASTER_SYNC_SUCCESS, true).apply();
                        startActivity(new Intent(SplashScreen.this, MainLoginScreen.class));
                        finish();
                    } else {
                        Log.d("MasterSyncSuccess", "false");
                        Log.v(LOG_TAG, "@@@ Master sync failed " + msg);
                        ApplicationThread.uiPost(LOG_TAG, "master sync message", new Runnable() {
                            @Override
                            public void run() {
                                UiUtils.showCustomToastMessage("Data syncing failed", SplashScreen.this, 1);
                                startActivity(new Intent(SplashScreen.this, MainLoginScreen.class));
                                finish();
                            }
                        }); 
                    }
                }
            });
        } else {
            startActivity(new Intent(SplashScreen.this, MainLoginScreen.class));
            finish();
        }
    }

    //Db Upgrade Method
    public void dbUpgradeCall() {
        DataAccessHandler dataAccessHandler = new DataAccessHandler(SplashScreen.this, false);
        String count = dataAccessHandler.getCountValue(Queries.getInstance().UpgradeCount());
        if (TextUtils.isEmpty(count) || Integer.parseInt(count) == 0) {
            SharedPreferences sharedPreferences = getSharedPreferences("appprefs", MODE_PRIVATE);
            sharedPreferences.edit().putBoolean(CommonConstants.IS_FRESH_INSTALL, true).apply();
        }
    }

    private void setViews() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                    Intent intent = new Intent(SplashScreen.this, MainLoginScreen.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
            }

        }, SPLASH_TIME_OUT);
    }

}