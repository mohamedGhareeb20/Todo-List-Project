package todo;

public class Main {
    public static void main(String[] args) {
        Manager manager = new Manager();
        GUI app = new GUI(manager);
        app.initializeListener();
        app.setVisible(true);
    }
}