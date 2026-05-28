CREATE TABLE audit_logs (
    id          BIGSERIAL    PRIMARY KEY,
    timestamp   TIMESTAMP    NOT NULL DEFAULT NOW(),
    user_id     INTEGER,
    user_name   VARCHAR(255),
    user_role   VARCHAR(50),
    farm_id     INTEGER,
    farm_name   VARCHAR(255),
    action      VARCHAR(50)  NOT NULL,
    entity_type VARCHAR(100),
    entity_id   VARCHAR(100),
    description VARCHAR(500),
    ip_address  VARCHAR(45)
);

CREATE INDEX idx_audit_logs_timestamp ON audit_logs (timestamp DESC);
CREATE INDEX idx_audit_logs_farm_id   ON audit_logs (farm_id);
CREATE INDEX idx_audit_logs_user_id   ON audit_logs (user_id);
CREATE INDEX idx_audit_logs_action    ON audit_logs (action);
