const WebSocket = require('ws')
const { inspect } = require('util')
const path = require('path')
const chokidar = require('chokidar')
const fs = require('fs')

// Initialize the server.
const wss = new WebSocket.Server({ port: 4269 })

// Broadcast function to all.
wss.broadcast = (data) => {
  wss.clients.forEach((client) => {
    if (client.readyState === WebSocket.OPEN) client.send(data)
  })
}

// Check if we are in plugins/ReConsole/node-ws-console
if (!process.env.RECONSOLE_LOGS && !__dirname.endsWith('plugins/ReConsole/node-ws-console')) {
  throw new Error(`It doesn't look like you're in a server directory (and RECONSOLE_LOGS env is unset as well!)
Please use the ReConsole plugin directly and let it handle startup of the server unless you know what you're doing.`)
}
// Check if logs/latest.log exists, if not, create it.
const pathToLogs = process.env.RECONSOLE_LOGS || path.join(__dirname, '..', '..', '..', 'logs', 'latest.log')
const reconsoleExists = fs.existsSync(pathToLogs)
if (!reconsoleExists) fs.mkdirSync(pathToLogs)

// The file.
let file = fs.readFileSync(pathToLogs, { encoding: 'utf8' }).split('\n')

// Watch logs/latest.log
const watcher = chokidar.watch(pathToLogs)
watcher.on('ready', () => console.log(`[ReConsole WS Gateway] Watching ${pathToLogs} for changes.`))

// When the file changes, we broadcast the change.
watcher.on('change', async () => {
  // We read the file by line.
  const prev = file.length
  file = fs.readFileSync(pathToLogs, { encoding: 'utf8' }).split('\n')
  // Calculate the new lines.
  const diff = []
  for (let a = 1; a <= (file.length - prev); a++) {
    diff.unshift(file[file.length - a])
  }
  wss.broadcast(diff.join('\n'))
})

// Register events on the WebSocket server.
wss.on('listening', () => console.log('[ReConsole WS Gateway] Listening on port 4269.'))
wss.on('connection', (ws) => {
  // We send the entire file.
  ws.send(file.join('\n'))
  // We probably shouldn't be getting messages. This is to be handled by the plugin.
  ws.on('message', (message) => console.log(`[ReConsole WS Gateway] Received: ${inspect(message)}`))
})

// When Node.js is about to shut down.
const exitHandler = () => {
  watcher.close()
  wss.close()
  console.log('\n[ReConsole WS Gateway] Shutting down file listener.')
  process.exit()
}
process.on('SIGINT', exitHandler)
