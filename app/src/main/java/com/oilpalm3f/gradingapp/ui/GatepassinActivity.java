package com.oilpalm3f.gradingapp.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.print.sdk.Barcode;
import com.android.print.sdk.PrinterConstants;
import com.android.print.sdk.PrinterInstance;
import com.oilpalm3f.gradingapp.MainActivity;
import com.oilpalm3f.gradingapp.R;
import com.oilpalm3f.gradingapp.cloudhelper.ApplicationThread;
import com.oilpalm3f.gradingapp.common.CommonConstants;
import com.oilpalm3f.gradingapp.common.CommonUtils;
import com.oilpalm3f.gradingapp.database.DataAccessHandler;
import com.oilpalm3f.gradingapp.database.Queries;
import com.oilpalm3f.gradingapp.datasync.helpers.DataSyncHelper;
import com.oilpalm3f.gradingapp.dbmodels.Test;
import com.oilpalm3f.gradingapp.printer.BluetoothDevicesFragment;
import com.oilpalm3f.gradingapp.printer.PrinterChooserFragment;
import com.oilpalm3f.gradingapp.printer.UsbDevicesListFragment;
import com.oilpalm3f.gradingapp.printer.onPrinterType;
import com.oilpalm3f.gradingapp.utils.UiUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

public class GatepassinActivity extends AppCompatActivity implements BluetoothDevicesFragment.onDeviceSelected, onPrinterType, UsbDevicesListFragment.onUsbDeviceSelected  {
    private static final String LOG_TAG = GatepassinActivity.class.getName();
    String qrvalue;
    String[] splitString;
    private DataAccessHandler dataAccessHandler;
    Spinner Weighbridge_spinner,vehicletype_spinner,vehiclecategory_spinner;

    boolean selectedfruittype;
    Button submit;
    String GatePassSerialNumber;
    String fruitType;
    String GatePassCode;
    String currentDateTime, currentdateandtimeforprint, currentdateandtimeforcreate;
    private LinkedHashMap<String, String> VehicleTypeMap, VehicleCategoryTypeMap,WeighbridgeIMap;
    private String vehicleCategoryCode, vehicleCategoryType;
    private String vehicleTypeCode, vehicleTypeName, WeighbridgeId, WeighbridgeCode, WeighbridgeName;
    int tokenexists;
    String locationcodeassigned;

    //private String WeighbridgeCode, WeighbridgeName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gatepassin);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Gate Pass-In");
        setSupportActionBar(toolbar);
        intviews();
        Setviews();

    }

    private void intviews() {
        dataAccessHandler = new DataAccessHandler(GatepassinActivity.this);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            qrvalue = extras.getString("qrvalue");
            Log.d("GatePassInQRValue", qrvalue + "");
        }
        Weighbridge_spinner = findViewById(R.id.Weighbridge_spinner);
        vehicletype_spinner = findViewById(R.id.vehicletypespinner_spinner);
        vehiclecategory_spinner= findViewById(R.id.vehiclecategory_spinner);
        submit = findViewById(R.id.gatepasstokensubmit);
    }
    private void Setviews() {

        splitString = qrvalue.split("/");
        Log.d("splitString",splitString.length + "");

        if (splitString.length == 5 && splitString[0].length() == 25) {

        Log.d("Length", splitString.length + "");

        Log.d("String1", splitString[0] + "");
        Log.d("String2", splitString[1] + "");
        Log.d("String3", splitString[2] + "");
        Log.d("String4", splitString[3] + "");
        Log.d("String5", splitString[4] + "");

        tokenexists = dataAccessHandler.getOnlyOneIntValueFromDb(Queries.getInstance().getgateinTokenExistQuery(splitString[0]));
        Log.d("tokenexists", tokenexists + "");

        if (tokenexists == 1) {
            showDialog(GatepassinActivity.this, "Gate Pass-In token already generated for this Serial Number");
        }

            locationcodeassigned = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().isLocationAssignedQuery(splitString[1]));
            Log.d("locationcodeassigned", locationcodeassigned + "");

            if (locationcodeassigned == null || locationcodeassigned.trim().isEmpty()){
                showDialog(GatepassinActivity.this, "Mill location not assigned to you");
            }
    }
        else{
            showDialog(GatepassinActivity.this, "Invalid Gate Pass-In Token");
        }




        //Binding Data to Vehicle Category & On Item Selected Listener
        WeighbridgeIMap =dataAccessHandler.getvechileData(Queries.getInstance().getVehicleCategoryType());

        VehicleCategoryTypeMap = dataAccessHandler.getvechileData(Queries.getInstance().getVehicleCategoryType());
        ArrayAdapter spinnerArrayAdaptervechilecategory = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                CommonUtils.fromMap(VehicleCategoryTypeMap, "Vehicle Category"));
        spinnerArrayAdaptervechilecategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vehiclecategory_spinner.setAdapter(spinnerArrayAdaptervechilecategory);

        vehiclecategory_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (VehicleCategoryTypeMap != null && VehicleCategoryTypeMap.size() > 0 && vehiclecategory_spinner.getSelectedItemPosition() != 0) {
                    vehicleCategoryCode = VehicleCategoryTypeMap.keySet().toArray(new String[VehicleCategoryTypeMap.size()])[i - 1];
                    vehicleCategoryType = vehiclecategory_spinner.getSelectedItem().toString();
                    android.util.Log.v(LOG_TAG, "@@@ vehicle category code " + vehicleCategoryCode + " category name " + vehicleCategoryType);

                    //Binding Data to Vehicle Type
                    VehicleTypeMap = dataAccessHandler.getvechileData(Queries.getInstance().getVehicleTypeonCategory(vehicleCategoryCode));
                    ArrayAdapter spinnerArrayAdaptervechileType = new ArrayAdapter<>(GatepassinActivity.this, android.R.layout.simple_spinner_item,
                            CommonUtils.fromMap(VehicleTypeMap, "Vehicle"));
                    spinnerArrayAdaptervechileType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    vehicletype_spinner.setAdapter(spinnerArrayAdaptervechileType);

                }
