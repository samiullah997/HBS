package com.example.hbs.Model;

public class Data {
    private int amount;
    private String type;
    private String note;
    private String id;
    private String date;
    private String documentId;
    private String dataType;
    private String dataStatus;


    public Data() {
    }

    public Data(String id,  String dataType) {
        this.id = id;

        this.dataType = dataType;
    }

    public Data(int amount, String type, String note, String id, String date, String documentId, String dataType,String dataStatus) {
        this.amount = amount;
        this.type = type;
        this.note = note;
        this.id = id;
        this.date = date;
        this.documentId = documentId;
        this.dataType = dataType;
        this.dataStatus=dataStatus;

    }

    public Data(int amount, String type, String note, String id, String date, String documentId) {
        this.amount = amount;
        this.type = type;
        this.note = note;
        this.id = id;
        this.date = date;
        this.documentId=documentId;
    }

    public String getDataStatus() {
        return dataStatus;
    }

    public void setDataStatus(String dataStatus) {
        this.dataStatus = dataStatus;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }


    @Override
    public String toString() {
        return "Data{" +
                "amount=" + amount +
                ", type='" + type + '\'' +
                ", note='" + note + '\'' +
                ", id='" + id + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
