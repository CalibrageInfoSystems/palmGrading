package com.oilpalm3f.gradingapp.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.oilpalm3f.gradingapp.R;
import com.oilpalm3f.gradingapp.cloudhelper.Log;
import com.oilpalm3f.gradingapp.database.DataAccessHandler;
import com.oilpalm3f.gradingapp.dbmodels.GatepassInListModel;
import com.oilpalm3f.gradingapp.dbmodels.GatepassTokenListModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class GatepassinlistAdapter extends RecyclerView.Adapter<GatepassinlistAdapter.GatepassinReportViewHolder> {

    private static final String LOG_TAG = GatepassinlistAdapter.class.getName();
    private List<GatepassInListModel> mList;
    private Context context;
    private GatepassInListModel item;
    private DataAccessHandler dataAccessHandler = null;
    private ongatepassinprintselected ongatepassinPrintSelected;

    SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat output = new SimpleDateFormat("dd-MM-yyyy");
    int row_index = -1;
    LayoutInflater mInflater;

    public GatepassinlistAdapter(Context context, List<GatepassInListModel> mList) {
        this.context = context;
        this.mList = mList;
        dataAccessHandler = new DataAccessHandler(context);
    }


    @NonNull
    @Override
    public GatepassinReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.gatepassin_item, null);
         GatepassinReportViewHolder myHolder = new GatepassinReportViewHolder(view);
        return myHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull GatepassinReportViewHolder holder, int position) {

        item = mList.get(position);
        Log.d("Size", mList.size() + "");

        Log.d("getGatepasscode", mList.get(position).getGatePassCode() + "");
        Log.d("getFruitType", mList.get(position).getFruitType() + "");

        holder.tvgatepasstokencode.setText(mList.get(position).getGatePassCode() + "");
        holder.tvgatepasstokennumber.setText(mList.get(position).getGatePassTokenCode() + "");
        holder.tvgatepassindate.setText(mList.get(position).getCreatedDate() + "");
        holder.tvwbcode.setText(mList.get(position).getWBCode() + "");
        holder.tvvehicletype.setText(mList.get(position).getVehicleType() + "");
        holder.tvmilllocation.setText(mList.get(position).getMillLocation() + "");
        holder.tvvehiclenumber.setText(mList.get(position).getVehicleNumber() + "");

//        String datetime = mList.get(position).getGatePassCode().substring(0, 14);
//        String serialNumber = mList.get(position).getGatePassCode().substring(14, 18);
//
//        Log.d("datetime", datetime + "");
//        Log.d("serialNumber", serialNumber + "");


        String printfruitType;

        Log.d("Collection", mList.get(position).getFruitType() + "");

        if ("0".equals(mList.get(position).getFruitType())) {
            printfruitType = "true";
        } else {
            printfruitType = "false";
        }


        String gpcvalue = mList.get(position).getGatePassCode();
        Log.d("gpcvalue", gpcvalue + "");
        String lastFourChars = gpcvalue.substring(gpcvalue.length() - 4);
        Log.d("lastFourChars", lastFourChars + "");


        String qrCodeValue = mList.get(position).getGatePassCode() +"/" +lastFourChars +"/" +printfruitType+"/" + mList.get(position).getVehicleNumber()
        + "/" +mList.get(position).getVehicleCategoryId()+"/" +mList.get(position).getVehicleTypeId()+"/" + mList.get(position).getWBID();

        holder.tvgatepasstokencode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(context,qrCodeValue);

            }
        });

        holder.printBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("Method1", "into this");

                if (null != ongatepassinPrintSelected) {

                    Log.d("Method2", "into this");

                    ongatepassinPrintSelected.gatepassinprinOptionSelected(position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void updateAdapter(List<GatepassInListModel> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    public void setonGatepassinPrintSelected(final ongatepassinprintselected ongatepassinPrintSelected) {
        this.ongatepassinPrintSelected = ongatepassinPrintSelected;
    }

    public class GatepassinReportViewHolder extends RecyclerView.ViewHolder {


        private TextView tvgatepasstokencode, tvgatepasstokennumber, tvisvehicleout, tvgatepassindate, tvwbcode, tvvehicletype, tvmilllocation, tvvehiclenumber;
        private ImageView printBtn;

        public GatepassinReportViewHolder(@NonNull View itemView) {
            super(itemView);

            tvgatepasstokencode = (TextView) itemView.findViewById(R.id.tvgatepasstokencode);
            tvgatepasstokennumber = (TextView) itemView.findViewById(R.id.tvgatepasstokennumber);
            tvgatepassindate = (TextView) itemView.findViewById(R.id.tvgatepassindate);
            tvwbcode = (TextView) itemView.findViewById(R.id.tvwbcode);
            tvvehicletype = (TextView) itemView.findViewById(R.id.tvvehicletype);
            tvmilllocation = (TextView) itemView.findViewById(R.id.tvmilllocation);
            tvvehiclenumber = (TextView) itemView.findViewById(R.id.tvvehiclenumber);


            printBtn = (ImageView) itemView.findViewById(R.id.printBtn);


        }
    }

    public void showDialog(Context context, String imgString) {
        final Dialog dialog = new Dialog(context, R.style.DialogSlideAnim);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialogg);
        ImageView img = dialog.findViewById(R.id.imageView);
        TextView cancel =dialog.findViewById(R.id.cancel);

        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(imgString, BarcodeFormat.QR_CODE, 256, 256);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            img.setImageBitmap(bmp);

        } catch (WriterException e) {
            e.printStackTrace();
        }

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
