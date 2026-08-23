package model;

/**
 * Represents the gender values supported by the student records.
 */
public enum Gender {
    MALE("Male"),
    FEMALE("Female"),
    OTHER("Other");

    private final String displayName;

    Gender(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Gender fromDatabaseValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Gender value cannot be empty.");
        }

        return Gender.valueOf(value.trim().toUpperCase());
    }

    public String toDatabaseValue() {
        return name();
    }

    @Override
    public String toString() {
        return displayName;
    }
}
