package app;

import app.dao.GradeDao;
import app.dao.ReportDao;
import app.dao.StudentDao;
import app.model.Student;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner in = new Scanner(System.in);
    private static final StudentDao students = new StudentDao();
    private static final GradeDao grades = new GradeDao();
    private static final ReportDao reports = new ReportDao();

    public static void main(String[] args) {
        try {
            Db.init();
        } catch (SQLException e) {
            System.out.println("Ошибка БД: " + e.getMessage());
            return;
        }
        while (true) {
            printMenu();
            String choice = in.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> students.findAll().forEach(System.out::println);
                    case "2" -> addStudent();
                    case "3" -> editStudent();
                    case "4" -> deleteStudent();
                    case "5" -> searchStudents();
                    case "6" -> grades.findAll().forEach(System.out::println);
                    case "7" -> addGrade();
                    case "8" -> editGrade();
                    case "9" -> deleteGrade();
                    case "10" -> reports.averageByStudentAndSubject();
                    case "11" -> reports.debtors(50);
                    case "12" -> showDictionaries();
                    case "0" -> {
                        System.out.println("Пока!");
                        return;
                    }
                    default -> System.out.println("Нет такого пункта.");
                }
            } catch (SQLException e) {
                System.out.println("Ошибка БД: " + e.getMessage());
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n===== УЧЁТ СТУДЕНТОВ И ОЦЕНОК =====");
        System.out.println(" 1. Список студентов");
        System.out.println(" 2. Добавить студента");
        System.out.println(" 3. Изменить студента");
        System.out.println(" 4. Удалить студента");
        System.out.println(" 5. Поиск студентов");
        System.out.println(" 6. Список оценок");
        System.out.println(" 7. Добавить оценку");
        System.out.println(" 8. Изменить оценку");
        System.out.println(" 9. Удалить оценку");
        System.out.println("10. Отчёт: средний балл по студентам и предметам");
        System.out.println("11. Отчёт: должники (средний < 50)");
        System.out.println("12. Справочники (группы, предметы, типы оценок)");
        System.out.println(" 0. Выход");
        System.out.print("Выбор: ");
    }

    // ---------- студенты ----------

    private static void addStudent() throws SQLException {
        String name = readNonEmpty("Имя: ");
        String email = readEmail();
        Db.printQuery("SELECT id, name FROM groups");
        int groupId = readExistingId("ID группы: ", "groups");
        students.insert(name, email, groupId);
        System.out.println("Студент добавлен.");
    }

    private static void editStudent() throws SQLException {
        int id = readExistingId("ID студента: ", "students");
        String name = readNonEmpty("Новое имя: ");
        String email = readEmail();
        Db.printQuery("SELECT id, name FROM groups");
        int groupId = readExistingId("ID группы: ", "groups");
        System.out.println(students.update(id, name, email, groupId) ? "Обновлено." : "Не найден.");
    }

    private static void deleteStudent() throws SQLException {
        int id = readExistingId("ID студента: ", "students");
        System.out.println(students.delete(id) ? "Удалено (оценки тоже)." : "Не найден.");
    }

    private static void searchStudents() throws SQLException {
        String q = readNonEmpty("Поиск (имя / email / группа): ");
        List<Student> found = students.search(q);
        if (found.isEmpty()) System.out.println("Ничего не найдено.");
        else found.forEach(System.out::println);
    }

    // ---------- оценки ----------

    private static void addGrade() throws SQLException {
        students.findAll().forEach(System.out::println);
        int studentId = readExistingId("ID студента: ", "students");
        Db.printQuery("SELECT id, name FROM subjects");
        int subjectId = readExistingId("ID предмета: ", "subjects");
        Db.printQuery("SELECT id, name FROM grade_types");
        int typeId = readExistingId("ID типа оценки: ", "grade_types");
        int grade = readGrade();
        String date = readDate();
        grades.insert(studentId, subjectId, typeId, grade, date);
        System.out.println("Оценка добавлена.");
    }

    private static void editGrade() throws SQLException {
        int id = readExistingId("ID оценки: ", "grades");
        int grade = readGrade();
        String date = readDate();
        System.out.println(grades.update(id, grade, date) ? "Обновлено." : "Не найдена.");
    }

    private static void deleteGrade() throws SQLException {
        int id = readExistingId("ID оценки: ", "grades");
        System.out.println(grades.delete(id) ? "Удалено." : "Не найдена.");
    }

    private static void showDictionaries() throws SQLException {
        System.out.println("\nГруппы:");
        Db.printQuery("SELECT id, name FROM groups");
        System.out.println("\nПредметы:");
        Db.printQuery("SELECT s.id, s.name, s.credits, t.full_name AS teacher " +
                "FROM subjects s JOIN teachers t ON t.id = s.teacher_id");
        System.out.println("\nТипы оценок:");
        Db.printQuery("SELECT id, name FROM grade_types");
    }

    // ---------- ввод с валидацией ----------

    private static String readNonEmpty(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = in.nextLine();
            if (Validator.isNotEmpty(s)) return s.trim();
            System.out.println("Поле не может быть пустым.");
        }
    }

    private static String readEmail() {
        while (true) {
            String s = readNonEmpty("Email: ");
            if (Validator.isEmail(s)) return s;
            System.out.println("Неверный формат email (пример: name@mail.com).");
        }
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            Integer n = Validator.parseInt(in.nextLine());
            if (n != null) return n;
            System.out.println("Нужно ввести целое число.");
        }
    }

    private static int readExistingId(String prompt, String table) throws SQLException {
        while (true) {
            int id = readInt(prompt);
            if (Db.exists(table, id)) return id;
            System.out.println("Записи с таким ID нет.");
        }
    }

    private static int readGrade() {
        while (true) {
            int g = readInt("Оценка (0-100): ");
            if (Validator.isGrade(g)) return g;
            System.out.println("Оценка должна быть от 0 до 100.");
        }
    }

    private static String readDate() {
        while (true) {
            System.out.print("Дата yyyy-MM-dd (Enter = сегодня): ");
            String s = in.nextLine().trim();
            if (s.isEmpty()) return LocalDate.now().toString();
            LocalDate d = Validator.parseDate(s);
            if (d != null) return d.toString();
            System.out.println("Неверный формат даты. Пример: 2026-11-10");
        }
    }
}
