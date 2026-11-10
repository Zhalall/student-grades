package app.dao;

import app.Db;

import java.sql.SQLException;

public class ReportDao {

    /** JOIN 5 таблиц + COUNT + AVG + GROUP BY. */
    public void averageByStudentAndSubject() throws SQLException {
        Db.printQuery(
                "SELECT g.name AS \"Группа\", s.full_name AS \"Студент\", sub.name AS \"Предмет\", " +
                "t.full_name AS \"Преподаватель\", COUNT(gr.id) AS \"Оценок\", " +
                "ROUND(AVG(gr.grade), 1) AS \"Средний балл\" " +
                "FROM grades gr " +
                "JOIN students s ON s.id = gr.student_id " +
                "JOIN groups g ON g.id = s.group_id " +
                "JOIN subjects sub ON sub.id = gr.subject_id " +
                "JOIN teachers t ON t.id = sub.teacher_id " +
                "GROUP BY s.id, sub.id " +
                "ORDER BY g.name, s.full_name, sub.name");
    }

    /** Студенты со средним баллом ниже порога. */
    public void debtors(int threshold) throws SQLException {
        Db.printQuery(
                "SELECT s.full_name AS \"Студент\", g.name AS \"Группа\", " +
                "COUNT(gr.id) AS \"Оценок\", ROUND(AVG(gr.grade), 1) AS \"Средний балл\" " +
                "FROM students s " +
                "JOIN groups g ON g.id = s.group_id " +
                "JOIN grades gr ON gr.student_id = s.id " +
                "GROUP BY s.id " +
                "HAVING AVG(gr.grade) < ? " +
                "ORDER BY AVG(gr.grade)", threshold);
    }
}
