# CLAUDE.md

## プロジェクト概要

- 目的: systemd のログ (journal) を追跡し、Discord (Bot または Webhook) もしくは Slack (Incoming Webhook) に送信する。
- 主な機能: `journalctl` によるログ監視、複数プラットフォームへの通知送信、`filteredWords` によるフィルタリング。
- 対象環境: `journalctl` が利用可能な Linux 環境。

## 技術スタック

- 言語: Java 16+ (pom.xml の `java.version` は 16)
- ビルドツール: Maven (`maven-assembly-plugin` で依存を含む実行可能 JAR を生成)
- 主要ライブラリ: OkHttp 4.12.0 (`com.squareup.okhttp3`)、org.json (`org.json:json`)

## 開発コマンド

```bash
mvn compile   # 依存関係の解決・コンパイル
mvn package   # 依存を含む実行可能 JAR を target/ に生成 (SystemdLogTracker-jar-with-dependencies.jar)
mvn clean     # ビルド成果物のクリーン
```

実行: `java -jar <JAR> [設定ファイルパス]` (設定ファイル省略時は `config.json` を使用)。リリース配布物は `SystemdLogTracker.jar` にリネームされている (README 参照)。

## アーキテクチャと主要ファイル

`src/main/java/com/tomacheese/systemdlogtracker/`

- `Main.java`: エントリーポイント。設定の読み込みと各コンポーネントの起動。
- `Config.java`: 設定ファイル (JSON) のパースと保持。
- `Tracker.java`: `journalctl` コマンドの実行と出力監視。
- `MessageOrganizer.java`: メッセージのフォーマット。
- `Sender.java`: 各プラットフォーム (Discord Bot/Webhook、Slack Webhook) への送信処理。

## コーディング規約

- 日本語と英数字の間には半角スペースを挿入する。
- インデントは 4 スペース。既存の Java コードのスタイルに従う。
- 命名: 変数・メソッドは camelCase、クラスは PascalCase。
- JavaDoc は日本語で記載する。
- コメント言語は日本語、エラーメッセージ言語は英語。
- 非推奨: 認証情報 (Token, Webhook URL 等) のハードコード。

## コミット規約

- [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/) に従う。`<description>` は日本語で記載する。

## テスト

- 現状は手動確認 (JAR を実行し journal 監視・通知送信を確認) がメイン。
- 自動テストを追加する場合は JUnit を検討する (`src/test/java/` が用意されている)。

## ドキュメント更新

- 機能追加や設定項目の変更時は `README.md` と `README-ja.md` の両方を更新する。

## セキュリティ / 機密情報

- 認証情報 (Discord Token、Webhook URL など) をコードや設定サンプルにコミットしない。
- ログに機密情報を出力しない。
