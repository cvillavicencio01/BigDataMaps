
const path = require('path');
const express = require('express');
const bodyParser = require('body-parser');
const hbaseDB = require('hbase-rpc-client');
var fs = require('fs');
const app = express();

const http = require('http');
const DBHOST = process.env.DBHOST || 'young:2181';
const DBROOT = process.env.DBROOT || '/hbase';

const client = hbaseDB({
  zookeeperHosts: [ DBHOST ],
  zookeeperRoot: DBROOT,
  zookeeperReconnectTimeout: 20000,
  rpcTimeout: 30000,
});

Number.prototype.pad = function(size) {
    var s = String(this);
    while (s.length < (size || 2)) {s = "0" + s;}
    return s;
}

http.createServer(function(req, res) {
    res.writeHead(200);
    res.end('Salut tout le monde !');
  });

app.use(bodyParser.json());


app.get('/:z/:x/:y.png', function (req, res){
	var x = parseInt(req.params.x);
	var y = parseInt(req.params.y);
	var z = parseInt(req.params.z);
	
	var col;
	var lig;

	if (z != 8){
		res.writeHead(404, {"Content-Type": "text/plain"});
  		 res.write("404 Not Found\n");
  		 res.end();
		 return;	
	}

	if (x>=0 && x<=179){
		var column = 180 - x;
		col = "W"+(column).pad(3);
	}
	else if (x>=180 && x<=359){
		var column = y-180;
		col = "E"+(column).pad(3);
	}else{
		res.writeHead(404, {"Content-Type": "text/plain"});
  		res.write("404 Not Found\n");
  		res.end();
		return;
	}

	if (y>=0 && y<=89){
		var column = 89 - y;
		lig = "N"+(column).pad(2);
	}
	else if (y>=90 && y<=179){
		var column = y-89;
		lig = "S"+(column).pad(2);
	}else{
		 res.writeHead(404, {"Content-Type": "text/plain"});
  		 res.write("404 Not Found\n");
  		 res.end();
		 return;
	}

	var get = new hbaseDB.Get(lig+col);
	client.get("villavicencio", get, function (err, row) {
		if (err || row == null){
			var img = fs.readFileSync('./empty.png');
		    res.writeHead(200, {'Content-Type': 'empty.png' });
		    res.end(img, 'binary');
		}
		else{
			res.writeHead(200, {'Content-Type':'image/png'});
			res.end(row.cols['hgt_data:image'].value.toString('base64'),'Base64');
		}
		
	});
})

app.get('/', function (req, res) {
    res.send('BigData');
})

app.listen(8000, function () {
    console.log('Example app listening on port 8000!')
})

app.use(express.static(path.join(__dirname, 'public')));

module.exports = app;
