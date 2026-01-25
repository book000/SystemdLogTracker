# GitHub Copilot Instructions

## プロジェクト概要
- 目的: systemd ログ（journal）を追跡し、Discord（Bot または Webhook）または Slack（Incoming Webhook）に送信する。
- 主な機能: journalctl コマンドによるログ監視、複数プラットフォームへの通知送信、フィルタリング機能。
- 対象ユーザー: サーバー管理者、開発者。

## 共通ルール
- 会話は日本語で行う。
- PR とコミットは Conventional Commits に従う。
- 日本語と英数字の間には半角スペースを入れる。

## 技術スタック
- 言語: Java 16+
- ビルドツール: Maven
- 主要ライブラリ: OkHttp 4.12.0, org.json

## 開発コマンド
```bash
# 依存関係の解決・コンパイル
mvn compile

# ビルド（実行可能 JAR の作成）
mvn package

# クリーンアップ
mvn clean
```

## コーディング規約
- 日本語と英数字の間には半角スペースを入れる。
- 関数やインターフェースには docstring（JavaDoc）を日本語で記載する。
- 変数名・メソッド名はキャメルケース（camelCase）を使用する。
- クラス名はパスカルケース（PascalCase）を使用する。

## テスト方針
- 現時点では自動テストは含まれていないが、新規追加する場合は JUnit を検討する。

## セキュリティ / 機密情報
- 認証情報（Discord Token, Webhook URL など）をコードに含めない。
- ログに機密情報を出力しない。

## ドキュメント更新
- `README.md`
- `README-ja.md`

## リポジトリ固有
- `journalctl` コマンドが利用可能な Linux 環境を前提としている。
