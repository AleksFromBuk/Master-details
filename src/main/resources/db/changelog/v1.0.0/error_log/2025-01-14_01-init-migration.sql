--liquibase formatted sql

-- changeset master-detail:error_log-1
CREATE SEQUENCE error_log_error_log_id_seq
  INCREMENT BY 1
  START WITH 1
  NO CYCLE;

CREATE TABLE IF NOT EXISTS error_log (
                                         error_id BIGINT DEFAULT nextval('error_log_error_log_id_seq') PRIMARY KEY,
                                         error_time TIMESTAMP NOT NULL,
                                         error_type VARCHAR(100) NOT NULL,
                                         message TEXT NOT NULL
);

COMMENT ON TABLE error_log IS 'Лог ошибок при операциях (например, нарушение уникальности)';
COMMENT ON COLUMN error_log.error_id IS 'PK лога ошибок';
COMMENT ON COLUMN error_log.error_time IS 'Время возникновения ошибки';
COMMENT ON COLUMN error_log.error_type IS 'Классификация/тип ошибки';
COMMENT ON COLUMN error_log.message   IS 'Подробное описание (stacktrace, поле и т.д.)';

--rollback DROP TABLE error_log;
--rollback DROP SEQUENCE error_log_error_log_id_seq;
