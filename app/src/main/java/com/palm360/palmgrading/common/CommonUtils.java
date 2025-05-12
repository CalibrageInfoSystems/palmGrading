package com.palm360.palmgrading.common;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.palm360.palmgrading.cloudhelper.ApplicationThread;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;

//Commonly used functions written here
public class CommonUtils {

    public static final int REQUEST_CAM_PERMISSIONS = 1;
    public static final int FROM_CAMERA = 1;
    public static final int FROM_GALLERY = 2;
    public static ArrayList<String> tableNames = new ArrayList<>();
    public static final int PERMISSION_CODE = 100;
    public static FragmentActivity currentActivity;
    public static DecimalFormat twoDForm = new DecimalFormat("#.##");

    static Pattern pattern = null;
    static Matcher matcher;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static String LOG_TAG = CommonUtils.class.getName();


    //To check is network available
    public static boolean isNetworkAvailable(final Context context) {
//        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        if (null != connectivityManager) {
//            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
//            return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
//        }
//
//        return false;

        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();

            Log.d(CommonUtils.class.getSimpleName(),"---> IS NET AVAILABLE  :"+exitValue);
            return (exitValue == 0);
        }
        catch (IOException e)          {
            Log.d(CommonUtils.class.getSimpleName(),"---> IS NET AVAILABLE error :"+e.getLocalizedMessage());
            e.printStackTrace(); }
        catch (InterruptedException e) {
            Log.d(CommonUtils.class.getSimpleName(),"---> IS NET AVAILABLE error :"+e.getLocalizedMessage());
            e.printStackTrace(); }

