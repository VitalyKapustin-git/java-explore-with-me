package ru.practicum.compilation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.event.Event;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "compilations")
public class Compilation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String title;

    @Column
    @JsonProperty("pinned")
    private boolean pinned;

    @ManyToMany
    @JoinTable(
            name = "compilations_events",
            inverseJoinColumns = { @JoinColumn(name = "events_id") },
            joinColumns = { @JoinColumn(name = "compilation_id") }
    )
    private List<Event> events;

}
