package uga.menik.csx370.models;

public class LotOption {
    private final int lotId;
    private final String name;

    public LotOption(int lotId, String name) {
        this.lotId = lotId;
        this.name = name;
    }

    public int getLotId() { return lotId; }
    public String getName() { return name; }
}
