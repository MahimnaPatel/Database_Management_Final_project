package uga.menik.csx370.models;

public class RecentReport {
    private final String lotName;
    private final String packedLevel;
    private final boolean hasOpenSpots;
    private final String reporter;
    private final String reportedAt;

    public RecentReport(String lotName, String packedLevel, boolean hasOpenSpots,
                        String reporter, String reportedAt) {
        this.lotName = lotName;
        this.packedLevel = packedLevel;
        this.hasOpenSpots = hasOpenSpots;
        this.reporter = reporter;
        this.reportedAt = reportedAt;
    }

    public String getLotName() {
        return lotName;
    }

    public String getPackedLevel() {
        return packedLevel;
    }

    public boolean isHasOpenSpots() {
        return hasOpenSpots;
    }

    public String getReporter() {
        return reporter;
    }
    
    public String getReportedAt() {
        return reportedAt;
    }
}
