package database.entity;

import java.util.List;

public class Event {
    private String id;
    private String title;
    private String ownerId;
    private List<String> participantsIds;
    private String description;
    private String photoUrl;
    private Double latitude;
    private Double longitude;
    private Long startDate;
    private Long endDate;
    private boolean showGuestList;
    private int maxParticipants;
    private boolean onlyRegistered;
    private String categoryId;
    private List<String> tagIds;
    private Double cost;
    private String externalUrl;
    private Address address;

    public Event(String title, String description, Long startDate, boolean showGuestList,
                 int maxParticipants, boolean onlyRegistered, String categoryId, Double cost, String externalUrl) {
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.showGuestList = showGuestList;
        this.maxParticipants = maxParticipants;
        this.onlyRegistered = onlyRegistered;
        this.categoryId = categoryId;
        this.cost = cost;
        this.externalUrl = externalUrl;
    }

    public Event(String title, String description, Double latitude, Double longitude,
                 int maxParticipants, boolean onlyRegistered, String categoryId, Double cost, String externalUrl) {
        this.title = title;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.maxParticipants = maxParticipants;
        this.onlyRegistered = onlyRegistered;
        this.categoryId = categoryId;
        this.cost = cost;
        this.externalUrl = externalUrl;
    }

    public Event() {
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getParticipantsIds() {
        return participantsIds;
    }

    public void setParticipantsIds(List<String> participantsIds) {
        this.participantsIds = participantsIds;
    }

    public List<String> getTagIds() {
        return tagIds;
    }

    public void setTagIds(List<String> tagIds) {
        this.tagIds = tagIds;
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

    public Long getStartDate() {
        return startDate;
    }

    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }

    public Long getEndDate() {
        return endDate;
    }

    public void setEndDate(Long endDate) {
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

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
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

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
