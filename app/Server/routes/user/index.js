
var express = require('express'); 
var router = express.Router();
var bodyParser = require('body-parser');
var mysql = require('mysql'); 


//------------------------------------------//
//     Datenbankverbindung aufbauen         //
//------------------------------------------//
var con = mysql.createConnection({
    host: 'localhost',
    user: 'root',
    password:'',
    database: 'FoodUP',
    port: 3306
});

/***mit Datenbank verbinden***/
con.connect(function(err){
    if(err) throw err;
    console.log("Datenbankverbindung hergestellt!");
});




//------------------------------------------//
//            Firebase                      //
//------------------------------------------//

var admin = require("firebase-admin");
var registrationToken;
var serviceAccount = require("../../json/foodup-abb79-84f27fcadd3d.json"); 

admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
    databaseURL: "https://foodup-abb79.firebaseio.com" 
});





//------------------------------------------//
//               SQL                        //
//------------------------------------------//


// Abfrage zur Überprüfung verfügbarer Produkte im System

function getCheckUsersInventarForProductSQL(lebensmittelID, userID){
    var sql = `SELECT b.idBenutzer, b.benutzername,l.bezeichnung, l.idLebensmittel, i.mhd, b.longitude, b.latitude
                FROM inventar_hat_lebensmittel i
                JOIN tblbenutzer b
                ON b.fkInventar = i.id_fkInventar
                JOIN tblLebensmittel l
                ON l.idLebensmittel = i.id_fkLebensmittel
                WHERE i.id_fkLebensmittel = ` + lebensmittelId + `
                AND i.oeffentlichSichtbar = true
                AND b.idBenutzer != `+ userId;
    return sql;
}



// Koordinaten eines Benutzers

function getLoggedInUsersCoordinates(){
  let sql = 'SELECT latitude, longitude FROM tblBenutzer WHERE idBenutzer = 1';
  return sql;
}



// Produkte einer Einkaufsliste eines Users

function getFoodListProductsByUser(userId){
    let sql = `SELECT name, menge, l.bezeichnung, l.idLebensmittel
                FROM tblEinkaufsliste el
                JOIN tblEinkaufsliste_hat_Lebensmittel elhl
                ON el.idEinkaufsliste = elhl.tblEinkaufsliste_idEinkaufsliste
                JOIN tblLebensmittel l
                ON elhl.tblLebensmittel_idLebensmittel = l.idLebensmittel
                WHERE tblBenutzer_idBenutzer = ` + userId;
    return sql;
}



// Bezieht die Einkaufslisten

function getFoodList(idBenutzer){
    let sql =  `SELECT idEinkaufsliste,name
                FROM tbleinkaufsliste
                WHERE tblBenutzer_idBenutzer =`+idBenutzer;
    return sql;
}



// Produkte zu den Einkaufslisten

function getFoodListProducts(idBenutzer){
    let sql = `SELECT tblEinkaufsliste_idEinkaufsliste as idEinkaufsliste,tblLebensmittel_idLebensmittel as idLebensmittel ,l.bezeichnung,l.marke,menge,bilderPfad
             FROM foodUP.tbleinkaufsliste_hat_lebensmittel
             JOIN tbllebensmittel l
             ON tblLebensmittel_idLebensmittel = l.idLebensmittel
             WHERE tblEinkaufsliste_tblBenutzer_idBenutzer =`+idBenutzer;
    return sql;
}



// Bezieht die Lebensmittelverschwendung Dokumentation

function getWastedEntries(idBenutzer){
    let sql = `SELECT id_fkLebensmittel,menge as verschwendet
             FROM foodUP.tbllebensmittelverschwendung
             WHERE id_fkBenutzer=`+idBenutzer;

    return sql;
}



// Aktualisiert die Einkaufsliste

function updateProductInList(updateProduct, idEinkaufsliste, idBenutzer){
    let sql = `UPDATE tbleinkaufsliste_hat_lebensmittel
             SET menge =`+updateProduct.menge+`
             WHERE tblEinkaufsliste_idEinkaufsliste =`+idEinkaufsliste+`
             AND tblLebensmittel_idLebensmittel =`+updateProduct.lebensmittel+`
             AND tblEinkaufsliste_tblBenutzer_idBenutzer =`+idBenutzer;

    return sql;
}



