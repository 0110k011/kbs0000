ALTER TABLE tb_transaction
  ALTER COLUMN financial_transaction_id TYPE UUID
  USING financial_transaction_id::uuid;