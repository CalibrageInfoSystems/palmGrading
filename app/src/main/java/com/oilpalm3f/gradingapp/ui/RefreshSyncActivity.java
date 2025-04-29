package com.oilpalm3f.gradingapp.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.oilpalm3f.gradingapp.R;
import com.oilpalm3f.gradingapp.cloudhelper.ApplicationThread;
import com.oilpalm3f.gradingapp.cloudhelper.CloudDataHandler;
import com.oilpalm3f.gradingapp.cloudhelper.Config;
import com.oilpalm3f.gradingapp.common.CommonConstants;
import com.oilpalm3f.gradingapp.common.CommonUtils;
import com.oilpalm3f.gradingapp.database.DataAccessHandler;
import com.oilpalm3f.gradingapp.database.Queries;
import com.oilpalm3f.gradingapp.datasync.helpers.DataSyncHelper;
import com.oilpalm3f.gradingapp.uihelper.ProgressBar;
import com.oilpalm3f.gradingapp.utils.UiUtils;

import java.io.File;
import java.util.List;

import es.dmoral.toasty.Toasty;

//Sync Activities can be done from this screen
public class RefreshSyncActivity extends AppCompatActivity {

    private static final String LOG_TAG = RefreshSyncActivity.class.getName();

    private TextView tvgradingfilerepository,GatePassTokencount,GatePasscount, GatePassoutcount;
    private Button btnsend, btnmastersync, btnDBcopy;
    private DataAccessHandler dataAccessHandler;
    private boolean isDataUpdated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refresh_sync);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Sync Screen");
        setSupportActionBar(toolbar);

        dataAccessHandler = new DataAccessHandler(this);
        tvgradingfilerepository = findViewById(R.id.gradingrepcount);
        GatePassTokencount = findViewById(R.id.GatePassTokencount);
        GatePasscount = findViewById(R.id.GatePasscount);
        GatePassoutcount = findViewById(R.id.GatePassoutcount);
        btnsend = findViewById(R.id.btsynctoserver);
        btnDBcopy = findViewById(R.id.btcopydatabase);
        btnmastersync = findViewById(R.id.btnmastersync);

        bindData();

        //Master Sync
        btnmastersync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (CommonUtils.isNetworkAvailable(RefreshSyncActivity.this)) {
                    DataSyncHelper.performMasterSync(RefreshSyncActivity.this, false, new ApplicationThread.OnComplete() {
                        @Override
                        public void execute(boolean success, Object result, String msg) {
                            ProgressBar.hideProgressBar();
                            if (success) {
                                if (!msg.equalsIgnoreCase("Sync is up-to-date")) {
                                    Toast.makeText(RefreshSyncActivity.this, "Data synced successfully", Toast.LENGTH_SHORT).show();
                                    // List<UserSync> userSyncList = (List<UserSync>)dataAccessHandler.getUserSyncData(Queries.getInstance().countOfMasterSync());
//                                    List<UserSync> userSyncList = (List<UserSync>) dataAccessHandler.getUserSyncData(Queries.getInstance().countOfSync());
//
//                                    if (userSyncList.size() == 0) {
//                                        Log.v("@@@MM", "mas");
//                                        addUserMasSyncDetails();
//                                    } else {
//                                        dataAccessHandler.updateMasterSync();
//                                    }

                                    // DataAccessHandler dataAccessHandler = new DataAccessHandler(RefreshSyncActivity.this);
                                    // dataAccessHandler.updateMasterSyncDate(false, CommonConstants.USER_ID);
                                } else {
                                    ApplicationThread.uiPost(LOG_TAG, "master sync message", new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(RefreshSyncActivity.this, "You have updated data", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            } else {
                                Log.v(LOG_TAG, "@@@ Master sync failed " + msg);
                                ApplicationThread.uiPost(LOG_TAG, "master sync message", new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(RefreshSyncActivity.this, "Master sync failed. Please try again", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                } else {
                    UiUtils.showCustomToastMessage("Please check network connection", RefreshSyncActivity.this, 1);
                }
            }
        });


        //Sends Data to Server
        btnsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (CommonUtils.isNetworkAvailable(RefreshSyncActivity.this)) {

                    btnsend.setVisibility(View.GONE);
                    isDataUpdated = false;
                    DataSyncHelper.performRefreshTransactionsSync(RefreshSyncActivity.this, new ApplicationThread.OnComplete() {
                        @Override
                        public void execute(boolean success, Object result, String msg) {
                            if (success) {
                                ApplicationThread.uiPost(LOG_TAG, "transactions sync message", new Runnable() {
                                    @Override
                                    public void run() {
                                        bindData();
//                                        Toasty.success(RefreshSyncActivity.this,"Successfully data sent to server",10).show();
                                        if (isDataUpdated) {
                                            CommonConstants.IsLogin = false;
                                            UiUtils.showCustomToastMessage("Successfully data sent to server", RefreshSyncActivity.this, 0);
                                            ProgressBar.hideProgressBar();
                                            btnsend.setVisibility(View.VISIBLE);
                                            //  dataAccessHandler.updateUserSync();
                                        }

                                    }
                                });
                            } else {
                                ApplicationThread.uiPost(LOG_TAG, "transactions sync failed message", new Runnable() {
                                    @Override
                                    public void run() {
                                        bindData();
                                        Toasty.error(RefreshSyncActivity.this, "Data sending failed", 10).show();
//                                        Toast.makeText(RefreshSyncActivity.this, "Data sending failed", Toast.LENGTH_SHORT).show();
                                        ProgressBar.hideProgressBar();
                                        btnsend.setVisibility(View.VISIBLE);
                                    }
                                });
                            }
                        }
                    });
                } else {
                    UiUtils.showCustomToastMessage("Please check network connection", RefreshSyncActivity.this, 1);
                    btnsend.setVisibility(View.VISIBLE);
                }
            }
        });

        btnDBcopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialog();
            }
        });

    }

    //Binds Data to the field
    private void bindData() {
        try {

            tvgradingfilerepository.setText(dataAccessHandler.getCountValue(Queries.getInstance().getRefreshCountQuery("FFBGradingRepository")));
            GatePassTokencount.setText(dataAccessHandler.getCountValue(Queries.getInstance().getRefreshCountQuery("GatePassToken")));
            GatePasscount.setText(dataAccessHandler.getCountValue(Queries.getInstance().getRefreshCountQuery("GatePass")));
            GatePassoutcount.setText(dataAccessHandler.getCountValue(Queries.getInstance().getRefreshCountQuery("GatePassOut")));
            isDataUpdated = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showAlertDialog() {
        final Dialog dialog = new Dialog(RefreshSyncActivity.this);
        dialog.setContentView(R.layout.custom_alert_dailog);

        Button yesDialogButton = dialog.findViewById(R.id.Yes);
        Button noDialogButton = dialog.findViewById(R.id.No);
        TextView msg = dialog.findViewById(R.id.test);
        yesDialogButton.setTextColor(getResources().getColor(R.color.green));
        noDialogButton.setTextColor(getResources().getColor(R.color.btnPressedColor));
        msg.setText("Do you want to upload data base to server ?");
        // if button is clicked, close the custom dialog
        yesDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonUtils.isNetworkAvailable(RefreshSyncActivity.this)) {
                    dialog.dismiss();
                    ProgressBar.showProgressBar(RefreshSyncActivity.this, "uploading database...");
                    CommonUtils.copyFile(RefreshSyncActivity.this);
                    uploadDatabaseFile();
                } else {
                    dialog.dismiss();
                    UiUtils.showCustomToastMessage("Please check network connection", RefreshSyncActivity.this, 1);
                }
            }
        });
        dialog.show();
        noDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
