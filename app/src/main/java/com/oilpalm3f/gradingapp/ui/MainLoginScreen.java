package com.oilpalm3f.gradingapp.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.oilpalm3f.gradingapp.MainActivity;
import com.oilpalm3f.gradingapp.R;
import com.oilpalm3f.gradingapp.cloudhelper.ApplicationThread;
import com.oilpalm3f.gradingapp.common.CommonConstants;
import com.oilpalm3f.gradingapp.common.CommonUtils;
import com.oilpalm3f.gradingapp.database.DataAccessHandler;
import com.oilpalm3f.gradingapp.database.Palm3FoilDatabase;
import com.oilpalm3f.gradingapp.database.Queries;
import com.oilpalm3f.gradingapp.datasync.helpers.DataManager;
import com.oilpalm3f.gradingapp.datasync.helpers.DataSyncHelper;
import com.oilpalm3f.gradingapp.dbmodels.UserDetails;
import com.oilpalm3f.gradingapp.helper.PrefUtil;
import com.oilpalm3f.gradingapp.uihelper.ProgressBar;
import com.oilpalm3f.gradingapp.utils.UiUtils;

import java.util.List;

import es.dmoral.toasty.Toasty;

import static com.oilpalm3f.gradingapp.datasync.helpers.DataManager.USER_DETAILS;
import static com.oilpalm3f.gradingapp.datasync.helpers.DataManager.USER_VILLAGES;


//Login Screen
public class MainLoginScreen extends AppCompatActivity {

    public static final String LOG_TAG = MainLoginScreen.class.getName();



    private TextView imeiNumberTxt;
    private TextView versionnumbertxt, dbVersionTxt;
    private EditText userID;
    private EditText passwordEdit;
    private Button signInBtn;
    private String userId;
    private String password;
    DataAccessHandler dataAccessHandler;
    FloatingActionButton sync;
    LocationManager lm;


//Oncreate with get User Details
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_login_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Login");
        setSupportActionBar(toolbar);
        dataAccessHandler = new DataAccessHandler(MainLoginScreen.this);

        initView();

        imeiNumberTxt.setText(CommonUtils.getIMEInumber(this));
        versionnumbertxt.setText(CommonUtils.getAppVersion(this));
        dbVersionTxt.setText(""+ Palm3FoilDatabase.DATA_VERSION);
        this.sync=(FloatingActionButton) findViewById(R.id.sync);

        String query = Queries.getInstance().getUserDetailsNewQuery(CommonUtils.getIMEInumber(this));


        final UserDetails userDetails = (UserDetails) dataAccessHandler.getUserDetails(query, 0);

        if (null != userDetails ) {
//            if (CommonUtils.isLocationPermissionGranted(MainLoginScreen.this) ) {
//                startService(new Intent(this, FalogService.class));
//            }
            // Updated Services For Android Q ###  CIS ## 21/05/21\\

//            if (CommonUtils.isLocationPermissionGranted(MainLoginScreen.this) ) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    getApplicationContext().startForegroundService(new Intent(this, FalogService.class));
//                } else {
//                    getApplicationContext().startService(new Intent(this, FalogService.class));
//                }
//            }
            userID.setText(userDetails.getUserName());
            passwordEdit.setText(userDetails.getPassword());

            //List userVillages = dataAccessHandler.getSingleListData(Queries.getInstance().getUserVillages(userDetails.getId()));
            DataManager.getInstance().addData(USER_DETAILS, userDetails);
//            if (!userVillages.isEmpty()) {
//                DataManager.getInstance().addData(USER_VILLAGES, userVillages);
//            }
            CommonConstants.USER_ID = userDetails.getId();
            CommonConstants.TAB_ID = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getTabId(CommonUtils.getIMEInumber(MainLoginScreen.this)));
            Log.d("CommonConstants.TAB_ID",CommonConstants.TAB_ID + "");
            CommonConstants.TAB_ID = CommonConstants.TAB_ID.replace("Tab", "T");
            Log.d("CommonConstants.TAB_ID1",CommonConstants.TAB_ID + "");
            CommonConstants.USER_CODE = userDetails.getUserCode();
            CommonConstants.USER_NAME = userDetails.getUserName();
            CommonConstants.ROLEID = userDetails.getRoleId() + "";

            Log.d("ROLEID",CommonConstants.ROLEID + "");
            Log.d("getUserName",CommonConstants.USER_NAME + "");

            imeiNumberTxt.setText(CommonUtils.getIMEInumber(this)+" ("+CommonConstants.TAB_ID+")");
//           List<String> userActivityRights = dataAccessHandler.getSingleListData(Queries.getInstance().activityRightQuery(userDetails.getRoleId()));
//////            List<String> userActivityRights = dataAccessHandler.getSingleListData(Queries.getInstance().activityRightQuery(1));
//            DataManager.getInstance().addData(DataManager.USER_ACTIVITY_RIGHTS, userActivityRights);


