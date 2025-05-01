 package com.palm360.palmgrading.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.print.sdk.Barcode;
import com.android.print.sdk.PrinterConstants;
import com.android.print.sdk.PrinterInstance;
import com.palm360.palmgrading.MainActivity;
import com.palm360.palmgrading.R;
import com.palm360.palmgrading.cloudhelper.ApplicationThread;
import com.palm360.palmgrading.common.CommonConstants;
import com.palm360.palmgrading.common.CommonUtils;
import com.palm360.palmgrading.database.DataAccessHandler;
import com.palm360.palmgrading.database.Queries;
import com.palm360.palmgrading.datasync.helpers.DataSyncHelper;
import com.palm360.palmgrading.printer.BluetoothDevicesFragment;
import com.palm360.palmgrading.printer.PrinterChooserFragment;
import com.palm360.palmgrading.printer.UsbDevicesListFragment;
import com.palm360.palmgrading.printer.onPrinterType;
import com.palm360.palmgrading.utils.UiUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

 public class GatepasstokenActivity extends AppCompatActivity implements BluetoothDevicesFragment.onDeviceSelected, onPrinterType, UsbDevicesListFragment.onUsbDeviceSelected  {
     private static final String LOG_TAG = GatepasstokenActivity.class.getName();
     String qrvalue;
     Spinner fruittype, location_spinner;
     EditText vehiclenumber;
     LinearLayout scan_lyt, consignmentlocation_ll, location_ll;
     boolean selectedfruittype;
     Button submit, scanformill;
     String GatePassSerialNumber;
     String fruitType;
     private DataAccessHandler dataAccessHandler;
     String qrCodeValue;
     String currentDateTime;
     TextView consignment_location;
     String millcode;
     String locationnameforconsignemnt;
     int locationidforconsignemnt;
     private String LocationId,locationname;

     String currentdateandtimeforprint, currentdateandtimeforcreate;
     private LinkedHashMap<String, String>  LocationMap;
     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_gatepasstoken);
         Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
         toolbar.setTitle("Gate Pass Serial Number");
         setSupportActionBar(toolbar);
         intviews();
         Setviews();

     }

     private void intviews() {
         dataAccessHandler = new DataAccessHandler(GatepasstokenActivity.this);
         fruittype = findViewById(R.id.fruit_spinner);
         vehiclenumber = findViewById(R.id.vehiclenumber);
         submit = findViewById(R.id.gatepasstokensubmit);
         location_spinner = findViewById(R.id.location_spinner);
         scan_lyt= findViewById(R.id.scan_lyt);
         scanformill = findViewById(R.id.scanformill);
         consignmentlocation_ll = findViewById(R.id.consignmentlocation_ll);
         consignment_location =  findViewById(R.id.consignment_location);
         location_ll =  findViewById(R.id.location_ll);
         location_ll.setVisibility(View.GONE);

//         Bundle extras = getIntent().getExtras();
//         if (extras != null) {
//             qrvalue = extras.getString("qrvalue");
//             Log.d("QRCodeValueis", qrvalue + "");
//         }

         InputFilter capitalLettersFilter = new InputFilter.AllCaps();
         InputFilter[] filters = new InputFilter[]{capitalLettersFilter};
         vehiclenumber.setFilters(filters);
     }
     private void Setviews() {

         //Binding data to isloosefruitavailable spinner and onclick listener
         String[] fruittypeArray = getResources().getStringArray(R.array.fruittype);
         List<String> fruittypeList = Arrays.asList(fruittypeArray);
         ArrayAdapter<String> isloosefruitavailableAdapter = new ArrayAdapter<>(GatepasstokenActivity.this, android.R.layout.simple_spinner_item, fruittypeList);
         isloosefruitavailableAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
         fruittype.setAdapter(isloosefruitavailableAdapter);

         fruittype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
             @Override
             public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                 Log.d("selectedfruittype", fruittype.getSelectedItem() + "");
                 submit.setVisibility(View.VISIBLE);
                 scan_lyt.setVisibility(View.GONE);



                 if (fruittype.getSelectedItemPosition() == 1) {
                     selectedfruittype = true;
                     fruitType= "Collection";
                     submit.setVisibility(View.VISIBLE);
                     location_spinner.setSelection(0);
                     scan_lyt.setVisibility(View.GONE);
                     location_ll.setVisibility(View.VISIBLE);
                     location_spinner.setEnabled(true);
                     location_spinner.setClickable(true);
                     consignmentlocation_ll.setVisibility(View.GONE);


                 }
                 if (fruittype.getSelectedItemPosition() == 2){
                     selectedfruittype = false;
                     fruitType= "Consignment";
                     submit.setVisibility(View.GONE);
                     location_spinner.setSelection(0);
                     location_spinner.setEnabled(false);
                     location_spinner.setClickable(false);
                     scan_lyt.setVisibility(View.VISIBLE);
                     location_ll.setVisibility(View.GONE);
                     consignmentlocation_ll.setVisibility(View.VISIBLE);


                 }


             }

             @Override
             public void onNothingSelected(AdapterView<?> adapterView) {

             }
         });



         LocationMap = dataAccessHandler.getvechileData(Queries.getInstance().getmilllocations());
         Log.d("LocationMaP", LocationMap.size() +"");
         ArrayAdapter locationArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                 CommonUtils.fromMap(LocationMap, "Mill  Location"));
         locationArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
         location_spinner.setAdapter(locationArrayAdapter);

         location_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
             @Override
             public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                 if (LocationMap != null && LocationMap.size() > 0 && location_spinner.getSelectedItemPosition() != 0) {
                     LocationId = LocationMap.keySet().toArray(new String[LocationMap.size()])[i - 1];
                     locationname = location_spinner.getSelectedItem().toString();
                     android.util.Log.v(LOG_TAG, "@@@ vehicle category code " + LocationId + " category name " + LocationId);

                     //Binding Data to Vehicle Type

                 }
             }

             @Override
             public void onNothingSelected(AdapterView<?> adapterView) {

             }
         });


         submit.setOnClickListener(new View.OnClickListener() {

             @Override
             public void onClick(View v) {
                 if (validation()){
//                    enablePrintBtn(false);
//                    submit.setAlpha(0.5f);

//                     Calendar calendar = Calendar.getInstance();
//                     Date currentDate = calendar.getTime();
//
//                     calendar.set(Calendar.HOUR_OF_DAY, 6);
//                     calendar.set(Calendar.MINUTE, 0);
//                     calendar.set(Calendar.SECOND, 0);
//                     calendar.set(Calendar.MILLISECOND, 0);
//
//
//                     // Get today's date at 6:00:00
//                     Date todayAt6AM = calendar.getTime();
//
//                     // Move back one day
//                     calendar.add(Calendar.DAY_OF_MONTH, +1);
//
//                     // Get yesterday's date at 6:00:00
//                     Date tommorowAt6AM = calendar.getTime();
//
//                     SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");



                     String currentDate = CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY);
//                     Log.d("currentDate", currentDate + "");
//                     Log.d("todayAt6AM", dateFormat.format(todayAt6AM));



                     String maxnumber = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getGatePassSerialNumber(currentDate));
                     Log.d("maxnumber", maxnumber + "");
                     String incrementedMaxNumber = "";
                     if(maxnumber!=null) {
                         try {
                             // Convert maxnumber to integer and increment by 1
                             int incrementedNumber = Integer.parseInt(maxnumber) + 1;

                             // Convert back to string
                              incrementedMaxNumber = String.valueOf(incrementedNumber);
                             GatePassSerialNumber = dataAccessHandler.getserialnumber(incrementedMaxNumber);
                             Log.d("maxnumber", incrementedMaxNumber);
                             Log.d("GatePassSerialNumber", GatePassSerialNumber + "");
                         } catch (NumberFormatException e) {
                             // Handle the case where maxnumber is not a valid integer
                             Log.e("maxnumber", "Error parsing maxnumber as an integer", e);
                         }
                     }
                     else{
                         GatePassSerialNumber = dataAccessHandler.getserialnumber(incrementedMaxNumber);
                         Log.d("GatePassSerialNumber134", GatePassSerialNumber + "");

                     }
//        select GatePassSerialNumber  as Maxnumber FROM  GatePassToken Where CreatedDate like '%2023-11-21%' ORDER BY ID DESC LIMIT 1



                     // Get current date and time stamp
                     SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                      currentDateTime = sdf.format(new Date());
                     Log.d("currentDateTime", currentDateTime + "");

                     SimpleDateFormat sdf1 = new SimpleDateFormat(CommonConstants.DATE_FORMAT_5);
                     currentdateandtimeforprint = sdf1.format(new Date());
                     Log.d("dateandtimeforprint", currentdateandtimeforprint + "");

                     if (fruittype.getSelectedItemPosition() == 1) {

                         qrCodeValue =  currentDateTime+CommonConstants.TAB_ID+LocationId+GatePassSerialNumber +"/"+ LocationId +"/" + GatePassSerialNumber +"/"+selectedfruittype+"/" + vehiclenumber.getText().toString();

                     }

                     if (fruittype.getSelectedItemPosition() == 2){

                         qrCodeValue =  currentDateTime+CommonConstants.TAB_ID+locationidforconsignemnt+GatePassSerialNumber +"/"+ locationidforconsignemnt +"/" + GatePassSerialNumber +"/"+selectedfruittype+"/" + vehiclenumber.getText().toString();

                     }

//                     qrCodeValue =  currentDateTime+CommonConstants.TAB_ID+LocationId+GatePassSerialNumber +"/"+ LocationId +"/" + GatePassSerialNumber +"/"+selectedfruittype+"/" + vehiclenumber.getText().toString()
//                             +"/" + vehiclenumber.getText().toString()
//                             +"/" + vehiclenumber.getText().toString()+"/" + vehiclenumber.getText().toString()+"/" + vehiclenumber.getText().toString()+"/" + vehiclenumber.getText().toString()+"/" + vehiclenumber.getText().toString()+"/" + vehiclenumber.getText().toString()+"/" + vehiclenumber.getText().toString()+"/" + vehiclenumber.getText().toString()+"/" + vehiclenumber.getText().toString()+"/" + vehiclenumber.getText().toString()+"/" + vehiclenumber.getText().toString()+"/" + vehiclenumber.getText().toString()+"/" + vehiclenumber.getText().toString()+"/" + vehiclenumber.getText().toString()+"/" + vehiclenumber.getText().toString()+"/" + vehiclenumber.getText().toString()+"/" + vehiclenumber.getText().toString()+"/" + vehiclenumber.getText().toString()+"/" + vehiclenumber.getText().toString()+"/" + vehiclenumber.getText().toString()+"/" + vehiclenumber.getText().toString()+"/" + vehiclenumber.getText().toString()+"/" + vehiclenumber.getText().toString()+"/" + vehiclenumber.getText().toString()+"/" + vehiclenumber.getText().toString()+"/" + vehiclenumber.getText().toString()+"/" + vehiclenumber.getText().toString();
                     Log.d("qrCodeValue", qrCodeValue + "");

                     //savegatepasstoken();
                    FragmentManager fm = getSupportFragmentManager();
                    PrinterChooserFragment printerChooserFragment = new PrinterChooserFragment();
                    printerChooserFragment.setPrinterType(GatepasstokenActivity.this);
                    printerChooserFragment.show(fm, "bluetooth fragment");

                 }
             }
         });

         scanformill.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {

//                 Intent millintent = new Intent(GatepasstokenActivity.this, QRScanActivity.class);
//                 millintent.putExtra("ActivityName", "GatepasstokenActivity");
//                 startActivity(millintent);
                 Intent intent = new Intent(GatepasstokenActivity.this, QRScanActivity.class);
                 intent.putExtra("ActivityName", "GatepasstokenActivity");
                 startActivityForResult(intent, 1001);
             }
         });

     }

     @Override
     protected void onActivityResult(int requestCode, int resultCode, Intent data) {
         super.onActivityResult(requestCode, resultCode, data);
         if (requestCode == 1001 && resultCode == RESULT_OK) {
             if (data != null) {
                  qrvalue = data.getStringExtra("qrvalue");
                 if (qrvalue != null) {
                     if(qrvalue.length() == 35){
                     // Handle the scanned QR code result
                     Log.d("QRCodeScan", "Scanned value: " + qrvalue);
                     millcode =  qrvalue.substring(qrvalue.length() - 8);
                     Log.d("millcode", millcode);

                     String weighbridgeCode = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getweighbridgedetails(millcode));
                     Log.d("weighbridgeCode", weighbridgeCode + "");


                     if (weighbridgeCode == null || weighbridgeCode.trim().isEmpty()){
                         scan_lyt.setVisibility(View.GONE);
                         consignmentlocation_ll.setVisibility(View.GONE);
                         //Toast.makeText(GatepasstokenActivity.this, "Consignment mill not assigned to you", Toast.LENGTH_SHORT).show();
                         UiUtils.showCustomToastMessage("Consignment mill not assigned to you", GatepasstokenActivity.this, 1);

                     }else {
                         scan_lyt.setVisibility(View.GONE);
                         submit.setVisibility(View.VISIBLE);
                         locationnameforconsignemnt = weighbridgeCode;
                         consignment_location.setText(locationnameforconsignemnt+"");

                         locationidforconsignemnt = dataAccessHandler.getOnlyOneIntValueFromDb(Queries.getInstance().getlocationid(locationnameforconsignemnt));
                         Log.d("locationidconsignemnt", locationidforconsignemnt + "");



                     }
                     }
                     else{
                         UiUtils.showCustomToastMessage("Invalid QR Code", GatepasstokenActivity.this, 1);

                     }

                 }
             }else{
                 UiUtils.showCustomToastMessage("QR Code doesn't have data", GatepasstokenActivity.this, 1);
             }

         }else{
             UiUtils.showCustomToastMessage("QR Code not scanned properly", GatepasstokenActivity.this, 1);
         }
     }

     private void savegatepasstoken() {

         List<LinkedHashMap> details = new ArrayList<>();
         LinkedHashMap map = new LinkedHashMap();

         //DATE_FORMAT_DDMMYYYY_HHMMSS

         String inputDate = currentdateandtimeforprint;
         currentdateandtimeforcreate = convertDateFormat(inputDate);


         if (fruittype.getSelectedItemPosition() == 1) {

             map.put("GatePassTokenCode", currentDateTime + CommonConstants.TAB_ID + LocationId + GatePassSerialNumber );
         }

         if (fruittype.getSelectedItemPosition() == 2) {

             map.put("GatePassTokenCode", currentDateTime + CommonConstants.TAB_ID + locationidforconsignemnt + GatePassSerialNumber );

         }

         map.put("VehicleNumber", vehiclenumber.getText().toString());
         map.put("GatePassSerialNumber", GatePassSerialNumber);

         if (fruittype.getSelectedItemPosition() == 1) {

             map.put("MillLocationTypeId", LocationId);
         }

         if (fruittype.getSelectedItemPosition() == 2) {

             map.put("MillLocationTypeId", locationidforconsignemnt);

         }



        int isfruitavailable = 0;

        if (fruittype.getSelectedItemPosition() == 1){

            isfruitavailable = 0;
        }else if (fruittype.getSelectedItemPosition() == 2){
            isfruitavailable = 1;
        }
         if (fruittype.getSelectedItemPosition() == 1) {
             selectedfruittype = true;
             fruitType= "Collection";

         } else {
             selectedfruittype = false;
             fruitType= "Consignment";
         }


         map.put("IsCollection", isfruitavailable);
         map.put("CreatedByUserId", CommonConstants.USER_ID);
         map.put("CreatedDate", currentdateandtimeforcreate);
         map.put("ServerUpdatedStatus", false);


         details.add(map);

         dataAccessHandler.saveData("GatePassToken", details, new ApplicationThread.OnComplete<String>() {
             @Override
             public void execute(boolean success, String result, String msg) {

                 if (success) {
                     Log.d(GradingActivity.class.getSimpleName(), "==>  Analysis ==> TABLE_GatePassToken INSERT COMPLETED");
                     if (CommonUtils.isNetworkAvailable(GatepasstokenActivity.this)) {
                         DataSyncHelper.performRefreshTransactionsSync(GatepasstokenActivity.this, new ApplicationThread.OnComplete() {
                             @Override
                             public void execute(boolean success, Object result, String msg) {
                                 if (success) {
                                     ApplicationThread.uiPost(LOG_TAG, "transactions sync message", new Runnable() {
                                         @Override
                                         public void run() {
                                             CommonConstants.IsLogin = false;
                                             UiUtils.showCustomToastMessage("Successfully data sent to server", GatepasstokenActivity.this, 0);
                                             startActivity(new Intent(GatepasstokenActivity.this, MainActivity.class));

                                         }
                                     });
                                 } else {
                                     ApplicationThread.uiPost(LOG_TAG, "transactions sync failed message", new Runnable() {
                                         @Override
                                         public void run() {
                                             UiUtils.showCustomToastMessage("Data sync failed", GatepasstokenActivity.this, 1);
                                         }
                                     });
                                 }
                             }
                         });
                     } else {
                         startActivity(new Intent(GatepasstokenActivity.this, MainActivity.class));

                     }


                 } else {
                     Toast.makeText(GatepasstokenActivity.this, "Submit Failed", Toast.LENGTH_SHORT).show();
                 }
             }
         });
     }




     private boolean validation() {
         if (TextUtils.isEmpty(vehiclenumber.getText().toString())) {
             UiUtils.showCustomToastMessage("Please Enter Vehicle Number", GatepasstokenActivity.this, 0);
             return false;
         }

         if (fruittype.getSelectedItemPosition() == 0) {
             UiUtils.showCustomToastMessage("Please Select  Fruit Type", GatepasstokenActivity.this, 0);
             return false;
         }
//&& fruitType == "Collection"
         if (location_spinner.getSelectedItemPosition() == 0 && fruitType != "Consignment") {
             UiUtils.showCustomToastMessage("Please Select  Mill Location", GatepasstokenActivity.this, 0);
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
             UiUtils.showCustomToastMessage("Printing failed", GatepasstokenActivity.this, 1);
         }
     }


     public void printGAtepasstoken(PrinterInstance mPrinter, boolean isReprint, int printCount) {

         int token = Integer.parseInt(GatePassSerialNumber);
         Log.d("token", token + "");
         String formattedToken = String.valueOf(token);
         String tokenCount = "";
         tokenCount = formattedToken;
         Log.d("tokenCount", tokenCount + "");

         mPrinter.init();
         StringBuilder sb = new StringBuilder();
         mPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER);
         mPrinter.setCharacterMultiple(0, 1);
         mPrinter.printText(" 3F OILPALM PVT LTD " + "\n");
         mPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER);
         mPrinter.setCharacterMultiple(0, 0);
         mPrinter.printText(" Gate Serial Number " + "\n");
         mPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER);
         mPrinter.setCharacterMultiple(0, 0);
         mPrinter.setLeftMargin(15, 15);
        // sb.append("==============================================" + "\n");

         String space = "-----------------------------------------------";
         String tokenNumber  =  "Token Number";
         String spaceBuilderr = "\n";

         mPrinter.printText(space);
         mPrinter.printText(spaceBuilderr);
         mPrinter.setPrintModel(true,true,true,false);
         mPrinter.printText(tokenCount);
         mPrinter.setPrintModel(false,false,false,false);
         mPrinter.printText(spaceBuilderr);
         mPrinter.printText(space);

         mPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_LEFT);
         mPrinter.setCharacterMultiple(0, 0);
         mPrinter.setLeftMargin(15, 15);


         sb.append(" ");
         sb.append("  Vehicle Number : ").append(vehiclenumber.getText().toString() + "").append("\n");
         sb.append(" ");
