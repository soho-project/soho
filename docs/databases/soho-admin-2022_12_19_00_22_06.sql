-- MariaDB dump 10.19  Distrib 10.6.11-MariaDB, for debian-linux-gnu (x86_64)
--
-- Host: 192.168.0.101    Database: dev
-- ------------------------------------------------------
-- Server version	5.5.68-MariaDB

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
-- Table structure for table `admin_config`
--

DROP TABLE IF EXISTS `admin_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8 COMMENT='系统配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin_config`
--

LOCK TABLES `admin_config` WRITE;
/*!40000 ALTER TABLE `admin_config` DISABLE KEYS */;
INSERT INTO `admin_config` VALUES (1,'public','login_use_captcha','false','后台登录是否开启验证码',1,'2022-04-22 21:53:32','2020-01-01 00:00:00'),(2,'public','key2','aaaa','这是页面测试用的',1,'2020-01-01 00:00:00','2020-01-01 00:00:00'),(14,'public','key14ff','test','这是页面测试用的',1,'2020-01-01 00:00:00','2020-01-01 00:00:00'),(15,'public','int','100','这是页面测试用的',1,'2020-01-01 00:00:00','2020-01-01 00:00:00');
/*!40000 ALTER TABLE `admin_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `admin_config_group`
--

DROP TABLE IF EXISTS `admin_config_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
-- Table structure for table `admin_content`
--

DROP TABLE IF EXISTS `admin_content`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `admin_content` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(1000) DEFAULT NULL COMMENT '文章标题',
  `description` varchar(3000) DEFAULT NULL COMMENT '文章描述',
  `keywords` varchar(1000) DEFAULT NULL COMMENT '关键字',
  `thumbnail` varchar(500) DEFAULT NULL COMMENT '缩略图',
  `body` text COMMENT '文章内容',
  `updated_time` datetime DEFAULT NULL COMMENT '创建时间',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `category_id` int(11) DEFAULT NULL COMMENT '文章分类ID',
  `user_id` int(11) DEFAULT NULL COMMENT '添加的管理员ID',
  `status` tinyint(4) DEFAULT NULL COMMENT '文章状态；  0 禁用  1 发布',
  `is_top` tinyint(4) DEFAULT '0' COMMENT '是否置顶',
  `star` int(11) NOT NULL DEFAULT '0' COMMENT 'star数量',
  `likes` int(11) NOT NULL DEFAULT '0' COMMENT '点赞数量',
  `dis_likes` int(11) NOT NULL DEFAULT '0',
  `comments_count` int(11) NOT NULL DEFAULT '0' COMMENT '评论数量',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='系统内容表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin_content`
--

LOCK TABLES `admin_content` WRITE;
/*!40000 ALTER TABLE `admin_content` DISABLE KEYS */;
INSERT INTO `admin_content` VALUES (1,'Java 教程，Java 简介','Java 教程\nJava 是高级程序设计语言\nJava 语言的数组是这样写的：int [] a=new int [10];\n运行 javac 命令后，如果成功编译没有错误的话，会出现一个 HelloWorld.class 的文件\nJava 简介\nJava 是由 Sun Microsystems 公司于 1995 年 5 月推出的 Java 面向对象程序设计语言和 Java 平台的总称。由 James Gosling 和同事们共同研发，并在 1995 年正式推出。','111','https://igogo-test.oss-cn-shenzhen.aliyuncs.com/test/2022_9_10_80965924416393216.jpg','<p>Java 教程</p>\n<p>Java 是高级程序设计语言</p>\n<p>Java 语言的数组是这样写的：int [] a=new int [10];</p>\n<p>运行 javac 命令后，如果成功编译没有错误的话，会出现一个 HelloWorld.class 的文件</p>\n<p>Java 简介</p>\n<p>Java 是由 Sun Microsystems 公司于 1995 年 5 月推出的 Java 面向对象程序设计语言和 Java 平台的总称。由 James Gosling 和同事们共同研发，并在 1995 年正式推出。</p>\n<p>Java 分为三个体系：</p>\n<ul>\n<li>JavaSE（J2SE）（Java2 Platform Standard Edition，java 平台标准版）</li>\n<li>JavaEE (J2EE)(Java 2 Platform,Enterprise Edition，java 平台企业版)</li>\n<li>JavaME (J2ME)(Java 2 Platform Micro Edition，java 平台微型版)。</li>\n</ul>\n<p>2005 年 6 月，JavaOne 大会召开，SUN 公司公开 Java SE 6。此时，Java 的各种版本已经更名以取消其中的数字 \"2\"：J2EE 更名为 Java EE, J2SE 更名为 Java SE，J2ME 更名为 Java ME。</p>\n<ul>\n<li>JDK（Java Development Kit ）：编写 Java 程序的程序员使用的软件</li>\n<li>JRE（Java Runtime Environment）：运行 Java 程序的用户使用的软件</li>\n<li>Server JRE （Java SE Runtime Environment）：服务端使用的 Java 运行环境</li>\n<li>SDK（Software Development Kit）：软件开发工具包，在 Java 中用于描述 1998 年～2006 年之间的 JDK</li>\n<li>DAO（Data Access Object）：数据访问接口，数据访问，顾名思义就是与数据库打交道</li>\n<li>MVC（Model View Controller）：模型 (model)－视图 (view)－控制器 (controller) 的缩写，一种软件设计典范，用于组织代码用一种业务逻辑和数据显示分离的方法</li>\n</ul>\n<p>面向对象程序设计的 3 个主要特征：封装性、继承性、多态性。</p>\n<p><strong>封装性（encapsulation）：</strong>封装是一种信息隐蔽技术，它体现于类的说明，是对象的重要特性。封装使数据和加工该数据的方法（函数）封装为一个整体，以实现独立性很强的模块，使得用户只能见到对象的外特性（对象能接受哪些消息，具有哪些处理能力），而对象的内特性（保存内部状态的私有数据和实现加工能力的算法）对用户是隐蔽的。封装的目的在于把对象的设计者和对象的使用者分开，使用者不必知晓其行为实现的细节，只须用设计者提供的消息来访问该对象。</p>\n<p><strong>继承性：</strong>继承性是子类共享其父类数据和方法的机制。它由类的派生功能体现。一个类直接继承其他类的全部描述，同时可修改和扩充。继承具有传递性。继承分为单继承（一个子类有一父类）和多重继承（一个类有多个父类）。类的对象是各自封闭的，如果没继承性机制，则类的对象中的数据、方法就会出现大量重复。继承不仅支持系统的可重用性，而且还促进系统的可扩充性。</p>\n<p><strong>多态性：</strong>对象根据所接收的消息而做出动作。同一消息被不同的对象接受时可产生完全不同的行动，这种现象称为多态性。利用多态性用户可发送一个通用的信息，而将所有的实现细节都留给接受消息的对象自行决定，如是，同一消息即可调用不同的方法。例如：同样是 run 方法，飞鸟调用时是飞，野兽调用时是奔跑。多态性的实现受到继承性的支持，利用类继承的层次关系，把具有通用功能的协议存放在类层次中尽可能高的地方，而将实现这一功能的不同方法置于较低层次，这样，在这些低层次上生成的对象就能给通用消息以不同的响应。在 OOPL 中可通过在派生类中重定义基类函数（定义为重载函数或虚函数）来实现多态性。</p>\n<p>SDK——soft development kit，软件开发工具包。sdk 是一个大的概念，比如开发安卓应用，你需要安卓开发工具包，叫 android sdk，比如你开发 java 程序，需要用到 java sdk，所以一般使用 sdk 这个概念，你需要在前面加上限定词。</p>\n<p>JDK—— 可以理解为 java sdk，它是编写 java 程序，使用到的工具包，为程序员提供一些已经封装好的 java 类库。</p>\n<p><a href=\"https://www.oschina.net/action/GoToLink?url=https%3A%2F%2Fwww.runoob.com%2Fw3cnote%2Fthe-different-of-jre-and-jdk.html\" target=\"_self\"><strong>JDK 和 JRE 的区别</strong></a></p>\n<p><strong>JRE(Java Runtime Enviroment)</strong> 是 Java 的运行环境。面向 Java 程序的使用者，而不是开发者。如果你仅下载并安装了 JRE，那么你的系统只能运行 Java 程序。JRE 是运行 Java 程序所必须环境的集合，包含 JVM 标准实现及 Java 核心类库。它包括 Java 虚拟机、Java 平台核心类和支持文件。它不包含开发工具 (编译器、调试器等)。</p>\n<p><strong>JDK(Java Development Kit)</strong> 又称 J2SDK (Java2 Software Development Kit)，是 Java 开发工具包，它提供了 Java 的开发环境 (提供了编译器 javac 等工具，用于将 java 文件编译为 class 文件) 和运行环境 (提 供了 JVM 和 Runtime 辅助包，用于解析 class 文件使其得到运行)。如果你下载并安装了 JDK，那么你不仅可以开发 Java 程序，也同时拥有了运行 Java 程序的平台。JDK 是整个 Java 的核心，包括了 Java 运行环境 (JRE)，一堆 Java 工具 tools.jar 和 Java 标准类库 (rt.jar)。</p>\n<p>DOS 命令操作</p>\n<p>1. 快捷键：<strong>windows+R  </strong>呼出 <strong>DOS</strong> 窗口。</p>\n<p>2. 输入 <strong>cmd</strong>(大小写不用区分) 回车，打开 <strong>DOS</strong> 窗口。</p>\n<p>常见的命令（基本都不区分大小写）</p>\n<p>1. 切换盘符：   盘符（就是电脑的 C,D,E.. 盘） ： （如 ：   <strong>D :  </strong>)</p>\n<p>2. 查看文件或者文件夹：  <strong>dir</strong></p>\n<p>3. 进入某个文件夹：    <strong>cd</strong> 文件夹名</p>\n<p>4. 返回上一级目录：    <strong>cd..</strong></p>\n<p>5. 清屏：  <strong>cls</strong></p>\n<p>6. 删除某个文件：   <strong>del</strong>   文件名</p>\n<p>7. 删除某个文件夹： <strong>rd</strong>  文件夹名</p>\n<p>8. 退出 <strong>DOS</strong> 窗口： <strong>exit</strong></p>\n<p><strong>这些都是学 Java 之前稍微要了解一下的东西，随便看看就好啦，不用花太多时间在上面哦</strong></p>\n','2022-09-28 01:49:56','2022-09-04 01:51:57',1,1,1,0,0,19,14,0),(2,'河南唐河：中医药变身便民惠民“香饽饽”','9月26日晚，夜幕降临，河南省南阳市唐河县体育文化广场中医药文化夜市上，来了不少体验中医健康咨询、健康查体、免费测血压、体验针灸推拿等技术服务的群众。\n据介绍，唐河县农耕文化历史悠久，中医药文化具有一定的群众基础。该县也是河南省内重要的中药材种植大县，包括国家地理标志产品唐栀子、唐半夏在内的数十种中药材种植面积就达18万亩。\n为充分发挥中医药资源优势，唐河县从中医药信息化建设、中医师签约服务、中医药宣传推广入手，促进中医药走进基层、服务群众。','keyword','https://igogo-test.oss-cn-shenzhen.aliyuncs.com/test/2022_9_14_82518662937841664.jpg','<p style=\"text-align:start;\"><span style=\"color: rgb(38,38,38);font-size: 18px;font-family: PingFang SC\", Helvetica, \"Microsoft YaHei\", Arial;\">9月26日晚，夜幕降临，河南省南阳市唐河县体育文化广场中医药文化夜市上，来了不少体验中医健康咨询、健康查体、免费测血压、体验针灸推拿等技术服务的群众。</span></p>\n<p style=\"text-align:start;\"><span style=\"color: rgb(38,38,38);font-size: 18px;font-family: PingFang SC\", Helvetica, \"Microsoft YaHei\", Arial;\">据介绍，唐河县农耕文化历史悠久，中医药文化具有一定的群众基础。该县也是河南省内重要的中药材种植大县，包括国家地理标志产品唐栀子、唐半夏在内的数十种中药材种植面积就达18万亩。</span></p>\n<p style=\"text-align:start;\"><span style=\"color: rgb(38,38,38);font-size: 18px;font-family: PingFang SC\", Helvetica, \"Microsoft YaHei\", Arial;\">为充分发挥中医药资源优势，唐河县从中医药信息化建设、中医师签约服务、中医药宣传推广入手，促进中医药走进基层、服务群众。</span></p>\n<p style=\"margin-left:0px;\"></p>\n<p></p>\n<p style=\"margin-left:0px;\"></p>\n<p></p>\n<p style=\"text-align:start;\"><span style=\"color: rgb(38,38,38);font-size: 18px;font-family: PingFang SC\", Helvetica, \"Microsoft YaHei\", Arial;\">推进中医药信息化建设。该县投资4000余万建设全县中医药信息化系统。目前，中医药信息化系统已基本实现县、乡、村三级实时心电传输、影像传输和远程会诊。配合县中医院的“经方云”平台的医疗诊断功能、“智慧中药房系统”远程配送功能和“中医师家庭医生签约”平台预防保健及宣教功能，实现了基层群众足不出户即可享受中医药服务。</span></p>\n<p style=\"text-align:start;\"><span style=\"color: rgb(38,38,38);font-size: 18px;font-family: PingFang SC\", Helvetica, \"Microsoft YaHei\", Arial;\">开展中医师家庭签约服务。全县统一签约手册、签约流程、签约档案、签约管理。全县统一行动，每月组织开展一次巡诊，每季度开展一次大型义诊，每年对签约人员开展一次健康体检。根据不同人群需求，与中医药特色优势相结合，为签约对象提供健康状态体质辨识、健康咨询指导、健康教育、健康干预、饮食调节、情志调节等治未病服务。截至目前，唐河县中医师家庭签约服务已在全县所有乡镇铺开，共签约81万余人，约19万户。</span></p>\n<p style=\"text-align:start;\"><span style=\"color: rgb(38,38,38);font-size: 18px;font-family: PingFang SC\", Helvetica, \"Microsoft YaHei\", Arial;\">开展中医药文化夜市活动。唐河县卫健委牵头在全县开展“中医药文化夜市”，设立各类体验活动区，为群众提供中医健康咨询、健康查体、免费测血压、体验针灸推拿等服务，免费向群众提供各种不同药膳、茶饮等。（马宏、高瞻）</span>&nbsp;</p>\n','2022-09-28 01:50:48','2022-09-14 18:38:11',3,1,1,1,0,0,0,0);
/*!40000 ALTER TABLE `admin_content` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `admin_content_category`
--

DROP TABLE IF EXISTS `admin_content_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `admin_content_category` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(500) DEFAULT NULL COMMENT '分类名称',
  `parent_id` int(11) DEFAULT '0' COMMENT '父分类ID',
  `description` varchar(1000) DEFAULT NULL COMMENT '分类描述',
  `keyword` varchar(500) DEFAULT NULL COMMENT '分类关键字',
  `content` text COMMENT '分类内容',
  `order` int(11) DEFAULT NULL COMMENT '排序',
  `is_display` tinyint(4) DEFAULT NULL COMMENT '分类是否显示 0 不显示  1 显示',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='内容分类';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin_content_category`
--

LOCK TABLES `admin_content_category` WRITE;
/*!40000 ALTER TABLE `admin_content_category` DISABLE KEYS */;
INSERT INTO `admin_content_category` VALUES (1,'Java',0,'Java','Java','Java相关内容',1,1,'2022-09-08 22:39:08',NULL),(2,'Android',0,'android','android','android',2,1,'2022-09-08 22:39:28',NULL),(3,'Java',2,'java','java','java',3,1,'2022-09-08 22:39:40',NULL);
/*!40000 ALTER TABLE `admin_content_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `admin_job`
--

DROP TABLE IF EXISTS `admin_job`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `admin_job` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(1000) DEFAULT NULL COMMENT '任务名称',
  `can_concurrency` int(11) DEFAULT NULL COMMENT '能否并发执行',
  `cmd` varchar(10000) DEFAULT NULL COMMENT '计划任务执行指令',
  `status` int(11) DEFAULT NULL COMMENT '状态 1 可执行  0 禁止执行',
  `cron` varchar(100) DEFAULT NULL COMMENT 'cron表达式',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `updated_time` datetime DEFAULT NULL COMMENT '最后更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='计划任务表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin_job`
--

LOCK TABLES `admin_job` WRITE;
/*!40000 ALTER TABLE `admin_job` DISABLE KEYS */;
INSERT INTO `admin_job` VALUES (2,'test2',1,'work.soho.common.quartz.Hello::test(33)',0,'0/5 * * * * ?',NULL,'2022-09-10 06:41:32');
/*!40000 ALTER TABLE `admin_job` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `admin_job_log`
--

DROP TABLE IF EXISTS `admin_job_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `admin_job_log` (
  `id` int(11) DEFAULT NULL,
  `job_id` int(11) DEFAULT NULL COMMENT '任务ID',
  `start_time` datetime DEFAULT NULL COMMENT '任务创建时间',
  `status` int(11) DEFAULT NULL COMMENT '任务执行状态; 1 执行中  2 执行完成  3 任务取消',
  `result` varchar(20000) DEFAULT NULL COMMENT '任务执行返回结果',
  `end_time` datetime DEFAULT NULL COMMENT '任务结束时间',
  `admin_id` int(11) DEFAULT NULL COMMENT '手动执行时候的用户ID'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='计划任务执行日志';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin_job_log`
--

LOCK TABLES `admin_job_log` WRITE;
/*!40000 ALTER TABLE `admin_job_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `admin_job_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `admin_notification`
--

DROP TABLE IF EXISTS `admin_notification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=47 DEFAULT CHARSET=utf8 COMMENT='资源表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin_resource`
--

LOCK TABLES `admin_resource` WRITE;
/*!40000 ALTER TABLE `admin_resource` DISABLE KEYS */;
INSERT INTO `admin_resource` VALUES (1,'Admin','/',0,'Admin','2022-01-30 23:42:25',1,1,0,'后台管理','dashboard'),(2,'System Mange','/',1,'系统管理目录','2022-04-06 23:00:40',1,200,1,'系统管理','AppstoreOutlined'),(3,'Users','/admin_user',1,'用户管理','2022-01-30 23:42:25',1,1,2,'用户管理','user'),(4,'Posts','/post',1,'文章管理','2022-01-30 23:42:25',1,100,2,'文章管理','shopping-cart'),(5,'User Detail','/user/:id',1,'用户详情','2022-01-30 23:42:25',0,100,2,'用户详情','user'),(6,'Request','/request',1,'接口模拟请求','2022-01-30 23:42:25',1,100,2,'接口模拟请求','api'),(7,'UI Element','/ui',0,'UI组件','2022-01-30 23:42:25',0,100,2,'UI组件','camera-o'),(8,'Editor','/editor',1,'编辑','2022-01-30 23:42:25',0,100,2,'编辑',NULL),(9,'Charts','/Charts',1,'图标','2022-01-30 23:42:25',0,100,1,'图表','area-chart'),(10,'ECharts','/chart/ECharts',1,'ECharts','2022-01-30 23:42:25',0,100,9,'ECharts','area-chart'),(11,'HighCharts','/chart/highCharts',1,'/chart/highCharts','2022-01-30 23:42:25',0,100,9,'Rechartst','area-chart'),(12,'Rechartst','Rechartst',1,'/chart/Recharts','2022-01-30 23:42:25',0,100,1,'Rechartst','area-chart'),(13,'Role','/role',1,'角色管理','2022-01-30 23:42:25',1,1,2,'角色管理','user'),(14,'Resource','/resource',1,'资源管理','2022-01-30 23:42:25',1,1,2,'资源管理','api'),(18,'Config','/config',1,'系统配置','2022-04-06 00:06:26',1,20,2,'系统配置','SettingOutlined'),(19,'Dashboard','/dashboard',1,'首页','2022-01-30 23:42:25',1,0,1,'仪表盘','dashboard'),(21,'notifications','/admin_notification',1,'系统通知','2022-04-24 02:36:48',1,20,2,'系统通知','MessageOutlined'),(22,'Approval Process Manger','/approval_process',1,'审批流管理','2022-05-10 15:16:20',1,199,1,'审批流管理','user'),(23,'Approval Process List','/approval_process',1,'审批流列表','2022-05-10 15:17:47',1,500,22,'审批流管理','user'),(24,'My Approval','/approval_process_myorder',1,'我的审批','2022-05-23 21:29:04',1,1,22,'我的审批','user'),(25,'Approval Order','/approval_process_order',1,'审批单管理','2022-05-23 21:29:04',1,1,22,'审批单管理','user'),(26,'计划任务管理','/admin_job',1,'计划任务管理','2022-08-01 16:59:37',1,1,2,'计划任务管理','user'),(27,'内容管理','/admin_content',1,'内容管理','2022-08-01 16:59:37',1,1,1,'内容管理','user'),(28,'内容列表','/admin_content',1,'内容列表','2022-08-01 16:59:37',1,1,27,'内容管理','user'),(29,'分类管理','/admin_content_category',1,'分类管理','2022-09-06 01:45:15',1,1,27,'分类管理','user'),(30,'物联网管理','/lot_model',1,'物联网管理','2022-11-06 21:15:44',1,10,1,'物联网管理','user'),(31,'模型列表','/lot_model',1,NULL,'2022-11-06 21:17:31',1,100,30,'模型列表','user'),(32,'产品列表','/lot_product',1,'产品列表','2022-11-06 21:18:36',1,100,30,'产品列表','user'),(33,'Pay Info','/pay_info',1,'支付管理','2022-11-07 23:09:46',1,101,1,'支付管理','user'),(34,'Pay Info','/pay_info',1,'支付方式列表','2022-11-07 23:10:22',1,101,33,'支付列表','user'),(35,'Pay Order','/pay_order',1,'支付单','2022-11-07 23:11:01',1,102,33,'支付单列表','user'),(36,'example','/example',1,'样例显示；用来开发自动化脚本用','2022-11-18 18:37:13',1,99,1,'自动化样例','user'),(38,'example','/example',1,'样例显示；用来开发自动化脚本用','2022-11-18 18:56:44',1,101,36,'自动化样例','user'),(39,'Example Category','/example_category',1,'样例显示分类','2022-11-18 18:57:49',1,101,36,'自动化样例分类Tree','user'),(40,'User Info','/user_info',1,'用户管理','2022-11-28 00:57:13',1,102,1,'用户管理','user'),(41,'User List','/user_info',1,'用户列表','2022-11-28 00:57:55',1,101,40,'用户列表','user'),(42,'Code Table','/code_table',1,'代码表','2022-01-01 00:00:00',1,100,2,'低代码模型','user'),(43,'Code Table Column','/code_table_column',1,'表字段','2022-11-30 15:07:38',0,101,42,'表字段','user'),(44,'Code Table List','/code_table',1,NULL,'2022-11-30 15:10:05',1,99,42,'表模型列表','user'),(45,'Code Table Template','/code_table_template',1,'代码生成模板','2022-12-08 00:34:16',1,101,42,'代码生成模板','user'),(46,'Template Group','/code_table_template_group',1,'模板分组','2022-12-12 17:38:11',1,99,42,'模板分组','user');
/*!40000 ALTER TABLE `admin_resource` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `admin_role`
--

DROP TABLE IF EXISTS `admin_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `admin_role` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(45) DEFAULT NULL COMMENT '角色名',
  `remarks` varchar(255) DEFAULT NULL COMMENT '备注',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `enable` tinyint(4) DEFAULT '1' COMMENT '是否启用',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='角色表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin_role`
--

LOCK TABLES `admin_role` WRITE;
/*!40000 ALTER TABLE `admin_role` DISABLE KEYS */;
INSERT INTO `admin_role` VALUES (1,'admin','管理员角色；该角色无需配置资源，默认所有资源权限','2022-01-29 12:03:07',1),(2,'developer','开发者角色；默认有所有权限， 线上不应该开启该角色','2022-01-29 12:03:07',1),(3,'角色3','String','2022-01-29 12:03:07',1);
/*!40000 ALTER TABLE `admin_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `admin_role_resource`
--

DROP TABLE IF EXISTS `admin_role_resource`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `admin_role_resource` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `role_id` int(10) unsigned DEFAULT NULL COMMENT '角色ID',
  `resource_id` int(10) unsigned DEFAULT NULL COMMENT '资源ID',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=64 DEFAULT CHARSET=utf8 COMMENT='角色资源关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin_role_resource`
--

LOCK TABLES `admin_role_resource` WRITE;
/*!40000 ALTER TABLE `admin_role_resource` DISABLE KEYS */;
INSERT INTO `admin_role_resource` VALUES (46,1,27,'2022-11-27 17:03:23'),(47,1,19,'2022-11-27 17:03:23'),(48,1,30,'2022-11-27 17:03:23'),(49,1,36,'2022-11-27 17:03:23'),(50,1,9,'2022-11-27 17:03:23'),(51,1,12,'2022-11-27 17:03:23'),(52,1,33,'2022-11-27 17:03:24'),(53,1,22,'2022-11-27 17:03:24'),(54,1,2,'2022-11-27 17:03:24'),(55,2,19,'2022-12-07 14:17:57'),(56,2,27,'2022-12-07 14:17:57'),(57,2,30,'2022-12-07 14:17:58'),(58,2,36,'2022-12-07 14:17:58'),(59,2,9,'2022-12-07 14:17:58'),(60,2,33,'2022-12-07 14:17:58'),(61,2,12,'2022-12-07 14:17:58'),(62,2,40,'2022-12-07 14:17:59'),(63,2,22,'2022-12-07 14:17:59');
/*!40000 ALTER TABLE `admin_role_resource` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `admin_role_user`
--

DROP TABLE IF EXISTS `admin_role_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `admin_role_user` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `role_id` int(10) unsigned DEFAULT NULL COMMENT '角色ID',
  `user_id` int(10) unsigned DEFAULT NULL COMMENT '用户ID',
  `status` tinyint(4) DEFAULT NULL COMMENT '状态',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8 COMMENT='角色用户关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin_role_user`
--

LOCK TABLES `admin_role_user` WRITE;
/*!40000 ALTER TABLE `admin_role_user` DISABLE KEYS */;
INSERT INTO `admin_role_user` VALUES (9,1,1,1,'2022-04-24 01:07:53'),(10,1,2,1,'2022-04-24 02:16:37'),(11,1,3,1,'2022-04-24 01:07:53'),(12,1,4,1,'2022-04-24 01:07:53');
/*!40000 ALTER TABLE `admin_role_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `admin_user`
--

DROP TABLE IF EXISTS `admin_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COMMENT='系统用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin_user`
--

LOCK TABLES `admin_user` WRITE;
/*!40000 ALTER TABLE `admin_user` DISABLE KEYS */;
INSERT INTO `admin_user` VALUES (1,'admin','15873164073','adminNickNamaadd','fang.liu','https://igogo-test.oss-cn-shenzhen.aliyuncs.com/user/avatar/2022_9_10_80891698921607168.jpg','i@liufang.org.cn','$2a$10$RN/EiJh.A73uddHJGh2rgedDOWrZLMB/91f8OPfOSLXOdNtuFtOmW','2022-01-23 17:32:56','2022-01-23 17:32:56',1,195,0),(2,'guest','15833333333','打发十aa',NULL,'https://igogo-test.oss-cn-shenzhen.aliyuncs.com/user/avatar/2022_4_23_30063941451255808.jpg','555i@liufang.org.cn','$2a$10$VeTSOwXxjfSDxcEKQgG9nuWVQRwkp3iS0DVDfB6xgbFHPIqi9dR7m','2022-03-27 20:49:48','2022-03-27 20:49:48',2,3,0),(3,'a','15873164074','a','a','https://igogo-test.oss-cn-shenzhen.aliyuncs.com/user/avatar/2022_4_7_190464236.jpg','a@liufang.org.cn','$2a$10$bPy9GxOynsydi64yULtvfee9zuoCFf0cb/VoXrhnrvoTa3wHOOLky','2022-01-23 17:32:56','2022-01-23 17:32:56',1,195,0),(4,'b','15873164075','b','b','https://igogo-test.oss-cn-shenzhen.aliyuncs.com/user/avatar/2022_4_7_190464236.jpg','b@liufang.org.cn','$2a$10$bPy9GxOynsydi64yULtvfee9zuoCFf0cb/VoXrhnrvoTa3wHOOLky','2022-01-23 17:32:56','2022-01-23 17:32:56',1,195,0),(5,'c','15873164076','c','c','https://igogo-test.oss-cn-shenzhen.aliyuncs.com/user/avatar/2022_4_7_190464236.jpg','c@liufang.org.cn','$2a$10$bPy9GxOynsydi64yULtvfee9zuoCFf0cb/VoXrhnrvoTa3wHOOLky','2022-01-23 17:32:56','2022-01-23 17:32:56',1,195,1);
/*!40000 ALTER TABLE `admin_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `admin_user_login_log`
--

DROP TABLE IF EXISTS `admin_user_login_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `admin_user_login_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `admin_user_id` int(11) DEFAULT NULL COMMENT '后台用户ID',
  `client_ip` varchar(50) DEFAULT NULL COMMENT '客户端IP地址考虑IPv6字段适当放宽',
  `client_user_agent` varchar(1000) DEFAULT NULL COMMENT '客户端软件信息',
  `token` varchar(3000) DEFAULT NULL COMMENT '给用户发放的token',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=76 DEFAULT CHARSET=utf8 COMMENT='用户登录日志';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin_user_login_log`
--

LOCK TABLES `admin_user_login_log` WRITE;
/*!40000 ALTER TABLE `admin_user_login_log` DISABLE KEYS */;
INSERT INTO `admin_user_login_log` VALUES (1,2,'127.0.0.1',NULL,'{\"exp\":\"1651957130539\",\"iat\":\"1650643130538\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOiIyIiwidW5hbWUiOiIxNTgzMzMzMzMzMyIsImV4cCI6MTY1MTk1NzEzMCwiaWF0IjoxNjUwNjQzMTMwfQ.aVewHPw_A81EDZoyJX_Kd-_O8XPyZEvRBTikwSvF5d8VsP4hV_8hNmqd5KJ4faBxLzzdyhi8txa6CUXaf3IFEw\"}','2022-04-22 23:58:50'),(2,2,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.60 Safari/537.36','{\"exp\":\"1651957392536\",\"iat\":\"1650643392536\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOiIyIiwidW5hbWUiOiIxNTgzMzMzMzMzMyIsImV4cCI6MTY1MTk1NzM5MiwiaWF0IjoxNjUwNjQzMzkyfQ.QBec_gRxAzpswCp_JeiW6odw0WAiYtmrDayhfnnaRWYCPxZTVaqRwkDZjP1tmZyzxJY7WOuO2nxuUZ-uiqSzag\"}','2022-04-23 00:03:12'),(3,2,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.60 Safari/537.36','{\"exp\":\"1652051723294\",\"iat\":\"1650737723294\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOiIyIiwidW5hbWUiOiIxNTgzMzMzMzMzMyIsImV4cCI6MTY1MjA1MTcyMywiaWF0IjoxNjUwNzM3NzIzfQ.1KtQf7ApZ7Nn06WS_AeZRSNfzvsBVMTz3nt3Ga3GSUe1POxU7vtK0LD2SlzCK8AW1vyL7CjpzT2ozuSX0yx5Rw\"}','2022-04-24 02:15:23'),(4,1,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.60 Safari/537.36','{\"exp\":\"1652051770033\",\"iat\":\"1650737770033\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOiIxIiwidW5hbWUiOiIxNTg3MzE2NDA3MyIsImV4cCI6MTY1MjA1MTc3MCwiaWF0IjoxNjUwNzM3NzcwfQ.wv3AvAaB2mBqDEA_bWkt10-ITRDPlzyEQEKvSKYtnC2k301NqOK0oYPCM9bhwEynh6L5J9AaK8JGUSbmxiC00Q\"}','2022-04-24 02:16:10'),(5,2,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.60 Safari/537.36','{\"exp\":\"1652051972079\",\"iat\":\"1650737972079\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOiIyIiwidW5hbWUiOiIxNTgzMzMzMzMzMyIsImV4cCI6MTY1MjA1MTk3MiwiaWF0IjoxNjUwNzM3OTcyfQ.k2916oXkE77rKnyy4_rkNtLQHddC60h09PW-ZBviFjtKf1krUKH1p1FvI49lvee1gTTcdsUzK-CRCBfcP3eK_Q\"}','2022-04-24 02:19:32'),(6,2,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.60 Safari/537.36','{\"exp\":\"1652053037705\",\"iat\":\"1650739037705\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOiIyIiwidW5hbWUiOiIxNTgzMzMzMzMzMyIsImV4cCI6MTY1MjA1MzAzNywiaWF0IjoxNjUwNzM5MDM3fQ.quVur9doelmCaVjxdDND2z1x9U1u4Ge-i6g_u0a7fqCiY3C356rvgiYo9n8TqGb0NGyWk953KopcGqOL00HT2w\"}','2022-04-24 02:37:17'),(7,2,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.60 Safari/537.36','{\"exp\":\"1652053123066\",\"iat\":\"1650739123066\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOiIyIiwidW5hbWUiOiIxNTgzMzMzMzMzMyIsImV4cCI6MTY1MjA1MzEyMywiaWF0IjoxNjUwNzM5MTIzfQ.tRURAjyVUtpIW58b7GidRu_SsC3JZCvID30fylsFPmOv_PrENX4PIOBu6rS-EBKpjYPdQ9kR5kmJ8WYRrlDAWg\"}','2022-04-24 02:38:43'),(8,2,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.60 Safari/537.36','{\"exp\":\"1652053325480\",\"iat\":\"1650739325480\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOiIyIiwidW5hbWUiOiIxNTgzMzMzMzMzMyIsImV4cCI6MTY1MjA1MzMyNSwiaWF0IjoxNjUwNzM5MzI1fQ.rXEdpDbLO1EOYgLq3V-H2ZzUaZaWxvnbi87sC1ZdlNjwmE8me0a70zBsqx8r74oJ7rIRoRwQGFeKHluYuFUOnw\"}','2022-04-24 02:42:05'),(9,1,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.60 Safari/537.36','{\"exp\":\"1652305486582\",\"iat\":\"1650991486582\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NTIzMDU0ODYsImlhdCI6MTY1MDk5MTQ4Nn0.fKhDHIwndIMMDuYttkWt6EAHYAAodoTNRWZu3Qyvif-uL3eSEhKx1XbI02qU_eWfsGdZi7EdbjdvuCJFPCGfYw\"}','2022-04-27 00:44:46'),(10,1,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.60 Safari/537.36','{\"exp\":\"1652305830226\",\"iat\":\"1650991830226\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NTIzMDU4MzAsImlhdCI6MTY1MDk5MTgzMH0.HIcJXqAumHIt3uCR_nPNGq3rHCMUK9OvBGL2eNC_ko0NhOZ2pk7qHdQNmD9Pt5LYWxethLGMKYrHXXNY88J1iA\"}','2022-04-27 00:50:30'),(11,1,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.60 Safari/537.36','{\"exp\":\"1652308891261\",\"iat\":\"1650994891261\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NTIzMDg4OTEsImlhdCI6MTY1MDk5NDg5MX0.K6uTqgujprLvopWg3Z_Ob_-gFVk5kzijFnUGgqIXU_-A_nRLSahVUbbpaHBGMcjKjJ8nL6S5jpIM9wKG9Lx0JQ\"}','2022-04-27 01:41:31'),(12,1,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.60 Safari/537.36','{\"exp\":\"1652309030481\",\"iat\":\"1650995030481\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NTIzMDkwMzAsImlhdCI6MTY1MDk5NTAzMH0.07X6VRwFmi1VJf6jpJyuLmonk-5VYUOFTWRRAvwzbWQ2mnvgkbAl5KMEZKM16yuMD9taMgPdLUofye-dnmD6JQ\"}','2022-04-27 01:43:50'),(13,1,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.60 Safari/537.36','{\"exp\":\"1652645588797\",\"iat\":\"1651331588797\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NTI2NDU1ODgsImlhdCI6MTY1MTMzMTU4OH0.PJqlgWee5dsUWnWDjAbORw9Uib242MOV6C3OssdpfPlVzK_gsHxnpckC1BU2UvwmseduFiM6FG_6PjRba3zhPg\"}','2022-04-30 23:13:08'),(14,1,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.127 Safari/537.36','{\"exp\":\"1652705411008\",\"iat\":\"1651391411008\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NTI3MDU0MTEsImlhdCI6MTY1MTM5MTQxMX0.v6p0HVAKyU3yrMnAlQcVA5y15IP1pfgZNJzi56DgMizOpcnVWY_NopVH574Nb6ap-U3i4PETVTUdNGf9GDv78w\"}','2022-05-01 15:50:11'),(15,1,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.127 Safari/537.36','{\"exp\":\"1652705410937\",\"iat\":\"1651391410937\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NTI3MDU0MTAsImlhdCI6MTY1MTM5MTQxMH0.f4ckwGCv8BRxMvgLJPQLdAI94yIA5CBUaAl1mJG4ipWptuFcO8DitLw5mPlM1L5Vk2V0XuGponRUrPUWm_-Ofg\"}','2022-05-01 15:50:10'),(16,2,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.127 Safari/537.36','{\"exp\":\"1652907071351\",\"iat\":\"1651593071351\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjIsInVuYW1lIjoiMTU4MzMzMzMzMzMiLCJleHAiOjE2NTI5MDcwNzEsImlhdCI6MTY1MTU5MzA3MX0.MQr9RW2la1cZ_camVTDmd2hT4ZHl7s3oSSXG6jn-D5fMp4X36CF8wBpcHfS0VrgMGawxoWLwpIkUwjxPYJxLGg\"}','2022-05-03 23:51:11'),(17,2,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.127 Safari/537.36','{\"exp\":\"1652907071351\",\"iat\":\"1651593071351\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjIsInVuYW1lIjoiMTU4MzMzMzMzMzMiLCJleHAiOjE2NTI5MDcwNzEsImlhdCI6MTY1MTU5MzA3MX0.MQr9RW2la1cZ_camVTDmd2hT4ZHl7s3oSSXG6jn-D5fMp4X36CF8wBpcHfS0VrgMGawxoWLwpIkUwjxPYJxLGg\"}','2022-05-03 23:51:11'),(18,1,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.127 Safari/537.36','{\"exp\":\"1652995010089\",\"iat\":\"1651681010089\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NTI5OTUwMTAsImlhdCI6MTY1MTY4MTAxMH0.gOsCWGxSXuAQSeRwP4bVeTBQKyDta_kq8AC-eknNOOb9D7LXihiPmmQsMr_GYyrFHjnRWSwSxX6JxLH7Lk9anw\"}','2022-05-05 00:16:50'),(19,1,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.127 Safari/537.36','{\"exp\":\"1652998589923\",\"iat\":\"1651684589923\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NTI5OTg1ODksImlhdCI6MTY1MTY4NDU4OX0._44ZnzmpRmrkyFL-NvZeAaOsefYcok460jWb2hdgUhOMssTYEHfhy1kQgEbRv5Uum2TCQLyfv3cLpzA1FMLsTg\"}','2022-05-05 01:16:29'),(20,1,'127.0.0.1',NULL,'{\"exp\":\"1653508421599\",\"iat\":\"1652194421599\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NTM1MDg0MjEsImlhdCI6MTY1MjE5NDQyMX0.8W1fv9qkJn1kYRdvcwdMuCZBt86QQAMfE7T1TL6Wsx2YNEcuC3h9QMnqNG2QHCOnuRdzASzivGXY0qa2TU1mCw\"}','2022-05-10 22:53:41'),(21,1,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.127 Safari/537.36','{\"exp\":\"1653510190838\",\"iat\":\"1652196190838\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NTM1MTAxOTAsImlhdCI6MTY1MjE5NjE5MH0.BotqJXanOfUKzjlUOSn7YkdKMjyquC3wjj973Zn1nNg-lgi0K65WPZ3NFi8AXm-O66VqrNI-Un9PPylRJlNM1A\"}','2022-05-10 23:23:10'),(22,1,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.127 Safari/537.36','{\"exp\":\"1653510345999\",\"iat\":\"1652196345999\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NTM1MTAzNDUsImlhdCI6MTY1MjE5NjM0NX0.FjlhpyGisD1T7qNBhb0kEgOuPq9QVezq7LubzdmSHb61VaZ5sBmv2ySVeCGVb6z9e1qvh8BonX4QxlfADb6UHg\"}','2022-05-10 23:25:45'),(23,1,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.127 Safari/537.36','{\"exp\":\"1653510691332\",\"iat\":\"1652196691332\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NTM1MTA2OTEsImlhdCI6MTY1MjE5NjY5MX0.lVHnuyTiaB5ueRw0YcY_1QqKt5pi6Op1-620WgFiO9AwEY4_v2Y2W4dgvprUtJvRbGYoZFe-XDrrUigVmmPM1A\"}','2022-05-10 23:31:31'),(24,1,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.127 Safari/537.36','{\"exp\":\"1653510777754\",\"iat\":\"1652196777754\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NTM1MTA3NzcsImlhdCI6MTY1MjE5Njc3N30.KEBkJQoXxjroh59eyIdkOtDCbhDFZk5QfjqUqfhVIP7gi2T9HPkhuTmIG91auo41_GkEVJDXPSKkh-YNslv04g\"}','2022-05-10 23:32:57'),(25,2,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.127 Safari/537.36','{\"exp\":\"1654290130656\",\"iat\":\"1652976130656\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjIsInVuYW1lIjoiMTU4MzMzMzMzMzMiLCJleHAiOjE2NTQyOTAxMzAsImlhdCI6MTY1Mjk3NjEzMH0.DBoQkPtCVw6rWiuTXzKon8rb0DmwHVmOOAj5Fm7n_4q3ngP47Uxgd-PRP7IOD4XJA0UR3iYxFWyMu7oZtfGVAQ\"}','2022-05-20 00:02:10'),(26,2,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.127 Safari/537.36','{\"exp\":\"1654290130720\",\"iat\":\"1652976130720\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjIsInVuYW1lIjoiMTU4MzMzMzMzMzMiLCJleHAiOjE2NTQyOTAxMzAsImlhdCI6MTY1Mjk3NjEzMH0.DBoQkPtCVw6rWiuTXzKon8rb0DmwHVmOOAj5Fm7n_4q3ngP47Uxgd-PRP7IOD4XJA0UR3iYxFWyMu7oZtfGVAQ\"}','2022-05-20 00:02:10'),(27,1,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.127 Safari/537.36','{\"exp\":\"1654626727174\",\"iat\":\"1653312727174\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NTQ2MjY3MjcsImlhdCI6MTY1MzMxMjcyN30.Q2YZbpALMUUQusCWCtYpDbUzTQh86p1yPXspyJxth9u8Dsfd5fiK0z_gEv2BMI3f2oqJDxc7N0CuTg4x0DVrww\"}','2022-05-23 21:32:07'),(28,1,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.127 Safari/537.36','{\"exp\":\"1654850702358\",\"iat\":\"1653536702358\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NTQ4NTA3MDIsImlhdCI6MTY1MzUzNjcwMn0.OA2leLWcRXOJxsQbcHlIf4ds3D9nHd19nP-NEZf1LbxV1mEVNCD5le-vEycEwG3oJRC5j_dwvLwmDretYYffSg\"}','2022-05-26 11:45:02'),(29,1,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.127 Safari/537.36','{\"exp\":\"1654967430473\",\"iat\":\"1653653430473\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NTQ5Njc0MzAsImlhdCI6MTY1MzY1MzQzMH0.b_wi8yDeB9ptv5b3LMiE24RinOYuvD3xE_BZ2y98bPAJ94lmL4tG2GTJBiqIJ-Y6DPub_oDprkyQz-fRRNRDLg\"}','2022-05-27 20:10:30'),(30,1,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.127 Safari/537.36','{\"exp\":\"1654973844079\",\"iat\":\"1653659844079\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NTQ5NzM4NDQsImlhdCI6MTY1MzY1OTg0NH0._Aal9cM7oA32Lo_w9va_uTAV9yQ4Zl2VZeTiRWexgCNbWkzBISRyJZNXIu0tGNfq-5Jod2wGNQ1HTF-UcwM5aA\"}','2022-05-27 21:57:24'),(31,2,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.127 Safari/537.36','{\"exp\":\"1654979575824\",\"iat\":\"1653665575824\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjIsInVuYW1lIjoiMTU4MzMzMzMzMzMiLCJleHAiOjE2NTQ5Nzk1NzUsImlhdCI6MTY1MzY2NTU3NX0.j3wakE3HVqhHP20My4354ZmJO9EFXW47JvZNV9Zp39Iwu42gXLGyucQefdd8zIUb5Zw-hvAiQTMtp7zjcuf0fQ\"}','2022-05-27 23:32:55'),(32,1,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.127 Safari/537.36','{\"exp\":\"1655025056477\",\"iat\":\"1653711056477\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NTUwMjUwNTYsImlhdCI6MTY1MzcxMTA1Nn0.mXB_YY_4VwPN9iU7KltThVHlEeilYhEEBNCeB3w15pt83x0R4uBob2qJP-dH8U2kUm8GSHrDL_2Tf2kG3Uzcag\"}','2022-05-28 12:10:56'),(33,1,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.127 Safari/537.36','{\"exp\":\"1655230476251\",\"iat\":\"1653916476251\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NTUyMzA0NzYsImlhdCI6MTY1MzkxNjQ3Nn0.883eNjbq5Tx9FY8uEOD5FMnIFkGnM__lLMydvBl0A1bIoVReZkVpvWGWzTR43jzWXiQ7NikQNbg2KQBCvI787g\"}','2022-05-30 21:14:36'),(34,1,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36','{\"exp\":\"1660008564831\",\"iat\":\"1658694564831\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NjAwMDg1NjQsImlhdCI6MTY1ODY5NDU2NH0.rthnoQYFzmZfbW6sMMvs3n-MSGPaWJ1-4fFwDB5aEKvumDSTtvgWFGFQfkRYCmGpDuZ5RWo7aHuGHnnLzbwlcQ\"}','2022-07-25 04:29:24'),(35,1,'127.0.0.1',NULL,'{\"exp\":\"1660594899222\",\"iat\":\"1659280899222\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NjA1OTQ4OTgsImlhdCI6MTY1OTI4MDg5OH0.qlym1jOhFfPVLnfGWGPsWAs6B6Vrxz9UyfZlB7N3m1qJYHnShMim95-dks9xHbBQ2O47x8J5znrZQp-5gKr4qg\"}','2022-07-31 23:21:39'),(36,1,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Safari/537.36 SLBrowser/8.0.0.7271 SLBChan/105','{\"exp\":\"1660656117286\",\"iat\":\"1659342117286\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NjA2NTYxMTcsImlhdCI6MTY1OTM0MjExN30.Y_5QR1a5xng_SLLvQEaDChLAnU8Yy0YgV4hb_AJAxIdICE3Lb6o4Qy7Lzegv6vuEOgyBE8CQAGcbhHNaFUFv3Q\"}','2022-08-01 16:21:57'),(37,1,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Safari/537.36 SLBrowser/8.0.0.7271 SLBChan/105','{\"exp\":\"1660656118776\",\"iat\":\"1659342118776\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NjA2NTYxMTgsImlhdCI6MTY1OTM0MjExOH0.ivMrM3nEhDti3_4jUKz5k2Cw9fpZciugeuusVc-Ad6GZ26-deNZ4nBohbUTpDRsyO87xmWI4Z36TGtM4BrQRAw\"}','2022-08-01 16:21:58'),(38,1,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Safari/537.36 SLBrowser/8.0.0.7271 SLBChan/105','{\"exp\":\"1660658426230\",\"iat\":\"1659344426230\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NjA2NTg0MjYsImlhdCI6MTY1OTM0NDQyNn0.hLdL7idrdwNttxQnrJaSi19t7mXw_qLsIwvwGCqAj2IkYztpq1jUArthcELH_Z-M7I4of6g2Y85snc0kczt7Jw\"}','2022-08-01 17:00:26'),(39,1,'127.0.0.1',NULL,'{\"exp\":\"1663028428958\",\"iat\":\"1661714428958\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NjMwMjg0MjgsImlhdCI6MTY2MTcxNDQyOH0.r27kaXifpgvB_x_Pa-Fa2KbnMQzbdQWb6trukQNjoJc_yYigZ-pCSDwHItET0_qZM3VzFvDBBwj7VPQey_FD4g\"}','2022-08-29 03:20:28'),(40,1,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Safari/537.36 SLBrowser/8.0.0.7271 SLBChan/105','{\"exp\":\"1663471284137\",\"iat\":\"1662157284137\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NjM0NzEyODQsImlhdCI6MTY2MjE1NzI4NH0.KIi0SHrtAiGLAuUcddDpAZbCY5muW3q5hBtTqtN_SzVIg9CbvxDkH9ylLvHojFJImOZKXyQEPd0YiSVE_laIPQ\"}','2022-09-03 06:21:24'),(41,1,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Safari/537.36 SLBrowser/8.0.0.7271 SLBChan/105','{\"exp\":\"1663713935093\",\"iat\":\"1662399935093\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NjM3MTM5MzUsImlhdCI6MTY2MjM5OTkzNX0.UchSSAVeopFXUN4Kr_EufDOpXXxBUhCJjwr5pbeL6r70cI7mm_qlA63fqtRSeBDXouxYSWjH5f_b6lTJ5_1ZNA\"}','2022-09-06 01:45:35'),(42,1,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36','{\"exp\":\"1664055570569\",\"iat\":\"1662741570569\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NjQwNTU1NzAsImlhdCI6MTY2Mjc0MTU3MH0.dw7O5Eo0N6VOPPceYt3gPS-2Vjq7o2Ua7j-p7esOX6CoZ3kGVejV_CHK_aOQzADZuhIoCz8Gd9KHwwUGgKLnDg\"}','2022-09-10 00:39:30'),(43,1,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36','{\"exp\":\"1664391929815\",\"iat\":\"1663077929815\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NjQzOTE5MjksImlhdCI6MTY2MzA3NzkyOX0.SYL7DVLuUpaJzw74MRbZOGBiA_RVnxafnguMghIVHCtsWuU-y78-WMhuR-pFgaCoE17vq6fe3Gy5mn-_5AS-RA\"}','2022-09-13 22:05:29'),(44,1,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36','{\"exp\":\"1665614964193\",\"iat\":\"1664300964193\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NjU2MTQ5NjMsImlhdCI6MTY2NDMwMDk2M30.wNz2gVOSuROvf7hX7swVr5yR3fMqCBHzrmwjnSW0bqMOjtfAhV3JJf4ldUYsyXupkfZECf-_4mIdOdNf1iL9lg\"}','2022-09-28 01:49:24'),(45,1,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/106.0.0.0 Safari/537.36','{\"exp\":\"1668796684977\",\"iat\":\"1667482684977\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2Njg3OTY2ODQsImlhdCI6MTY2NzQ4MjY4NH0.VEDcuHs9MZnnPx-RXBEVs99jiobvscPrIW5So3VvKBX3dlrpzUklY1aNCTrUCJQN9sgEZ15-hmA6Z9LgLOBIhA\"}','2022-11-03 21:38:04'),(46,1,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36','{\"exp\":\"1669053540772\",\"iat\":\"1667739540772\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NjkwNTM1NDAsImlhdCI6MTY2NzczOTU0MH0.XRM8wsSCW299coHD7sohq_Dk2brrVRQB836849iZotgiojWrOxSKAmd9YJ7utQTaeyGknp25c58ZZY0FMeLkIA\"}','2022-11-06 20:59:00'),(47,1,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36','{\"exp\":\"1669054734874\",\"iat\":\"1667740734874\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NjkwNTQ3MzQsImlhdCI6MTY2Nzc0MDczNH0._s4RUfJZVQexQdzhN-S8SxJowMAaref98-gbVo8-8epAWOEUYA0Pgq6dZHIxH2qwC3fmGkqR5TIkeJ8xseeBow\"}','2022-11-06 21:18:54'),(48,1,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36','{\"exp\":\"1669054797886\",\"iat\":\"1667740797886\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NjkwNTQ3OTcsImlhdCI6MTY2Nzc0MDc5N30.2kRMQ1CjLD10YLN6MhamMsX6rCLpf1QJgVBxjlXMXs6xvHnIu4ZN3LmqLzaYfxLMsUXvyRUXKpxQE6SjXMqeTA\"}','2022-11-06 21:19:57'),(49,1,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36','{\"exp\":\"1669054836660\",\"iat\":\"1667740836660\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NjkwNTQ4MzYsImlhdCI6MTY2Nzc0MDgzNn0.WdRcPXl_hxQKdqnYayO03-3PITMnmFXJVZkcZlI1tkd7FoUI5-nealjX_Nscl2d-WnCAROiBox9k75NEwWcrlQ\"}','2022-11-06 21:20:36'),(50,1,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36','{\"exp\":\"1669147878329\",\"iat\":\"1667833878329\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NjkxNDc4NzgsImlhdCI6MTY2NzgzMzg3OH0.qHMjGxENzGWQNygXCHhzG33dRPyuxBIuzeOeNc-Em-cYBCRQw7qjbhW3Nq01eDsB_w9F-Ej9j0obUP3LwkUMxg\"}','2022-11-07 23:11:18'),(51,1,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36','{\"exp\":\"1669695305565\",\"iat\":\"1668381305564\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2Njk2OTUzMDUsImlhdCI6MTY2ODM4MTMwNX0.LuNeH2U8tuqL3COBPn3iMNK7z2BwWTBB54VKFXrRyHtEZQzPERxTS0yfB6q1pUeH8ELptPkQXLrvr3HJ3aZM3Q\"}','2022-11-14 07:15:05'),(52,1,'127.0.0.1','Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36','{\"exp\":\"1669758641213\",\"iat\":\"1668444641213\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2Njk3NTg2NDAsImlhdCI6MTY2ODQ0NDY0MH0.kVJ4MQIGGfeRMgO-ENnoRpw8wNAZ6lxUas1fnVknZUQwwBqrY8bQQ2fxnLrKr3vlKLrNRPSHyaSYqvFyOqQgtA\"}','2022-11-15 00:50:41'),(53,1,'127.0.0.1','Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36','{\"exp\":\"1669886667568\",\"iat\":\"1668572667568\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2Njk4ODY2NjcsImlhdCI6MTY2ODU3MjY2N30.BDJ5nfkNkf8Lm3KsRCrtVJX3ATGB7nabe2jc4-O_N_yzAmdV1mvrumo8hFGJ75kPjBCsyX4pfm_r2i782Um5NQ\"}','2022-11-16 12:24:27'),(54,1,'127.0.0.1','Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36','{\"exp\":\"1670083117167\",\"iat\":\"1668769117167\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NzAwODMxMTcsImlhdCI6MTY2ODc2OTExN30.Q0LUoQAzHBDY8fA6e_nSSbfCYOqUI0HyMLk6Cm47O5z0bwf2zXoIrZTXh6hDxU9DJlfezm08N7hG9qEE1qQrkg\"}','2022-11-18 18:58:37'),(55,1,'127.0.0.1','Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:107.0) Gecko/20100101 Firefox/107.0','{\"exp\":\"1670452757630\",\"iat\":\"1669138757630\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NzA0NTI3NTcsImlhdCI6MTY2OTEzODc1N30.TlU599otXKMweT1AQGLrJygKi3nDq0uRPzVXDfU8eNrHvv15RJhfw6Bo3iBohCYS1aX7WaJLAMUO27FcC7itPw\"}','2022-11-23 01:39:17'),(56,1,'127.0.0.1','Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36','{\"exp\":\"1670848479702\",\"iat\":\"1669534479702\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NzA4NDg0NzksImlhdCI6MTY2OTUzNDQ3OX0.scmcinwcy_iUxFtAH0EAYJKmzRQVcNXd1oVxi3J9MrhMkr5a98P9QK2MFzVNgDan6zhG9NSBX1zcmOaIC9X5ZA\"}','2022-11-27 15:34:39'),(57,1,'127.0.0.1','Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36','{\"exp\":\"1670848685196\",\"iat\":\"1669534685196\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NzA4NDg2ODUsImlhdCI6MTY2OTUzNDY4NX0.wM4-ocqoZWbCnX7IpU1ljhC3R0yEhXXkcsZaLg1Cu4aOy5CIRk8rOc7FaaEcBNQxie1NCRGuzBLP46h3sl1nug\"}','2022-11-27 15:38:05'),(58,1,'127.0.0.1','Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36','{\"exp\":\"1670880467263\",\"iat\":\"1669566467263\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NzA4ODA0NjcsImlhdCI6MTY2OTU2NjQ2N30.kr61J-jeHR0ZIXFvA7kcZ0uflhKbteGMtlkFoRVr3ffnRwrQ7YLcuZVolLTasEYCAAi5IeTB3OU_Qe29EQYX7A\"}','2022-11-28 00:27:47'),(59,1,'127.0.0.1','Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36','{\"exp\":\"1670880496308\",\"iat\":\"1669566496308\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NzA4ODA0OTYsImlhdCI6MTY2OTU2NjQ5Nn0.H6FpqNiIipejkNATIBU6Jv12oRTyXBX2ITNr814SHdyoL7F7yzCduf_FD9ZyAgjduhY7WiXN1ukYi6iHKsxDew\"}','2022-11-28 00:28:16'),(60,1,'127.0.0.1','Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36','{\"exp\":\"1670882300551\",\"iat\":\"1669568300551\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NzA4ODIzMDAsImlhdCI6MTY2OTU2ODMwMH0.aHIfoyYsLph4ZSn2Pf21USMPIjGs3qrmhZ4cke9EttH3pqmVW-6OX4KvUbS_w-pyVR54k_DDkm2jnkDbfPaS-w\"}','2022-11-28 00:58:20'),(61,1,'127.0.0.1','Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36','{\"exp\":\"1670882378269\",\"iat\":\"1669568378269\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NzA4ODIzNzgsImlhdCI6MTY2OTU2ODM3OH0.26fvEdEjseJgKwKaicFfopKKSO3B6WIFXfyQGEGI5OEP8JKCPsK-4rBiv_S_C9qcvB2mcJhrEK9CA-sbozYnYA\"}','2022-11-28 00:59:38'),(62,1,'127.0.0.1','Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36','{\"exp\":\"1670882455989\",\"iat\":\"1669568455989\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NzA4ODI0NTUsImlhdCI6MTY2OTU2ODQ1NX0.kLQwFSWiWmPvMoWYPYdadzISDk1Ez2ruEAtnq1Emlf6asPANyrc6VIIo92CeJGk5V2cghTRWyRLaaSugxOc53g\"}','2022-11-28 01:00:55'),(63,1,'127.0.0.1','Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36','{\"exp\":\"1671063320060\",\"iat\":\"1669749320060\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NzEwNjMzMTksImlhdCI6MTY2OTc0OTMxOX0.viGTU_4yzMcDuczOCNaJGZlqVJwzaby6KNBpyPPWS_tCseFkmXHWFCzGqRqyMB53svRbNacSAcowXTBR0HoZsQ\"}','2022-11-30 03:15:20'),(64,1,'127.0.0.1','Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36','{\"exp\":\"1671063762935\",\"iat\":\"1669749762935\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NzEwNjM3NjIsImlhdCI6MTY2OTc0OTc2Mn0.Rdt1ukW7QfTexh0LyLoQ5WRcOf8rR3dkdfGUG6Yi6nX9qDw2xGLkVfiV9uEMXYjSOBKD_cUh15ADoIeWPoIvmw\"}','2022-11-30 03:22:42'),(65,1,'127.0.0.1','Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36','{\"exp\":\"1671063828013\",\"iat\":\"1669749828013\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NzEwNjM4MjgsImlhdCI6MTY2OTc0OTgyOH0.Bh1j05SWinQUZJUyD9ZeeiX9Un3scurgFGDS_UQbWzX49NZUeX0fge_vM1M8t82btAuhp7mm8XxqCCAxhY8XqA\"}','2022-11-30 03:23:48'),(66,1,'127.0.0.1','Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36','{\"exp\":\"1671106068078\",\"iat\":\"1669792068078\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NzExMDYwNjgsImlhdCI6MTY2OTc5MjA2OH0.5-uMwrLrkwFou7DiCVlpEafVWpiOEsdRgs4TJiQ9jPTuNisJitaIv7J8RegB4U_MM1meiJg-Qb_gIvjRHpwI2g\"}','2022-11-30 15:07:48'),(67,1,'127.0.0.1','Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36','{\"exp\":\"1671106229227\",\"iat\":\"1669792229227\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NzExMDYyMjksImlhdCI6MTY2OTc5MjIyOX0._r50Px-8SORepOdxxaxFOQKhkOIHkp961d_flvfwzXRcm_86eTqtnUZGsz4CX72rO5CNvB1L9q9YeWsVeMtnBQ\"}','2022-11-30 15:10:29'),(68,1,'127.0.0.1','Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36','{\"exp\":\"1671671961779\",\"iat\":\"1670357961779\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NzE2NzE5NjEsImlhdCI6MTY3MDM1Nzk2MX0.i4ZF5pXjXMr31UW9Z2U0xRTyrPJdaUEi4TzSmZ2L-YB5x_dR6YkaGwhtFzIe1Ayn7Cl0VXVmNakyJY75P_6yBQ\"}','2022-12-07 04:19:21'),(69,1,'127.0.0.1','Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36','{\"exp\":\"1671672088079\",\"iat\":\"1670358088079\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NzE2NzIwODgsImlhdCI6MTY3MDM1ODA4OH0.sWMY4-qJ741oAY5I7IPgbQ1fThc2IAzLYOE3lwaTC-P-NjliaOTNFqIsM78gU3EYWZTuTq0I_IxFoQWp-NJd3w\"}','2022-12-07 04:21:28'),(70,1,'127.0.0.1','Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36','{\"exp\":\"1671706229045\",\"iat\":\"1670392229045\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NzE3MDYyMjksImlhdCI6MTY3MDM5MjIyOX0.buWzvpeOdHAy9yZwQ03h_5j1ku0lLncD6KJbH6_5mh9SSmbbXdLyo6e6bAIThILVT_gbAHafxLUdBccbhKaBMw\"}','2022-12-07 13:50:29'),(71,1,'127.0.0.1','Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36','{\"exp\":\"1671708223328\",\"iat\":\"1670394223328\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NzE3MDgyMjMsImlhdCI6MTY3MDM5NDIyM30.UPN5yzr1WBsguwGwpkkN-klqKBrQSuUMwkEqEFBL-r8mrWJZMHttDv-yTdqlVPprkkYkMJ_6bKqqVDrZBnAM8w\"}','2022-12-07 14:23:43'),(72,1,'127.0.0.1','Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36','{\"exp\":\"1671744872581\",\"iat\":\"1670430872581\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NzE3NDQ4NzIsImlhdCI6MTY3MDQzMDg3Mn0.fKskxtM06Y7fW0USrvnIgsAl7JtlYhMeWkSA1Oy4gS4Js6MXVttmoXjpQF-dAEmBlXxulFKB9vo4M_lINgnZvQ\"}','2022-12-08 00:34:32'),(73,1,'127.0.0.1','Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36','{\"exp\":\"1672151901919\",\"iat\":\"1670837901919\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NzIxNTE5MDEsImlhdCI6MTY3MDgzNzkwMX0.osdy1eWuC8cWJZQUdAWV9ANiN-Cuk8MnGUknLawB1tOlxcDIFy8NEF1lHoGeKUIrKbbb51YEUcxvgiDUMFfR_w\"}','2022-12-12 17:38:21'),(74,1,'192.168.0.6','Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4692.71 Safari/537.36','{\"exp\":\"1672234129997\",\"iat\":\"1670920129997\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NzIyMzQxMjksImlhdCI6MTY3MDkyMDEyOX0.VMogqGSApR140YlbUOqvZm9ZwjI16Es8xLrHRkSgRb2-ax426UCgdpb8l5lAZSfJKvSC_rWD6rON-BkJexCayg\"}','2022-12-13 16:28:49'),(75,1,'192.168.0.7','Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36','{\"exp\":\"1672543203396\",\"iat\":\"1671229203396\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2NzI1NDMyMDMsImlhdCI6MTY3MTIyOTIwM30.atEhTnUmH5gKfhkrJp8j7CsRr0LYyDtq8hPJSm2F4IowFxisJhqw7QvRonFrGaYgwAzO62h6W7hLj2d5APWrvA\"}','2022-12-17 06:20:03');
/*!40000 ALTER TABLE `admin_user_login_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `approval_process`
--

DROP TABLE IF EXISTS `approval_process`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
INSERT INTO `approval_process` VALUES (1,'1','请假申请',1,'',10,'[{\"key\":\"CONTENT\",\"title\":\"请假事由\",\"type\":\"textarea\",\"tips\":\"请假事由详细描述\"},{\"key\":\"START_TIME\",\"title\":\"开始时间\",\"type\":\"datetime\",\"tips\":\"请假开始时间\"},{\"key\":\"END_TIME\",\"title\":\"结束时间\",\"type\":\"datetime\",\"tips\":\"请假结束时间\"}]','2022-06-01 11:27:11'),(2,'2','调休申请',1,'',10,'[\n{\"key\":\"CONTENT\", \"title\":\"申请描述\"},\n{\"key\":\"START_TIME\", \"title\":\"开始时间\"},\n{\"key\":\"END_TIME\", \"title\":\"结束时间\"}\n]','2022-05-29 01:15:20'),(3,'3','印章申请',1,'',10,'[{\"key\":\"CONTENT\",\"title\":\"申请描述\",\"type\":\"textarea\",\"tips\":\"用章事由详细\"},{\"key\":\"START_TIME\",\"title\":\"开始时间\",\"type\":\"datetime\",\"tips\":\"用章开始时间\"},{\"key\":\"END_TIME\",\"title\":\"结束时间\",\"type\":\"datetime\",\"tips\":\"用章结束时间\"}]','2022-06-01 11:26:37');
/*!40000 ALTER TABLE `approval_process` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `approval_process_node`
--

DROP TABLE IF EXISTS `approval_process_node`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `approval_process_node` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `approval_process_id` int(11) DEFAULT NULL COMMENT '审批流ID',
  `source_user_id` int(11) DEFAULT NULL COMMENT '源用户ID； 上一节点ID',
  `user_id` int(11) DEFAULT NULL COMMENT '审批用户ID',
  `serial_number` int(11) DEFAULT NULL COMMENT '序列号； 决定审批顺序',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8 COMMENT='审批流节点';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `approval_process_node`
--

LOCK TABLES `approval_process_node` WRITE;
/*!40000 ALTER TABLE `approval_process_node` DISABLE KEYS */;
INSERT INTO `approval_process_node` VALUES (3,2,0,1,0,'2022-05-29 01:15:20'),(4,2,1,2,1,'2022-05-29 01:15:20'),(13,3,0,1,0,'2022-06-01 11:26:37'),(14,3,1,2,1,'2022-06-01 11:26:37'),(15,1,0,1,0,'2022-06-01 11:27:11'),(16,1,1,2,1,'2022-06-01 11:27:12');
/*!40000 ALTER TABLE `approval_process_node` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `approval_process_order`
--

DROP TABLE IF EXISTS `approval_process_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8 COMMENT='审批单';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `approval_process_order`
--

LOCK TABLES `approval_process_order` WRITE;
/*!40000 ALTER TABLE `approval_process_order` DISABLE KEYS */;
INSERT INTO `approval_process_order` VALUES (1,3,1,'43119611092144128',0,0,'2022-05-29 01:20:15','[{\"key\":\"CONTENT\",\"title\":\"显示标题\",\"content\":\"新增合同， 申请盖章\"}]'),(2,1,1,'43119734970912768',0,0,'2022-05-29 01:20:44','[{\"key\":\"CONTENT\",\"title\":\"显示标题\",\"content\":\"家里有事， 请假一天\"}]'),(3,1,1,'43320174387924992',0,0,'2022-05-29 14:37:14','[{\"key\":\"CONTENT\",\"title\":\"显示标题\",\"content\":\"惆怅长岑长错错\"},{\"key\":\"START_TIME\",\"title\":\"显示标题\",\"content\":\"2022-05-10 00:00:00\"},{\"key\":\"END_TIME\",\"title\":\"显示标题\",\"content\":\"2022-05-29 00:00:00\"}]'),(4,1,1,'43328528598437888',0,0,'2022-05-29 15:10:26','[{\"key\":\"START_TIME\",\"title\":\"显示标题\",\"content\":\"2022-05-29 00:00:00\"},{\"key\":\"END_TIME\",\"title\":\"显示标题\",\"content\":\"2022-05-29 00:00:00\"}]'),(5,1,1,'43338500841410560',0,0,'2022-05-29 15:50:03','[{\"key\":\"START_TIME\",\"title\":\"显示标题\",\"content\":\"2022-05-13 00:00:00\"},{\"key\":\"END_TIME\",\"title\":\"显示标题\",\"content\":\"2022-05-29 00:00:00\"}]'),(6,1,1,'43355288803217408',0,0,'2022-05-29 16:56:48','[{\"key\":\"CONTENT\",\"title\":\"申请描述\",\"content\":\"热热热热热若若若若若从\"},{\"key\":\"START_TIME\",\"title\":\"开始时间\",\"content\":\"2022-05-20 00:00:00\"},{\"key\":\"END_TIME\",\"title\":\"结束时间\",\"content\":\"2022-05-29 00:00:00\"}]'),(7,1,1,'43376085097779200',1,1,'2022-05-29 18:19:24','[{\"key\":\"CONTENT\",\"title\":\"申请描述\",\"content\":\"家里有事， 请假三天；望领导批准\"},{\"key\":\"START_TIME\",\"title\":\"开始时间\",\"content\":\"2022-05-29 00:00:00\"},{\"key\":\"END_TIME\",\"title\":\"结束时间\",\"content\":\"2022-05-31 00:00:00\"}]'),(8,1,1,'45116188841218048',0,0,'2022-06-03 13:33:57','[{\"key\":\"TITLE\",\"title\":\"标题\",\"content\":\"这里是测试标题\"},{\"key\":\"CONTENT\",\"title\":\"内容\",\"content\":\"这里是测试审批内容\"}]'),(9,1,1,'45116264292552704',0,0,'2022-06-03 13:34:15','[{\"key\":\"TITLE\",\"title\":\"标题\",\"content\":\"这里是测试标题\"},{\"key\":\"CONTENT\",\"title\":\"内容\",\"content\":\"这里是测试审批内容\"}]'),(10,1,1,'45117007200260096',0,0,'2022-06-03 13:37:12','[{\"key\":\"CONTENT\",\"title\":\"内容\",\"content\":\"这里是测试审批内容\"},{\"key\":\"START_TIME\",\"title\":\"开始时间\",\"content\":\"2022-06-03 00:00:00\"},{\"key\":\"END_TIME\",\"title\":\"结束时间\",\"content\":\"2023-06-03 00:00:00\"}]'),(11,1,1,'45117229670338560',0,0,'2022-06-03 13:38:05','[{\"key\":\"CONTENT\",\"title\":\"内容\",\"content\":\"世界这么大，我想去看看！\"},{\"key\":\"START_TIME\",\"title\":\"开始时间\",\"content\":\"2022-06-03 00:00:00\"},{\"key\":\"END_TIME\",\"title\":\"结束时间\",\"content\":\"2023-06-03 00:00:00\"}]'),(12,1,1,'63822691039186944',0,0,'2022-07-25 04:27:00','[{\"key\":\"CONTENT\",\"title\":\"内容\",\"content\":\"世界这么大，我想去看看！\"},{\"key\":\"START_TIME\",\"title\":\"开始时间\",\"content\":\"2022-06-03 00:00:00\"},{\"key\":\"END_TIME\",\"title\":\"结束时间\",\"content\":\"2023-06-03 00:00:00\"}]');
/*!40000 ALTER TABLE `approval_process_order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `approval_process_order_node`
--

DROP TABLE IF EXISTS `approval_process_order_node`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8 COMMENT='审批单节点';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `approval_process_order_node`
--

LOCK TABLES `approval_process_order_node` WRITE;
/*!40000 ALTER TABLE `approval_process_order_node` DISABLE KEYS */;
INSERT INTO `approval_process_order_node` VALUES (1,1,0,1,30,'同意请假； 请保持电话畅通','2022-05-29 01:20:15',0,'2022-05-29 01:20:22'),(2,1,1,2,10,NULL,'2022-05-29 01:20:15',1,NULL),(3,2,0,1,40,'test by fang','2022-05-29 01:20:45',0,'2022-06-01 00:23:05'),(4,2,5,2,0,NULL,'2022-05-29 01:20:45',2,NULL),(5,3,0,1,30,'同意请假； 请保持电话畅通','2022-05-29 14:37:15',0,'2022-05-29 21:50:32'),(6,3,1,2,10,NULL,'2022-05-29 14:37:15',1,NULL),(7,4,0,1,30,'同意请假； 请保持电话畅通','2022-05-29 15:10:27',0,'2022-05-29 15:24:51'),(8,4,1,2,10,NULL,'2022-05-29 15:10:27',1,NULL),(9,5,0,1,40,'测试转发','2022-05-29 15:50:04',0,'2022-06-01 00:19:50'),(10,5,4,2,0,NULL,'2022-05-29 15:50:04',2,NULL),(11,6,0,1,30,'同意请假； 请保持电话畅通','2022-05-29 16:56:48',0,'2022-05-29 21:54:23'),(12,6,1,2,10,NULL,'2022-05-29 16:56:49',1,NULL),(13,7,0,1,20,'延期请假； 请优先工作进度，力求月底完工','2022-05-29 18:19:25',0,'2022-05-29 18:49:46'),(14,7,1,2,0,NULL,'2022-05-29 18:19:26',1,NULL),(15,5,1,4,0,NULL,NULL,1,NULL),(16,2,1,5,10,NULL,'2022-06-01 00:23:05',1,NULL),(17,8,0,1,10,NULL,'2022-06-03 13:33:57',0,NULL),(18,8,1,2,0,NULL,'2022-06-03 13:33:57',1,NULL),(19,9,0,1,10,NULL,'2022-06-03 13:34:15',0,NULL),(20,9,1,2,0,NULL,'2022-06-03 13:34:15',1,NULL),(21,10,0,1,10,NULL,'2022-06-03 13:37:12',0,NULL),(22,10,1,2,0,NULL,'2022-06-03 13:37:12',1,NULL),(23,11,0,1,10,NULL,'2022-06-03 13:38:05',0,NULL),(24,11,1,2,0,NULL,'2022-06-03 13:38:05',1,NULL),(25,12,0,1,10,NULL,'2022-07-25 04:27:02',0,NULL),(26,12,1,2,0,NULL,'2022-07-25 04:27:03',1,NULL);
/*!40000 ALTER TABLE `approval_process_order_node` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `code_table`
--

DROP TABLE IF EXISTS `code_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `code_table` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) DEFAULT NULL COMMENT '表名',
  `title` varchar(45) DEFAULT NULL COMMENT '表标题',
  `comment` varchar(1000) DEFAULT NULL COMMENT '表注释',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=155439140 DEFAULT CHARSET=utf8 COMMENT='代码表;;option:id~name';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `code_table`
--

LOCK TABLES `code_table` WRITE;
/*!40000 ALTER TABLE `code_table` DISABLE KEYS */;
INSERT INTO `code_table` VALUES (155439106,'pay_info','支付表','支付表'),(155439107,'admin_content_category','内容分类','内容分类'),(155439108,'example_product_model_attr_option','属性选项','属性选项'),(155439109,'approval_process_node','审批流节点','审批流节点'),(155439110,'example_category','自动化样例分类表','自动化样例分类表;;option:id~title,tree:id~title~parent_id,frontHome:tree'),(155439111,'admin_job_log','计划任务执行日志','计划任务执行日志'),(155439112,'example','自动化样例表','自动化样例表'),(155439113,'lot_product_value','产品值','产品值'),(155439114,'code_table_column','代码表字段','代码表字段'),(155439115,'user_info','用户信息','用户信息;;option:id~username'),(155439116,'approval_process','审批流','审批流'),(155439117,'code_table_template_group','模本分组','模本分组;;option:id~name'),(155439118,'admin_user_login_log','用户登录日志','用户登录日志'),(155439119,'admin_config','系统配置表','系统配置表'),(155439120,'admin_job','计划任务表','计划任务表'),(155439121,'lot_model','物联网模型','物联网模型'),(155439122,'admin_config_group','配置分组表','配置分组表'),(155439123,'admin_role','角色表','角色表'),(155439124,'example_product_model','产品模型','产品模型;;foreign:id~example_product_model_attr.model_id~title'),(155439125,'lot_model_item','',''),(155439126,'admin_role_resource','角色资源关联表','角色资源关联表'),(155439127,'code_table_template','代码表模板','代码表模板;;option:id~title'),(155439128,'admin_content','系统内容表','系统内容表'),(155439129,'lot_product','产品','产品'),(155439130,'pay_order','支付单','支付单;option:id~title'),(155439131,'admin_resource','资源表','资源表'),(155439132,'code_table','代码表','代码表;;option:id~name'),(155439133,'approval_process_order','审批单','审批单'),(155439134,'admin_notification','管理员通知','管理员通知'),(155439135,'approval_process_order_node','审批单节点','审批单节点'),(155439136,'example_product_model_attr','模型属性','模型属性'),(155439137,'hello','',''),(155439138,'admin_role_user','角色用户关联表','角色用户关联表'),(155439139,'admin_user','系统用户表','系统用户表');
/*!40000 ALTER TABLE `code_table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `code_table_column`
--

DROP TABLE IF EXISTS `code_table_column`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `code_table_column` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `table_id` int(11) DEFAULT NULL COMMENT '表ID;;frontType:select,foreign:code_talbe.id~name,frontName:Table Name,isFilter:true',
  `name` varchar(45) DEFAULT NULL COMMENT '表名;;isFilter:true',
  `title` varchar(45) DEFAULT NULL COMMENT '表标题;;isFilter:true',
  `data_type` varchar(45) DEFAULT NULL COMMENT '数据类型;tinyint,smallint,mediumint,int,bigint,float,double,char,varchar,tinytext,text,mediumtext,longtext,date,time,datetime,timestamp,decimal;frontType:select,isFilter:true',
  `is_pk` tinyint(4) DEFAULT NULL COMMENT '是否主键;0:否,1:是;frontType:select',
  `is_not_null` tinyint(4) DEFAULT NULL COMMENT '是否不为空;0:否,1:是;frontType:select',
  `is_unique` tinyint(4) DEFAULT NULL COMMENT '是否无符号;0:否,1:是;frontType:select',
  `is_auto_increment` tinyint(4) DEFAULT NULL COMMENT '是否自增;0:否,1:是;frontType:select',
  `is_zero_fill` tinyint(4) DEFAULT NULL COMMENT '是否0填充;0:否,1:是;frontType:select',
  `default_value` varchar(1000) DEFAULT NULL COMMENT '默认值',
  `length` int(11) DEFAULT NULL COMMENT '长度',
  `scale` int(11) DEFAULT NULL COMMENT '小数点位数',
  `comment` varchar(3000) DEFAULT NULL COMMENT '字段注释',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uni-table_id-name` (`table_id`,`name`),
  KEY `idx-table_id` (`table_id`)
) ENGINE=InnoDB AUTO_INCREMENT=804 DEFAULT CHARSET=utf8 COMMENT='代码表字段';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `code_table_column`
--

LOCK TABLES `code_table_column` WRITE;
/*!40000 ALTER TABLE `code_table_column` DISABLE KEYS */;
INSERT INTO `code_table_column` VALUES (527,155439111,'id',NULL,'int',0,0,0,0,0,NULL,11,0,NULL),(528,155439111,'job_id',NULL,'int',0,0,0,0,0,NULL,11,0,'任务ID'),(529,155439111,'start_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'任务创建时间'),(530,155439111,'status',NULL,'int',0,0,0,0,0,NULL,11,0,'任务执行状'),(531,155439111,'result',NULL,'varchar',0,0,0,0,0,NULL,20000,0,'任务执行返回结果'),(532,155439111,'end_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'任务结束时间'),(533,155439111,'admin_id',NULL,'int',0,0,0,0,0,NULL,11,0,'手动执行时候的用户I'),(534,155439106,'id',NULL,'int',0,1,0,1,0,NULL,11,0,'ID'),(535,155439106,'title',NULL,'varchar',0,1,0,0,0,NULL,45,0,'标题;;,min:1,frontType:text,min:3,max:46'),(536,155439106,'name',NULL,'varchar',0,1,0,0,0,NULL,45,0,NULL),(537,155439106,'account_app_id',NULL,'varchar',0,0,0,0,0,NULL,45,0,'账号AppId'),(538,155439106,'account_id',NULL,'varchar',0,1,0,0,0,NULL,45,0,'支付ID'),(539,155439106,'account_private_key',NULL,'varchar',0,1,0,0,0,NULL,6000,0,'支付私钥'),(540,155439106,'account_serial_number',NULL,'varchar',0,0,0,0,0,NULL,128,0,'商户证书编号'),(541,155439106,'account_public_key',NULL,'varchar',0,1,0,0,0,NULL,6000,0,'支付公钥'),(542,155439106,'account_img',NULL,'varchar',0,1,0,0,0,NULL,2048,0,'支付封面图片;;frontType:upload,uploadCount:1'),(543,155439106,'client_type',NULL,'varchar',0,1,0,0,0,NULL,1024,0,'支持客户端类型;'),(544,155439106,'created_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'创建时间'),(545,155439106,'updated_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'更新时间'),(546,155439106,'platform',NULL,'varchar',0,1,0,0,0,'soho',45,0,'平台识别名'),(547,155439106,'status',NULL,'tinyint',0,1,0,0,0,NULL,1,0,'状'),(548,155439106,'adapter_name',NULL,'varchar',0,1,0,0,0,NULL,100,0,'支付驱动;wechat_jsapi,wechat_h5,wechat_app,wechat_native,alipay_wap,alipay_web;frontType:select'),(549,155439107,'id',NULL,'int',0,1,0,1,0,NULL,11,0,'分类ID'),(550,155439107,'name',NULL,'varchar',0,0,0,0,0,NULL,500,0,'分类名称'),(551,155439107,'parent_id',NULL,'int',0,0,0,0,0,'0',11,0,'父分类ID'),(552,155439107,'description',NULL,'varchar',0,0,0,0,0,NULL,1000,0,'分类描述'),(553,155439107,'keyword',NULL,'varchar',0,0,0,0,0,NULL,500,0,'分类关键字'),(554,155439107,'content',NULL,'text',0,0,0,0,0,NULL,NULL,0,'分类内容'),(555,155439107,'order',NULL,'int',0,0,0,0,0,NULL,11,0,'排序'),(556,155439107,'is_display',NULL,'tinyint',0,0,0,0,0,NULL,4,0,'分类是否'),(557,155439107,'update_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'更新时间'),(558,155439107,'created_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'创建时间'),(559,155439108,'id',NULL,'int',0,1,0,0,0,NULL,11,0,NULL),(560,155439108,'attr_id',NULL,'int',0,0,0,0,0,NULL,11,0,'属性ID;;frontName:属性'),(561,155439108,'value',NULL,'varchar',0,0,0,0,0,NULL,45,0,'值'),(562,155439108,'updated_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,NULL),(563,155439108,'created_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,NULL),(564,155439109,'id',NULL,'int',0,1,0,1,0,NULL,11,0,NULL),(565,155439109,'approval_process_id',NULL,'int',0,0,0,0,0,NULL,11,0,'审批流ID'),(566,155439109,'source_user_id',NULL,'int',0,0,0,0,0,NULL,11,0,'源用户I'),(567,155439109,'user_id',NULL,'int',0,0,0,0,0,NULL,11,0,'审批用户ID'),(568,155439109,'serial_number',NULL,'int',0,0,0,0,0,NULL,11,0,'序列'),(569,155439109,'created_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'创建时间'),(570,155439112,'id',NULL,'int',0,1,0,0,0,NULL,11,0,NULL),(571,155439112,'title',NULL,'varchar',0,0,0,0,0,NULL,45,0,'标题'),(572,155439112,'category_id',NULL,'int',0,0,0,0,0,NULL,11,0,'分类ID;;frontType:treeSelect,foreign:example_category.id~title,frontName:category'),(573,155439112,'pay_id',NULL,'varchar',0,0,0,0,0,NULL,45,0,'支付方式ID;;frontType:select,foreign:pay_info.id~title'),(574,155439112,'content',NULL,'text',0,0,0,0,0,NULL,NULL,0,'富媒体;;frontType:editor,ignoreInList:true'),(575,155439112,'updated_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'更新时间'),(576,155439112,'created_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'创建时间'),(577,155439113,'id',NULL,'int',0,1,0,1,0,NULL,11,0,NULL),(578,155439113,'params_name',NULL,'varchar',0,0,0,0,0,NULL,45,0,'mod'),(579,155439113,'product_id',NULL,'int',0,0,0,0,0,NULL,11,0,'产品ID'),(580,155439113,'title',NULL,'varchar',0,0,0,0,0,NULL,45,0,'产品标题'),(581,155439113,'value',NULL,'varchar',0,0,0,0,0,NULL,45,0,'产品当前实际值'),(582,155439113,'given_value',NULL,'varchar',0,0,0,0,0,NULL,45,0,'产品设置值'),(583,155439113,'unit',NULL,'varchar',0,0,0,0,0,NULL,45,0,'单位'),(584,155439113,'type',NULL,'varchar',0,0,0,0,0,NULL,45,0,'值类型'),(585,155439113,'order',NULL,'int',0,0,0,0,0,NULL,11,0,'排序'),(586,155439113,'created_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'创建时间'),(587,155439113,'updated_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'更新时间'),(588,155439114,'id',NULL,'int',0,1,0,1,0,NULL,11,0,NULL),(589,155439114,'table_id',NULL,'int',0,0,0,0,0,NULL,11,0,'表ID;;frontType:select,foreign:code_talbe.id~name,frontName:Tab'),(590,155439114,'name',NULL,'varchar',0,0,0,0,0,NULL,45,0,'表名;;isFilter:true'),(591,155439114,'title',NULL,'varchar',0,0,0,0,0,NULL,45,0,'表标题;;isFilter:true'),(592,155439114,'data_type',NULL,'varchar',0,0,0,0,0,NULL,45,0,'数据类型;tinyint,smallint,mediumint,int,bigint,float,double,char,varchar,tinytext,text,mediumtext,longtext,date,time,datetime,timestamp,decimal;frontType:select,isFilter:true'),(593,155439114,'is_pk',NULL,'tinyint',0,0,0,0,0,NULL,4,0,'是否主键;0:否,1:是;frontType:select'),(594,155439114,'is_not_null',NULL,'tinyint',0,0,0,0,0,NULL,4,0,'是否不为空;0:否,1:是;frontType:select'),(595,155439114,'is_unique',NULL,'tinyint',0,0,0,0,0,NULL,4,0,'是否无符号;0:否,1:是;frontType:select'),(596,155439114,'is_auto_increment',NULL,'tinyint',0,0,0,0,0,NULL,4,0,'是否自增;0:否,1:是;frontType:select'),(597,155439114,'is_zero_fill',NULL,'tinyint',0,0,0,0,0,NULL,4,0,'是否0填充;0:否,1:是;frontType:select'),(598,155439114,'default_value',NULL,'varchar',0,0,0,0,0,NULL,1000,0,'默认值'),(599,155439114,'length',NULL,'int',0,0,0,0,0,NULL,11,0,'长度'),(600,155439114,'scale',NULL,'int',0,0,0,0,0,NULL,11,0,'小数点位数'),(601,155439114,'comment',NULL,'varchar',0,0,0,0,0,NULL,3000,0,'字段注释'),(602,155439115,'id',NULL,'int',0,1,0,1,0,NULL,11,0,'ID'),(603,155439115,'username',NULL,'varchar',0,0,0,0,0,NULL,45,0,'用户名'),(604,155439115,'email',NULL,'varchar',0,0,0,0,0,NULL,255,0,'邮箱'),(605,155439115,'phone',NULL,'varchar',0,0,0,0,0,NULL,15,0,'手机号'),(606,155439115,'password',NULL,'varchar',0,0,0,0,0,NULL,255,0,'密码;;frontType:password,ignoreInList:true'),(607,155439115,'avatar',NULL,'varchar',0,0,0,0,0,NULL,255,0,'头像;;frontType:upload,uploadCount:1,ignoreInList:true'),(608,155439115,'status',NULL,'tinyint',0,0,0,0,0,'1',4,0,'状态;0:禁用,1:活跃;frontType:select'),(609,155439115,'age',NULL,'tinyint',0,0,1,0,0,NULL,4,0,'年龄;;frontName:年龄'),(610,155439115,'sex',NULL,'tinyint',0,0,0,0,0,NULL,4,0,'性别;0:女,1:男;frontType:select,frontName:性别'),(611,155439115,'updated_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'更新时间'),(612,155439115,'created_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'头像;;frontType:upload,uploadCount:1'),(613,155439116,'id',NULL,'int',0,1,0,1,0,NULL,11,0,NULL),(614,155439116,'no',NULL,'varchar',0,0,0,0,0,NULL,100,0,'审批流编号；业务绑定'),(615,155439116,'name',NULL,'varchar',0,0,0,0,0,NULL,100,0,'审批流名称'),(616,155439116,'type',NULL,'tinyint',0,0,0,0,0,NULL,4,0,'审批类'),(617,155439116,'enable',NULL,'bit',0,0,0,0,0,NULL,1,0,'是否'),(618,155439116,'reject_action',NULL,'tinyint',0,0,0,0,0,NULL,4,0,'审批拒绝动'),(619,155439116,'metadata',NULL,'varchar',0,0,0,0,0,NULL,20000,0,'元数据信息'),(620,155439116,'created_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'创建时间'),(621,155439117,'id',NULL,'int',0,1,0,1,0,NULL,11,0,'ID'),(622,155439117,'title',NULL,'varchar',0,0,0,0,0,NULL,255,0,'标题'),(623,155439117,'name',NULL,'varchar',0,0,0,0,0,NULL,45,0,'名称'),(624,155439117,'base_path',NULL,'varchar',0,0,0,0,0,NULL,1000,0,'基本写入路径'),(625,155439117,'main_function',NULL,'varchar',0,0,0,0,0,NULL,45,0,'该组入口函数'),(626,155439117,'explain',NULL,'varchar',0,0,0,0,0,NULL,1000,0,'说明'),(627,155439117,'updated_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'更新时间'),(628,155439117,'created_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'创建时间'),(629,155439118,'id',NULL,'int',0,1,0,1,0,NULL,11,0,'ID'),(630,155439118,'admin_user_id',NULL,'int',0,0,0,0,0,NULL,11,0,'后台用户ID'),(631,155439118,'client_ip',NULL,'varchar',0,0,0,0,0,NULL,50,0,'客户端IP地址考虑IPv6字段适当放宽'),(632,155439118,'client_user_agent',NULL,'varchar',0,0,0,0,0,NULL,1000,0,'客户端软件信息'),(633,155439118,'token',NULL,'varchar',0,0,0,0,0,NULL,3000,0,'给用户发放的token'),(634,155439118,'created_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'创建时间'),(635,155439119,'id',NULL,'int',0,1,1,1,0,NULL,10,0,'ID'),(636,155439119,'group_key',NULL,'varchar',0,0,0,0,0,NULL,200,0,'配置文件分组名'),(637,155439119,'key',NULL,'varchar',0,0,0,0,0,NULL,200,0,'配置信息唯一识别key'),(638,155439119,'value',NULL,'text',0,0,0,0,0,NULL,NULL,0,'配置信息值'),(639,155439119,'explain',NULL,'varchar',0,0,0,0,0,NULL,500,0,'说明'),(640,155439119,'type',NULL,'tinyint',0,0,0,0,0,NULL,4,0,'配置信息类型'),(641,155439119,'updated_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'更新时间'),(642,155439119,'created_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'创建时间'),(643,155439120,'id',NULL,'int',0,1,0,1,0,NULL,11,0,NULL),(644,155439120,'name',NULL,'varchar',0,0,0,0,0,NULL,1000,0,'任务名称'),(645,155439120,'can_concurrency',NULL,'int',0,0,0,0,0,NULL,11,0,'能否并发执行'),(646,155439120,'cmd',NULL,'varchar',0,0,0,0,0,NULL,10000,0,'计划任务执行指令'),(647,155439120,'status',NULL,'int',0,0,0,0,0,NULL,11,0,''),(648,155439120,'cron',NULL,'varchar',0,0,0,0,0,NULL,100,0,'cron表达式'),(649,155439120,'created_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'创建时间'),(650,155439120,'updated_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'最后更新时间'),(651,155439121,'id',NULL,'int',0,1,0,1,0,NULL,11,0,'ID'),(652,155439121,'name',NULL,'varchar',0,0,0,0,0,NULL,255,0,'模型名称'),(653,155439121,'supplier_id',NULL,'int',0,0,0,0,0,NULL,11,0,'供应商ID'),(654,155439121,'updated_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'更新时间'),(655,155439121,'created_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'创建时间'),(656,155439122,'id',NULL,'int',0,1,0,1,0,NULL,11,0,NULL),(657,155439122,'key',NULL,'varchar',0,1,0,0,0,NULL,500,0,'组主键'),(658,155439122,'name',NULL,'varchar',0,0,0,0,0,NULL,500,0,'组名'),(659,155439122,'created_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'创建时间'),(660,155439123,'id',NULL,'int',0,1,1,1,0,NULL,10,0,NULL),(661,155439123,'name',NULL,'varchar',0,0,0,0,0,NULL,45,0,'角色名'),(662,155439123,'remarks',NULL,'varchar',0,0,0,0,0,NULL,255,0,'备注'),(663,155439123,'created_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'创建时间'),(664,155439123,'enable',NULL,'tinyint',0,0,0,0,0,'1',4,0,'是否启用'),(666,155439124,'id',NULL,'int',0,1,0,0,0,NULL,11,0,'ID'),(667,155439124,'title',NULL,'varchar',0,0,0,0,0,NULL,45,0,'产品标题'),(668,155439124,'created_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'创建时间'),(669,155439124,'updated_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'更新时间'),(670,155439125,'id',NULL,'int',0,1,0,1,0,NULL,11,0,NULL),(671,155439125,'model_id',NULL,'int',0,0,0,0,0,NULL,11,0,'模型ID'),(672,155439125,'title',NULL,'varchar',0,0,0,0,0,NULL,255,0,'标题'),(673,155439125,'unit',NULL,'varchar',0,0,0,0,0,NULL,45,0,'单位'),(674,155439125,'params_name',NULL,'varchar',0,0,0,0,0,NULL,45,0,'参数名'),(675,155439125,'tips',NULL,'varchar',0,0,0,0,0,NULL,45,0,'提示'),(676,155439125,'extended_data',NULL,'varchar',0,0,0,0,0,NULL,2000,0,'wifi模块pin编号'),(677,155439125,'type',NULL,'varchar',0,0,0,0,0,NULL,45,0,'数据类型'),(678,155439125,'updated_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'更新时间'),(679,155439125,'created_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'创建时间'),(680,155439126,'id',NULL,'int',0,1,1,1,0,NULL,10,0,NULL),(681,155439126,'role_id',NULL,'int',0,0,1,0,0,NULL,10,0,'角色ID'),(682,155439126,'resource_id',NULL,'int',0,0,1,0,0,NULL,10,0,'资源ID'),(683,155439126,'created_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'创建时间'),(684,155439127,'id',NULL,'int',0,1,0,1,0,NULL,11,0,NULL),(685,155439127,'group_id',NULL,'int',0,0,0,0,0,NULL,11,0,'分组;;frontType:select,foreign:code_table_template_group.id~name'),(686,155439127,'name',NULL,'varchar',0,0,0,0,0,NULL,255,0,'模板名'),(687,155439127,'title',NULL,'varchar',0,0,0,0,0,NULL,255,0,'标题'),(688,155439127,'status',NULL,'tinyint',0,0,0,0,0,NULL,4,0,'状态;0:禁用,1:活跃;frontType:select'),(689,155439127,'code',NULL,'text',0,0,0,0,0,NULL,NULL,0,'代码;;frontType:textArea'),(690,155439127,'created_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,NULL),(691,155439128,'id',NULL,'int',0,1,0,1,0,NULL,11,0,NULL),(692,155439128,'title',NULL,'varchar',0,0,0,0,0,NULL,1000,0,'文章标题'),(693,155439128,'description',NULL,'varchar',0,0,0,0,0,NULL,3000,0,'文章描述'),(694,155439128,'keywords',NULL,'varchar',0,0,0,0,0,NULL,1000,0,'关键字'),(695,155439128,'thumbnail',NULL,'varchar',0,0,0,0,0,NULL,500,0,'缩略图'),(696,155439128,'body',NULL,'text',0,0,0,0,0,NULL,NULL,0,'文章内容'),(697,155439128,'updated_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'创建时间'),(698,155439128,'created_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'创建时间'),(699,155439128,'category_id',NULL,'int',0,0,0,0,0,NULL,11,0,'文章分类ID'),(700,155439128,'user_id',NULL,'int',0,0,0,0,0,NULL,11,0,'添加的管理员ID'),(701,155439128,'status',NULL,'tinyint',0,0,0,0,0,NULL,4,0,'文章状'),(702,155439128,'is_top',NULL,'tinyint',0,0,0,0,0,'0',4,0,'是否置顶'),(703,155439128,'star',NULL,'int',0,1,0,0,0,'0',11,0,'star数量'),(704,155439128,'likes',NULL,'int',0,1,0,0,0,'0',11,0,'点赞数量'),(705,155439128,'dis_likes',NULL,'int',0,1,0,0,0,'0\'',11,0,NULL),(706,155439128,'comments_count',NULL,'int',0,1,0,0,0,'0',11,0,'评论数量'),(707,155439129,'id',NULL,'int',0,1,0,1,0,NULL,11,0,NULL),(708,155439129,'model_id',NULL,'int',0,0,0,0,0,NULL,11,0,'模型ID'),(709,155439129,'mac',NULL,'varchar',0,0,0,0,0,NULL,45,0,'设备mac地址'),(710,155439129,'user_id',NULL,'int',0,0,0,0,0,NULL,11,0,'所属用户'),(711,155439129,'update_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'更新时间'),(712,155439129,'created_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'创建时间'),(713,155439129,'status',NULL,'tinyint',0,0,0,0,0,NULL,4,0,'产品状态'),(714,155439130,'id',NULL,'int',0,1,0,1,0,NULL,11,0,NULL),(715,155439130,'pay_id',NULL,'int',0,1,0,0,0,NULL,11,0,'支付方式ID;;frontType:select,foreign:pay_info.id~title'),(716,155439130,'order_no',NULL,'varchar',0,1,0,0,0,NULL,128,0,'支付单号'),(717,155439130,'tracking_no',NULL,'varchar',0,1,0,0,0,NULL,128,0,'外部跟踪单号'),(718,155439130,'transaction_id',NULL,'varchar',0,0,0,0,0,NULL,128,0,'支付供应商跟踪ID；例如微信，支付宝支付单号'),(719,155439130,'amount',NULL,'decimal',0,1,0,0,0,NULL,9,2,'支付金额'),(720,155439130,'status',NULL,'tinyint',0,1,0,0,0,'1',4,0,'支付单状态;1:待支付,10:已扫码,20:支付成功,30:支付失败;frontType:select'),(721,155439130,'payed_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'支付时间;;frontType:datetime'),(722,155439130,'notify_url',NULL,'varchar',0,0,0,0,0,NULL,500,0,'通知地址'),(723,155439130,'created_time',NULL,'datetime',0,1,0,0,0,NULL,NULL,0,'创建时间'),(724,155439130,'updated_time',NULL,'datetime',0,1,0,0,0,NULL,NULL,0,'更新时间'),(725,155439131,'id',NULL,'int',0,1,0,1,0,NULL,11,0,NULL),(726,155439131,'name',NULL,'varchar',0,0,0,0,0,NULL,500,0,'英文名'),(727,155439131,'route',NULL,'varchar',0,0,0,0,0,NULL,500,0,'路由'),(728,155439131,'type',NULL,'tinyint',0,0,0,0,0,'1',1,0,'资源类型'),(729,155439131,'remarks',NULL,'varchar',0,0,0,0,0,NULL,500,0,'备注'),(730,155439131,'created_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'创建时间'),(731,155439131,'visible',NULL,'tinyint',0,0,0,0,0,'1',4,0,'资源界面是否可见'),(732,155439131,'sort',NULL,'int',0,0,0,0,0,'100',11,0,'排序'),(733,155439131,'breadcrumb_parent_id',NULL,'int',0,0,0,0,0,'1',11,0,'父ID'),(734,155439131,'zh_name',NULL,'varchar',0,0,0,0,0,NULL,500,0,'中文名'),(735,155439131,'icon_name',NULL,'varchar',0,0,0,0,0,NULL,500,0,'菜单图标'),(736,155439132,'id',NULL,'int',0,1,0,1,0,NULL,11,0,NULL),(737,155439132,'name',NULL,'varchar',0,0,0,0,0,NULL,45,0,'表名'),(738,155439132,'title',NULL,'varchar',0,0,0,0,0,NULL,45,0,'表标题'),(739,155439132,'comment',NULL,'varchar',0,0,0,0,0,NULL,1000,0,'表注释'),(740,155439133,'id',NULL,'int',0,1,0,1,0,NULL,11,0,NULL),(741,155439133,'approval_process_id',NULL,'int',0,0,0,0,0,NULL,11,0,'审批单ID'),(742,155439133,'apply_user_id',NULL,'int',0,0,0,0,0,NULL,11,0,'申请人ID'),(743,155439133,'out_no',NULL,'varchar',0,0,0,0,0,NULL,128,0,'外部单号'),(744,155439133,'status',NULL,'tinyint',0,0,0,0,0,NULL,4,0,'审批状'),(745,155439133,'apply_status',NULL,'tinyint',0,0,0,0,0,NULL,4,0,'审批处理结'),(746,155439133,'created_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'创建时间'),(747,155439133,'content',NULL,'varchar',0,0,0,0,0,NULL,20000,0,'审核单内容'),(748,155439134,'id',NULL,'int',0,1,0,1,0,NULL,11,0,NULL),(749,155439134,'admin_user_id',NULL,'int',0,0,0,0,0,NULL,11,0,'接收人'),(750,155439134,'title',NULL,'varchar',0,0,0,0,0,NULL,500,0,'标题'),(751,155439134,'create_admin_user_id',NULL,'int',0,1,0,0,0,NULL,11,0,'创'),(752,155439134,'content',NULL,'text',0,0,0,0,0,NULL,NULL,0,'通知内容'),(753,155439134,'created_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'创建时间'),(754,155439134,'is_read',NULL,'tinyint',0,0,0,0,0,'0',4,0,'是否'),(755,155439135,'id',NULL,'int',0,1,0,1,0,NULL,11,0,NULL),(756,155439135,'order_id',NULL,'int',0,0,0,0,0,NULL,11,0,NULL),(757,155439135,'source_user_id',NULL,'int',0,0,0,0,0,NULL,11,0,'源审批'),(758,155439135,'user_id',NULL,'int',0,0,0,0,0,NULL,11,0,'审批人ID'),(759,155439135,'status',NULL,'tinyint',0,0,0,0,0,NULL,4,0,'审批状态'),(760,155439135,'reply',NULL,'varchar',0,0,0,0,0,NULL,1000,0,'审批回复'),(761,155439135,'created_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'创建时间'),(762,155439135,'serial_number',NULL,'tinyint',0,0,0,0,0,NULL,4,0,'审批序号；审批顺序'),(763,155439135,'approval_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'审批时间'),(764,155439136,'id',NULL,'int',0,1,0,0,0,NULL,11,0,NULL),(765,155439136,'model_id',NULL,'int',0,0,0,0,0,NULL,11,0,NULL),(766,155439136,'title',NULL,'varchar',0,0,0,0,0,NULL,45,0,NULL),(767,155439136,'data_type',NULL,'tinyint',0,0,0,0,0,NULL,4,0,'数据类型;0:文本,1:整数,3:小数'),(768,155439136,'unit_name',NULL,'varchar',0,0,0,0,0,NULL,45,0,'单位名'),(769,155439136,'customizable',NULL,'tinyint',0,0,0,0,0,NULL,4,0,'可自定义;0:不可以,1:可以;frontType:select'),(770,155439136,'is_sales_attribute',NULL,'tinyint',0,0,0,0,0,NULL,4,0,'是否销售属性;0:否,1:是;frontType:select'),(771,155439136,'created_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,NULL),(772,155439136,'updated_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'更新时间'),(773,155439137,'id',NULL,'int',0,1,1,1,1,NULL,11,0,NULL),(774,155439137,'name',NULL,'varchar',0,0,0,0,0,NULL,45,0,NULL),(775,155439137,'value',NULL,'varchar',0,0,0,0,0,NULL,45,0,NULL),(776,155439137,'updated_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'更新时间'),(777,155439137,'created_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'创建时间'),(778,155439110,'id',NULL,'int',0,1,0,1,0,NULL,11,0,'ID;;optionKey'),(779,155439110,'title',NULL,'varchar',0,0,0,0,0,NULL,145,0,'标题;;optionValue'),(780,155439110,'parent_id',NULL,'int',0,0,0,0,0,NULL,11,0,'父级ID;;frontType:treeSelect,parent:id,foreign:example_category,frontName:Parent_Category'),(781,155439110,'updated_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'更新时间'),(782,155439110,'created_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'创建时间'),(783,155439110,'only_date',NULL,'date',0,0,0,0,0,NULL,NULL,0,'只是日期;;frontType:date'),(784,155439110,'pay_datetime',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'日期时间;;frontType:datetime'),(785,155439110,'img',NULL,'varchar',0,0,0,0,0,NULL,500,0,'图片;;frontType:upload,uploadCount:3'),(786,155439138,'id',NULL,'int',0,1,1,1,0,NULL,10,0,NULL),(787,155439138,'role_id',NULL,'int',0,0,1,0,0,NULL,10,0,'角色ID'),(788,155439138,'user_id',NULL,'int',0,0,1,0,0,NULL,10,0,'用户ID'),(789,155439138,'status',NULL,'tinyint',0,0,0,0,0,NULL,4,0,'状态'),(790,155439138,'created_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'创建时间'),(791,155439139,'id',NULL,'bigint',0,1,0,1,0,NULL,20,0,NULL),(792,155439139,'username',NULL,'varchar',0,1,0,0,0,NULL,128,0,'用户名'),(793,155439139,'phone',NULL,'varchar',0,1,0,0,0,NULL,11,0,'手机号'),(794,155439139,'nick_name',NULL,'varchar',0,0,0,0,0,NULL,45,0,'昵称'),(795,155439139,'real_name',NULL,'varchar',0,0,0,0,0,NULL,45,0,'真实名称'),(796,155439139,'avatar',NULL,'varchar',0,0,0,0,0,'//image.zuiidea.com/photo-1519336555923-59661f41bb45.jpeg?imageView2/1/w/200/h/200/format/webp/q/75|imageslim',500,0,'头像'),(797,155439139,'email',NULL,'varchar',0,0,0,0,0,NULL,255,0,'邮箱地址'),(798,155439139,'password',NULL,'varchar',0,0,0,0,0,NULL,300,0,'用户密码'),(799,155439139,'updated_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'更新时间'),(800,155439139,'created_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'创建时间'),(801,155439139,'sex',NULL,'tinyint',0,0,0,0,0,NULL,4,0,'性别'),(802,155439139,'age',NULL,'int',0,0,0,0,0,NULL,11,0,'年龄'),(803,155439139,'is_deleted',NULL,'tinyint',0,0,0,0,0,'0',4,0,'软删除标记');
/*!40000 ALTER TABLE `code_table_column` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `code_table_template`
--

DROP TABLE IF EXISTS `code_table_template`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `code_table_template` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `group_id` int(11) DEFAULT NULL COMMENT '分组;;frontType:select,foreign:code_table_template_group.id~name',
  `name` varchar(255) DEFAULT NULL COMMENT '模板名',
  `title` varchar(255) DEFAULT NULL COMMENT '标题',
  `status` tinyint(4) DEFAULT NULL COMMENT '状态;0:禁用,1:活跃;frontType:select',
  `code` text COMMENT '代码;;frontType:textArea',
  `created_time` datetime DEFAULT NULL,
  `sort` int(11) NOT NULL DEFAULT '0' COMMENT '排序',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=1170362373 DEFAULT CHARSET=utf8 COMMENT='代码表模板;;option:id~title';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `code_table_template`
--

LOCK TABLES `code_table_template` WRITE;
/*!40000 ALTER TABLE `code_table_template` DISABLE KEYS */;
INSERT INTO `code_table_template` VALUES (1,1715638274,'utils','工具类库',1,'import java.text.SimpleDateFormat\nimport java.util.regex.Matcher;\nimport java.util.regex.Pattern;\n\ndef main(name) {\n  \"hello: \" + name\n}\n\ndef tint(i) {\n  \"int: \" + i\n}\n\ndef javaName(str) {\n    if(str  == null) {\n        return null;\n    }\n  def name = capitalName(str)\n  name.substring(0, 1).toUpperCase() + name.substring(1);\n}\n\ndef capitalName(str) {\n    if(str  == null) {\n        return null;\n    }\n    final StringBuffer sb = new StringBuffer();\n    Pattern p = Pattern.compile(\"_(\\\\w)\");\n    Matcher m = p.matcher(str);\n    while (m.find()){\n        m.appendReplacement(sb,m.group(1).toUpperCase());\n    }\n    m.appendTail(sb);\n    sb.toString()\n}\n\n/**\n * 获取前端类型\n *\n * @param dbType\n * @param comment\n * @return\n */\ndef getFrontType(dbType, comment) {\n    def frontType = getExtendData(comment, \'frontType\', null);\n    if(frontType) {\n        return frontType;\n    }\n    switch (dbType) {\n        case \"int\":\n        case \"tinyint\":\n        case \"smallint\":\n        case \"mediumint\":\n        case \"bigint\":\n        case \"float\":\n        case \"double\":\n        case \"decimal\":\n            frontType = \"number\"\n            break;\n        default:\n            frontType = \"text\";\n            break;\n    }\n    return frontType;\n}\n\n/**\n * 获取外链表\n *\n * @param comment\n * @return\n */\ndef getForeignTable(comment) {\n    def foreign = getExtendData(comment, \'foreign\', null)\n    if(foreign) {\n        String[] parts = foreign.split(\"~\")\n        if(parts && parts.length>=1) {\n            def fid = parts[0]\n            String[] tableParts = fid.split(\"\\\\.\")\n            if(tableParts.length>0) {\n                return tableParts[0]\n            }\n        }\n    }\n    return null;\n}\n\n/**\n * 获取关联表大驼峰名\n */\ndef getForeignTableCapitalKey(comment) {\n    def capita = getForeignTable(comment)\n    if(capita) {\n        return capitalName(capita).replace(\" \", \"\");\n    }\n    return null;\n}\n\n/**\n * 获取前端校验最小值\n */\ndef getFrontMin(it) {\n    def min = 0\n    def minStr = getExtendData(it.comment,\"min\", null)\n    if(minStr != null) {\n        return Integer.valueOf(minStr)\n    }\n\n    //检查表单类型\n    def frontType = getExtendData(it.comment, \'frontType\', \'text\')\n    if(frontType != \'text\') {\n        return null;\n    }\n\n    if(it.dbUnsigned) {\n        //无符号\n        switch (it.dbType) {\n            case \"tinyint\":\n            case \"smallint\":\n            case \"mediumint\":\n            case \"int\":\n            case \"bigint\":\n                min = 0;\n                break;\n            default:\n//                min = it.length;\n                break;\n        }\n    } else {\n        switch (it.dbType) {\n            case \"tinyint\":\n                min = -128\n                break;\n            case \"smallint\":\n                min = -32768;\n                break;\n            case \"mediumint\":\n                min = 	-8388608;\n                break;\n            case \"int\":\n                min = -2147483648;\n                break;\n            case \"bigint\":\n                min = -9007199254740991;  //该值为Js最大值\n                break;\n            default:\n//                min = it.length;\n                break;\n        }\n    }\n    return min;\n}\n\n/**\n * 获取前端校验最小值\n */\ndef getFrontMax(it) {\n    def max = it.length\n    //dbUnsigned\n    def maxStr = getExtendData(it.comment,\"max\", null)\n    if(maxStr != null) {\n        //如果指定直接使用指定值\n        return Integer.valueOf(maxStr)\n    }\n\n    //检查表单类型\n    def frontType = getExtendData(it.comment, \'frontType\', \'text\')\n    if(frontType != \'text\') {\n        return null;\n    }\n\n    if(it.dbUnsigned) {\n        //无符号\n        switch (it.dbType) {\n            case \"tinyint\":\n                max = 255\n                break;\n            case \"smallint\":\n                max = 	65535;\n                break;\n            case \"mediumint\":\n                max = 16777215;\n                break;\n            case \"int\":\n                max = 4294967295;\n                break;\n            case \"bigint\":\n                max = 9007199254740992;  //该值为Js最大值\n                break;\n            default:\n                max = it.length;\n                break;\n        }\n    } else {\n        switch (it.dbType) {\n            case \"tinyint\":\n                max = 127\n                break;\n            case \"smallint\":\n                max = 32767;\n                break;\n            case \"mediumint\":\n                max = 8388607;\n                break;\n            case \"int\":\n                max = 2147483647;\n                break;\n            case \"bigint\":\n                max = 9007199254740992;  //该值为Js最大值\n                break;\n            default:\n                max = it.length;\n                break;\n        }\n    }\n\n    return max;\n}\n\n/**\n * 查找domain命名空间\n *\n * @param dir\n */\ndef findDomainPackageName(dir) {\n    return findPackageName(new File(dir.getParent() + \"/domain\"))\n}\n\n/**\n * 查找service命名空间\n *\n * @param dir\n */\ndef findServicePackageName(dir) {\n    return findPackageName(new File(dir.getParent() + \"/service\"))\n}\n\n/**\n * 查找指定目录现有的命名空间\n *\n * @param dir\n * @return\n */\ndef findPackageName(dir) {\n    if(dir != null && dir.isDirectory()) {\n        File[] files = dir.listFiles();\n        if(files != null && files.length>0) {\n            for (File item:files) {\n                if(item.isFile()) {\n                    List<String> lines = Files.readLines(item, StandardCharsets.UTF_8);\n                    //查找命名空间\n                    for (String line: lines) {\n                        line = line.trim();\n                        if(line.startsWith(\"package\")) {\n                            line = line.split(\" \")[1];\n                            line = line.replace(\";\", \"\")\n                            line = line.trim();\n                            return line;\n                        }\n                    }\n                }\n            }\n        }\n    }\n    return \"\"\n}\n\n/**\n * 首字母小写\n * @param s\n * @return\n */\ndef toLowerCaseFirstOne(String s){\n    if(Character.isLowerCase(s.charAt(0)))\n        return s;\n    else\n        return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();\n}\n\n/**\n * 转换成中划线\n *\n * @param s\n * @return\n */\ndef toStrikethrough(String str) {\n    def s = com.intellij.psi.codeStyle.NameUtil.splitNameIntoWords(str)\n            .collect { Case.LOWER.apply(it) }\n            .join(\"-\")\n            .replaceAll(/[^\\p{javaJavaIdentifierPart}[_]]/, \"_\")\n    return s\n}\n\n/**\n * 获取当前时间\n * @return\n */\ndef currentDate() {\n    Date d = new Date();\n    System.out.println(d);\n    SimpleDateFormat sdf = new SimpleDateFormat(\"yyyy-MM-dd HH:mm:ss\");\n    String dateNowStr = sdf.format(d);\n    return dateNowStr;\n}\n\n/**\n * 获取注解选项\n *\n * @param comment\n * @return\n */\ndef getOptions(String comment) {\n    if(comment == null) {\n        return []\n    }\n    String[] parts = comment.split(\";\");\n    if(parts.length<2) {\n        return null;\n    }\n//    String colmenName = parts[0];\n    parts = parts[1].split(\",\");\n    HashMap<String, String> dataMap = new HashMap<>();\n    for (String item: parts) {\n        String[] kv = item.split(\":\");\n        if(kv.length == 2) {\n            dataMap.put(kv[0].trim(), kv[1]);\n        } else {\n            dataMap.put(kv[0], kv[0]);\n        }\n    }\n    return dataMap;\n}\n\n/**\n * 获取前端展示类型\n *\"frontType\"\n * @param comment\n */\ndef getExtendData(String comment, String key, String defaultVal) {\n    String type = defaultVal\n    if(comment == \'\' || comment == null) {\n        return defaultVal\n    }\n    String[] parts = comment.split(\";\");\n    if(parts.length<3) {\n        return type;\n    }\n    if(parts.length>=3) {\n        parts = parts[2].split(\",\");\n        HashMap<String, String> dataMap = new HashMap<>();\n        for (String item: parts) {\n            String[] kv = item.split(\":\");\n            if(kv.length == 2) {\n                dataMap.put(kv[0].trim(), kv[1]);\n            }\n        }\n        if(dataMap.containsKey(key)) {\n            type = dataMap.get(key)\n        }\n    }\n    return type\n}\n\n/**\n * 获取表前端首页类型\n *\n * @param table\n * @return\n */\ndef getTableFrontHome(table) {\n    def fontType = getExtendData(table.getComment(), \'frontHome\', \'list\')\n    return fontType;\n}\n\n/**\n * 获取制定字段类型\n *\n * @param name\n * @param fileds\n */\ndef getCol(name, fileds) {\n    def col = null;\n    fileds.each() {\n        if(name == it.dbColName) {\n            col = it;\n        }\n    }\n    return col;\n}\n\n/**\n * 获取表选项key\n *\n * @param comment\n * @param fileds\n * @return\n */\ndef getOptionKeyCol(comment, fileds) {\n    def optionStr = getExtendData(comment, \'option\', null)\n    if(optionStr == null) {\n        return null\n    }\n    String[] parts = optionStr.split(\"~\")\n    if(parts.length>0)  {\n        def col = getCol(parts[0], fileds)\n        if(col == null) {\n            return null;\n        }\n        return col;\n    }\n    return null;\n}\n\n/**\n * 获取表选项key\n *\n * @param comment\n * @param fileds\n * @return\n */\ndef getOptionValCol(comment, fileds) {\n    optionStr = getExtendData(comment, \'option\', null)\n    if(optionStr == null) {\n        return null\n    }\n    String[] parts = optionStr.split(\"~\")\n    if(parts.length>1)  {\n        def col = getCol(parts[1], fileds)\n        if(col == null) {\n            return null;\n        }\n        return col;\n    }\n    return null;\n}\n\n/**\n * 根据字段类型注解获取java类型\n *\n * @param dbType\n * @param comment\n */\ndef getJavaType(dbType, comment) {\n    def javaType = getExtendData(comment, \"javaType\", null)\n    if(javaType != null)  {\n        return javaType\n    }\n    //查看注解有没有指定后段类型\n    switch (dbType) {\n        case \'int\':\n        case \'tinyint\':\n        case \'smallint\':\n        case \'mediumint\':\n            javaType = \"Integer\"\n            break;\n        case \'bigint\':\n            javaType = \"BigInteger\"\n            break;\n        case \'bit\':\n            javaType = \'Boolean\'\n            break;\n        case \'data\':\n        case \'datetime\':\n        case \'timestamp\':\n            javaType = \"LocalDateTime\"\n            break;\n        case \'float\':\n            javaType = \"Float\"\n            break\n        case \'double\':\n            javaType = \"Double\"\n            break;\n        default:\n            javaType = \"String\"\n            break;\n    }\n    return javaType\n}','2022-12-10 00:26:16',0),(2,1736609793,'hello','hello',1,'def main(tb) {\n    String str = new String();\n    str += \"Table Name: \" + tb.getName() +\n    \"\\nTable Title: \" + tb.title + \"\\n\"\n    str += \"Table Comment: \" + tb.comment + \"\\n\"\n   str += \"\\ntest by fang${tb.name}\";\n   tb.columnList.each() {\n     str += \"=============================================================\\n\"\n     str += \"name: \"+it.name + \"\\n\"\n     str += \"dataType: \" + it.dataType + \"\\n\" \n     str += \"length: \" + it.length + \"\\n\"\n     str == \"scale: \" + it.scale + \"\\n\"\n     str += \"title: \" + it.title + \"\\n\"\n     str += \"isPk: \" + it.isPk + \"\\n\"\n     str += \"isNotNull: \" + it.isNotNull + \"\\n\"\n     str += \"isUnique: \" + it.isUnique + \"\\n\"\n     str += \"isAutoIncrement: \" + it.isAutoIncrement + \"\\n\"\n     str += \"isZeroFill: \" + it.isZeroFill + \"\\n\"\n     str += \"defaultValue: \" + it.defaultValue + \"\\n\"\n     str += \"comment: \" + it.comment + \"\\n\"\n   }\n   str\n}\n\n\ndef getCode(vo) {\n  \"helloaaaaaaaaaaaaa\"\n}\n\ndef getFileName(vo) {\n  \"a/b/c/Test2.java\"\n}','2022-12-08 00:52:37',0),(3,NULL,'testUtils','test',1,'\ndef main(tableVo) {\n  //shell = new GroovyShell()\n  //双向绑定\n  shell.parse(\'binding.setVariable(\"a\", \"b\")\', \'abc.groovy\').run()\n  //加载别的脚本，且调用方法; 推荐的其他脚本调用方式\n  def tool = shell.parse(context.codeTableTemplateService.getById(1).code)\n  //tool.run()\n  System.out.println(\"=========================\"+tool.javaName(\"test_abc_name\"))\n\n  println tableVo\n  def str = context.invokeByIdWithName(1, \"main\", \"test\")\n  //小驼峰\n  def str2 = context.runById(1,\"javaName\", \"test_by_fang\")\n  System.out.println(context.runById(1,\"javaName\", \"test_by_fang\"))\n  System.out.println(context.runById(1,\"capitalName\", \"test_by_fang\"))\n  System.out.println(context.runById(1,\"getFrontType\", \"int\", \"支付封面图片;;frontType:upload,uploadCount:1\"))\n  def t = context.codeTableService.getTableVoById(155439106);\n      \"hello    \" + t.name + \"    \" + creator + \"   \" + str + \"  \" + str2 + a\n}','2022-12-10 00:14:17',0),(1170362370,1715638274,'testzip','zip测试',1,'import work.soho.code.biz.utils.ZipUtils;\n\ndef main(t) {\n  def zip = new ZipUtils(\"/tmp/\"+t.name+\".zip\")\n  zip.appendFile(\"a.txt\", \"test by fang\")\n  zip.close()\n  \"success\"\n}','2022-12-10 02:37:38',0),(1170362371,1736609793,'JavaController','java控制器',1,'\ndef getCode(vo) {\n  \"hello\"\n}\n\ndef getFileName(vo) {\n  \"a/b/c/Test.java\"\n}','2022-12-13 21:40:08',0),(1170362372,948080641,'ReactModel','react模型',1,'\ndef getFileName(vo) {\n  \"filename\"\n}\n\ndef getCode(vo) {\n  \"file body\"\n} ','2022-12-13 21:41:39',0);
/*!40000 ALTER TABLE `code_table_template` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `code_table_template_group`
--

DROP TABLE IF EXISTS `code_table_template_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `code_table_template_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `title` varchar(255) DEFAULT NULL COMMENT '标题',
  `name` varchar(45) DEFAULT NULL COMMENT '名称',
  `base_path` varchar(1000) DEFAULT NULL COMMENT '基本写入路径',
  `main_function` varchar(45) DEFAULT NULL COMMENT '该组入口函数',
  `explain` varchar(1000) DEFAULT NULL COMMENT '说明',
  `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `sort` int(11) NOT NULL DEFAULT '0' COMMENT '排序',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1736609794 DEFAULT CHARSET=utf8 COMMENT='模本分组;;option:id~name';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `code_table_template_group`
--

LOCK TABLES `code_table_template_group` WRITE;
/*!40000 ALTER TABLE `code_table_template_group` DISABLE KEYS */;
INSERT INTO `code_table_template_group` VALUES (948080641,'前段','React','/media/fang/ssd1t/home/fang/work/html/soho-admin-front/','getPage;getFileName','获取一个前端页面','2022-12-17 06:43:57','2022-12-13 02:13:17',0),(1715638274,'其他','other','/media/fang/ssd1t/home/fang/work/java/admin',NULL,'其他不便归类','2022-12-18 11:40:12','2022-12-14 10:01:56',10),(1736609793,'Java代码','Java','/home/fang/tmp','getPage;getFileName','获取一个页面','2022-12-18 23:32:00','2022-12-12 17:43:15',1);
/*!40000 ALTER TABLE `code_table_template_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `example`
--

DROP TABLE IF EXISTS `example`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `example` (
  `id` int(11) NOT NULL,
  `title` varchar(45) DEFAULT NULL COMMENT '标题',
  `category_id` int(11) DEFAULT NULL COMMENT '分类ID;;frontType:treeSelect,foreign:example_category.id~title,frontName:category',
  `pay_id` varchar(45) DEFAULT NULL COMMENT '支付方式ID;;frontType:select,foreign:pay_info.id~title',
  `content` text COMMENT '富媒体;;frontType:editor,ignoreInList:true',
  `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='自动化样例表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `example`
--

LOCK TABLES `example` WRITE;
/*!40000 ALTER TABLE `example` DISABLE KEYS */;
INSERT INTO `example` VALUES (-1002209279,'标题4',10,'1','<p>ddddddddddddddd</p>\n','2022-11-25 06:35:17','2022-11-25 06:35:17'),(-951877630,'标题4',12,'1','<p>sdfasdf</p>\n','2022-11-25 06:32:36','2022-11-25 06:32:36'),(-524058622,'标题2',2,'1','','2022-11-25 12:53:50','2022-11-25 06:31:38'),(209944578,'标题3',8,'2','<p>saaa</p>\n','2022-11-25 13:38:21','2022-11-25 06:00:03'),(725868546,'啊啊啊啊啊啊啊啊啊啊啊',13,'1','<p>大法师地方</p>\n','2022-11-25 01:41:13','2022-11-23 01:52:53'),(1241714689,'标题4',14,'1','<p>三生三世三生三世三生三世三生三世三生三世三生三世三生三世三生三世三生三世三生三世三生三世三生三世</p>\n','2022-11-25 01:41:32','2022-11-22 00:19:08'),(2026102786,'标题4',10,'1','<p>wdrqwe&nbsp;</p>\n','2022-11-24 02:17:47','2022-11-24 02:17:47');
/*!40000 ALTER TABLE `example` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `example_category`
--

DROP TABLE IF EXISTS `example_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `example_category` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID;;optionKey',
  `title` varchar(145) DEFAULT NULL COMMENT '标题;;optionValue',
  `parent_id` int(11) DEFAULT NULL COMMENT '父级ID;;frontType:treeSelect,parent:id,foreign:example_category,frontName:Parent_Category',
  `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `only_date` date DEFAULT NULL COMMENT '只是日期;;frontType:date',
  `pay_datetime` datetime DEFAULT NULL COMMENT '日期时间;;frontType:datetime',
  `img` varchar(500) DEFAULT NULL COMMENT '图片;;frontType:upload,uploadCount:3',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8 COMMENT='自动化样例分类表;;option:id~title,tree:id~title~parent_id,frontHome:tree';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `example_category`
--

LOCK TABLES `example_category` WRITE;
/*!40000 ALTER TABLE `example_category` DISABLE KEYS */;
INSERT INTO `example_category` VALUES (1,'标题1',6,'2022-11-24 00:19:16','2022-11-18 21:58:37','2022-12-31','2022-11-23 00:27:37','https://igogo-test.oss-cn-shenzhen.aliyuncs.com/user/avatar/2022_11_23_107613429073735680.jpeg'),(2,'标题2',0,'2022-11-24 17:12:32','2022-11-18 21:58:51','2022-11-17','2022-11-26 02:11:30','https://igogo-test.oss-cn-shenzhen.aliyuncs.com/user/avatar/2022_11_24_107999845301645312.jpeg'),(3,'标题3',0,'2022-11-18 21:59:04','2022-11-18 21:59:04',NULL,NULL,''),(6,'标题1-1',1,'2022-11-23 00:52:27','2022-11-18 21:59:46','2022-11-15','2022-11-23 00:00:05','https://igogo-test.oss-cn-shenzhen.aliyuncs.com/user/avatar/2022_11_23_107615203113332736.jpeg'),(7,'标题1-2',1,'2022-11-20 01:00:36','2022-11-18 21:59:58',NULL,NULL,''),(8,'标题2-1',2,'2022-11-24 02:15:01','2022-11-18 22:00:24','2022-11-24','2022-11-24 00:00:04','https://igogo-test.oss-cn-shenzhen.aliyuncs.com/user/avatar/2022_11_24_108000770992926720.jpeg'),(9,'标题2-2',2,'2022-11-24 02:16:57','2022-11-18 22:00:37',NULL,NULL,'https://igogo-test.oss-cn-shenzhen.aliyuncs.com/user/avatar/2022_11_24_108001299781414912.jpeg'),(10,'标题2-1-1',8,'2022-11-25 01:30:06','2022-11-18 22:00:55','2022-11-10',NULL,'https://igogo-test.oss-cn-shenzhen.aliyuncs.com/user/avatar/2022_11_24_107969893772587008.jpg'),(11,'啊啊啊aaa标题2-1-2',8,'2022-11-24 02:17:08','2022-11-18 22:01:16',NULL,NULL,'https://igogo-test.oss-cn-shenzhen.aliyuncs.com/user/avatar/2022_11_24_108001338733916160.jpeg'),(12,'这也是个子标题',10,'2022-11-24 20:32:08','2022-11-22 00:31:31','2022-11-26','2022-11-18 00:25:57','https://igogo-test.oss-cn-shenzhen.aliyuncs.com/user/avatar/2022_11_22_107248660416327680.jpg'),(14,'标题3-1',3,'2022-11-24 00:02:01','2022-11-24 00:02:01','2022-11-24','2022-11-23 00:01:57','https://igogo-test.oss-cn-shenzhen.aliyuncs.com/user/avatar/2022_11_24_107967346919895040.jpeg'),(15,'标题4',0,'2022-11-25 01:12:46','2022-11-25 01:11:57','2022-11-25','2022-11-25 00:00:06','https://igogo-test.oss-cn-shenzhen.aliyuncs.com/user/avatar/2022_11_25_108347331685937152.jpeg');
/*!40000 ALTER TABLE `example_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `example_product_model`
--

DROP TABLE IF EXISTS `example_product_model`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `example_product_model` (
  `id` int(11) NOT NULL COMMENT 'ID',
  `title` varchar(45) DEFAULT NULL COMMENT '产品标题',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='产品模型;;foreign:id~example_product_model_attr.model_id~title';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `example_product_model`
--

LOCK TABLES `example_product_model` WRITE;
/*!40000 ALTER TABLE `example_product_model` DISABLE KEYS */;
/*!40000 ALTER TABLE `example_product_model` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `example_product_model_attr`
--

DROP TABLE IF EXISTS `example_product_model_attr`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `example_product_model_attr` (
  `id` int(11) NOT NULL,
  `model_id` int(11) DEFAULT NULL,
  `title` varchar(45) DEFAULT NULL,
  `data_type` tinyint(4) DEFAULT NULL COMMENT '数据类型;0:文本,1:整数,3:小数',
  `unit_name` varchar(45) DEFAULT NULL COMMENT '单位名',
  `customizable` tinyint(4) DEFAULT NULL COMMENT '可自定义;0:不可以,1:可以;frontType:select',
  `is_sales_attribute` tinyint(4) DEFAULT NULL COMMENT '是否销售属性;0:否,1:是;frontType:select',
  `created_time` datetime DEFAULT NULL,
  `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='模型属性';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `example_product_model_attr`
--

LOCK TABLES `example_product_model_attr` WRITE;
/*!40000 ALTER TABLE `example_product_model_attr` DISABLE KEYS */;
/*!40000 ALTER TABLE `example_product_model_attr` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `example_product_model_attr_option`
--

DROP TABLE IF EXISTS `example_product_model_attr_option`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `example_product_model_attr_option` (
  `id` int(11) NOT NULL,
  `attr_id` int(11) DEFAULT NULL COMMENT '属性ID;;frontName:属性',
  `value` varchar(45) DEFAULT NULL COMMENT '值',
  `updated_time` datetime DEFAULT NULL,
  `created_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='属性选项';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `example_product_model_attr_option`
--

LOCK TABLES `example_product_model_attr_option` WRITE;
/*!40000 ALTER TABLE `example_product_model_attr_option` DISABLE KEYS */;
/*!40000 ALTER TABLE `example_product_model_attr_option` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hello`
--

DROP TABLE IF EXISTS `hello`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hello` (
  `id` int(11) unsigned zerofill NOT NULL AUTO_INCREMENT,
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
/*!40000 ALTER TABLE `hello` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lot_model`
--

DROP TABLE IF EXISTS `lot_model`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `lot_model` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` varchar(255) DEFAULT NULL COMMENT '模型名称',
  `supplier_id` int(11) DEFAULT NULL COMMENT '供应商ID',
  `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1485418499 DEFAULT CHARSET=utf8 COMMENT='物联网模型';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lot_model`
--

LOCK TABLES `lot_model` WRITE;
/*!40000 ALTER TABLE `lot_model` DISABLE KEYS */;
/*!40000 ALTER TABLE `lot_model` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lot_model_item`
--

DROP TABLE IF EXISTS `lot_model_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `lot_model_item` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `model_id` int(11) DEFAULT NULL COMMENT '模型ID',
  `title` varchar(255) DEFAULT NULL COMMENT '标题',
  `unit` varchar(45) DEFAULT NULL COMMENT '单位',
  `params_name` varchar(45) DEFAULT NULL COMMENT '参数名',
  `tips` varchar(45) DEFAULT NULL COMMENT '提示',
  `extended_data` varchar(2000) DEFAULT NULL COMMENT 'wifi模块pin编号',
  `type` varchar(45) DEFAULT NULL COMMENT '数据类型',
  `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1942487043 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lot_model_item`
--

LOCK TABLES `lot_model_item` WRITE;
/*!40000 ALTER TABLE `lot_model_item` DISABLE KEYS */;
INSERT INTO `lot_model_item` VALUES (-246816767,1,'cc','ccc','ccc','ddddddccccccccc','cccc','number',NULL,'2022-11-07 19:27:37'),(688513025,1485418498,'a','a','a','aaaa','a','string',NULL,'2022-11-07 16:28:37'),(1602871297,1485418498,'bb','b','b','bbbbb','bbb','number',NULL,'2022-11-07 16:28:37'),(1942487042,1,'bbbeeeee','bb','bbbddd','bbbbb','bbb','number',NULL,'2022-11-07 19:27:37');
/*!40000 ALTER TABLE `lot_model_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lot_product`
--

DROP TABLE IF EXISTS `lot_product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `lot_product` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `model_id` int(11) DEFAULT NULL COMMENT '模型ID',
  `mac` varchar(45) DEFAULT NULL COMMENT '设备mac地址',
  `user_id` int(11) DEFAULT NULL COMMENT '所属用户',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `status` tinyint(4) DEFAULT NULL COMMENT '产品状态',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=789139472 DEFAULT CHARSET=utf8 COMMENT='产品';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lot_product`
--

LOCK TABLES `lot_product` WRITE;
/*!40000 ALTER TABLE `lot_product` DISABLE KEYS */;
/*!40000 ALTER TABLE `lot_product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lot_product_value`
--

DROP TABLE IF EXISTS `lot_product_value`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `lot_product_value` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `params_name` varchar(45) DEFAULT NULL COMMENT 'model item id',
  `product_id` int(11) DEFAULT NULL COMMENT '产品ID',
  `title` varchar(45) DEFAULT NULL COMMENT '产品标题',
  `value` varchar(45) DEFAULT NULL COMMENT '产品当前实际值',
  `given_value` varchar(45) DEFAULT NULL COMMENT '产品设置值',
  `unit` varchar(45) DEFAULT NULL COMMENT '单位',
  `type` varchar(45) DEFAULT NULL COMMENT '值类型',
  `order` int(11) DEFAULT NULL COMMENT '排序',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8 COMMENT='产品值';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lot_product_value`
--

LOCK TABLES `lot_product_value` WRITE;
/*!40000 ALTER TABLE `lot_product_value` DISABLE KEYS */;
INSERT INTO `lot_product_value` VALUES (1,'ccc',789139467,'cc',NULL,NULL,'ccc','number',1,'2022-11-07 19:11:58','2022-11-07 19:11:58'),(2,'bbbddd',789139467,'bbbeeeee',NULL,NULL,'bb','number',2,'2022-11-07 19:11:59','2022-11-07 19:11:59'),(3,'ccc',789139468,'cc',NULL,NULL,'ccc','number',1,'2022-11-07 19:27:49','2022-11-07 19:27:49'),(4,'bbbddd',789139468,'bbbeeeee',NULL,NULL,'bb','number',2,'2022-11-07 19:27:49','2022-11-07 19:27:49'),(5,'ccc',789139469,'cc',NULL,NULL,'ccc','number',1,'2022-11-07 22:11:53','2022-11-07 22:11:53'),(6,'bbbddd',789139469,'bbbeeeee',NULL,NULL,'bb','number',2,'2022-11-07 22:11:53','2022-11-07 22:11:53'),(7,'a',789139470,'a',NULL,NULL,'a','string',1,'2022-11-07 22:12:03','2022-11-07 22:12:03'),(8,'b',789139470,'bb',NULL,NULL,'b','number',2,'2022-11-07 22:12:03','2022-11-07 22:12:03'),(9,'ccc',789139471,'cc',NULL,NULL,'ccc','number',1,'2022-11-07 22:12:47','2022-11-07 22:12:47'),(10,'bbbddd',789139471,'bbbeeeee',NULL,NULL,'bb','number',2,'2022-11-07 22:12:47','2022-11-07 22:12:47');
/*!40000 ALTER TABLE `lot_product_value` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pay_info`
--

DROP TABLE IF EXISTS `pay_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pay_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `title` varchar(45) NOT NULL COMMENT '标题;;,min:1,frontType:text,min:3,max:46',
  `name` varchar(45) NOT NULL,
  `account_app_id` varchar(45) DEFAULT NULL COMMENT '账号AppId',
  `account_id` varchar(45) NOT NULL COMMENT '支付ID',
  `account_private_key` varchar(6000) NOT NULL COMMENT '支付私钥',
  `account_serial_number` varchar(128) DEFAULT NULL COMMENT '商户证书编号',
  `account_public_key` varchar(6000) NOT NULL COMMENT '支付公钥',
  `account_img` varchar(2048) NOT NULL COMMENT '支付封面图片;;frontType:upload,uploadCount:1',
  `client_type` varchar(1024) NOT NULL COMMENT '支持客户端类型;1: web,2: wap,3:app,4:微信公众号,5:微信小程序;frontType:select',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
  `platform` varchar(45) NOT NULL DEFAULT 'soho' COMMENT '平台识别名',
  `status` tinyint(1) NOT NULL COMMENT '状态; 0:禁用,1:启用;frontType:select',
  `adapter_name` varchar(100) NOT NULL COMMENT '支付驱动;wechat_jsapi,wechat_h5,wechat_app,wechat_native,alipay_wap,alipay_web;frontType:select',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='支付表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pay_info`
--

LOCK TABLES `pay_info` WRITE;
/*!40000 ALTER TABLE `pay_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `pay_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pay_order`
--

DROP TABLE IF EXISTS `pay_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pay_order` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `pay_id` int(11) NOT NULL COMMENT '支付方式ID;;frontType:select,foreign:pay_info.id~title',
  `order_no` varchar(128) NOT NULL COMMENT '支付单号',
  `tracking_no` varchar(128) NOT NULL COMMENT '外部跟踪单号',
  `transaction_id` varchar(128) DEFAULT NULL COMMENT '支付供应商跟踪ID；例如微信，支付宝支付单号',
  `amount` decimal(9,2) NOT NULL COMMENT '支付金额',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '支付单状态;1:待支付,10:已扫码,20:支付成功,30:支付失败;frontType:select',
  `payed_time` datetime DEFAULT NULL COMMENT '支付时间;;frontType:datetime',
  `notify_url` varchar(500) DEFAULT NULL COMMENT '通知地址',
  `created_time` datetime NOT NULL COMMENT '创建时间',
  `updated_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=50 DEFAULT CHARSET=utf8 COMMENT='支付单;option:id~title';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pay_order`
--

LOCK TABLES `pay_order` WRITE;
/*!40000 ALTER TABLE `pay_order` DISABLE KEYS */;
/*!40000 ALTER TABLE `pay_order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `test`
--

DROP TABLE IF EXISTS `test`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `test` (
  `id` mediumint(11) unsigned NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='测试数据';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `test`
--

LOCK TABLES `test` WRITE;
/*!40000 ALTER TABLE `test` DISABLE KEYS */;
/*!40000 ALTER TABLE `test` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_info`
--

DROP TABLE IF EXISTS `user_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `username` varchar(45) DEFAULT NULL COMMENT '用户名',
  `email` varchar(255) DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(15) DEFAULT NULL COMMENT '手机号',
  `password` varchar(255) DEFAULT NULL COMMENT '密码;;frontType:password,ignoreInList:true',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像;;frontType:upload,uploadCount:1,ignoreInList:true',
  `status` tinyint(4) DEFAULT '1' COMMENT '状态;0:禁用,1:活跃;frontType:select',
  `age` tinyint(4) unsigned DEFAULT NULL COMMENT '年龄;;frontName:年龄',
  `sex` tinyint(4) DEFAULT NULL COMMENT '性别;0:女,1:男;frontType:select,frontName:性别',
  `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
  `created_time` datetime DEFAULT NULL COMMENT '头像;;frontType:upload,uploadCount:1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username_UNIQUE` (`username`),
  UNIQUE KEY `email_UNIQUE` (`email`),
  UNIQUE KEY `phone_UNIQUE` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 COMMENT='用户信息;;option:id~username';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_info`
--

LOCK TABLES `user_info` WRITE;
/*!40000 ALTER TABLE `user_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_info` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-12-19  0:22:10
