package com.oilpalm3f.gradingapp.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.print.sdk.Barcode;
import com.android.print.sdk.PrinterConstants;
import com.android.print.sdk.PrinterInstance;
import com.oilpalm3f.gradingapp.R;
import com.oilpalm3f.gradingapp.cloudhelper.ApplicationThread;
import com.oilpalm3f.gradingapp.cloudhelper.Log;
import com.oilpalm3f.gradingapp.common.CommonConstants;
import com.oilpalm3f.gradingapp.common.CommonUtils;
import com.oilpalm3f.gradingapp.database.DataAccessHandler;
import com.oilpalm3f.gradingapp.database.Queries;
import com.oilpalm3f.gradingapp.dbmodels.GatepassTokenListModel;
import com.oilpalm3f.gradingapp.dbmodels.GradingReportModel;
import com.oilpalm3f.gradingapp.printer.BluetoothDevicesFragment;
import com.oilpalm3f.gradingapp.printer.PrinterChooserFragment;
import com.oilpalm3f.gradingapp.printer.UsbDevicesListFragment;
import com.oilpalm3f.gradingapp.printer.onPrinterType;
import com.oilpalm3f.gradingapp.uihelper.ProgressBar;
import com.oilpalm3f.gradingapp.utils.UiUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GradingReportActivity extends AppCompatActivity implements onPrintOptionSelected, onPrinterType, UsbDevicesListFragment.onUsbDeviceSelected, BluetoothDevicesFragment.onDeviceSelected{
    private static final String LOG_TAG = GradingReportActivity.class.getName();
    private GradingReportAdapter gradingReportRecyclerAdapter;
    private RecyclerView gradingReportsList;
    private List<GradingReportModel> mReportsList = new ArrayList<>();
    private TextView tvNorecords, totalNetWeightSum;
    private DataAccessHandler dataAccessHandler;
    private EditText fromDateEdt, toDateEdt;
    private Calendar myCalendar = Calendar.getInstance();
    private Button searchBtn;
    private String searchQuery = "";
    private String fromDateStr = "";
    private String toDateStr = "";
    private GradingReportModel selectedReport;
    private BluetoothDevicesFragment bluetoothDevicesFragment = null;
    private UsbDevicesListFragment usbDevicesListFragment = null;

    private EditText searchtext;
    private ImageView clearsearch;
    private android.widget.ProgressBar searchprogress;
    private boolean isSearch = false;

    String searchKey = "";

    private LinearLayoutManager layoutManager;



    public static final int LIMIT = 30;
    private int offset;
    private boolean hasMoreItems = true;


    //Oncreate with SetAdapter
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grading_report);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Grading Reports");
        setSupportActionBar(toolbar);

        dataAccessHandler = new DataAccessHandler(this);
        initUI();
        setViews();

        String currentDate = CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        fromDateEdt.setText(sdf.format(new Date()));
        toDateEdt.setText(sdf.format(new Date()));
//
//       searchQuery = Queries.getInstance().getGradingReports(currentDate, currentDate);
////        SearchCollectionwithoutPlotQuery = Queries.getInstance().getCollectionCenterReportsWithOutPlot(currentDate, currentDate);
//        updateLabel(0);
//        updateLabel(1);
//        getGradingReports(searchQuery);
        CommonUtils.currentActivity = this;
        gradingReportRecyclerAdapter = new GradingReportAdapter(GradingReportActivity.this, mReportsList);

//        gradingReportsList.setLayoutManager(new LinearLayoutManager(GradingReportActivity.this, LinearLayoutManager.VERTICAL, false));
//        gradingReportsList.setAdapter(gradingReportRecyclerAdapter);

