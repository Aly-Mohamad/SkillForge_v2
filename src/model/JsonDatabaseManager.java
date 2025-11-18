package model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JsonDatabaseManager {
    private static final String USERS_FILE = "src/model/users.json";
    private static final String COURSES_FILE = "src/model/courses.json";

    private List<User> users = new ArrayList<User>();
    private List<Course> courses = new ArrayList<Course>();

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public JsonDatabaseManager() { load(); }

    private void load() {
        try {
            File u = new File(USERS_FILE);
            if (u.exists()) {
                FileReader fr = new FileReader(USERS_FILE);
                UserWrapper wrapper = gson.fromJson(fr, UserWrapper.class);
                fr.close();
                if (wrapper!=null) {
                    if (wrapper.students!=null) users.addAll(wrapper.students);
                    if (wrapper.instructors!=null) users.addAll(wrapper.instructors);
                }
            }
        } catch (Exception e) { System.out.println("Could not load users.json: " + e.getMessage()); }

        try {
            File c = new File(COURSES_FILE);
            if (c.exists()) {
                FileReader fr = new FileReader(COURSES_FILE);
                Type courseListType = new TypeToken<ArrayList<Course>>(){}.getType();
                List<Course> loaded = gson.fromJson(fr, courseListType);
                fr.close();
                if (loaded!=null) courses = loaded;
            }
        } catch (Exception e) { System.out.println("Could not load courses.json: " + e.getMessage()); }
    }

    public  void save() {
        try {
            List<Student> students = new ArrayList<Student>();
            List<Instructor> instructors = new ArrayList<Instructor>();
            for (User u : users) {
                if (u instanceof Student) students.add((Student)u);
                else if (u instanceof Instructor) instructors.add((Instructor)u);
            }
            UserWrapper wrapper = new UserWrapper();
            wrapper.students = students;
            wrapper.instructors = instructors;

            FileWriter fw = new FileWriter(USERS_FILE);
            gson.toJson(wrapper, fw);
            fw.close();

            FileWriter fw2 = new FileWriter(COURSES_FILE);
            gson.toJson(courses, fw2);
            fw2.close();
        } catch (Exception e) { System.out.println("Save failed: " + e.getMessage()); }
    }

    public  boolean addUser(User u) {
        for (User x : users) {
            if (x.getEmail().equalsIgnoreCase(u.getEmail())) return false;
        }
        users.add(u);
        save();
        return true;
    }

    public  Optional<User> findByEmail(String email) {
        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(email)) return Optional.of(u);
        }
        return Optional.empty();
    }

    public  Optional<User> findById(String id) {
        for (User u : users) {
            if (u.getUserId().equals(id)) return Optional.of(u);
        }
        return Optional.empty();
    }

    public  void addCourse(Course c) {
        courses.add(c);
        save();
    }

    public  List<Course> getAllCourses() {
        return courses;
    }

    public  Optional<Course> getCourseById(String id) {
        for (Course c : courses) {
            if (c.getCourseId().equals(id)) return Optional.of(c);
        }
        return Optional.empty();
    }

    public void updateCourse(Course updated) {
        for (int i=0;i<courses.size();i++) if (courses.get(i).getCourseId().equals(updated.getCourseId())) { courses.set(i, updated); break; }
        save();
    }

    static class UserWrapper {
        public List<Student> students;
        public List<Instructor> instructors;
    }
}
