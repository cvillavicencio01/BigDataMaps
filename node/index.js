const path = require('path');
const express = require('express');
const bodyParser = require('body-parser');
const hbaseDB = require('hbase-rpc-client');
const app = express();

const http = require('http');
const DBHOST = process.env.DBHOST || 'localhost:2181';
const DBROOT = process.env.DBROOT || '/hbase';
const DBNAME = 'HBASENAME';

/*const client = hbaseDB({
  zookeeperHosts: [ DBHOST ],
  zookeeperRoot: DBROOT,
  zookeeperReconnectTimeout: 20000,
  rpcTimeout: 30000,
});*/


http.createServer(function(req, res) {

    res.writeHead(200);
  
    res.end('Salut tout le monde !');
  
  });
app.use(bodyParser.json());

app.use('/img/:x/:y/:z', (req, res, next) => {
  // Afficher une image se trouvant dans HBase
});

app.get('/', function (req, res) {

    res.send('BigData')
})

app.listen(8000, function () {
    console.log('Example app listening on port 8000!')
  })
app.use(express.static(path.join(__dirname, 'public')));

module.exports = app;