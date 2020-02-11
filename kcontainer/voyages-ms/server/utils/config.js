const config = require('../config/local.json');

module.exports = {

    getKafkaBrokers: function() {
        return process.env.KAFKA_BROKERS || config.kafkaBrokers;
    },

    getOrderTopicName: function() {
        return process.env.ORDER_TOPIC || config.orderTopicName;
    },

    getKafkaApiKey: function() {
        return process.env.KAFKA_APIKEY || config.kafkaApiKey;
    },

    getCertsPath: function() {
        return process.env.CERTS_PATH || config.certsPath;
    },

    getPort: function() {
        return process.env.PORT || config.port;
    },

    isEventStreams: function() {
       return ('KAFKA_APIKEY' in process.env && process.env.KAFKA_APIKEY.trim());
    },

    areEventStreamsCertificatesRequired: function() {
        return ('CERTS_ENABLED' in process.env);
    }
}
