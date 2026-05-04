package uga.menik.csx370.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uga.menik.csx370.models.FavoriteLot;
import uga.menik.csx370.models.PackedTrend;
import uga.menik.csx370.models.RecentReport;

@Service
public class FavoritesService {
    private final DataSource data_source;

    @Autowired
    public FavoritesService(DataSource data_source) {
        this.data_source = data_source;
    }

    // Add a specific lot to the user's favorites page.
    public void addFavorite(int userId, int lotId) throws SQLException {
        final String sql = "INSERT IGNORE INTO favorite (userId, lotId) VALUES (?, ?)";

        try (Connection conn = data_source.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, lotId);
            stmt.executeUpdate();
        }
    }

    public void removeFavorite(int userId, int lotId) throws SQLException {
        final String sql = "DELETE FROM favorite WHERE userId = ? AND lotId = ?";

        try (Connection conn = data_source.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, lotId);
            stmt.executeUpdate();
        }
    }

    // Gets the list of the user's favorite lots with their latest stats
    public List<FavoriteLot> getFavoriteLots(int userId) throws SQLException {
        final String sql =
            "SELECT l.lotId, l.name, l.address, l.paymentType, sr.packedLevel, sr.hasOpenSpots, DATE_FORMAT(sr.reportedAt, '%b %d %h:%i %p') AS lastReported " +
            "FROM favorite f " +
            "JOIN lot l ON f.lotId = l.lotId " +
            "LEFT JOIN spot_report sr ON sr.lotId = l.lotId " +
            "  AND sr.reportedAt = ( " +
            "      SELECT MAX(sr2.reportedAt) FROM spot_report sr2 WHERE sr2.lotId = l.lotId " +
            "  ) " +
            "WHERE f.userId = ? " +
            "ORDER BY l.name ASC";

        List<FavoriteLot> results = new ArrayList<>();

        try (Connection conn = data_source.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);

            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    results.add(new FavoriteLot(
                        resultSet.getInt("lotId"),
                        resultSet.getString("name"),
                        resultSet.getString("address"),
                        resultSet.getString("paymentType"),
                        resultSet.getString("packedLevel"),
                        resultSet.getBoolean("hasOpenSpots"),
                        resultSet.getString("lastReported")
                    ));
                }
            }
        }

        return results;
    }


    // Gets the 20 most recent reports for the user's favorite lots
    public List<RecentReport> getRecentActivity(int userId) throws SQLException {
        final String sql =
            "SELECT l.name AS lotName, sr.packedLevel, sr.hasOpenSpots, u.username AS reporter, DATE_FORMAT(sr.reportedAT, '%b %d %h:%i %p') AS reportedAt " +
            "FROM spot_report sr " +
            "JOIN lot l ON sr.lotId = l.lotId " +
            "JOIN user u ON sr.userId = u.userId " +
            "JOIN favorite f ON f.lotId = sr.lotId AND f.userId = ? " +
            "ORDER BY sr.reportedAt DESC " +
            "LIMIT 20";

        List<RecentReport> results = new ArrayList<>();

        try (Connection conn = data_source.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);

            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    results.add(new RecentReport(
                        resultSet.getString("lotName"),
                        resultSet.getString("packedLevel"),
                        resultSet.getBoolean("hasOpenSpots"),
                        resultSet.getString("reporter"),
                        resultSet.getString("reportedAt")
                    ));
                }
            }
        }

        return results;
    }

    // Gets the average packed level for all of the user's favorited lots per day over the last 7 days
    public List<PackedTrend> getAvgPackedTrend(int userId) throws SQLException {
        final String sql =
            "SELECT DATE_FORMAT(sr.reportedAt, '%b %d') AS dateLabel, AVG(CASE sr.packedLevel " +
            "    WHEN 'Empty'    THEN 1 " +
            "    WHEN 'Light'    THEN 2 " +
            "    WHEN 'Moderate' THEN 3 " +
            "    WHEN 'Busy'     THEN 4 " +
            "    WHEN 'Full'     THEN 5 END) AS avgLevel " +
            "FROM spot_report sr " +
            "JOIN favorite f ON f.lotId = sr.lotId AND f.userId = ? " +
            "WHERE sr.reportedAt >= NOW() - INTERVAL 7 DAY " +
            "GROUP BY DATE(sr.reportedAt) " +
            "ORDER BY DATE(sr.reportedAt) ASC";

        List<PackedTrend> results = new ArrayList<>();

        try (Connection conn = data_source.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);

            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    results.add(new PackedTrend(
                        resultSet.getString("dateLabel"),
                        resultSet.getDouble("avgLevel")
                    ));
                }
            }
        }

        return results;
    }
}
