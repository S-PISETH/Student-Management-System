package service;

import dao.ActivityLogDAO;
import dao.UserDAO;
import model.UserAccount;

import java.sql.SQLException;

public class AuthService {
    private final UserDAO userDAO;
    private final ActivityLogDAO activityLogDAO;

    public AuthService() {
        this(new UserDAO(), new ActivityLogDAO());
    }

    public AuthService(UserDAO userDAO, ActivityLogDAO activityLogDAO) {
        this.userDAO = userDAO;
        this.activityLogDAO = activityLogDAO;
    }

    public UserAccount login(String username, String password) throws SQLException {
        UserAccount user = userDAO.authenticate(username, password);
        if (user != null) {
            activityLogDAO.log(user, "LOGIN", "User", String.valueOf(user.getUserId()), "User logged in.");
        }
        return user;
    }
}
