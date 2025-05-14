package model;

import java.time.LocalDateTime;

public class Receipt {
    private final int receiptId;
    private final User user;
    private final String info;
    private final LocalDateTime receiptDate;

    public Receipt(int receiptId, User user, String info, LocalDateTime receiptDate) {
        this.receiptId = receiptId;
        this.user = user;
        this.info = info;
        this.receiptDate = receiptDate;
    }

    public int getReceiptId(){
        return receiptId;
    }
    public User getUser(){
        return user;
    }
    public String getInfo(){
        return info;
    }
    public LocalDateTime getReceiptDate(){
        return receiptDate;
    }
}