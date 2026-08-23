package model;

/**
 * Represents the employment status of a teacher.
 */
public enum TeacherStatus {
    ACTIVE("Active"),
    SUSPENDED("Suspended"),
    INACTIVE("Inactive");

    private final String displayName;

    TeacherStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static TeacherStatus fromDatabaseValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Teacher status value cannot be empty.");
        }

        return TeacherStatus.valueOf(value.trim().toUpperCase());
    }

    public String toDatabaseValue() {
        return name();
    }

    @Override
    public String toString() {
        return displayName;
    }
}
