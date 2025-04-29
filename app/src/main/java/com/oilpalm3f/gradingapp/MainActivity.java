package com.oilpalm3f.gradingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.oilpalm3f.gradingapp.cloudhelper.Log;
import com.oilpalm3f.gradingapp.common.CommonConstants;
import com.oilpalm3f.gradingapp.database.DataAccessHandler;
import com.oilpalm3f.gradingapp.database.Queries;
import com.oilpalm3f.gradingapp.datasync.helpers.DataManager;
import com.oilpalm3f.gradingapp.ui.GatePassInReportActivity;
import com.oilpalm3f.gradingapp.ui.GatePassTokenReportActivity;
import com.oilpalm3f.gradingapp.ui.GatepassinActivity;
import com.oilpalm3f.gradingapp.ui.GatepasstokenActivity;
import com.oilpalm3f.gradingapp.ui.GradingActivity;
import com.oilpalm3f.gradingapp.ui.GradingReportActivity;
import com.oilpalm3f.gradingapp.ui.MainLoginScreen;
import com.oilpalm3f.gradingapp.ui.QRScanActivity;
import com.oilpalm3f.gradingapp.ui.RefreshSyncActivity;

import java.util.List;

//Home Screen

public class MainActivity extends AppCompatActivity {

    ImageView scanImg, reportsImg, sync_logo,gatepassinimg,gatepasstokenimg,gatepassoutimg;
    LinearLayout synclyt;
    LinearLayout gradinglayout, gatepassseriallayout, gatepassinlayout, gatepassoutlayout;
    private boolean doubleback = false;
    DataAccessHandler dataAccessHandler;

    //Initializing the UI and there OnClick Listeners
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Grading Home Screen");
        setSupportActionBar(toolbar);

        dataAccessHandler = new DataAccessHandler(MainActivity.this);

        synclyt = findViewById(R.id.synclyt);
        scanImg = findViewById(R.id.scanImg);
        reportsImg = findViewById(R.id.reportsImg);
        sync_logo = findViewById(R.id.refresh_logo1);
        gatepassinimg = findViewById(R.id.gatepassinimg);
        gatepasstokenimg =findViewById(R.id.gatepasstokenimg);
        gatepassoutimg =findViewById(R.id.gatepassoutimg);
        gradinglayout = findViewById(R.id.grading_ll);
        gatepassseriallayout = findViewById(R.id.gatepassserial_ll);
        gatepassinlayout = findViewById(R.id.gatepassin_ll);
        gatepassoutlayout = findViewById(R.id.gatepassout_ll);

        sync_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent syncintent = new Intent(MainActivity.this, RefreshSyncActivity.class);
                startActivity(syncintent);
            }
        });

        reportsImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDialog(MainActivity.this);

//                Intent syncintent = new Intent(MainActivity.this, GradingReportActivity.class);
//                startActivity(syncintent);

            }
        });

        scanImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent scanintent = new Intent(MainActivity.this, QRScanActivity.class);
                scanintent.putExtra("ActivityName", "GradingActivity");
                startActivity(scanintent);
            }
        });
        gatepasstokenimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gatepassintent = new Intent(MainActivity.this, GatepasstokenActivity.class);
                startActivity(gatepassintent);
            }
        });
        gatepassinimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gatepassintent = new Intent(MainActivity.this, QRScanActivity.class);
                gatepassintent.putExtra("ActivityName", "GatepassinActivity");
                startActivity(gatepassintent);
            }
        });

        gatepassoutimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gatepassintent = new Intent(MainActivity.this, QRScanActivity.class);
                gatepassintent.putExtra("ActivityName", "GatepassoutActivity");
                startActivity(gatepassintent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d("onResume", "onResumeCalled");

        List<String> userActivityRights = dataAccessHandler.getSingleListData(Queries.getInstance().activityRightQuery(Integer.parseInt(CommonConstants.ROLEID)));
        DataManager.getInstance().addData(DataManager.USER_ACTIVITY_RIGHTS, userActivityRights);


        List<String> userActivities = (List<String>) DataManager.getInstance().getDataFromManager(DataManager.USER_ACTIVITY_RIGHTS);
        Log.d("userActivities", userActivities.size() + "");

        if (null != userActivities && userActivities.contains("CanManageGateSerial")) {
            gatepassseriallayout.setVisibility(View.VISIBLE);
        }else{
            gatepassseriallayout.setVisibility(View.GONE);
        }

        if (null != userActivities && userActivities.contains("CanManageGatePass-In")) {
            gatepassinlayout.setVisibility(View.VISIBLE);
        }else{
            gatepassinlayout.setVisibility(View.GONE);
        }
        if (null != userActivities && userActivities.contains("CanManageGatePass-Out")) {
            gatepassoutlayout.setVisibility(View.VISIBLE);
        }else{
            gatepassoutlayout.setVisibility(View.GONE);
        }

        if (null != userActivities && userActivities.contains("CanManageGrading")) {
            gradinglayout.setVisibility(View.VISIBLE);
        }else{
            gradinglayout.setVisibility(View.GONE);
        }
    }

    public void showDialog(Activity activity) {
        final Dialog dialog = new Dialog(activity, R.style.DialogSlideAnim);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setCancelable(true);
        dialog.setContentView(R.layout.selectreport);

        RelativeLayout gradingreport = (RelativeLayout) dialog.findViewById(R.id.gradingreport);
        RelativeLayout gatepasstoken = (RelativeLayout) dialog.findViewById(R.id.gatepasstoken);
        RelativeLayout gatepassinreport = (RelativeLayout) dialog.findViewById(R.id.gatepassinreport);

        gradingreport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent syncintent = new Intent(MainActivity.this, GradingReportActivity.class);
                startActivity(syncintent);

            }
        });

        gatepasstoken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent syncintent = new Intent(MainActivity.this, GatePassTokenReportActivity.class);
                startActivity(syncintent);
            }
        });

        gatepassinreport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent syncintent = new Intent(MainActivity.this, GatePassInReportActivity.class);
                startActivity(syncintent);
            }
        });

        dialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
            }
        }, 500);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        int count = fm.getBackStackEntryCount();
        if (count > 0) {
            fm.popBackStack();
        } else {
            if (doubleback) {
                finishAffinity();
            } else {
                doubleback = true;
                Toast.makeText(this, "Press the back key again to close the app", Toast.LENGTH_SHORT).show();
                //UiUtils.showCustomToastMessage("Press the back key again to close the app", this, 1);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doubleback = false;
                    }
                }, 2000);
            }
        }
    }
}