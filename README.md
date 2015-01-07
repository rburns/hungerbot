##Hungerbot

Hungerbot is a bot for Slack built with the intention of handling github activity feeds in a more elegant fashion than the standard Slackbot feed reader.                                                                          

```
NOTE: This is yet to be in good working order. Under development.
```

### Install

```bash
git clone https://github.com/rburns/hungerbot.git
cd hungerbot
npm install
lein deps
```

### Compile

```bash
lein cljsbuild once hungerbot
```

### Configure

```bash
cp config.example.js config.js
```

Populate config.js with details garnered from the slack bot integration page https://your-slack.slack.com/services/new/bot. 

### Run

```
node ./run.js
```

### Test

```
lein cljsbuild once hungerbot-test
```

### Develop

```
lein cljsbuild auto
```
