package uga.menik.csx370.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uga.menik.csx370.models.Lot;

@Service
public class LotService {

    private final DataSource dataSource;
    private static final DateTimeFormatter REPORT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Autowired
    public LotService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Lot> searchLots(String query, String paymentType, int userId) throws SQLException {
        List<Lot> results = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT l.lotId, l.name, l.address, l.totalCapacity, l.paymentType, l.latitude, l.longitude, ");
        sql.append("sr.packedLevel, sr.hasOpenSpots, sr.reportedAt AS lastReported, ");
        sql.append("IF(f.userId IS NOT NULL, TRUE, FALSE) AS isFavorited ");
        sql.append("FROM lot l ");
        sql.append("LEFT JOIN spot_report sr ON sr.reportId = (SELECT reportId FROM spot_report WHERE lotId = l.lotId ORDER BY reportedAt DESC LIMIT 1) ");
        sql.append("LEFT JOIN favorite f ON f.lotId = l.lotId AND f.userId = ? ");
        sql.append("WHERE 1 = 1 ");

        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (query != null && !query.trim().isEmpty()) {
            sql.append("AND (l.name LIKE ? OR l.address LIKE ?) ");
            String like = "%" + query.trim() + "%";
            params.add(like);
            params.add(like);
        }

        if (paymentType != null && !paymentType.trim().isEmpty()) {
            sql.append("AND l.paymentType = ? ");
            params.add(paymentType.trim());
        }

        sql.append("ORDER BY l.name ASC");

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Lot lot = createLotFromResult(rs);
                    lot.setFavorited(rs.getBoolean("isFavorited"));
                    results.add(lot);
                }
            }
        }

        return results;
    }

    public Optional<Lot> getLotById(int lotId) throws SQLException {
        final String sql = "SELECT l.lotId, l.name, l.address, l.totalCapacity, l.paymentType, l.latitude, l.longitude, " +
                "sr.packedLevel, sr.hasOpenSpots, sr.reportedAt AS lastReported " +
                "FROM lot l " +
                "LEFT JOIN spot_report sr ON sr.reportId = (SELECT reportId FROM spot_report WHERE lotId = l.lotId ORDER BY reportedAt DESC LIMIT 1) " +
                "WHERE l.lotId = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, lotId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(createLotFromResult(rs));
                }
            }
        }

        return Optional.empty();
    }

    public boolean createLot(String name, String address, int totalCapacity, String paymentType,
                             double latitude, double longitude) throws SQLException {
        final String sql = "INSERT INTO lot (name, address, totalCapacity, paymentType, latitude, longitude) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, address);
            pstmt.setInt(3, totalCapacity);
            pstmt.setString(4, paymentType);
            pstmt.setDouble(5, latitude);
            pstmt.setDouble(6, longitude);
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean updateLot(int lotId, String name, String address, int totalCapacity, String paymentType,
                             double latitude, double longitude) throws SQLException {
        final String sql = "UPDATE lot SET name = ?, address = ?, totalCapacity = ?, paymentType = ?, latitude = ?, longitude = ? WHERE lotId = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, address);
            pstmt.setInt(3, totalCapacity);
            pstmt.setString(4, paymentType);
            pstmt.setDouble(5, latitude);
            pstmt.setDouble(6, longitude);
            pstmt.setInt(7, lotId);
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deleteLot(int lotId) throws SQLException {
        final String deleteReportsSql = "DELETE FROM spot_report WHERE lotId = ?";
        final String deleteLotSql = "DELETE FROM lot WHERE lotId = ?";

        try (Connection conn = dataSource.getConnection()) {
            boolean previousAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            try (PreparedStatement deleteReports = conn.prepareStatement(deleteReportsSql);
                 PreparedStatement deleteLot = conn.prepareStatement(deleteLotSql)) {
                deleteReports.setInt(1, lotId);
                deleteReports.executeUpdate();

                deleteLot.setInt(1, lotId);
                boolean deleted = deleteLot.executeUpdate() > 0;

                conn.commit();
                return deleted;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(previousAutoCommit);
            }
        }
    }

    private Lot createLotFromResult(ResultSet rs) throws SQLException {
        Timestamp reportedAt = rs.getTimestamp("lastReported");
        String lastReported = null;
        if (reportedAt != null) {
            lastReported = reportedAt.toLocalDateTime().format(REPORT_FORMAT);
        }

        return new Lot(
                rs.getInt("lotId"),
                rs.getString("name"),
                rs.getString("address"),
                rs.getInt("totalCapacity"),
                rs.getString("paymentType"),
                rs.getDouble("latitude"),
                rs.getDouble("longitude"),
                rs.getString("packedLevel"),
                rs.getBoolean("hasOpenSpots"),
                lastReported
        );
    }
}
