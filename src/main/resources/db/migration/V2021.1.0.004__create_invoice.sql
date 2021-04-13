CREATE TABLE invoice(uuid VARCHAR(36),
                     business_uuid VARCHAR(36) NOT NULL,
                     customer_uuid VARCHAR(36) NOT NULL,
                     amount double(10, 3) NOT NULL,
                     payment_status varchar(10),
                     due_date date,
                     is_deleted tinyint(1) NOT NULL DEFAULT 0,
                     PRIMARY KEY (uuid)
        );

ALTER TABLE invoice
ADD CONSTRAINT business_uuid_fk2 FOREIGN KEY (business_uuid) REFERENCES business(uuid);

ALTER TABLE invoice
ADD CONSTRAINT user_uuid_fk2 FOREIGN KEY (customer_uuid) REFERENCES customer(uuid);