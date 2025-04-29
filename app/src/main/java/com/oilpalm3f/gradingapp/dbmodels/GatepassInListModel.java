package com.oilpalm3f.gradingapp.dbmodels;

public class GatepassInListModel {


    private String GatePassCode;
    private String GatePassTokenCode;
    private String CreatedDate;
    private String WBCode;
    private String VehicleType;
    private String WBID;
    private String WBName;
    private String VehicleCategory;
    private String VehicleNumber;

    private String VehicleTypeId;

    private String VehicleCategoryId;


    private int MillLocationTypeId;
    private String MillLocation;
    private String SerialNumber;
    private String FruitType;

    private String CreatedBy;

    public String getCreatedBy() {
        return CreatedBy;
    }

    public void setCreatedBy(String createdBy) {
        CreatedBy = createdBy;
    }

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

    public String getSerialNumber() {
        return SerialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        SerialNumber = serialNumber;
    }

    public String getFruitType() {
        return FruitType;
    }

    public void setFruitType(String fruitType) {
        FruitType = fruitType;
    }

    public String getVehicleTypeId() {
        return VehicleTypeId;
    }

    public void setVehicleTypeId(String vehicleTypeId) {
        VehicleTypeId = vehicleTypeId;
    }

    public String getVehicleCategoryId() {
        return VehicleCategoryId;
    }

    public void setVehicleCategoryId(String vehicleCategoryId) {
        VehicleCategoryId = vehicleCategoryId;
    }

    public String getVehicleNumber() {
        return VehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        VehicleNumber = vehicleNumber;
    }

    public String getGatePassCode() {
        return GatePassCode;
    }

    public void setGatePassCode(String gatePassCode) {
        GatePassCode = gatePassCode;
    }

    public String getGatePassTokenCode() {
        return GatePassTokenCode;
    }

    public void setGatePassTokenCode(String gatePassTokenCode) {
        GatePassTokenCode = gatePassTokenCode;
    }
    public String getCreatedDate() {
        return CreatedDate;
    }

    public void setCreatedDate(String createdDate) {
        CreatedDate = createdDate;
    }

    public String getWBCode() {
        return WBCode;
    }

    public void setWBCode(String WBCode) {
        this.WBCode = WBCode;
    }

    public String getVehicleType() {
        return VehicleType;
    }

    public void setVehicleType(String vehicleType) {
        VehicleType = vehicleType;
    }

    public String getWBID() {
        return WBID;
    }

    public void setWBID(String WBID) {
        this.WBID = WBID;
    }

    public String getVehicleCategory() {
        return VehicleCategory;
    }

    public void setVehicleCategory(String vehicleCategory) {
        VehicleCategory = vehicleCategory;
    }

    public String getWBName() {
        return WBName;
    }

    public void setWBName(String WBName) {
        this.WBName = WBName;
    }
}
