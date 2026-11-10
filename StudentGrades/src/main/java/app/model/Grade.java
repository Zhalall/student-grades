package app.model;

public record Grade(int id, int studentId, String studentName, int subjectId, String subjectName,
                    int typeId, String typeName, int grade, String date) {
    @Override
    public String toString() {
        return String.format("#%-3d | %-22s | %-14s | %-13s | %3d | %s",
                id, studentName, subjectName, typeName, grade, date);
    }
}
