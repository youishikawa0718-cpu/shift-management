# シフト管理システム - フロントエンド

React + Vite + TailwindCSSで構築されたシフト管理システムのフロントエンドアプリケーションです。

## 技術スタック

- **React 19** - UIライブラリ
- **Vite 7** - 高速ビルドツール
- **TailwindCSS 4** - ユーティリティファーストCSSフレームワーク
- **ESLint** - コード品質管理

## セットアップ

### 必要なもの
- Node.js 20以上
- npm または pnpm

### 起動方法

**オプション1: 開発サーバー起動**
```bash
# 依存関係のインストール
npm install

# 開発サーバー起動
npm run dev

# ブラウザで開く
# http://localhost:5173
```

**オプション2: Dockerで起動**
```bash
# ルートディレクトリから全サービス起動
cd ..
docker-compose up --build
```

## スクリプト

```bash
# 開発サーバー起動
npm run dev

# プロダクションビルド
npm run build

# ビルドをプレビュー
npm run preview

# Lint実行
npm run lint
```

## プロジェクト構造

```
frontend/
├── public/          # 静的ファイル
├── src/
│   ├── App.jsx     # メインアプリケーション
│   ├── main.jsx    # エントリーポイント
│   └── index.css   # グローバルスタイル
├── index.html
├── vite.config.js
├── package.json
├── Dockerfile
└── nginx.conf      # 本番環境用Nginx設定
```

## 環境変数

`.env` ファイルを作成してバックエンドAPIのURLを設定します。

```env
VITE_API_URL=http://localhost:8080
```

## 開発

### 新しいコンポーネントの追加

```bash
# src/components/ ディレクトリに作成
mkdir -p src/components
```

### TailwindCSSカスタマイズ

`tailwind.config.js` を編集してカスタムテーマを設定できます。

## ビルドとデプロイ

### プロダクションビルド

```bash
npm run build
```

ビルドされたファイルは `dist/` ディレクトリに生成されます。

### Dockerビルド

```bash
docker build -t shift-frontend .
```

### デプロイ

**Vercel**
```bash
npm install -g vercel
vercel
```

**Netlify**
```bash
npm install -g netlify-cli
netlify deploy
```

## トラブルシューティング

### ポート競合

別のアプリケーションがポート5173を使用している場合:

```bash
# vite.config.js に追加
export default defineConfig({
  server: {
    port: 3000
  }
})
```

### API接続エラー

1. バックエンドが起動しているか確認
2. `.env` ファイルでAPI URLが正しいか確認
3. CORSエラーの場合はバックエンドのCORS設定を確認

## ライセンス

MIT
