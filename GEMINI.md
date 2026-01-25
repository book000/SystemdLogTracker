# Gemini CLI 用コンテキストと方針

## 目的
Gemini CLI 向けのコンテキストと作業方針を定義する。

## 出力スタイル
- 言語: 日本語
- トーン: プロフェッショナルかつ簡潔
- 形式: GitHub Flavored Markdown

## 共通ルール
- 会話言語: 日本語
- コミット規約: [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/)（`<description>` は日本語）
- 日本語と英数字の間: 半角スペースを挿入

## プロジェクト概要
- 目的: systemd ログを監視し、Discord や Slack に転送する。
- 主な機能: `journalctl -f` を使用したログ追跡、OkHttp を使用した Webhook 送信、設定ファイルによる柔軟なフィルタリング。

## コーディング規約
- フォーマット: 既存の Java コードのインデント（4スペース）とスタイルに従う。
- 命名規則: camelCase (variables/methods), PascalCase (classes).
- コメント言語: 日本語
- エラーメッセージ言語: 英語

## 開発コマンド
```bash
# コンパイル
mvn compile

# ビルド（依存関係を含む JAR を作成）
mvn package

# クリーン
mvn clean
```

## 注意事項
- 認証情報（Token, URL）を絶対にコミットしない。
- ログに機密情報を出力しない。
- 既存の JavaDoc やコメントを尊重し、必要に応じて日本語で追加・更新する。

## リポジトリ固有
- Java 16 以降が必要。
- `journalctl` コマンドが実行できる環境が必要。
