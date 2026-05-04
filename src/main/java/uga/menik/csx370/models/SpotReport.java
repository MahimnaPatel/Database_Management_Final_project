package uga.menik.csx370.models;

public class SpotReport {
    private final String username;
    private final String packedLevel;
    private final boolean hasOpenSpots;
    private final String reportedAt;
    private final String notes;

    public SpotReport(String username, String packedLevel,
                      boolean hasOpenSpots, String reportedAt, String notes) {
        this.username = username;
        this.packedLevel = packedLevel;
        this.hasOpenSpots = hasOpenSpots;
        this.reportedAt = reportedAt;
        this.notes = notes;
    }

    public String getUsername() { return username; }
    public String getPackedLevel() { return packedLevel; }
    public boolean isHasOpenSpots() { return hasOpenSpots; }
    public String getReportedAt() { return reportedAt; }
    public String getNotes() { return notes != null ? notes : ""; }
}
