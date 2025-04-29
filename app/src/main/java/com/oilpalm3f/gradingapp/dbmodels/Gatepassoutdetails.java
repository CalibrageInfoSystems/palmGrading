package com.oilpalm3f.gradingapp.dbmodels;

public class Gatepassoutdetails {
    private String GatePassSerialNumber;
    private String VehicleNumber;
    private String  CreatedDate;

    public String getGatePassSerialNumber() {
        return GatePassSerialNumber;
    }

    public void setGatePassSerialNumber(String gatePassSerialNumber) {
        GatePassSerialNumber = gatePassSerialNumber;
    }

    public String getVehicleNumber() {
        return VehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        VehicleNumber = vehicleNumber;
    }

    public String getCreatedDate() {
        return CreatedDate;
    }

    public void setCreatedDate(String createdDate) {
        CreatedDate = createdDate;
    }
}
