package com.oilpalm3f.gradingapp.dbmodels;

public class GatepassTokenListModel {

    private String GatePassTokenCode;
    private String VehicleNumber;
    private String GatePassSerialNumber;
    private String IsCollection;
    private String CreatedDate;

    private int MillLocationTypeId;


    private String MillLocation;


    public int getMillLocationTypeId() {
        return MillLocationTypeId;
    }

    public void setMillLocationTypeId(int millLocationTypeId) {
        MillLocationTypeId = millLocationTypeId;
    }

    public String getMillLocation() {
        return MillLocation;
    }

    public void setMillLocation(String millLocation) {
        MillLocation = millLocation;
    }

    private String CreatedBy;


    public String getCreatedBy() {
        return CreatedBy;
    }

    public void setCreatedBy(String createdBy) {
        CreatedBy = createdBy;
    }

    public String getGatePassTokenCode() {
        return GatePassTokenCode;
    }

    public void setGatePassTokenCode(String gatePassTokenCode) {
        GatePassTokenCode = gatePassTokenCode;
    }

    public String getVehicleNumber() {
        return VehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        VehicleNumber = vehicleNumber;
    }

    public String getGatePassSerialNumber() {
        return GatePassSerialNumber;
    }

    public void setGatePassSerialNumber(String gatePassSerialNumber) {
        GatePassSerialNumber = gatePassSerialNumber;
    }

    public String getIsCollection() {
        return IsCollection;
    }

    public void setIsCollection(String isCollection) {
        IsCollection = isCollection;
    }

    public String getCreatedDate() {
        return CreatedDate;
    }

    public void setCreatedDate(String createdDate) {
        CreatedDate = createdDate;
    }
}
