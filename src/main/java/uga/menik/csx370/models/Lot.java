package uga.menik.csx370.models;

public class Lot{
    private final int lotId;
    private final String name;
    private final String address;
    private final int totalCapacity;
    private final String paymentType;
    private final double latitude;
    private final double longitude;

    private final String packedLevel;
    private final boolean hasOpenSpots;
    private final String lastReported;

    public Lot(int lotId, String name, String address, int totalCapacity, String paymentType,
               double latitude, double longitude, String packedLevel, boolean hasOpenSpots, String lastReported) {
        this.lotId = lotId;
        this.name = name;
        this.address = address;
        this.totalCapacity = totalCapacity;
        this.paymentType = paymentType;
        this.latitude = latitude;
        this.longitude = longitude;
        this.packedLevel = packedLevel;
        this.hasOpenSpots = hasOpenSpots;
        this.lastReported = lastReported;
    }

    public Lot(int lotId, String name, String address, int totalCapacity, String paymentType,
               double latitude, double longitude) {
        this(lotId, name, address, totalCapacity, paymentType, latitude, longitude, null, false, null);
    }

    public int getLotId(){
        return lotId;
    }

    public String getName(){
        return name;
    }
    public String getAddress(){
        return address;
    }
    public int getTotalCapacity(){
        return totalCapacity;
    }
    public String getPaymentType(){
        return paymentType;
    }
    public double getLatitude(){
        return latitude;
    }
    public double getLongitude(){
        return longitude;
    }
    public String getPackedLevel() {
        return packedLevel;
    }

    public boolean hasOpenSpots() {
        return hasOpenSpots;
    }

    public boolean isHasOpenSpots() {
        return hasOpenSpots;
    }

    public String getLastReported() {
        return lastReported;
    }
}