//         sb.append(" CCCode : ").append(splitString[1] + "").append("\n");
//         sb.append(" ");
         sb.append(" Fruit Type : ").append(fruitType + "").append("\n");
         sb.append(" ");

         sb.append(" Date : ").append(currentdateandtimeforprint + "").append("\n");

         sb.append("  Created By : ").append(CommonConstants.USER_NAME + "").append("\n");

         sb.append("-----------------------------------------------" + "\n");

         mPrinter.printText(sb.toString());


//         String hashString = qrvalue+"/"+CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS)+"/"+unripen.getText().toString()+"/"+underripe.getText().toString()+"/"+ripen.getText().toString()
//                 +"/"+overripe.getText().toString()+"/"+diseased.getText().toString()+"/"+emptybunches.getText().toString()+"/"
//                 +longstalk.getText().toString()+"/"+mediumstalk.getText().toString()+"/"+shortstalk.getText().toString()+"/"+
//                 optimum.getText().toString()+"/"+fruitavailable+"/"+fruightweight+"/"+rejectedbunches+
//                 "/"+gradingdoneby.getText().toString();
//         String qrCodeValue = hashString;
         Log.d("qrCodeValueis", qrCodeValue  + "");
         Barcode barcode = new Barcode(PrinterConstants.BarcodeType.QRCODE, 3, 95, 3, qrCodeValue);

         mPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER);
         mPrinter.setCharacterMultiple(0, 1);