//                        Toast.makeText(getApplicationContext(),"Dismissed..!!",Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void uploadDatabaseFile() {
        ApplicationThread.bgndPost(LOG_TAG, "upload database..", new Runnable() {
            @Override
            public void run() {
                uploadDataBase(getDbFileToUpload(), new ApplicationThread.OnComplete<String>() {
                    @Override
                    public void execute(boolean success, String result, String msg) {
                        ProgressBar.hideProgressBar();
                        if (success) {
                            Log.v(LOG_TAG, "@@@ 3f db file upload success");
                            CommonUtils.showToast("3f db file uploaded successfully", RefreshSyncActivity.this);
                        } else {
                            Log.v(LOG_TAG, "@@@ 3f db file upload failed due to " + msg);
                            CommonUtils.showToast("3f db file upload failed due to" + msg, RefreshSyncActivity.this);
                        }
                    }
                });
            }
        });
    }

    public void uploadDataBase(final File uploadDbFile, final ApplicationThread.OnComplete<String> onComplete) {
        if (null != uploadDbFile) {
            final long nanoTime = System.nanoTime();
            final String filePathToSave = "/sdcard/3f_" + CommonConstants.TAB_ID + "_" + nanoTime + "_v_" + CommonUtils.getAppVersion(RefreshSyncActivity.this) + ".gzip";
            final File toZipFile = getDbFileToUpload();
            CommonUtils.gzipFile(toZipFile, filePathToSave, new ApplicationThread.OnComplete<String>() {
                @Override
                public void execute(boolean success, String result, String msg) {
                    if (success) {
                        File dir = Environment.getExternalStorageDirectory();
                        File uploadFile = new File(dir, "3f_" + CommonConstants.TAB_ID + "_" + nanoTime + "_v_" + CommonUtils.getAppVersion(RefreshSyncActivity.this) + ".gzip");
                        Log.v(LOG_TAG, "@@@ file size " + uploadFile.length());
                        if (uploadFile != null) {
                            CloudDataHandler.uploadFileToServer(uploadFile, Config.live_url + Config.updatedbFile, new ApplicationThread.OnComplete<String>() {
                                @Override
                                public void execute(boolean success, String result, String msg) {
                                    onComplete.execute(success, result, msg);
                                }
                            });
                        } else {
                            onComplete.execute(false, "failed", "data base is empty");
                        }

                    } else {
                        onComplete.execute(success, result, msg);
                    }
                }
            });
        } else {
            onComplete.execute(false, "file upload failed", "null database");
        }

    }

    public File getDbFileToUpload() {
        try {
//            File dir = Environment.getExternalStorageDirectory();
            File dbFileToUpload = new File("/sdcard/3F_Grading/3F_Database/3foilpalm.sqlite");
            return dbFileToUpload;
        } catch (Exception e) {
            android.util.Log.w("Settings Backup", e);
        }
        return null;
    }

}