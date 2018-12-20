package model;

import database.entity.User;

import java.util.List;

public class UserProfileDto {
    private String id;
    private String email;
    private String nickname;
    private List<EventShortDataDto> upcomingEvents;
    private List<EventShortDataDto> createdEvents;

    public UserProfileDto(User user, List<EventShortDataDto> upcomingEvents, List<EventShortDataDto> createdEvents) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.upcomingEvents = upcomingEvents;
        this.createdEvents = createdEvents;
    }
}
