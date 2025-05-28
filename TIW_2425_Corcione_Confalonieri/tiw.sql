CREATE DATABASE  IF NOT EXISTS `tiw` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `tiw`;
-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: tiw
-- ------------------------------------------------------
-- Server version	8.0.41

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

--
-- Table structure for table `appello`
--

DROP TABLE IF EXISTS `appello`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `appello` (
  `CodiceAppello` int NOT NULL,
  `DataAppello` date NOT NULL,
  `CodiceCorso` int NOT NULL,
  PRIMARY KEY (`CodiceAppello`),
  UNIQUE KEY `DataAppello` (`DataAppello`,`CodiceCorso`),
  KEY `CodiceCorso` (`CodiceCorso`),
  CONSTRAINT `appello_ibfk_1` FOREIGN KEY (`CodiceCorso`) REFERENCES `corso` (`CodiceCorso`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `appello`
--

LOCK TABLES `appello` WRITE;
/*!40000 ALTER TABLE `appello` DISABLE KEYS */;
INSERT INTO `appello` VALUES (01, '2025-06-01', 01),(02, '2025-07-01', 01), (03, '2025-06-02', 02);
/*!40000 ALTER TABLE `appello` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `corso`
--

DROP TABLE IF EXISTS `corso`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `corso` (
  `CodiceCorso` int NOT NULL,
  `NomeCorso` varchar(20) NOT NULL,
  `MatricolaDocente` int NOT NULL,
  PRIMARY KEY (`CodiceCorso`),
  KEY `MatricolaDocente` (`MatricolaDocente`),
  CONSTRAINT `corso_ibfk_1` FOREIGN KEY (`MatricolaDocente`) REFERENCES `docente` (`MatricolaDocente`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `corso`
--

LOCK TABLES `corso` WRITE;
/*!40000 ALTER TABLE `corso` DISABLE KEYS */;
INSERT INTO `corso` VALUES (01, "TIW", 100000),(02, "SDR", 200000),(03, "DB2", 100000);
/*!40000 ALTER TABLE `corso` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `docente`
--

DROP TABLE IF EXISTS `docente`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `docente` (
  `MatricolaDocente` int NOT NULL,
  `Nome` varchar(20) NOT NULL,
  `Cognome` varchar(20) NOT NULL,
  `Mail` varchar(60) NOT NULL,
  `PasswordDocente` varchar(100) NOT NULL,
  PRIMARY KEY (`MatricolaDocente`),
  UNIQUE KEY `Mail` (`Mail`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `docente`
--

LOCK TABLES `docente` WRITE;
/*!40000 ALTER TABLE `docente` DISABLE KEYS */;
INSERT INTO `docente` VALUES (100000,'Piero','Fraternali','piero','passPiero'),(200000,'Stefano','Bregni','bregni','passBregni'),(300000,'Daniele','Braga','braga','passBraga');
/*!40000 ALTER TABLE `docente` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `frequenza`
--

DROP TABLE IF EXISTS `frequenza`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `frequenza` (
  `MatricolaStudente` int NOT NULL,
  `CodiceCorso` int NOT NULL,
  PRIMARY KEY (`MatricolaStudente`,`CodiceCorso`),
  KEY `CodiceCorso` (`CodiceCorso`),
  CONSTRAINT `frequenza_ibfk_1` FOREIGN KEY (`MatricolaStudente`) REFERENCES `studente` (`MatricolaStudente`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `frequenza_ibfk_2` FOREIGN KEY (`CodiceCorso`) REFERENCES `corso` (`CodiceCorso`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `frequenza`
--

LOCK TABLES `frequenza` WRITE;
/*!40000 ALTER TABLE `frequenza` DISABLE KEYS */;
/*!40000 ALTER TABLE `frequenza` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `iscrizione`
--

DROP TABLE IF EXISTS `iscrizione`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `iscrizione` (
  `CodiceAppello` int NOT NULL,
  `MatricolaStudente` int NOT NULL,
  `Voto` enum('ASSENTE','RIMANDATO','RIPROVATO','18','19','20','21','22','23','24','25','26','27','28','29','30','30L') DEFAULT NULL,
  `StatoVoto` enum('NON INSERITO','INSERITO','PUBBLICATO','RIFIUTATO','VERBALIZZATO') NOT NULL,
  PRIMARY KEY (`CodiceAppello`,`MatricolaStudente`),
  KEY `MatricolaStudente` (`MatricolaStudente`),
  CONSTRAINT `iscrizione_ibfk_1` FOREIGN KEY (`CodiceAppello`) REFERENCES `appello` (`CodiceAppello`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `iscrizione_ibfk_2` FOREIGN KEY (`MatricolaStudente`) REFERENCES `studente` (`MatricolaStudente`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `iscrizione`
--

LOCK TABLES `iscrizione` WRITE;
/*!40000 ALTER TABLE `iscrizione` DISABLE KEYS */;
INSERT INTO `iscrizione` VALUES (1, 215144, '30', 'INSERITO');
/*!40000 ALTER TABLE `iscrizione` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `studente`
--

DROP TABLE IF EXISTS `studente`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `studente` (
  `MatricolaStudente` int NOT NULL,
  `Nome` varchar(20) NOT NULL,
  `Cognome` varchar(20) NOT NULL,
  `Mail` varchar(60) NOT NULL,
  `PasswordStudente` varchar(100) NOT NULL,
  `CorsoLaurea` varchar(30) NOT NULL,
  PRIMARY KEY (`MatricolaStudente`),
  UNIQUE KEY `Mail` (`Mail`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `studente`
--

LOCK TABLES `studente` WRITE;
/*!40000 ALTER TABLE `studente` DISABLE KEYS */;
INSERT INTO `studente` VALUES (215144, 'Raffaella', 'Corcione', 'raffa', 'passRaf', 'IngInf');
/*!40000 ALTER TABLE `studente` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `verbale`
--

DROP TABLE IF EXISTS `verbale`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `verbale` (
  `CodiceVerbale` int NOT NULL,
  `DataVerbale` date NOT NULL,
  `Ora` time NOT NULL,
  `CodiceDocente` int NOT NULL,
  `CodiceAppello` int NOT NULL,
  PRIMARY KEY (`CodiceVerbale`),
  KEY `CodiceDocente` (`CodiceDocente`),
  KEY `CodiceAppello` (`CodiceAppello`),
  CONSTRAINT `verbale_ibfk_1` FOREIGN KEY (`CodiceDocente`) REFERENCES `docente` (`MatricolaDocente`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `verbale_ibfk_2` FOREIGN KEY (`CodiceAppello`) REFERENCES `appello` (`CodiceAppello`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `verbale`
--

LOCK TABLES `verbale` WRITE;
/*!40000 ALTER TABLE `verbale` DISABLE KEYS */;
/*!40000 ALTER TABLE `verbale` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'tiw'
--

--
-- Dumping routines for database 'tiw'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-04-16 17:18:00