//
//                if (vehiclecategory_spinner.getSelectedItemPosition() == 1) {
//
//                    vehiclenumber_tv.setText("Vehicle Number");
//                } else {
//
//                    vehiclenumber_tv.setText("Vehicle Number *");
//                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Vehicle Type On Item Selected
        vehicletype_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (VehicleTypeMap != null && VehicleTypeMap.size() > 0 && vehicletype_spinner.getSelectedItemPosition() != 0) {
                    vehicleTypeCode = VehicleTypeMap.keySet().toArray(new String[VehicleTypeMap.size()])[i - 1];
                    vehicleTypeName = vehicletype_spinner.getSelectedItem().toString();
                    android.util.Log.v(LOG_TAG, "@@@ vehicle Type code " + vehicleTypeCode + " vehicle Type name " + vehicleTypeName);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        WeighbridgeIMap =dataAccessHandler.getvechileData(Queries.getInstance().getWeighbridgeDetails());
        ArrayAdapter spinnerArrayAdapter= new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                CommonUtils.fromMap1(WeighbridgeIMap));
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Weighbridge_spinner.setEnabled(false);
        Weighbridge_spinner.setClickable(false);
        Weighbridge_spinner.setAdapter(spinnerArrayAdapter);


        Log.d("WeighbridgeSize", WeighbridgeIMap.size() + "");

        if (WeighbridgeIMap.size() == 0){

            Toast.makeText(this, "No Weighbridge Assigned", Toast.LENGTH_SHORT).show();
            finish();

        }else{
            int randomIndex = new Random().nextInt(WeighbridgeIMap.size());
            Weighbridge_spinner.setSelection(randomIndex);
        }