//        String CollectionNetWeight = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getCollectionNetSum(currentDate, currentDate));
//        String CollectionWithOutPlotNetWeight = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getCollectionWithOutPlotNetSum(currentDate, currentDate));
//
//        if (CollectionNetWeight == null){
//            CollectionNetWeight = "0.0";
//        }
//        if (CollectionWithOutPlotNetWeight == null){
//            CollectionWithOutPlotNetWeight = "0.0";
//        }
//        Float totalNetWeight = Float.valueOf(CollectionNetWeight) +Float.valueOf(CollectionWithOutPlotNetWeight);
//        totalNetWeightSum.setText(" "+totalNetWeight + " Kgs");
    }

    private void setViews() {
        offset = offset + LIMIT;
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressBar.showProgressBar(GradingReportActivity.this, "Please wait...");
                mReportsList = (List<GradingReportModel>) dataAccessHandler.getGradingList(Queries.getInstance().getGradingReports(LIMIT, offset,fromDateStr,toDateStr, searchKey), 1);
                Collections.reverse(mReportsList);
                ApplicationThread.bgndPost(LOG_TAG, "getting reports data", new Runnable() {
                    @Override
                    public void run() {

                        ApplicationThread.uiPost(LOG_TAG, "", new Runnable() {
                            @Override
                            public void run() {
                                ProgressBar.hideProgressBar();
                                gradingReportRecyclerAdapter = new GradingReportAdapter(GradingReportActivity.this, mReportsList);
                                if (mReportsList != null && !mReportsList.isEmpty() && mReportsList.size()!= 0) {
                                    gradingReportsList.setVisibility(View.VISIBLE);
                                    tvNorecords.setVisibility(View.GONE);
                                    layoutManager = new LinearLayoutManager(GradingReportActivity.this, LinearLayoutManager.VERTICAL, false);
                                    gradingReportsList.setLayoutManager(layoutManager);

                                    gradingReportsList.setAdapter(gradingReportRecyclerAdapter);
                                    gradingReportRecyclerAdapter.setonPrintSelected((GradingReportActivity.this));
                                    //   setTitle(alert_type, offset == 0 ? alertsVisitsInfoList.size() : offset);
                                }
                                else{
                                    gradingReportsList.setVisibility(View.GONE);
                                    tvNorecords.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                    }
                });
            }
        });

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(0);
            }
        };



        final DatePickerDialog.OnDateSetListener toDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(1);
            }
        };

        String dateFormatter = "yyyy-MM-dd";
        SimpleDateFormat sdf2 = new SimpleDateFormat(dateFormatter, Locale.US);
        fromDateStr = sdf2.format(myCalendar.getTime());
        toDateStr = sdf2.format(myCalendar.getTime());

        toDateEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(GradingReportActivity.this, toDate, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));  //date is dateSetListener as per your code in question
                datePickerDialog.show();
            }
        });

        fromDateEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(GradingReportActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));  //date is dateSetListener as per your code in question
                datePickerDialog.show();
            }
        });

        clearsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSearch = false;
                mReportsList.clear();
                searchtext.setText("");
            }
        });

        searchtext.addTextChangedListener(mTextWatcher);

        gradingsearchreportslist(offset);

    }

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Log.d("WhatisinSearch", "is :"+ s);
            //
            offset = 0;
            ApplicationThread.uiPost(LOG_TAG, "search", new Runnable() {
                @Override
                public void run() {
                    doSearch(s.toString().trim());
                    if (s.toString().length() > 0) {
                        clearsearch.setVisibility(View.VISIBLE);
                    } else {
                        clearsearch.setVisibility(View.GONE);
                    }
                }
            }, 100);
        }

        @Override
        public void afterTextChanged(final Editable s) {

        }
    };

    public void doSearch(String searchQuery) {
        Log.d("DoSearchQuery", "is :" +  searchQuery);
        offset = 0;
        hasMoreItems = true;
        if (searchQuery !=null &  !TextUtils.isEmpty(searchQuery)  & searchQuery.length()  > 0) {

            offset = 0;
            isSearch = true;
            searchKey = searchQuery.trim();
            gradingsearchreportslist(offset);
        } else {
            searchKey = "";
            isSearch = false;
            gradingsearchreportslist(offset);
        }
    }

    //To Get Gradnig Reports
