package com.palm360.palmgrading.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;

import com.google.zxing.Result;
import com.palm360.palmgrading.R;

import me.dm7.barcodescanner.zxing.ZXingScannerView;


//To Scan the QR Code and handle the result
public class QRScanActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    ZXingScannerView scannerView;
    String Activityfrom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscan);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("QR Scan");
        setSupportActionBar(toolbar);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            try {
                Activityfrom = extras.getString("ActivityName");


                Log.e("=========>SCREEN_FROM", Activityfrom + "");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        scannerView = new ZXingScannerView(this);
        contentFrame.addView(scannerView);
    }

    @Override
    public void handleResult(Result result) {

        Log.e("result.getText()",result.getText()+"");
        if(Activityfrom.equalsIgnoreCase("GatepassinActivity")){
            if (!TextUtils.isEmpty(result.getText())){
                Intent gradingintent = new Intent(QRScanActivity.this, GatepassinActivity.class);
                gradingintent.putExtra("qrvalue", result.getText() + "");
                startActivity(gradingintent);
            }else{
                Log.d("QRCode Scan", "Failed");
            }
        }else  if(Activityfrom.equalsIgnoreCase("GatepassoutActivity")){
            if (!TextUtils.isEmpty(result.getText())){
                Intent gradingintent = new Intent(QRScanActivity.this, GatepassoutActivity.class);
                gradingintent.putExtra("qrvalue", result.getText() + "");
                startActivity(gradingintent);
            }else{
                Log.d("QRCode Scan", "Failed");
            }
        }
        else  if(Activityfrom.equalsIgnoreCase("GatepasstokenActivity")){
            if (!TextUtils.isEmpty(result.getText())){
//                Intent gradingintent = new Intent(QRScanActivity.this, GatepasstokenActivity.class);
//                gradingintent.putExtra("qrvalue", result.getText() + "");
//                startActivity(gradingintent);
                Intent resultIntent = new Intent();
                resultIntent.putExtra("qrvalue", result.getText());
                setResult(RESULT_OK, resultIntent);
                finish();
            }else{
                Log.d("QRCode Scan", "Failed");
            }
        }
        else{
            if (!TextUtils.isEmpty(result.getText())){
                Intent gradingintent = new Intent(QRScanActivity.this, GradingActivity.class);
                gradingintent.putExtra("qrvalue", result.getText() + "");
                startActivity(gradingintent);
            }else{
                Log.d("QRCode Scan", "Failed");
            }
        }



    }

    @Override
    protected void onResume() {
        super.onResume();
        scannerView.setResultHandler(QRScanActivity.this);
        scannerView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }
}