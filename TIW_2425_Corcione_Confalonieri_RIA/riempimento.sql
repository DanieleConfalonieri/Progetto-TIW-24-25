-- Impostazioni iniziali per l'esecuzione dello script
USE `tiw`;
/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

-- =================================================================
-- Popolamento della tabella `docente` (3 docenti)
-- =================================================================
LOCK TABLES `docente` WRITE;
/*!40000 ALTER TABLE `docente` DISABLE KEYS */;
INSERT INTO `docente` VALUES (100000,'Piero','Fraternali','piero','passPiero'),(200000,'Stefano','Bregni','bregni','passBregni'),(300000,'Daniele','Braga','braga','passBraga');
/*!40000 ALTER TABLE `docente` ENABLE KEYS */;
UNLOCK TABLES;

-- =================================================================
-- Popolamento della tabella `studente` (12 studenti)
-- Un pool sufficiente per popolare densamente gli appelli secondo le regole.
-- =================================================================
LOCK TABLES `studente` WRITE;
/*!40000 ALTER TABLE `studente` DISABLE KEYS */;
INSERT INTO `studente` VALUES (101010,'Mario','Rossi','mario.rossi','passMario','IngInf'),(102030,'Anna','Verdi','anna.verdi','passAnna','IngInf'),(103040,'Luca','Bianchi','luca.bianchi','passLuca','IngInf'),(104050,'Giulia','Neri','giulia.neri','passGiulia','IngInf'),(105060,'Matteo','Galli','matteo.galli','passMatteo','IngInf'),(106070,'Sara','Conti','sara.conti','passSara','IngInf'),(107080,'Paolo','Ferrari','paolo.ferrari','passPaoloF','IngInf'),(108090,'Laura','Romano','laura.romano','passLaura','IngInf'),(109100,'Simone','Ricci','simone.ricci','passSimoneR','IngInf'),(110110,'Chiara','Marino','chiara.marino','passChiaraM','IngInf'),(111120,'Davide','Greco','davide.greco','passDavideG','IngElettronica'),(112130,'Francesco','Lombardi','francesco.lombardi','passFrancescoL','IngElettronica');
/*!40000 ALTER TABLE `studente` ENABLE KEYS */;
UNLOCK TABLES;

-- =================================================================
-- Popolamento della tabella `corso` (6 corsi totali, 2 per docente)
-- =================================================================
LOCK TABLES `corso` WRITE;
/*!40000 ALTER TABLE `corso` DISABLE KEYS */;
INSERT INTO `corso` VALUES (1,'TIW',100000),(2,'DB2',100000),(3,'SDR',200000),(4,'Teoria dei Segnali',200000),(5,'Calcolatori Elettronici',300000),(6,'Programmazione',400000);
/*!40000 ALTER TABLE `corso` ENABLE KEYS */;
UNLOCK TABLES;

-- =================================================================
-- Popolamento della tabella `appello` (18 appelli totali, 3 per corso)
-- =================================================================
LOCK TABLES `appello` WRITE;
/*!40000 ALTER TABLE `appello` DISABLE KEYS */;
-- 3 Appelli per corso 1 (TIW)
INSERT INTO `appello` VALUES (1,'2025-06-10',1),(2,'2025-07-10',1),(3,'2025-09-10',1);
-- 3 Appelli per corso 2 (DB2)
INSERT INTO `appello` VALUES (4,'2025-06-12',2),(5,'2025-07-12',2),(6,'2025-09-12',2);
-- 3 Appelli per corso 3 (SDR)
INSERT INTO `appello` VALUES (7,'2025-06-14',3),(8,'2025-07-14',3),(9,'2025-09-14',3);
-- 3 Appelli per corso 4 (Teoria dei Segnali)
INSERT INTO `appello` VALUES (10,'2025-06-16',4),(11,'2025-07-16',4),(12,'2025-09-16',4);
-- 3 Appelli per corso 5 (Calcolatori Elettronici)
INSERT INTO `appello` VALUES (13,'2025-06-18',5),(14,'2025-07-18',5),(15,'2025-09-18',5);
-- 3 Appelli per corso 6 (Programmazione)
INSERT INTO `appello` VALUES (16,'2025-06-20',6),(17,'2025-07-20',6),(18,'2025-09-20',6);
/*!40000 ALTER TABLE `appello` ENABLE KEYS */;
UNLOCK TABLES;

