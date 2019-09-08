# SystemdLogTracker

[日本語のREADMEはこちらから](https://github.com/book000/SystemdLogTracker/blob/master/README-ja.md)

Tracking systemd log (journal) and send them to discord.

## Feature

- Choose sending by Discord Bot OR sending by webhook in the config file.
- All journalctl command arguments can be set.
- When sending via webhook, the sender name can be changed in the config file.
- When sending via webhook, the avatar url you can be changed in the config file.

## Requirements

- Java 1.8+
- Valid Discord Bot or Webhook URL
- Eclipse (When installing from source)
- Maven (When installing from source)

## Install

### from release

1. Go to [Release](https://github.com/book000/SystemdLogTracker/releases) page.
2. Download latest version or any older version
3. First start it using `java -jar SystemdLogTracker.jar` etc.
4. A configuration file is generated. Check and edit the `Configuration` below.
5. Please start again after editing.
6. (if necessary) Let's register with Systemd etc. Check `Register with Systemd` below.

### from source with Eclipse

1. `File` -> `Import` -> `Git` -> `Projects from Git` -> `Clone URI`
2. Enter the `https://github.com/book000/SystemdLogTracker.git` in the URI field. -> `Next`
3. Select `master` branch. -> `Next`
4. (if necessary) Set `Destination directory`. -> `Next`
5. Select `Import existing Eclipse projects`. -> `Next`
6. Select `SystemdLogTracker` project. -> `Finish`
7. Right click on the project name. -> `Run as` -> `Maven install`
8. `SystemdLogTracker-jar-with-dependencies.jar` is created in the `target` directory.

### Run

Run the following command:

```shell
java -jar SystemdLogTracker.jar [Config file path]
```

`[Config file path]` can specify the path of the config file. For example: `java -jar SystemdLogTracker.jar Tester.properties`  
If not specified, `conf.properties` is used.

## Configuration

The default config file is `conf.properties`. But, the config file path can be changed by the argument when executing jar.

### discordToken

Required when using **Discord Bot**.

Please specify a token for Discord Bot.  
For example: `discordToken=***********************************************************`

### channelId

Required when using **Discord Bot**.

Please specify the destination channel ID.  
For example: `channelId=620226160168796210`

### webhookUrl

Required when using **Webhook**.

Please specify the URL of the Webhook.  
For example: `webhookUrl=https://discordapp.com/api/webhooks/**********`

### webhookBotName

Required when using **Webhook**.

Please specify your webhook username.  
For example: `webhookBotName=Systemd`  
Default value: `SystemdLogTracker`

### webhookAvatarUrl

**Not** required.

Please specify your webhook avatar url.  
For example: `webhookAvatarUrl=https://i.imgur.com/MDJlL8Q.jpg`

### arguments

**Required**.

Please specify argument of journalctl.  
For example: `arguments=-a -o cat -f -n 0`  
Default value: `-a -o cat -f -n 0`

Please be sure to specified the `-f` or `--follow` argument. If not specified, monitoring will fail.  
It is recommended to specify `-a` or `--all`. This shows everything even when output is very long.  
The argument `-u` or `--unit=UNIT|PATTERN` is very useful. This can specify a systemd service (unit).

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
   WantedBy = multi-user.target
   ```

   (For `<Command>` specify `/usr/bin/java -jar /var/SystemdLogTracker/SystemdLogTracker.jar /var/SystemdLogTracker/Tester.properties` etc.)
3. After executing `systemctl daemon-reload`, start the service with `systemctl start <ServiceName>`. `<ServiceName>` is `SystemdLogTracker` if service file is `SystemdLogTracker.service`.

## License

The license for this project is MIT License.  
[LICENSE](https://github.com/book000/SystemdLogTracker/blob/master/LICENSE)
