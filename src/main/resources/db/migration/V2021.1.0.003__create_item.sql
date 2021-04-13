CREATE TABLE item(
                  uuid VARCHAR(36),
                  name VARCHAR(36) NOT NULL,
                  price decimal(10,3) NOT NULL,
                  PRIMARY KEY (uuid),
                  UNIQUE (name)
                );