-- =================================================================
-- Popolamento della tabella `frequenza` (densa e coerente)
-- Ogni corso ha un bacino di 8 studenti che lo frequentano.
-- =================================================================
LOCK TABLES `frequenza` WRITE;
/*!40000 ALTER TABLE `frequenza` DISABLE KEYS */;
-- Corso 1: TIW
INSERT INTO frequenza VALUES (101010,1),(102030,1),(103040,1),(104050,1),(105060,1),(106070,1),(107080,1),(108090,1);
-- Corso 2: DB2
INSERT INTO frequenza VALUES (101010,2),(102030,2),(103040,2),(104050,2),(109100,2),(110110,2),(111120,2),(112130,2);
-- Corso 3: SDR
INSERT INTO frequenza VALUES (105060,3),(106070,3),(107080,3),(108090,3),(109100,3),(110110,3),(111120,3),(112130,3);
-- Corso 4: Teoria dei Segnali
INSERT INTO frequenza VALUES (101010,4),(103040,4),(105060,4),(107080,4),(109100,4),(111120,4),(102030,4),(104050,4);
-- Corso 5: Calcolatori Elettronici
INSERT INTO frequenza VALUES (101010,5),(102030,5),(105060,5),(106070,5),(109100,5),(110110,5),(111120,5),(112130,5);
-- Corso 6: Programmazione
INSERT INTO frequenza VALUES (101010,6),(102030,6),(103040,6),(104050,6),(105060,6),(106070,6),(107080,6),(108090,6);
/*!40000 ALTER TABLE `frequenza` ENABLE KEYS */;
UNLOCK TABLES;

-- =================================================================
-- Popolamento della tabella `iscrizione`
-- SCRIPT COMPLETO E DEFINITIVO: 6 iscritti per ognuno dei 18 appelli.
-- =================================================================
LOCK TABLES `iscrizione` WRITE;
/*!40000 ALTER TABLE `iscrizione` DISABLE KEYS */;