//    public void getGradingReports(final String searchQuery) {
//        ProgressBar.showProgressBar(this, "Please wait...");
//        ApplicationThread.bgndPost(LOG_TAG, "getting reports data", new Runnable() {
//            @Override
//            public void run() {
//                dataAccessHandler.getGradingReportDetails(searchQuery, new ApplicationThread.OnComplete<List<GradingReportModel>>() {
//                    @Override
//                    public void execute(boolean success, final List<GradingReportModel> reports, String msg) {
//                        ProgressBar.hideProgressBar();
//                        if (success) {
//                            if (reports != null && reports.size() > 0) {
//                                mReportsList.clear();
//                                mReportsList = reports;
//                                ApplicationThread.uiPost(LOG_TAG, "update ui", new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        int recordsSize = reports.size();
//                                        Log.v(LOG_TAG, "data size " + recordsSize);
//                                        if (recordsSize > 0) {
//                                            gradingReportRecyclerAdapter.updateAdapter(reports);
//                                            tvNorecords.setVisibility(View.GONE);
//                                            gradingReportsList.setVisibility(View.VISIBLE);
//                                          //  setTile(getString(R.string.collection_report) + " ("+recordsSize+")");
//                                        } else {
//                                            tvNorecords.setVisibility(View.VISIBLE);
//                                        }
//                                    }
//                                });
//                            } else {
//                                ApplicationThread.uiPost(LOG_TAG, "updating ui", new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        tvNorecords.setVisibility(View.VISIBLE);
//                                        Log.v(LOG_TAG, "@@@ No records found");
//                                        gradingReportsList.setVisibility(View.GONE);
//                                    }
//                                });
//                            }
//                        } else {
//                            ApplicationThread.uiPost(LOG_TAG, "updating ui", new Runnable() {
//                                @Override
//                                public void run() {
//                                    tvNorecords.setVisibility(View.VISIBLE);
//                                    Log.v(LOG_TAG, "@@@ No records found");
//                                    gradingReportsList.setVisibility(View.GONE);
//                                }
//                            });
//                        }
//                    }
//                });
//            }
//        });
//    }

