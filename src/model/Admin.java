package model;

public class Admin extends User {
    public Admin(String username, String email, String password) {
        super("admin", username, email, password);
    }
}
