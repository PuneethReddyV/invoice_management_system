CREATE TABLE invoice_status (
    status VARCHAR(16) NOT NULL,
    PRIMARY KEY (status)
);


INSERT INTO invoice_status (status) VALUES ('paid'),('un_paid');