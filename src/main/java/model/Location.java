package model;

public class Location {
    private final int locationId;
    private final String sectionName;
    private final String shelfName;
    private final String position;

    public Location(int locationId, String sectionName, String shelfName, String position) {
        this.locationId = locationId;
        this.sectionName = sectionName;
        this.shelfName = shelfName;
        this.position = position;
    }

    public int getLocationId() {
        return locationId;
    }

    public String getSectionName() {
        return sectionName;
    }

    public String getShelfName() {
        return shelfName;
    }

    public String getPosition() {
        return position;
    }
}
