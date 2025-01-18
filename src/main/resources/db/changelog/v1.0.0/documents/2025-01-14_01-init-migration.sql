--liquibase formatted sql

--changeset master-detail:document-1
CREATE SEQUENCE documents_documents_id_seq
  INCREMENT BY 1
  START WITH 1
  NO CYCLE;

CREATE TABLE IF NOT EXISTS documents (
                                         document_id BIGINT DEFAULT nextval('documents_documents_id_seq') PRIMARY KEY,
                                         doc_number VARCHAR(50) NOT NULL,
                                         doc_date TIMESTAMP NOT NULL,
                                         total_sum NUMERIC(18, 2) NOT NULL,  -- хранит пересчитанную сумму из деталей
                                         notes TEXT,

                                         CONSTRAINT uq_documents_docnumber UNIQUE (doc_number)  -- уникальный номер документа
);

COMMENT ON TABLE documents IS 'Таблица мастер: документы';
COMMENT ON COLUMN documents.document_id IS 'Первичный ключ документа';
COMMENT ON COLUMN documents.doc_number   IS 'Номер документа (уникальный)';
COMMENT ON COLUMN documents.doc_date     IS 'Дата документа';
COMMENT ON COLUMN documents.total_sum    IS 'Сумма по документу (агрегат по позициям)';
COMMENT ON COLUMN documents.notes        IS 'Примечание';

--rollback DROP TABLE documents;
--rollback DROP SEQUENCE documents_documents_id_seq;