package uga.menik.csx370.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import uga.menik.csx370.models.User;

@Service
@SessionScope
public class UserService {

    private final DataSource dataSource;
    private final BCryptPasswordEncoder passwordEncoder;
    private User loggedInUser = null;

    @Autowired
    public UserService(DataSource dataSource) {
        this.dataSource = dataSource;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public boolean authenticate(String username, String password) throws SQLException {
        // Updated to common SQL naming convention: user_id
        final String sql = "SELECT * FROM user WHERE username = ?";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password");
                    boolean match = passwordEncoder.matches(password, storedHash);
                    if (match) {
                        // FIX: use rs.getInt for the ID to match the User model
                        loggedInUser = new User(
                            rs.getInt("userId"), 
                            rs.getString("username"),
                            rs.getString("firstName"),
                            rs.getString("lastName")
                        );
                    }
                    return match;
                }
            }
        }
        return false;
    }

    // Update Profile logic
    public boolean updateProfile(int userId, String firstName, String lastName) throws SQLException {
        // Ensure the WHERE clause uses the correct column name (user_id)
        final String sql = "UPDATE user SET firstName = ?, lastName = ? WHERE userId = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setInt(3, userId);
            
            boolean success = pstmt.executeUpdate() > 0;
            
            if (success && loggedInUser != null && loggedInUser.getUserId() == userId) {
                loggedInUser.setFirstName(firstName);
                loggedInUser.setLastName(lastName);
            }
            return success;
        }
    }

    public void unAuthenticate() {
        loggedInUser = null;
    }

    public boolean isAuthenticated() {
        return loggedInUser != null;
    }

    public User getLoggedInUser() {
        return loggedInUser;
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