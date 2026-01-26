# シフト管理システム / Shift Management System

アルバイトのシフト申請・承認・管理を行うフルスタックWebアプリケーションです。

## デモ
🚀 **Live Demo**: http://54.65.90.180

## デプロイ

### 本番環境
- **URL**: http://54.65.90.180
- **AWS EC2** (Amazon Linux 2023)
- **Docker Compose** によるコンテナ管理

### CI/CD パイプライン
GitHub Actions による自動デプロイを構成しています。

`main` ブランチにプッシュすると自動的に:
1. **CI**: ESLint / テスト実行
2. **CD**: EC2へSSHデプロイ

### 必要なGitHub Secrets
| Name | 説明 |
|------|------|
| `EC2_HOST` | EC2のパブリックIPアドレス |
| `EC2_USER` | SSHユーザー名 (`ec2-user`) |
| `EC2_SSH_KEY` | SSH秘密鍵 (.pemファイルの中身) |

## 主な機能

### 👥 アルバイト向け機能
- ✅ シフト申請・取消
- ✅ 確定シフト確認
- ✅ 欠勤申請

### 👨‍💼 店長向け機能
- ✅ シフト申請の承認・却下
- ✅ 確定シフト管理
- ✅ 従業員管理（追加・編集・削除）
- ✅ 欠勤申請の承認・却下

## 技術スタック

### フロントエンド
- **React 19** - UIライブラリ
- **Vite** - 高速ビルドツール
- **TailwindCSS 4** - ユーティリティファーストCSS

### バックエンド
- **Java 21** - プログラミング言語
- **Spring Boot 3.5** - アプリケーションフレームワーク
- **Spring Security + JWT** - 認証・認可
- **Spring Data JPA** - ORM
- **PostgreSQL 16** - データベース
- **Flyway** - DBマイグレーション
- **Swagger/OpenAPI** - API仕様書

### インフラ
- **Docker & Docker Compose** - コンテナ化
- **Nginx** - リバースプロキシ
  
##  プロジェクトの目的

前職で店舗の店長をしていた際、毎月のシフト作成に多くの時間がかかっていました。

- スタッフからの希望休の回収がアナログ（口頭・LINEなど）
- 誰がいつ入れるのか一覧で見づらい
- 人数不足や偏りに気づきにくい
- 修正が発生するたびに全体を作り直す必要がある

といった課題があり、「もっと効率的に管理できる仕組みがあればいいのに」と感じたことがきっかけです。

そこで、**実際の店舗運営でのシフト管理業務を想定したWebアプリケーション**として本システムを開発しました。

単なるカレンダーアプリではなく、

- スタッフがシフト希望を提出  
- 管理者が内容を確認・承認  
- 確定シフトとして一覧管理  

という、実務に近いワークフローを再現することを目的としています。

## 🛠 技術選定の理由

### ■ フロントエンド：React

- コンポーネント単位でUIを分割でき、保守性の高い画面設計が可能
- 状態管理により「ログイン状態」「シフト情報」などの動的データを扱いやすい
- 実務でも広く利用されているため、モダンなフロントエンド開発を経験する目的で採用

### ■ バックエンド：Java / Spring Boot

- エンタープライズ領域での採用実績が多く、堅牢なAPI開発を学ぶために選定
- Spring Boot により、REST API・認証・DB接続などを体系的に実装可能
- 今後業務系システム開発に携わることを想定した技術スタック

### ■ 認証：JWT + Spring Security

- ステートレスな認証方式を実装することで、実務に近いセキュリティ設計を学ぶため
- ユーザーごとの権限管理（管理者 / スタッフ）を安全に制御するために採用

### ■ データベース：PostgreSQL

- 実務利用の多いRDBであり、リレーショナル設計を意識したデータモデリングを行うため採用

### ■ インフラ：Docker / GitHub Actions / AWS EC2

- 開発環境と本番環境の差異をなくすためDockerを使用
- GitHub Actions による自動デプロイでCI/CDを構築
- 実際にクラウド上で動作するアプリケーションとして公開するためAWS EC2へデプロイ

  ## システムアーキテクチャ

```
┌─────────────┐      ┌─────────────┐      ┌─────────────┐
│   React     │ HTTP │   Nginx     │ HTTP │ Spring Boot │
│  Frontend   │─────▶│   (Port 80) │─────▶│   Backend   │
│  (Vite)     │      │             │      │  (Port 8080)│
└─────────────┘      └─────────────┘      └──────┬──────┘
                                                   │
                                                   │ JDBC
                                                   ▼
                                           ┌─────────────┐
                                           │ PostgreSQL  │
                                           │   Database  │
                                           └─────────────┘
```

## ER図

