CREATE TABLE IF NOT EXISTS visits (
     id      BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
     app    varchar                                 not null,
     uri   varchar                                 not null,
     ip   varchar                                 not null,
     date   timestamp                                 not null,
     CONSTRAINT pk_visit PRIMARY KEY (id)
);