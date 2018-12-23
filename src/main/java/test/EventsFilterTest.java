package test;

import database.entity.Event;
import model.EventsFilter;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EventsFilterTest {

    @org.junit.Test
    public void verifyTagPredicateForTwoTags() {
        //given
        List<Event> events = new ArrayList<>();
        Event eventWithTag1And2 = new Event();
        eventWithTag1And2.setTags(Arrays.asList("1", "2"));
        Event eventWithTag1 = new Event();
        eventWithTag1.setTags(Arrays.asList("1"));
        Event eventWithTag4 = new Event();
        eventWithTag4.setTags(Arrays.asList("4"));
        Event eventWithTag2 = new Event();
        eventWithTag2.setTags(Arrays.asList("2"));
        Event eventWithoutTag = new Event();

        events.add(eventWithTag1);
        events.add(eventWithTag1And2);
        events.add(eventWithTag4);
        events.add(eventWithTag2);
        events.add(eventWithoutTag);

        HttpServletRequest mockedRequest = mock(HttpServletRequest.class);
        Map<String, String[]> parametersMap = new HashMap<>();
        parametersMap.put("tagId", new String[]{"1", "2"});
        when(mockedRequest.getParameterMap()).thenReturn(parametersMap);
        EventsFilter eventsFilter = new EventsFilter(mockedRequest);

        //when
        List<Event> filteredEvents = eventsFilter.filterEvents(events);

        //then
        assertTrue(filteredEvents.contains(eventWithTag1));
        assertTrue(filteredEvents.contains(eventWithTag1And2));
        assertTrue(filteredEvents.contains(eventWithTag2));
        assertFalse(filteredEvents.contains(eventWithTag4));
        assertFalse(filteredEvents.contains(eventWithoutTag));
    }

    @org.junit.Test
    public void verifyTagPredicateForOneTag() {
        //given
        List<Event> events = new ArrayList<>();
        Event eventWithTag1And2 = new Event();
        eventWithTag1And2.setTags(Arrays.asList("1", "2"));
        Event eventWithTag1 = new Event();
        eventWithTag1.setTags(Arrays.asList("1"));
        Event eventWithTag4 = new Event();
        eventWithTag4.setTags(Arrays.asList("4"));
        Event eventWithTag2 = new Event();
        eventWithTag2.setTags(Arrays.asList("2"));
        Event eventWithoutTag = new Event();

        events.add(eventWithTag1);
        events.add(eventWithTag1And2);
        events.add(eventWithTag4);
        events.add(eventWithTag2);
        events.add(eventWithoutTag);

        HttpServletRequest mockedRequest = mock(HttpServletRequest.class);
        Map<String, String[]> parametersMap = new HashMap<>();
        parametersMap.put("tagId", new String[]{"1"});
        when(mockedRequest.getParameterMap()).thenReturn(parametersMap);
        EventsFilter eventsFilter = new EventsFilter(mockedRequest);

        //when
        List<Event> filteredEvents = eventsFilter.filterEvents(events);

        //then
        assertTrue(filteredEvents.contains(eventWithTag1));
        assertTrue(filteredEvents.contains(eventWithTag1And2));
        assertFalse(filteredEvents.contains(eventWithTag2));
        assertFalse(filteredEvents.contains(eventWithTag4));
        assertFalse(filteredEvents.contains(eventWithoutTag));
    }

    @org.junit.Test
    public void verifyTagPredicateForOneTagAndNoResults() {
        //given
        List<Event> events = new ArrayList<>();
        Event eventWithTag1And2 = new Event();
        eventWithTag1And2.setTags(Arrays.asList("1", "2"));
        Event eventWithTag1 = new Event();
        eventWithTag1.setTags(Arrays.asList("1"));
        Event eventWithTag4 = new Event();
        eventWithTag4.setTags(Arrays.asList("4"));
        Event eventWithTag2 = new Event();
        eventWithTag2.setTags(Arrays.asList("2"));
        Event eventWithoutTag = new Event();

        events.add(eventWithTag1);
        events.add(eventWithTag1And2);
        events.add(eventWithTag4);
        events.add(eventWithTag2);
        events.add(eventWithoutTag);

        HttpServletRequest mockedRequest = mock(HttpServletRequest.class);
        Map<String, String[]> parametersMap = new HashMap<>();
        parametersMap.put("tagId", new String[]{"8"});
        when(mockedRequest.getParameterMap()).thenReturn(parametersMap);
        EventsFilter eventsFilter = new EventsFilter(mockedRequest);

        //when
        List<Event> filteredEvents = eventsFilter.filterEvents(events);

        //then
        assertFalse(filteredEvents.contains(eventWithTag1));
        assertFalse(filteredEvents.contains(eventWithTag1And2));
        assertFalse(filteredEvents.contains(eventWithTag2));
        assertFalse(filteredEvents.contains(eventWithTag4));
        assertFalse(filteredEvents.contains(eventWithoutTag));
    }
    @org.junit.Test
    public void verifyTagPredicateForNoTag() {
        //given
        List<Event> events = new ArrayList<>();
        Event eventWithTag1And2 = new Event();
        eventWithTag1And2.setTags(Arrays.asList("1", "2"));
        Event eventWithTag1 = new Event();
        eventWithTag1.setTags(Arrays.asList("1"));
        Event eventWithTag4 = new Event();
        eventWithTag4.setTags(Arrays.asList("4"));
        Event eventWithTag2 = new Event();
        eventWithTag2.setTags(Arrays.asList("2"));
        Event eventWithoutTag = new Event();

        events.add(eventWithTag1);
        events.add(eventWithTag1And2);
        events.add(eventWithTag4);
        events.add(eventWithTag2);
        events.add(eventWithoutTag);

        HttpServletRequest mockedRequest = mock(HttpServletRequest.class);
        Map<String, String[]> parametersMap = new HashMap<>();
        when(mockedRequest.getParameterMap()).thenReturn(parametersMap);
        EventsFilter eventsFilter = new EventsFilter(mockedRequest);

        //when
        List<Event> filteredEvents = eventsFilter.filterEvents(events);

        //then
        assertTrue(filteredEvents.contains(eventWithTag1));
        assertTrue(filteredEvents.contains(eventWithTag1And2));
        assertTrue(filteredEvents.contains(eventWithTag2));
        assertTrue(filteredEvents.contains(eventWithTag4));
        assertTrue(filteredEvents.contains(eventWithoutTag));
    }
}