-- === CORSO 1: TIW (Appelli 1, 2, 3) ===
INSERT INTO `iscrizione` VALUES (1,101010,'28','PUBBLICATO',NULL),(1,102030,'30L','PUBBLICATO',NULL),(1,103040,'RIPROVATO','INSERITO',NULL),(1,104050,'18','RIFIUTATO',NULL),(1,105060,'ASSENTE','INSERITO',NULL),(1,106070,'25','PUBBLICATO',NULL);
INSERT INTO `iscrizione` VALUES (2,103040,'22','PUBBLICATO',NULL),(2,104050,'24','PUBBLICATO',NULL),(2,105060,'21','PUBBLICATO',NULL),(2,107080,'29','PUBBLICATO',NULL),(2,108090,'26','PUBBLICATO',NULL),(2,101010,'RIPROVATO','INSERITO',NULL);
INSERT INTO `iscrizione` VALUES (3,101010,'29','PUBBLICATO',NULL),(3,102030,'30','PUBBLICATO',NULL),(3,106070,'27','PUBBLICATO',NULL),(3,107080,'28','PUBBLICATO',NULL),(3,108090,'26','INSERITO',NULL),(3,105060,'24','PUBBLICATO',NULL);
-- === CORSO 2: DB2 (Appelli 4, 5, 6) ===
INSERT INTO `iscrizione` VALUES (4,101010,'27','PUBBLICATO',NULL),(4,102030,'29','PUBBLICATO',NULL),(4,103040,'RIPROVATO','INSERITO',NULL),(4,104050,'18','RIFIUTATO',NULL),(4,109100,'ASSENTE','INSERITO',NULL),(4,110110,'26','PUBBLICATO',NULL);
INSERT INTO `iscrizione` VALUES (5,103040,'24','PUBBLICATO',NULL),(5,104050,'25','PUBBLICATO',NULL),(5,109100,'28','PUBBLICATO',NULL),(5,111120,'30','PUBBLICATO',NULL),(5,112130,'22','PUBBLICATO',NULL),(5,101010,'RIPROVATO','INSERITO',NULL);
INSERT INTO `iscrizione` VALUES (6,101010,'28','PUBBLICATO',NULL),(6,102030,'30L','PUBBLICATO',NULL),(6,110110,'27','PUBBLICATO',NULL),(6,111120,'29','PUBBLICATO',NULL),(6,112130,'26','INSERITO',NULL),(6,109100,'25','PUBBLICATO',NULL);
-- === CORSO 3: SDR (Appelli 7, 8, 9) ===
INSERT INTO `iscrizione` VALUES (7,105060,'26','PUBBLICATO',NULL),(7,106070,'28','PUBBLICATO',NULL),(7,107080,'RIPROVATO','INSERITO',NULL),(7,108090,'18','RIFIUTATO',NULL),(7,109100,'ASSENTE','INSERITO',NULL),(7,110110,'30','PUBBLICATO',NULL);
INSERT INTO `iscrizione` VALUES (8,107080,'23','PUBBLICATO',NULL),(8,108090,'22','PUBBLICATO',NULL),(8,109100,'27','PUBBLICATO',NULL),(8,111120,'29','PUBBLICATO',NULL),(8,112130,'25','PUBBLICATO',NULL),(8,105060,'RIPROVATO','INSERITO',NULL);
INSERT INTO `iscrizione` VALUES (9,105060,'27','PUBBLICATO',NULL),(9,106070,'30L','PUBBLICATO',NULL),(9,110110,'28','PUBBLICATO',NULL),(9,111120,'29','PUBBLICATO',NULL),(9,112130,'26','INSERITO',NULL),(9,107080,'25','PUBBLICATO',NULL);
-- === CORSO 4: Teoria dei Segnali (Appelli 10, 11, 12) ===
INSERT INTO `iscrizione` VALUES (10,101010,'25','PUBBLICATO',NULL),(10,102030,'27','PUBBLICATO',NULL),(10,103040,'RIPROVATO','INSERITO',NULL),(10,104050,'18','RIFIUTATO',NULL),(10,105060,'ASSENTE','INSERITO',NULL),(10,107080,'29','PUBBLICATO',NULL);
INSERT INTO `iscrizione` VALUES (11,103040,'21','PUBBLICATO',NULL),(11,104050,'23','PUBBLICATO',NULL),(11,105060,'26','PUBBLICATO',NULL),(11,109100,'30','PUBBLICATO',NULL),(11,111120,'28','PUBBLICATO',NULL),(11,101010,'RIPROVATO','INSERITO',NULL);
INSERT INTO `iscrizione` VALUES (12,101010,'26','PUBBLICATO',NULL),(12,102030,'28','PUBBLICATO',NULL),(12,107080,'30','PUBBLICATO',NULL),(12,109100,'29','PUBBLICATO',NULL),(12,111120,'27','INSERITO',NULL),(12,105060,'25','PUBBLICATO',NULL);
-- === CORSO 5: Calcolatori Elettronici (Appelli 13, 14, 15) ===
INSERT INTO `iscrizione` VALUES (13,101010,'28','PUBBLICATO',NULL),(13,102030,'30','PUBBLICATO',NULL),(13,105060,'RIPROVATO','INSERITO',NULL),(13,106070,'18','RIFIUTATO',NULL),(13,109100,'ASSENTE','INSERITO',NULL),(13,110110,'27','PUBBLICATO',NULL);
INSERT INTO `iscrizione` VALUES (14,105060,'24','PUBBLICATO',NULL),(14,106070,'22','PUBBLICATO',NULL),(14,109100,'26','PUBBLICATO',NULL),(14,111120,'29','PUBBLICATO',NULL),(14,112130,'25','PUBBLICATO',NULL),(14,101010,'RIPROVATO','INSERITO',NULL);
INSERT INTO `iscrizione` VALUES (15,101010,'26','PUBBLICATO',NULL),(15,102030,'30L','PUBBLICATO',NULL),(15,110110,'28','PUBBLICATO',NULL),(15,111120,'29','PUBBLICATO',NULL),(15,112130,'27','INSERITO',NULL),(15,109100,'25','PUBBLICATO',NULL);
-- === CORSO 6: Programmazione (Appelli 16, 17, 18) ===
INSERT INTO `iscrizione` VALUES (16,101010,'29','PUBBLICATO',NULL),(16,102030,'27','PUBBLICATO',NULL),(16,103040,'RIPROVATO','INSERITO',NULL),(16,104050,'18','RIFIUTATO',NULL),(16,105060,'ASSENTE','INSERITO',NULL),(16,106070,'26','PUBBLICATO',NULL);
INSERT INTO `iscrizione` VALUES (17,103040,'23','PUBBLICATO',NULL),(17,104050,'24','PUBBLICATO',NULL),(17,105060,'28','PUBBLICATO',NULL),(17,107080,'30','PUBBLICATO',NULL),(17,108090,'25','PUBBLICATO',NULL),(17,101010,'RIPROVATO','INSERITO',NULL);
INSERT INTO `iscrizione` VALUES (18,101010,'27','PUBBLICATO',NULL),(18,102030,'29','PUBBLICATO',NULL),(18,106070,'28','PUBBLICATO',NULL),(18,107080,'30L','PUBBLICATO',NULL),(18,108090,'26','INSERITO',NULL),(18,105060,'25','PUBBLICATO',NULL);

/*!40000 ALTER TABLE `iscrizione` ENABLE KEYS */;
UNLOCK TABLES;

-- =================================================================
-- Popolamento della tabella `verbale` (vuota)
-- =================================================================
LOCK TABLES `verbale` WRITE;
/*!40000 ALTER TABLE `verbale` DISABLE KEYS */;
/*!40000 ALTER TABLE `verbale` ENABLE KEYS */;
UNLOCK TABLES;

-- Ripristino delle impostazioni SQL originali
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;