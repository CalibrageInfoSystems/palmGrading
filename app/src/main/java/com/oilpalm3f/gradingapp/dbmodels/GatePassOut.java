package com.oilpalm3f.gradingapp.dbmodels;

public class GatePassOut {

    private int Id;
    private String GatePassCode;
    private int CreatedByUserId;
    private String CreatedDate;
    private String GatePassInTime;
    private int ServerUpdatedStatus;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getGatePassCode() {
        return GatePassCode;
    }

    public void setGatePassCode(String gatePassCode) {
        GatePassCode = gatePassCode;
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

    public String getGatePassInTime() {
        return GatePassInTime;
    }

    public void setGatePassInTime(String gatePassInTime) {
        GatePassInTime = gatePassInTime;
    }

    public int getServerUpdatedStatus() {
        return ServerUpdatedStatus;
    }

    public void setServerUpdatedStatus(int serverUpdatedStatus) {
        ServerUpdatedStatus = serverUpdatedStatus;
    }
}
