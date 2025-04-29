package com.oilpalm3f.gradingapp.datasync.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.oilpalm3f.gradingapp.cloudhelper.ApplicationThread;
import com.oilpalm3f.gradingapp.cloudhelper.CloudDataHandler;
import com.oilpalm3f.gradingapp.cloudhelper.Config;
import com.oilpalm3f.gradingapp.common.CommonConstants;
import com.oilpalm3f.gradingapp.common.CommonUtils;
import com.oilpalm3f.gradingapp.database.DataAccessHandler;
import com.oilpalm3f.gradingapp.database.DatabaseKeys;
import com.oilpalm3f.gradingapp.database.Queries;
import com.oilpalm3f.gradingapp.dbmodels.GatePass;
import com.oilpalm3f.gradingapp.dbmodels.GatePassOut;
import com.oilpalm3f.gradingapp.dbmodels.GatePassToken;
import com.oilpalm3f.gradingapp.dbmodels.GradingFileRepository;
import com.oilpalm3f.gradingapp.ui.MainLoginScreen;
import com.oilpalm3f.gradingapp.uihelper.ProgressBar;
import com.oilpalm3f.gradingapp.utils.UiUtils;

import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

//Master/Transaction/Send Data will be done from here
public class DataSyncHelper {
    private static final String LOG_TAG = DataSyncHelper.class.getName();
    public static int countCheck, transactionsCheck = 0, imagesCount = 0, reverseSyncTransCount = 0, innerCountCheck = 0;
    public static List<String> refreshtableNamesList = new ArrayList<>();
    public static LinkedHashMap<String, List> refreshtransactionsDataMap = new LinkedHashMap<>();
    private static String IMEINUMBER;
    MainLoginScreen mainLoginScreen;

