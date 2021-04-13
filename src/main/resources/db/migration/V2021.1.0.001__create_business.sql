CREATE TABLE business(uuid VARCHAR(36),
                                   name VARCHAR(36) NOT NULL,
                                   PRIMARY KEY (uuid),
                                   UNIQUE (name));
