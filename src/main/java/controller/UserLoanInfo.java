package controller;

import java.time.LocalDate;

public class UserLoanInfo {
    private final String mediaTitle;
    private final LocalDate dueDate;
    private final String status;

    public UserLoanInfo(String mediaTitle, LocalDate dueDate, String status) {
        this.mediaTitle = mediaTitle;
        this.dueDate = dueDate;
        this.status = status;
    }

    public String getMediaTitle() {
        return mediaTitle;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public String getStatus() {
        return status;
    }
}