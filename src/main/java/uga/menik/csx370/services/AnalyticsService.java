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

import uga.menik.csx370.models.HourStat;
import uga.menik.csx370.models.LotStat;
import uga.menik.csx370.models.ReporterStat;

@Service
public class AnalyticsService {

    private final DataSource dataSource;

    @Autowired
    public AnalyticsService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // Returns the hours of day when Busy/Full reports are most common.
    // Uses idx_report_time index on reportedAt.
    // URL: /analytics
    public List<HourStat> getBusiestHours() throws SQLException {
        final String sql =
            "SELECT HOUR(reportedAt) AS hour, COUNT(*) AS reportCount " +
            "FROM spot_report " +
            "WHERE packedLevel IN ('Busy', 'Full') " +
            "GROUP BY HOUR(reportedAt) " +
            "ORDER BY reportCount DESC " +
            "LIMIT 8";

        List<HourStat> results = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                results.add(new HourStat(rs.getInt("hour"), rs.getInt("reportCount")));
            }
        }
        return results;
    }

    // Returns lots ordered from most to least available based on their
    // most recent crowd-sourced report. Uses idx_report_lot index on lotId.
    // URL: /analytics
    public List<LotStat> getMostAvailableLots() throws SQLException {
        final String sql =
            "SELECT l.name, sr.packedLevel, sr.hasOpenSpots, l.paymentType, " +
            "       DATE_FORMAT(sr.reportedAt, '%b %d %h:%i %p') AS lastReported " +
            "FROM lot l " +
            "JOIN spot_report sr ON l.lotId = sr.lotId " +
            "WHERE sr.reportedAt = ( " +
            "    SELECT MAX(sr2.reportedAt) FROM spot_report sr2 WHERE sr2.lotId = l.lotId " +
            ") " +
            "ORDER BY CASE sr.packedLevel " +
            "    WHEN 'Empty'    THEN 1 " +
            "    WHEN 'Light'    THEN 2 " +
            "    WHEN 'Moderate' THEN 3 " +
            "    WHEN 'Busy'     THEN 4 " +
            "    WHEN 'Full'     THEN 5 END ASC " +
            "LIMIT 10";

        List<LotStat> results = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                results.add(new LotStat(
                    rs.getString("name"),
                    rs.getString("packedLevel"),
                    rs.getBoolean("hasOpenSpots"),
                    rs.getString("paymentType"),
                    rs.getString("lastReported")
                ));
            }
        }
        return results;
    }

    // Returns the top 10 users ranked by number of spot reports submitted.
    // Aggregation join: user JOIN spot_report.
    // URL: /analytics
    public List<ReporterStat> getTopReporters() throws SQLException {
        final String sql =
            "SELECT u.firstName, u.lastName, u.username, COUNT(*) AS reportCount " +
            "FROM user u " +
            "JOIN spot_report sr ON u.userId = sr.userId " +
            "GROUP BY u.userId, u.firstName, u.lastName, u.username " +
            "ORDER BY reportCount DESC " +
            "LIMIT 10";

        List<ReporterStat> results = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                results.add(new ReporterStat(
                    rs.getString("firstName"),
                    rs.getString("lastName"),
                    rs.getString("username"),
                    rs.getInt("reportCount")
                ));
            }
        }
        return results;
    }
}
