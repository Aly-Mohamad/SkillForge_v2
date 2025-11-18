package model;

import java.util.Optional;

public class AuthManager {
    private JsonDatabaseManager db;

    public AuthManager(JsonDatabaseManager db) { this.db = db; }

    public boolean signupStudent(String username, String email, String password) {
        String hash = HashUtil.sha256(password);
        return db.addUser(new Student(username, email, hash));
    }

    public boolean signupInstructor(String username, String email, String password) {
        String hash = HashUtil.sha256(password);
        return db.addUser(new Instructor(username, email, hash));
    }

    public boolean signupAdmin(String username, String email, String password) {
        String hash = HashUtil.sha256(password);
        return db.addUser(new Admin(username, email, hash));
    }

    public Optional<User> login(String email, String password) {
        Optional<User> u = db.findByEmail(email);
        if (!u.isPresent()) return Optional.empty();
        String hash = HashUtil.sha256(password);
        if (u.get().getPassword().equals(hash)) return u;
        return Optional.empty();
    }
}