// Erstellt eine Einkaufsliste

function createFoodList(foodList){
    let value = '('+foodList.idEinkaufsliste+','+foodList.idBenutzer+',"'+foodList.name+'")';
    let sql = 'INSERT INTO tbleinkaufsliste VALUES'+value;
    return sql;
}



// Fügt einer Einkaufsliste Produkte hinzu

function insertProductsToList(foodList){
    // Erst wird überprüft, ob mehrere Produkte für die Einkaufsliste eingetragen wurden.
    var sql = 'INSERT INTO tbleinkaufsliste_hat_lebensmittel VALUES ';
    for(var i = 0; i<foodList.products.length;i++){
      sql += '('+foodList.idEinkaufsliste+','+foodList.idBenutzer+','+foodList.products[i].id+','+foodList.products[i].menge+'),' 
      // Pro Produkt einen Datensatz hinzufügen.
    }
    // Sobald die Abfrage durchgeführt wurde, das letzte "," aus dem SQL Statement entfernen um Fehler des SQL Query zu vermeiden.
    sql = sql.substr(0,sql.length-1);
  
    return sql;
  }







//------------------------------------------//
//               FUNCTIONS                  //
//------------------------------------------//



// Überprüfung, ob Produkte vorm Verfall des MHD stehen

function checkExpiringFood(mhd_date){
  var dateObj = new Date(); // aktuelles Datum
  var response = false; 
  
  // Formatierung des Datums auf deutschen Standard
  var day = dateObj.getUTCDate();
  var month = (dateObj.getUTCMonth() < 10) ? '0'+dateObj.getUTCMonth() : dateObj.getUTCMonth();

  console.log(day, month);

  // Umwandlung des SQL Datums
  mhd_date = parseData(mhd_date);

  // Überprüfung, ob das MHD fast erreicht ist
  if(mhd_date['month'] <= Number(month)+1){
    if(mhd_date['day']-3 <= day){   //3 Tage
      // Falls das MHD fast erreicht ist, wird ein true zurückgegeben um das ablaufende Produkt zu kennzeichnen
      response = true; 
    }
  }
  return response;
}



// Das zuvor aus der Datenbank entnommene MHD in ein einheitliches Format umwandeln

function parseData(mhd_date){
  mhd_date = mhd_date.split('-'); // splittet die - aus der Variable und erstellt einen Array
  console.log(mhd_date);
  mhd_date[2] = parseInt(mhd_date[2].substr(0,2)) +1; // Array Anpassung, dass nur Tag, Monat und Jahr entnommen werden
  console.log('mhd_date[2]: ', mhd_date[2]);
  mhd_date = {
    'day': mhd_date[2],
    'month': mhd_date[1],
    'year': mhd_date[0]
  }
  return mhd_date;
}



// Einkaufslisten parsen

function parseFoodList(foodListArray, result){
  var foodLists = []

  for(list in foodListArray){
    let foodList = {
      id: foodListArray[list].idEinkaufsliste,
      name: foodListArray[list].name,
      products:[]
    }

    for(var i=0; i<Object.keys(result).length; i++){
      if(foodList.id == result[i].idEinkaufsliste){
        let product = {
          idLebensmittel: result[i].idLebensmittel,
          bilderPfad: result[i].imagePath,
          bezeichnung: result[i].bezeichnung,
          marke: result[i].marke,
          menge: result[i].menge
        }
        foodList.products.push(product);
      }
    }
    foodLists.push(foodList)
  }
  return foodLists;
}



// Distanzberechnung in km  (https://stackoverflow.com/questions/27928/calculate-distance-between-two-latitude-longitude-points-haversine-formula)

function getDistanceFromLatLonInKm(lat1, lon1, lat2, lon2){
  var R = 6371; // Radius of the earth in km
  var dLat = deg2rad(lat2-lat1);  // deg2rad below
  var dLon = deg2rad(lon2-lon1); 
  var a = 
    Math.sin(dLat/2) * Math.sin(dLat/2) +
    Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * 
    Math.sin(dLon/2) * Math.sin(dLon/2)
    ; 
  var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
  var d = R * c; // Distance in km
  return d;
}

