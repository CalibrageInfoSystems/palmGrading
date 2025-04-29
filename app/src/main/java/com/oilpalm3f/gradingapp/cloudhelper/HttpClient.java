package com.oilpalm3f.gradingapp.cloudhelper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.AndroidHttpClient;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;

import com.oilpalm3f.gradingapp.common.CommonConstants;
import com.oilpalm3f.gradingapp.common.Connectivity;
import com.oilpalm3f.gradingapp.common.OilPalmException;
import com.oilpalm3f.gradingapp.database.Palm3FoilDatabase;
import com.oilpalm3f.gradingapp.utils.UiUtils;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/*
 * Class for communicating with the sever.
 */

public class HttpClient {

    public static final String OFFLINE = "3";
    private static final String LOG_TAG = HttpClient.class.getName();
    @SuppressLint("ConstantLocale")
    private static final String HTTP_ACCEPT_LANGUAGE = Locale.getDefault().getLanguage() + "_" + Locale.getDefault().getCountry();
    private static final int CONNECTION_TIMEOUT = 30000; // 3 seconds
    private static final MediaType TEXT_PLAIN = MediaType.parse("application/x-www-form-urlencoded");
    private static MediaType TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    public static boolean offline = false;
    public static String HTTP_USER_AGENT = "app";
    public static String response = null;
    private static OkHttpClient client;


    public static Request.Builder buildRequest(String url, String method, RequestBody body) {
        String encodedUrl = encodeURL(url);
        Log.d(LOG_TAG, "######## url: " + encodedUrl);
        URL requestUrl = null;
        try {
            requestUrl = new URL(encodedUrl);
        } catch (Exception ignored) {
        }

        Request.Builder requestBuilder = new Request.Builder()
                .url(requestUrl)
                .method(method, body)
                .header("User-Agent", HTTP_USER_AGENT);

        return requestBuilder;
    }

    public static String encodeURL(String url) {
        if (url == null) return null;
        try {
            return (new URI(url.trim().replaceAll(" ", "%20"))).toASCIIString();
        } catch (Exception e) {
            Log.e(LOG_TAG, LOG_TAG, e);
            return url.trim().replaceAll(" ", "%20");
        }
    }

    public static OkHttpClient getOkHttpClient() {
        if (client == null) {
            // 50 seconds
            int CONNECTION_TIMEOUT_MILLIS = 3*60;
            client = new OkHttpClient.Builder()
                    .connectTimeout(CONNECTION_TIMEOUT_MILLIS, TimeUnit.SECONDS)
                    .writeTimeout(CONNECTION_TIMEOUT_MILLIS, TimeUnit.SECONDS)
                    .readTimeout(CONNECTION_TIMEOUT_MILLIS, TimeUnit.SECONDS)
                    .build();
        }

        return client;
    }

