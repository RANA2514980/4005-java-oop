package dao;

import models.Book;
import utils.LoggerUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class BookDAO {
    private static final Logger LOGGER = LoggerUtil.getLogger(BookDAO.class);

    public boolean insert(Book book) {
        String sql = "INSERT INTO books (title, author, category, status) VALUES (?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, book.getTitle());
            statement.setString(2, book.getAuthor());
            statement.setString(3, book.getCategory());
            statement.setString(4, book.getStatus());
            return statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            LOGGER.severe("Failed to insert book: " + ex.getMessage());
            return false;
        }
    }

    public List<Book> getAll() {
        String sql = "SELECT id, title, author, category, status FROM books";
        List<Book> books = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                books.add(mapRow(resultSet));
            }
        } catch (SQLException ex) {
            LOGGER.severe("Failed to fetch books: " + ex.getMessage());
        }
        return books;
    }

    public Optional<Book> getById(int id) {
        String sql = "SELECT id, title, author, category, status FROM books WHERE id = ?";
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }
            }
        } catch (SQLException ex) {
            LOGGER.severe("Failed to fetch book by id: " + ex.getMessage());
        }
        return Optional.empty();
    }

    public List<Book> searchByTitleOrAuthor(String query) {
        String sql = "SELECT id, title, author, category, status FROM books WHERE title LIKE ? OR author LIKE ?";
        List<Book> books = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            String pattern = "%" + query + "%";
            statement.setString(1, pattern);
            statement.setString(2, pattern);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    books.add(mapRow(resultSet));
                }
            }
        } catch (SQLException ex) {
            LOGGER.severe("Failed to search books: " + ex.getMessage());
        }
        return books;
    }

    public boolean update(Book book) {
        String sql = "UPDATE books SET title = ?, author = ?, category = ?, status = ? WHERE id = ?";
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, book.getTitle());
            statement.setString(2, book.getAuthor());
            statement.setString(3, book.getCategory());
            statement.setString(4, book.getStatus());
            statement.setInt(5, book.getId());
            return statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            LOGGER.severe("Failed to update book: " + ex.getMessage());
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM books WHERE id = ?";
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            LOGGER.severe("Failed to delete book: " + ex.getMessage());
            return false;
        }
    }

    private Book mapRow(ResultSet resultSet) throws SQLException {
        return new Book(
            resultSet.getInt("id"),
            resultSet.getString("title"),
            resultSet.getString("author"),
            resultSet.getString("category"),
            resultSet.getString("status")
        );
    }
}
