package model;

import java.util.Objects;

public class Subject {
    private int subjectId;
    private String subjectCode;
    private String subjectName;
    private Major major;
    private boolean active;

    public Subject() {
        this.active = true;
    }

    public Subject(int subjectId, String subjectCode, String subjectName, Major major, boolean active) {
        this.subjectId = subjectId;
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.major = major;
        this.active = active;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public Major getMajor() {
        return major;
    }

    public void setMajor(Major major) {
        this.major = major;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
    

        @Override
    public String toString(){

    return getSubjectName();

    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Subject subject)) {
            return false;
        }
        return subjectId == subject.subjectId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(subjectId);
    }
    
}
