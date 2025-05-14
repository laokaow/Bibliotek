package model;

import java.time.LocalDate;

public class Loan {
    private final int loanId;
    private final User user; //Skapar referens till Userobjekt. Detta ger mer flexibilitet för programmet
    private final Copy copy; //Samma som ovanstående
    private final int receiptId;
    private final LocalDate borrowDate;
    private LocalDate returnDate; //Det faktiska återlämningsdatumet

    public Loan(int loanId, User user, Copy copy, int receiptId, LocalDate borrowDate, LocalDate returnDate) {
        this.loanId = loanId;
        this.user = user;
        this.copy = copy;
        this.receiptId = receiptId;
        this.borrowDate = borrowDate; //Lägga till validering e.g. if(borrowDate.isAfter(returnDate)){throw new IllegalArgumentException("")}
        this.returnDate = returnDate;
    }
    public int getLoanId(){
        return loanId;
    }
    public User getUser(){
        return user;
    }
    public Copy getCopy(){
        return copy;
    }
    public int getReceiptId(){
        return receiptId;
    }
    public LocalDate getBorrowDate(){
        return borrowDate;
    }
    public LocalDate getReturnDate(){
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate){
        this.returnDate = returnDate;
    }
}