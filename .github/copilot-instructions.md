# GitHub Copilot コードレビュー向け指示

このファイルは GitHub Copilot のコードレビュー機能が参照する。レビュー時に重点確認すべき観点を示す。

## プロジェクトの前提

- systemd のログ (journal) を追跡し、Discord (Bot/Webhook) または Slack (Incoming Webhook) に送信する Java 16+ / Maven プロジェクト。
- 主要ライブラリは OkHttp 4.12.0 と org.json。`journalctl` が利用可能な Linux 環境を前提とする。

## レビュー時の重点確認

- **機密情報の混入**: Discord Token、Webhook URL、Slack Webhook URL などの認証情報がコード・設定サンプル・テストにハードコードされていないか。ログ出力に機密情報が含まれていないか。
- **外部プロセス実行**: `journalctl` を起動する箇所 (`Tracker.java`) で、外部入力や設定値をコマンドへ渡す際の扱いが安全か。プロセスの標準出力・エラーの読み取りとリソース解放が適切か。
- **HTTP 送信**: OkHttp の `Response` / `ResponseBody` が確実に close されているか。送信失敗時のエラーハンドリングが握りつぶしになっていないか。
- **設定パース**: `Config.java` で必須項目の欠落や不正値に対する検証があるか (Discord Bot・Discord Webhook・Slack Webhook のいずれか一つが設定されている前提)。
- **エラーメッセージ**: 英語で記述されているか。

## コーディング規約 (レビューで指摘してよい点)

- 日本語と英数字の間に半角スペースがあるか。
- インデントは 4 スペース。変数・メソッドは camelCase、クラスは PascalCase。
- JavaDoc・コメントは日本語。

## 誤検知させない (フラグ不要)

- コメント・JavaDoc が日本語であること自体は問題ではない。
- 自動テストが存在しないこと自体は現状の方針であり、PR ごとにテスト追加を強制しない (新規ロジック追加時は JUnit を提案する程度でよい)。
