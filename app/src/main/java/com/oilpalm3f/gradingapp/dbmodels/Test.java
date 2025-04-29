package com.oilpalm3f.gradingapp.dbmodels;

public class Test {
    private String name;

    public Test(String name, int code, String gender) {
        this.name = name;
        this.code = code;
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    private int code;
    private String gender;
}