//Intialize the UI & search funtionality
    private void initUI() {
        gradingReportsList = (RecyclerView) findViewById(R.id.grading_reports_list);
        searchBtn = (Button) findViewById(R.id.searchBtn);
        totalNetWeightSum = (TextView) findViewById(R.id.totalNetWeightSum);

        searchtext = (EditText) findViewById(R.id.searchtext);
        clearsearch = (ImageView) findViewById(R.id.clearsearch);
        searchprogress = (android.widget.ProgressBar) findViewById(R.id.searchprogress);

//        searchBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (TextUtils.isEmpty(fromDateStr) && TextUtils.isEmpty(toDateStr)) {
//                    UiUtils.showCustomToastMessage("Please select from or to dates", GradingReportActivity.this, 0);
//                } else if (isDateAfter(fromDateStr, toDateStr)){
////                    String CollectionNetWeight = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getCollectionNetSum(fromDateStr, toDateStr));
////                    String CollectionWithOutPlotNetWeight = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getCollectionWithOutPlotNetSum(fromDateStr, toDateStr));
////                    if (CollectionNetWeight == null){
////                        CollectionNetWeight = "0.0";
////                    }
////                    if (CollectionWithOutPlotNetWeight == null){
////                        CollectionWithOutPlotNetWeight = "0.0";
////                    }
////                    Float totalNetWeight = Float.valueOf(CollectionNetWeight) + Float.valueOf(CollectionWithOutPlotNetWeight);
//////                    String totalNetWeight = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getCollectionNetSum(fromDateStr, toDateStr));
////                    if (!TextUtils.isEmpty(String.valueOf(totalNetWeight))) {
////                        totalNetWeightSum.setText(" "+totalNetWeight + " Kgs");
////                    }
//                    searchQuery = Queries.getInstance().getGradingReports(fromDateStr, toDateStr);
////                    SearchCollectionwithoutPlotQuery = Queries.getInstance().getCollectionCenterReportsWithOutPlot(fromDateStr, toDateStr);
//                    if (null != gradingReportRecyclerAdapter) {
//                        mReportsList.clear();
//                        gradingReportRecyclerAdapter.notifyDataSetChanged();
//                    }
//                    getGradingReports(searchQuery);
//                }else{
//                    UiUtils.showCustomToastMessage("From date must be less than To date", GradingReportActivity.this, 1);
//                    mReportsList.clear();
//                    gradingReportRecyclerAdapter.notifyDataSetChanged();
//
//                }
//            }
//        });
        tvNorecords = (TextView) findViewById(R.id.no_records);
        tvNorecords.setVisibility(View.GONE);

        fromDateEdt = (EditText) findViewById(R.id.fromDate);
        toDateEdt = (EditText) findViewById(R.id.toDate);

        //From and To Date on Click listeners with DatePicker Dialogs

//        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker view, int year, int monthOfYear,
//                                  int dayOfMonth) {
//                myCalendar.set(Calendar.YEAR, year);
//                myCalendar.set(Calendar.MONTH, monthOfYear);
//                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//                updateLabel(0);
//            }
//        };
//
//
//
//        final DatePickerDialog.OnDateSetListener toDate = new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker view, int year, int monthOfYear,
//                                  int dayOfMonth) {
//                myCalendar.set(Calendar.YEAR, year);
//                myCalendar.set(Calendar.MONTH, monthOfYear);
//                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//                updateLabel(1);
//            }
//        };
//
//        toDateEdt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                DatePickerDialog datePickerDialog = new DatePickerDialog(GradingReportActivity.this, toDate, myCalendar
//                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
//                        myCalendar.get(Calendar.DAY_OF_MONTH));  //date is dateSetListener as per your code in question
//                datePickerDialog.show();
//            }
//        });
//
//        fromDateEdt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                DatePickerDialog datePickerDialog = new DatePickerDialog(GradingReportActivity.this, date, myCalendar
//                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
//                        myCalendar.get(Calendar.DAY_OF_MONTH));  //date is dateSetListener as per your code in question
//                datePickerDialog.show();
//            }
//        });
    }

    //Update the from and to date labels
    private void updateLabel(int type) {
        String myFormat = "dd-MM-yyyy";
        String dateFormatter = "yyyy-MM-dd";

        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        SimpleDateFormat sdf2 = new SimpleDateFormat(dateFormatter, Locale.US);

        if (type == 0) {
            fromDateStr = sdf2.format(myCalendar.getTime());
            fromDateEdt.setText(sdf.format(myCalendar.getTime()));
        } else {
            toDateStr = sdf2.format(myCalendar.getTime());
            toDateEdt.setText(sdf.format(myCalendar.getTime()));
        }

    }



    public static boolean isDateAfter(String startDate,String endDate)
    {
        try
        {
            String myFormatString = "yyyy-MM-dd"; // for example
            SimpleDateFormat df = new SimpleDateFormat(myFormatString);
            Date date1 = df.parse(endDate);
            Date startingDate = df.parse(startDate);

            if (date1.after(startingDate))
                return true;
            else if (date1.equals(startingDate))
                return true;
            else
                return false;
        }
        catch (Exception e)
        {

            return false;
        }
    }

    //On Print Option Selected
    @Override
    public void printOptionSelected(int position) {

        selectedReport = mReportsList.get(position);
    

        //Log.d("CreatedDate",selectedReport.getCreatedDatewithtime() + "");
//        Log.d("Underipe",selectedReport.getUnderRipe() + "");
//        Log.d("Ripen",selectedReport.getRipen() + "");
//        Log.d("Overripe",selectedReport.getOverRipe() + "");
//        Log.d("Diseased",selectedReport.getDiseased() + "");
//        Log.d("EmptyBunches",selectedReport.getEmptyBunches() + "");
//
//        Log.d("FFBLong",selectedReport.getFFBQualityLong() + "");
//        Log.d("FFBMedium",selectedReport.getFFBQualityMedium() + "");
//        Log.d("FFBShort",selectedReport.getFFBQualityShort() + "");
//        Log.d("FFBOptimum",selectedReport.getFFBQualityOptimum() + "");

        FragmentManager fm = getSupportFragmentManager();
        PrinterChooserFragment printerChooserFragment = new PrinterChooserFragment();
        printerChooserFragment.setPrinterType(this);
        printerChooserFragment.show(fm, "bluetooth fragment");

    }

    @Override
    public void enablingPrintButton(boolean rePrint) {

    }

    //Letting know how many times data should Print
    @Override
    public void selectedDevice(PrinterInstance printerInstance) {

        for (int i = 0; i < 1; i++) {
            printGradingData(printerInstance, i);
        }

    }

    // When Printer type selected
    @Override
    public void onPrinterTypeSelected(int printerType) {

        if (printerType == PrinterChooserFragment.USB_PRINTER) {
            FragmentManager fm = getSupportFragmentManager();
            usbDevicesListFragment = new UsbDevicesListFragment();
            usbDevicesListFragment.setOnUsbDeviceSelected(this);
            usbDevicesListFragment.show(fm, "usb fragment");
        } else {
            FragmentManager fm = getSupportFragmentManager();
            bluetoothDevicesFragment = new BluetoothDevicesFragment();
            bluetoothDevicesFragment.setOnDeviceSelected(this);
            bluetoothDevicesFragment.show(fm, "bluetooth fragment");
        }

    }

    //Print Data
    public void printGradingData(PrinterInstance mPrinter, int printCount) {

        String fruitTypeNumber;
        String fruitType;
        Log.d("fruitType", selectedReport.getFruitType());

        if (selectedReport.getFruitType().equalsIgnoreCase("1")){

            fruitTypeNumber = "01";
        }else{
            fruitTypeNumber = "02";
        }

        if (selectedReport.getFruitType().equalsIgnoreCase("1")){

            fruitType = "Collection";
        }else{
            fruitType = "Consignment";
        }

        String  requiredvalue = null;
//        String  requiredvaluee = null;

        requiredvalue = convertDateFormat(selectedReport.getCreatedDatewithtime());
        Log.d("requiredvalue", requiredvalue + "");
//        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        SimpleDateFormat output = new SimpleDateFormat("dd-MM-yyyy HH:mm");
//        SimpleDateFormat outputt = new SimpleDateFormat("dd-MM-yyyy");
//
//        try {
//            Date inputdate = input.parse(selectedReport.getCreatedDatewithtime());
//            requiredvalue = output.format(inputdate);
//            Log.d("inputdate", inputdate + "");
//            Log.d("requiredvalue", requiredvalue + "");
//
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        try {
//            Date inputdatee = input.parse(selectedReport.getCreatedDatewithtime());
//            requiredvaluee = outputt.format(inputdatee);
//
//
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

        mPrinter.init();
        StringBuilder sb = new StringBuilder();
        mPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER);
        mPrinter.setCharacterMultiple(0, 1);
        mPrinter.printText(" 3F OILPALM PVT LTD " + "\n");
        mPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER);
        mPrinter.setCharacterMultiple(0, 0);
        mPrinter.printText(" FFB Grading Receipt" + "\n");
        mPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_LEFT);
        mPrinter.setCharacterMultiple(0, 0);
        mPrinter.setLeftMargin(15, 15);
        mPrinter.printText("Duplicate Copy" + "\n");
        sb.append("==============================================" + "\n");

        sb.append(" ");
        sb.append(" Token Number : ").append(selectedReport.getTokenNumber()).append("\n");
        sb.append(" ");
        sb.append(" CCCode : ").append(selectedReport.getCCCode()).append("\n");
        sb.append(" ");
        sb.append(" Fruit Type : ").append(fruitType).append("\n");
        sb.append(" ");
        sb.append(" Gross Weight(Kgs) : ").append(selectedReport.getGrossWeight()).append("\n");
        sb.append(" ");
        sb.append(" Grading Date : ").append(requiredvalue + "").append("\n");



