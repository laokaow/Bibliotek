package util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    //Metod för att hasha lösenordet
    public static String hashPassword(String password){
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
    //Metod för att verifiera att lösenordet matchar hashvärdet
    public static boolean checkPassword(String password, String hashedPassword){
        return BCrypt.checkpw(password, hashedPassword);
    }

}
