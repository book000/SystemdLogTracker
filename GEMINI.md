# GEMINI.md

## 目的
- Gemini CLI 向けのコンテキストと作業方針を定義する。

## 出力スタイル
- 言語: 日本語
- トーン: 簡潔で事実ベース
- 形式: Markdown

## 共通ルール
- 会話は日本語で行う。
- PR とコミットは Conventional Commits に従う。
- PR タイトルとコミット本文の言語: PR タイトルは Conventional Commits 形式（英語推奨）。PR 本文は日本語。コミットは Conventional Commits 形式（description は日本語）。
- 日本語と英数字の間には半角スペースを入れる。

## プロジェクト概要
Monitors systemd journal logs and sends them to Discord (bot/webhook) or Slack for real-time notifications of system events.

### 技術スタック
- **言語**: Java
- **フレームワーク**: N, /, A
- **パッケージマネージャー**: Maven
- **主要な依存関係**:
  - okhttp3:4.12.0
  - json:20250517

## コーディング規約
- フォーマット: 既存設定（ESLint / Prettier / formatter）に従う。
- 命名規則: 既存のコード規約に従う。
- コメント言語: 日本語
- エラーメッセージ: 英語

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

## 注意事項
- 認証情報やトークンはコミットしない。
- ログに機密情報を出力しない。
- 既存のプロジェクトルールがある場合はそれを優先する。

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