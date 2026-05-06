public class Main {
    public static void main(String[] args) {
        System.out.println("St Mary's Digital Library System starting...");

        try {
            dao.DatabaseInitializer.initialize();
            javax.swing.SwingUtilities.invokeLater(() -> new views.MainDashboard().setVisible(true));
        } catch (Exception e) {
            System.err.println("Database connection failed: " + e.getMessage());
        }
    }
}