// Umrechnung von Gradmaß in Bogenmaß 
function deg2rad(deg) {
  return deg * (Math.PI/180)
}



// Benachrichtigung der Benutzer (1min)

function notifyUser(){
  setInterval(checkForNeededProducts, 1000);
}



// Senden der Benachrichtigung mittels FCM

function sendMessage(potentialUsers){
  if(registrationToken){
    var payload = {
      data: { userList: JSON.stringify(potentialUsers)}
    };

    admin.messaging().sendToDevice(registrationToken, payload)
    .then(function(response){
      console.log("Nachricht wurde gesendet: ", response);
    }).catch(function(err){
      console.log("Nachricht wurde nicht gesendet: ", err);
    });
  }else{
    console.log("Token noch nicht gesetzt");
  }
}




// Überprüfung, ob Produkte der Einkaufsliste eines Benutzers in den 
// Inventaren anderer Benutzer kurz vor dem Verfall des MHD stehen

function checkForNeededProducts(){

  // Beziehe die Koordinaten des Nutzers
  var loggedInUserCoordinates = {laengengrad: null, breitengrad: null};
  var listeGesuchteProdukte = [];
  var potentialUsers = [];

  var sql = getLoggedInUsersCoordinates();

  con.query(sql, function(err, result){
    if(err) throw err;
    console.log(result);
    loggedInUserCoordinates.laengengrad = result[0].longitude;
    loggedInUserCoordinates.breitengrad = result[0].latitude;
  });

  console.log('Koordinaten des eingelogten Nutzers: ', loggedInUserCoordinates);

  // Beziehung der Einkaufslisten mit Produkten des eingeloggten Nutzers
  sql = getFoodListProductsByUser(1);

  con.query(sql, function(err, result){
    if(err) throw err;

    if(result.length >0){
      listeGesuchteProdukte = result;
      for(var produktIndex in listeGesuchteProdukte){
        console.log('### Gesuchte Produkte: ',listeGesuchteProdukte[produktIndex]);
        console.log('### pIndex: ', produktIndex); 

        // Beziehe die Produkte anderer Nutzer aus deren Inventar
        sql = getCheckUsersInventarForProductSQL(listeGesuchteProdukte[produktIndex].idLebensmittel, 1);

        con.query(sql, function(err, result){
          if(err) throw err;

          if(result.length > 0){
            for(var resultIndex in result){
              console.log('### rIndex: ', resultIndex);

            if(result[resultIndex].mhd != null && checkExpiringFood(JSON.stringify(result[resultIndex].mhd))){
              
              // Prüfung, ob der Benutzer im Array der verfügbaren/angebotenen Produkte bereits enthalten ist
              var benutzerFehlt = true;
              var hIndex = 0; //HilfsIndex, an welcher Position im Array sich der Benutzer befindet

              for(var pUserIndex in potentialUsers){
                if(potentialUsers[pUserIndex].benutzername === result[resultIndex].benutzername){
                  benutzerFehlt = false;
                  hIndex = pUserIndex;
                }
              }

              if(benutzerFehlt){  // Benutzer nicht vorhanden
                var potentialUser = {
                  id: result[resultIndex].idBenutzer,
                  benutzername: result[resultIndex].benutzername,
                  distanz: getDistanceFromLatLonInKm(
                      loggedInUserCoordinates.breitengrad,
                      loggedInUserCoordinates.laengengrad,
                      result[resultIndex].latitude,
                      result[resultIndex].longitude
                  ),
                  products: [{
                      id: result[resultIndex].idLebensmittel,
                      name: result[resultIndex].bezeichnung,
                      mhd: result[resultIndex].mhd
                  }]
                };
                potentialUsers.push(potentialUser);
              }

              else{
                // Prüfung, ob das Produkt im Array der verfügbaren/angebotenen Produkte des potentiellen Users enthalten ist
                var produktFehlt = false;
                var potentialUserProducts = potentialUsers[hIndex].products;
                for(var pUserProductIndex in potentialUserProducts){
                  if(potentialUserProducts[pUserProductIndex].id === result[resultIndex].idLebensmittel){
                    produktFehlt = true;
                  }
                }
                if(produktFehlt){
                  potentialUsers[hIndex].products.push({
                    id: result[resultIndex].idLebensmittel,
                    name: result[resultIndex].bezeichnung,
                    mhd: result[resultIndex].mhd
                  });
                }
              }
            }
            }
            sendMessage(potentialUsers);
          }
        });
      }
    }else{
      console.log('Es befinden sich keine Produkte auf den Einkaufslisten');
    }
  });
}







