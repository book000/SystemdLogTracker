# SystemdLogTracker

[Click here for English README](https://github.com/book000/SystemdLogTracker/blob/master/README.md)

systemdログ(journal)をトラッキングし、Discordに送信します。

## 特徴

- コンフィグファイルによって、Discord Botによるチャンネルへの送信とWebhookによる送信を選ぶことができます。
- journalctlコマンドの引数を全て設定できます。
- webhookによる送信の場合、送信者名を設定で変更できます。
- webhookによる送信の場合、アバターURLを設定で変更できます。

## 前提条件

- Java 1.8+
- 有効なDiscord BotまたはWebhook URL
- Eclipse (ソースからインストールする際のみ)
- Maven (ソースからインストールする際のみ)

## インストール

### Releaseから

1. [Release](https://github.com/book000/SystemdLogTracker/releases)ページを開いてください。
2. 最新のバージョンかそれより古いバージョンのいずれかをダウンロードしてください。
3. まずはじめに、`java -jar SystemdLogTracker.jar`などを使用して起動します。
4. コンフィグファイルが生成されます。下部の`設定`を確認して編集してください。
5. 編集したら、再度起動してください。
6. (必要に応じて) Systemdなどに登録しましょう。下部の`Systemdに登録する`を確認してください。

### Eclipseを使用してソースから

1. `ファイル` -> `インポート` -> `Git` -> `Git からプロジェクト` -> `クローン URI`
2. URI欄に`https://github.com/book000/SystemdLogTracker.git`を入力し、`次へ`をクリックしてください。
3. `master`ブランチを選択し、`次へ`をクリックしてください。
4. `宛先`の`ディレクトリー`を必要に応じて編集し、`次へ`をクリックしてください。
5. `既存プロジェクトのインポート`を選択し、`次へ`をクリックしてください。
6. `SystemdLogTracker`プロジェクトを選択し、`完了`をクリックしてください。
7. プロジェクト名を右クリックし、`実行`から`Maven install`をクリックしてください。
8. `target`ディレクトリに`SystemdLogTracker-jar-with-dependencies.jar`が作成されます。

### 実行

実行するには次のコマンドを実行してください。

```shell
java -jar SystemdLogTracker.jar [Config file path]
```

`[Config file path]`には、コンフィグファイルのパスを指定できます。例: `java -jar SystemdLogTracker.jar Tester.properties`  
指定しない場合、`conf.properties`が使用されます。

## 設定

デフォルトのコンフィグファイルは`conf.properties`です。ただし、コンフィグファイルのパスはjarの実行時に引数によって変更できます。

### discordToken

**Discord Bot**を使用する場合に必要です。

Discord Botのトークンを指定してください。  
例: `discordToken=***********************************************************`

### channelId

**Discord Bot**を使用する場合に必要です。

送信先のChannelIDを指定してください。  
例: `channelId=620226160168796210`

### webhookUrl

**Webhook**を使用する場合に必要です。

WebhookのURLを指定してください。  
例: `webhookUrl=https://discordapp.com/api/webhooks/**********`

### webhookBotName

**Webhook**を使用する場合に必要です。

Webhookのユーザーネームを指定してください。  
例: `webhookBotName=Systemd`  
デフォルト値: `SystemdLogTracker`

### webhookAvatarUrl

必須では**ありません**。

WebhookのアバターURLを指定してください。  
例: `webhookAvatarUrl=https://i.imgur.com/MDJlL8Q.jpg`

### arguments

**必須です**。

journalctlの引数を指定してください。  
例: `arguments=-a -o cat -f -n 0`  
デフォルト値: `-a -o cat -f -n 0`

必ず`-f`または`--follow`引数を指定してください。指定しない場合、監視は失敗します。  
`-a`または`--all`を指定することをお勧めします。これは出力が非常に長い場合でもすべてを表示します。  
引数`-u`または`--unit=UNIT|PATTERN`はとても有用です。これにより、systemdサービス(Unit)を指定できます。

### sendInterval

必須では**ありません**。

出力を処理するインターバルミリ秒を指定してください。  
例: `1000`  
デフォルト値: `5000`

`long`値として解析できない場合は、デフォルト値が使用されます。

## Systemdに登録する

1. `/etc/systemd/system/`の下にサービスファイルを作成します。例: `/etc/systemd/system/SystemdLogTracker.service`
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
   WantedBy = multi-user.target
   ```

   (`<Command>`には`/usr/bin/java -jar /var/SystemdLogTracker/SystemdLogTracker.jar /var/SystemdLogTracker/Tester.properties`などを指定してください。)
3. `systemctl daemon-reload`を実行した後、`systemctl start <ServiceName>`でサービスを開始します。サービスファイルが`SystemdLogTracker`の場合、`<ServiceName>`は`SystemdLogTracker.service`です。

## ライセンス

このプロジェクトのライセンスはMITライセンスです。
[LICENSE](https://github.com/book000/SystemdLogTracker/blob/master/LICENSE)
