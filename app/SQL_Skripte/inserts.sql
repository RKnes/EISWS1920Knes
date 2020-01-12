INSERT INTO `FoodUP`.`tblinventar` (`idInventar`) VALUES ('1');
INSERT INTO `FoodUP`.`tblinventar` (`idInventar`) VALUES ('2');

INSERT INTO `FoodUP`.`tblbenutzer` (`idBenutzer`, `vorname`, `nachname`, `geburtsdatum`, `adresse`, `ort`, `plz`, `benutzername`, `passwort`,`fkInventar`, `longitude`, `latitude`) 
VALUES ('1', 'Lisa', 'Fröhlich', '1994-12-06', 'Teststr 1', 'Köln', '50969', 'FoodUP1', 'Test1234',1, '6.941835', '50.909655');
INSERT INTO `FoodUP`.`tblbenutzer` (`idBenutzer`, `vorname`, `nachname`, `geburtsdatum`, `adresse`, `ort`, `plz`, `benutzername`, `passwort`,`fkInventar`, `longitude`, `latitude`) 
VALUES ('2', 'Max', 'Müller', '1994-01-01', 'Teststr 2', 'Köln', '50969', 'FoodUP2', 'Test1234',2, '6.958281400000033', '50.94127839999999');

INSERT INTO `FoodUP`.`tbllebensmittel` (`idLebensmittel`, `bezeichnung`, `ean`, `marke`,`bilderPfad`) 
VALUES ('1', 'Marmelade Erbeere', '4388844149533', 'Hero','image/hero-diet-low-carb-marmelade-erdbeer-280g');
INSERT INTO `FoodUP`.`tbllebensmittel` (`idLebensmittel`, `bezeichnung`, `ean`, `marke`,`bilderPfad`) 
VALUES ('2', 'Haltbare Milch laktosefrei', '2100999001007', 'Chiquita','image/hmilch_1l_laktosefrei_15');

INSERT INTO `FoodUP`.`inventar_hat_lebensmittel` (`id_fkInventar`, `id_fkLebensmittel`, `menge`, `mhd`,`oeffentlichSichtbar`) 
VALUES ('1', '1', '2', '2018-11-12',0);
INSERT INTO `FoodUP`.`inventar_hat_lebensmittel` (`id_fkInventar`, `id_fkLebensmittel`, `menge`,`mhd`,`oeffentlichSichtbar`) 
VALUES ('2', '1', '3','2019-01-23',1);

INSERT INTO `tblEinkaufsliste` VALUES (1,1,'Weihnachten'),(2,1,'Geburtstag');
INSERT INTO `tblEinkaufsliste_hat_Lebensmittel` VALUES
(1,1,1,2),
(2,1,2,1),
(1,1,2,1);

INSERT INTO `tblLebensmittelverschwendung` VALUES
(1,1,1,null,2,null),
(2,1,1,null,1,null),
(3,1,2,null,1,null);