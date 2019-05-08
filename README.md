# ReConsole

A plugin which provides an API for ReConsole clients to control your server.

AKA it is the best Minecraft console in existence.

## Installation (it's not as easy as you think but it is easy)

We'll automate this soon enough.

1. Download the latest version of ReConsole from [here](https://github.com/retrixe/reconsole/releases). Also, download the node_modules.zip from there as well.
2. Put the ReConsole JAR in `plugins/` and create a folder `ReConsole` in `plugins/` there.
3. Extract node_modules.zip inside `plugins/ReConsole` such that `plugins/ReConsole/node_modules` contains a ton of folders. The directory structure is very important.
4. [Install Node.js 10+ from here, required for the console to work.](https://nodejs.com/en/download) [Click here if you can't install Node or are too lazy to.](https://github.com/retrixe/reconsole/blob/master/README.md#FAQ)
5. Startup the server. If you did everything right, ReConsole will start up perfectly.

## FAQ

### I can't install Node.js because my host doesn't let me to!

Coming soon, but just download a portable version of Node.js and put it in `plugins/ReConsole` and then set `use-fallback-node` to true.

### WebSocket server failed to listen on port 4169!

Make sure you did everything right.