    //Performing Master Sync
    public static synchronized void performMasterSync(final Context context, final boolean firstTimeInsertFinished, final ApplicationThread.OnComplete onComplete) {
        IMEINUMBER = CommonUtils.getIMEInumber(context);
        LinkedHashMap<String, String> syncDataMap = new LinkedHashMap<>();
        syncDataMap.put("LastUpdatedDate", "");
        syncDataMap.put("IMEINumber", IMEINUMBER);
        countCheck = 0;
        final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
        ProgressBar.showProgressBar(context, "Making data ready for you...");
        CloudDataHandler.getMasterData(Config.live_url + Config.masterSyncUrl, syncDataMap, new ApplicationThread.OnComplete<HashMap<String, List>>() {
            @Override
            public void execute(boolean success, final HashMap<String, List> masterData, String msg) {
                if (success) {
                    if (masterData != null && masterData.size() > 0) {
                        //Log.v(LOG_TAG, "@@@ Master sync is success and data size is " + masterData.size());

                        final Set<String> tableNames = masterData.keySet();
                        masterData.remove("CcRate");
                        for (final String tableName : tableNames) {
                           // Log.v(LOG_TAG, "@@@ Delete Query " + String.format(Queries.getInstance().deleteTableData(), tableName));
                            ApplicationThread.dbPost("Master Data Sync..", "master data", new Runnable() {
                                @Override
                                public void run() {
                                    countCheck++;
                                    if (!firstTimeInsertFinished) {
                                        dataAccessHandler.deleteRow(tableName, null, null, false, new ApplicationThread.OnComplete<String>() {
                                            @Override
                                            public void execute(boolean success, String result, String msg) {
                                                if (success) {
                                                    dataAccessHandler.insertData(true, tableName, masterData.get(tableName), new ApplicationThread.OnComplete<String>() {
                                                        @Override
                                                        public void execute(boolean success, String result, String msg) {
                                                            if (success) {
                                                               //Log.v(LOG_TAG, "@@@ sync success for " + tableName);
                                                            } else {
//                                                                Log.v(LOG_TAG, "@@@ check 1 " + masterData.size() + "...pos " + countCheck);
//                                                                Log.v(LOG_TAG, "@@@ sync failed for " + tableName + " message " + msg);
                                                            }
                                                            if (countCheck == masterData.size()) {
                                                                //Log.v(LOG_TAG, "@@@ Done with master sync " + countCheck);
                                                                ProgressBar.hideProgressBar();
                                                                onComplete.execute(true, null, "Sync is success");
                                                            }
                                                        }
                                                    });
                                                } else {
                                                  //  Log.v(LOG_TAG, "@@@ Master table deletion failed for " + tableName);
                                                }
                                            }
                                        });
                                    } else {
                                        dataAccessHandler.insertData(tableName, masterData.get(tableName), new ApplicationThread.OnComplete<String>() {
                                            @Override
                                            public void execute(boolean success, String result, String msg) {
                                                if (success) {
                                                   // Log.v(LOG_TAG, "@@@ sync success for " + tableName);
                                                } else {
                                                   // Log.v(LOG_TAG, "@@@ check 2 " + masterData.size() + "...pos " + countCheck);
                                                    //Log.v(LOG_TAG, "@@@ sync failed for " + tableName + " message " + msg);
                                                }
                                                if (countCheck == masterData.size()) {
                                                    //Log.v(LOG_TAG, "@@@ Done with master sync " + countCheck);
                                                    ProgressBar.hideProgressBar();
                                                    onComplete.execute(true, null, "Sync is success");
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    } else {
                        ProgressBar.hideProgressBar();
                        Log.v(LOG_TAG, "@@@ Sync is up-to-date");
                        onComplete.execute(true, null, "Sync is up-to-date");
                    }
                } else {
                    ProgressBar.hideProgressBar();
                    onComplete.execute(false, null, "Master sync failed. Please try again");
                }
            }
        });
    }

    //Performing Transaction Sync
    public static synchronized void performRefreshTransactionsSync(final Context context, final ApplicationThread.OnComplete onComplete) {
        countCheck = 0;
        transactionsCheck = 0;
        reverseSyncTransCount = 0;
        imagesCount = 0;
        refreshtableNamesList.clear();
        refreshtransactionsDataMap.clear();
        final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
        ProgressBar.showProgressBar(context, "Sending data to server...");
        ApplicationThread.bgndPost(LOG_TAG, "getting transactions data", new Runnable() {
            @Override
            public void run() {
                getRefreshSyncTransDataMap(context, new ApplicationThread.OnComplete<LinkedHashMap<String, List>>() {
                    @Override
                    public void execute(boolean success, final LinkedHashMap<String, List> transDataMap, String msg) {

                        if (success) {
                            if (transDataMap != null && transDataMap.size() > 0) {
                                Log.v(LOG_TAG, "transactions data size " + transDataMap.size());
                                Set<String> transDataTableNames = transDataMap.keySet();
                                refreshtableNamesList.addAll(transDataTableNames);
                                refreshtransactionsDataMap = transDataMap;
                               // sendTrackingData(context, onComplete);
                                postTransactionsDataToCloud(context, refreshtableNamesList.get(transactionsCheck), dataAccessHandler, onComplete);
                            }
                        } else {
                            ProgressBar.hideProgressBar();
                            Log.v(LOG_TAG, "@@@ Transactions sync failed due to data retrieval error");
                            onComplete.execute(false, null, "Transactions sync failed due to data retrieval error");
                        }
                    }
                });
            }
        });

    }

    //Hitting Send Data to Server API
    public static void postTransactionsDataToCloud(final Context context, final String tableName, final DataAccessHandler dataAccessHandler, final ApplicationThread.OnComplete onComplete) {

        List cctransDataList = refreshtransactionsDataMap.get(tableName);

     //   List cctransDataList = refreshtransactionsDataMap.get(tableName);

        if (null != cctransDataList && cctransDataList.size() > 0) {
            Type listType = new TypeToken<List>() {
            }.getType();
            Gson gson = new GsonBuilder().serializeNulls().create();

            String dat = gson.toJson(cctransDataList, listType);
            JSONObject transObj = new JSONObject();
            try {
                transObj.put(tableName, new JSONArray(dat));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.v(LOG_TAG, "@@@@ check.." + transObj.toString());
            CommonConstants.SyncTableName = tableName;


//
//        if (null != cctransDataList && cctransDataList.size() > 0) {
//            Type listType = new TypeToken<List>() {
//            }.getType();
//            Gson gson = new GsonBuilder().serializeNulls().create();
//
//            String dat = gson.toJson(cctransDataList, listType);
//            JSONArray transObj = new JSONArray();
//            try {
//                //transObj.put(tableName, new JSONArray(dat));
//                transObj = new JSONArray(dat);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            Log.v(LOG_TAG, "@@@@ check.." + transObj.toString());
//            Log.v(LOG_TAG, "@@@@ checkkkkk.." + transObj.length());
//            CommonConstants.SyncTableName = tableName;
            CloudDataHandler.placeDataInCloud(context, transObj, Config.live_url + Config.transactionSyncURL, new ApplicationThread.OnComplete<String>() {
                @Override
                public void execute(boolean success, String result, String msg) {

                    if (success) {
                        CommonConstants.IsLogin = false;
                        dataAccessHandler.executeRawQuery(String.format(Queries.getInstance().updateServerUpdatedStatus(), tableName));
                        Log.v(LOG_TAG, "@@@ Transactions sync success for " + tableName);
                        transactionsCheck++;
                        if (transactionsCheck == refreshtransactionsDataMap.size()) {
                            Log.v(LOG_TAG, "@@@ Done with transactions sync " + transactionsCheck);
                            ProgressBar.hideProgressBar();
                            onComplete.execute(true, null, "Sync is success");
                            CommonConstants.IsLogin = false;

                        } else {
                            postTransactionsDataToCloud(context, refreshtableNamesList.get(transactionsCheck), dataAccessHandler, onComplete);
                        }
                    }
                    else {
                        ApplicationThread.uiPost(LOG_TAG, "Sync is failed", new Runnable() {
                            @Override
                            public void run() {

                               if (CommonConstants.IsLogin == true){
                                   ProgressBar.hideProgressBar();
                                   UiUtils.showCustomToastMessage("Sync failed for " + tableName, context, 1);
                                   CommonConstants.IsLogin = false;
                               }else{
                                   ProgressBar.hideProgressBar();
                                   UiUtils.showCustomToastMessage("Sync failed for " + tableName, context, 1);
                                   ((Activity)context).finish();
                               }

                            }
                        });
                        transactionsCheck++;
                        if (transactionsCheck == refreshtransactionsDataMap.size()) {
                            Log.v(LOG_TAG, "@@@ Done with transactions sync " + transactionsCheck);

                        } else {
                            postTransactionsDataToCloud(context, refreshtableNamesList.get(transactionsCheck), dataAccessHandler, onComplete);
                        }
                        Log.v(LOG_TAG, "@@@ Transactions sync failed for " + tableName);
                        Log.v(LOG_TAG, "@@@ Transactions sync due to " + result);

                    }
                }
            });
        } else {
            transactionsCheck++;
            if (transactionsCheck == refreshtransactionsDataMap.size()) {
                Log.v(LOG_TAG, "@@@ Done with transactions sync " + transactionsCheck);
                    ProgressBar.hideProgressBar();
                    onComplete.execute(true, null, "Sync is success");
                    Log.v(LOG_TAG, "@@@ Done with transactions sync " + transactionsCheck);
 
            } else {
                postTransactionsDataToCloud(context, refreshtableNamesList.get(transactionsCheck), dataAccessHandler, onComplete);
            }
        }
    }


    //Preparing Send Data
    private static void getRefreshSyncTransDataMap(final Context context, final ApplicationThread.OnComplete onComplete) {

        final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);

        List<GradingFileRepository> gradingrepoList = (List<GradingFileRepository>) dataAccessHandler.getGradingRepoDetails(Queries.getInstance().getGradingRepoRefresh(), 1);
        List<GatePassToken>gatepasstokenlist = (List<GatePassToken>) dataAccessHandler.getGatepasstokendetails(Queries.getInstance().getGatepasstokenRefresh(), 1);
        List<GatePass>gatepasslist = (List<GatePass>) dataAccessHandler.getGatepassdetails(Queries.getInstance().getGatepassRefresh(), 1);
        List<GatePassOut>gatepassoutlist = (List<GatePassOut>) dataAccessHandler.getGatepassoutdetails(Queries.getInstance().getGatepassoutRefresh(), 1);


        LinkedHashMap<String, List> allRefreshDataMap = new LinkedHashMap<>();
        allRefreshDataMap.put(DatabaseKeys.TABLE_Grading_Repository, gradingrepoList);
        allRefreshDataMap.put(DatabaseKeys.TABLE_Gatepasstoken, gatepasstokenlist);
        allRefreshDataMap.put(DatabaseKeys.TABLE_GatepassIN, gatepasslist);
        allRefreshDataMap.put(DatabaseKeys.TABLE_GatepassOut, gatepassoutlist);

        onComplete.execute(true, allRefreshDataMap, "here is collection of table transactions data");

    }

}