//        sb.append(" ");
//        sb.append("-----------------------------------------------\n");
//        sb.append("  FFB Quality Details" + "\n");
//        sb.append("-----------------------------------------------\n");
//
//        if (selectedReport.getUnRipen() != 0) {
//            sb.append(" ");
//            sb.append(" Unripen : ").append(selectedReport.getUnRipen() + "%").append("\n");
//        }
//        if (selectedReport.getUnderRipe() != 0) {
//            sb.append(" ");
//            sb.append(" Under Ripe : ").append(selectedReport.getUnderRipe() + "%").append("\n");
//        }
//        if (selectedReport.getRipen() != 0) {
//            sb.append(" ");
//            sb.append(" Ripen : ").append(selectedReport.getRipen() + "%").append("\n");
//        }
//        if (selectedReport.getOverRipe() != 0) {
//            sb.append(" ");
//            sb.append(" Over Ripe : ").append(selectedReport.getOverRipe() + "%").append("\n");
//        }
//        if (selectedReport.getDiseased() != 0) {
//            sb.append(" ");
//            sb.append(" Diseased : ").append(selectedReport.getDiseased() + "%").append("\n");
//        }
//        if (selectedReport.getEmptyBunches() != 0) {
//            sb.append(" ");
//            sb.append(" Empty Bunch's : ").append(selectedReport.getEmptyBunches() + "%").append("\n");
//        }
//
//        sb.append(" ");
//        sb.append("-----------------------------------------------\n");
//        sb.append("  Stalk Quality Details" + "\n");
//        sb.append("-----------------------------------------------\n");
//
//        if (selectedReport.getFFBQualityLong() != 0) {
//            sb.append(" ");
//            sb.append(" Long : ").append(selectedReport.getFFBQualityLong() + "%").append("\n");
//        }
//        if (selectedReport.getFFBQualityMedium() != 0) {
//            sb.append(" ");
//            sb.append(" Medium : ").append(selectedReport.getFFBQualityMedium() + "%").append("\n");
//        }
//        if (selectedReport.getFFBQualityShort() != 0) {
//            sb.append(" ");
//            sb.append(" Short : ").append(selectedReport.getFFBQualityShort() + "%").append("\n");
//        }
//        if (selectedReport.getFFBQualityOptimum() != 0) {
//            sb.append(" ");
//            sb.append(" Optimum : ").append(selectedReport.getFFBQualityOptimum() + "%").append("\n");
//        }
//
//        sb.append(" ");
//        sb.append("-----------------------------------------------\n");
//
//        if (!TextUtils.isEmpty(selectedReport.getLooseFruitWeight())){
//            sb.append(" ");
//            sb.append(" Loose Fruit Approx.Quantity : ").append(selectedReport.getLooseFruitWeight() + "(Kgs)").append("\n");
//        }
//        if (!TextUtils.isEmpty(selectedReport.getRejectedBunches())){
//
//            sb.append(" ");
//            sb.append(" Rejected Bunches : ").append(selectedReport.getRejectedBunches() + "").append("\n");
//        }
//
//        sb.append(" ");
//        sb.append(" Grader Name : ").append(selectedReport.getGraderName()).append("\n");
//
//
//        sb.append(" ");
//        sb.append("\n");
//        sb.append(" ");
//        sb.append("\n");
//        sb.append(" ");
//        sb.append("\n");
//        sb.append(" ");
//        sb.append("\n");
//        sb.append(" Ramp Incharge/Grader signature");
//        sb.append(" ");
//        sb.append("\n");
//        sb.append(" ");
//        sb.append("\n");
//        sb.append(" ");
//        sb.append("\n");
//        sb.append(" ");
//        sb.append("\n");
//        sb.append(" Farmer signature");
//        sb.append(" ");
//        sb.append("\n");
//        sb.append(" ");

        mPrinter.printText(sb.toString());

        String tokenCount = selectedReport.getTokenNumber().substring(21);
        int tokennum = Integer.parseInt(tokenCount);
        android.util.Log.d("tokenCount", tokenCount);
        String formattedToken = String.valueOf(tokennum);
        android.util.Log.d("formattedToken", formattedToken);
        String finaltoken = formattedToken;
        Log.d("tokenCount", tokenCount + "");

        boolean fruitavailable;

        if( selectedReport.getLooseFruit().equalsIgnoreCase("1")){

            fruitavailable = true;
        }else {
            fruitavailable = false;
        }

