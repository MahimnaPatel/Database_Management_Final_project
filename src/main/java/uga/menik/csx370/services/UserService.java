package uga.menik.csx370.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;
import uga.menik.csx370.models.User;

@Service
public class UserService {

    private final DataSource dataSource;
    private final BCryptPasswordEncoder passwordEncoder;
    private final HttpSession httpSession;
    private static final String SESSION_USER_KEY = "loggedInUser";

    @Autowired
    public UserService(DataSource dataSource, HttpSession httpSession) {
        this.dataSource = dataSource;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.httpSession = httpSession;
    }

    public boolean authenticate(String username, String password) throws SQLException {
        final String sql = "SELECT * FROM user WHERE username = ?";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password");
                    boolean match = passwordEncoder.matches(password, storedHash);
                    if (match) {
                        User loggedInUser = new User(
                            rs.getInt("userId"), 
                            rs.getString("username"),
                            rs.getString("firstName"),
                            rs.getString("lastName")
                        );
                        httpSession.setAttribute(SESSION_USER_KEY, loggedInUser);
                    }
                    return match;
                }
            }
        }
        return false;
    }

    public boolean updateProfile(int userId, String firstName, String lastName) throws SQLException {
        final String sql = "UPDATE user SET firstName = ?, lastName = ? WHERE userId = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setInt(3, userId);
            
            boolean success = pstmt.executeUpdate() > 0;
            
            if (success) {
                User loggedInUser = getLoggedInUser();
                if (loggedInUser != null && loggedInUser.getUserId() == userId) {
                    loggedInUser.setFirstName(firstName);
                    loggedInUser.setLastName(lastName);
                    httpSession.setAttribute(SESSION_USER_KEY, loggedInUser);
                }
            }
            return success;
        }
    }

    public void unAuthenticate() {
        httpSession.removeAttribute(SESSION_USER_KEY);
    }

    public boolean isAuthenticated() {
        return getLoggedInUser() != null;
    }

    public User getLoggedInUser() {
        return (User) httpSession.getAttribute(SESSION_USER_KEY);
    }

    public boolean registerUser(String username, String password, String firstName, String lastName)
            throws SQLException {
        final String sql = "INSERT INTO user (username, password, firstName, lastName) VALUES (?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, passwordEncoder.encode(password));
            pstmt.setString(3, firstName);
            pstmt.setString(4, lastName);
            return pstmt.executeUpdate() > 0;
        }
    }

    public DataSource getDataSource() {
        return dataSource;
    }
}