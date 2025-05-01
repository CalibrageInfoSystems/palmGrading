package com.palm360.palmgrading.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.palm360.palmgrading.common.CommonUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;

//Creating/Opening/Closing/Copying DB
public class Palm3FoilDatabase extends SQLiteOpenHelper {

    public static final String LOG_TAG = Palm3FoilDatabase.class.getName();
    // TODO NEED TO SET VESION 30 for LIVE
//    public final static int DATA_VERSION = 30;
    public final static int DATA_VERSION = 4; //Changed to 5th March
    //    public final static int DATA_VERSION = 28;
    private final static String DATABASE_NAME = "3foilpalm.sqlite";
    public static String Lock = "dblock";
    private static Palm3FoilDatabase palm3FoilDatabase;
    private static String DB_PATH;
    private static SQLiteDatabase mSqLiteDatabase = null;
    private Context mContext;
    File rootDirectory;
    private static String FULL_DB_PATH;


    public Palm3FoilDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATA_VERSION);
        this.mContext = context;

        File dbDirectory = new File(CommonUtils.get3FFileRootPath() + "3F_Database");
        if (!dbDirectory.exists()) {
            boolean created = dbDirectory.mkdirs();
            Log.d(LOG_TAG, "DB directory created: " + created);
        }

        DB_PATH = dbDirectory.getAbsolutePath() + File.separator;
        FULL_DB_PATH = DB_PATH + DATABASE_NAME;

        Log.v("The Database Path", FULL_DB_PATH);
    }

    public static synchronized Palm3FoilDatabase getPalm3FoilDatabase(Context context) {
        synchronized (Lock) {
            if (palm3FoilDatabase == null) {
                palm3FoilDatabase = new Palm3FoilDatabase(context);
            }
            return palm3FoilDatabase;
        }
    }

    public static SQLiteDatabase openDataBaseNew() throws SQLException {
        if (mSqLiteDatabase == null || !mSqLiteDatabase.isOpen()) {
            mSqLiteDatabase = SQLiteDatabase.openDatabase(FULL_DB_PATH, null,
                    SQLiteDatabase.OPEN_READWRITE | SQLiteDatabase.CREATE_IF_NECESSARY | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
        }
        return mSqLiteDatabase;
    }

    public static void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    public void createDataBase() throws IOException {
        if (!checkDataBase()) {
            try {
                copyDataBase(); // You may need to define this or clarify if you're copying from assets
                Log.v("dbcopied:::", "true");
            } catch (Exception e) {
                e.printStackTrace();
                throw new Error("Error copying database");
            }

            try {
                openDataBaseNew();
            } catch (Exception e) {
                e.printStackTrace();
                throw new Error("Error opening database");
            }
        }
    }

    private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        try {
            Log.e("DB Check Path", FULL_DB_PATH);
            checkDB = SQLiteDatabase.openDatabase(FULL_DB_PATH, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
            Log.w(LOG_TAG, "Database does not exist yet.");
        }

        if (checkDB != null) {
            checkDB.close();
            return true;
        }

        return false;
    }


    /*  private boolean checkDataBase() {
        boolean dataBaseExisted = false;
        try {
            String check_Path = DB_PATH + DATABASE_NAME;
            mSqLiteDatabase = SQLiteDatabase.openDatabase(check_Path, null, SQLiteDatabase.OPEN_READWRITE);
        } catch (Exception ex) {
            // TODO: handle exception
            ex.printStackTrace();
        }
        return mSqLiteDatabase != null;
    }
*/
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    private void copyDataBase() throws IOException {
        File dbDir = new File(DB_PATH);
        if (!dbDir.exists()) {
            dbDir.mkdir();

        }
        InputStream myInput = mContext.getAssets().open(DATABASE_NAME);
        OutputStream myOutput = new FileOutputStream(DB_PATH + DATABASE_NAME);
        copyFile(myInput, myOutput);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}