//        String fruightweight;
//
//        if(TextUtils.isEmpty(selectedReport.getLooseFruitWeight())){
//            fruightweight = "null";
//        }else{
//            fruightweight = selectedReport.getLooseFruitWeight();
//        }

        String fruightweight;
        String rejectedbunches;

        if(TextUtils.isEmpty(selectedReport.getLooseFruitWeight())){
            fruightweight = "0";
        }else{
            fruightweight = selectedReport.getLooseFruitWeight();
        }

        if(TextUtils.isEmpty(selectedReport.getRejectedBunches())){
            rejectedbunches = "0";
        }else{
            rejectedbunches = selectedReport.getRejectedBunches();
        }

        String hashString = selectedReport.getTokenNumber()+"/"+selectedReport.getCCCode()+"/"+fruitTypeNumber+"/"+selectedReport.getGrossWeight()+"/"+selectedReport.getVehicleNumber()+"/"+selectedReport.getLooseFruitorBunches()+"/"+
                selectedReport.getCreatedDatewithtime()+"/"+ selectedReport.getUnRipen()+"/"+selectedReport.getUnderRipe()
                +"/"+selectedReport.getRipen()+"/"+selectedReport.getOverRipe()+"/"+selectedReport.getDiseased()+"/"+selectedReport.getEmptyBunches()+"/"
                +selectedReport.getFFBQualityLong()+"/"+selectedReport.getFFBQualityMedium()+"/"+selectedReport.getFFBQualityShort()+"/"+
                selectedReport.getFFBQualityOptimum()+"/"+fruitavailable+"/"+fruightweight+"/"+rejectedbunches+
                "/"+selectedReport.getGraderName();

        String qrCodeValue = hashString;
        android.util.Log.d("qrCodeValueis", qrCodeValue  + "");
        Barcode barcode = new Barcode(PrinterConstants.BarcodeType.QRCODE, 3, 95, 3, qrCodeValue);

        mPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER);
        mPrinter.setCharacterMultiple(0, 1);

        String space = "-----------------------------------------------";
        String tokenNumber  =  "Token Number";
        String spaceBuilderr = "\n";

        mPrinter.printText(space);
        mPrinter.printText(spaceBuilderr);
        mPrinter.printText(tokenNumber + " - " + finaltoken);
        mPrinter.printText(spaceBuilderr);
        mPrinter.printText(space);
        mPrinter.printText(spaceBuilderr);

