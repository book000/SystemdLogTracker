# GitHub Copilot Instructions

## プロジェクト概要
Monitors systemd journal logs and sends them to Discord (bot/webhook) or Slack for real-time notifications of system events.

## 共通ルール
- 会話は日本語で行う。
- PR とコミットは Conventional Commits に従う。
- PR タイトルとコミット本文の言語: PR タイトルは Conventional Commits 形式（英語推奨）。PR 本文は日本語。コミットは Conventional Commits 形式（description は日本語）。
- 日本語と英数字の間には半角スペースを入れる。
- 既存のプロジェクトルールがある場合はそれを優先する。

## 技術スタック
- 言語: Java
- パッケージマネージャー: Maven

## コーディング規約
- フォーマット: 既存設定（ESLint / Prettier / formatter）に従う。
- 命名規則: 既存のコード規約に従う。
- Lint / Format: 既存の Lint / Format 設定に従う。
- コメント言語: 日本語
- エラーメッセージ: 英語
- TypeScript 使用時は strict 前提とし、`skipLibCheck` で回避しない。
- 関数やインターフェースには docstring（JSDoc など）を記載する。

### 開発コマンド
```bash
# install
Download JAR from Releases page

# dev
mvn clean package

# build
mvn clean assembly:single

# run
java -jar SystemdLogTracker.jar [config-file.json]

```

## テスト方針
- 新機能や修正には適切なテストを追加する。

## セキュリティ / 機密情報
- 認証情報やトークンはコミットしない。
- ログに機密情報を出力しない。

## ドキュメント更新
- 実装確定後、同一コミットまたは追加コミットで更新する。
- README、API ドキュメント、コメント等は常に最新状態を保つ。

## リポジトリ固有
- **type**: Java CLI Utility / System Monitor
**platforms:**
  - Linux
  - macOS
  - Windows
- **main_class**: com.tomacheese.systemdlogtracker.Main
- **config_format**: JSON
- **default_config_file**: config.json
**output_targets:**
  - Discord Bot
  - Discord Webhook
  - Slack Incoming Webhook
- **commands_supported**: All journalctl arguments (recommended: -a -o cat -f -n 0)
**features:**
  - Real-time systemd journal tracking
  - Configurable filtering (filteredWords)
  - Customizable send intervals
  - Multiple output targets
- **registration**: Supports systemd service registration for auto-start
**json_config_keys:**
  - discordToken
  - discordChannelId
  - discordWebhookUrl
  - slackWebhookUrl
  - arguments
  - sendInterval
  - filteredWords