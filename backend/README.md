# シフト管理システム API

アルバイトのシフト管理を行うREST APIです。

## 技術スタック

- Java 21
- Spring Boot 3.5.9
- Spring Security + JWT
- Spring Data JPA
- PostgreSQL 16
- Flyway
- Docker

## 機能

### アルバイト向け
- ログイン
- シフト申請
- 確定シフト確認
- 欠勤申請

### 店長向け
- 従業員管理（追加・編集・削除）
- シフト申請の承認・却下
- 確定シフト管理
- 欠勤申請の承認・却下

## セットアップ

### 必要なもの
- Java 21
- Docker

### 起動方法

**オプション1: モノレポのルートから実行（推奨）**
```bash
# ルートディレクトリから
cd ..
docker-compose -f docker-compose.dev.yml up -d  # DBのみ起動
cd backend
./gradlew bootRun
```

**オプション2: バックエンド単体で実行**
```bash
# 1. PostgreSQLを起動
docker compose up -d

# 2. アプリケーションを起動
./gradlew bootRun
```

**オプション3: Dockerで完全起動**
```bash
# ルートディレクトリから全サービス起動
cd ..
docker-compose up --build
```

### APIドキュメントにアクセス
```
http://localhost:8080/swagger-ui.html
```

## 開発

### テスト実行
```bash
./gradlew test
```

### ビルド
```bash
./gradlew build
```

### Dockerビルド
```bash
docker build -t shift-backend .
```

## API一覧

### 認証
| メソッド | エンドポイント | 説明 |
|----------|---------------|------|
| POST | /auth/register | ユーザー登録 |
| POST | /auth/login | ログイン |

### シフト申請
| メソッド | エンドポイント | 説明 | 権限 |
|----------|---------------|------|------|
| POST | /shift-requests | シフト申請作成 | 全員 |
| GET | /shift-requests/me | 自分の申請一覧 | 全員 |
| GET | /shift-requests | 全申請一覧 | 店長 |
| GET | /shift-requests/pending | 未承認一覧 | 店長 |
| PATCH | /shift-requests/{id}/approve | 承認 | 店長 |
| PATCH | /shift-requests/{id}/reject | 却下 | 店長 |
| DELETE | /shift-requests/{id} | 申請取消 | 全員 |

### 確定シフト
| メソッド | エンドポイント | 説明 | 権限 |
|----------|---------------|------|------|
| GET | /shifts/me | 自分のシフト | 全員 |
| GET | /shifts | シフト一覧 | 店長 |
| GET | /shifts/date/{date} | 日別シフト | 店長 |
| POST | /shifts/from-request/{id} | 申請から作成 | 店長 |
| PATCH | /shifts/{id}/actual | 実績入力 | 店長 |

### 従業員管理
| メソッド | エンドポイント | 説明 | 権限 |
|----------|---------------|------|------|
| GET | /employees | 従業員一覧 | 店長 |
| GET | /employees/{id} | 従業員詳細 | 店長 |
| POST | /employees | 従業員追加 | 店長 |
| PATCH | /employees/{id} | 従業員更新 | 店長 |
| DELETE | /employees/{id} | 従業員削除 | 店長 |

### 欠勤申請
| メソッド | エンドポイント | 説明 | 権限 |
|----------|---------------|------|------|
| POST | /absence-requests | 欠勤申請作成 | 全員 |
| GET | /absence-requests/me | 自分の申請一覧 | 全員 |
| GET | /absence-requests | 全申請一覧 | 店長 |
| GET | /absence-requests/pending | 未承認一覧 | 店長 |
| PATCH | /absence-requests/{id}/approve | 承認 | 店長 |
| PATCH | /absence-requests/{id}/reject | 却下 | 店長 |

## 認証

JWTトークン認証を使用しています。

1. `/auth/login` でログインしてトークンを取得
2. リクエストヘッダーに `Authorization: Bearer {token}` を付与

## ER図
```
users
├── id (PK)
├── email
├── password_hash
├── name
├── role (EMPLOYEE/MANAGER)
├── is_active
├── created_at
└── updated_at

shift_requests
├── id (PK)
├── user_id (FK)
├── date
├── start_time
├── end_time
├── status (PENDING/APPROVED/REJECTED)
├── requested_at
└── reviewed_at

shifts
├── id (PK)
├── user_id (FK)
├── date
├── start_time
├── end_time
├── actual_start_time
├── actual_end_time
├── created_at
└── updated_at

absence_requests
├── id (PK)
├── user_id (FK)
├── shift_id (FK)
├── reason
├── status (PENDING/APPROVED/REJECTED)
├── requested_at
└── reviewed_at
```