package org.adalovelacehackaton.teameleven.ecoscan.api;

public enum ItemType {
    BIO_DEGRADABLE("bio_degradable"),
    CARDBOARD_PAPER("cardboard_paper"),
    OTHER("other"),
    PLASTIC("plastic"),
    GLASS("glass"),
    TEXTILE("textile"),
    METAL("metal");


    private String name;

    ItemType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static ItemType get(String type) {
        switch (type) {
            case "bio_degradable":
                return BIO_DEGRADABLE;
            case "cardboard_paper":
                return CARDBOARD_PAPER;
            case "other":
                return OTHER;
            case "plastic":
                return PLASTIC;
            case "glass":
                return GLASS;
            case "textile":
                return TEXTILE;
            case "metal":
                return METAL;
            default:
                return null;
        }
    }
}
