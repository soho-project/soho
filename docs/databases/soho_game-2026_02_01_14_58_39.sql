-- MySQL dump 10.13  Distrib 8.0.28, for Win64 (x86_64)
--
-- Host: 192.168.0.101    Database: soho_game
-- ------------------------------------------------------
-- Server version	5.5.5-10.3.32-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
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
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `game_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL COMMENT '娓告垙鍚嶇О',
  `title` varchar(255) DEFAULT NULL COMMENT '娓告垙鏍囬',
  `logo` varchar(255) DEFAULT NULL COMMENT '娓告垙LOGO',
  `updated_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `created_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `game_info`
--

LOCK TABLES `game_info` WRITE;
/*!40000 ALTER TABLE `game_info` DISABLE KEYS */;
INSERT INTO `game_info` VALUES (1,'snake','璐悆铔?,'https://igogo-test.oss-cn-shenzhen.aliyuncs.com/admin/avatar/2026_1_22_526849769146945536.png','2026-01-22 21:31:24','2020-01-01 00:00:00'),(4,'娴嬭瘯','娴嬭瘯',NULL,'2025-09-21 02:46:20','2025-09-21 02:46:20');
/*!40000 ALTER TABLE `game_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `game_snake_player_profile`
--

DROP TABLE IF EXISTS `game_snake_player_profile`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `game_snake_player_profile` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '鐜╁ID',
  `revive_cards` int(11) NOT NULL DEFAULT 0 COMMENT '澶嶆椿鍗℃暟閲?,
  `updated_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `created_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_snake_player_profile_player_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COMMENT='璐悆铔囩敤鎴峰睘鎬?;apis:admin/admin_id.details/list/batch/create/update/delete/option/exportExcel/importExcel';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `game_snake_player_profile`
--

