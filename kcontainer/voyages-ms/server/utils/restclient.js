var request = require('request');

const endpoint = process.env.ORDER_ENDPOINT || 'http://orders-kn-channel.kcontainer.svc.cluster.local'

const emit = (event) => {
    request.post(endpoint, {
        json: event
    }, (error, res, body) => {
        if (error) {
            console.error(error)
            return
        }
    console.log(`emit() response statusCode: ${res.statusCode}`)
    //console.log(body)
    })
}

// export this
module.exports = {
    emit
};