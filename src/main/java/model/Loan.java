package model;

import java.time.LocalDate;

public class Loan {
    private final int loanId;
    private final User user; //Skapar referens till Userobjekt. Detta ger mer flexibilitet för programmet
    private final Copy copy; //Samma som ovanstående
    private final int receiptId;
    private final LocalDate borrowDate;
    private LocalDate returnDate; //Det faktiska återlämningsdatumet
    private LocalDate dueDate;

    //Overloaded konstruktor. Detta för att kunna skapa lånvy utan att behöva ha Receipt med. Sätter -1 på receiptId för NULL
    public Loan(int loanId, User user, Copy copy,  LocalDate borrowDate, LocalDate returnDate, LocalDate dueDate){
        this(loanId, user, copy, -1, borrowDate, returnDate, dueDate);
    }

    public Loan(int loanId, User user, Copy copy, int receiptId, LocalDate borrowDate, LocalDate returnDate, LocalDate dueDate) {
        this.loanId = loanId;
        this.user = user;
        this.copy = copy;
        this.receiptId = receiptId;
        this.borrowDate = borrowDate; //Lägga till validering e.g. if(borrowDate.isAfter(returnDate)){throw new IllegalArgumentException("")}
        this.returnDate = returnDate;
        this.dueDate = dueDate; //Lade till dueDate för att kunna visa detta i applikationen
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

    public LocalDate getDueDate() {
        return dueDate;
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