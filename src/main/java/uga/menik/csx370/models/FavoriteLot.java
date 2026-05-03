package uga.menik.csx370.models;

public class FavoriteLot {
    private final int lotId;
    private final String lotName;
    private final String address;
    private final String paymentType;
    private final String packedLevel;
    private final boolean hasOpenSpots;
    private final String lastReported;

    public FavoriteLot(int lotId, String lotName, String address, String paymentType,
                       String packedLevel, boolean hasOpenSpots, String lastReported) {
        this.lotId = lotId;
        this.lotName = lotName;
        this.address = address;
        this.paymentType = paymentType;
        this.packedLevel = packedLevel;
        this.hasOpenSpots = hasOpenSpots;
        this.lastReported = lastReported;
    }

    public int getLotId() {
        return lotId;
    }

    public String getLotName() {
        return lotName;
    }

    public String getAddress() {
        return address;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public String getPackedLevel() {
        return packedLevel;
    }

    public boolean isHasOpenSpots() {
        return hasOpenSpots;
    }

    public String getLastReported() {
        return lastReported;
    }
                        
}
