# Учёт студентов и оценок

Курсовой проект: консольное Java-приложение (JDBC + SQLite).

## Запуск
1. Открыть `pom.xml` в IntelliJ IDEA (File -> Open -> Open as Project).
2. Дождаться загрузки Maven-зависимостей (sqlite-jdbc).
3. Запустить класс `app.Main`. Файл БД `students.db` создаётся автоматически вместе с тестовыми данными.

Если кириллица в консоли отображается неверно: Run -> Edit Configurations -> VM options -> `-Dfile.encoding=UTF-8`.

## Возможности
- Полный CRUD: студенты, оценки
- Поиск студентов по имени, email, группе (`LIKE`)
- Отчёт 1: средний балл по студентам и предметам (JOIN 5 таблиц + COUNT / AVG / GROUP BY)
- Отчёт 2: должники со средним баллом ниже 50 (HAVING)
- Валидация: пустые поля, формат email, оценка 0-100, дата `yyyy-MM-dd`, существование ID, уникальный email

## Нормализация (3NF)
Группа, преподаватель, предмет и тип оценки вынесены в отдельные таблицы. В `grades` хранятся только внешние ключи и сама оценка, поэтому нет повторяющихся данных и транзитивных зависимостей.

## ER-диаграмма

```mermaid
erDiagram
    GROUPS ||--o{ STUDENTS : "состоит"
    TEACHERS ||--o{ SUBJECTS : "ведёт"
    STUDENTS ||--o{ GRADES : "получает"
    SUBJECTS ||--o{ GRADES : "по предмету"
    GRADE_TYPES ||--o{ GRADES : "тип"

    GROUPS {
        int id PK
        string name
    }
    STUDENTS {
        int id PK
        string full_name
        string email
        int group_id FK
    }
    TEACHERS {
        int id PK
        string full_name
    }
    SUBJECTS {
        int id PK
        string name
        int credits
        int teacher_id FK
    }
    GRADE_TYPES {
        int id PK
        string name
    }
    GRADES {
        int id PK
        int student_id FK
        int subject_id FK
        int type_id FK
        int grade
        string grade_date
    }
```

## Class diagram

```mermaid
classDiagram
    class Main {
        +main(String[] args)
    }
    class Db {
        +get() Connection
        +init()
        +exists(String, int) boolean
        +printQuery(String, Object...)
    }
    class Validator {
        +isNotEmpty(String) boolean
        +isEmail(String) boolean
        +parseInt(String) Integer
        +isGrade(int) boolean
        +parseDate(String) LocalDate
    }
    class Student {
        <<record>>
        int id
        String fullName
        String email
        int groupId
        String groupName
    }
    class Grade {
        <<record>>
        int id
        int studentId
        String studentName
        int subjectId
        String subjectName
        int typeId
        String typeName
        int grade
        String date
    }
    class StudentDao {
        +findAll() List~Student~
        +search(String) List~Student~
        +insert(...)
        +update(...) boolean
        +delete(int) boolean
    }
    class GradeDao {
        +findAll() List~Grade~
        +insert(...)
        +update(...) boolean
        +delete(int) boolean
    }
    class ReportDao {
        +averageByStudentAndSubject()
        +debtors(int)
    }

    Main --> StudentDao
    Main --> GradeDao
    Main --> ReportDao
    Main --> Validator
    Main --> Db
    StudentDao --> Db
    GradeDao --> Db
    ReportDao --> Db
    StudentDao ..> Student
    GradeDao ..> Grade
```
