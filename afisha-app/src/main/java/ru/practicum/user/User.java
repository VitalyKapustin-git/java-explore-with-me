package ru.practicum.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.event.Event;
import ru.practicum.request.Request;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    private String email;

    @OneToMany(mappedBy = "requestor")
    private List<Request> requests;

    @OneToMany(mappedBy = "initiator")
    private List<Event> events;

}

