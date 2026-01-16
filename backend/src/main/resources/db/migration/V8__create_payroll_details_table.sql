-- 給与明細詳細テーブル
CREATE TABLE payroll_details (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    payroll_id UUID NOT NULL REFERENCES monthly_payrolls(id) ON DELETE CASCADE,
    shift_id UUID REFERENCES shifts(id) ON DELETE SET NULL,
    date DATE NOT NULL,
    hours_worked DECIMAL(5, 2) NOT NULL,
    hourly_wage INTEGER NOT NULL,
    base_amount INTEGER NOT NULL,
    allowances JSONB DEFAULT '[]',
    subtotal INTEGER NOT NULL
);

-- インデックス
CREATE INDEX idx_payroll_details_payroll_id ON payroll_details(payroll_id);