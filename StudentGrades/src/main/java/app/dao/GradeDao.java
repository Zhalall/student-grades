package app.dao;

import app.Db;
import app.model.Grade;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GradeDao {
    private static final String BASE =
            "SELECT gr.id, s.id, s.full_name, sub.id, sub.name, t.id, t.name, gr.grade, gr.grade_date " +
            "FROM grades gr " +
            "JOIN students s ON s.id = gr.student_id " +
            "JOIN subjects sub ON sub.id = gr.subject_id " +
            "JOIN grade_types t ON t.id = gr.type_id ";

    public List<Grade> findAll() throws SQLException {
        List<Grade> list = new ArrayList<>();
        try (Connection c = Db.get();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(BASE + "ORDER BY gr.id")) {
            while (rs.next()) {
                list.add(new Grade(rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getInt(4),
                        rs.getString(5), rs.getInt(6), rs.getString(7), rs.getInt(8), rs.getString(9)));
            }
        }
        return list;
    }

    public void insert(int studentId, int subjectId, int typeId, int grade, String date) throws SQLException {
        try (Connection c = Db.get();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO grades(student_id, subject_id, type_id, grade, grade_date) VALUES (?,?,?,?,?)")) {
            ps.setInt(1, studentId);
            ps.setInt(2, subjectId);
            ps.setInt(3, typeId);
            ps.setInt(4, grade);
            ps.setString(5, date);
            ps.executeUpdate();
        }
    }

    public boolean update(int id, int grade, String date) throws SQLException {
        try (Connection c = Db.get();
             PreparedStatement ps = c.prepareStatement(
                     "UPDATE grades SET grade = ?, grade_date = ? WHERE id = ?")) {
            ps.setInt(1, grade);
            ps.setString(2, date);
            ps.setInt(3, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        try (Connection c = Db.get();
             PreparedStatement ps = c.prepareStatement("DELETE FROM grades WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}
