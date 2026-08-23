package model;

/**
 * Represents the academic status of a student.
 */
public enum StudentStatus {
    ACTIVE("Active"),
    SUSPENDED("Suspended"),
    GRADUATED("Graduated"),
    INACTIVE("Inactive");

    private final String displayName;

    StudentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean canBeEdited() {
        return this != GRADUATED;
    }

    public static StudentStatus fromDatabaseValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Student status value cannot be empty.");
        }

        return StudentStatus.valueOf(value.trim().toUpperCase());
    }

    public String toDatabaseValue() {
        return name();
    }

    @Override
    public String toString() {
        return displayName;
    }
}
