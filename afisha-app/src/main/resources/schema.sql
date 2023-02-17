CREATE TABLE IF NOT EXISTS users (
     id      BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
     name    varchar                                 not null,
     email   varchar                                 not null,
     CONSTRAINT pk_user PRIMARY KEY (id),
     CONSTRAINT unique_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS categories (
     id      BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
     name    varchar                                 not null,
     CONSTRAINT pk_category PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS events (
      id      BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
      annotation varchar NOT NULL,
      category_id int NOT NULL,
      created_on timestamp,
      description varchar NOT NULL,
      initiator_id int NOT NULL,
      event_date timestamp NOT NULL,
      paid boolean NOT NULL,
      published_on timestamp,
      participant_limit bigint NOT NULL,
      request_moderation boolean,
      state varchar,
      title varchar NOT NULL,
      lat float NOT NULL,
      lon float NOT NULL,
      CONSTRAINT pk_events PRIMARY KEY (id),
      CONSTRAINT fk_category FOREIGN KEY (category_id) REFERENCES categories (id),
      CONSTRAINT fk_initiator FOREIGN KEY (initiator_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS participations (
      id      BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
      requester_id    int                                 not null,
      event_id    int                                 not null,
      status varchar,
      created timestamp,
      CONSTRAINT pk_participations PRIMARY KEY (id),
      CONSTRAINT fk_requester FOREIGN KEY (requester_id) REFERENCES users (id),
      CONSTRAINT fk_event FOREIGN KEY (event_id) REFERENCES events (id)
);


CREATE TABLE IF NOT EXISTS compilations (
      id      BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
      title    varchar                                 not null,
      pinned   boolean NOT NULL,
      CONSTRAINT pk_compilations PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS compilations_events (
        compilation_id      BIGINT NOT NULL,
        events_id    BIGINT  not null,
        CONSTRAINT pk_ce PRIMARY KEY (compilation_id, events_id),
        CONSTRAINT fk_ce_comp FOREIGN KEY (compilation_id) REFERENCES compilations (id),
        CONSTRAINT fk_ce_ev FOREIGN KEY (events_id) REFERENCES events (id)
);










