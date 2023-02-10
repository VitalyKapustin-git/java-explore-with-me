package ru.practicum.event;

import ru.practicum.category.CategoryMapper;
import ru.practicum.user.UserMapper;

public class EventMapper {

    public static Event toEvent(EventNewDto eventNewDto) {

        Event event = new Event();

        event.setAnnotation(eventNewDto.getAnnotation());
        event.setCategory(CategoryMapper.toCategory(eventNewDto.getCategoryDto()));
        event.setDescription(eventNewDto.getDescription());
        event.setEventDate(eventNewDto.getEventDate());
        event.setLat(eventNewDto.getLocation().getLat());
        event.setLon(eventNewDto.getLocation().getLon());
        event.setPaid(eventNewDto.getPaid());
        event.setParticipantLimit(eventNewDto.getParticipantLimit());
        event.setRequestModeration(eventNewDto.getRequestModeration());
        event.setTitle(eventNewDto.getTitle());

        return event;

    }

    public static Event toEvent(EventFullDto eventFullDto) {

        Event event = new Event();

        event.setId(eventFullDto.getId());
        event.setAnnotation(eventFullDto.getAnnotation());
        event.setCategory(CategoryMapper.toCategory(eventFullDto.getCategoryDto()));
        event.setCreatedOn(eventFullDto.getCreatedOn());
        event.setInitiator(UserMapper.toUser(eventFullDto.getInitiator()));
        event.setEventDate(eventFullDto.getEventDate());
        event.setPaid(eventFullDto.getPaid());
        event.setParticipantLimit(eventFullDto.getParticipantLimit());
        event.setPublishedOn(eventFullDto.getPublishedOn());
        event.setRequestModeration(eventFullDto.getRequestModeration());
        event.setState(eventFullDto.getState());
        event.setTitle(eventFullDto.getTitle());
        event.setLat(eventFullDto.getLocation().getLat());
        event.setLon(eventFullDto.getLocation().getLon());
        event.setDescription(eventFullDto.getDescription());

        return event;

    }

    public static EventFullDto toEventFullDto(Event event) {

        EventFullDto eventFullDto = new EventFullDto();

        Location location = new Location();
        location.setLon(event.getLon() == null ? 0.0F : event.getLon());
        location.setLat(event.getLat() == null ? 0.0F : event.getLat());

        eventFullDto.setId(event.getId());
        eventFullDto.setAnnotation(event.getAnnotation());
        eventFullDto.setCategoryDto(CategoryMapper.toCategoryDto(event.getCategory()));
        eventFullDto.setCreatedOn(event.getCreatedOn());
        eventFullDto.setInitiator(UserMapper.toUserDto(event.getInitiator()));
        eventFullDto.setEventDate(event.getEventDate());
        eventFullDto.setPaid(event.getPaid());
        eventFullDto.setParticipantLimit(event.getParticipantLimit());
        eventFullDto.setPublishedOn(event.getPublishedOn());
        eventFullDto.setRequestModeration(event.getRequestModeration());
        eventFullDto.setState(event.getState());
        eventFullDto.setTitle(event.getTitle());
        eventFullDto.setLocation(location);
        eventFullDto.setDescription(event.getDescription());

        return eventFullDto;

    }

    public static EventShortDto toEventShortDto(Event event) {

        EventShortDto eventShortDto = new EventShortDto();

        eventShortDto.setId(event.getId());
        eventShortDto.setAnnotation(event.getAnnotation());
        eventShortDto.setCategory(CategoryMapper.toCategoryDto(event.getCategory()));
        eventShortDto.setConfirmedRequests(event.getRequests() == null ? 0L : event.getRequests().size());
        eventShortDto.setEventDate(event.getEventDate());
        eventShortDto.setInitiator(UserMapper.toUserShortDto(event.getInitiator()));
        eventShortDto.setPaid(event.getPaid());
        eventShortDto.setTitle(event.getTitle());
        eventShortDto.setCreatedOn(event.getCreatedOn());

        return eventShortDto;

    }

}
