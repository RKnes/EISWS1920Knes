
SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';


-- Schema FoodUP

CREATE SCHEMA IF NOT EXISTS `FoodUP` DEFAULT CHARACTER SET utf8 ;
USE `FoodUP` ;



-- Table Benutzer

DROP TABLE IF EXISTS `FoodUP`.`tblBenutzer`;

CREATE TABLE IF NOT EXISTS `FoodUP`.`tblBenutzer` (
  `idBenutzer` INT NOT NULL AUTO_INCREMENT,
  `vorname` VARCHAR(45) NOT NULL,
  `nachname` VARCHAR(80) NOT NULL,
  `geburtsdatum` DATE NOT NULL,
  `adresse` VARCHAR(120) NOT NULL,
  `ort` VARCHAR(80) NOT NULL,
  `plz` INT NOT NULL,
  `benutzername` VARCHAR(80) NOT NULL,
  `anzeigename` VARCHAR(80) NULL,
  `passwort` VARCHAR(80) NOT NULL,
  `fkStatistik` INT NULL,
  `fkInventar` INT NULL,
  `token` VARCHAR(500) NULL,
  `longitude` VARCHAR(500) NULL,
  `latitude` VARCHAR(500) NULL,
  PRIMARY KEY (`idBenutzer`),
  CONSTRAINT `fk_tblBenutzer_tblStatistik1`
    FOREIGN KEY (`fkStatistik`)
    REFERENCES `FoodUP`.`tblStatistik` (`idStatistik`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_tblBenutzer_tblInventar1`
    FOREIGN KEY (`fkInventar`)
    REFERENCES `FoodUP`.`tblInventar` (`idInventar`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- Table Lebensmittel

DROP TABLE IF EXISTS `FoodUP`.`tblLebensmittel`;
CREATE TABLE IF NOT EXISTS `FoodUP`.`tblLebensmittel` (
  `idLebensmittel` INT NOT NULL,
  `bezeichnung` VARCHAR(125) NOT NULL,
  `ean` VARCHAR(125) NOT NULL,
  `marke` VARCHAR(125) NOT NULL,
  `bilderPfad` VARCHAR(500) NULL,
  PRIMARY KEY (`idLebensmittel`))
ENGINE = InnoDB;



-- Table Inventar

DROP TABLE IF EXISTS `FoodUP`.`tblInventar`;

CREATE TABLE IF NOT EXISTS `FoodUP`.`tblInventar` (
  `idInventar` INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`idInventar`))
ENGINE = InnoDB;



-- Table Einkaufsliste

DROP TABLE IF EXISTS `FoodUP`.`tblEinkaufsliste`;

CREATE TABLE IF NOT EXISTS `FoodUP`.`tblEinkaufsliste` (
  `idEinkaufsliste` INT NOT NULL AUTO_INCREMENT,
  `tblBenutzer_idBenutzer` INT NOT NULL,
  `name` VARCHAR(250) NOT NULL,
  PRIMARY KEY (`idEinkaufsliste`, `tblBenutzer_idBenutzer`),
  CONSTRAINT `fk_tblEinkaufsliste_tblBenutzer1`
    FOREIGN KEY (`tblBenutzer_idBenutzer`)
    REFERENCES `FoodUP`.`tblBenutzer` (`idBenutzer`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;



-- Table Lebensmittelverschwendung

DROP TABLE IF EXISTS `FoodUP`.`tblLebensmittelverschwendung`;

CREATE TABLE IF NOT EXISTS `FoodUP`.`tblLebensmittelverschwendung` (
  `idLebensmittelverschwendung` INT NOT NULL AUTO_INCREMENT,
  `id_fkBenutzer` INT NOT NULL,
  `id_fkLebensmittel` INT NOT NULL,
  `fkBenutzer` INT NULL,
  `menge` INT NOT NULL,
  `datum` DATE NULL,
  UNIQUE (`idLebensmittelverschwendung`),
  PRIMARY KEY (`idLebensmittelverschwendung`,`id_fkBenutzer`, `id_fkLebensmittel`),
  CONSTRAINT `fk_tblBenutzer_has_tblLebensmittel_tblBenutzer1`
    FOREIGN KEY (`id_fkBenutzer`)
    REFERENCES `FoodUP`.`tblBenutzer` (`idBenutzer`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_tblBenutzer_has_tblLebensmittel_tblLebensmittel2`
    FOREIGN KEY (`id_fkLebensmittel`)
    REFERENCES `FoodUP`.`tblLebensmittel` (`idLebensmittel`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_tblBenutzer_has_tblLebensmittel_tblBenutzer2`
    FOREIGN KEY (`fkBenutzer`)
    REFERENCES `FoodUP`.`tblBenutzer` (`idBenutzer`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;



-- Table Inventar_hat_Lebensmittel

DROP TABLE IF EXISTS `FoodUP`.`tblInventar_hat_Lebensmittel`;

CREATE TABLE IF NOT EXISTS `FoodUP`.`tblInventar_hat_Lebensmittel` (
  `id_fkInventar` INT NOT NULL,
  `id_fkLebensmittel` INT NOT NULL,
  `menge` INT NOT NULL,
  `mhd` DATE NULL,
  `oeffentlichSichtbar` BOOLEAN NOT NULL,
  PRIMARY KEY (`id_fkInventar`, `id_fkLebensmittel`),
  CONSTRAINT `fk_tblInventar_has_tblLebensmittel_tblInventar1`
    FOREIGN KEY (`id_fkInventar`)
    REFERENCES `FoodUP`.`tblInventar` (`idInventar`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_tblInventar_hat_tblLebensmittel_tblLebensmittel1`
    FOREIGN KEY (`id_fkLebensmittel`)
    REFERENCES `FoodUP`.`tblLebensmittel` (`idLebensmittel`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;




-- Table Einkaufsliste hat Lebensmittel

DROP TABLE IF EXISTS `FoodUP`.`tblEinkaufsliste_hat_Lebensmittel`;

CREATE TABLE IF NOT EXISTS `FoodUP`.`tblEinkaufsliste_hat_Lebensmittel` (
  `tblEinkaufsliste_idEinkaufsliste` INT NOT NULL,
  `tblEinkaufsliste_tblBenutzer_idBenutzer` INT NOT NULL,
  `tblLebensmittel_idLebensmittel` INT NOT NULL,
  `menge` INT NOT NULL,
  PRIMARY KEY (`tblEinkaufsliste_idEinkaufsliste`, `tblEinkaufsliste_tblBenutzer_idBenutzer`, `tblLebensmittel_idLebensmittel`),
  CONSTRAINT `fk_tblEinkaufsliste_has_tblLebensmittel_tblEinkaufsliste1`
    FOREIGN KEY (`tblEinkaufsliste_idEinkaufsliste` , `tblEinkaufsliste_tblBenutzer_idBenutzer`)
    REFERENCES `FoodUP`.`tblEinkaufsliste` (`idEinkaufsliste` , `tblBenutzer_idBenutzer`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_tblEinkaufsliste_has_tblLebensmittel_tblLebensmittel1`
    FOREIGN KEY (`tblLebensmittel_idLebensmittel`)
    REFERENCES `FoodUP`.`tblLebensmittel` (`idLebensmittel`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;



SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
