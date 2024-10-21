package com.mycompany.myapp.service.dto;

import java.io.Serializable;

public class DashboardStatsDTO implements Serializable {

    private Long totalEvents;
    private Long totalParticipants;
    private Long upcomingEvents;
    private Long ongoingEvents;
    private Long completedEvents;

    // Getters and Setters
    public Long getTotalEvents() {
        return totalEvents;
    }

    public void setTotalEvents(Long totalEvents) {
        this.totalEvents = totalEvents;
    }

    public Long getTotalParticipants() {
        return totalParticipants;
    }

    public void setTotalParticipants(Long totalParticipants) {
        this.totalParticipants = totalParticipants;
    }

    public Long getUpcomingEvents() {
        return upcomingEvents;
    }

    public void setUpcomingEvents(Long upcomingEvents) {
        this.upcomingEvents = upcomingEvents;
    }

    public Long getOngoingEvents() {
        return ongoingEvents;
    }

    public void setOngoingEvents(Long ongoingEvents) {
        this.ongoingEvents = ongoingEvents;
    }

    public Long getCompletedEvents() {
        return completedEvents;
    }

    public void setCompletedEvents(Long completedEvents) {
        this.completedEvents = completedEvents;
    }
}
