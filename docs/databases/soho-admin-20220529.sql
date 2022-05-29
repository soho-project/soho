-- MySQL dump 10.13  Distrib 8.0.28, for Win64 (x86_64)
--
-- Host: 192.168.0.101    Database: dev
-- ------------------------------------------------------
-- Server version	5.5.68-MariaDB

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
-- Table structure for table `admin_config`
--

DROP TABLE IF EXISTS `admin_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admin_config` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `group_key` varchar(200) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '配置文件分组名',
  `key` varchar(200) DEFAULT NULL COMMENT '配置信息唯一识别key',
  `value` text COMMENT '配置信息值',
  `explain` varchar(500) DEFAULT NULL COMMENT '说明',
  `type` tinyint(4) DEFAULT NULL COMMENT '配置信息类型',
  `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `key-unique` (`key`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8 COMMENT='系统配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin_config`
--

LOCK TABLES `admin_config` WRITE;
/*!40000 ALTER TABLE `admin_config` DISABLE KEYS */;
INSERT INTO `admin_config` VALUES (1,'public','login_use_captcha','false','后台登录是否开启验证码',1,'2022-04-22 21:53:32','2020-01-01 00:00:00'),(2,'public','key2','c','这是页面测试用的',1,'2020-01-01 00:00:00','2020-01-01 00:00:00'),(14,'public','key14ff','test','这是页面测试用的',1,'2020-01-01 00:00:00','2020-01-01 00:00:00');
/*!40000 ALTER TABLE `admin_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `admin_config_group`
--

DROP TABLE IF EXISTS `admin_config_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admin_config_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `key` varchar(500) NOT NULL COMMENT '组主键',
  `name` varchar(500) DEFAULT NULL COMMENT '组名',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='配置分组表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin_config_group`
--

LOCK TABLES `admin_config_group` WRITE;
/*!40000 ALTER TABLE `admin_config_group` DISABLE KEYS */;
INSERT INTO `admin_config_group` VALUES (1,'public','公共配置','2020-01-01 00:00:00'),(2,'admin','管理后台','2020-01-01 00:00:00');
/*!40000 ALTER TABLE `admin_config_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `admin_notification`
--

DROP TABLE IF EXISTS `admin_notification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admin_notification` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `admin_user_id` int(11) DEFAULT NULL COMMENT '接收人',
  `title` varchar(500) DEFAULT NULL COMMENT '标题',
  `create_admin_user_id` int(11) NOT NULL COMMENT '创建者 0 为系统发送',
  `content` text COMMENT '通知内容',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `is_read` tinyint(4) DEFAULT '0' COMMENT '是否已读 0 未读 1 已读',
  PRIMARY KEY (`id`),
  KEY `index-user_id-is_read` (`admin_user_id`,`is_read`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8 COMMENT='管理员通知';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin_notification`
--

LOCK TABLES `admin_notification` WRITE;
/*!40000 ALTER TABLE `admin_notification` DISABLE KEYS */;
INSERT INTO `admin_notification` VALUES (3,1,'大是大非',2,'大是大非','2022-04-24 20:00:10',1),(4,1,'的说法大发',2,'的说法','2022-04-24 22:44:19',1),(5,1,'的说法大发',2,'的说法','2022-04-24 22:53:45',1),(8,1,'的点点滴滴多多多多',2,'的点点滴滴多多多','2022-04-24 23:45:21',1),(9,2,'的点点滴滴多多多多',2,'的点点滴滴多多多','2022-04-24 23:45:21',1),(10,1,'发根深蒂固',2,'sssssssssss','2022-04-25 00:50:09',1),(11,2,'发根深蒂固',2,'sssssssssss','2022-04-25 00:50:10',1),(12,1,'他吞吞吐吐',1,'顶顶顶顶顶','2022-04-30 23:33:57',1),(13,1,'test',2,'ttttttt','2022-05-04 00:26:45',1),(14,2,'ttttttttttt',2,'tttttttttttttttttt','2022-05-04 00:27:06',1),(15,2,'ttttttttt',2,'ttttttttttttttttttt','2022-05-04 00:27:15',0);
/*!40000 ALTER TABLE `admin_notification` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `admin_resource`
--

DROP TABLE IF EXISTS `admin_resource`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admin_resource` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(500) DEFAULT NULL COMMENT '英文名',
  `route` varchar(500) DEFAULT NULL COMMENT '路由',
  `type` tinyint(1) DEFAULT '1' COMMENT '资源类型',
  `remarks` varchar(500) DEFAULT NULL COMMENT '备注',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `visible` tinyint(4) DEFAULT '1' COMMENT '资源界面是否可见',
  `sort` int(11) DEFAULT '100' COMMENT '排序',
  `breadcrumb_parent_id` int(11) DEFAULT '1' COMMENT '父ID',
  `zh_name` varchar(500) DEFAULT NULL COMMENT '中文名',
  `icon_name` varchar(500) DEFAULT NULL COMMENT '菜单图标',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8 COMMENT='资源表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin_resource`
--

LOCK TABLES `admin_resource` WRITE;
/*!40000 ALTER TABLE `admin_resource` DISABLE KEYS */;
INSERT INTO `admin_resource` VALUES (1,'Admin','/',0,'Admin','2022-01-30 23:42:25',1,1,0,'后台管理','dashboard'),(2,'System Mange','/',1,'系统管理目录','2022-04-06 23:00:40',1,3,1,'系统管理','AppstoreOutlined'),(3,'Users','/user',1,'用户管理','2022-01-30 23:42:25',1,1,2,'用户管理','user'),(4,'Posts','/post',1,'文章管理','2022-01-30 23:42:25',1,100,2,'文章管理','shopping-cart'),(5,'User Detail','/user/:id',1,'用户详情','2022-01-30 23:42:25',0,100,2,'用户详情','user'),(6,'Request','/request',1,'接口模拟请求','2022-01-30 23:42:25',1,100,2,'接口模拟请求','api'),(7,'UI Element','/ui',0,'UI组件','2022-01-30 23:42:25',0,100,2,'UI组件','camera-o'),(8,'Editor','/editor',1,'编辑','2022-01-30 23:42:25',0,100,2,'编辑',NULL),(9,'Charts','/Charts',1,'图标','2022-01-30 23:42:25',0,100,1,'图表','area-chart'),(10,'ECharts','/chart/ECharts',1,'ECharts','2022-01-30 23:42:25',0,100,9,'ECharts','area-chart'),(11,'HighCharts','/chart/highCharts',1,'/chart/highCharts','2022-01-30 23:42:25',0,100,9,'Rechartst','area-chart'),(12,'Rechartst','Rechartst',1,'/chart/Recharts','2022-01-30 23:42:25',0,100,1,'Rechartst','area-chart'),(13,'Role','/role',1,'角色管理','2022-01-30 23:42:25',1,1,2,'角色管理','user'),(14,'Resource','/resource',1,'资源管理','2022-01-30 23:42:25',1,1,2,'资源管理','api'),(15,'tesaaccc','/test',0,'testaa','2022-04-05 16:31:51',0,1,1,'eeeeeee','testwerq'),(17,'t2','/t2',0,NULL,'2022-04-05 17:53:50',0,1,15,'taddfasd','ddd'),(18,'Config','/config',1,'系统配置','2022-04-06 00:06:26',1,20,2,'系统配置','SettingOutlined'),(19,'Dashboard','/dashboard',1,'首页','2022-01-30 23:42:25',1,0,1,'仪表盘','dashboard'),(21,'notifications','/admin_notification',1,'系统通知','2022-04-24 02:36:48',1,20,2,'系统通知','MessageOutlined'),(22,'Approval Process Manger','/approval_process',1,'审批流管理','2022-05-10 15:16:20',1,500,1,'审批流管理','user'),(23,'Approval Process List','/approval_process',1,'审批流列表','2022-05-10 15:17:47',1,500,22,'审批流管理','user'),(24,'My Approval','/approval_process_myorder',1,'我的审批','2022-05-23 21:29:04',1,1,22,'我的审批','user'),(25,'Approval Order','/approval_process_order',1,'审批单管理','2022-05-23 21:29:04',1,1,22,'审批单管理','user');
/*!40000 ALTER TABLE `admin_resource` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `admin_role`
--

DROP TABLE IF EXISTS `admin_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admin_role` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(45) DEFAULT NULL COMMENT '角色名',
  `remarks` varchar(255) DEFAULT NULL COMMENT '备注',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `enable` tinyint(4) DEFAULT '1' COMMENT '是否启用',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COMMENT='角色表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin_role`
--

LOCK TABLES `admin_role` WRITE;
/*!40000 ALTER TABLE `admin_role` DISABLE KEYS */;
INSERT INTO `admin_role` VALUES (1,'admin','管理员角色；该角色无需配置资源，默认所有资源权限','2022-01-29 12:03:07',1),(2,'developer','开发者角色；默认有所有权限， 线上不应该开启该角色','2022-01-29 12:03:07',1),(3,'角色3','String','2022-01-29 12:03:07',1),(4,'角色4aggg','String','2022-01-29 12:03:07',1),(5,'角色5','String','2022-01-29 12:03:07',1);
/*!40000 ALTER TABLE `admin_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `admin_role_resource`
--

DROP TABLE IF EXISTS `admin_role_resource`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admin_role_resource` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `role_id` int(10) unsigned DEFAULT NULL COMMENT '角色ID',
  `resource_id` int(10) unsigned DEFAULT NULL COMMENT '资源ID',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8 COMMENT='角色资源关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin_role_resource`
--

LOCK TABLES `admin_role_resource` WRITE;
/*!40000 ALTER TABLE `admin_role_resource` DISABLE KEYS */;
INSERT INTO `admin_role_resource` VALUES (21,5,6,'2022-04-03 22:00:53'),(22,5,4,'2022-04-03 22:00:53'),(23,1,2,'2022-04-23 00:06:07'),(24,1,9,'2022-04-23 00:06:07'),(25,1,15,'2022-04-24 01:08:47'),(26,1,12,'2022-04-24 01:08:47'),(27,1,19,'2022-04-24 01:08:47');
/*!40000 ALTER TABLE `admin_role_resource` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `admin_role_user`
--

DROP TABLE IF EXISTS `admin_role_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admin_role_user` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `role_id` int(10) unsigned DEFAULT NULL COMMENT '角色ID',
  `user_id` int(10) unsigned DEFAULT NULL COMMENT '用户ID',
  `status` tinyint(4) DEFAULT NULL COMMENT '状态',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8 COMMENT='角色用户关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin_role_user`
--

LOCK TABLES `admin_role_user` WRITE;
/*!40000 ALTER TABLE `admin_role_user` DISABLE KEYS */;
INSERT INTO `admin_role_user` VALUES (9,1,1,1,'2022-04-24 01:07:53'),(10,1,2,1,'2022-04-24 02:16:37');
/*!40000 ALTER TABLE `admin_role_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `admin_user`
--

DROP TABLE IF EXISTS `admin_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admin_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(128) NOT NULL COMMENT '用户名',
  `phone` varchar(11) NOT NULL COMMENT '手机号',
  `nick_name` varchar(45) DEFAULT NULL COMMENT '昵称',
  `real_name` varchar(45) DEFAULT NULL COMMENT '真实名称',
  `avatar` varchar(500) DEFAULT '//image.zuiidea.com/photo-1519336555923-59661f41bb45.jpeg?imageView2/1/w/200/h/200/format/webp/q/75|imageslim' COMMENT '头像',
  `email` varchar(255) DEFAULT NULL COMMENT '邮箱地址',
  `password` varchar(300) DEFAULT NULL COMMENT '用户密码',
  `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `sex` tinyint(4) DEFAULT NULL COMMENT '性别',
  `age` int(11) DEFAULT NULL COMMENT '年龄',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '软删除标记',
  PRIMARY KEY (`id`,`username`),
  UNIQUE KEY `unique-username` (`username`),
  UNIQUE KEY `unique-phone` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='系统用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin_user`
--

LOCK TABLES `admin_user` WRITE;
/*!40000 ALTER TABLE `admin_user` DISABLE KEYS */;
INSERT INTO `admin_user` VALUES (1,'admin','15873164073','adminNickNamaadd','fang.liu','https://igogo-test.oss-cn-shenzhen.aliyuncs.com/user/avatar/2022_4_7_190464236.jpg','i@liufang.org.cn','$2a$10$bPy9GxOynsydi64yULtvfee9zuoCFf0cb/VoXrhnrvoTa3wHOOLky','2022-01-23 17:32:56','2022-01-23 17:32:56',1,195,0),(2,'guest','15833333333','打发十aa',NULL,'https://igogo-test.oss-cn-shenzhen.aliyuncs.com/user/avatar/2022_4_23_30063941451255808.jpg','555i@liufang.org.cn','$2a$10$VeTSOwXxjfSDxcEKQgG9nuWVQRwkp3iS0DVDfB6xgbFHPIqi9dR7m','2022-03-27 20:49:48','2022-03-27 20:49:48',2,3,0);
/*!40000 ALTER TABLE `admin_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `admin_user_login_log`
--

DROP TABLE IF EXISTS `admin_user_login_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admin_user_login_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `admin_user_id` int(11) DEFAULT NULL COMMENT '后台用户ID',
  `client_ip` varchar(50) DEFAULT NULL COMMENT '客户端IP地址考虑IPv6字段适当放宽',
  `client_user_agent` varchar(1000) DEFAULT NULL COMMENT '客户端软件信息',
  `token` varchar(3000) DEFAULT NULL COMMENT '给用户发放的token',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8 COMMENT='用户登录日志';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin_user_login_log`
--

LOCK TABLES `admin_user_login_log` WRITE;
/*!40000 ALTER TABLE `admin_user_login_log` DISABLE KEYS */;
INSERT INTO `admin_user_login_log` VALUES (1,2,'127.0.0.1',NULL,'{\"exp\":\"1651957130539\",\"iat\":\"1650643130538\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOiIyIiwidW5hbWUiOiIxNTgzMzMzMzMzMyIsImV4cCI6MTY1MTk1NzEzMCwiaWF0IjoxNjUwNjQzMTMwfQ.aVewHPw_A81EDZoyJX_Kd-_O8XPyZEvRBTikwSvF5d8VsP4hV_8hNmqd5KJ4faBxLzzdyhi8txa6CUXaf3IFEw\"}','2022-04-22 23:58:50'),(2,2,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.60 Safari/537.36','{\"exp\":\"1651957392536\",\"iat\":\"1650643392536\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOiIyIiwidW5hbWUiOiIxNTgzMzMzMzMzMyIsImV4cCI6MTY1MTk1NzM5MiwiaWF0IjoxNjUwNjQzMzkyfQ.QBec_gRxAzpswCp_JeiW6odw0WAiYtmrDayhfnnaRWYCPxZTVaqRwkDZjP1tmZyzxJY7WOuO2nxuUZ-uiqSzag\"}','2022-04-23 00:03:12'),(3,2,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.60 Safari/537.36','{\"exp\":\"1652051723294\",\"iat\":\"1650737723294\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOiIyIiwidW5hbWUiOiIxNTgzMzMzMzMzMyIsImV4cCI6MTY1MjA1MTcyMywiaWF0IjoxNjUwNzM3NzIzfQ.1KtQf7ApZ7Nn06WS_AeZRSNfzvsBVMTz3nt3Ga3GSUe1POxU7vtK0LD2SlzCK8AW1vyL7CjpzT2ozuSX0yx5Rw\"}','2022-04-24 02:15:23'),(4,1,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.60 Safari/537.36','{\"exp\":\"1652051770033\",\"iat\":\"1650737770033\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOiIxIiwidW5hbWUiOiIxNTg3MzE2NDA3MyIsImV4cCI6MTY1MjA1MTc3MCwiaWF0IjoxNjUwNzM3NzcwfQ.wv3AvAaB2mBqDEA_bWkt10-ITRDPlzyEQEKvSKYtnC2k301NqOK0oYPCM9bhwEynh6L5J9AaK8JGUSbmxiC00Q\"}','2022-04-24 02:16:10'),(5,2,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.60 Safari/537.36','{\"exp\":\"1652051972079\",\"iat\":\"1650737972079\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOiIyIiwidW5hbWUiOiIxNTgzMzMzMzMzMyIsImV4cCI6MTY1MjA1MTk3MiwiaWF0IjoxNjUwNzM3OTcyfQ.k2916oXkE77rKnyy4_rkNtLQHddC60h09PW-ZBviFjtKf1krUKH1p1FvI49lvee1gTTcdsUzK-CRCBfcP3eK_Q\"}','2022-04-24 02:19:32'),(6,2,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.60 Safari/537.36','{\"exp\":\"1652053037705\",\"iat\":\"1650739037705\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOiIyIiwidW5hbWUiOiIxNTgzMzMzMzMzMyIsImV4cCI6MTY1MjA1MzAzNywiaWF0IjoxNjUwNzM5MDM3fQ.quVur9doelmCaVjxdDND2z1x9U1u4Ge-i6g_u0a7fqCiY3C356rvgiYo9n8TqGb0NGyWk953KopcGqOL00HT2w\"}','2022-04-24 02:37:17'),(7,2,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.60 Safari/537.36','{\"exp\":\"1652053123066\",\"iat\":\"1650739123066\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOiIyIiwidW5hbWUiOiIxNTgzMzMzMzMzMyIsImV4cCI6MTY1MjA1MzEyMywiaWF0IjoxNjUwNzM5MTIzfQ.tRURAjyVUtpIW58b7GidRu_SsC3JZCvID30fylsFPmOv_PrENX4PIOBu6rS-EBKpjYPdQ9kR5kmJ8WYRrlDAWg\"}','2022-04-24 02:38:43'),(8,2,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.60 Safari/537.36','{\"exp\":\"1652053325480\",\"iat\":\"1650739325480\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOiIyIiwidW5hbWUiOiIxNTgzMzMzMzMzMyIsImV4cCI6MTY1MjA1MzMyNSwiaWF0IjoxNjUwNzM5MzI1fQ.rXEdpDbLO1EOYgLq3V-H2ZzUaZaWxvnbi87sC1ZdlNjwmE8me0a70zBsqx8r74oJ7rIRoRwQGFeKHluYuFUOnw\"}','2022-04-24 02:42:05'),(9,1,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.60 Safari/537.36','{\"exp\":\"1652305486582\",\"iat\":\"1650991486582\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NTIzMDU0ODYsImlhdCI6MTY1MDk5MTQ4Nn0.fKhDHIwndIMMDuYttkWt6EAHYAAodoTNRWZu3Qyvif-uL3eSEhKx1XbI02qU_eWfsGdZi7EdbjdvuCJFPCGfYw\"}','2022-04-27 00:44:46'),(10,1,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.60 Safari/537.36','{\"exp\":\"1652305830226\",\"iat\":\"1650991830226\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NTIzMDU4MzAsImlhdCI6MTY1MDk5MTgzMH0.HIcJXqAumHIt3uCR_nPNGq3rHCMUK9OvBGL2eNC_ko0NhOZ2pk7qHdQNmD9Pt5LYWxethLGMKYrHXXNY88J1iA\"}','2022-04-27 00:50:30'),(11,1,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.60 Safari/537.36','{\"exp\":\"1652308891261\",\"iat\":\"1650994891261\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NTIzMDg4OTEsImlhdCI6MTY1MDk5NDg5MX0.K6uTqgujprLvopWg3Z_Ob_-gFVk5kzijFnUGgqIXU_-A_nRLSahVUbbpaHBGMcjKjJ8nL6S5jpIM9wKG9Lx0JQ\"}','2022-04-27 01:41:31'),(12,1,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.60 Safari/537.36','{\"exp\":\"1652309030481\",\"iat\":\"1650995030481\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NTIzMDkwMzAsImlhdCI6MTY1MDk5NTAzMH0.07X6VRwFmi1VJf6jpJyuLmonk-5VYUOFTWRRAvwzbWQ2mnvgkbAl5KMEZKM16yuMD9taMgPdLUofye-dnmD6JQ\"}','2022-04-27 01:43:50'),(13,1,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.60 Safari/537.36','{\"exp\":\"1652645588797\",\"iat\":\"1651331588797\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NTI2NDU1ODgsImlhdCI6MTY1MTMzMTU4OH0.PJqlgWee5dsUWnWDjAbORw9Uib242MOV6C3OssdpfPlVzK_gsHxnpckC1BU2UvwmseduFiM6FG_6PjRba3zhPg\"}','2022-04-30 23:13:08'),(14,1,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.127 Safari/537.36','{\"exp\":\"1652705411008\",\"iat\":\"1651391411008\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NTI3MDU0MTEsImlhdCI6MTY1MTM5MTQxMX0.v6p0HVAKyU3yrMnAlQcVA5y15IP1pfgZNJzi56DgMizOpcnVWY_NopVH574Nb6ap-U3i4PETVTUdNGf9GDv78w\"}','2022-05-01 15:50:11'),(15,1,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.127 Safari/537.36','{\"exp\":\"1652705410937\",\"iat\":\"1651391410937\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NTI3MDU0MTAsImlhdCI6MTY1MTM5MTQxMH0.f4ckwGCv8BRxMvgLJPQLdAI94yIA5CBUaAl1mJG4ipWptuFcO8DitLw5mPlM1L5Vk2V0XuGponRUrPUWm_-Ofg\"}','2022-05-01 15:50:10'),(16,2,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.127 Safari/537.36','{\"exp\":\"1652907071351\",\"iat\":\"1651593071351\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjIsInVuYW1lIjoiMTU4MzMzMzMzMzMiLCJleHAiOjE2NTI5MDcwNzEsImlhdCI6MTY1MTU5MzA3MX0.MQr9RW2la1cZ_camVTDmd2hT4ZHl7s3oSSXG6jn-D5fMp4X36CF8wBpcHfS0VrgMGawxoWLwpIkUwjxPYJxLGg\"}','2022-05-03 23:51:11'),(17,2,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.127 Safari/537.36','{\"exp\":\"1652907071351\",\"iat\":\"1651593071351\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjIsInVuYW1lIjoiMTU4MzMzMzMzMzMiLCJleHAiOjE2NTI5MDcwNzEsImlhdCI6MTY1MTU5MzA3MX0.MQr9RW2la1cZ_camVTDmd2hT4ZHl7s3oSSXG6jn-D5fMp4X36CF8wBpcHfS0VrgMGawxoWLwpIkUwjxPYJxLGg\"}','2022-05-03 23:51:11'),(18,1,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.127 Safari/537.36','{\"exp\":\"1652995010089\",\"iat\":\"1651681010089\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NTI5OTUwMTAsImlhdCI6MTY1MTY4MTAxMH0.gOsCWGxSXuAQSeRwP4bVeTBQKyDta_kq8AC-eknNOOb9D7LXihiPmmQsMr_GYyrFHjnRWSwSxX6JxLH7Lk9anw\"}','2022-05-05 00:16:50'),(19,1,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.127 Safari/537.36','{\"exp\":\"1652998589923\",\"iat\":\"1651684589923\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NTI5OTg1ODksImlhdCI6MTY1MTY4NDU4OX0._44ZnzmpRmrkyFL-NvZeAaOsefYcok460jWb2hdgUhOMssTYEHfhy1kQgEbRv5Uum2TCQLyfv3cLpzA1FMLsTg\"}','2022-05-05 01:16:29'),(20,1,'127.0.0.1',NULL,'{\"exp\":\"1653508421599\",\"iat\":\"1652194421599\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NTM1MDg0MjEsImlhdCI6MTY1MjE5NDQyMX0.8W1fv9qkJn1kYRdvcwdMuCZBt86QQAMfE7T1TL6Wsx2YNEcuC3h9QMnqNG2QHCOnuRdzASzivGXY0qa2TU1mCw\"}','2022-05-10 22:53:41'),(21,1,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.127 Safari/537.36','{\"exp\":\"1653510190838\",\"iat\":\"1652196190838\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NTM1MTAxOTAsImlhdCI6MTY1MjE5NjE5MH0.BotqJXanOfUKzjlUOSn7YkdKMjyquC3wjj973Zn1nNg-lgi0K65WPZ3NFi8AXm-O66VqrNI-Un9PPylRJlNM1A\"}','2022-05-10 23:23:10'),(22,1,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.127 Safari/537.36','{\"exp\":\"1653510345999\",\"iat\":\"1652196345999\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NTM1MTAzNDUsImlhdCI6MTY1MjE5NjM0NX0.FjlhpyGisD1T7qNBhb0kEgOuPq9QVezq7LubzdmSHb61VaZ5sBmv2ySVeCGVb6z9e1qvh8BonX4QxlfADb6UHg\"}','2022-05-10 23:25:45'),(23,1,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.127 Safari/537.36','{\"exp\":\"1653510691332\",\"iat\":\"1652196691332\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NTM1MTA2OTEsImlhdCI6MTY1MjE5NjY5MX0.lVHnuyTiaB5ueRw0YcY_1QqKt5pi6Op1-620WgFiO9AwEY4_v2Y2W4dgvprUtJvRbGYoZFe-XDrrUigVmmPM1A\"}','2022-05-10 23:31:31'),(24,1,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.127 Safari/537.36','{\"exp\":\"1653510777754\",\"iat\":\"1652196777754\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NTM1MTA3NzcsImlhdCI6MTY1MjE5Njc3N30.KEBkJQoXxjroh59eyIdkOtDCbhDFZk5QfjqUqfhVIP7gi2T9HPkhuTmIG91auo41_GkEVJDXPSKkh-YNslv04g\"}','2022-05-10 23:32:57'),(25,2,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.127 Safari/537.36','{\"exp\":\"1654290130656\",\"iat\":\"1652976130656\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjIsInVuYW1lIjoiMTU4MzMzMzMzMzMiLCJleHAiOjE2NTQyOTAxMzAsImlhdCI6MTY1Mjk3NjEzMH0.DBoQkPtCVw6rWiuTXzKon8rb0DmwHVmOOAj5Fm7n_4q3ngP47Uxgd-PRP7IOD4XJA0UR3iYxFWyMu7oZtfGVAQ\"}','2022-05-20 00:02:10'),(26,2,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.127 Safari/537.36','{\"exp\":\"1654290130720\",\"iat\":\"1652976130720\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjIsInVuYW1lIjoiMTU4MzMzMzMzMzMiLCJleHAiOjE2NTQyOTAxMzAsImlhdCI6MTY1Mjk3NjEzMH0.DBoQkPtCVw6rWiuTXzKon8rb0DmwHVmOOAj5Fm7n_4q3ngP47Uxgd-PRP7IOD4XJA0UR3iYxFWyMu7oZtfGVAQ\"}','2022-05-20 00:02:10'),(27,1,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.127 Safari/537.36','{\"exp\":\"1654626727174\",\"iat\":\"1653312727174\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NTQ2MjY3MjcsImlhdCI6MTY1MzMxMjcyN30.Q2YZbpALMUUQusCWCtYpDbUzTQh86p1yPXspyJxth9u8Dsfd5fiK0z_gEv2BMI3f2oqJDxc7N0CuTg4x0DVrww\"}','2022-05-23 21:32:07'),(28,1,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.127 Safari/537.36','{\"exp\":\"1654850702358\",\"iat\":\"1653536702358\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NTQ4NTA3MDIsImlhdCI6MTY1MzUzNjcwMn0.OA2leLWcRXOJxsQbcHlIf4ds3D9nHd19nP-NEZf1LbxV1mEVNCD5le-vEycEwG3oJRC5j_dwvLwmDretYYffSg\"}','2022-05-26 11:45:02'),(29,1,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.127 Safari/537.36','{\"exp\":\"1654967430473\",\"iat\":\"1653653430473\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NTQ5Njc0MzAsImlhdCI6MTY1MzY1MzQzMH0.b_wi8yDeB9ptv5b3LMiE24RinOYuvD3xE_BZ2y98bPAJ94lmL4tG2GTJBiqIJ-Y6DPub_oDprkyQz-fRRNRDLg\"}','2022-05-27 20:10:30'),(30,1,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.127 Safari/537.36','{\"exp\":\"1654973844079\",\"iat\":\"1653659844079\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NTQ5NzM4NDQsImlhdCI6MTY1MzY1OTg0NH0._Aal9cM7oA32Lo_w9va_uTAV9yQ4Zl2VZeTiRWexgCNbWkzBISRyJZNXIu0tGNfq-5Jod2wGNQ1HTF-UcwM5aA\"}','2022-05-27 21:57:24'),(31,2,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.127 Safari/537.36','{\"exp\":\"1654979575824\",\"iat\":\"1653665575824\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjIsInVuYW1lIjoiMTU4MzMzMzMzMzMiLCJleHAiOjE2NTQ5Nzk1NzUsImlhdCI6MTY1MzY2NTU3NX0.j3wakE3HVqhHP20My4354ZmJO9EFXW47JvZNV9Zp39Iwu42gXLGyucQefdd8zIUb5Zw-hvAiQTMtp7zjcuf0fQ\"}','2022-05-27 23:32:55'),(32,1,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.127 Safari/537.36','{\"exp\":\"1655025056477\",\"iat\":\"1653711056477\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NTUwMjUwNTYsImlhdCI6MTY1MzcxMTA1Nn0.mXB_YY_4VwPN9iU7KltThVHlEeilYhEEBNCeB3w15pt83x0R4uBob2qJP-dH8U2kUm8GSHrDL_2Tf2kG3Uzcag\"}','2022-05-28 12:10:56');
/*!40000 ALTER TABLE `admin_user_login_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `approval_process`
--

DROP TABLE IF EXISTS `approval_process`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `approval_process` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `no` varchar(100) DEFAULT NULL COMMENT '审批流编号；业务绑定',
  `name` varchar(100) DEFAULT NULL COMMENT '审批流名称',
  `type` tinyint(4) DEFAULT NULL COMMENT '审批类型； 1 普通列队审批',
  `enable` bit(1) DEFAULT NULL COMMENT '是否开启 0 不开启  1 开启',
  `reject_action` tinyint(4) DEFAULT NULL COMMENT '审批拒绝动作; 1 默认关闭 10 打回第一个审批人  20 打回上一审批人',
  `metadata` varchar(20000) DEFAULT NULL COMMENT '元数据信息',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `no_UNIQUE` (`no`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='审批流';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `approval_process`
--

LOCK TABLES `approval_process` WRITE;
/*!40000 ALTER TABLE `approval_process` DISABLE KEYS */;
INSERT INTO `approval_process` VALUES (1,'1','请假申请',1,_binary '',10,'[\n {\"key\":\"CONTENT\", \"title\":\"申请描述\",\"type\":\"textarea\"},\n {\"key\":\"START_TIME\", \"title\":\"开始时间\",\"type\":\"datetime\"},\n {\"key\":\"END_TIME\", \"title\":\"结束时间\",\"type\":\"datetime\"}\n ]','2022-05-29 01:13:57'),(2,'2','调休申请',1,_binary '',10,'[\n{\"key\":\"CONTENT\", \"title\":\"申请描述\"},\n{\"key\":\"START_TIME\", \"title\":\"开始时间\"},\n{\"key\":\"END_TIME\", \"title\":\"结束时间\"}\n]','2022-05-29 01:15:20'),(3,'3','印章申请',1,_binary '',10,'[\n{\"key\":\"CONTENT\", \"title\":\"申请描述\"},\n{\"key\":\"START_TIME\", \"title\":\"开始时间\"},\n{\"key\":\"END_TIME\", \"title\":\"结束时间\"}\n]','2022-05-29 02:26:16');
/*!40000 ALTER TABLE `approval_process` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `approval_process_node`
--

DROP TABLE IF EXISTS `approval_process_node`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `approval_process_node` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `approval_process_id` int(11) DEFAULT NULL COMMENT '审批流ID',
  `source_user_id` int(11) DEFAULT NULL COMMENT '源用户ID； 上一节点ID',
  `user_id` int(11) DEFAULT NULL COMMENT '审批用户ID',
  `serial_number` int(11) DEFAULT NULL COMMENT '序列号； 决定审批顺序',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8 COMMENT='审批流节点';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `approval_process_node`
--

LOCK TABLES `approval_process_node` WRITE;
/*!40000 ALTER TABLE `approval_process_node` DISABLE KEYS */;
INSERT INTO `approval_process_node` VALUES (1,1,0,1,0,'2022-05-29 01:13:58'),(2,1,1,2,1,'2022-05-29 01:13:58'),(3,2,0,1,0,'2022-05-29 01:15:20'),(4,2,1,2,1,'2022-05-29 01:15:20'),(9,3,0,1,0,'2022-05-29 02:26:18'),(10,3,1,2,1,'2022-05-29 02:26:18');
/*!40000 ALTER TABLE `approval_process_node` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `approval_process_order`
--

DROP TABLE IF EXISTS `approval_process_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `approval_process_order` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `approval_process_id` int(11) DEFAULT NULL COMMENT '审批单ID',
  `apply_user_id` int(11) DEFAULT NULL COMMENT '申请人ID',
  `out_no` varchar(128) DEFAULT NULL COMMENT '外部单号',
  `status` tinyint(4) DEFAULT NULL COMMENT '审批状态; 0 待审批  10 审批完成',
  `apply_status` tinyint(4) DEFAULT NULL COMMENT '审批处理结果： 0 失败  1 成功',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `content` varchar(20000) DEFAULT NULL COMMENT '审核单内容',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8 COMMENT='审批单';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `approval_process_order`
--

LOCK TABLES `approval_process_order` WRITE;
/*!40000 ALTER TABLE `approval_process_order` DISABLE KEYS */;
INSERT INTO `approval_process_order` VALUES (1,3,1,'43119611092144128',0,0,'2022-05-29 01:20:15','[{\"key\":\"CONTENT\",\"title\":\"显示标题\",\"content\":\"新增合同， 申请盖章\"}]'),(2,1,1,'43119734970912768',0,0,'2022-05-29 01:20:44','[{\"key\":\"CONTENT\",\"title\":\"显示标题\",\"content\":\"家里有事， 请假一天\"}]'),(3,1,1,'43320174387924992',0,0,'2022-05-29 14:37:14','[{\"key\":\"CONTENT\",\"title\":\"显示标题\",\"content\":\"惆怅长岑长错错\"},{\"key\":\"START_TIME\",\"title\":\"显示标题\",\"content\":\"2022-05-10 00:00:00\"},{\"key\":\"END_TIME\",\"title\":\"显示标题\",\"content\":\"2022-05-29 00:00:00\"}]'),(4,1,1,'43328528598437888',0,0,'2022-05-29 15:10:26','[{\"key\":\"START_TIME\",\"title\":\"显示标题\",\"content\":\"2022-05-29 00:00:00\"},{\"key\":\"END_TIME\",\"title\":\"显示标题\",\"content\":\"2022-05-29 00:00:00\"}]'),(5,1,1,'43338500841410560',0,0,'2022-05-29 15:50:03','[{\"key\":\"START_TIME\",\"title\":\"显示标题\",\"content\":\"2022-05-13 00:00:00\"},{\"key\":\"END_TIME\",\"title\":\"显示标题\",\"content\":\"2022-05-29 00:00:00\"}]'),(6,1,1,'43355288803217408',0,0,'2022-05-29 16:56:48','[{\"key\":\"CONTENT\",\"title\":\"申请描述\",\"content\":\"热热热热热若若若若若从\"},{\"key\":\"START_TIME\",\"title\":\"开始时间\",\"content\":\"2022-05-20 00:00:00\"},{\"key\":\"END_TIME\",\"title\":\"结束时间\",\"content\":\"2022-05-29 00:00:00\"}]'),(7,1,1,'43376085097779200',1,1,'2022-05-29 18:19:24','[{\"key\":\"CONTENT\",\"title\":\"申请描述\",\"content\":\"家里有事， 请假三天；望领导批准\"},{\"key\":\"START_TIME\",\"title\":\"开始时间\",\"content\":\"2022-05-29 00:00:00\"},{\"key\":\"END_TIME\",\"title\":\"结束时间\",\"content\":\"2022-05-31 00:00:00\"}]');
/*!40000 ALTER TABLE `approval_process_order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `approval_process_order_node`
--

DROP TABLE IF EXISTS `approval_process_order_node`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `approval_process_order_node` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `order_id` int(11) DEFAULT NULL,
  `source_user_id` int(11) DEFAULT NULL COMMENT '源审批人； 上一审批人',
  `user_id` int(11) DEFAULT NULL COMMENT '审批人ID',
  `status` tinyint(4) DEFAULT NULL COMMENT '审批状态',
  `reply` varchar(1000) DEFAULT NULL COMMENT '审批回复',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `serial_number` tinyint(4) DEFAULT NULL COMMENT '审批序号；审批顺序',
  `approval_time` datetime DEFAULT NULL COMMENT '审批时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8 COMMENT='审批单节点';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `approval_process_order_node`
--

LOCK TABLES `approval_process_order_node` WRITE;
/*!40000 ALTER TABLE `approval_process_order_node` DISABLE KEYS */;
INSERT INTO `approval_process_order_node` VALUES (1,1,0,1,30,'同意请假； 请保持电话畅通','2022-05-29 01:20:15',0,'2022-05-29 01:20:22'),(2,1,1,2,10,NULL,'2022-05-29 01:20:15',1,NULL),(3,2,0,1,10,NULL,'2022-05-29 01:20:45',0,NULL),(4,2,1,2,0,NULL,'2022-05-29 01:20:45',1,NULL),(5,3,0,1,30,'同意请假； 请保持电话畅通','2022-05-29 14:37:15',0,'2022-05-29 21:50:32'),(6,3,1,2,10,NULL,'2022-05-29 14:37:15',1,NULL),(7,4,0,1,30,'同意请假； 请保持电话畅通','2022-05-29 15:10:27',0,'2022-05-29 15:24:51'),(8,4,1,2,10,NULL,'2022-05-29 15:10:27',1,NULL),(9,5,0,1,10,NULL,'2022-05-29 15:50:04',0,NULL),(10,5,1,2,0,NULL,'2022-05-29 15:50:04',1,NULL),(11,6,0,1,30,'同意请假； 请保持电话畅通','2022-05-29 16:56:48',0,'2022-05-29 21:54:23'),(12,6,1,2,10,NULL,'2022-05-29 16:56:49',1,NULL),(13,7,0,1,20,'延期请假； 请优先工作进度，力求月底完工','2022-05-29 18:19:25',0,'2022-05-29 18:49:46'),(14,7,1,2,0,NULL,'2022-05-29 18:19:26',1,NULL);
/*!40000 ALTER TABLE `approval_process_order_node` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hello`
--

DROP TABLE IF EXISTS `hello`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hello` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) DEFAULT NULL,
  `value` varchar(45) DEFAULT NULL,
  `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hello`
--

LOCK TABLES `hello` WRITE;
/*!40000 ALTER TABLE `hello` DISABLE KEYS */;
INSERT INTO `hello` VALUES (1,'1111','11111','2222-01-01 00:00:00','2222-01-02 03:04:05'),(2,'dfasd','dfasdf','2222-01-01 00:00:00','2222-01-01 00:00:00'),(3,'cddf','dfasdf','2222-01-01 00:00:00','2222-01-01 00:00:00');
/*!40000 ALTER TABLE `hello` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-05-29 22:15:26
