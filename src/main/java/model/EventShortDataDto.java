package model;

import database.entity.Event;

public class EventShortDataDto {
    private String id;
    private String title;
    private String ownerId;
    private String description;
    private String photoUrl;
    private String ownerName;
    private boolean didUserJoin;

    public EventShortDataDto(Event event, String ownerName, boolean didUserJoin) {
        id = event.getId();
        title = event.getTitle();
        ownerId = event.getOwnerId();
        description = event.getDescription();
        photoUrl = event.getPhotoUrl();
        this.ownerName = ownerName;
        this.didUserJoin = didUserJoin;
    }
}
