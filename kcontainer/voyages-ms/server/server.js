// Uncomment following to enable zipkin tracing, tailor to fit your network configuration:
// var appzip = require('appmetrics-zipkin')({
//     host: 'localhost',
//     port: 9411,
//     serviceName:'frontend'
// });

//require('appmetrics-dash').attach();
//require('appmetrics-prometheus').attach();
const appName = require('./../package').name;
const http = require('http');
const express = require('express');
const log4js = require('log4js');
const localConfig = require('./utils/config.js');
const path = require('path');

const logger = log4js.getLogger(appName);
logger.level = process.env.LOG_LEVEL || 'info'
const app = express();
const server = http.createServer(app);

app.use(express.json());
app.use(log4js.connectLogger(logger, { level: logger.level }));


require('./services/index')(app);
require('./routers/index')(app, server);

const port = localConfig.getPort();
const kbroker = null;
if(process.env.KAFKA_BROKERS) {
  const kbroker = localConfig.getKafkaBrokers();
}
server.listen(port, function(){
  logger.info(`voyagesms listening on http://localhost:${port}/appmetrics-dash`);
  logger.info(`voyagesms listening on http://localhost:${port}`);
  if(process.env.KAFKA_BROKERS) {
    logger.info(`voyagesms should be connected to kafka at: ${kbroker}`  );
  }  
});

app.use(function (req, res, next) {
  res.sendFile(path.join(__dirname, '../public', '404.html'));
});

app.use(function (err, req, res, next) {
	res.sendFile(path.join(__dirname, '../public', '500.html'));
});

module.exports = server;