            Log.v(LOG_TAG, "@@@@ activity rights ");
        } else {
            UiUtils.showCustomToastMessage("User not existed", MainLoginScreen.this, 1);
        }

//        DataSyncHelper.getAlertsData(MainLoginScreen.this, new ApplicationThread.OnComplete<String>() {
//            @Override
//            public void execute(boolean success, String result, String msg) {
//                if (success) {
//                } else {
//                    UiUtils.showCustomToastMessage("Error while getting alerts Data", MainLoginScreen.this, 1);
//                }
//            }
//        });

        //SignIn Btn On Click Listener
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startActivity(new Intent(MainLoginScreen.this, CropMaintenanceHomeScreen.class));
                userId = userID.getText().toString();
                password = passwordEdit.getText().toString();
                if (validateField()) {
                    CommonUtils.hideKeyPad(MainLoginScreen.this, passwordEdit);
                    startActivity(new Intent(MainLoginScreen.this, MainActivity.class));
                    finish();
                }
            }
        });

        //Sync Btn On Click Listener
        sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startMasterSync();

            }
        });

        transactionSync();

    }


    //Initialize the UI
    private void initView() {
        imeiNumberTxt = (TextView) findViewById(R.id.imeiNumberTxt);
        versionnumbertxt = (TextView) findViewById(R.id.versionnumbertxt);
        dbVersionTxt = (TextView) findViewById(R.id.dbVersiontxt);
        userID = (EditText) findViewById(R.id.userID);
        passwordEdit = (EditText) findViewById(R.id.passwordEdit);
        signInBtn = (Button) findViewById(R.id.signInBtn);
    }


    //Validations
    private boolean validateField() {
        if (TextUtils.isEmpty(userId)) {
            Toasty.error(this, "Please enter user id", Toast.LENGTH_SHORT).show();
            userID.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            Toasty.error(this, "Please enter password", Toast.LENGTH_SHORT).show();
            passwordEdit.requestFocus();
            return false;
        }
        return true;
    }

    //Perform mastersync
    public void startMasterSync() {


        DataSyncHelper.performMasterSync(this, PrefUtil.getBool(this, CommonConstants.IS_MASTER_SYNC_SUCCESS), new ApplicationThread.OnComplete() {
            @Override
            public void execute(boolean success, Object result, String msg) {

                if (success) {

                    ApplicationThread.uiPost(LOG_TAG, "master sync message", new Runnable() {
                        @Override
                        public void run() {
                            UiUtils.showCustomToastMessage("Data syncing success", MainLoginScreen.this, 0);
                            ProgressBar.hideProgressBar();
//                            List<UserSync> userSyncList;
//                               userSyncList = (List<UserSync>)dataAccessHandler.getUserSyncData(Queries.getInstance().countOfMasterSync());
//                            userSyncList = (List<UserSync>)dataAccessHandler.getUserSyncData(Queries.getInstance().countOfSync());
//
//                            if(userSyncList.size()==0){
//                                Log.v("@@@MM","mas");
//                                addUserMasSyncDetails();
//                            }else {
//                                dataAccessHandler.updateMasterSync();
//                            }

                        }
                    });

                } else {
                    Log.v(LOG_TAG, "@@@ Master sync failed " + msg);
                    ApplicationThread.uiPost(LOG_TAG, "master sync message", new Runnable() {
                        @Override
                        public void run() {
                            UiUtils.showCustomToastMessage("Data syncing failed", MainLoginScreen.this, 1);
                            ProgressBar.hideProgressBar();
                        }
                    });
                }
            }
        });

    }

    //perform transaction sync
    public void transactionSync(){
        CommonConstants.IsLogin = true;
        if (CommonUtils.isNetworkAvailable(MainLoginScreen.this)) {
            DataSyncHelper.performRefreshTransactionsSync(MainLoginScreen.this, new ApplicationThread.OnComplete() {
                @Override
                public void execute(boolean success, final Object result, String msg) {
                    if (success) {
                        ApplicationThread.uiPost(LOG_TAG, "transactions sync message", new Runnable() {
                            @Override
                            public void run() {
                                UiUtils.showCustomToastMessage("Successfully data sent to server", MainLoginScreen.this, 0);
                                // dataAccessHandler.updateUserSync();

                            }
                        });
                    } else {
                        ApplicationThread.uiPost(LOG_TAG, "transactions sync failed message", new Runnable() {
                            @Override
                            public void run() {
                                UiUtils.showCustomToastMessage("Sync Failed due to"+result, MainLoginScreen.this, 1);
                            }
                        });
                    }
                }

            });

        } else {
            UiUtils.showCustomToastMessage("Please check network connection", MainLoginScreen.this, 1);
        }


    }
}