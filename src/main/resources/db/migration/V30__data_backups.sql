CREATE TABLE data_backups (
    id          SERIAL       PRIMARY KEY,
    backup_type VARCHAR(50)  NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by  VARCHAR(255),
    row_count   INTEGER,
    data_csv    TEXT         NOT NULL
);
