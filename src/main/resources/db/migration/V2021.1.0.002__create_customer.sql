CREATE TABLE customer(uuid VARCHAR(36),
                                   name VARCHAR(36) NOT NULL,
                                   contact_number VARCHAR(20) NOT NULL,
                                   email_address VARCHAR(36) NOT NULL,
                                   PRIMARY KEY (uuid),
                                   UNIQUE (contact_number),
                                   UNIQUE (email_address)
                                   );