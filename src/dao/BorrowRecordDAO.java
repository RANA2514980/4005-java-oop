package dao;

import models.BorrowRecord;
import utils.LoggerUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class BorrowRecordDAO {
    private static final Logger LOGGER = LoggerUtil.getLogger(BorrowRecordDAO.class);

    public boolean insert(BorrowRecord record) {
        String sql = "INSERT INTO borrow_records (book_id, member_id, borrow_date, return_date) VALUES (?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, record.getBookId());
            statement.setInt(2, record.getMemberId());
            statement.setString(3, record.getBorrowDate());
            statement.setString(4, record.getReturnDate());
            return statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            LOGGER.severe("Failed to insert borrow record: " + ex.getMessage());
            return false;
        }
    }

    public List<BorrowRecord> getAll() {
        String sql = "SELECT id, book_id, member_id, borrow_date, return_date FROM borrow_records";
        List<BorrowRecord> records = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                records.add(new BorrowRecord(
                    resultSet.getInt("id"),
                    resultSet.getInt("book_id"),
                    resultSet.getInt("member_id"),
                    resultSet.getString("borrow_date"),
                    resultSet.getString("return_date")
                ));
            }
        } catch (SQLException ex) {
            LOGGER.severe("Failed to fetch borrow records: " + ex.getMessage());
        }
        return records;
    }

    public boolean update(BorrowRecord record) {
        String sql = "UPDATE borrow_records SET book_id = ?, member_id = ?, borrow_date = ?, return_date = ? WHERE id = ?";
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, record.getBookId());
            statement.setInt(2, record.getMemberId());
            statement.setString(3, record.getBorrowDate());
            statement.setString(4, record.getReturnDate());
            statement.setInt(5, record.getId());
            return statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            LOGGER.severe("Failed to update borrow record: " + ex.getMessage());
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM borrow_records WHERE id = ?";
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            LOGGER.severe("Failed to delete borrow record: " + ex.getMessage());
            return false;
        }
    }
}
