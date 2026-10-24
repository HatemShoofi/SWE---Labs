public class User {

    private static final int VALID = 0;
    private static final int TOO_LONG_ERROR = 1;
    private static final int INVALID_CHARACTERS_ERROR = 2;
    private static final int TOO_SHORT_ERROR = 3;
    

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

        if(password_validity == TOO_LONG_ERROR){
            throw new IllegalArgumentException("Your password is too long, try a shorter one");
        }
        if(password_validity == INVALID_CHARACTERS_ERROR){
            throw new IllegalArgumentException("Please enter a valid password");
        }

        if(password_validity == TOO_SHORT_ERROR){
            throw new IllegalArgumentException("Your password is too short, add more characters");
        }

        this.username = username;
        this.password = password;
    }

    public String GetUsername(){
        return this.username;
    }
    public String GetPassword(){
        
        return password;
    }

    private int CheckUsername(String username){
        int strudel_count = 0;
        int strudel_index = 0;
        int last_dot_index = 0;
        int dot_count = 0;
        if(username.length() < 6)
            return INVALID_CHARACTERS_ERROR;
        if(username.length() > 50)
            return TOO_LONG_ERROR;

        for(int i = 0; i< username.length(); i++){ //check if there's only one '@' and find its index along with index of last dot (.)
            char c = username.charAt(i);
            if(c == '@'){
                strudel_count++;
                if(strudel_count > 1)
                    return INVALID_CHARACTERS_ERROR;
                strudel_index = i;
            }
            if(c == '.'){
                last_dot_index = i;
                dot_count++;
            }
        }
        if(strudel_count == 0 || dot_count == 0)
            return INVALID_CHARACTERS_ERROR; //if no @ or dots were found
        if(strudel_index > last_dot_index)
            return INVALID_CHARACTERS_ERROR;
        if(strudel_index < 1 || last_dot_index-strudel_index <= 1 || last_dot_index >= username.length() - 2 )
            return INVALID_CHARACTERS_ERROR; //if first part, second part or last part are too short

        for(int i = 0; i < strudel_index; i++){
            char c = username.charAt(i);
            boolean is_letter = ((int)c >= (int)'a' && (int)c <= (int)'z') || ((int)c >= (int)'A' && (int)c <= (int)'Z');
            boolean is_number = (int)c >= (int)'0' && (int)c <= (int)'9';
            boolean is_valid_symbol_for_section_1 = ((c == '-') || (c == '+') || (c == '%') || (c == '_') || (c == '.'));
            if(! (is_number || is_letter ||is_valid_symbol_for_section_1))
                return INVALID_CHARACTERS_ERROR;
        }
        for(int i = strudel_index + 1; i<last_dot_index; i++){
            char c = username.charAt(i);
            boolean is_letter = ((int)c >= (int)'a' && (int)c <= (int)'z') || ((int)c >= (int)'A' && (int)c <= (int)'Z');
            boolean is_number = (int)c >= (int)'0' && (int)c <= (int)'9';
            boolean is_valid_symbol_for_section_2 = ((c == '-') || (c == '.'));
            if(i == strudel_index + 1 && !is_letter && !is_number)
                return INVALID_CHARACTERS_ERROR; //first char of section 2 must be letter or number only
            if(! (is_letter || is_number || is_valid_symbol_for_section_2))
                return INVALID_CHARACTERS_ERROR; //rest of the chars of section 2 can be letters, numbers or - or .
            
        }
        for(int i = last_dot_index + 1; i < username.length(); i++){
            char c = username.charAt(i);
            boolean is_letter = ((int)c >= (int)'a' && (int)c <= (int)'z') || ((int)c >= (int)'A' && (int)c <= (int)'Z');
            if(!is_letter)
                return INVALID_CHARACTERS_ERROR;
        }
        return VALID;

    }

    private int CheckPassword(String password){

        if(password.length() < 8)
            return TOO_SHORT_ERROR;
        if(password.length() > 12)
            return TOO_LONG_ERROR;

        boolean seen_letter = false;
        boolean seen_number = false;
        boolean seen_special_char = false;

        for(int i = 0; i < password.length(); i++){
            char c = password.charAt(i);
            boolean is_letter = ((int)c >= (int)'a' && (int)c <= (int)'z') || ((int)c >= (int)'A' && (int)c <= (int)'Z');
            boolean is_number = (int)c >= (int)'0' && (int)c <= (int)'9';
            boolean is_special_char = (int)c == (int)'!' || (int)c == (int)'@' || (int)c == (int)'#' || (int)c == (int)'$' || (int)c == (int)'%' || (int)c == (int)'^' || (int)c == (int)'&' || (int)c == (int)'*' || (int)c == (int)'(' ||(int)c == (int)')' || (int)c == (int)'-' || (int)c == (int)'=' || (int)c == (int)'_' || (int)c == (int)'+';
            if(!(is_letter||is_number||is_special_char))
                return INVALID_CHARACTERS_ERROR;
            if(is_letter)
                seen_letter=true;
            if(is_number)
                seen_number = true;
            if(is_special_char)
                seen_special_char = true;
        }
        if (!(seen_letter && seen_number && seen_special_char))
            return INVALID_CHARACTERS_ERROR;
        return VALID;
    }


}
