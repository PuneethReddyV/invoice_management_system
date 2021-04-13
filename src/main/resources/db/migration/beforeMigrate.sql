#-------------- FIX SCHEMA CHARSET AND COLLATION ------------------------
DROP procedure if exists update_schema_char_set;
DELIMITER $$

CREATE PROCEDURE update_schema_char_set()
  BEGIN
    IF (@@character_set_database != 'utf8mb4' OR @@collation_database != 'utf8mb4_unicode_ci') THEN
      ALTER DATABASE invoice_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
    END IF;
  END$$

DELIMITER ;

CALL update_schema_char_set();
DROP procedure if exists update_schema_char_set;

#-------------- FIX TABLE CHARSET AND COLLATION ------------------------
DROP procedure if exists update_table_char_set;
DELIMITER $$
CREATE PROCEDURE update_table_char_set()
  BEGIN
    DECLARE done INTEGER DEFAULT 0;
    DECLARE tableName VARCHAR(128);
    DECLARE table_cursor CURSOR FOR SELECT TABLE_NAME
                                    FROM `information_schema`.`TABLES`
                                    WHERE table_schema = 'invoice_management'
                                      AND TABLE_TYPE = 'BASE TABLE'
                                      AND TABLE_COLLATION != 'utf8mb4_unicode_ci';
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;
    OPEN table_cursor;
    SET FOREIGN_KEY_CHECKS = 0;
    read_loop: LOOP
      FETCH table_cursor
      INTO tableName;
      IF done
      THEN
        LEAVE read_loop;
      END IF;
      SET @t_sql = CONCAT('ALTER TABLE ', tableName, ' CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci');
      PREPARE stmt FROM @t_sql;
      EXECUTE stmt;
      DEALLOCATE PREPARE stmt;
    END LOOP read_loop;
    SET FOREIGN_KEY_CHECKS = 1;
    CLOSE table_cursor;
  END$$

DELIMITER ;

CALL update_table_char_set();

DROP procedure if exists update_table_char_set;