package app.dao;

import app.Db;
import app.model.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDao {
    private static final String BASE =
            "SELECT s.id, s.full_name, s.email, s.group_id, g.name " +
            "FROM students s JOIN groups g ON g.id = s.group_id ";

    private Student map(ResultSet rs) throws SQLException {
        return new Student(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getInt(4), rs.getString(5));
    }

    public List<Student> findAll() throws SQLException {
        return query(BASE + "ORDER BY s.id");
    }

    public List<Student> search(String q) throws SQLException {
        String like = "%" + q.trim() + "%";
        return query(BASE + "WHERE s.full_name LIKE ? OR s.email LIKE ? OR g.name LIKE ? ORDER BY s.id",
                like, like, like);
    }

    private List<Student> query(String sql, Object... params) throws SQLException {
        List<Student> list = new ArrayList<>();
        try (Connection c = Db.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) ps.setObject(i + 1, params[i]);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public void insert(String fullName, String email, int groupId) throws SQLException {
        try (Connection c = Db.get();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO students(full_name, email, group_id) VALUES (?, ?, ?)")) {
            ps.setString(1, fullName);
            ps.setString(2, email);
            ps.setInt(3, groupId);
            ps.executeUpdate();
        }
    }

    public boolean update(int id, String fullName, String email, int groupId) throws SQLException {
        try (Connection c = Db.get();
             PreparedStatement ps = c.prepareStatement(
                     "UPDATE students SET full_name = ?, email = ?, group_id = ? WHERE id = ?")) {
            ps.setString(1, fullName);
            ps.setString(2, email);
            ps.setInt(3, groupId);
            ps.setInt(4, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        try (Connection c = Db.get();
             PreparedStatement ps = c.prepareStatement("DELETE FROM students WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}
