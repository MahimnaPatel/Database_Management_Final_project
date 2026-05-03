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

import uga.menik.csx370.models.LotOption;
import uga.menik.csx370.models.SpotReport;

@Service
public class ReportService {

    private final DataSource dataSource;

    @Autowired
    public ReportService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<LotOption> getAllLots() throws SQLException {
        final String sql = "SELECT lotId, name FROM lot ORDER BY name ASC";
        List<LotOption> lots = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                lots.add(new LotOption(rs.getInt("lotId"), rs.getString("name")));
            }
        }
        return lots;
    }

    public void submitReport(int userId, int lotId, String packedLevel,
                             boolean hasOpenSpots, String notes) throws SQLException {
        final String sql =
            "INSERT INTO spot_report (userId, lotId, packedLevel, hasOpenSpots, notes) " +
            "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, lotId);
            pstmt.setString(3, packedLevel);
            pstmt.setBoolean(4, hasOpenSpots);
            if (notes == null || notes.trim().isEmpty()) {
                pstmt.setNull(5, java.sql.Types.NULL);
            } else {
                pstmt.setString(5, notes.trim());
            }
            pstmt.executeUpdate();
        }
    }

    public List<SpotReport> getReportsByLot(int lotId) throws SQLException {
        final String sql =
            "SELECT u.username, sr.packedLevel, sr.hasOpenSpots, " +
            "       DATE_FORMAT(CONVERT_TZ(sr.reportedAt, '+00:00', '-04:00'), '%b %d %h:%i %p') AS reportedAt, " +
            "       sr.notes " +
            "FROM spot_report sr " +
            "JOIN user u ON sr.userId = u.userId " +
            "WHERE sr.lotId = ? " +
            "ORDER BY sr.reportedAt DESC " +
            "LIMIT 50";
        List<SpotReport> reports = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, lotId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    reports.add(new SpotReport(
                        rs.getString("username"),
                        rs.getString("packedLevel"),
                        rs.getBoolean("hasOpenSpots"),
                        rs.getString("reportedAt"),
                        rs.getString("notes")
                    ));
                }
            }
        }
        return reports;
    }

    public String getLotName(int lotId) throws SQLException {
        final String sql = "SELECT name FROM lot WHERE lotId = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, lotId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("name");
                }
            }
        }
        return null;
    }
}
