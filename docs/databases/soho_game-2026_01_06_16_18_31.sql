/*!999999\- enable the sandbox mode */ 
-- MariaDB dump 10.19  Distrib 10.6.18-MariaDB, for debian-linux-gnu (x86_64)
--
-- Host: 192.168.0.101    Database: soho_game
-- ------------------------------------------------------
-- Server version	10.3.32-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `game_info`
--

DROP TABLE IF EXISTS `game_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `game_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL COMMENT '游戏名称',
  `title` varchar(255) DEFAULT NULL COMMENT '游戏标题',
  `logo` varchar(255) DEFAULT NULL COMMENT '游戏LOGO',
  `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `game_info`
--

LOCK TABLES `game_info` WRITE;
/*!40000 ALTER TABLE `game_info` DISABLE KEYS */;
INSERT INTO `game_info` VALUES (1,'test','test','test','2020-01-01 00:00:00','2020-01-01 00:00:00'),(4,'测试','测试',NULL,'2025-09-21 02:46:20','2025-09-21 02:46:20'),(5,'测试2','测试2',NULL,'2025-09-21 02:46:20','2025-09-21 02:46:20'),(6,'测试','测试',NULL,'2025-09-21 02:46:22','2025-09-21 02:46:22'),(7,'测试2','测试2',NULL,'2025-09-21 02:46:22','2025-09-21 02:46:22'),(8,'测试','测试',NULL,'2025-09-21 02:49:55','2025-09-21 02:49:55'),(9,'测试2','测试2',NULL,'2025-09-21 02:49:55','2025-09-21 02:49:55'),(10,'测试','测试',NULL,'2025-09-21 02:49:58','2025-09-21 02:49:58'),(11,'测试2','测试2',NULL,'2025-09-21 02:49:58','2025-09-21 02:49:58');
/*!40000 ALTER TABLE `game_info` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-01-06 16:18:44
