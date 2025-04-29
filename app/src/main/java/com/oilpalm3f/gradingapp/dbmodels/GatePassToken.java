package com.oilpalm3f.gradingapp.dbmodels;

public class GatePassToken {
    private int Id;

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

    public int getIsCollection() {
        return IsCollection;
    }

    public void setIsCollection(int isCollection) {
        IsCollection = isCollection;
    }

    public int getCreatedByUserId() {
        return CreatedByUserId;
    }

    public void setCreatedByUserId(int createdByUserId) {
        CreatedByUserId = createdByUserId;
    }

    public String getCreatedDate() {
        return CreatedDate;
    }

    public void setCreatedDate(String createdDate) {
        CreatedDate = createdDate;
    }

    public int getServerUpdatedStatus() {
        return ServerUpdatedStatus;
    }

    public void setServerUpdatedStatus(int serverUpdatedStatus) {
        ServerUpdatedStatus = serverUpdatedStatus;
    }

    private String GatePassTokenCode;
    private String VehicleNumber;
    private int GatePassSerialNumber;

    public int getGatePassSerialNumber() {
        return GatePassSerialNumber;
    }

    public void setGatePassSerialNumber(int gatePassSerialNumber) {
        GatePassSerialNumber = gatePassSerialNumber;
    }

    private int IsCollection;
    private int CreatedByUserId;
    private String CreatedDate;
    private int ServerUpdatedStatus;

    private int MillLocationTypeId;

    public int getLocationId() {
        return MillLocationTypeId ;
    }

    public void setLocationId(int locationId) {
        MillLocationTypeId  = locationId;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }
}
