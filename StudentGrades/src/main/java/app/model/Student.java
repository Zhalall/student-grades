package app.model;

public record Student(int id, String fullName, String email, int groupId, String groupName) {
    @Override
    public String toString() {
        return String.format("#%-3d | %-25s | %-28s | %s", id, fullName, email, groupName);
    }
}
