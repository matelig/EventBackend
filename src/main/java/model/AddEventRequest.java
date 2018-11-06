package model;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Date;

public class AddEventRequest {
    @JsonProperty(value = "name")
    private String name;
    @JsonProperty(value = "description")
    private String description;
    @JsonProperty(value = "latitude")
    private Long latitude;
    @JsonProperty(value = "longitude")
    private Long longitude;
    @JsonProperty(value = "startDate")
    private Date startDate;
    @JsonProperty(value = "endDate")
    private Date endDate;
    @JsonProperty(value = "showGuestList")
    private boolean showGuestList;
    @JsonProperty(value = "maxParticipants")
    private int maxParticipants;
    @JsonProperty(value = "onlyRegistered")
    private boolean onlyRegistered;
    @JsonProperty(value = "externalUrl")
    private String externalUrl;
    @JsonProperty(value = "categoryId")
    private int categoryId;
    @JsonProperty(value = "cost")
    private Long cost;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getLatitude() {
        return latitude;
    }

    public void setLatitude(Long latitude) {
        this.latitude = latitude;
    }

    public Long getLongitude() {
        return longitude;
    }

    public void setLongitude(Long longitude) {
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

    public String getExternalUrl() {
        return externalUrl;
    }

    public void setExternalUrl(String externalUrl) {
        this.externalUrl = externalUrl;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public Long getCost() {
        return cost;
    }

    public void setCost(Long cost) {
        this.cost = cost;
    }
}
