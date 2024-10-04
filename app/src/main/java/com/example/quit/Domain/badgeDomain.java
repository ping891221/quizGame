package com.example.quit.Domain;

public class badgeDomain {
    private Integer goldNumber;
    private Integer silverNumber;
    private Integer copperNumber;
    private String goldImage;
    private String silverImage;
    private String copperImage;
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

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getGoldImage() {
        return goldImage;
    }

    public void setGoldImage(String goldImage) {
        this.goldImage = goldImage;
    }

    public String getSilverImage() {
        return silverImage;
    }

    public void setSilverImage(String silverImage) {
        this.silverImage = silverImage;
    }

    public String getCopperImage() {
        return copperImage;
    }

    public void setCopperImage(String copperImage) {
        this.copperImage = copperImage;
    }

    public badgeDomain(Integer goldNumber, Integer silverNumber, Integer copperNumber, String goldImage, String silverImage, String copperImage, String describe) {
        this.goldNumber = goldNumber;
        this.silverNumber = silverNumber;
        this.copperNumber = copperNumber;
        this.goldImage = goldImage;
        this.silverImage = silverImage;
        this.copperImage = copperImage;
        this.describe = describe;
    }


}
