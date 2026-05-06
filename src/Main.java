import javax.swing.UIManager;
import java.awt.Font;
import java.util.Enumeration;

public class Main {
    private static void applyGlobalFont(Font font) {
        Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof Font) {
                UIManager.put(key, font);
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("St Mary's Digital Library System starting...");

        try {
            dao.DatabaseInitializer.initialize();

            applyGlobalFont(new Font("SansSerif", Font.PLAIN, 18));
            
            // Check for --seed argument to populate database with test data
            if (args.length > 0 && args[0].equals("--seed")) {
                System.out.println("Seeding database with test data...");
                dao.DatabaseSeeder.seedTestData();
            }
            
            javax.swing.SwingUtilities.invokeLater(() -> new views.MainDashboard().setVisible(true));
        } catch (Exception e) {
            System.err.println("Database connection failed: " + e.getMessage());
        }
    }
}
