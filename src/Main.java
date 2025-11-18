import model.JsonDatabaseManager;
import ui.frames.LoginFrame;

public class Main {
    public static void main(String[] args) {
        JsonDatabaseManager db = new JsonDatabaseManager();
        new LoginFrame(db).setVisible(true);
    }
}