//         String space = "-----------------------------------------------";
//         String tokenNumber  =  "Token Number";
//       String spaceBuilderr = "\n";
//
//         mPrinter.printText(space);
//         mPrinter.printText(spaceBuilderr);
//         mPrinter.printText(tokenNumber);
//         mPrinter.printText(spaceBuilderr);
//         mPrinter.printText(tokenCount);
//         mPrinter.printText(spaceBuilderr);
//         mPrinter.printText(space);
//         mPrinter.printText(spaceBuilderr);

//         mPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER);
//         mPrinter.setCharacterMultiple(0, 3);


         if(CommonConstants.PrinterName.contains("G-8MBT3")){
             Log.d(LOG_TAG,"Available");
             //mPrinter.setPrintModel(false,true,true,false);
             //print_qr_code(mPrinter,qrCodeValue);
         }else{
             Log.d(LOG_TAG,"Not Available");
         }


         if(CommonConstants.PrinterName.contains("G-8BT3 AMIGOS")){
             Log.d(LOG_TAG,"########### NEW ##############");
             //mPrinter.setPrintModel(false,true,true,false);
             print_qr_codee(mPrinter,qrCodeValue);
         }else if (CommonConstants.PrinterName.contains("AMIGOS")){
             print_qr_code(mPrinter,qrCodeValue);
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
             UiUtils.showCustomToastMessage("Printing failes due to " + e.getMessage(), GatepasstokenActivity.this, 1);
             printSuccess = false;
         } finally {
             if (printSuccess) {
                 Toast.makeText(GatepasstokenActivity.this, "Print Success", Toast.LENGTH_SHORT).show();
                 savegatepasstoken();
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
             usbDevicesListFragment.setOnUsbDeviceSelected(GatepasstokenActivity.this);
             usbDevicesListFragment.show(fm, "usb fragment");
         } else {
             FragmentManager fm = getSupportFragmentManager();
             BluetoothDevicesFragment bluetoothDevicesFragment = new BluetoothDevicesFragment();
             bluetoothDevicesFragment.setOnDeviceSelected(GatepasstokenActivity.this);
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