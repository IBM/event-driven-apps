var kafka = require('node-rdkafka');
var config = require('../utils/config.js')

const connectTimeoutMs = 10000;
const committedTimeoutMs = 10000;

const getCloudConfig = () => {
    var _config = {
        'security.protocol': 'sasl_ssl',
        'sasl.mechanisms': 'PLAIN',
        'sasl.username': 'token',
        'sasl.password': config.getKafkaApiKey()
    };

    if(config.areEventStreamsCertificatesRequired()){
      _config['ssl.ca.location'] = config.getCertsPath();
    }

    return _config;
}

const getProducerConfig = () => {
    var producerConfig = {
        //'debug': 'all',
        'metadata.broker.list': config.getKafkaBrokers(),
        'broker.version.fallback': '0.10.2.1',
        'log.connection.close' : false,
        'client.id': 'voyage-producer',
        'dr_msg_cb': true, // Enable delivery reports with message payload,
        'socket.keepalive.enable': true
    };
    if (config.isEventStreams()) {
        eventStreamsConfig = getCloudConfig();
        for (var key in eventStreamsConfig) {
            producerConfig[key] = eventStreamsConfig[key];
        }
    }
    console.log('producer configs' + JSON.stringify(producerConfig));
    return producerConfig;
}

const getConsumerConfig = (gid) => {
    var consumerConfig = {
        //'debug': 'all',
        'metadata.broker.list': config.getKafkaBrokers(),
        'broker.version.fallback': '0.10.2.1',
        'log.connection.close' : false,
        'client.id': 'voyage-consumer',
        'group.id': gid,
        'enable.auto.commit' : false,
        'socket.keepalive.enable': true
    };
    if (config.isEventStreams()) {
        eventStreamsConfig = getCloudConfig();
        for (var key in eventStreamsConfig) {
            consumerConfig[key] = eventStreamsConfig[key];
        }
    }
    console.log('consumer configs' + JSON.stringify(consumerConfig));
    return consumerConfig;
}

const getConsumerTopicConfig = () => {
    return {'auto.offset.reset':'earliest'};
}

var producer = new kafka.Producer(getProducerConfig(), {
    'request.required.acks': -1,
    'produce.offset.report': true,
    'message.timeout.ms' : 10000  //speeds up a producer error response
});
producer.on('event.log', function(m){
    console.log('P', m);
})
producer.on('event.error', function(m){
    console.error('PE', m);
})

var consumer = new kafka.KafkaConsumer(getConsumerConfig('voyage-consumer-group'), getConsumerTopicConfig());
var reloadConsumer = new kafka.KafkaConsumer(getConsumerConfig('voyage-consumer-group-reload'), getConsumerTopicConfig());

consumer.on('event.log', function(m){
     console.log('C', m);
})
reloadConsumer.on('event.log', function(m){
    console.log('RC', m);
})
consumer.on('event.error', function(m){
    console.error('CE', m);
})
reloadConsumer.on('event.error', function(m){
   console.error('RCE', m);
})

producer.setPollInterval(1000);
var producerReady = false;
producer.connect({ timeout: connectTimeoutMs }, function(err, info) {
    if(err) {
        console.error('Error in producer connect cb', err);
        process.exit(-99); // microservice can't be available
    } else {
        console.log('Producer connected to Kafka');
        producerReady = true;
    }
})

producer.on('delivery-report', (err, report) => {
    if (typeof report.opaque === 'function') {
        report.opaque.call(null, err, report);
    } else {
        console.error('Assertion failed: opaque not a function!' + err);
    }
});

const emit = (key, event) => {
    if (!producerReady) {
        // kafka will handle reconnections but the produce method should never
        // be called if the client was never 'ready'
        console.log('Producer never connected to Kafka yet');
        return Promise.reject(new Error('Producer never connected to Kafka yet'));
    }

    return new Promise((resolve, reject) => {
        try {
            producer.produce(config.getOrderTopicName(),
                null, /* partition */
                new Buffer(JSON.stringify(event)),
                key,
                Date.now(),
                (err, report) => {
                    if (err) return reject(err);
                    return resolve(report);
                }
            );
        } catch (e) {
            console.error('Failed sending event ' + JSON.stringify(event) + " error:" + e);
            return reject(e);
        }
    });
}

const reload = (subscription) => {
    consumer.connect({ timeout: connectTimeoutMs }, function(err, info) {
        if(err) {
            console.error('Error in consumer connect ', err);
            process.exit(-100); // If reload fails, microservice can't be available
        } else {
            console.log('Consumer connected to Kafka');
        }

        // TODO handle multiple partitions
        consumer.committed([{ topic: subscription.topic, partition: 0, offset: -1 }], committedTimeoutMs, function(err,tps) {
            if(err) {
                console.error('Error in committed cb', err);
                consumer.disconnect();
                process.exit(-200); // If reload fails, microservice can't be available
            }

            var reloadLimit = tps[0].offset-1;
            console.log(tps);
            console.log('ReloadLimit='+reloadLimit);

            doReload(reloadLimit, subscription)
            // .then(function() {
            //     listen(subscription)
            // })
            .catch(function(err) {
                consumer.disconnect();
                process.exit(-300); // If reload fails, microservice can't be available
            });

        });
    });
}

const doReload = (reloadLimit, subscription) => {
    return new Promise((resolve, reject) => {
        if (reloadLimit>=0) {
            reloadConsumer.connect({ timeout: 5000 }, function(err, info) {
                if (err) {
                    console.error('ReloadConsumer error in connected cb', err);
                    return reject(err);
                } else {
                    console.log('ReloadConsumer connected to Kafka');
                }

                reloadConsumer.subscribe([subscription.topic]); //will consume from earliest
                var finishedReloading = false;

                var consumeCb = function(err,messages) {
                    if (isIterable(messages)) {
                        for(var m of messages) {
                            //console.log("@@@@ processing m, m offset="+m.offset + ' reloadLimit()' + reloadLimit);
                            //if (m.offset > reloadLimit) {
                            console.log('@@@ New message received');
                            subscription.callback(m, true);
                            reloadConsumer.commitMessageSync(m);
                            //}

                            // if (m.offset === reloadLimit) {
                            //     finishedReloading = true;
                            //     break;
                            // }
                        };
                        reloadConsumer.consume(10, consumeCb);
                        // if (!finishedReloading) {
                        //     reloadConsumer.consume(10, consumeCb);
                        // } else {
                        //     console.log("Finished reloading");
                        //     reloadConsumer.disconnect();
                        //     return resolve();
                        // }
                    }
                }

                reloadConsumer.consume(10, consumeCb);
            });
        } else {
            console.log("No reloading needed");
            return resolve();
        }
    });
}


const listen = (subscription) => {
    console.log('Main Consumer in listen', subscription);
    consumer.on('data', function(message) {
        try {
            subscription.callback(message, false);
            console.log('Main Consumer committing message...');
            consumer.commitMessageSync(message);
        } catch(err) {
            // TODO send to error queue
            logger.error(err)
        }
    });
    consumer.subscribe([subscription.topic]); //will consume from committed
    consumer.consume();
    console.log('Main Consumer starting consume loop');
}

function isIterable(obj) {
    // checks for null and undefined
    if (obj == null) {
      return false;
    }
    return typeof obj[Symbol.iterator] === 'function';
  }

module.exports = {
    emit,
    reload
};
