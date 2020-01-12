
//Module einbinden
var express = require('express');
var router = express.Router();

//Für Zugriff an Objekte binden 
var app = express();
const port = 3000;



/****ROUTING****/ 

//Pfad an eine Variable binden
const user = require('./routes/user');
//Einbinden der Pfade
//app.use('/user', user);
//app.use('/image/', express.static('img'));

app.listen(port, function(){
    console.log("Express App läuft auf Port " + port);
});
