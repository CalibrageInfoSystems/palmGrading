package com.oilpalm3f.gradingapp.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.oilpalm3f.gradingapp.common.CommonConstants;

import static android.content.Context.MODE_PRIVATE;

public class DataBaseUpgrade {
    private static final String LOG_TAG = DataBaseUpgrade.class.getName();

    static void upgradeDataBase(final Context context, final SQLiteDatabase db) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("appprefs", MODE_PRIVATE);
        boolean result = true;
        try {
            boolean isFreshInstall = sharedPreferences.getBoolean(CommonConstants.IS_FRESH_INSTALL, true);
            if (isFreshInstall) {
                upgradeDb1(db);
                upgradeDb2(db);
                upgradeDb3(db);
                upgradeDb4(db);
//

            } else {
                boolean isDbUpgradeFinished = sharedPreferences.getBoolean(String.valueOf(Palm3FoilDatabase.DATA_VERSION), false);
                Log.v(LOG_TAG, "@@@@ database....." + isDbUpgradeFinished);
                if (!isDbUpgradeFinished) {
                    switch (Palm3FoilDatabase.DATA_VERSION) {
                        case 1:
                            upgradeDb1(db);
                            break;
                        case 2:
                            upgradeDb2(db);
                            break;
                        case 3:
                            upgradeDb3(db);
                            break;
                        case 4:
                            upgradeDb4(db);
                            break;

                    }
                } else {
                    Log.v(LOG_TAG, "@@@@ database is already upgraded " + Palm3FoilDatabase.DATA_VERSION);
                }
            }

        } catch (Exception e) {
            result = false;
        } finally {
            if (result) {
                Log.v(LOG_TAG, "@@@@ database is upgraded " + Palm3FoilDatabase.DATA_VERSION);
            } else {
                Log.e(LOG_TAG, "@@@@ database is upgrade failed or already upgraded");
            }
            sharedPreferences.edit().putBoolean(CommonConstants.IS_FRESH_INSTALL, false).apply();
            sharedPreferences.edit().putBoolean(String.valueOf(Palm3FoilDatabase.DATA_VERSION), true).apply();
        }
    }


    public static void upgradeDb1(final SQLiteDatabase db) {
        Log.d(LOG_TAG, "******* upgradeDataBase " + Palm3FoilDatabase.DATA_VERSION);

        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void upgradeDb2( final SQLiteDatabase db) {
        Log.d(LOG_TAG, "******* upgradeDataBase 2 ******" + Palm3FoilDatabase.DATA_VERSION);

        String fingerprintcolumn = "ALTER TABLE CollectionCenter Add IsFingerPrintReq BIT";
        String column1 = "Alter Table FFBGrading Add VehicleNumber Varchar(50)";


        String locationidcc = "ALTER TABLE CollectionCenter Add MillLocationTypeId INT";




        String GatePassToken = "CREATE TABLE GatePassToken(    " +
                " Id                                INTEGER     PRIMARY KEY AUTOINCREMENT ,\n" +
                " GatePassTokenCode                         VARCHAR(100)             NOT NULL,\n" +
                " VehicleNumber            VARCHAR(50)             NOT NULL,\n" +
                " GatePassSerialNumber          INT              NOT NULL,\n" +
                " IsCollection    BOOLEAN       NOT NULL,\n" +
                " MillLocationTypeId  INT,\n" +
                " CreatedByUserId                     INT                 NOT NULL,\n" +
                " CreatedDate                      DATETIME               NOT NULL,\n" +
                "    ServerUpdatedStatus     BOOLEAN       NOT NULL\n" +
                " );";

        String GatePass = "CREATE TABLE GatePass(  " +
                " Id                                INTEGER     PRIMARY KEY AUTOINCREMENT ,\n" +
                " GatePassCode                         VARCHAR(100)             NOT NULL,\n" +
                " GatePassTokenCode             VARCHAR(100)             NOT NULL,\n" +
                " WeighbridgeId         INT                 NOT NULL,\n" +
                " VehicleTypeId     INT           NOT NULL,\n" +
                " CreatedByUserId                     INT                 NOT NULL,\n" +
                " CreatedDate                      DATETIME               NOT NULL,\n" +
                " UpdatedByUserId                    INT                  NOT NULL,\n" +
                " UpdatedDate                      DATETIME               NOT NULL,\n" +
                " MillLocationTypeId                     INT                 NOT NULL,\n" +
                " SerialNumber                    INT               NOT NULL,\n" +
                " FruitType                    BOOLEAN                  NOT NULL,\n" +
                " VehicleNumber                     VARCHAR(50)                   NOT NULL,\n" +
                "    ServerUpdatedStatus     BOOLEAN       NOT NULL\n" +
                " );";


        String ActivityRight = "CREATE TABLE ActivityRight(    " +
                " Id                                INTEGER    ,\n" +
                " Name            VARCHAR(100)             NOT NULL,\n" +
                " Desc                         VARCHAR(500)             NOT NULL,\n" +
                " IsActive    BOOLEAN       NOT NULL,\n" +
                " CreatedByUserId                     INT                 NOT NULL,\n" +
                " CreatedDate                      DATETIME               NOT NULL,\n" +
                " UpdatedByUserId                    INT                  NOT NULL,\n" +
                " UpdatedDate                      DATETIME               NOT NULL\n" +
                " );";

        String ClassType = "CREATE TABLE ClassType(    " +
                " ClassTypeId                              INTEGER    ,\n" +
                " Name            VARCHAR(255)             NOT NULL,\n" +
                " IsActive    BOOLEAN       NOT NULL,\n" +
                " CreatedByUserId                     INT                 NOT NULL,\n" +
                " CreatedDate                      DATETIME               NOT NULL,\n" +
                " UpdatedByUserId                    INT                  NOT NULL,\n" +
                " UpdatedDate                      DATETIME               NOT NULL\n" +
                " );";

        String TypeCdDmt = "CREATE TABLE TypeCdDmt(    " +
                " TypeCdId                                INTEGER    NOT NULL ,\n" +
                " ClassTypeId                              INTEGER    NOT NULL ,\n" +
                " Desc            VARCHAR(255)     NOT NULL,\n" +
                " TableName            VARCHAR(255)  ,\n" +
                " ColumnName            VARCHAR(255)  ,\n" +
                " SortOrder                                INTEGER    ,\n" +
                " IsActive    BOOLEAN       NOT NULL,\n" +
                " CreatedByUserId                     INT                 NOT NULL,\n" +
                " CreatedDate                      DATETIME               NOT NULL,\n" +
                " UpdatedByUserId                    INT                  NOT NULL,\n" +
                " UpdatedDate                      DATETIME               NOT NULL\n" +
                " );";



        String LookUp = "CREATE TABLE LookUp(    " +
                " Id                                INTEGER    ,\n" +
                " LookUpTypeId                              INTEGER    ,\n" +
                " Name                         VARCHAR(255)             NOT NULL,\n" +
                " Remarks            VARCHAR(10000)  ,\n" +
                " IsActive    BOOLEAN       NOT NULL,\n" +
                " CreatedByUserId                     INT                 NOT NULL,\n" +
                " CreatedDate                      DATETIME               NOT NULL,\n" +
                " UpdatedByUserId                    INT                  NOT NULL,\n" +
                " UpdatedDate                      DATETIME               NOT NULL\n" +
                " );";

        String MillWeighBridge = "CREATE TABLE MillWeighBridge(    " +
                " Id                                INTEGER     PRIMARY KEY AUTOINCREMENT ,\n" +
                " Code                         VARCHAR(25)             NOT NULL,\n" +
                " Name            VARCHAR(100)             NOT NULL,\n" +
                " IsActive    BOOLEAN       NOT NULL,\n" +
                " MillLocationTypeId  INT ,\n" +
                " IsAutomatic    BOOLEAN       NOT NULL,\n" +
                " CreatedByUserId                     INT                 NOT NULL,\n" +
                " CreatedDate                      DATETIME               NOT NULL,\n" +
                " UpdatedByUserId                    INT                  NOT NULL,\n" +
                " UpdatedDate                      DATETIME               NOT NULL\n" +
                " );";


        String UserMillWeighBridgexref = "CREATE TABLE UserMillWeighBridgexref(    " +

                " UserId                     INT                         NOT NULL,\n" +
                " MillWeighBridgeId                     INT                 NOT NULL\n" +
                " );";

       // String column2 = "Alter Table FFBGrading Add GatePassCode Varchar(100)";

        String Role = "CREATE TABLE Role(    " +
                " Id INTEGER PRIMARY KEY AUTOINCREMENT ,\n" +
                " Code VARCHAR(50) NOT NULL,\n" +
                " Name VARCHAR(255) NOT NULL,\n" +
                " Desc VARCHAR(500) NULL,\n" +
                " ParentRoleId INT NULL,\n" +
                " IsActive BOOLEAN NOT NULL,\n" +
                " CreatedByUserId INT NOT NULL,\n" +
                " CreatedDate DATETIME NOT NULL,\n" +
                " UpdatedByUserId INT NOT NULL,\n" +
                " UpdatedDate DATETIME NOT NULL\n" +
                " );";

        String RoleActivityRightXref = "CREATE TABLE RoleActivityRightXrefs(    " +

                " RoleId                     INT                         NOT NULL,\n" +
                " ActivityRightId                     INT                 NOT NULL\n" +
                " );";

//        String LocationIdGP  = "Alter Table GatePass Add MillLocationTypeId  INT NOT NULL";
//        String SerialNumber   = "Alter Table GatePass Add SerialNumber INT NOT NULL";
//        String FruitType   = "Alter Table GatePass Add FruitType  BOOLEAN NOT NULL";
//        String VehicleNumber   = "Alter Table GatePass Add VehicleNumber  Varchar(50) NOT NULL";

        String GatePassOut  = "CREATE TABLE GatePassOut (    " +
                " Id INTEGER PRIMARY KEY AUTOINCREMENT ,\n" +
                " GatePassCode VARCHAR(50) NOT NULL,\n" +
                " CreatedByUserId  INT NOT NULL, \n" +
                " CreatedDate DATETIME NOT NULL, \n" +
                " GatePassInTime DATETIME NOT NULL, \n" +
                " ServerUpdatedStatus BOOLEAN NOT NULL\n" +
                " );";

        //String LocationIdGPS   = "Alter Table GatePassToken Add LocationId  INT NOT NULL";

        String locationcc  = "Alter Table CollectionCenter Add LocationId  INT NOT NULL";

        try {
            db.execSQL(fingerprintcolumn);
            db.execSQL(column1);
            db.execSQL(locationidcc);
            db.execSQL(GatePassToken);
            db.execSQL(GatePass);
            db.execSQL(ActivityRight);
            db.execSQL(ClassType);
            db.execSQL(TypeCdDmt);
            db.execSQL(LookUp);
            db.execSQL(MillWeighBridge);
            db.execSQL(UserMillWeighBridgexref);
           // db.execSQL(column2);

            db.execSQL(Role);
            db.execSQL(RoleActivityRightXref);
            db.execSQL(GatePassOut);

//            db.execSQL(LocationIdGP);
//            db.execSQL(SerialNumber);
//            db.execSQL(FruitType);
//            db.execSQL(VehicleNumber);



        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void upgradeDb3( final SQLiteDatabase db) {
        Log.d(LOG_TAG, "******* upgradeDataBase 3 ******" + Palm3FoilDatabase.DATA_VERSION);

        String column1 = "Alter Table FFBGrading Add LooseFruitORBunches Varchar(50)";

        try {

            db.execSQL(column1);

//            db.execSQL(LocationIdGP);
//            db.execSQL(SerialNumber);
//            db.execSQL(FruitType);
//            db.execSQL(VehicleNumber);



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void upgradeDb4( final SQLiteDatabase db) {
        Log.d(LOG_TAG, "******* upgradeDataBase 4 ******" + Palm3FoilDatabase.DATA_VERSION);

        String millWeighBridgeXref = "CREATE TABLE MillWeighBridgeXref(    " +

                " WeighBridgeCode VARCHAR(25) NOT NULL ,\n" +
                " MillCode VARCHAR(10) NOT NULL \n" +
                " );";

        try {

            db.execSQL(millWeighBridgeXref);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
