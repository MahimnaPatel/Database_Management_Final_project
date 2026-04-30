package uga.menik.csx370.models;

public class HourStat {
    private final String hourLabel;
    private final int reportCount;

    public HourStat(int hour, int reportCount) {
        this.hourLabel = formatHour(hour);
        this.reportCount = reportCount;
    }

    private static String formatHour(int h) {
        if (h == 0)  return "12 AM";
        if (h < 12)  return h + " AM";
        if (h == 12) return "12 PM";
        return (h - 12) + " PM";
    }

    public String getHourLabel()  { return hourLabel; }
    public int    getReportCount() { return reportCount; }
}
