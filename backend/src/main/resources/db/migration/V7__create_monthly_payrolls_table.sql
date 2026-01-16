-- 月次給与テーブル
CREATE TABLE monthly_payrolls (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    year_month VARCHAR(7) NOT NULL,
    total_hours DECIMAL(10, 2) NOT NULL DEFAULT 0,
    base_pay INTEGER NOT NULL DEFAULT 0,
    allowance_pay INTEGER NOT NULL DEFAULT 0,
    total_pay INTEGER NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    finalized_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- インデックス
CREATE INDEX idx_monthly_payrolls_user_id ON monthly_payrolls(user_id);
CREATE INDEX idx_monthly_payrolls_year_month ON monthly_payrolls(year_month);
CREATE UNIQUE INDEX idx_monthly_payrolls_user_month ON monthly_payrolls(user_id, year_month);