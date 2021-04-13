CREATE TABLE business_to_customers(id int(11) NOT NULL AUTO_INCREMENT,
                     business_uuid VARCHAR(36) NOT NULL,
                     customer_uuid VARCHAR(36) NOT NULL,
                     remainder_enabled tinyint(1) NOT NULL DEFAULT 1,
                     PRIMARY KEY (id)
        );

ALTER TABLE business_to_customers
ADD CONSTRAINT business_uuid_fk3 FOREIGN KEY (business_uuid) REFERENCES business(uuid);

ALTER TABLE business_to_customers
ADD CONSTRAINT customer_uuid_fk2 FOREIGN KEY (customer_uuid) REFERENCES customer(uuid);