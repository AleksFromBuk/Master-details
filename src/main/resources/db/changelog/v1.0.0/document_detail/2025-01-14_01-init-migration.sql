--liquibase formatted sql
-- changeset master-detail:document_detail-1
CREATE SEQUENCE document_detail_document_detail_id_seq
  INCREMENT BY 1
  START WITH 1
  NO CYCLE;

CREATE TABLE IF NOT EXISTS document_detail (
                                               detail_id BIGINT DEFAULT nextval('document_detail_document_detail_id_seq') PRIMARY KEY,
                                               document_id BIGINT NOT NULL,
                                               item_name VARCHAR(100) NOT NULL,
                                               item_sum NUMERIC(18, 2) NOT NULL,

                                               CONSTRAINT fk_docdetail_document FOREIGN KEY (document_id)
                                                   REFERENCES documents (document_id)
                                                   ON DELETE CASCADE
);

COMMENT ON TABLE document_detail IS 'Таблица деталей документов (Detail)';
COMMENT ON COLUMN document_detail.detail_id IS 'Первичный ключ строки спецификации';
COMMENT ON COLUMN document_detail.document_id IS 'Ссылка на документ (Master)';
COMMENT ON COLUMN document_detail.item_name IS 'Наименование позиции';
COMMENT ON COLUMN document_detail.item_sum  IS 'Сумма по позиции';

--rollback DROP TABLE document_detail;
--rollback DROP SEQUENCE document_detail_document_detail_id_seq;