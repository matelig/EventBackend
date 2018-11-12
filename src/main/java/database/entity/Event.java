package database.entity;

import java.util.Date;
import java.util.List;

public class Event {
    private String title;
    private String ownerId;
    private List<String> participantsIds;
    private String description;
    private String photoUrl;
    private Double latitude;
    private Double longitude;
    private Date startDate;
    private Date endDate;
    private boolean showGuestList;
    private int maxParticipants;
    private boolean onlyRegistered;
    private Category category;
    private List<String> tagIds;
    private Double cost;
    private String externalUrl;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public List<String> getParticipants() {
        return participantsIds;
    }

    public void setParticipants(List<String> participants) {
        this.participantsIds = participants;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public boolean isShowGuestList() {
        return showGuestList;
    }

    public void setShowGuestList(boolean showGuestList) {
        this.showGuestList = showGuestList;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public boolean isOnlyRegistered() {
        return onlyRegistered;
    }

    public void setOnlyRegistered(boolean onlyRegistered) {
        this.onlyRegistered = onlyRegistered;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<String> getTags() {
        return tagIds;
    }

    public void setTags(List<String> tags) {
        this.tagIds = tags;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public String getExternalUrl() {
        return externalUrl;
    }

    public void setExternalUrl(String externalUrl) {
        this.externalUrl = externalUrl;
    }
}
