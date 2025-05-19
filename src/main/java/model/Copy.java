package model;

public class Copy {
    private final int copyId; /**Tanken är att det här skall vara den unika barcoden*/
    private final Media media; //Bättre att skapa mediaobjekt. Enklare att hämta mediaNamn till kvittot
    private final boolean referenceCopy; //Går att argumentera för att detta inte ska vara final, för projektets skull så får den vara det
    private AvailabilityStatus availability;
    private Location location;

    public enum AvailabilityStatus {
        AVAILABLE, LOANED, LOST, DAMAGED
    }

    public Copy(int copyId, Media media, boolean referenceCopy, AvailabilityStatus availability, Location location) {
        this.copyId = copyId;
        this.media = media;
        this.referenceCopy = referenceCopy;
        this.availability = availability;
        this.location = location;
    }

    public int getCopyId() {
        return copyId;
    }

    public int getMediaId() {
        return media.getMediaId();
    }
    public String getMediaName(){
        return media.getMediaName();
    }
    public Media.MediaType getMediaType(){
        return media.getMediaType();
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
