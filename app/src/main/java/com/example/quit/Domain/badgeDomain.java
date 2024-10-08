package com.example.quit.Domain;

public class badgeDomain {
    private Integer goldNumber;
    private Integer silverNumber;
    private Integer copperNumber;
    private String title;
    private String describe;

    public Integer getGoldNumber() {
        return goldNumber;
    }

    public void setGoldNumber(Integer goldNumber) {
        this.goldNumber = goldNumber;
    }

    public Integer getSilverNumber() {
        return silverNumber;
    }

    public void setSilverNumber(Integer silverNumber) {
        this.silverNumber = silverNumber;
    }

    public Integer getCopperNumber() {
        return copperNumber;
    }

    public void setCopperNumber(Integer copperNumber) {
        this.copperNumber = copperNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }



    public badgeDomain(Integer goldNumber, Integer silverNumber, Integer copperNumber, String title, String describe) {
        this.goldNumber = goldNumber;
        this.silverNumber = silverNumber;
        this.copperNumber = copperNumber;
        this.title = title;
        this.describe = describe;
    }



}
