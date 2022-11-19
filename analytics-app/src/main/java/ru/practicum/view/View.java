package ru.practicum.view;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "visits")
public class View {

    @Id
    long id;

    String ip;

}
