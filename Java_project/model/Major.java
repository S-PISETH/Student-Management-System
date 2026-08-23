package model;

import java.util.Objects;

/**
 * Represents a university major assigned to students.
 */
public class Major {
    private int majorId;
    private String majorCode;
    private String majorName;
    private String faculty;
    private boolean active;

    public Major() {
        this.active = true;
    }

    public Major(int majorId, String majorCode, String majorName, String faculty, boolean active) {
        this.majorId = majorId;
        this.majorCode = majorCode;
        this.majorName = majorName;
        this.faculty = faculty;
        this.active = active;
    }

    public int getMajorId() {
        return majorId;
    }

    public void setMajorId(int majorId) {
        this.majorId = majorId;
    }

    public String getMajorCode() {
        return majorCode;
    }

    public void setMajorCode(String majorCode) {
        this.majorCode = majorCode;
    }

    public String getMajorName() {
        return majorName;
    }

    public void setMajorName(String majorName) {
        this.majorName = majorName;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return majorName == null ? "" : majorName;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Major major)) {
            return false;
        }
        return majorId == major.majorId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(majorId);
    }
}
