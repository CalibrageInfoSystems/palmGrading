package com.oilpalm3f.gradingapp.dbmodels;

public class GradingReportModel {

     private int Id;
     private String TokenNumber;
     private String CCCode;
    private String FruitType;
    private String GrossWeight;
    private String TokenDate;
    private int UnRipen;
    private int UnderRipe;
    private  int Ripen;
    private int OverRipe;
    private int Diseased;
    private int  EmptyBunches;
    private int FFBQualityLong;
    private int FFBQualityMedium;
    private int FFBQualityShort;
    private int FFBQualityOptimum;
    private String LooseFruit;
    private String LooseFruitWeight;
    private String GraderName;



    private String RejectedBunches;
    private boolean expanded;

    private String CreatedDate;
    private String CreatedDatewithtime;

    private String VehicleNumber;
    private String GatePassCode;

    private String looseFruitorBunches;


    public String getLooseFruitorBunches() {
        return looseFruitorBunches;
    }

    public void setLooseFruitorBunches(String looseFruitorBunches) {
        this.looseFruitorBunches = looseFruitorBunches;
    }

    public String getGatePassCode() {
        return GatePassCode;
    }

    public void setGatePassCode(String gatePassCode) {
        GatePassCode = gatePassCode;
    }

    public String getVehicleNumber() {
        return VehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        VehicleNumber = vehicleNumber;
    }

    public String getCreatedDatewithtime() {
        return CreatedDatewithtime;
    }

    public void setCreatedDatewithtime(String createdDatewithtime) {
        CreatedDatewithtime = createdDatewithtime;
    }

    public String getRejectedBunches() {
        return RejectedBunches;
    }

    public void setRejectedBunches(String rejectedBunches) {
        RejectedBunches = rejectedBunches;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;

    }

    public String getCreatedDate() {
        return CreatedDate;
    }

    public void setCreatedDate(String createdDate) {
        CreatedDate = createdDate;

    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getTokenNumber() {
        return TokenNumber;
    }

    public void setTokenNumber(String tokenNumber) {
        TokenNumber = tokenNumber;
    }

    public String getCCCode() {
        return CCCode;
    }

    public void setCCCode(String CCCode) {
        this.CCCode = CCCode;
    }

    public String getFruitType() {
        return FruitType;
    }

    public void setFruitType(String fruitType) {
        FruitType = fruitType;
    }

    public String getGrossWeight() {
        return GrossWeight;
    }

    public void setGrossWeight(String grossWeight) {
        GrossWeight = grossWeight;
    }

    public String getTokenDate() {
        return TokenDate;
    }

    public void setTokenDate(String tokenDate) {
        TokenDate = tokenDate;
    }

    public int getUnRipen() {
        return UnRipen;
    }

    public void setUnRipen(int unRipen) {
        UnRipen = unRipen;
    }

    public int getUnderRipe() {
        return UnderRipe;
    }

    public void setUnderRipe(int underRipe) {
        UnderRipe = underRipe;
    }

    public int getDiseased() {
        return Diseased;
    }

    public void setDiseased(int diseased) {
        Diseased = diseased;
    }

    public int getEmptyBunches() {
        return EmptyBunches;
    }

    public void setEmptyBunches(int emptyBunches) {
        EmptyBunches = emptyBunches;
    }

    public int getFFBQualityLong() {
        return FFBQualityLong;
    }

    public void setFFBQualityLong(int FFBQualityLong) {
        this.FFBQualityLong = FFBQualityLong;
    }

    public int getFFBQualityMedium() {
        return FFBQualityMedium;
    }

    public void setFFBQualityMedium(int FFBQualityMedium) {
        this.FFBQualityMedium = FFBQualityMedium;
    }

    public int getFFBQualityShort() {
        return FFBQualityShort;
    }

    public void setFFBQualityShort(int FFBQualityShort) {
        this.FFBQualityShort = FFBQualityShort;
    }

    public int getFFBQualityOptimum() {
        return FFBQualityOptimum;
    }

    public void setFFBQualityOptimum(int FFBQualityOptimum) {
        this.FFBQualityOptimum = FFBQualityOptimum;
    }

    public String getLooseFruit() {
        return LooseFruit;
    }

    public void setLooseFruit(String looseFruit) {
        LooseFruit = looseFruit;
    }

    public String getLooseFruitWeight() {
        return LooseFruitWeight;
    }

    public void setLooseFruitWeight(String looseFruitWeight) {
        LooseFruitWeight = looseFruitWeight;
    }

    public String getGraderName() {
        return GraderName;
    }

    public void setGraderName(String graderName) {
        GraderName = graderName;
    }



    public int getRipen() {
        return Ripen;
    }

    public void setRipen(int ripen) {
        Ripen = ripen;
    }

    public int getOverRipe() {
        return OverRipe;
    }

    public void setOverRipe(int overRipe) {
        OverRipe = overRipe;
    }



}
