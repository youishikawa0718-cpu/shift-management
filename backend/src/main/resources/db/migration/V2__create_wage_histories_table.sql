-- 時給履歴テーブル
CREATE TABLE wage_histories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    hourly_wage INTEGER NOT NULL,
    effective_from DATE NOT NULL,
    effective_to DATE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- インデックス
CREATE INDEX idx_wage_histories_user_id ON wage_histories(user_id);
CREATE INDEX idx_wage_histories_effective_from ON wage_histories(effective_from);