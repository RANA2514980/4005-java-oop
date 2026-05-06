package dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseInitializer {
    private DatabaseInitializer() {
    }

    public static void initialize() throws SQLException {
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(
                "CREATE TABLE IF NOT EXISTS books (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT NOT NULL, " +
                "author TEXT NOT NULL, " +
                "category TEXT, " +
                "status TEXT NOT NULL DEFAULT 'Available')"
            );
            statement.executeUpdate(
                "CREATE TABLE IF NOT EXISTS members (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "email TEXT NOT NULL, " +
                "type TEXT NOT NULL)"
            );
            statement.executeUpdate(
                "CREATE TABLE IF NOT EXISTS borrow_records (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "book_id INTEGER NOT NULL, " +
                "member_id INTEGER NOT NULL, " +
                "borrow_date TEXT NOT NULL, " +
                "return_date TEXT, " +
                "FOREIGN KEY(book_id) REFERENCES books(id), " +
                "FOREIGN KEY(member_id) REFERENCES members(id))"
            );
        }
    }
}