    public static void get(final String url, final ApplicationThread.OnComplete<String> onComplete) {
        Response response = null;
        if (offline) {
            if (null != onComplete) onComplete.execute(false, null, "not connected");
            return;
        }
        Request request = buildRequest(url, "GET", null).build();
        try {
            OkHttpClient client = getOkHttpClient();
            response = client.newCall(request).execute();
            int statusCode = response.code();
            if (statusCode >= HttpURLConnection.HTTP_BAD_REQUEST) {
                onComplete.execute(false, statusCode + "|" + response.message() + "|" + request.url(), "Error " + statusCode + " while retrieving data from " + request.url() + "\nreason phrase: " + response.message());
                response.body().close();
                if (null != onComplete) onComplete.execute(false, null, response.message());
                return;
            }
            ResponseBody responseBody = response.body();
            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE); // request the entire body.
            Buffer buffer = source.buffer();

// clone buffer before reading from it
            String responseBodyString = buffer.clone().readString(Charset.forName("UTF-8"));
            Log.d("TAG", responseBodyString);
            if (null != onComplete) onComplete.execute(true, responseBodyString, null);
            Log.d(LOG_TAG, " ############# GET RESPONSE ################ (" + statusCode + ")\n\n" + responseBodyString + "\n\n");

        } catch (Exception e) {
            Log.e(LOG_TAG, "accessing: (" + request.url() + ")", e);
            if (null != onComplete) onComplete.execute(false, null, e.getMessage());
        } finally {
            if (null != response) {
                response.body().close();
            }
        }
    }
    public static synchronized void postDataToServerjson(final Context context, final String url, final JSONObject jsonObject, final ApplicationThread.OnComplete onComplete) {
        // check the connectivity mode

        if (offline) {
            if (null != onComplete) onComplete.execute(false, null, "not connected");
            return;
        }

        Palm3FoilDatabase palm3FoilDatabase = Palm3FoilDatabase.getPalm3FoilDatabase(context);
        final AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
        final HttpPost post = new org.apache.http.client.methods.HttpPost(url);

        // Set timeout values in milliseconds (adjust as needed)
        int timeoutConnection = 30000; // 30 seconds
        int timeoutSocket = 30000; // 30 seconds

        HttpParams httpParams = client.getParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, timeoutConnection);
        HttpConnectionParams.setSoTimeout(httpParams, timeoutSocket);

        try {
            if (jsonObject != null) {
                Log.i("Data...", "@@@ " + jsonObject.toString());
                StringEntity entity = new StringEntity(jsonObject.toString(), "UTF-8");
                // sets the post request as the resulting string
                entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                post.setEntity(entity);
                post.setHeader("Accept", "application/json");
                post.setHeader("Content-type", "application/json");

                HttpClientParams.setRedirecting(httpParams, true);

                       final HttpResponse response = client.execute(post);
                final int statusCode = response.getStatusLine().getStatusCode();

                if (statusCode == HttpStatus.SC_OK) {
                    final String postResponse = org.apache.http.util.EntityUtils.toString(response.getEntity(), "UTF-8");
                    Log.d(HttpClient.class.getName(), "\n\npost response: \n" + postResponse);
                    Log.v("@@postResponse", "" + postResponse);
                    if (null != onComplete) onComplete.execute(true, postResponse, postResponse);
                } else {
                    final String postResponse = EntityUtils.toString(response.getEntity(), "UTF-8");

                    // palm3FoilDatabase.insertErrorLogs(CommonConstants.SyncTableName,postResponse);
                    Log.pushLogToCrashlytics(url + "\n" + jsonObject.toString());
                    Log.pushLogToCrashlytics(postResponse);
                    Log.pushExceptionToCrashlytics(new OilPalmException(postResponse));
                    if (null != onComplete) onComplete.execute(false, postResponse, postResponse);
                }
            }

        } catch (SocketTimeoutException e) {
            // Handle SocketTimeoutException
            e.printStackTrace();
            Log.e(HttpClient.class.getName(), "Socket Timeout", e);
            if (null != onComplete) onComplete.execute(false, null, "Socket Timeout: " + e.getMessage());
        } catch (Exception e) {
            // Handle other exceptions
            e.printStackTrace();
            Log.e(HttpClient.class.getName(), e);
            post.abort();
            if (null != onComplete) onComplete.execute(false, null, e.getMessage());
        } finally {
            client.close();
        }
    }


//    public static synchronized void  postDataToServerjson(final Context context,final String url, final JSONObject jsonObject, final ApplicationThread.OnComplete onComplete) {
//        // check the connectivity mode
//
//        if (offline) {
//            if (null != onComplete) onComplete.execute(false, null, "not connected");
//            return;
//        }
//        Log.i("Jurl...", url);
//        Palm3FoilDatabase  palm3FoilDatabase = Palm3FoilDatabase.getPalm3FoilDatabase(context);
//        final AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
//        final HttpPost post = new org.apache.http.client.methods.HttpPost(url);
//        try {
//            if( jsonObject != null) {
//                Log.i("Data...", "@@@ "+jsonObject.toString());
//                StringEntity entity = new StringEntity(jsonObject.toString(), "UTF-8");
//                //sets the post request as the resulting string
//                entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
//                post.setEntity(entity);
//                post.setHeader("Accept", "application/json");
//                post.setHeader("Content-type", "application/json");
////                Log.i("Json Data to server", jsonObject.toString());
//
//
//                HttpClientParams.setRedirecting(client.getParams(), true);
//
//                final HttpResponse response = client.execute(post);
//                final int statusCode = response.getStatusLine().getStatusCode();
//
//                if (statusCode == HttpStatus.SC_OK) {
//                    final String postResponse = org.apache.http.util.EntityUtils.toString(response.getEntity(), "UTF-8");
//                    Log.d(HttpClient.class.getName(), "\n\npost response: \n" + postResponse);
//                    Log.v("@@postResponse",""+postResponse);
//                    if (null != onComplete) onComplete.execute(true, postResponse, postResponse);
//                } else {
//                    final String postResponse = EntityUtils.toString(response.getEntity(), "UTF-8");
//
//                    palm3FoilDatabase.insertErrorLogs(CommonConstants.SyncTableName,postResponse);
//                    Log.pushLogToCrashlytics(url+"\n"+jsonObject.toString());
//                    Log.pushLogToCrashlytics(postResponse);
//                    Log.pushExceptionToCrashlytics(new OilPalmException(postResponse));
//                    if (null != onComplete) onComplete.execute(false, postResponse, postResponse);
//
//
//                }
//            }
//            // enable redirects
//
//        } catch(Exception e) {
//
//            e.printStackTrace();
//            Log.e(HttpClient.class.getName(), e);
//            post.abort();
//
//            if (null != onComplete) onComplete.execute(false, null, e.getMessage());
//        } finally {
//            client.close();
//        }
//    }
//

    public static class myPhoneStateListener extends PhoneStateListener {
        public int signalStrengthValue;

        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            if (signalStrength.isGsm()) {
                if (signalStrength.getGsmSignalStrength() != 99)
                    signalStrengthValue = signalStrength.getGsmSignalStrength() * 2- 113;
                else
                    signalStrengthValue = signalStrength.getGsmSignalStrength();
            } else {
                signalStrengthValue = signalStrength.getCdmaDbm();
            }
            Log.d("SignalStrength", signalStrengthValue + "");
        }
    }

