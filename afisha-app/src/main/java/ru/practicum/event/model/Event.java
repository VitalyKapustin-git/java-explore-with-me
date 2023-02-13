package ru.practicum.event.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.category.model.Category;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.request.model.Request;
import ru.practicum.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "events")
@ToString
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String annotation;

    @ManyToOne
    private Category category;

    @Column
    private LocalDateTime createdOn = LocalDateTime.now();

    @ManyToOne
    private User initiator;

    @Column
    private LocalDateTime eventDate;

    @Column
    @JsonProperty("paid")
    private Boolean paid;

    @Column
    private Long participantLimit;

    @Column
    private LocalDateTime publishedOn;

    @Column
    private Boolean requestModeration;

    @Column
    private String state;

    @Column
    private String title;

    @Column
    private Float lat;

    @Column
    private Float lon;

    @ManyToMany
    private List<Compilation> compilations;

    @OneToMany(mappedBy = "event")
    private List<Request> requests;

    @Column
    private String description;

}
