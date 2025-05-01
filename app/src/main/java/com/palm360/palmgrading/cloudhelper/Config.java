package com.palm360.palmgrading.cloudhelper;

import com.palm360.palmgrading.BuildConfig;

//Urls Listing
public class Config {

    public static final boolean DEVELOPER_MODE = false;

    //public static  String live_url = "http://182.18.139.166/3FOilPalm/API/api";//Live
    //public static String live_url = "http://182.18.157.215/3FSmartPalm_Nursery/API/api";//Test
   //public static String live_url = "http://182.18.157.215/3FSmartPalmNursery_UAT/API/api";//Current UAT NOV 19th
   public static String live_url = "hhttp://182.18.157.215/Palm360/API/api";//New test url 24th Jan 2025
    public static void initialize() {
        if (BuildConfig.BUILD_TYPE.equalsIgnoreCase("release")) {

            //live_url = "http://182.18.139.166/3FOilPalm/API/api";
            //live_url = "http://182.18.157.215/3FSmartPalm_Nursery/API/api";
            //live_url = "http://182.18.157.215/3FSmartPalmNursery_UAT/API/api";//Current UAT NOV 19th
            live_url = "http://182.18.157.215/Palm360/API/api";//New test url 24th Jan 2025

        } else {

            //live_url = "http://182.18.139.166/3FOilPalm/API/api";
            //live_url = "http://182.18.157.215/3FSmartPalm_Nursery/API/api";
            //live_url = "http://182.18.157.215/3FSmartPalmNursery_UAT/API/api";//Current UAT NOV 19th
            live_url = "http://182.18.157.215/Palm360/API/api";//New test url 24th Jan 2025
        }
    }

    public static final String masterSyncUrl = "/SyncMasters/SyncFFBGradingMasters";

    public static final String transactionSyncURL = "/SyncTransactions/SyncGradingTransactions";
    public static final String locationTrackingURL = "/LocationTracker/SaveOfflineLocations";
    public static final String imageUploadURL = "/SyncTransactions/UploadImage";

    public static final String findcollectioncode = "/SyncTransactions/FindCollectionCode/%s";
    public static final String findconsignmentcode = "/SyncTransactions/FindConsignmentCode/%s";
    public static final String findcollectionplotcode = "/SyncTransactions/FindCollectionPlotXref/%s/%s";

    public static final String updatedbFile = "/TabDatabase/UploadDatabase";

    public static final String getTransCount = "/SyncTransactions/GetCount";//{Date}/{UserId}
    public static final String getTransData = "/SyncTransactions/%s";//api/TranSync/SyncFarmers/{Date}/{UserId}/{Index}
    public static final String validateTranSync = "/TranSync/ValidateTranSync/%s";
    //public static final String image_url = "http://182.18.139.166/3FOilPalm/3FOilPalmRepository/FileRepository/";//Commented on 12-04-2021 to change the URL to local
    //public static final String image_url = "http://183.82.111.111/3FOilPalm/3FFileRepositery/FileRepository/";
    // public static final String image_url = "http://183.82.111.111/3FOilPalm_Live/3FOilPalmRepository/FileRepository/";
   // public static final String image_url = "http://182.18.157.215/3FSmartPalm_Nursery/3FSmartPalm_Nursery_Repo/FileRepository/";
    //public static final String image_url = "http://182.18.157.215/3FSmartPalmNursery_UAT/3FSmartPalm_Nursery_Repo/FileRepository/";//UAT
    public static final String GETMONTHLYTARGETSBYUSERIDANDFINANCIALYEAR = "/KRA/GetMonthlyTargetsByUserIdandFinancialYear";
    public static final String GETTARGETSBYUSERIDANDFINANCIALYEAR = "/KRA/GetTargetsByUserIdandFinancialYear";
    public static final String GET_ALERTS = "/SyncTransactions/SyncAlertDetails/";//{UserId}
}
