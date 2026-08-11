DELETE FROM tb_transaction
WHERE id IN (
    SELECT id
    FROM (
        SELECT id,
               ROW_NUMBER() OVER (
                   PARTITION BY financial_transaction_id
                   ORDER BY id ASC
               ) AS row_num
        FROM tb_transaction
    ) t
    WHERE t.row_num > 1
);

ALTER TABLE tb_transaction
ADD CONSTRAINT uk_tb_transaction_financial_transaction_id UNIQUE (financial_transaction_id);