//------------------------------------------//
//              ROUTING                     //
//------------------------------------------//

router.get('/:id/einkaufsliste/',function(req,res){
    var sql = '';
      sql = getFoodList(req.params.id);
  
      let promiseSQL = new Promise(function(resolve,reject){
        con.query(sql,function(err,result){
          if(err) reject(err);
          resolve(result);
        });
      });
  
      promiseSQL.then(function(fromResolve){
       let sql = getFoodListProducts(req.params.id);
       var foodListArray = fromResolve;
  
       con.query(sql,function(err,result){
         if(err) throw err;
  
         var parsedFoodLists = parseFoodList(foodListArray,result);
         if(fromResolve.length >0){
         res.status(200).send(parsedFoodLists); // Ausgabe der geparsten Einkaufsliste
         }
         else{
           res.status(404).send("Keine Einträge gefunden");
         }
       });
  
      }).catch(function(fromReject){
  
      });
  
});

router.post('/:id/einkaufsliste',bodyParser.json(),function(req,res){

  // Einkaufslisten Objekt
  var  foodList = {
    idEinkaufsliste:req.body.id,
    idBenutzer:req.params.id,
    name:req.body.name,
    products : req.body.products
  }

  // Einkaufsliste Erstellen
  let sql = createFoodList(foodList);

  con.query(sql,function(err,result){
    if(err) res.status(500).send(err); // Bei einem Fehler der Ausführung des SQL Query.

    // Callback Funktion, falls kein Error geschmissen wurde - SQL Query erfolgreich.
    // Nachdem die Einkaufsliste angelegt wurde, gilt es die Produkte in die Tabelle "einkaufsliste_hat_lebensmittel" einzupflegen
    let sqlProducts = insertProductsToList(foodList);

    con.query(sqlProducts,function(err,result){
      if(err) res.status(500).send(err);

      res.status(201).send("Einkaufsliste erfolgreich angelegt");
    });
  })
});

router.get('/:id/verschwLebensmittel',function(req,res){
  var sql = getWastedEntries(req.params.id);

  con.query(sql,function(err,result){
    if(err) throw err;

    if(result.length >0){
      res.status(200).send(result); // Ausgabe der geparsten Einkaufsliste
    }

    else{
      res.status(404).send("Keine Einträge gefunden");
    }
  });

});

router.put('/:id/einkaufsliste/:idEinkaufsliste',bodyParser.json(),function(req,res){
  var updateProduct = req.body;
  var sql = '';
  sql = updateProductInList(updateProduct,req.params.idEinkaufsliste,req.params.id);

  con.query(sql,function(err,result){
    if(err) res.status(406).send(err);

    res.status(201).send("Produkt wurde aktualisiert"); // Ausgabe der geparsten Einkaufsliste

  });
});


// Diese Route wird für den Prototyp benötigt, um den Token zusetzen
// so umgehen wir eine hardcodierte Variante eines Tokens, der bei Start der App
// neu generiert wird.
router.post('/:id/token',bodyParser.json(),function(req,res){
  registrationToken = req.body.token;
  console.log(registrationToken);
  res.status(200).send("Token wurde gesendet");
});

// Sobald die JavaScript-Datei kompiliert wurde, wird die Funktion notifyUser ausgeführt.
// Dies hat den Zweck, dass der Server bei Laufzeit in einem bestimmten Intervall dem
// Benutzer potentielle Benutzer vorschlagen kann, über das FCM.
notifyUser();

//Bereitstellen des Moduls um require in der app.js einbinden zu können.
module.exports = router;