//    public static synchronized void  postDataToServerjsonn(final Context context, final String url, final JSONArray jsonObject, final ApplicationThread.OnComplete onComplete) {
//        // check the connectivity mode
//        int signalSupport = 0;
//        String signalSupportString = "";
//
//        if (offline) {
//            if (null != onComplete) onComplete.execute(false, null, "not connected");
//            return;
//        }
//        Log.i("Jurl...", url);
//        Palm3FoilDatabase  palm3FoilDatabase = Palm3FoilDatabase.getPalm3FoilDatabase(context);
//
//        boolean wifiAvailable = false;
//        boolean mobileAvailable = false;
//        ConnectivityManager conManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo[] networkInfo = conManager.getAllNetworkInfo();
//        for (NetworkInfo netInfo : networkInfo) {
//            if (netInfo.getTypeName().equalsIgnoreCase("WIFI"))
//                if (netInfo.isConnected())
//                    wifiAvailable = true;
//            if (netInfo.getTypeName().equalsIgnoreCase("MOBILE"))
//                if (netInfo.isConnected())
//                    mobileAvailable = true;
//        }
//
//        Log.d("wifiAvailable", wifiAvailable + "");
//        Log.d("mobileAvailable", mobileAvailable + "");
//
//
//        final AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
//
//        HttpConnectionParams.setConnectionTimeout(client.getParams(), 3000);
//        HttpConnectionParams.setSoTimeout(client.getParams(), 3000);
//
//        final HttpPost post = new HttpPost(url);
//
//        if (mobileAvailable == true){
//
//            TelephonyManager telephonyManager =        (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
//            CellInfoLte cellinfogsm = (CellInfoLte)telephonyManager.getAllCellInfo().get(0);
//            CellSignalStrengthLte cellSignalStrengthGsm = cellinfogsm.getCellSignalStrength();
//            cellSignalStrengthGsm.getDbm();
//            signalSupport = cellSignalStrengthGsm.getDbm();
//
//
//            Log.d("cellSignalStrengthGsm", cellSignalStrengthGsm.getDbm() + "");
//            Log.d("cellSignalStrengthGsmm", cellinfogsm.getCellSignalStrength() + "");
//
//            signalSupportString = String.valueOf(signalSupport);
//
//            Log.d("signalSupport", "Signal Strength is" +signalSupport + "");
//            Log.d("signalSupportString", signalSupportString + "");
//
//            if (signalSupport <= -50 && signalSupport >= -89){
//
//                Log.d("Strengh", "whatisthestrength");
//                try {
//                    if( jsonObject != null) {
//                        Log.i("Data...", "@@@ "+jsonObject.toString());
//                        StringEntity entity = new StringEntity(jsonObject.toString(), "UTF-8");
//                        //sets the post request as the resulting string
//                        entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
//                        post.setEntity(entity);
//                        post.setHeader("Accept", "application/json");
//                        post.setHeader("Content-type", "application/json");
////                Log.i("Json Data to server", jsonObject.toString());
//
//
//                        HttpClientParams.setRedirecting(client.getParams(), true);
//
//                        final HttpResponse response = client.execute(post);
//                        final int statusCode = response.getStatusLine().getStatusCode();
//
//                        if (statusCode == HttpStatus.SC_OK) {
//                            final String postResponse = org.apache.http.util.EntityUtils.toString(response.getEntity(), "UTF-8");
//                            Log.d(HttpClient.class.getName(), "\n\npost response: \n" + postResponse);
//                            Log.v("@@postResponse",""+postResponse);
//                            if (null != onComplete) onComplete.execute(true, postResponse, postResponse);
//                        }
//                        else {
//                            final String postResponse = EntityUtils.toString(response.getEntity(), "UTF-8");
//                            // palm3FoilDatabase.insertErrorLogs(CommonConstants.SyncTableName,postResponse);
//                            Log.pushLogToCrashlytics(url+"\n"+jsonObject.toString());
//                            Log.pushLogToCrashlytics(postResponse);
//                            Log.pushExceptionToCrashlytics(new OilPalmException(postResponse));
//                            if (null != onComplete) onComplete.execute(false, postResponse, postResponse);
//                        }
//                    }
//
//                    //client.getConnectionManager().shutdown();
//                    // enable redirects
//
//                } catch(Exception e) {
//
//                    e.printStackTrace();
//                    Log.e(HttpClient.class.getName(), e);
//                    post.abort();
//
//                    if (null != onComplete) onComplete.execute(false, null, e.getMessage());
//                    UiUtils.showCustomToastMessage("Network Error", context, 1);
//                } finally {
//                    client.close();
//                }
//
//            }else{
//                if (null != onComplete) onComplete.execute(false, null, "Network Error");
//                UiUtils.showCustomToastMessage("Network Error", context, 1);
//            }
//
//        }else{
//            try {
//                if( jsonObject != null) {
//                    Log.i("Data...", "@@@ "+jsonObject.toString());
//                    StringEntity entity = new StringEntity(jsonObject.toString(), "UTF-8");
//                    //sets the post request as the resulting string
//                    entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
//                    post.setEntity(entity);
//                    post.setHeader("Accept", "application/json");
//                    post.setHeader("Content-type", "application/json");
////                Log.i("Json Data to server", jsonObject.toString());
//
//
//                    HttpClientParams.setRedirecting(client.getParams(), true);
//
//                    final HttpResponse response = client.execute(post);
//                    final int statusCode = response.getStatusLine().getStatusCode();
//
//                    if (statusCode == HttpStatus.SC_OK) {
//                        final String postResponse = org.apache.http.util.EntityUtils.toString(response.getEntity(), "UTF-8");
//                        Log.d(HttpClient.class.getName(), "\n\npost response: \n" + postResponse);
//                        Log.v("@@postResponse",""+postResponse);
//                        if (null != onComplete) onComplete.execute(true, postResponse, postResponse);
//                    }
//                    else {
//                        final String postResponse = EntityUtils.toString(response.getEntity(), "UTF-8");
//                        // palm3FoilDatabase.insertErrorLogs(CommonConstants.SyncTableName,postResponse);
//                        Log.pushLogToCrashlytics(url+"\n"+jsonObject.toString());
//                        Log.pushLogToCrashlytics(postResponse);
//                        Log.pushExceptionToCrashlytics(new OilPalmException(postResponse));
//                        if (null != onComplete) onComplete.execute(false, postResponse, postResponse);
//                    }
//                }
//
//                //client.getConnectionManager().shutdown();
//                // enable redirects
//
//            } catch(Exception e) {
//
//                e.printStackTrace();
//                Log.e(HttpClient.class.getName(), e);
//                post.abort();
//
//                UiUtils.showCustomToastMessage("Network Error", context, 1);
//                if (null != onComplete) onComplete.execute(false, null, e.getMessage());
//            } finally {
//                client.close();
//            }
//
//        }
//
//    }

    public static synchronized void  postDataToServerjsonn(final Context context, final String url, final JSONArray jsonObject, final ApplicationThread.OnComplete onComplete) {

        // check the connectivity mode
        if (offline) {
            if (null != onComplete) onComplete.execute(false, null, "not connected");
            return;
        }
        Log.i("Jurl...", url);

        final AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
        HttpConnectionParams.setConnectionTimeout(client.getParams(), 3000);
        HttpConnectionParams.setSoTimeout(client.getParams(), 8000);
        final HttpPost post = new HttpPost(url);
        try {
            // Not running on main thread so can use AndroidHttpClient.newInstance

            if( jsonObject != null) {
                Log.i("Data...", "@@@ "+jsonObject.toString());
                StringEntity entity = new StringEntity(jsonObject.toString(), "UTF-8");
                //sets the post request as the resulting string
                entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                post.setEntity(entity);
                post.setHeader("Accept", "application/json");
                post.setHeader("Content-type", "application/json");
//                Log.i("Json Data to server", jsonObject.toString());

                HttpClientParams.setRedirecting(client.getParams(), true);

                final HttpResponse response = client.execute(post);

                final int statusCode = response.getStatusLine().getStatusCode();

                if (statusCode == HttpStatus.SC_OK) {
                    final String postResponse = EntityUtils.toString(response.getEntity(), "UTF-8");
                    Log.pushLogToCrashlytics(url+"\n"+jsonObject.toString());
                    Log.pushLogToCrashlytics(postResponse);
                    Log.pushExceptionToCrashlytics(new OilPalmException(postResponse));
                    Log.d(HttpClient.class.getName(), "\n\npost response: \n" + postResponse);
                    if (null != onComplete) onComplete.execute(true, postResponse, null);
                } else {
                    final String postResponse = EntityUtils.toString(response.getEntity(), "UTF-8");
                    Log.pushLogToCrashlytics(url+"\n"+jsonObject.toString());
                    Log.pushLogToCrashlytics(postResponse);
                    Log.pushExceptionToCrashlytics(new OilPalmException(jsonObject.toString()+"\n"+postResponse));
                    Log.d(HttpClient.class.getName(), "\n\npost response failed: \n" + postResponse);
                    if (null != onComplete) onComplete.execute(false, postResponse, postResponse);
                }

            }
            else {
                if (null != onComplete) onComplete.execute(false, "Empty Data set", "Empty Data set");
            }

        } catch(Exception e) {
            e.printStackTrace();
            Log.e(HttpClient.class.getName(), e);
            post.abort();

            if (null != onComplete) onComplete.execute(false, null, e.getMessage());
        } finally {
            client.close();
        }
    }


    public static void post(String url, Map<String, Object> values,
                            ApplicationThread.OnComplete<String> onComplete) {
        Response response = null;
        if (offline) {
            if (null != onComplete) onComplete.execute(false, null, "not connected");
            return;
        }
        try {
            RequestBody requestBody = null;
            if (values != null) {
                final StringBuilder sb = new StringBuilder();
                boolean first = true;
                for (Map.Entry<String, Object> entry : values.entrySet()) {
                    if (first) first = false;
                    else sb.append("&");

                    sb.append(URLEncoder.encode(entry.getKey(), HTTP.UTF_8)).append("=")
                            .append(URLEncoder.encode(entry.getValue().toString(), HTTP.UTF_8));

                    Log.d(LOG_TAG, "\nposting key: " + entry.getKey() + " -- value: " + entry.getValue());
                }
                requestBody = RequestBody.create(TEXT_PLAIN, sb.toString());
            }
            Request request = buildRequest(url, "POST", (requestBody != null) ? requestBody : RequestBody.create(TEXT_PLAIN, "")).build();
            OkHttpClient client = getOkHttpClient();
            response = client.newCall(request).execute();
            int statusCode = response.code();
            if (statusCode >= HttpURLConnection.HTTP_BAD_REQUEST) {
                Log.d(LOG_TAG, " ############# POST RESPONSE ################ (" + statusCode + ")\n\nError " + statusCode + "\nreason phrase: " + response.message());
                if (null != onComplete)
                    onComplete.execute(false, statusCode + "|" + response.message() + "|" + request.url(), "Error " + statusCode + " while retrieving data from " + request.url() + "\nreason phrase: " + response.message());
                response.body().close();
                return;
            }
            final String strResponse = response.body().string();
            Log.d(LOG_TAG, " ############# POST RESPONSE ################ (" + statusCode + ")\n\n" + strResponse + "\n\n");

            if (null != onComplete) {
                if (HttpURLConnection.HTTP_NO_CONTENT == statusCode) {
                    onComplete.execute(true, null, null);
                } else {
                    onComplete.execute(true, strResponse, null);
                    response.body().close();
                }
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "accessing: (" + url + ")", e);
            if (null != onComplete) onComplete.execute(false, null, e.getMessage());
        } finally {
            if (null != response) {
                response.body().close();
            }
        }
    }
}