//        if(CommonConstants.PrinterName.contains("AMIGOS")){
//            android.util.Log.d(LOG_TAG,"########### NEW ##############");
//            print_qr_code(mPrinter,qrCodeValue);
//        }else{
//            android.util.Log.d(LOG_TAG,"########### OLD ##############");
//            mPrinter.printBarCode(barcode);
//        }

        if(CommonConstants.PrinterName.contains("G-8BT3 AMIGOS")){
            android.util.Log.d(LOG_TAG,"########### NEW ##############");
            //mPrinter.setPrintModel(false,true,true,false);
            print_qr_codee(mPrinter,qrCodeValue);
        }else if (CommonConstants.PrinterName.contains("AMIGOS")){
            print_qr_code(mPrinter,qrCodeValue);
        }else{
            android.util.Log.d(LOG_TAG,"########### OLD ##############");
            mPrinter.printBarCode(barcode);
        }

        mPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER);
        mPrinter.setCharacterMultiple(0, 1);
       // mPrinter.printText(qrCodeValue);

        String spaceBuilder = "\n" +
                " " ;
        mPrinter.printText(spaceBuilder);

        boolean printSuccess = false;
        try {
            mPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);
            printSuccess = true;
        } catch (Exception e) {
            android.util.Log.v(LOG_TAG, "@@@ printing failed " + e.getMessage());
            UiUtils.showCustomToastMessage("Printing failes due to " + e.getMessage(), GradingReportActivity.this, 1);
            printSuccess = false;
        } finally {
            if (printSuccess) {
                Toast.makeText(GradingReportActivity.this, "Print Success", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

    }



    public static String convertDateFormat(String inputDate) {
        String outputDate = null;

        try {
            // Input format
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            Date date = inputFormat.parse(inputDate);

            // Output format
            //SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

            outputDate = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            // Handle parsing exception if needed
        }

        return outputDate;
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


        byte[] sizeQR = {(byte)0x1d, (byte)0x28, (byte)0x6b, (byte)0x03, (byte)0x00, (byte)0x31, (byte)0x43, (byte)0x08};


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

    private void gradingsearchreportslist(final int offset) {
        //ProgressBar.showProgressBar(this, "Please wait...");

        if (searchprogress != null) {
            searchprogress.setVisibility(View.VISIBLE);
        }
        ApplicationThread.bgndPost(LOG_TAG, "notvisitedplots", new Runnable() {
            @Override
            public void run() {

                mReportsList = (List<GradingReportModel>) dataAccessHandler.getGradingList(Queries.getInstance().getGradingReports(LIMIT, offset,fromDateStr,toDateStr, searchKey), 1);
                Collections.reverse(mReportsList);

                ApplicationThread.uiPost(LOG_TAG, "", new Runnable() {
                    @Override
                    public void run() {
                        //ProgressBar.hideProgressBar();
                        searchprogress.setVisibility(View.GONE);
                        gradingReportRecyclerAdapter = new GradingReportAdapter(GradingReportActivity.this, mReportsList);
                        if (mReportsList != null && !mReportsList.isEmpty()) {
                            gradingReportsList.setVisibility(View.VISIBLE);
                            tvNorecords.setVisibility(View.GONE);
                            layoutManager = new LinearLayoutManager(GradingReportActivity.this, LinearLayoutManager.VERTICAL, false);
                            gradingReportsList.setLayoutManager(layoutManager);

                            gradingReportsList.setAdapter(gradingReportRecyclerAdapter);
                            gradingReportRecyclerAdapter.setonPrintSelected((GradingReportActivity.this));
                            //   setTitle(alert_type, offset == 0 ? alertsVisitsInfoList.size() : offset);
                        }
                        else{
                            gradingReportsList.setVisibility(View.GONE);
                            tvNorecords.setVisibility(View.VISIBLE);
                        }
                    }
                });

            }
        });
    }
}