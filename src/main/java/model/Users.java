package model;

public class Users {
    public enum UserType
    {STUDENT, TEACHER, RESEARCHER, PUBLIC, STAFF}

    private int userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String pinCode;
    private UserType userType;


    public Users( int userId, String firstName, String lastName, String email, String phoneNumber, String pinCode, UserType userType){
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.pinCode = pinCode;
        this.userType = userType;
    }
    public int getUserId(){
        return userId;
    }
    public String getFirstName(){
        return firstName;
    }
    public String getLastName(){
        return lastName;
    }
    public String getEmail() {
        return email;
    }
    public String getPhoneNumber() {
            return phoneNumber;
        }
    public String getPinCode(){
        return pinCode;
    }
    public UserType getUserType(){
        return userType;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public void setPhoneNumber(String phoneNumber ){
        this.phoneNumber = phoneNumber;
    }
    public void setPinCode(String pinCode){
        this.pinCode = pinCode;
    }
    public void setUserType(UserType userType){
        this.userType = userType;
    }

}
