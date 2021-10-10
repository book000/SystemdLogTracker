# SystemdLogTracker

[日本語の README はこちらから](https://github.com/book000/SystemdLogTracker/blob/master/README-ja.md)

Tracking systemd log (journal) and send them to discord or slack.

## Feature

- Choose sending by Discord Bot, sending by discord webhook or slack incoming webhook in the config file.
- All journalctl command arguments can be set.

## Requirements

- Java 16+
- One of the following
  - Valid Discord Bot Token and Discord Channel ID
  - Valid Discord Webhook URL
  - Valid Slack Incoming Message Webhook URL

## Install

### from release

1. Go to [Release](https://github.com/book000/SystemdLogTracker/releases) page.
2. Download latest version or any older version
3. First start it using `java -jar SystemdLogTracker.jar` etc.
4. A configuration file is generated. Check and edit the `Configuration` below.
5. Please start again after editing.
6. (if necessary) Let's register with Systemd etc. Check `Register with Systemd` below.

### Run

Run the following command:

```shell
java -jar SystemdLogTracker.jar [Config file path]
```

`[Config file path]` can specify the path of the config file. For example: `java -jar SystemdLogTracker.jar Tester.json`  
If not specified, `config.json` is used.

## Configuration

The default config file is `config.json`. But, the config file path can be changed by the argument when executing jar.

```json
{
  "discordWebhookUrl": "https://discord.com/api/webhooks/00000000000000/xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx",
  "arguments": "-a -o cat -f -n 0 -u sshd"
}
```

Be sure to set `discordToken` and `discordChannelId`, `discordWebhookUrl`, or `slackWebhookUrl`.

### discordToken

Required when using **Discord Bot**.

Please specify a token for Discord Bot.

```json
{
  "discordToken": "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
}
```

### discordChannelId

Required when using **Discord Bot**.

Please specify the destination channel ID.

```json
{
  "discordChannelId": "00000000000000"
}
```

### discordWebhookUrl

Required when using **Discord Webhook**.

Please specify the URL of the Discord Webhook.

```json
{
  "discordWebhookUrl": "https://discord.com/api/webhooks/00000000000000/xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
}
```

### slackWebhookUrl

Required when using **Slack Incoming Webhook**.

Please specify the URL of the Slack Webhook.

```json
{
  "slackWebhookUrl": "https://hooks.slack.com/services/xxxxxxxxxx/xxxxxxxxxx/xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
}
```

### arguments

It is an option, but it is recommended to set it.

Please specify argument of journalctl.  
Default value: `arguments=-a -o cat -f -n 0`

```json
{
  "arguments": "-a -o cat -f -n 0 -u sshd"
}
```

- Please be sure to specified the `-f` or `--follow` argument. If not specified, monitoring will fail.  
- It is recommended to specify `-a` or `--all`. This shows everything even when output is very long.  
- The argument `-u` or `--unit=UNIT|PATTERN` is very useful. This can specify a systemd service (unit).

### sendInterval

**Not** required.

Please specify the interval milliseconds for processing the output.  
Default value: `5000`

```json
{
  "sendInterval": 5000
}
```

## Register with Systemd

1. Create a service file under `/etc/systemd/system/`. For example: `/etc/systemd/system/SystemdLogTracker.service`
2. Open the file with your favorite editor and write the following:

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

   (For `<Command>` specify `/usr/bin/java -jar /var/SystemdLogTracker/SystemdLogTracker.jar /var/SystemdLogTracker/Tester.json` etc.)

3. After executing `systemctl daemon-reload`, start the service with `systemctl start <ServiceName>`. `<ServiceName>` is `SystemdLogTracker` if service file is `SystemdLogTracker.service`.

## License

The license for this project is MIT License.  
[LICENSE](https://github.com/book000/SystemdLogTracker/blob/master/LICENSE)