```
users
├── id (PK)
├── email (unique)
├── password_hash
├── name
├── role (EMPLOYEE/MANAGER)
├── is_active
├── created_at
└── updated_at

shift_requests                shifts
├── id (PK)                  ├── id (PK)
├── user_id (FK)             ├── user_id (FK)
├── date                     ├── date
├── start_time               ├── start_time
├── end_time                 ├── end_time
├── status                   ├── actual_start_time
├── requested_at             ├── actual_end_time
└── reviewed_at              ├── created_at
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

## クイックスタート

### 必要な環境
- Docker & Docker Compose
- (開発時) Java 21, Node.js 20+

### 🚀 一発起動（本番モード）

```bash
# プロジェクトをクローン
git clone <repository-url>
cd shift-management

# Docker Composeで全サービスを起動
docker-compose up --build

# アクセス
# - Frontend: http://localhost:3000
# - Backend API: http://localhost:8080

# - Swagger UI: http://localhost:8080/swagger-ui.html
```

### 🛠️ 開発モード

```bash
# 1. データベースのみ起動
docker-compose -f docker-compose.dev.yml up -d

# 2. バックエンドを起動（別ターミナル）
cd backend
./gradlew bootRun

# 3. フロントエンドを起動（別ターミナル）
cd frontend
npm install
npm run dev

# アクセス
# - Frontend: http://localhost:5173
# - Backend API: http://localhost:8080
```

## API一覧

### 認証
| メソッド | エンドポイント | 説明 |
|----------|---------------|------|
| POST | `/auth/register` | ユーザー登録 |
| POST | `/auth/login` | ログイン（JWT取得） |

### シフト申請
| メソッド | エンドポイント | 説明 | 権限 |
|----------|---------------|------|------|
| POST | `/shift-requests` | シフト申請作成 | 全員 |
| GET | `/shift-requests/me` | 自分の申請一覧 | 全員 |
| GET | `/shift-requests` | 全申請一覧 | 店長 |
| GET | `/shift-requests/pending` | 未承認一覧 | 店長 |
| PATCH | `/shift-requests/{id}/approve` | 承認 | 店長 |
| PATCH | `/shift-requests/{id}/reject` | 却下 | 店長 |
| DELETE | `/shift-requests/{id}` | 申請取消 | 全員 |

### 確定シフト
| メソッド | エンドポイント | 説明 | 権限 |
|----------|---------------|------|------|
| GET | `/shifts/me` | 自分のシフト | 全員 |
| GET | `/shifts` | シフト一覧 | 店長 |
| POST | `/shifts/from-request/{id}` | 申請から作成 | 店長 |

### 従業員管理
| メソッド | エンドポイント | 説明 | 権限 |
|----------|---------------|------|------|
| GET | `/employees` | 従業員一覧 | 店長 |
| POST | `/employees` | 従業員追加 | 店長 |
| PATCH | `/employees/{id}` | 従業員更新 | 店長 |
| DELETE | `/employees/{id}` | 従業員削除 | 店長 |

詳細は [バックエンドREADME](./backend/README.md) を参照してください。

## プロジェクト構成

```
shift-management/
├── backend/              # Spring Boot API
│   ├── src/
│   ├── build.gradle.kts
│   ├── Dockerfile
│   └── README.md
├── frontend/             # React SPA
│   ├── src/
│   ├── package.json
│   ├── Dockerfile
│   ├── nginx.conf
│   └── README.md
├── docker-compose.yml        # 本番環境用
├── docker-compose.dev.yml    # 開発環境用
└── README.md                 # このファイル
```

## 開発

### バックエンド開発

```bash
cd backend

# テスト実行
./gradlew test

# ビルド
./gradlew build

# ローカル起動
./gradlew bootRun
```

### フロントエンド開発

```bash
cd frontend

# 依存関係インストール
npm install

# 開発サーバー起動
npm run dev

# Lint
npm run lint

# プロダクションビルド
npm run build
```

## 環境変数

### バックエンド（`backend/src/main/resources/application.yml`）

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/shiftdb
    username: postgres
    password: postgres
  jpa:
    hibernate:
      ddl-auto: validate
  flyway:
    enabled: true

jwt:
  secret: your-secret-key
  expiration: 86400000  # 24時間
```

### フロントエンド（`.env`）

```env
VITE_API_URL=http://localhost:8080
```

## トラブルシューティング

### ポート競合エラー

```bash
# 使用中のポートを確認
# Windows
netstat -ano | findstr :8080
netstat -ano | findstr :3000
netstat -ano | findstr :5432

# プロセスを終了
taskkill /PID <PID> /F
```

### Docker関連

```bash
# コンテナを完全削除して再構築
docker-compose down -v
docker-compose up --build

# ログ確認
docker-compose logs -f backend
docker-compose logs -f frontend
```

## ライセンス

MIT

## 作成者

[Your Name]

