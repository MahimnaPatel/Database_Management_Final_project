package uga.menik.csx370.models;

public class ReporterStat {
    private final String firstName;
    private final String lastName;
    private final String username;
    private final int reportCount;

    public ReporterStat(String firstName, String lastName, String username, int reportCount) {
        this.firstName   = firstName;
        this.lastName    = lastName;
        this.username    = username;
        this.reportCount = reportCount;
    }

    public String getFirstName()   { return firstName; }
    public String getLastName()    { return lastName; }
    public String getUsername()    { return username; }
    public int    getReportCount() { return reportCount; }
}