        return false;

    }

    //Hides Keyboard
    public static void hideKeyPad(Context context, EditText editField) {
        final InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editField.getWindowToken(), 0);
    }


    //Loads the Image Filepath
    private static byte[] loadFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        long length = file.length();
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }
        byte[] bytes = new byte[(int) length];

        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }

        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }

        is.close();
        return bytes;
    }

    //Converts image in filepath to base64
    public static String encodeFileToBase64Binary(File file)
            throws IOException {

        byte[] bytes = loadFile(file);

        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

    //To get Current Date & time
    public static String getcurrentDateTime(final String format) {
        Calendar c = Calendar.getInstance();
        String date;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat Objdateformat = new SimpleDateFormat(format);
        date = Objdateformat.format(c.getTime());
        return date;
    }

    //We are calling this method to check the permission status
    public static boolean isPermissionAllowed(final Context context, final String permission) {
        //Getting the permission status
        int result = ContextCompat.checkSelfPermission(context, permission);

        //If permission is granted returning true
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;

        //If permission is not granted returning false
        return false;
    }

    //To check permissions are allowed or not
    public static boolean areAllPermissionsAllowedNew(final Context context, final String[] permissions) {
        boolean isAllPermissionsGranted = true;
        for (String permission : permissions) {
            int result = ContextCompat.checkSelfPermission(context, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                return   isAllPermissionsGranted = false;
            }
        }
        return isAllPermissionsGranted;
    }

    //To get Imei number of the device
    public static String   getIMEInumber(final Context context) {
        String deviceId;
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } else {
            TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            deviceId = mTelephony.getDeviceId();
        }

        //return deviceId;
       return "351558072434071";  //myid
     //  return "9426d26947822060";  //myid
       //return "2db226bbc3a23fd4";  //myid
      // return "9fac7f5a32533583";  //UAT user
         //return "351558072968326";
    }

    //To get app version
    public static String getAppVersion(final Context context) {
        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "version error " + e.getMessage());
        }
        return pInfo.versionName;
    }

    //To map the Key and Values
    public static LinkedHashMap<String, Object> toMap(JSONObject object) throws JSONException {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();

        Iterator<String> keysItr = object.keys();
        while (keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    //Converts JsonArray to List
    public static List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }


    //DB File Root Path
    public static String getFileRootPath() {
        String root = Environment.getExternalStorageDirectory().toString();
        File rootDirectory = new File(root + "/Palm_Grading");
        if (!rootDirectory.exists()) {
            rootDirectory.mkdirs();
        }
        return rootDirectory.getAbsolutePath() + File.separator;
    }

    public static final TimeZone UTC_TIME_ZONE = TimeZone.getTimeZone("UTC");

    private static final ThreadLocal<DateFormat> ISO_8601_FORMAT = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            df.setTimeZone(UTC_TIME_ZONE);
            return df;
        }
    };

    private static final ThreadLocal<DateFormat> ISO_8601_1_FORMAT = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            df.setTimeZone(UTC_TIME_ZONE);
            return df;
        }
    };

    private static final ThreadLocal<DateFormat> ISO_8601_FORMAT_2 = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            return df;
        }
    };

    public static void copyFile(final Context context) {
        try {
            String dataDir = context.getApplicationInfo().dataDir;

            final String dbfile = "/sdcard/3f_" + CommonConstants.TAB_ID + "_" + System.nanoTime();
            Log.e(LOG_TAG,"============>dbfile"+ dbfile);

            File dir = new File(dataDir + "/databases");
            Log.e(LOG_TAG,"============>dbfiless"+ dir.length());
            Log.e(LOG_TAG,"============>dbfiless1111"+ dir.exists());

            for (File file : dir.listFiles()) {
                if (file.isFile() && file.getName().equals("3foilpalm.sqlite")) {
                    try {
                        copy(file, new File(dbfile));
                    } catch (Exception e) {
                        android.util.Log.e(LOG_TAG, "", e);
                    }
                    break;
                }
            }
        } catch (Exception e) {
            android.util.Log.w("Settings Backup", e);
        }

    }

    public static void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    public static void showToast(final String message, final Context context) {
        ApplicationThread.uiPost(LOG_TAG, "toast", new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void gzipFile(File sourceFile, String destinaton_zip_filepath, final ApplicationThread.OnComplete<String> onComplete) {

        byte[] buffer = new byte[1024];

        try {

            FileOutputStream fileOutputStream = new FileOutputStream(destinaton_zip_filepath);

            GZIPOutputStream gzipOuputStream = new GZIPOutputStream(fileOutputStream);

            FileInputStream fileInput = new FileInputStream(sourceFile);

            int bytes_read;

            while ((bytes_read = fileInput.read(buffer)) > 0) {
                gzipOuputStream.write(buffer, 0, bytes_read);
            }

            fileInput.close();

            gzipOuputStream.finish();
            gzipOuputStream.close();

            System.out.println("The file was compressed successfully!");
            onComplete.execute(true, "success", "success");
        } catch (IOException ex) {
            ex.printStackTrace();
            onComplete.execute(false, "failed", "failed");
        }
    }

    public static String serialNumber(int number, int stringLength) {
        int numberOfDigits = String.valueOf(number).length();
        int numberOfLeadingZeroes = stringLength - numberOfDigits;
        StringBuilder sb = new StringBuilder();
        if (numberOfLeadingZeroes > 0) {
            for (int i = 0; i < numberOfLeadingZeroes; i++) {
                sb.append("0");
            }
        }
        sb.append(number);
        return sb.toString();
    }

    //Maps Data
    public static String[] fromMap(LinkedHashMap<String, String> inputMap, String type) {
        Collection c = inputMap.values();
        Iterator itr = c.iterator();
        int size = inputMap.size() + 1;
        String[] toMap = new String[size];
       toMap[0] = "-- Select " + type + " --";
        int iCount = 1;
        while (iCount < size && itr.hasNext()) {
            toMap[iCount] = itr.next().toString();
            iCount++;
        }
        while (iCount < size && itr.hasNext()) {
            toMap[iCount] = itr.next().toString();
            iCount++;
        }
        return toMap;
    }
    public static String[] fromMap1(LinkedHashMap<String, String> inputMap) {
        Collection<String> values = inputMap.values();
        Iterator<String> itr = values.iterator();
        int size = inputMap.size();
        String[] toMap = new String[size];

        int iCount = 0;
        while (iCount < size && itr.hasNext()) {
            toMap[iCount] = itr.next().toString();
            iCount++;
        }

        return toMap;
    }


}
