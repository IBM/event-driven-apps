#!/usr/bin/env node

const yargs = require("yargs");
const axios = require("axios");

const command = yargs.command('create <n> <url>', 'create n containers for each city', (yargv) => { 
    yargv.positional('n', {
      describe: 'number of containers for city to create',
      type: 'int'
    }).positional('url', {
      describe: 'URL to containers service',
      type: 'string'
    })
  })
  .command('delete-all <url>', 'delete all container', (yargv) => {
    yargv.positional('url', {
      describe: 'URL to containers service',
      type: 'string'
    })
   })  
   .command('list <url>', 'list all container', (yargv) => {
    yargv.positional('url', {
      describe: 'URL to containers service',
      type: 'string'
    })
   })  
  .demandCommand()
  .help()
  .argv

  switch (command._[0]) {
    case 'create':
        createContainers(command.n,command.url)
        break;
    case 'delete-all':
        console.log('deleting all container at '+command.url); 
        deleteAllContainers(command.url);
        break;
    case 'list':
        console.log('listing all container at '+command.url); 
        listAllContainers(command.url,0);
        break;    
  }       

 
  /**
   * Create n containers
   * @param {*} n 
   * @param {*} url 
   */
  function createContainers(n, url) {
    var cities = [];
    // Oakland
    cities.push({ lat: 37.8044, lon: -122.2712 });
    // Shanghai
    cities.push({ lat: 31.323246, lon: 121.562938 });
    // NYC
    cities.push({ lat: 40.625289, lon: -73.995350 });
    // NYC
    cities.push({ lat: 40.625289, lon: -73.995350 });
     // London
    cities.push({ lat: 51.507619, lon: -.142253 });
     // San Francisco
    cities.push({ lat: 37.6749, lon: 122.5194 });

    console.log('creating '+n+' containers for '+cities.length+ ' cities (total='+n*cities.length+' at '+url);

    // iterate on cities
    var k = 1;
    for (var i = 0; i < cities.length; i++) {
        // generate n containers for each city
        for (var j = 0; j < n; j++) {
            axios({
                method: 'post',
                url: url,
                data: {
                  containerID: 'c'+k,
                  latitude: cities[i].lat,
                  longitude: cities[i].lon,
                  type: `Reefer`,
                  brand: 'SeaBox',
                  capacity: 1000
                }
              });
            k++;  
        } 
    } 
  }

  /**
   * Delete all containers
   * @param {*} url 
   */
  function deleteAllContainers(url){
    axios({
        method: 'delete',
        url: url
    });    
  }

  /**
   * List all containers
   * @param {*} url 
   */
  function listAllContainers(url,page) {
    // to page: ?page=2&limit=20
    console.log(url+'?page='+page+'&limit=20');
    axios.get(url+'?page='+page+'&limit=20')
        .then(function (response) {
        // handle success
        console.log(response.data.content);
        if (response.data.last) {
            return;
        } else {
            page += 1;
            listAllContainers(url, page);
        }
        })
    .catch(function (error) {
    // handle error
    console.log(error);
    })
    .then(function () {
    // always executed
    });

  }

  