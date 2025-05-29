package model;

public class MediaActor {
    private final int mediaActorId;
    private final int mediaId;
    private final int actorId;

    public MediaActor(int mediaActorId, int mediaId, int actorId) {
        this.mediaActorId = mediaActorId;
        this.mediaId = mediaId;
        this.actorId = actorId;
    }

    public int getMediaActorId() {
        return mediaActorId;
    }

    public int getMediaId() {
        return mediaId;
    }


    public int getActorId() {
        return actorId;
    }

}