package dao;

import models.Member;
import utils.LoggerUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class MemberDAO {
    private static final Logger LOGGER = LoggerUtil.getLogger(MemberDAO.class);

    public boolean insert(Member member) {
        String sql = "INSERT INTO members (name, email, type) VALUES (?, ?, ?)";
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, member.getName());
            statement.setString(2, member.getEmail());
            statement.setString(3, member.getType());
            return statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            LOGGER.severe("Failed to insert member: " + ex.getMessage());
            return false;
        }
    }

    public List<Member> getAll() {
        String sql = "SELECT id, name, email, type FROM members";
        List<Member> members = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                members.add(new Member(
                    resultSet.getInt("id"),
                    resultSet.getString("name"),
                    resultSet.getString("email"),
                    resultSet.getString("type")
                ));
            }
        } catch (SQLException ex) {
            LOGGER.severe("Failed to fetch members: " + ex.getMessage());
        }
        return members;
    }

    public boolean update(Member member) {
        String sql = "UPDATE members SET name = ?, email = ?, type = ? WHERE id = ?";
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, member.getName());
            statement.setString(2, member.getEmail());
            statement.setString(3, member.getType());
            statement.setInt(4, member.getId());
            return statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            LOGGER.severe("Failed to update member: " + ex.getMessage());
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM members WHERE id = ?";
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            LOGGER.severe("Failed to delete member: " + ex.getMessage());
            return false;
        }
    }
}
