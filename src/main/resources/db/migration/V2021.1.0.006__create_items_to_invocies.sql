CREATE TABLE items_to_invoices(id int(11) NOT NULL AUTO_INCREMENT,
                     invoice_uuid VARCHAR(36) NOT NULL,
                     item_uuid VARCHAR(36) NOT NULL,
                     quantity int(10) NOT NULL,
                     PRIMARY KEY (id)
        );

ALTER TABLE items_to_invoices
ADD CONSTRAINT invoice_uuid_fk2 FOREIGN KEY (invoice_uuid) REFERENCES invoice(uuid);

ALTER TABLE items_to_invoices
ADD CONSTRAINT items_uuid_fk2 FOREIGN KEY (item_uuid) REFERENCES item(uuid);