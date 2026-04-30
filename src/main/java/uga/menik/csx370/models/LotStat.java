package uga.menik.csx370.models;

public class LotStat {
    private final String lotName;
    private final String packedLevel;
    private final boolean hasOpenSpots;
    private final String paymentType;
    private final String lastReported;

    public LotStat(String lotName, String packedLevel, boolean hasOpenSpots,
                   String paymentType, String lastReported) {
        this.lotName      = lotName;
        this.packedLevel  = packedLevel;
        this.hasOpenSpots = hasOpenSpots;
        this.paymentType  = paymentType;
        this.lastReported = lastReported;
    }

    public String  getLotName()     { return lotName; }
    public String  getPackedLevel() { return packedLevel; }
    public boolean isHasOpenSpots() { return hasOpenSpots; }
    public String  getPaymentType() { return paymentType; }
    public String  getLastReported(){ return lastReported; }
}
