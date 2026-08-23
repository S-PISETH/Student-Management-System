package model;

import java.time.LocalDateTime;

/**
 * Represents an application user account used for login and activity ownership.
 */
public class UserAccount {
    private int userId;
    private String username;
    private String fullName;
    private String roleName;
    private boolean active;
    private LocalDateTime createdAt;

    public UserAccount() {
        this.active = true;
    }

    public UserAccount(int userId, String username, String fullName, String roleName, boolean active) {
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.roleName = roleName;
        this.active = active;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public boolean hasRole(String roleName) {
        return this.roleName != null && this.roleName.equalsIgnoreCase(roleName);
    }

    public boolean isAdmin() {
        return hasRole("ADMIN");
    }

    public boolean isStaff() {
        return hasRole("STAFF");
    }

    public boolean isTeacher() {
        return hasRole("TEACHER");
    }

    public boolean isStudent() {
        return hasRole("STUDENT");
    }

    public boolean canManageRecords() {
        return isAdmin() || isStaff();
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return username == null ? "" : username;
    }
}