//        int randomIndex = new Random().nextInt(WeighbridgeIMap.size());
//        Weighbridge_spinner.setSelection(randomIndex);

        Weighbridge_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                WeighbridgeId = WeighbridgeIMap.keySet().toArray(new String[WeighbridgeIMap.size()])[position];
                WeighbridgeName = Weighbridge_spinner.getSelectedItem().toString();
                WeighbridgeCode = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getgetWeighbridgeCode(Integer.parseInt(WeighbridgeId)));

                String selectedName = parent.getSelectedItem().toString();
                //Toast.makeText(GatepassinActivity.this, selectedName, Toast.LENGTH_SHORT).show();

                android.util.Log.v(LOG_TAG, "@@@ WeighbridgeName " + WeighbridgeName + " WeighbridgeId " + WeighbridgeId);
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (validation()){
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                    currentDateTime = sdf.format(new Date());

                    SimpleDateFormat sdf1 = new SimpleDateFormat(CommonConstants.DATE_FORMAT_5);
                    currentdateandtimeforprint = sdf1.format(new Date());

                    Log.d("currentDateTime", currentDateTime + "");
                    Log.d("dateandtimeforprint", currentdateandtimeforprint + "");
                    GatePassCode = currentDateTime+CommonConstants.TAB_ID+splitString[1]+splitString[2] +"/" + splitString[2] +"/" +splitString[3] +"/"+ splitString[4] +"/"+vehicleCategoryCode+ "/"+vehicleTypeCode+"/"+WeighbridgeId;
                    Log.d("GatePassCode", GatePassCode + "");
//                    enablePrintBtn(false);
//                    submit.setAlpha(0.5f);
                    FragmentManager fm = getSupportFragmentManager();
                    PrinterChooserFragment printerChooserFragment = new PrinterChooserFragment();
                    printerChooserFragment.setPrinterType(GatepassinActivity.this);
                    printerChooserFragment.show(fm, "bluetooth fragment");
                    //savegatepassindetails();
                }
            }
        });
        
    }

    public void showDialog(Activity activity, String msg) {
        final Dialog dialog = new Dialog(activity, R.style.DialogSlideAnim);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog);
        final ImageView img = dialog.findViewById(R.id.img_cross);

        TextView text = (TextView) dialog.findViewById(R.id.text_dialog);
        text.setText(msg);
        Button dialogButton = (Button) dialog.findViewById(R.id.btn_dialog);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });
        dialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ((Animatable) img.getDrawable()).start();
            }
        }, 500);
    }

    private void savegatepassindetails() {

        List<LinkedHashMap> details = new ArrayList<>();
        LinkedHashMap map = new LinkedHashMap();


        String inputDate = currentdateandtimeforprint;
        currentdateandtimeforcreate = convertDateFormat(inputDate);

        Log.d("splitStringFruitType",  splitString[3]);

        int fruitType = 0;

        if ("true".contains(splitString[3])) {
            fruitType = 0;
        } else {
            fruitType = 1;
        }


        String gptvalue = splitString[0];
        Log.d("gpcvalue", gptvalue + "");
        String lastFourChars = gptvalue.substring(gptvalue.length() - 4);
        Log.d("lastFourChars", lastFourChars + "");


        map.put("GatePassCode",currentDateTime +CommonConstants.TAB_ID+ splitString[1] + lastFourChars);
        map.put("GatePassTokenCode", splitString[0] );
        map.put("WeighbridgeId", WeighbridgeId);
        map.put("VehicleTypeId", vehicleTypeCode);
        map.put("CreatedByUserId", CommonConstants.USER_ID);
        map.put("CreatedDate",currentdateandtimeforcreate);
        map.put("UpdatedByUserId", CommonConstants.USER_ID);
        map.put("UpdatedDate", CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
        map.put("ServerUpdatedStatus", false);
        map.put("MillLocationTypeId ", splitString[1]);
        map.put("SerialNumber", splitString[2]);
        map.put("FruitType", fruitType);
        map.put("VehicleNumber", splitString[4]);


        details.add(map);

        dataAccessHandler.saveData("GatePass", details, new ApplicationThread.OnComplete<String>() {
            @Override
            public void execute(boolean success, String result, String msg) {

                if (success) {
                    Log.d(GradingActivity.class.getSimpleName(), "==>  Analysis ==> TABLE_GatePassToken INSERT COMPLETED");
                    if (CommonUtils.isNetworkAvailable(GatepassinActivity.this)) {
                        DataSyncHelper.performRefreshTransactionsSync(GatepassinActivity.this, new ApplicationThread.OnComplete() {
                            @Override
                            public void execute(boolean success, Object result, String msg) {
                                if (success) {
                                    ApplicationThread.uiPost(LOG_TAG, "transactions sync message", new Runnable() {
                                        @Override
                                        public void run() {
                                            CommonConstants.IsLogin = false;
                                            UiUtils.showCustomToastMessage("Successfully data sent to server", GatepassinActivity.this, 0);
                                            startActivity(new Intent(GatepassinActivity.this, MainActivity.class));
                                        }
                                    });
                                } else {
                                    ApplicationThread.uiPost(LOG_TAG, "transactions sync failed message", new Runnable() {
                                        @Override
                                        public void run() {
                                            UiUtils.showCustomToastMessage("Data sync failed", GatepassinActivity.this, 1);
                                        }
                                    });
                                }
                            }
                        });
                    } else {
                        startActivity(new Intent(GatepassinActivity.this, MainActivity.class));
                    }


                } else {
                    Toast.makeText(GatepassinActivity.this, "Submit Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private boolean validation() {

        if (vehiclecategory_spinner.getSelectedItemPosition() == 0) {
            UiUtils.showCustomToastMessage("Please Select  Vehicle Category", GatepassinActivity.this, 0);
            return false;
        }
        if (vehicletype_spinner.getSelectedItemPosition() == 0) {
            UiUtils.showCustomToastMessage("Please Select  Vehicle Type", GatepassinActivity.this, 0);
            return false;
        }

        return true;
    }
    @Override
    public void selectedDevice(PrinterInstance printerInstance) {
        Log.v(LOG_TAG, "selected address is ");
        if (null != printerInstance) {
            enablePrintBtn(false);
            for (int i = 0; i < 1; i++) {
                printGAtepasstoken(printerInstance, false, i);
            }
        } else {
            UiUtils.showCustomToastMessage("Printing failed", GatepassinActivity.this, 1);
        }
    }


    public void printGAtepasstoken(PrinterInstance mPrinter, boolean isReprint, int printCount) {

//        SpannableString spannableString = new SpannableString(WeighbridgeCode);
//        int startIndex = WeighbridgeCode.indexOf("bold");
//        int endIndex = startIndex + "bold".length();
//        spannableString.setSpan(new StyleSpan(Typeface.BOLD), startIndex, endIndex, SpannableString.SPAN_INCLUSIVE_INCLUSIVE);


        mPrinter.init();
        StringBuilder sb = new StringBuilder();
        mPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER);
        mPrinter.setCharacterMultiple(0, 1);
        mPrinter.printText(" 3F OILPALM PVT LTD " + "\n");
        mPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER);
        mPrinter.setCharacterMultiple(0, 0);
        mPrinter.printText(" Gate Pass-In " + "\n");
        mPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_LEFT);
        mPrinter.setCharacterMultiple(0, 0);
        mPrinter.setLeftMargin(15, 15);
        sb.append("==============================================" + "\n");

        sb.append(" ");
        sb.append(" Vehicle Number : ").append(splitString[4] + "").append("\n");
        sb.append(" ");

        if(splitString[3].equalsIgnoreCase("true")){
            fruitType= "Collection";
        }else{
            fruitType= "Consignment";
        }
        sb.append(" Fruit Type : ").append(fruitType + "").append("\n");
        sb.append(" ");

        sb.append(" Vehicle Category : ").append(vehicleCategoryType+ "").append("\n");
        sb.append(" ");

        sb.append(" Vehicle Type Name : ").append(vehicleTypeName + "").append("\n");
        sb.append(" ");
        sb.append(" Date : ").append(currentdateandtimeforprint + "").append("\n");

        sb.append("  Created By : ").append(CommonConstants.USER_NAME + "").append("\n");

        mPrinter.printText(sb.toString());



//         String hashString = qrvalue+"/"+CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS)+"/"+unripen.getText().toString()+"/"+underripe.getText().toString()+"/"+ripen.getText().toString()
//                 +"/"+overripe.getText().toString()+"/"+diseased.getText().toString()+"/"+emptybunches.getText().toString()+"/"
//                 +longstalk.getText().toString()+"/"+mediumstalk.getText().toString()+"/"+shortstalk.getText().toString()+"/"+
//                 optimum.getText().toString()+"/"+fruitavailable+"/"+fruightweight+"/"+rejectedbunches+
//                 "/"+gradingdoneby.getText().toString();
//         String qrCodeValue = hashString;
        Log.d("GatePassCodeValueis", GatePassCode  + "");
        Barcode barcode = new Barcode(PrinterConstants.BarcodeType.QRCODE, 3, 95, 3, GatePassCode);

        mPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER);
        mPrinter.setCharacterMultiple(0, 1);

        String space = "-----------------------------------------------";
        String tokenNumber  =  "WeighBridge";
        String spaceBuilderr = "\n";

        mPrinter.printText(space);
        mPrinter.printText(spaceBuilderr);
        mPrinter.printText(tokenNumber + " - "+ WeighbridgeName);
        mPrinter.printText(spaceBuilderr);
        mPrinter.printText(space);
        mPrinter.printText(spaceBuilderr);

//        if(CommonConstants.PrinterName.contains("AMIGOS")){
//            Log.d(LOG_TAG,"########### NEW ##############");
//            print_qr_code(mPrinter,GatePassCode);
//        }else{
//            Log.d(LOG_TAG,"########### OLD ##############");
//            mPrinter.printBarCode(barcode);
//        }

        if(CommonConstants.PrinterName.contains("G-8BT3 AMIGOS")){
            Log.d(LOG_TAG,"########### NEW ##############");
            //mPrinter.setPrintModel(false,true,true,false);
            print_qr_codee(mPrinter,GatePassCode);
        }else if (CommonConstants.PrinterName.contains("AMIGOS")){
            print_qr_code(mPrinter,GatePassCode);
        }else{
            Log.d(LOG_TAG,"########### OLD ##############");
            mPrinter.printBarCode(barcode);
        }


        mPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER);
        mPrinter.setCharacterMultiple(0, 1);
        //mPrinter.printText(qrCodeValue);

        String spaceBuilder = "\n" +
                " ";

        mPrinter.printText(spaceBuilder);

        boolean printSuccess = false;
        try {
            mPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);
            printSuccess = true;
        } catch (Exception e) {
            Log.v(LOG_TAG, "@@@ printing failed " + e.getMessage());
            UiUtils.showCustomToastMessage("Printing failes due to " + e.getMessage(), GatepassinActivity.this, 1);
            printSuccess = false;
        } finally {
            if (printSuccess) {
                Toast.makeText(GatepassinActivity.this, "Print Success", Toast.LENGTH_SHORT).show();
                savegatepassindetails();
            }
        }

    }

    //Generate QRCode
    public void print_qr_code(PrinterInstance mPrinter,String qrdata)
    {
        int store_len = qrdata.length() + 3;
        byte store_pL = (byte) (store_len % 256);
        byte store_pH = (byte) (store_len / 256);


        // QR Code: Select the modelc
        //              Hex     1D      28      6B      04      00      31      41      n1(x32)     n2(x00) - size of model
        // set n1 [49 x31, model 1] [50 x32, model 2] [51 x33, micro qr code]
        // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=140
        byte[] modelQR = {(byte)0x1d, (byte)0x28, (byte)0x6b, (byte)0x04, (byte)0x00, (byte)0x31, (byte)0x41, (byte)0x32, (byte)0x00};

        // QR Code: Set the size of module
        // Hex      1D      28      6B      03      00      31      43      n
        // n depends on the printer
        // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=141


        byte[] sizeQR = {(byte)0x1d, (byte)0x28, (byte)0x6b, (byte)0x03, (byte)0x00, (byte)0x31, (byte)0x43, (byte)0x5};


        //          Hex     1D      28      6B      03      00      31      45      n
        // Set n for error correction [48 x30 -> 7%] [49 x31-> 15%] [50 x32 -> 25%] [51 x33 -> 30%]
        // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=142
        byte[] errorQR = {(byte)0x1d, (byte)0x28, (byte)0x6b, (byte)0x03, (byte)0x00, (byte)0x31, (byte)0x45, (byte)0x31};


        // QR Code: Store the data in the symbol storage area
        // Hex      1D      28      6B      pL      pH      31      50      30      d1...dk
        // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=143
        //                        1D          28          6B         pL          pH  cn(49->x31) fn(80->x50) m(48->x30) d1…dk
        byte[] storeQR = {(byte)0x1d, (byte)0x28, (byte)0x6b, store_pL, store_pH, (byte)0x31, (byte)0x50, (byte)0x30};


        // QR Code: Print the symbol data in the symbol storage area
        // Hex      1D      28      6B      03      00      31      51      m
        // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=144
        byte[] printQR = {(byte)0x1d, (byte)0x28, (byte)0x6b, (byte)0x03, (byte)0x00, (byte)0x31, (byte)0x51, (byte)0x30};

        // flush() runs the print job and clears out the print buffer
//        flush();

        // write() simply appends the data to the buffer
        mPrinter.sendByteData(modelQR);

        mPrinter.sendByteData(sizeQR);
        mPrinter.sendByteData(errorQR);
        mPrinter.sendByteData(storeQR);
        mPrinter.sendByteData(qrdata.getBytes());
        mPrinter.sendByteData(printQR);

    }


    public void print_qr_codee(PrinterInstance mPrinter,String qrdata)
    {
        int store_len = qrdata.length() + 3;
        byte store_pL = (byte) (store_len % 256);
        byte store_pH = (byte) (store_len / 256);


        // QR Code: Select the modelc
        //              Hex     1D      28      6B      04      00      31      41      n1(x32)     n2(x00) - size of model
        // set n1 [49 x31, model 1] [50 x32, model 2] [51 x33, micro qr code]
        // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=140
        byte[] modelQR = {(byte)0x1d, (byte)0x28, (byte)0x6b, (byte)0x04, (byte)0x00, (byte)0x31, (byte)0x41, (byte)0x32, (byte)0x00};

        // QR Code: Set the size of module
        // Hex      1D      28      6B      03      00      31      43      n
        // n depends on the printer
        // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=141


        byte[] sizeQR = {(byte)0x1d, (byte)0x28, (byte)0x6b, (byte)0x03, (byte)0x00, (byte)0x31, (byte)0x43, (byte)0x1};


        //          Hex     1D      28      6B      03      00      31      45      n
        // Set n for error correction [48 x30 -> 7%] [49 x31-> 15%] [50 x32 -> 25%] [51 x33 -> 30%]
        // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=142
        byte[] errorQR = {(byte)0x1d, (byte)0x28, (byte)0x6b, (byte)0x03, (byte)0x00, (byte)0x31, (byte)0x45, (byte)0x31};


        // QR Code: Store the data in the symbol storage area
        // Hex      1D      28      6B      pL      pH      31      50      30      d1...dk
        // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=143
        //                        1D          28          6B         pL          pH  cn(49->x31) fn(80->x50) m(48->x30) d1…dk
        byte[] storeQR = {(byte)0x1d, (byte)0x28, (byte)0x6b, store_pL, store_pH, (byte)0x31, (byte)0x50, (byte)0x30};


        // QR Code: Print the symbol data in the symbol storage area
        // Hex      1D      28      6B      03      00      31      51      m
        // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=144
        byte[] printQR = {(byte)0x1d, (byte)0x28, (byte)0x6b, (byte)0x03, (byte)0x00, (byte)0x31, (byte)0x51, (byte)0x30};

        // flush() runs the print job and clears out the print buffer
//        flush();

        // write() simply appends the data to the buffer
        mPrinter.sendByteData(modelQR);

        mPrinter.sendByteData(sizeQR);
        mPrinter.sendByteData(errorQR);
        mPrinter.sendByteData(storeQR);
        mPrinter.sendByteData(qrdata.getBytes());
        mPrinter.sendByteData(printQR);

    }

    public void enablePrintBtn(final boolean enable) {
        ApplicationThread.uiPost(LOG_TAG, "updating ui", new Runnable() {
            @Override
            public void run() {
                submit.setEnabled(enable);
                submit.setClickable(enable);
                submit.setFocusable(enable);
            }
        });

    }

    @Override
    public void enablingPrintButton(boolean rePrint) {
        enablePrintBtn(rePrint);
    }

    //When Printer type selected
    @Override
    public void onPrinterTypeSelected(int printerType) {

        if (printerType == PrinterChooserFragment.USB_PRINTER) {
            FragmentManager fm = getSupportFragmentManager();
            UsbDevicesListFragment usbDevicesListFragment = new UsbDevicesListFragment();
            usbDevicesListFragment.setOnUsbDeviceSelected(GatepassinActivity.this);
            usbDevicesListFragment.show(fm, "usb fragment");
        } else {
            FragmentManager fm = getSupportFragmentManager();
            BluetoothDevicesFragment bluetoothDevicesFragment = new BluetoothDevicesFragment();
            bluetoothDevicesFragment.setOnDeviceSelected(GatepassinActivity.this);
            bluetoothDevicesFragment.show(fm, "bluetooth fragment");
        }

    }

    public static String convertDateFormat(String inputDate) {
        String outputDate = null;

        try {
            // Input format
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            Date date = inputFormat.parse(inputDate);

            // Output format
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            outputDate = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            // Handle parsing exception if needed
        }

        return outputDate;
    }

}