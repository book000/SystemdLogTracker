# SystemdLogTracker

[Click here for English README](https://github.com/book000/SystemdLogTracker/blob/master/README.md)

systemd ログ(journal)をトラッキングし、Discord に送信します。

## 特徴

- コンフィグファイルによって、Discord Bot によるチャンネルへの送信と Discord Webhook による送信、Slack Webhook による送信を選ぶことができます。
- journalctl コマンドの引数を全て設定できます。

## 前提条件

- Java 16+
- 以下のいずれか
  - 有効な Discord Bot Token と Discord Channel ID
  - 有効な Discord Webhook URL
  - 有効な Slack Incoming Message Webhook URL

## インストール

### Release から

1. [Release](https://github.com/book000/SystemdLogTracker/releases)ページを開いてください。
2. 最新のバージョンかそれより古いバージョンのいずれかをダウンロードしてください。
3. まずはじめに、`java -jar SystemdLogTracker.jar` などを使用して起動します。
4. コンフィグファイルが生成されます。下部の `設定` を確認して編集してください。
5. 編集したら、再度起動してください。
6. (必要に応じて) Systemd などに登録しましょう。下部の `Systemdに登録する` を確認してください。

### 実行

実行するには次のコマンドを実行してください。

```shell
java -jar SystemdLogTracker.jar [Config file path]
```

`[Config file path]` には、コンフィグファイルのパスを指定できます。例: `java -jar SystemdLogTracker.jar Tester.json`  
指定しない場合、カレントディレクトリの `config.json` が使用されます。

## 設定

デフォルトのコンフィグファイルは `config.json` です。ただし、コンフィグファイルのパスは jar の実行時に引数によって変更できます。

```json
{
  "discordWebhookUrl": "https://discord.com/api/webhooks/00000000000000/xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx",
  "arguments": "-a -o cat -f -n 0 -u sshd"
}
```

`discordToken` と `discordChannelId`、`discordWebhookUrl`、`slackWebhookUrl` のいずれかは必ず設定してください。

### discordToken

**Discord Bot** を使用する場合に必要です。

Discord Bot のトークンを指定してください。

```json
{
  "discordToken": "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
}
```

### discordChannelId

**Discord Bot** を使用する場合に必要です。

送信先チャンネルの ID を指定してください。

```json
{
  "discordChannelId": "00000000000000"
}
```

### discordWebhookUrl

**Discord Webhook** を使用する場合に必要です。

Discord Webhook の URL を指定してください。

```json
{
  "discordWebhookUrl": "https://discord.com/api/webhooks/00000000000000/xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
}
```

### slackWebhookUrl

**Slack Incoming Webhook** を使用する場合に必要です。

Slack Incoming Webhook の URL を指定してください。

```json
{
  "slackWebhookUrl": "https://hooks.slack.com/services/xxxxxxxxxx/xxxxxxxxxx/xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
}
```

### arguments

オプションですが、設定することをお勧めします。

journalctl の引数を指定してください。  
デフォルト値は `-a -o cat -f -n 0` です。

```json
{
  "arguments": "-a -o cat -f -n 0 -u sshd"
}
```

- 必ず `-f` または `--follow` 引数を指定してください。指定しない場合、監視は失敗します。
- `-a` または `--all` を指定することをお勧めします。これは出力が非常に長い場合でもすべてを表示します。
- 引数 `-u` または `--unit=UNIT|PATTERN` はとても有用です。これにより、systemd サービス(Unit)を指定できます。

### sendInterval

必須では**ありません**。

出力を処理する間隔をミリ秒で指定してください。  
デフォルト値は `5000` です。

```json
{
  "sendInterval": 5000
}
```

## Systemd に登録する

1. `/etc/systemd/system/` の下にサービスファイルを作成します。例: `/etc/systemd/system/SystemdLogTracker.service`
2. お好きなエディターでファイルを開き、次のように記述してください。

   ```ini
   [Unit]
   Description=Systemd Log Tracker

   [Service]
   User=root
   Group=root
   ExecStart=<Command>
   Restart=always

   [Install]
   WantedBy=multi-user.target
   ```

   (`<Command>`には`/usr/bin/java -jar /var/SystemdLogTracker/SystemdLogTracker.jar /var/SystemdLogTracker/Tester.config`などを指定してください。)

3. `systemctl daemon-reload` を実行した後、 `systemctl start <ServiceName>` でサービスを開始します。サービスファイルが `SystemdLogTracker` の場合、`<ServiceName>` は `SystemdLogTracker.service` です。

## ライセンス

このプロジェクトのライセンスは MIT ライセンスです。
[LICENSE](https://github.com/book000/SystemdLogTracker/blob/master/LICENSE)
