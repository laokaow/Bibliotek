package model;

import java.time.LocalDateTime;

//Klass som ej används. Kommer att användas ifall ytterligare iterationer görs

public class ReminderLog {
    private final int reminderId;
    private final User user;
    private final String email;
    private final String message;
    private final LocalDateTime reminderDate;
    private boolean sent;

    public ReminderLog(int reminderId, User user, String email, String message, LocalDateTime reminderDate, boolean sent) {
        this.reminderId = reminderId;
        this.user = user;
        this.email = email;
        this.message = message;
        this.reminderDate = reminderDate;
        this.sent = sent;

    }

    public int getReminderId(){
        return reminderId;
    }
    public User getUser(){
        return user;
    }
    public String getEmail(){
        return email;
    }
    public String getMessage(){
        return message;
    }
    public LocalDateTime getReminderDate(){
        return reminderDate;
    }
    public boolean getSent(){
        return sent;
    }
    public void setSent(boolean sent){
        this.sent = sent;
    }

}