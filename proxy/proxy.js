/**
 * Created by fantasy on 2015/9/8.
 */
var http = require('http')
    ,httpProxy = require('http-proxy')
    ,proxy = httpProxy.createProxyServer({})
    ,fs = require("fs")
    ,path = require('path')
;
proxy.on('err', function(e){
   console.log(e);
});
var server = http.createServer(function(req, res) {
    console.log(req.url);
    proxy.web(req, res, { target: 'http://127.0.0.1' });


});
console.log("start");
server.listen(4000);
