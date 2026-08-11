CREATE TABLE IF NOT EXISTS tb_transaction (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    transaction_type VARCHAR(50) NOT NULL,
    date_posted TIMESTAMP NOT NULL,
    transaction_amount NUMERIC(19, 2) NOT NULL,
    financial_transaction_id VARCHAR(255) NOT NULL,
    memo VARCHAR(255) NOT NULL
);