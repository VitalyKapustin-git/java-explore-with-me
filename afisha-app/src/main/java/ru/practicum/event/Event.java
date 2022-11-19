package ru.practicum.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.category.Category;
import ru.practicum.compilation.Compilation;
import ru.practicum.request.Request;
import ru.practicum.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "events")
public class Event {

    @Id
    private long id;

    private String annotation;

    @ManyToOne
    private Category category;

    private LocalDateTime createdOn;

    @ManyToOne
    private User initiator;

    private LocalDateTime eventDate;

    @JsonProperty("paid")
    boolean paid;

    private int participantLimit;

    private LocalDateTime publishedOn;

    private boolean requestModeration;

    @Enumerated
    private EventState state;

    private String title;

    private float lat;

    private float lon;

    @ManyToMany
    private List<Compilation> compilations;

    @OneToMany(mappedBy = "event")
    private List<Request> requests;

}
