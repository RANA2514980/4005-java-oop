public class Main {
    public static void main(String[] args) {
        System.out.println("St Mary's Digital Library System starting...");

        try {
            dao.DatabaseInitializer.initialize();
            try (var connection = dao.DatabaseConnection.getInstance().getConnection()) {
                System.out.println("Database connection OK: " + (connection != null));
            }
        } catch (Exception e) {
            System.err.println("Database connection failed: " + e.getMessage());
        }
    }
}