LOCK TABLES `game_snake_player_profile` WRITE;
/*!40000 ALTER TABLE `game_snake_player_profile` DISABLE KEYS */;
INSERT INTO `game_snake_player_profile` VALUES (2,2,0,'2015-01-01 00:00:00','2015-01-01 00:00:00'),(3,1,0,'2026-01-22 19:27:37','2026-01-22 19:27:37'),(4,18,0,'2026-01-23 23:21:57','2026-01-23 23:21:57'),(5,21,0,'2026-01-24 22:36:51','2026-01-24 22:36:51');
/*!40000 ALTER TABLE `game_snake_player_profile` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `game_word_match_battle`
--

DROP TABLE IF EXISTS `game_word_match_battle`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `game_word_match_battle` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `room_id` varchar(64) NOT NULL,
  `mode` varchar(32) DEFAULT NULL,
  `status` varchar(32) DEFAULT NULL,
  `winner_id` varchar(64) DEFAULT NULL,
  `end_reason` varchar(64) DEFAULT NULL,
  `scores_json` longtext DEFAULT NULL,
  `started_at` datetime DEFAULT NULL,
  `ended_at` datetime DEFAULT NULL,
  `updated_time` datetime DEFAULT NULL,
  `created_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_room_id` (`room_id`),
  KEY `idx_status` (`status`),
  KEY `idx_started_at` (`started_at`)
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `game_word_match_battle`
--

LOCK TABLES `game_word_match_battle` WRITE;
/*!40000 ALTER TABLE `game_word_match_battle` DISABLE KEYS */;
INSERT INTO `game_word_match_battle` VALUES (1,'0d24abdc-46ce-438f-b502-ab2e19799e10','SOLO','RUNNING',NULL,NULL,NULL,'2026-01-29 21:00:39',NULL,'2026-01-29 21:00:38','2026-01-29 21:00:38'),(2,'b9b044f8-a77b-44bf-9631-5235c67ed121','SOLO','RUNNING',NULL,NULL,NULL,'2026-01-29 21:02:33',NULL,'2026-01-29 21:02:33','2026-01-29 21:02:33'),(3,'53b71695-74e0-4b8f-9601-48379241c57a','SOLO','RUNNING',NULL,NULL,NULL,'2026-01-29 21:12:59',NULL,'2026-01-29 21:12:59','2026-01-29 21:12:59'),(4,'4c5e79fc-0293-4b5a-a801-f07a4e6e0ee8','SOLO','RUNNING',NULL,NULL,NULL,'2026-01-29 21:16:32',NULL,'2026-01-29 21:16:32','2026-01-29 21:16:32'),(5,'ae33f454-32cd-43cc-a7d9-f23cf4bceb94','SOLO','RUNNING',NULL,NULL,NULL,'2026-01-29 21:34:10',NULL,'2026-01-29 21:34:10','2026-01-29 21:34:10'),(6,'5d46b19e-56c1-482a-ad7b-aeaac9c31135','SOLO','RUNNING',NULL,NULL,NULL,'2026-01-29 21:35:18',NULL,'2026-01-29 21:35:18','2026-01-29 21:35:18'),(7,'13619186-fdb4-4bfc-98d4-ad0ab4ce7690','SOLO','RUNNING',NULL,NULL,NULL,'2026-01-29 21:35:29',NULL,'2026-01-29 21:35:29','2026-01-29 21:35:29'),(8,'bb656990-c042-4dd5-a5b2-89bd03fe22aa','SOLO','RUNNING',NULL,NULL,NULL,'2026-01-29 21:36:01',NULL,'2026-01-29 21:36:00','2026-01-29 21:36:00'),(9,'2d4f4e5e-d1a9-4c42-a044-341905e81884','SOLO','RUNNING',NULL,NULL,NULL,'2026-01-29 21:43:19',NULL,'2026-01-29 21:43:19','2026-01-29 21:43:19'),(10,'0812c8ee-d3da-43d6-a0af-8bdcee028eee','SOLO','RUNNING',NULL,NULL,NULL,'2026-01-29 21:44:52',NULL,'2026-01-29 21:44:52','2026-01-29 21:44:52'),(11,'65ad618e-6cc4-47b6-80de-8a9ad7238004','SOLO','RUNNING',NULL,NULL,NULL,'2026-01-29 21:44:55',NULL,'2026-01-29 21:44:55','2026-01-29 21:44:55'),(12,'5d0179f6-12be-4fc8-b9c8-b471b33b9aca','SOLO','RUNNING',NULL,NULL,NULL,'2026-01-29 21:44:59',NULL,'2026-01-29 21:44:59','2026-01-29 21:44:59'),(13,'d7a0a64f-26bb-43ff-8934-b2b47d4fc586','SOLO','RUNNING',NULL,NULL,NULL,'2026-01-29 21:45:21',NULL,'2026-01-29 21:45:20','2026-01-29 21:45:20'),(14,'5b9b30e9-1fbd-4ba6-8b51-076882f8b820','SOLO','RUNNING',NULL,NULL,NULL,'2026-01-29 21:46:28',NULL,'2026-01-29 21:46:28','2026-01-29 21:46:28'),(15,'7e36d03f-4b28-4e7d-a4f1-ddab59b78039','SOLO','RUNNING',NULL,NULL,NULL,'2026-01-29 21:47:02',NULL,'2026-01-29 21:47:02','2026-01-29 21:47:02'),(16,'a8dfd3bd-8120-47fa-b0e9-b04e23deb3c6','SOLO','RUNNING',NULL,NULL,NULL,'2026-01-29 21:47:03',NULL,'2026-01-29 21:47:03','2026-01-29 21:47:03'),(17,'18e06d7a-e85f-4d88-a7b5-daafd46ad5f8','SOLO','RUNNING',NULL,NULL,NULL,'2026-01-29 21:47:43',NULL,'2026-01-29 21:47:43','2026-01-29 21:47:43'),(18,'b350d82a-c675-40c0-b99b-e265422911ab','SOLO','RUNNING',NULL,NULL,NULL,'2026-01-29 21:47:47',NULL,'2026-01-29 21:47:47','2026-01-29 21:47:47'),(19,'d79ea320-77f9-4426-a8f0-62377d001942','SOLO','RUNNING',NULL,NULL,NULL,'2026-01-29 21:47:51',NULL,'2026-01-29 21:47:51','2026-01-29 21:47:51'),(20,'d42a6da4-7f40-4d79-9c58-6712419b4e6f','SOLO','RUNNING',NULL,NULL,NULL,'2026-01-29 21:47:55',NULL,'2026-01-29 21:47:55','2026-01-29 21:47:55'),(21,'f6527e58-fec8-4e74-aca2-bd5eb252787b','SOLO','RUNNING',NULL,NULL,NULL,'2026-01-29 21:47:58',NULL,'2026-01-29 21:47:58','2026-01-29 21:47:58'),(22,'9ff56e9b-7e0c-4ecf-ab23-1aafc7dc9fff','SOLO','RUNNING',NULL,NULL,NULL,'2026-01-29 21:49:51',NULL,'2026-01-29 21:49:51','2026-01-29 21:49:51'),(23,'356e62a1-ba51-4e61-a3b0-077b7145d4e2','SOLO','RUNNING',NULL,NULL,NULL,'2026-01-29 21:57:03',NULL,'2026-01-29 21:57:02','2026-01-29 21:57:02'),(24,'7b736878-6828-4874-9149-9f41fdd735b4','SOLO','RUNNING',NULL,NULL,NULL,'2026-01-29 22:02:21',NULL,'2026-01-29 22:02:21','2026-01-29 22:02:21'),(25,'1e3fecad-3ae9-41d0-9896-2b59d1bef3f9','SOLO','RUNNING',NULL,NULL,NULL,'2026-01-29 22:02:45',NULL,'2026-01-29 22:02:42','2026-01-29 22:02:42'),(26,'c4377c78-2a5c-4bf1-9ceb-9ba6fa39fab5','SOLO','RUNNING',NULL,NULL,NULL,'2026-01-29 22:02:58',NULL,'2026-01-29 22:02:58','2026-01-29 22:02:58'),(27,'7ac50cdb-2616-4025-b4e6-22362b655a68','SOLO','RUNNING',NULL,NULL,NULL,'2026-01-29 22:04:38',NULL,'2026-01-29 22:04:38','2026-01-29 22:04:38'),(28,'7197a435-2540-425e-a734-8105442bfb8a','SOLO','RUNNING',NULL,NULL,NULL,'2026-01-29 22:05:42',NULL,'2026-01-29 22:05:42','2026-01-29 22:05:42'),(29,'bbd883df-92d5-4009-96c5-95274d1ac24e','SOLO','RUNNING',NULL,NULL,NULL,'2026-01-29 22:05:46',NULL,'2026-01-29 22:05:46','2026-01-29 22:05:46'),(30,'a16d7c07-9c14-4728-9129-e80b31a6ae07','SOLO','RUNNING',NULL,NULL,NULL,'2026-01-29 22:05:47',NULL,'2026-01-29 22:05:47','2026-01-29 22:05:47'),(31,'dfebc4b7-18f5-4918-af3b-932b1f04a319','SOLO','RUNNING',NULL,NULL,NULL,'2026-01-29 22:06:39',NULL,'2026-01-29 22:06:39','2026-01-29 22:06:39'),(32,'cf533888-4584-46b2-a73f-3f7c3cc4cd82','SOLO','RUNNING',NULL,NULL,NULL,'2026-01-29 22:06:50',NULL,'2026-01-29 22:06:49','2026-01-29 22:06:49'),(33,'7bd34515-8b0a-4ac2-a8db-7f0dba43c0ca','SOLO','RUNNING',NULL,NULL,NULL,'2026-01-29 22:07:00',NULL,'2026-01-29 22:06:59','2026-01-29 22:06:59'),(34,'605e6b90-5045-446d-935c-4655ee432bd1','SOLO','RUNNING',NULL,NULL,NULL,'2026-01-29 22:07:03',NULL,'2026-01-29 22:07:03','2026-01-29 22:07:03'),(35,'8be7086f-23a8-4f61-9455-a2c8a77cbbdb','SOLO','RUNNING',NULL,NULL,NULL,'2026-01-29 22:07:07',NULL,'2026-01-29 22:07:07','2026-01-29 22:07:07'),(36,'b5028498-1f56-42fb-9c9c-2ceea31e47d1','SOLO','RUNNING',NULL,NULL,NULL,'2026-01-29 22:07:10',NULL,'2026-01-29 22:07:10','2026-01-29 22:07:10'),(37,'713f231f-8f7d-4511-bca2-6323b4730d6f','SOLO','RUNNING',NULL,NULL,NULL,'2026-01-29 22:07:13',NULL,'2026-01-29 22:07:13','2026-01-29 22:07:13'),(38,'e1b4ecd7-b11e-4950-999e-e5b39d868318','SOLO','RUNNING',NULL,NULL,NULL,'2026-01-29 22:07:16',NULL,'2026-01-29 22:07:15','2026-01-29 22:07:15'),(39,'9146b416-ec34-45c3-9d99-ca659e09d711','SOLO','RUNNING',NULL,NULL,NULL,'2026-01-29 22:07:29',NULL,'2026-01-29 22:07:29','2026-01-29 22:07:29'),(40,'5615f244-f889-40e3-91cb-28fac8afb637','SOLO','RUNNING',NULL,NULL,NULL,'2026-01-29 22:12:06',NULL,'2026-01-29 22:12:05','2026-01-29 22:12:05'),(41,'3b52d007-d8bc-46d9-80da-63eb8969a137','SOLO','RUNNING',NULL,NULL,NULL,'2026-01-29 22:19:32',NULL,'2026-01-29 22:19:31','2026-01-29 22:19:31'),(42,'03058ae3-298c-41dd-a23a-8bec2e46b22e','SOLO','RUNNING',NULL,NULL,NULL,'2026-01-29 22:21:14',NULL,'2026-01-29 22:21:13','2026-01-29 22:21:13'),(43,'70359f15-22ca-4dda-a354-f9fbc177953b','SOLO','RUNNING',NULL,NULL,NULL,'2026-01-29 22:25:28',NULL,'2026-01-29 22:25:28','2026-01-29 22:25:28'),(44,'47228ffc-84cf-4579-b8c9-4a0de1a68c27','SOLO','RUNNING',NULL,NULL,NULL,'2026-01-29 22:27:43',NULL,'2026-01-29 22:27:42','2026-01-29 22:27:42'),(45,'cd440543-5f4d-4b64-91c9-87e89703f22f','SOLO','RUNNING',NULL,NULL,NULL,'2026-01-30 14:33:21',NULL,'2026-01-30 14:33:20','2026-01-30 14:33:20'),(46,'2036b882-1c46-48e7-89b4-991c9c841d15','SOLO','RUNNING',NULL,NULL,NULL,'2026-01-30 14:34:05',NULL,'2026-01-30 14:34:05','2026-01-30 14:34:05'),(47,'aa01e2b9-ab85-4f6b-8550-83c061671928','SOLO','RUNNING',NULL,NULL,NULL,'2026-01-30 14:39:00',NULL,'2026-01-30 14:39:00','2026-01-30 14:39:00'),(48,'ae5a81ad-e6ee-4620-9310-d53a8c333b0c','SOLO','RUNNING',NULL,NULL,NULL,'2026-01-30 14:53:34',NULL,'2026-01-30 14:53:34','2026-01-30 14:53:34'),(49,'2b248065-2d77-4597-8e55-92867bbb7461','SOLO','RUNNING',NULL,NULL,NULL,'2026-01-30 14:54:40',NULL,'2026-01-30 14:54:40','2026-01-30 14:54:40'),(50,'6deea511-be0c-45d4-a474-de7060bdc7ce','SOLO','RUNNING',NULL,NULL,NULL,'2026-01-30 15:09:12',NULL,'2026-01-30 15:09:12','2026-01-30 15:09:12');
/*!40000 ALTER TABLE `game_word_match_battle` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `game_word_match_battle_event`
--

DROP TABLE IF EXISTS `game_word_match_battle_event`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `game_word_match_battle_event` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `battle_id` bigint(20) DEFAULT NULL,
  `room_id` varchar(64) DEFAULT NULL,
  `seq` bigint(20) DEFAULT NULL,
  `type` varchar(64) DEFAULT NULL,
  `payload_json` longtext DEFAULT NULL,
  `created_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_battle_seq` (`battle_id`,`seq`),
  KEY `idx_room_id` (`room_id`)
) ENGINE=InnoDB AUTO_INCREMENT=229 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `game_word_match_battle_event`
--

LOCK TABLES `game_word_match_battle_event` WRITE;
/*!40000 ALTER TABLE `game_word_match_battle_event` DISABLE KEYS */;
:1,\"value\":null}],\"board\":[[\"R\",\"B\",\"N\",\"E\",\"L\",\"J\",\"I\",\"R\"],[\"N\",\"O\",\"Q\",\"O\",\"I\",\"V\",\"G\",\"G\"],[\"E\",\"A\",\"U\",\"I\",\"T\",\"F\",\"N\",\"U\"],[\"C\",\"E\",\"A\",\"C\",\"K\",\"E\",\"Z\",\"H\"],[\"T\",\"L\",\"Y\",\"L\",\"A\",\"R\",\"D\",\"Z\"],[\"S\",\"B\",\"C\",\"C\",\"C\",\"A\",\"E\",\"W\"],[\"H\",\"V\",\"T\",\"A\",\"U\",\"K\",\"V\",\"U\"],[\"C\",\"T\",\"H\",\"E\",\"H\",\"T\",\"G\",\"V\"]]}}}','2026-01-30 14:48:43'),(194,47,'aa01e2b9-ab85-4f6b-8550-83c061671928',4,'WORD_CLEAR','{\"request\":{\"roomId\":\"aa01e2b9-ab85-4f6b-8550-83c061671928\",\"playerId\":\"2\",\"word\":\"cake\",\"wordLevel\":\"P1\"},\"response\":{\"accepted\":true,\"scoreDelta\":14,\"combo\":3,\"message\":\"accepted\",\"boardDelta\":{\"filled\":[{\"x\":4,\"y\":2,\"value\":\"J\"},{\"x\":4,\"y\":1,\"value\":\"F\"},{\"x\":4,\"y\":0,\"value\":\"P\"},{\"x\":5,\"y\":0,\"value\":\"G\"}],\"cleared\":[{\"x\":4,\"y\":5,\"value\":null},{\"x\":4,\"y\":4,\"value\":null},{\"x\":4,\"y\":3,\"value\":null},{\"x\":5,\"y\":3,\"value\":null}],\"board\":[[\"R\",\"B\",\"N\",\"E\",\"P\",\"G\",\"I\",\"R\"],[\"N\",\"O\",\"Q\",\"O\",\"F\",\"J\",\"G\",\"G\"],[\"E\",\"A\",\"U\",\"I\",\"J\",\"V\",\"N\",\"U\"],[\"C\",\"E\",\"A\",\"C\",\"L\",\"F\",\"Z\",\"H\"],[\"T\",\"L\",\"Y\",\"L\",\"I\",\"R\",\"D\",\"Z\"],[\"S\",\"B\",\"C\",\"C\",\"T\",\"A\",\"E\",\"W\"],[\"H\",\"V\",\"T\",\"A\",\"U\",\"K\",\"V\",\"U\"],[\"C\",\"T\",\"H\",\"E\",\"H\",\"T\",\"G\",\"V\"]]}}}','2026-01-30 14:50:45'),(195,47,'aa01e2b9-ab85-4f6b-8550-83c061671928',5,'WORD_CLEAR','{\"request\":{\"roomId\":\"aa01e2b9-ab85-4f6b-8550-83c061671928\",\"playerId\":\"2\",\"word\":\"egg\",\"wordLevel\":\"P1\"},\"response\":{\"accepted\":false,\"scoreDelta\":0,\"combo\":0,\"message\":\"word not on board\",\"boardDelta\":null}}','2026-01-30 14:50:58'),(196,47,'aa01e2b9-ab85-4f6b-8550-83c061671928',6,'WORD_CLEAR','{\"request\":{\"roomId\":\"aa01e2b9-ab85-4f6b-8550-83c061671928\",\"playerId\":\"2\",\"word\":\"meat\",\"wordLevel\":\"P1\"},\"response\":{\"accepted\":false,\"scoreDelta\":0,\"combo\":0,\"message\":\"word not on board\",\"boardDelta\":null}}','2026-01-30 14:51:28'),(197,48,'ae5a81ad-e6ee-4620-9310-d53a8c333b0c',1,'MATCH_START','{\"roomId\":\"ae5a81ad-e6ee-4620-9310-d53a8c333b0c\",\"mode\":\"SOLO\",\"status\":\"RUNNING\",\"maxPlayers\":1,\"players\":[\"2\"],\"scores\":{\"2\":0}}','2026-01-30 14:53:34'),(198,49,'2b248065-2d77-4597-8e55-92867bbb7461',1,'MATCH_START','{\"roomId\":\"2b248065-2d77-4597-8e55-92867bbb7461\",\"mode\":\"SOLO\",\"status\":\"RUNNING\",\"maxPlayers\":1,\"players\":[\"2\"],\"scores\":{\"2\":0}}','2026-01-30 14:54:41'),(199,49,'2b248065-2d77-4597-8e55-92867bbb7461',2,'WORD_CLEAR','{\"request\":{\"roomId\":\"2b248065-2d77-4597-8e55-92867bbb7461\",\"playerId\":\"2\",\"word\":\"arm\",\"wordLevel\":\"P1\"},\"response\":{\"accepted\":true,\"scoreDelta\":8,\"combo\":1,\"message\":\"accepted\",\"boardDelta\":{\"filled\":[{\"x\":5,\"y\":0,\"value\":\"A\"},{\"x\":6,\"y\":1,\"value\":\"Y\"},{\"x\":6,\"y\":0,\"value\":\"Q\"}],\"cleared\":[{\"x\":5,\"y\":6,\"value\":null},{\"x\":6,\"y\":6,\"value\":null},{\"x\":6,\"y\":7,\"value\":null}],\"board\":[[\"Q\",\"U\",\"Z\",\"W\",\"N\",\"A\",\"Q\",\"F\"],[\"G\",\"Y\",\"T\",\"N\",\"B\",\"I\",\"Y\",\"O\"],[\"L\",\"F\",\"W\",\"E\",\"O\",\"H\",\"E\",\"C\"],[\"C\",\"S\",\"E\",\"Y\",\"X\",\"W\",\"L\",\"V\"],[\"U\",\"O\",\"X\",\"D\",\"G\",\"J\",\"J\",\"A\"],[\"P\",\"K\",\"H\",\"O\",\"S\",\"H\",\"O\",\"O\"],[\"A\",\"C\",\"N\",\"U\",\"Z\",\"K\",\"J\",\"A\"],[\"B\",\"L\",\"B\",\"V\",\"P\",\"F\",\"L\",\"D\"]]}}}','2026-01-30 14:55:05'),(200,49,'2b248065-2d77-4597-8e55-92867bbb7461',3,'WORD_CLEAR','{\"request\":{\"roomId\":\"2b248065-2d77-4597-8e55-92867bbb7461\",\"playerId\":\"2\",\"word\":\"eye\",\"wordLevel\":\"P1\"},\"response\":{\"accepted\":true,\"scoreDelta\":10,\"combo\":2,\"message\":\"accepted\",\"boardDelta\":{\"filled\":[{\"x\":2,\"y\":0,\"value\":\"C\"},{\"x\":3,\"y\":1,\"value\":\"J\"},{\"x\":3,\"y\":0,\"value\":\"F\"}],\"cleared\":[{\"x\":3,\"y\":2,\"value\":null},{\"x\":3,\"y\":3,\"value\":null},{\"x\":2,\"y\":3,\"value\":null}],\"board\":[[\"Q\",\"U\",\"C\",\"F\",\"N\",\"A\",\"Q\",\"F\"],[\"G\",\"Y\",\"Z\",\"J\",\"B\",\"I\",\"Y\",\"O\"],[\"L\",\"F\",\"T\",\"W\",\"O\",\"H\",\"E\",\"C\"],[\"C\",\"S\",\"W\",\"N\",\"X\",\"W\",\"L\",\"V\"],[\"U\",\"O\",\"X\",\"D\",\"G\",\"J\",\"J\",\"A\"],[\"P\",\"K\",\"H\",\"O\",\"S\",\"H\",\"O\",\"O\"],[\"A\",\"C\",\"N\",\"U\",\"Z\",\"K\",\"J\",\"A\"],[\"B\",\"L\",\"B\",\"V\",\"P\",\"F\",\"L\",\"D\"]]}}}','2026-01-30 14:55:36'),(201,49,'2b248065-2d77-4597-8e55-92867bbb7461',4,'WORD_CLEAR','{\"request\":{\"roomId\":\"2b248065-2d77-4597-8e55-92867bbb7461\",\"playerId\":\"2\",\"word\":\"ear\",\"wordLevel\":\"P1\"},\"response\":{\"accepted\":false,\"scoreDelta\":0,\"combo\":0,\"message\":\"word not on board\",\"boardDelta\":null}}','2026-01-30 14:57:28'),(202,49,'2b248065-2d77-4597-8e55-92867bbb7461',5,'WORD_CLEAR','{\"request\":{\"roomId\":\"2b248065-2d77-4597-8e55-92867bbb7461\",\"playerId\":\"2\",\"word\":\"nose\",\"wordLevel\":\"P1\"},\"response\":{\"accepted\":false,\"scoreDelta\":0,\"combo\":0,\"message\":\"word not on board\",\"boardDelta\":null}}','2026-01-30 14:57:34'),(203,49,'2b248065-2d77-4597-8e55-92867bbb7461',6,'WORD_CLEAR','{\"request\":{\"roomId\":\"2b248065-2d77-4597-8e55-92867bbb7461\",\"playerId\":\"2\",\"word\":\"leg\",\"wordLevel\":\"P1\"},\"response\":{\"accepted\":false,\"scoreDelta\":0,\"combo\":0,\"message\":\"word not on board\",\"boardDelta\":null}}','2026-01-30 14:57:39'),(204,49,'2b248065-2d77-4597-8e55-92867bbb7461',7,'WORD_CLEAR','{\"request\":{\"roomId\":\"2b248065-2d77-4597-8e55-92867bbb7461\",\"playerId\":\"2\",\"word\":\"head\",\"wordLevel\":\"P1\"},\"response\":{\"accepted\":false,\"scoreDelta\":0,\"combo\":0,\"message\":\"word not on board\",\"boardDelta\":null}}','2026-01-30 14:57:48'),(205,49,'2b248065-2d77-4597-8e55-92867bbb7461',8,'WORD_CLEAR','{\"request\":{\"roomId\":\"2b248065-2d77-4597-8e55-92867bbb7461\",\"playerId\":\"2\",\"word\":\"back\",\"wordLevel\":\"P1\"},\"response\":{\"accepted\":true,\"scoreDelta\":10,\"combo\":1,\"message\":\"accepted\",\"boardDelta\":{\"filled\":[{\"x\":0,\"y\":1,\"value\":\"V\"},{\"x\":0,\"y\":0,\"value\":\"F\"},{\"x\":1,\"y\":1,\"value\":\"N\"},{\"x\":1,\"y\":0,\"value\":\"P\"}],\"cleared\":[{\"x\":0,\"y\":7,\"value\":null},{\"x\":0,\"y\":6,\"value\":null},{\"x\":1,\"y\":6,\"value\":null},{\"x\":1,\"y\":5,\"value\":null}],\"board\":[[\"F\",\"P\",\"C\",\"F\",\"N\",\"A\",\"Q\",\"F\"],[\"V\",\"N\",\"Z\",\"J\",\"B\",\"I\",\"Y\",\"O\"],[\"Q\",\"U\",\"T\",\"W\",\"O\",\"H\",\"E\",\"C\"],[\"G\",\"Y\",\"W\",\"N\",\"X\",\"W\",\"L\",\"V\"],[\"L\",\"F\",\"X\",\"D\",\"G\",\"J\",\"J\",\"A\"],[\"C\",\"S\",\"H\",\"O\",\"S\",\"H\",\"O\",\"O\"],[\"U\",\"O\",\"N\",\"U\",\"Z\",\"K\",\"J\",\"A\"],[\"P\",\"L\",\"B\",\"V\",\"P\",\"F\",\"L\",\"D\"]]}}}','2026-01-30 14:57:52'),(206,49,'2b248065-2d77-4597-8e55-92867bbb7461',9,'WORD_CLEAR','{\"request\":{\"roomId\":\"2b248065-2d77-4597-8e55-92867bbb7461\",\"playerId\":\"2\",\"word\":\"adventure\",\"wordLevel\":\"P2\"},\"response\":{\"accepted\":false,\"scoreDelta\":0,\"combo\":0,\"message\":\"word not on board\",\"boardDelta\":null}}','2026-01-30 14:58:52'),(207,49,'2b248065-2d77-4597-8e55-92867bbb7461',10,'WORD_CLEAR','{\"request\":{\"roomId\":\"2b248065-2d77-4597-8e55-92867bbb7461\",\"playerId\":\"2\",\"word\":\"balance\",\"wordLevel\":\"P2\"},\"response\":{\"accepted\":false,\"scoreDelta\":0,\"combo\":0,\"message\":\"word not on board\",\"boardDelta\":null}}','2026-01-30 14:58:58'),(208,49,'2b248065-2d77-4597-8e55-92867bbb7461',11,'WORD_CLEAR','{\"request\":{\"roomId\":\"2b248065-2d77-4597-8e55-92867bbb7461\",\"playerId\":\"2\",\"word\":\"challenge\",\"wordLevel\":\"P2\"},\"response\":{\"accepted\":false,\"scoreDelta\":0,\"combo\":0,\"message\":\"word not on board\",\"boardDelta\":null}}','2026-01-30 14:59:03'),(209,49,'2b248065-2d77-4597-8e55-92867bbb7461',12,'WORD_CLEAR','{\"request\":{\"roomId\":\"2b248065-2d77-4597-8e55-92867bbb7461\",\"playerId\":\"2\",\"word\":\"distance\",\"wordLevel\":\"P2\"},\"response\":{\"accepted\":false,\"scoreDelta\":0,\"combo\":0,\"message\":\"word not on board\",\"boardDelta\":null}}','2026-01-30 14:59:09'),(210,49,'2b248065-2d77-4597-8e55-92867bbb7461',13,'WORD_CLEAR','{\"request\":{\"roomId\":\"2b248065-2d77-4597-8e55-92867bbb7461\",\"playerId\":\"2\",\"word\":\"excellent\",\"wordLevel\":\"P2\"},\"response\":{\"accepted\":false,\"scoreDelta\":0,\"combo\":0,\"message\":\"word not on board\",\"boardDelta\":null}}','2026-01-30 14:59:59'),(211,49,'2b248065-2d77-4597-8e55-92867bbb7461',14,'WORD_CLEAR','{\"request\":{\"roomId\":\"2b248065-2d77-4597-8e55-92867bbb7461\",\"playerId\":\"2\",\"word\":\"favourite\",\"wordLevel\":\"P2\"},\"response\":{\"accepted\":false,\"scoreDelta\":0,\"combo\":0,\"message\":\"word not on board\",\"boardDelta\":null}}','2026-01-30 15:00:04'),(212,49,'2b248065-2d77-4597-8e55-92867bbb7461',15,'WORD_CLEAR','{\"request\":{\"roomId\":\"2b248065-2d77-4597-8e55-92867bbb7461\",\"playerId\":\"2\",\"word\":\"government\",\"wordLevel\":\"P2\"},\"response\":{\"accepted\":false,\"scoreDelta\":0,\"combo\":0,\"message\":\"word not on board\",\"boardDelta\":null}}','2026-01-30 15:00:09'),(213,49,'2b248065-2d77-4597-8e55-92867bbb7461',16,'WORD_CLEAR','{\"request\":{\"roomId\":\"2b248065-2d77-4597-8e55-92867bbb7461\",\"playerId\":\"2\",\"word\":\"hospital\",\"wordLevel\":\"P2\"},\"response\":{\"accepted\":false,\"scoreDelta\":0,\"combo\":0,\"message\":\"word not on board\",\"boardDelta\":null}}','2026-01-30 15:00:17'),(214,49,'2b248065-2d77-4597-8e55-92867bbb7461',17,'WORD_CLEAR','{\"request\":{\"roomId\":\"2b248065-2d77-4597-8e55-92867bbb7461\",\"playerId\":\"2\",\"word\":\"hospital\",\"wordLevel\":\"P2\"},\"response\":{\"accepted\":false,\"scoreDelta\":0,\"combo\":0,\"message\":\"word not on board\",\"boardDelta\":null}}','2026-01-30 15:00:23'),(215,49,'2b248065-2d77-4597-8e55-92867bbb7461',18,'WORD_CLEAR','{\"request\":{\"roomId\":\"2b248065-2d77-4597-8e55-92867bbb7461\",\"playerId\":\"2\",\"word\":\"imagine\",\"wordLevel\":\"P2\"},\"response\":{\"accepted\":false,\"scoreDelta\":0,\"combo\":0,\"message\":\"word not on board\",\"boardDelta\":null}}','2026-01-30 15:00:25'),(216,49,'2b248065-2d77-4597-8e55-92867bbb7461',19,'WORD_CLEAR','{\"request\":{\"roomId\":\"2b248065-2d77-4597-8e55-92867bbb7461\",\"playerId\":\"2\",\"word\":\"knowledge\",\"wordLevel\":\"P2\"},\"response\":{\"accepted\":false,\"scoreDelta\":0,\"combo\":0,\"message\":\"word not on board\",\"boardDelta\":null}}','2026-01-30 15:00:29'),(217,49,'2b248065-2d77-4597-8e55-92867bbb7461',20,'WORD_CLEAR','{\"request\":{\"roomId\":\"2b248065-2d77-4597-8e55-92867bbb7461\",\"playerId\":\"2\",\"word\":\"language\",\"wordLevel\":\"P2\"},\"response\":{\"accepted\":false,\"scoreDelta\":0,\"combo\":0,\"message\":\"word not on board\",\"boardDelta\":null}}','2026-01-30 15:00:40'),(218,49,'2b248065-2d77-4597-8e55-92867bbb7461',21,'WORD_CLEAR','{\"request\":{\"roomId\":\"2b248065-2d77-4597-8e55-92867bbb7461\",\"playerId\":\"2\",\"word\":\"mountain\",\"wordLevel\":\"P2\"},\"response\":{\"accepted\":false,\"scoreDelta\":0,\"combo\":0,\"message\":\"word not on board\",\"boardDelta\":null}}','2026-01-30 15:00:48'),(219,49,'2b248065-2d77-4597-8e55-92867bbb7461',22,'WORD_CLEAR','{\"request\":{\"roomId\":\"2b248065-2d77-4597-8e55-92867bbb7461\",\"playerId\":\"2\",\"word\":\"notebook\",\"wordLevel\":\"P2\"},\"response\":{\"accepted\":false,\"scoreDelta\":0,\"combo\":0,\"message\":\"word not on board\",\"boardDelta\":null}}','2026-01-30 15:00:52'),(220,49,'2b248065-2d77-4597-8e55-92867bbb7461',23,'WORD_CLEAR','{\"request\":{\"roomId\":\"2b248065-2d77-4597-8e55-92867bbb7461\",\"playerId\":\"2\",\"word\":\"opposite\",\"wordLevel\":\"P2\"},\"response\":{\"accepted\":false,\"scoreDelta\":0,\"combo\":0,\"message\":\"word not on board\",\"boardDelta\":null}}','2026-01-30 15:00:56'),(221,49,'2b248065-2d77-4597-8e55-92867bbb7461',24,'WORD_CLEAR','{\"request\":{\"roomId\":\"2b248065-2d77-4597-8e55-92867bbb7461\",\"playerId\":\"2\",\"word\":\"opposite\",\"wordLevel\":\"P2\"},\"response\":{\"accepted\":false,\"scoreDelta\":0,\"combo\":0,\"message\":\"word not on board\",\"boardDelta\":null}}','2026-01-30 15:01:14'),(222,49,'2b248065-2d77-4597-8e55-92867bbb7461',25,'WORD_CLEAR','{\"request\":{\"roomId\":\"2b248065-2d77-4597-8e55-92867bbb7461\",\"playerId\":\"2\",\"word\":\"possible\",\"wordLevel\":\"P2\"},\"response\":{\"accepted\":false,\"scoreDelta\":0,\"combo\":0,\"message\":\"word not on board\",\"boardDelta\":null}}','2026-01-30 15:01:19'),(223,49,'2b248065-2d77-4597-8e55-92867bbb7461',26,'WORD_CLEAR','{\"request\":{\"roomId\":\"2b248065-2d77-4597-8e55-92867bbb7461\",\"playerId\":\"2\",\"word\":\"question\",\"wordLevel\":\"P2\"},\"response\":{\"accepted\":false,\"scoreDelta\":0,\"combo\":0,\"message\":\"word not on board\",\"boardDelta\":null}}','2026-01-30 15:01:23'),(224,49,'2b248065-2d77-4597-8e55-92867bbb7461',27,'WORD_CLEAR','{\"request\":{\"roomId\":\"2b248065-2d77-4597-8e55-92867bbb7461\",\"playerId\":\"2\",\"word\":\"remember\",\"wordLevel\":\"P2\"},\"response\":{\"accepted\":false,\"scoreDelta\":0,\"combo\":0,\"message\":\"word not on board\",\"boardDelta\":null}}','2026-01-30 15:01:29'),(225,49,'2b248065-2d77-4597-8e55-92867bbb7461',28,'WORD_CLEAR','{\"request\":{\"roomId\":\"2b248065-2d77-4597-8e55-92867bbb7461\",\"playerId\":\"2\",\"word\":\"standard\",\"wordLevel\":\"P2\"},\"response\":{\"accepted\":false,\"scoreDelta\":0,\"combo\":0,\"message\":\"word not on board\",\"boardDelta\":null}}','2026-01-30 15:01:32'),(226,49,'2b248065-2d77-4597-8e55-92867bbb7461',29,'WORD_CLEAR','{\"request\":{\"roomId\":\"2b248065-2d77-4597-8e55-92867bbb7461\",\"playerId\":\"2\",\"word\":\"together\",\"wordLevel\":\"P2\"},\"response\":{\"accepted\":false,\"scoreDelta\":0,\"combo\":0,\"message\":\"word not on board\",\"boardDelta\":null}}','2026-01-30 15:01:35'),(227,49,'2b248065-2d77-4597-8e55-92867bbb7461',30,'WORD_CLEAR','{\"request\":{\"roomId\":\"2b248065-2d77-4597-8e55-92867bbb7461\",\"playerId\":\"2\",\"word\":\"understand\",\"wordLevel\":\"P2\"},\"response\":{\"accepted\":false,\"scoreDelta\":0,\"combo\":0,\"message\":\"word not on board\",\"boardDelta\":null}}','2026-01-30 15:01:39'),(228,50,'6deea511-be0c-45d4-a474-de7060bdc7ce',1,'MATCH_START','{\"roomId\":\"6deea511-be0c-45d4-a474-de7060bdc7ce\",\"mode\":\"SOLO\",\"status\":\"RUNNING\",\"maxPlayers\":1,\"players\":[\"2\"],\"scores\":{\"2\":0}}','2026-01-30 15:09:12');
/*!40000 ALTER TABLE `game_word_match_battle_event` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `game_word_match_rank_profile`
--

DROP TABLE IF EXISTS `game_word_match_rank_profile`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `game_word_match_rank_profile` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `player_id` varchar(64) NOT NULL,
  `rank_score` int(11) DEFAULT NULL,
  `wins` int(11) DEFAULT NULL,
  `losses` int(11) DEFAULT NULL,
  `updated_time` datetime DEFAULT NULL,
  `created_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_player_id` (`player_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `game_word_match_rank_profile`
--

LOCK TABLES `game_word_match_rank_profile` WRITE;
/*!40000 ALTER TABLE `game_word_match_rank_profile` DISABLE KEYS */;
/*!40000 ALTER TABLE `game_word_match_rank_profile` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `game_word_match_word`
--

DROP TABLE IF EXISTS `game_word_match_word`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `game_word_match_word` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `word` varchar(128) NOT NULL,
  `meaning` varchar(512) DEFAULT NULL,
  `level` varchar(32) DEFAULT NULL,
  `freq` double DEFAULT NULL,
  `updated_time` datetime DEFAULT NULL,
  `created_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_word_level` (`word`,`level`),
  KEY `idx_level` (`level`)
) ENGINE=InnoDB AUTO_INCREMENT=551 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `game_word_match_word`
--

LOCK TABLES `game_word_match_word` WRITE;
/*!40000 ALTER TABLE `game_word_match_word` DISABLE KEYS */;
INSERT INTO `game_word_match_word` VALUES (501,'arm','鎵嬭噦','P1',0.9,'2026-01-28 23:37:34','2026-01-28 23:37:34'),(502,'leg','鑵?,'P1',0.8999,'2026-01-28 23:37:34','2026-01-28 23:37:34'),(503,'nose','榧诲瓙','P1',0.8998,'2026-01-28 23:37:34','2026-01-28 23:37:34'),(504,'ear','鑰虫湹','P1',0.8997,'2026-01-28 23:37:34','2026-01-28 23:37:34'),(505,'eye','鐪肩潧','P1',0.8996,'2026-01-28 23:37:34','2026-01-28 23:37:34'),(506,'head','澶?,'P1',0.8995,'2026-01-28 23:37:34','2026-01-28 23:37:34'),(507,'back','鑳岄儴','P1',0.8994,'2026-01-28 23:37:34','2026-01-28 23:37:34'),(508,'desk','涔︽','P1',0.8993,'2026-01-28 23:37:34','2026-01-28 23:37:34'),(509,'box','鐩掑瓙','P1',0.8992,'2026-01-28 23:37:34','2026-01-28 23:37:34'),(510,'bag','鍖?,'P1',0.8991,'2026-01-28 23:37:34','2026-01-28 23:37:34'),(511,'hat','甯藉瓙','P1',0.899,'2026-01-28 23:37:34','2026-01-28 23:37:34'),(512,'coat','澶栧','P1',0.8989,'2026-01-28 23:37:34','2026-01-28 23:37:34'),(513,'cup','鏉瓙','P1',0.8988,'2026-01-28 23:37:34','2026-01-28 23:37:34'),(514,'egg','楦¤泲','P1',0.8987,'2026-01-28 23:37:34','2026-01-28 23:37:34'),(515,'cake','铔嬬硶','P1',0.8986,'2026-01-28 23:37:34','2026-01-28 23:37:34'),(516,'meat','鑲?,'P1',0.8985,'2026-01-28 23:37:34','2026-01-28 23:37:34'),(517,'rice','绫抽キ','P1',0.8984,'2026-01-28 23:37:34','2026-01-28 23:37:34'),(518,'pear','姊?,'P1',0.8983,'2026-01-28 23:37:34','2026-01-28 23:37:34'),(519,'banana','棣欒晧','P1',0.8982,'2026-01-28 23:37:34','2026-01-28 23:37:34'),(520,'grape','钁¤悇','P1',0.8981,'2026-01-28 23:37:34','2026-01-28 23:37:34'),(521,'adventure','鍐掗櫓','P2',0.85,'2026-01-28 23:37:34','2026-01-28 23:37:34'),(522,'balance','骞宠　','P2',0.8499,'2026-01-28 23:37:34','2026-01-28 23:37:34'),(523,'challenge','鎸戞垬','P2',0.8498,'2026-01-28 23:37:34','2026-01-28 23:37:34'),(524,'distance','璺濈','P2',0.8497,'2026-01-28 23:37:34','2026-01-28 23:37:34'),(525,'excellent','浼樼鐨?,'P2',0.8496,'2026-01-28 23:37:34','2026-01-28 23:37:34'),(526,'favourite','鏈€鍠滅埍鐨?,'P2',0.8495,'2026-01-28 23:37:34','2026-01-28 23:37:34'),(527,'government','鏀垮簻','P2',0.8494,'2026-01-28 23:37:34','2026-01-28 23:37:34'),(528,'hospital','鍖婚櫌','P2',0.8493,'2026-01-28 23:37:34','2026-01-28 23:37:34'),(529,'imagine','鎯宠薄','P2',0.8492,'2026-01-28 23:37:34','2026-01-28 23:37:34'),(530,'knowledge','鐭ヨ瘑','P2',0.8491,'2026-01-28 23:37:34','2026-01-28 23:37:34'),(531,'language','璇█','P2',0.849,'2026-01-28 23:37:34','2026-01-28 23:37:34'),(532,'mountain','灞辫剦','P2',0.8489,'2026-01-28 23:37:34','2026-01-28 23:37:34'),(533,'notebook','绗旇鏈?,'P2',0.8488,'2026-01-28 23:37:34','2026-01-28 23:37:34'),(534,'opposite','鐩稿弽鐨?,'P2',0.8487,'2026-01-28 23:37:34','2026-01-28 23:37:34'),(535,'possible','鍙兘鐨?,'P2',0.8486,'2026-01-28 23:37:34','2026-01-28 23:37:34'),(536,'question','闂','P2',0.8485,'2026-01-28 23:37:34','2026-01-28 23:37:34'),(537,'remember','璁板緱','P2',0.8484,'2026-01-28 23:37:34','2026-01-28 23:37:34'),(538,'standard','鏍囧噯鐨?,'P2',0.8483,'2026-01-28 23:37:34','2026-01-28 23:37:34'),(539,'together','涓€璧?,'P2',0.8482,'2026-01-28 23:37:34','2026-01-28 23:37:34'),(540,'understand','鐞嗚В','P2',0.8481,'2026-01-28 23:37:34','2026-01-28 23:37:34'),(541,'vacation','鍋囨湡','P2',0.848,'2026-01-28 23:37:34','2026-01-28 23:37:34'),(542,'wonderful','绮惧僵鐨?,'P2',0.8479,'2026-01-28 23:37:34','2026-01-28 23:37:34'),(543,'yesterday','鏄ㄥぉ','P2',0.8478,'2026-01-28 23:37:34','2026-01-28 23:37:34'),(544,'ability','鑳藉姏','P2',0.8477,'2026-01-28 23:37:34','2026-01-28 23:37:34'),(545,'believe','鐩镐俊','P2',0.8476,'2026-01-28 23:37:34','2026-01-28 23:37:34'),(546,'calendar','鏃ュ巻','P2',0.8475,'2026-01-28 23:37:34','2026-01-28 23:37:34'),(547,'daughter','濂冲効','P2',0.8474,'2026-01-28 23:37:34','2026-01-28 23:37:34'),(548,'education','鏁欒偛','P2',0.8473,'2026-01-28 23:37:34','2026-01-28 23:37:34'),(549,'festival','鑺傛棩','P2',0.8472,'2026-01-28 23:37:34','2026-01-28 23:37:34'),(550,'graduate','姣曚笟','P2',0.8471,'2026-01-28 23:37:34','2026-01-28 23:37:34');
/*!40000 ALTER TABLE `game_word_match_word` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-02-01 14:58:57

