package app;

import java.sql.*;

public class Db {
    private static final String URL = "jdbc:sqlite:students.db";

    public static Connection get() throws SQLException {
        Connection c = DriverManager.getConnection(URL);
        try (Statement s = c.createStatement()) {
            s.execute("PRAGMA foreign_keys = ON");
        }
        return c;
    }

    public static void init() throws SQLException {
        String[] ddl = {
            "CREATE TABLE IF NOT EXISTS groups (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL UNIQUE)",
            "CREATE TABLE IF NOT EXISTS students (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, full_name TEXT NOT NULL, " +
                "email TEXT NOT NULL UNIQUE, group_id INTEGER NOT NULL REFERENCES groups(id))",
            "CREATE TABLE IF NOT EXISTS teachers (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, full_name TEXT NOT NULL)",
            "CREATE TABLE IF NOT EXISTS subjects (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL UNIQUE, " +
                "credits INTEGER NOT NULL CHECK (credits BETWEEN 1 AND 10), " +
                "teacher_id INTEGER NOT NULL REFERENCES teachers(id))",
            "CREATE TABLE IF NOT EXISTS grade_types (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL UNIQUE)",
            "CREATE TABLE IF NOT EXISTS grades (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "student_id INTEGER NOT NULL REFERENCES students(id) ON DELETE CASCADE, " +
                "subject_id INTEGER NOT NULL REFERENCES subjects(id), " +
                "type_id INTEGER NOT NULL REFERENCES grade_types(id), " +
                "grade INTEGER NOT NULL CHECK (grade BETWEEN 0 AND 100), " +
                "grade_date TEXT NOT NULL)"
        };
        try (Connection c = get(); Statement st = c.createStatement()) {
            for (String sql : ddl) st.execute(sql);

            ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM groups");
            rs.next();
            if (rs.getInt(1) == 0) seed(st);
        }
    }

    private static void seed(Statement st) throws SQLException {
        String[] data = {
            "INSERT INTO groups(name) VALUES ('IT-21'), ('IT-22'), ('MN-21')",
            "INSERT INTO teachers(full_name) VALUES ('Асанов Бакыт'), ('Иванова Мария'), ('Токтогулов Эрлан')",
            "INSERT INTO subjects(name, credits, teacher_id) VALUES " +
                "('Java', 6, 1), ('Базы данных', 5, 2), ('Менеджмент', 4, 3)",
            "INSERT INTO grade_types(name) VALUES ('Экзамен'), ('Лабораторная'), ('Контрольная')",
            "INSERT INTO students(full_name, email, group_id) VALUES " +
                "('Эрназар уулу Жалал', 'jalal@alatoo.edu.kg', 1), " +
                "('Бекова Айгерим', 'aigerim@alatoo.edu.kg', 1), " +
                "('Сыдыков Нурлан', 'nurlan@alatoo.edu.kg', 2), " +
                "('Петров Иван', 'ivan@alatoo.edu.kg', 3)",
            "INSERT INTO grades(student_id, subject_id, type_id, grade, grade_date) VALUES " +
                "(1, 1, 1, 90, '2026-10-10'), (1, 2, 2, 85, '2026-10-12'), " +
                "(2, 1, 1, 70, '2026-10-10'), (2, 3, 3, 65, '2026-10-15'), " +
                "(3, 1, 2, 40, '2026-10-11'), (3, 2, 1, 35, '2026-10-20'), " +
                "(4, 3, 1, 55, '2026-10-21')"
        };
        for (String sql : data) st.execute(sql);
    }

    public static boolean exists(String table, int id) throws SQLException {
        try (Connection c = get();
             PreparedStatement ps = c.prepareStatement("SELECT 1 FROM " + table + " WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeQuery().next();
        }
    }

    /** Печатает результат любого запроса таблицей. */
    public static void printQuery(String sql, Object... params) throws SQLException {
        try (Connection c = get(); PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) ps.setObject(i + 1, params[i]);
            ResultSet rs = ps.executeQuery();
            ResultSetMetaData md = rs.getMetaData();
            int n = md.getColumnCount();
            StringBuilder head = new StringBuilder();
            for (int i = 1; i <= n; i++) head.append(String.format("%-24s", md.getColumnLabel(i)));
            System.out.println(head);
            System.out.println("-".repeat(24 * n));
            int rows = 0;
            while (rs.next()) {
                StringBuilder row = new StringBuilder();
                for (int i = 1; i <= n; i++) row.append(String.format("%-24s", rs.getString(i)));
                System.out.println(row);
                rows++;
            }
            if (rows == 0) System.out.println("(пусто)");
        }
    }
}
