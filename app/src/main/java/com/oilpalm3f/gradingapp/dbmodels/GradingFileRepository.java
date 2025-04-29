package com.oilpalm3f.gradingapp.dbmodels;

public class GradingFileRepository {

    private int Id;
    private String ImageString;
    private String TokenNumber;
    private String CCCode;
    private int FruitType;
    private Double GrossWeight;
    private String FileName;
    private String FileLocation;
    private String FileExtension;
    private int CreatedByUserId;
    private String CreatedDate;
    private int ServerUpdatedStatus;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getImageString() {
        return ImageString;
    }

    public void setImageString(String imageString) {
        ImageString = imageString;
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

    public int getFruitType() {
        return FruitType;
    }

    public void setFruitType(int fruitType) {
        FruitType = fruitType;
    }

    public Double getGrossWeight() {
        return GrossWeight;
    }

    public void setGrossWeight(Double grossWeight) {
        GrossWeight = grossWeight;
    }

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public String getFileLocation() {
        return FileLocation;
    }

    public void setFileLocation(String fileLocation) {
        FileLocation = fileLocation;
    }

    public String getFileExtension() {
        return FileExtension;
    }

    public void setFileExtension(String fileExtension) {
        FileExtension = fileExtension;
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
}
