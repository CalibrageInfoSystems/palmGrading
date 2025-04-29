package com.oilpalm3f.gradingapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import com.oilpalm3f.gradingapp.cloudhelper.ApplicationThread;
import com.oilpalm3f.gradingapp.common.CommonUtils;
import com.oilpalm3f.gradingapp.dbmodels.GatePass;
import com.oilpalm3f.gradingapp.dbmodels.GatePassOut;
import com.oilpalm3f.gradingapp.dbmodels.GatePassToken;
import com.oilpalm3f.gradingapp.dbmodels.GatepassInListModel;
import com.oilpalm3f.gradingapp.dbmodels.GatepassTokenListModel;
import com.oilpalm3f.gradingapp.dbmodels.Gatepassoutdetails;
import com.oilpalm3f.gradingapp.dbmodels.GradingFileRepository;
import com.oilpalm3f.gradingapp.dbmodels.GradingReportModel;
import com.oilpalm3f.gradingapp.dbmodels.UserDetails;
import com.oilpalm3f.gradingapp.utils.ImageUtility;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class DataAccessHandler <T> {

    private static final String LOG_TAG = DataAccessHandler.class.getName();

    private Context context;
    private SQLiteDatabase mDatabase;
    private String var = "";
    String queryForLookupTable = "select Name from LookUp where id=" + var;
    private int value;

    public DataAccessHandler() {

    }

    SimpleDateFormat simpledatefrmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    String currentTime = simpledatefrmt.format(new Date());


    public DataAccessHandler(final Context context) {
        this.context = context;
        try {
            mDatabase = Palm3FoilDatabase.openDataBaseNew();
            DataBaseUpgrade.upgradeDataBase(context, mDatabase);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public DataAccessHandler(final Context context, boolean firstTime) {
        this.context = context;
        try {
            mDatabase = Palm3FoilDatabase.openDataBaseNew();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //To Get Only One Int value from DB
    public Integer getOnlyOneIntValueFromDb(String query) {
        Log.v(LOG_TAG, "@@@ query " + query);
        Cursor mOprQuery = null;
        try {
            mOprQuery = mDatabase.rawQuery(query, null);
            if (mOprQuery != null && mOprQuery.moveToFirst()) {
                return mOprQuery.getInt(0);
            }

            return null;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != mOprQuery)
                mOprQuery.close();

            closeDataBase();
        }
        return null;
    }
    //To Get Only One value from DB
    public String getOnlyOneValueFromDb(String query) {
        Log.v(LOG_TAG, "@@@ query " + query);
        Cursor mOprQuery = null;
        try {
            mOprQuery = mDatabase.rawQuery(query, null);
            if (mOprQuery != null && mOprQuery.moveToFirst()) {
                return mOprQuery.getString(0);
            }

            return null;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != mOprQuery)
                mOprQuery.close();

            closeDataBase();
        }
        return "";
    }


    //To insert Data received from Server
    public synchronized void insertData(boolean fromMaster, String tableName, List<LinkedHashMap> mapList, final ApplicationThread.OnComplete<String> oncomplete) {
        int checkCount = 0;
        try {
            List<ContentValues> values1 = new ArrayList<>();
            for (int i = 0; i < mapList.size(); i++) {
                checkCount++;
                List<LinkedHashMap.Entry> entryList = new ArrayList<>((mapList.get(i)).entrySet());

                ContentValues contentValues = new ContentValues();
                for (LinkedHashMap.Entry temp : entryList) {
                    String keyToInsert = temp.getKey().toString();
                    if (keyToInsert.equalsIgnoreCase("ServerUpdatedStatus")) {
                        contentValues.put(keyToInsert, "1");
                    } else {
                        contentValues.put(temp.getKey().toString(), temp.getValue().toString());
                    }
                }
                values1.add(contentValues);
            }
            Log.v(LOG_TAG, "@@@@ log check " + checkCount + " here " + values1.size());
            boolean hasError = bulkinserttoTable(values1, tableName);
            if (hasError) {
                Log.v(LOG_TAG, "@@@ Error while inserting data ");
                if (null != oncomplete) {
                    oncomplete.execute(false, "failed to insert data", "");
                }
            } else {
                Log.v(LOG_TAG, "@@@ data inserted successfully for table :" + tableName);
                if (null != oncomplete) {
                    oncomplete.execute(true, "data inserted successfully", "");
                }
            }
        } catch (Exception e) {
            checkCount++;
            e.printStackTrace();
            Log.v(LOG_TAG, "@@@@ exception log check " + checkCount + " here " + mapList.size());
            if (checkCount == mapList.size()) {
                if (null != oncomplete)
                    oncomplete.execute(false, "data insertion failed", "" + e.getMessage());
            }
        } finally {
            closeDataBase();
        }
    }

    //Save Data into the table
    public synchronized void savedata(boolean fromMaster, String tableName, List<LinkedHashMap> mapList, final ApplicationThread.OnComplete<String> oncomplete) {
        int checkCount = 0;
        try {
            List<ContentValues> values1 = new ArrayList<>();
            for (int i = 0; i < mapList.size(); i++) {
                checkCount++;
                List<LinkedHashMap.Entry> entryList = new ArrayList<>((mapList.get(i)).entrySet());

                ContentValues contentValues = new ContentValues();
                for (LinkedHashMap.Entry temp : entryList) {
                    String keyToInsert = temp.getKey().toString();
                    if (keyToInsert.equalsIgnoreCase("ServerUpdatedStatus")) {
                        contentValues.put(keyToInsert, "0");
                    } else {
                        contentValues.put(temp.getKey().toString(), temp.getValue().toString());
                    }
                }
                values1.add(contentValues);
            }
            Log.v(LOG_TAG, "@@@@ log check " + checkCount + " here " + values1.size());
            boolean hasError = bulkinserttoTable(values1, tableName);
            if (hasError) {
                Log.v(LOG_TAG, "@@@ Error while inserting data ");
                if (null != oncomplete) {
                    oncomplete.execute(false, "failed to insert data", "");
                }
            } else {
                Log.v(LOG_TAG, "@@@ data inserted successfully for table :" + tableName);
                if (null != oncomplete) {
                    oncomplete.execute(true, "data inserted successfully", "");
                }
            }
        } catch (Exception e) {
            checkCount++;
            e.printStackTrace();
            Log.v(LOG_TAG, "@@@@ exception log check " + checkCount + " here " + mapList.size());
            if (checkCount == mapList.size()) {
                if (null != oncomplete)
                    oncomplete.execute(false, "data insertion failed", "" + e.getMessage());
            }
        } finally {
            closeDataBase();
        }
    }

    //Save Data
    public synchronized void saveData(String tableName, List<LinkedHashMap> mapList, final ApplicationThread.OnComplete<String> oncomplete) {
        savedata(false, tableName, mapList, oncomplete);
    }

    //Insert Data
    public synchronized void insertData(String tableName, List<LinkedHashMap> mapList, final ApplicationThread.OnComplete<String> oncomplete) {
        insertData(false, tableName, mapList, oncomplete);
    }

    //Delete Row in the Tables
    public synchronized void deleteRow(String tableName, String columnName, String value, boolean isWhere, final ApplicationThread.OnComplete<String> onComplete) {
        boolean isDataDeleted = true;
//        if (!ApplicationThread.dbThreadCheck())
//            Log.e(LOG_TAG, "called on non-db thread", new RuntimeException());

        try {
//            mDatabase = palm3FoilDatabase.getWritableDatabase();
            String query = "delete from " + tableName;
            if (isWhere) {
                query = query + " where " + columnName + " = '" + value + "'";
            }
            mDatabase.execSQL(query);
        } catch (Exception e) {
            isDataDeleted = false;
            Log.e(LOG_TAG, "@@@ master data deletion failed for " + tableName + " error is " + e.getMessage());
            onComplete.execute(false, null, "master data deletion failed for " + tableName + " error is " + e.getMessage());
        } finally {
            closeDataBase();

            if (isDataDeleted) {
                Log.v(LOG_TAG, "@@@ master data deleted successfully for " + tableName);
                onComplete.execute(true, null, "master data deleted successfully for " + tableName);
            }

        }
    }

    //To get the count from table
    public String getCountValue(String query) {
//        mDatabase = palm3FoilDatabase.getWritableDatabase();
        Cursor mOprQuery = null;
        try {
            mOprQuery = mDatabase.rawQuery(query, null);
            if (mOprQuery != null && mOprQuery.moveToFirst()) {
                return mOprQuery.getString(0);
            }

            return null;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mOprQuery.close();
            closeDataBase();
        }
        return "";
    }


    public void closeDataBase() {
//        if (mDatabase != null)
//            mDatabase.close();
    }

    public void executeRawQuery(String query) {
        try {
            if (mDatabase != null) {
                mDatabase.execSQL(query);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }


    //Bulk Insert to the Table
    public boolean bulkinserttoTable(List<ContentValues> cv, final String tableName) {
        boolean isError = false;
        mDatabase.beginTransaction();
        try {
            for (int i = 0; i < cv.size(); i++) {
                ContentValues stockResponse = cv.get(i);
                long id = mDatabase.insert(tableName, null, stockResponse);
                if (id < 0) {
                    isError = true;
                }
            }
            mDatabase.setTransactionSuccessful();
        } finally {
            mDatabase.endTransaction();
        }
        return isError;
    }

    //To Get the Data from the Table
    public void getGradingReportDetails(final String query, final ApplicationThread.OnComplete onComplete) {
        List<GradingReportModel> gradingReportDetails = new ArrayList<>();
        Cursor cursor = null;
//        String query = Queries.getInstance().getCollectionReportDetails();
        Log.v(LOG_TAG, "@@@@ collection reports query " + query);
        try {
            cursor = mDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {

                do {

                    GradingReportModel grading_details = new GradingReportModel();
                    grading_details.setTokenNumber(cursor.getString(cursor.getColumnIndex("TokenNumber")));
                    grading_details.setCCCode(cursor.getString(cursor.getColumnIndex("CCCode")));
                    grading_details.setFruitType(cursor.getString(cursor.getColumnIndex("FruitType")));
                    grading_details.setGrossWeight(cursor.getString(cursor.getColumnIndex("GrossWeight")));
                    grading_details.setTokenDate(cursor.getString(cursor.getColumnIndex("TokenDate")));
                    grading_details.setUnRipen(cursor.getInt(cursor.getColumnIndex("UnRipen")));
                    grading_details.setUnderRipe(cursor.getInt(cursor.getColumnIndex("UnderRipe")));
                    grading_details.setRipen(cursor.getInt(cursor.getColumnIndex("Ripen")));
                    grading_details.setOverRipe(cursor.getInt(cursor.getColumnIndex("OverRipe")));
                    grading_details.setDiseased(cursor.getInt(cursor.getColumnIndex("Diseased")));
                    grading_details.setEmptyBunches(cursor.getInt(cursor.getColumnIndex("EmptyBunches")));
                    grading_details.setFFBQualityLong(cursor.getInt(cursor.getColumnIndex("FFBQualityLong")));
                    grading_details.setFFBQualityMedium(cursor.getInt(cursor.getColumnIndex("FFBQualityMedium")));
                    grading_details.setFFBQualityShort(cursor.getInt(cursor.getColumnIndex("FFBQualityShort")));
                    grading_details.setFFBQualityOptimum(cursor.getInt(cursor.getColumnIndex("FFBQualityOptimum")));
                    grading_details.setLooseFruit(cursor.getString(cursor.getColumnIndex("LooseFruit")));
                    grading_details.setLooseFruitWeight(cursor.getString(cursor.getColumnIndex("LooseFruitWeight")));
                    grading_details.setGraderName(cursor.getString(cursor.getColumnIndex("GraderName")));
                    grading_details.setRejectedBunches(cursor.getString(cursor.getColumnIndex("RejectedBunches")));
                    grading_details.setCreatedDatewithtime(cursor.getString(cursor.getColumnIndex("CreatedDate")));
                    grading_details.setCreatedDate(cursor.getString(cursor.getColumnIndex("date")));
                    grading_details.setVehicleNumber(cursor.getString(cursor.getColumnIndex("VehicleNumber")));
                    grading_details.setLooseFruitorBunches(cursor.getString(cursor.getColumnIndex("LooseFruitORBunches")));
                    grading_details.setGatePassCode(cursor.getString(cursor.getColumnIndex("GatePassCode")));

                    gradingReportDetails.add(grading_details);
                } while (cursor.moveToNext());
            }



        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            closeDataBase();

            onComplete.execute(true, gradingReportDetails, "getting data");
        }
    }

    public T getGradingList(String query, int type) {
        List<GradingReportModel> gradingReportDetails = new ArrayList<>();
        Cursor cursor = null;
        Log.v(LOG_TAG, "Query for getNotvisitedplotInfo " + query);
        GradingReportModel grading_details = null;
        try {
            cursor = mDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    grading_details = new GradingReportModel();
                    grading_details.setTokenNumber(cursor.getString(cursor.getColumnIndex("TokenNumber")));
                    grading_details.setCCCode(cursor.getString(cursor.getColumnIndex("CCCode")));
                    grading_details.setFruitType(cursor.getString(cursor.getColumnIndex("FruitType")));
                    grading_details.setGrossWeight(cursor.getString(cursor.getColumnIndex("GrossWeight")));
                    grading_details.setTokenDate(cursor.getString(cursor.getColumnIndex("TokenDate")));
                    grading_details.setUnRipen(cursor.getInt(cursor.getColumnIndex("UnRipen")));
                    grading_details.setUnderRipe(cursor.getInt(cursor.getColumnIndex("UnderRipe")));
                    grading_details.setRipen(cursor.getInt(cursor.getColumnIndex("Ripen")));
                    grading_details.setOverRipe(cursor.getInt(cursor.getColumnIndex("OverRipe")));
                    grading_details.setDiseased(cursor.getInt(cursor.getColumnIndex("Diseased")));
                    grading_details.setEmptyBunches(cursor.getInt(cursor.getColumnIndex("EmptyBunches")));
                    grading_details.setFFBQualityLong(cursor.getInt(cursor.getColumnIndex("FFBQualityLong")));
                    grading_details.setFFBQualityMedium(cursor.getInt(cursor.getColumnIndex("FFBQualityMedium")));
                    grading_details.setFFBQualityShort(cursor.getInt(cursor.getColumnIndex("FFBQualityShort")));
                    grading_details.setFFBQualityOptimum(cursor.getInt(cursor.getColumnIndex("FFBQualityOptimum")));
                    grading_details.setLooseFruit(cursor.getString(cursor.getColumnIndex("LooseFruit")));
                    grading_details.setLooseFruitWeight(cursor.getString(cursor.getColumnIndex("LooseFruitWeight")));
                    grading_details.setGraderName(cursor.getString(cursor.getColumnIndex("GraderName")));
                    grading_details.setRejectedBunches(cursor.getString(cursor.getColumnIndex("RejectedBunches")));
                    grading_details.setCreatedDatewithtime(cursor.getString(cursor.getColumnIndex("CreatedDate")));
                    grading_details.setCreatedDate(cursor.getString(cursor.getColumnIndex("date")));
                    grading_details.setVehicleNumber(cursor.getString(cursor.getColumnIndex("VehicleNumber")));
                    grading_details.setLooseFruitorBunches(cursor.getString(cursor.getColumnIndex("LooseFruitORBunches")));

                    gradingReportDetails.add(grading_details);
                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return (T) ((type == 0) ? grading_details : gradingReportDetails);

    }


    public void getGatepasstokenreportDetails(final String query, final ApplicationThread.OnComplete onComplete) {
        List<GatepassTokenListModel> gatepasstokenReportDetails = new ArrayList<>();
        Cursor cursor = null;
//        String query = Queries.getInstance().getCollectionReportDetails();
        Log.v(LOG_TAG, "@@@@ collection reports query " + query);
        try {
            cursor = mDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {

                do {

                    GatepassTokenListModel gatepasstoken_details = new GatepassTokenListModel();
                    gatepasstoken_details.setGatePassTokenCode(cursor.getString(cursor.getColumnIndex("GatePassTokenCode")));
                    gatepasstoken_details.setVehicleNumber(cursor.getString(cursor.getColumnIndex("VehicleNumber")));
                    gatepasstoken_details.setGatePassSerialNumber(cursor.getString(cursor.getColumnIndex("GatePassSerialNumber")));
                    gatepasstoken_details.setIsCollection(cursor.getString(cursor.getColumnIndex("IsCollection")));
                    gatepasstoken_details.setCreatedDate(cursor.getString(cursor.getColumnIndex("CreatedDate")));
                    gatepasstoken_details.setMillLocation(cursor.getString(cursor.getColumnIndex("MillLocation")));
                    gatepasstoken_details.setMillLocationTypeId(cursor.getInt(cursor.getColumnIndex("MillLocationTypeId")));
                    gatepasstoken_details.setCreatedBy(cursor.getString(cursor.getColumnIndex("CreatedBy")));

                    gatepasstokenReportDetails.add(gatepasstoken_details);
                } while (cursor.moveToNext());
            }



        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            closeDataBase();

            onComplete.execute(true, gatepasstokenReportDetails, "getting data");
        }
    }


    public T getGatpassserialList(String query, int type) {
        List<GatepassTokenListModel> gatepasstokenlist = new ArrayList<>();
        Cursor cursor = null;
        Log.v(LOG_TAG, "Query for getNotvisitedplotInfo " + query);
        GatepassTokenListModel gatepassseriallist = null;
        try {
            cursor = mDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    gatepassseriallist = new GatepassTokenListModel();
                    gatepassseriallist.setGatePassTokenCode(cursor.getString(cursor.getColumnIndex("GatePassTokenCode")));
                    gatepassseriallist.setVehicleNumber(cursor.getString(cursor.getColumnIndex("VehicleNumber")));
                    gatepassseriallist.setGatePassSerialNumber(cursor.getString(cursor.getColumnIndex("GatePassSerialNumber")));
                    gatepassseriallist.setIsCollection(cursor.getString(cursor.getColumnIndex("IsCollection")));
                    gatepassseriallist.setCreatedDate(cursor.getString(cursor.getColumnIndex("CreatedDate")));
                    gatepassseriallist.setMillLocation(cursor.getString(cursor.getColumnIndex("MillLocation")));
                    gatepassseriallist.setMillLocationTypeId(cursor.getInt(cursor.getColumnIndex("MillLocationTypeId")));
                    gatepassseriallist.setCreatedBy(cursor.getString(cursor.getColumnIndex("CreatedBy")));
                    gatepasstokenlist.add(gatepassseriallist);
                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return (T) ((type == 0) ? gatepassseriallist : gatepasstokenlist);

    }

    public void getGatepassInreportDetails(final String query, final ApplicationThread.OnComplete onComplete) {
        List<GatepassInListModel> gatepassInReportDetails = new ArrayList<>();
        Cursor cursor = null;
//        String query = Queries.getInstance().getCollectionReportDetails();
        Log.v(LOG_TAG, "@@@@ collection reports query " + query);
        try {
            cursor = mDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {

                do {

                    GatepassInListModel gatepassin_details = new GatepassInListModel();
                    gatepassin_details.setGatePassCode(cursor.getString(cursor.getColumnIndex("GatePassCode")));
                    gatepassin_details.setGatePassTokenCode(cursor.getString(cursor.getColumnIndex("GatePassTokenCode")));
                    gatepassin_details.setCreatedDate(cursor.getString(cursor.getColumnIndex("CreatedDate")));
                    gatepassin_details.setWBCode(cursor.getString(cursor.getColumnIndex("WBCode")));
                    gatepassin_details.setVehicleType(cursor.getString(cursor.getColumnIndex("VehicleType")));
                    gatepassin_details.setWBID(cursor.getString(cursor.getColumnIndex("WBID")));
                    gatepassin_details.setVehicleCategory(cursor.getString(cursor.getColumnIndex("VehicleCategory")));
                    gatepassin_details.setFruitType(cursor.getString(cursor.getColumnIndex("FruitType")));
                    gatepassin_details.setVehicleNumber(cursor.getString(cursor.getColumnIndex("VehicleNumber")));
                    gatepassin_details.setVehicleTypeId(cursor.getString(cursor.getColumnIndex("VehicleTypeId")));
                    gatepassin_details.setVehicleCategoryId(cursor.getString(cursor.getColumnIndex("VehicleCategoryId")));
                    gatepassin_details.setMillLocation(cursor.getString(cursor.getColumnIndex("MillLocation")));
                    gatepassin_details.setMillLocationTypeId(cursor.getInt(cursor.getColumnIndex("MillLocationTypeId")));
                    gatepassin_details.setSerialNumber(cursor.getString(cursor.getColumnIndex("SerialNumber")));
                    gatepassin_details.setCreatedBy(cursor.getString(cursor.getColumnIndex("CreatedBy")));



                    gatepassInReportDetails.add(gatepassin_details);
                } while (cursor.moveToNext());
            }



        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            closeDataBase();

            onComplete.execute(true, gatepassInReportDetails, "getting data");
        }
    }

    public T getGatpassinList(String query, int type) {
        List<GatepassInListModel> gatepassinList = new ArrayList<>();
        Cursor cursor = null;
        Log.v(LOG_TAG, "Query for getNotvisitedplotInfo " + query);
        GatepassInListModel gatepassin_details = null;
        try {
            cursor = mDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    gatepassin_details = new GatepassInListModel();
                    gatepassin_details.setGatePassCode(cursor.getString(cursor.getColumnIndex("GatePassCode")));
                    gatepassin_details.setGatePassTokenCode(cursor.getString(cursor.getColumnIndex("GatePassTokenCode")));
                    gatepassin_details.setCreatedDate(cursor.getString(cursor.getColumnIndex("CreatedDate")));
                    gatepassin_details.setWBCode(cursor.getString(cursor.getColumnIndex("WBCode")));
                    gatepassin_details.setWBName(cursor.getString(cursor.getColumnIndex("WBName")));
                    gatepassin_details.setVehicleType(cursor.getString(cursor.getColumnIndex("VehicleType")));
                    gatepassin_details.setWBID(cursor.getString(cursor.getColumnIndex("WBID")));
                    gatepassin_details.setVehicleCategory(cursor.getString(cursor.getColumnIndex("VehicleCategory")));
                    gatepassin_details.setFruitType(cursor.getString(cursor.getColumnIndex("FruitType")));
                    gatepassin_details.setVehicleNumber(cursor.getString(cursor.getColumnIndex("VehicleNumber")));
                    gatepassin_details.setVehicleTypeId(cursor.getString(cursor.getColumnIndex("VehicleTypeId")));
                    gatepassin_details.setVehicleCategoryId(cursor.getString(cursor.getColumnIndex("VehicleCategoryId")));
                    gatepassin_details.setMillLocation(cursor.getString(cursor.getColumnIndex("MillLocation")));
                    gatepassin_details.setMillLocationTypeId(cursor.getInt(cursor.getColumnIndex("MillLocationTypeId")));
                    gatepassin_details.setSerialNumber(cursor.getString(cursor.getColumnIndex("SerialNumber")));
                    gatepassin_details.setCreatedBy(cursor.getString(cursor.getColumnIndex("CreatedBy")));
                    gatepassinList.add(gatepassin_details);
                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return (T) ((type == 0) ? gatepassin_details : gatepassinList);

    }




    //To get user details
    public T getUserDetails(final String query, int dataReturnType) {
        UserDetails userDetails = null;
        Cursor cursor = null;
        List userDataList = new ArrayList();
        Log.v(LOG_TAG, "@@@ user details query " + query);
        try {
            cursor = mDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    userDetails = new UserDetails();
                    userDetails.setUserId(cursor.getString(0));
                    userDetails.setUserName(cursor.getString(1));
                    userDetails.setPassword(cursor.getString(2));
                    userDetails.setRoleId(cursor.getInt(3));
                    userDetails.setManagerId(cursor.getInt(4));
                    userDetails.setId(cursor.getString(5));
                    userDetails.setFirstName(cursor.getString(6));
                    userDetails.setTabName(cursor.getString(7));
                    userDetails.setUserCode(cursor.getString(8));
//                    userDetails.setTabletId(cursor.getInt(5));
//                    userDetails.setUserVillageId(cursor.getString(6));
                    if (dataReturnType == 1) {
                        userDataList.add(userDetails);
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "@@@ getting user details " + e.getMessage());
        }
        return (T) ((dataReturnType == 0) ? userDetails : userDataList);
    }

    //To get the Grading Repository Details
    public T getGradingRepoDetails(final String query, final int type) {
        List<GradingFileRepository> gradingrepolist = new ArrayList<>();
        GradingFileRepository mgradingrepository = null;
        Cursor cursor = null;
        Log.v(LOG_TAG, "@@@ GradingRepo details query " + query);
        try {
            cursor = mDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {


                    mgradingrepository = new GradingFileRepository();

                    String filelocation = cursor.getString(cursor.getColumnIndex("FileLocation"));

                    File imagefile = new File(filelocation);
                    FileInputStream fis = null;
                    try {
                        fis = new FileInputStream(imagefile);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    Bitmap bm = BitmapFactory.decodeStream(fis);
                    bm = ImageUtility.rotatePicture(90, bm);
                    String base64string = ImageUtility.convertBitmapToString(bm);
                    mgradingrepository.setImageString(base64string);

//
//                    if(filelocation != null){
//                        try{
//                            mgradingrepository.setImageString(CommonUtils.encodeFileToBase64Binary(new File(filelocation)));
//
//                        }catch (Exception exc){
//
//                        }
//                    }

                    mgradingrepository.setTokenNumber(cursor.getString(cursor.getColumnIndex("TokenNumber")));
                    mgradingrepository.setCCCode(cursor.getString(cursor.getColumnIndex("CCCode")));
                    mgradingrepository.setFruitType(cursor.getInt(cursor.getColumnIndex("FruitType")));
                    mgradingrepository.setGrossWeight(cursor.getDouble(cursor.getColumnIndex("GrossWeight")));
                    mgradingrepository.setFileName(cursor.getString(cursor.getColumnIndex("FileName")));
                    //mgradingrepository.setFileLocation(cursor.getString(cursor.getColumnIndex("FileLocation")));
                    mgradingrepository.setFileExtension(cursor.getString(cursor.getColumnIndex("FileExtension")));
                    mgradingrepository.setCreatedByUserId(cursor.getInt(cursor.getColumnIndex("CreatedByUserId")));
                    mgradingrepository.setCreatedDate(cursor.getString(cursor.getColumnIndex("CreatedDate")));
                    mgradingrepository.setServerUpdatedStatus(0);
                    if (type == 1) {
                        gradingrepolist.add(mgradingrepository);
                        mgradingrepository = null;
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "@@@ getting GradingRepo details " + e.getMessage());
        }
        return (T) ((type == 0) ? mgradingrepository : gradingrepolist);
    }

    public T getGatepasstokendetails(final String query, final int type) {
        List<GatePassToken> gatepasslist = new ArrayList<>();
        GatePassToken  gatePassTokendata= null;
        Cursor cursor = null;
        Log.v(LOG_TAG, "@@@ GatePassToken details query " + query);
        try {
            cursor = mDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {


                    gatePassTokendata = new GatePassToken();
                    gatePassTokendata.setGatePassTokenCode(cursor.getString(cursor.getColumnIndex("GatePassTokenCode")));
                    gatePassTokendata.setVehicleNumber(cursor.getString(cursor.getColumnIndex("VehicleNumber")));
                    gatePassTokendata.setGatePassSerialNumber(cursor.getInt(cursor.getColumnIndex("GatePassSerialNumber")));

                    Log.d("IsCollection", String.valueOf(cursor.getInt(cursor.getColumnIndex("IsCollection"))));

                    gatePassTokendata.setIsCollection(cursor.getInt(cursor.getColumnIndex("IsCollection")));
                    gatePassTokendata.setLocationId(cursor.getInt(cursor.getColumnIndex("MillLocationTypeId")));
                    gatePassTokendata.setCreatedByUserId(cursor.getInt(cursor.getColumnIndex("CreatedByUserId")));
                    gatePassTokendata.setCreatedDate(cursor.getString(cursor.getColumnIndex("CreatedDate")));
                    gatePassTokendata.setServerUpdatedStatus(0);

                    if (type == 1) {
                        gatepasslist.add(gatePassTokendata);
                        gatePassTokendata = null;
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "@@@ getting GradingRepo details " + e.getMessage());
        }
        return (T) ((type == 0) ? gatePassTokendata : gatepasslist);
    }

    public String getserialnumber(final String maxNum) {
        // String maxNum = getOnlyOneValueFromDb(query);
        String convertedNum = "";
        if (!TextUtils.isEmpty(maxNum)) {
            convertedNum = CommonUtils.serialNumber(Integer.parseInt(maxNum) , 4);
        } else {
            convertedNum = CommonUtils.serialNumber(1, 4);
        }
        //   StringBuilder farmerCoder = new StringBuilder();
        String finalNumber = StringUtils.leftPad(convertedNum,4,"0");

        Log.v(LOG_TAG, "@@@ finalNumber code " + finalNumber);
        return finalNumber;
    }


    public LinkedHashMap<String, String> getvechileData(final String query) {
        Log.v(LOG_TAG, "@@@ Generic Query " + query);
        LinkedHashMap<String, String> mGenericData = new LinkedHashMap<>();
        Cursor genericDataQuery = null;
        try {
            genericDataQuery = mDatabase.rawQuery(query, null);
            if (genericDataQuery.moveToFirst()) {
                do {
                    mGenericData.put(genericDataQuery.getString(0), genericDataQuery.getString(2));
                } while (genericDataQuery.moveToNext());
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        } finally {
//            palm3FoilDatabase.closeDataBase();
            if (null != genericDataQuery)
                genericDataQuery.close();

            closeDataBase();
        }
        return mGenericData;
    }


    public T getGatepassdetails(final String query, final int type) {
        List<GatePass> gatepasslist = new ArrayList<>();
        GatePass  gatePassdata= null;
        Cursor cursor = null;
        Log.v(LOG_TAG, "@@@ GatePass details query " + query);
        try {
            cursor = mDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {

                    gatePassdata = new GatePass();
                    gatePassdata.setGatePassCode(cursor.getString(cursor.getColumnIndex("GatePassCode")));
                    gatePassdata.setGatePassTokenCode(cursor.getString(cursor.getColumnIndex("GatePassTokenCode")));
                    gatePassdata.setWeighbridgeId(cursor.getInt(cursor.getColumnIndex("WeighbridgeId")));
                    gatePassdata.setVehicleTypeId(cursor.getInt(cursor.getColumnIndex("VehicleTypeId")));
                    gatePassdata.setCreatedByUserId(cursor.getInt(cursor.getColumnIndex("CreatedByUserId")));
                    gatePassdata.setCreatedDate(cursor.getString(cursor.getColumnIndex("CreatedDate")));
                    gatePassdata.setUpdatedByUserId(cursor.getInt(cursor.getColumnIndex("UpdatedByUserId")));
                    gatePassdata.setUpdatedDate(cursor.getString(cursor.getColumnIndex("UpdatedDate")));
                    gatePassdata.setMillLocationTypeId(cursor.getInt(cursor.getColumnIndex("MillLocationTypeId")));
                    gatePassdata.setSerialNumber(cursor.getInt(cursor.getColumnIndex("SerialNumber")));
                    gatePassdata.setFruitType(cursor.getInt(cursor.getColumnIndex("FruitType")));
                    gatePassdata.setVehicleNumber(cursor.getString(cursor.getColumnIndex("VehicleNumber")));
                    gatePassdata.setServerUpdatedStatus(0);
                    if (type == 1) {
                        gatepasslist.add(gatePassdata);
                        gatePassdata = null;
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "@@@ getting GatePass details " + e.getMessage());
        }
        return (T) ((type == 0) ? gatePassdata : gatepasslist);
    }

    public T getGatepassoutdetails(final String query, final int type) {
        List<GatePassOut> gatepassoutlist = new ArrayList<>();
        GatePassOut  gatePassoutdata= null;
        Cursor cursor = null;
        Log.v(LOG_TAG, "@@@ GatePass details query " + query);
        try {
            cursor = mDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {

                    gatePassoutdata = new GatePassOut();
                    gatePassoutdata.setGatePassCode(cursor.getString(cursor.getColumnIndex("GatePassCode")));
                    gatePassoutdata.setCreatedByUserId(cursor.getInt(cursor.getColumnIndex("CreatedByUserId")));
                    gatePassoutdata.setCreatedDate(cursor.getString(cursor.getColumnIndex("CreatedDate")));
                    gatePassoutdata.setGatePassInTime(cursor.getString(cursor.getColumnIndex("GatePassInTime")));
                    gatePassoutdata.setServerUpdatedStatus(0);
                    if (type == 1) {
                        gatepassoutlist.add(gatePassoutdata);
                        gatePassoutdata = null;
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "@@@ getting GatePass details " + e.getMessage());
        }
        return (T) ((type == 0) ? gatePassoutdata : gatepassoutlist);
    }


    public T getgatepassDetails(final String query, int dataReturnType) {
        Gatepassoutdetails Gatepassout_details = null;
        Cursor cursor = null;
        List Gatepassoutdetailslist = new ArrayList();
        Log.v(LOG_TAG, "@@@ user details query " + query);
        try {
            cursor = mDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Gatepassout_details = new Gatepassoutdetails();
                    Gatepassout_details.setGatePassSerialNumber(cursor.getString(0));
                    Gatepassout_details.setVehicleNumber(cursor.getString(1));
                    Gatepassout_details.setCreatedDate(cursor.getString(2));

                    if (dataReturnType == 1) {
                        Gatepassoutdetailslist.add(Gatepassout_details);
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "@@@ getting Gatepassoutdetailslist " + e.getMessage());
        }
        return (T) ((dataReturnType == 0) ? Gatepassout_details : Gatepassoutdetailslist);
    }

    public synchronized void updateData(String tableName, List<LinkedHashMap> list, Boolean isClaues, String whereCondition, final ApplicationThread.OnComplete<String> oncomplete) {
        boolean isUpdateSuccess = false;
        int checkCount = 0;
        try {
            for (int i = 0; i < list.size(); i++) {
                checkCount++;
                List<Map.Entry> entryList = new ArrayList<Map.Entry>((list.get(i)).entrySet());
                String query = "update " + tableName + " set ";
                String namestring = "";

                System.out.println("\n==> Size of Entry list: " + entryList.size());
                StringBuffer columns = new StringBuffer();
                for (Map.Entry temp : entryList) {
                    columns.append(temp.getKey());
                    columns.append("='");
                    columns.append(temp.getValue());
                    columns.append("',");
                }

                namestring = columns.deleteCharAt(columns.length() - 1).toString();
                query = query + namestring + "" + whereCondition;
                mDatabase.execSQL(query);
                isUpdateSuccess = true;
                Log.v(LOG_TAG, "@@@ query for  " + query);
            }
        } catch (Exception e) {
            checkCount++;
            e.printStackTrace();
            isUpdateSuccess = false;
        } finally {
            closeDataBase();
            if (checkCount == list.size()) {
                if (isUpdateSuccess) {
                    Log.v(LOG_TAG, "@@@ data updated successfully for " + tableName);
                    oncomplete.execute(true, null, "data updated successfully for " + tableName);
                } else {
                    oncomplete.execute(false, null, "data updation failed for " + tableName);
                }
            }
        }
    }

    //tp get single list data from db
    public List<String> getSingleListData(final String query) {
        Log.v(LOG_TAG, "@@@ Generic Query " + query);
        List<String> mGenericData = new ArrayList<>();
        Cursor genericDataQuery = null;
        try {
            genericDataQuery = mDatabase.rawQuery(query, null);
            if (genericDataQuery.moveToFirst()) {
                do {
                    String plotCode = genericDataQuery.getString(0);
                    mGenericData.add(plotCode);
                } while (genericDataQuery.moveToNext());
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        } finally {
            if (null != genericDataQuery)
                genericDataQuery.close();
            closeDataBase();
        }
        return mGenericData;
    }

}
