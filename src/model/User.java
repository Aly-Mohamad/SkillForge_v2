package model;

public abstract class User {
    protected String userId;
    protected String role; // "student" or "instructor"
    protected String username;
    protected String email;
    protected String password;

    public User() { }

    public User(String role, String username, String email, String password) {
        this.userId = generateuserid(role);
        this.role = role;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public String getUserId() { return userId; }
    public String getRole() { return role; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }

    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }

    private String generateuserid(String role) {
        int number = (int)(Math.random() * 10000);
        if (role.equalsIgnoreCase("Instructor")) {
            return String.format("I%04d", number);
        }
        else return String.format("S%04d", number);
    }

}
