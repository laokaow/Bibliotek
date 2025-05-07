package model;

public class Copy {
    private final int copyId; /**Tanken är att det här skall vara den unika barcoden*/
    private final int mediaId;
    private final boolean referenceCopy; //Går att argumentera för att detta inte ska vara final, för projektets skull så får den vara det
    private AvailabilityStatus availability;
    private Location location;

    public enum AvailabilityStatus {
        AVAILABLE, LOANED, LOST, DAMAGED
    }

    public Copy(int copyId, int mediaId, boolean referenceCopy, AvailabilityStatus availability) {
        this.copyId = copyId;
        this.mediaId = mediaId;
        this.referenceCopy = referenceCopy;
        this.availability = availability;
    }

    public int getCopyId() {
        return copyId;
    }

    public int getMediaId() {
        return mediaId;
    }

    public boolean isReferenceCopy() {
        return referenceCopy;
    }

    public AvailabilityStatus getAvailability() {
        return availability;
    }
    public void setAvailability(AvailabilityStatus availability) {
        this.availability = availability;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
