package uga.menik.csx370.models;

public class PackedTrend {
    private final String dateLabel;
    private final double avgLevel;
    private final String avgLevelLabel;

    public PackedTrend(String dateLabel, double avgLevel) {
        this.dateLabel = dateLabel;
        this.avgLevel = avgLevel;
        this.avgLevelLabel = toLabel(avgLevel);
    }

    private static String toLabel(double v) {
        if (v < 1.5) {
            return "Empty";
        } else if (v < 2.5) {
            return "Light";
        } else if (v < 3.5) {
            return "Moderate";
        } else if (v < 4.5) {
            return "Busy";
        } else {
            return "Full";
        }
    }

    public String getDateLabel() {
        return dateLabel;
    }

    public String getAvgLevelLabel() {
        return avgLevelLabel;
    }

    public String getAvgLevelString() {
        return String.format("%.2f", avgLevel);
    }
}
