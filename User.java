import java.io.*;
import java.util.ArrayList;

public class User {


    private static int VALID = 0;
    private static int TOO_LONG_ERROR = 1;
    private static int INVALID_CHARACTERS_ERROR = 2;
    private static int TOO_SHORT_ERROR = 3;
    private static int A_ASCII = 65;
    private static int Z_ASCII = 90;
    private static int a_ASCII = 97;
    private static int z_ASCII = 122;
    private static int Zero_ASCII = 48;
    private static int Nine_ASCII = 57;

    private String username;
    private String password;

    public User(String username, String password){

        int username_validity = CheckUsername(username);
        int password_validity = CheckPassword(password);

        if(username_validity == TOO_LONG_ERROR){
            throw new IllegalArgumentException("Username is too long, try something shorter");
        }
        if(username_validity == INVALID_CHARACTERS_ERROR){
            throw new IllegalArgumentException("Please enter a valid Email as username");
        }

        if(password_validity != TOO_LONG_ERROR){
            throw new IllegalArgumentException("Your password is too long, try a shorter one");
        }
        if(password_validity != INVALID_CHARACTERS_ERROR){
            throw new IllegalArgumentException("Please enter a valid password");
        }

        if(password_validity != TOO_SHORT_ERROR){
            throw new IllegalArgumentException("Your password is too short, add more characters");
        }

        this.username = username;
        this.password = password;
    }

    private int CheckUsername(String username){
        int phase = 1;
        for(int i = 0; i< username.length();i++){
            if(phase == 1){
                char c = username.charAt(i);
                boolean is_letter = (((int)c >= A_ASCII) && ((int)c <= Z_ASCII)) || (((int)c >= a_ASCII) && ((int)c <= z_ASCII));
                boolean is_number = ((int)c >= Zero_ASCII) && ((int)c <= Nine_ASCII) ;
                boolean is_valid_symbol_for_phase_1 = ((c == '-') || (c == '+') || (c == '%') || (c == '_'));
                boolean is_strudel = (c == '@');
                boolean is_valid_symbol_for_phase_2 = ((c == '-') || (c == '.'));
                boolean is_dot = (c == '.');

            }
            if(phase == 2){

            }
            if(phase == 3){

            }
        }
        return VALID;
    }

    private int CheckPassword(String password){
        return VALID;
    }


}
