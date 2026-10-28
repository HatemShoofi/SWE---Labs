import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;



public class UsersApp {
    public static void main(String[] args) {
        try {
        File readFile = new File("Users.txt");
        Scanner reader = new Scanner(readFile);
        ArrayList<User> Users = new ArrayList<>();

        while(reader.hasNextLine()){
            String data = reader.nextLine();
            String[] parts = data.trim().split("\\s+");
            if(parts.length != 2)
                continue;
            String username = parts[0];
            String password = parts[1];
            try{
                User user = new User(username, password);
                Users.add(user);
            }catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            }
        }
        reader.close();
        Users.sort((u1, u2) -> u1.GetUsername().compareTo(u2.GetUsername()));
        for(User user:Users){
            System.out.println(user.GetUsername() + " " + user.GetPassword());
        }
        } catch (FileNotFoundException e) {
            
            System.out.println(e.getMessage());
        }
        

    }
    
}



