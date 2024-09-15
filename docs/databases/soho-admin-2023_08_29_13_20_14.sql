-- MariaDB dump 10.19  Distrib 10.6.12-MariaDB, for debian-linux-gnu (x86_64)
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
  `type` tinyint(4) DEFAULT NULL COMMENT '配置信息类型;1:文本,2:Select,3:布尔值,4:文件上传编辑',
  `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `key-unique` (`key`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8 COMMENT='系统配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin_config`
--

LOCK TABLES `admin_config` WRITE;
/*!40000 ALTER TABLE `admin_config` DISABLE KEYS */;
INSERT INTO `admin_config` VALUES (1,'public','login_use_captcha','false','后台登录是否开启验证码',1,'2022-04-22 21:53:32','2020-01-01 00:00:00'),(2,'public','test-json','{\n  \"a\": \"cccbdfasdfasdfdfsadfsafaSFD\",\n  \"c\": \"dddddddddCCCCCCCCCCCCCC\"\n}','这是页面测试用的',2,'2020-01-01 00:00:00','2020-01-01 00:00:00'),(14,'public','key14ff','test','这是页面测试用的',1,'2020-01-01 00:00:00','2020-01-01 00:00:00'),(15,'public','int','100','这是页面测试用的',1,'2020-01-01 00:00:00','2020-01-01 00:00:00'),(16,'public','upload','https://igogo-test.oss-cn-shenzhen.aliyuncs.com/user/avatar/2023_3_20_150327182428737536.jpeg','文件上传测试',4,'2020-01-01 00:00:00','2020-01-01 00:00:00'),(17,'public','bool','false','bool值',3,'2020-01-01 00:00:00','2020-01-01 00:00:00'),(18,'chat','chat-system-text','你是Java程序员，根据用户提供的要求输出代码。如果是要求创建图片请输出 createImage([图片描述])','chatgpt前置训练信息',1,'2023-07-25 00:00:00','2023-07-25 00:00:00'),(19,'chat','chat-customer-service-uid','[\n  1,\n  2,\n  3\n]','客服服务用户ID，请输入Json数组',2,'2023-07-25 00:00:00','2023-07-25 00:00:00'),(20,'chat','chat-default-customer-service-avatar','https://igogo-test.oss-cn-shenzhen.aliyuncs.com/user/avatar/2023_3_20_150327182428737536.jpeg','客服会话默认头像',4,'2020-01-01 00:00:00','2020-01-01 00:00:00'),(21,'admin','admin-notice-adapter','polling','通知驱动方式；polling:轮训,longLink:长链接',1,'2022-04-22 21:53:32','2020-01-01 00:00:00');
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
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='配置分组表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin_config_group`
--

LOCK TABLES `admin_config_group` WRITE;
/*!40000 ALTER TABLE `admin_config_group` DISABLE KEYS */;
INSERT INTO `admin_config_group` VALUES (1,'public','公共配置','2020-01-01 00:00:00'),(2,'admin','管理后台','2020-01-01 00:00:00'),(3,'chat','即时通讯','2023-07-25 00:00:00');
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
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COMMENT='系统内容表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin_content`
--

LOCK TABLES `admin_content` WRITE;
/*!40000 ALTER TABLE `admin_content` DISABLE KEYS */;
INSERT INTO `admin_content` VALUES (1,'Java 教程，Java 简介','Java 教程\nJava 是高级程序设计语言\nJava 语言的数组是这样写的：int [] a=new int [10];\n运行 javac 命令后，如果成功编译没有错误的话，会出现一个 HelloWorld.class 的文件\nJava 简介\nJava 是由 Sun Microsystems 公司于 1995 年 5 月推出的 Java 面向对象程序设计语言和 Java 平台的总称。由 James Gosling 和同事们共同研发，并在 1995 年正式推出。','111','https://igogo-test.oss-cn-shenzhen.aliyuncs.com/test/2022_9_10_80965924416393216.jpg','<p>Java 教程Java 是高<img src=\"https://igogo-test.oss-cn-shenzhen.aliyuncs.com/test/2022_12_27_119948147970568192.png\"><img src=\"https://igogo-test.oss-cn-shenzhen.aliyuncs.com/test/2022_12_27_119948177922093056.png\">级程序设计语言Java 语言的数组是这样写bcvxb<img src=\"https://igogo-test.oss-cn-shenzhen.aliyuncs.com/test/2022_12_27_119946390456528896.png\">的：int [] a=new int [10];运行 javac 命令后，如果成功编译没有错误的话，会出现一个 HelloWorld.class 的文件Java 简介Java 是由 Sun Microsystems 公司于 1995 年 5 月推出的 Java 面向对象程序设计语言和 Java 平台的总称。由 James Gosling 和同事们共同研发，并在 1995 年正式推出。Java 分为三个体系： JavaSE（J2SE）（Java2 Platform Standard Edition，java 平台标准版） JavaEE (J2EE)(Java 2 Platform,Enterprise Edition，java 平台企业版) JavaME (J2ME)(Java 2 Platform Micro Edition，java 平台微型版)。 2005 年 6 月，JavaOne 大会召开，SUN 公司公开 Java SE 6。此时，Java 的各种版本已经更名以取消其中的数字 \"2\"：J2EE 更名为 Java EE, J2SE 更名为 Java SE，J2ME 更名为 Java ME。 JDK（Java Development Kit&nbsp;）：编写 Java 程序的程序员使用的软件 JRE（Java Runtime Environment）：运行 Java 程序的用户使用的软件 Server JRE&nbsp;（Java SE Runtime Environment）：服务端使用的 Java 运行环境 SDK（Software Development Kit）：软件开发工具包，在 Java 中用于描述 1998 年～2006 年之间的 JDK DAO（Data Access Object）：数据访问接口，数据访问，顾名思义就是与数据库打交道 MVC（Model View Controller）：模型 (model)－视图 (view)－控制器 (controller) 的缩写，一种软件设计典范，用于组织代码用一种业务逻辑和数据显示分离的方法 面向对象程序设计的 3 个主要特征：封装性、继承性、多态性。<strong>封装性（encapsulation）：</strong>封装是一种信息隐蔽技术，它体现于类的说明，是对象的重要特性。封装使数据和加工该数据的方法（函数）封装为一个整体，以实现独立性很强的模块，使得用户只能见到对象的外特性（对象能接受哪些消息，具有哪些处理能力），而对象的内特性（保存内部状态的私有数据和实现加工能力的算法）对用户是隐蔽的。封装的目的在于把对象的设计者和对象的使用者分开，使用者不必知晓其行为实现的细节，只须用设计者提供的消息来访问该对象。<strong>继承性：</strong>继承性是子类共享其父类数据和方法的机制。它由类的派生功能体现。一个类直接继承其他类的全部描述，同时可修改和扩充。继承具有传递性。继承分为单继承（一个子类有一父类）和多重继承（一个类有多个父类）。类的对象是各自封闭的，如果没继承性机制，则类的对象中的数据、方法就会出现大量重复。继承不仅支持系统的可重用性，而且还促进系统的可扩充性。<strong>多态性：</strong>对象根据所接收的消息而做出动作。同一消息被不同的对象接受时可产生完全不同的行动，这种现象称为多态性。利用多态性用户可发送一个通用的信息，而将所有的实现细节都留给接受消息的对象自行决定，如是，同一消息即可调用不同的方法。例如：同样是 run 方法，飞鸟调用时是飞，野兽调用时是奔跑。多态性的实现受到继承性的支持，利用类继承的层次关系，把具有通用功能的协议存放在类层次中尽可能高的地方，而将实现这一功能的不同方法置于较低层次，这样，在这些低层次上生成的对象就能给通用消息以不同的响应。在 OOPL 中可通过在派生类中重定义基类函数（定义为重载函数或虚函数）来实现多态性。SDK——soft development kit，软件开发工具包。sdk 是一个大的概念，比如开发安卓应用，你需要安卓开发工具包，叫 android sdk，比如你开发 java 程序，需要用到 java sdk，所以一般使用 sdk 这个概念，你需要在前面加上限定词。JDK—— 可以理解为 java sdk，它是编写 java 程序，使用到的工具包，为程序员提供一些已经封装好的 java 类库。<a href=\"https://www.oschina.net/action/GoToLink?url=https%3A%2F%2Fwww.runoob.com%2Fw3cnote%2Fthe-different-of-jre-and-jdk.html\" rel=\"noopener noreferrer\" target=\"_blank\"><strong>JDK 和 JRE 的区别</strong></a><strong>JRE(Java Runtime Enviroment)</strong> 是 Java 的运行环境。面向 Java 程序的使用者，而不是开发者。如果你仅下载并安装了 JRE，那么你的系统只能运行 Java 程序。JRE 是运行 Java 程序所必须环境的集合，包含 JVM 标准实现及 Java 核心类库。它包括 Java 虚拟机、Java 平台核心类和支持文件。它不包含开发工具 (编译器、调试器等)。<strong>JDK(Java Development Kit)</strong> 又称 J2SDK (Java2 Software Development Kit)，是 Java 开发工具包，它提供了 Java 的开发环境 (提供了编译器 javac 等工具，用于将 java 文件编译为 class 文件) 和运行环境 (提&nbsp;供了 JVM 和 Runtime 辅助包，用于解析 class 文件使其得到运行)。如果你下载并安装了 JDK，那么你不仅可以开发 Java 程序，也同时拥有了运行 Java 程序的平台。JDK 是整个 Java 的核心，包括了 Java 运行环境 (JRE)，一堆 Java 工具 tools.jar 和 Java 标准类库 (rt.jar)。DOS 命令操作1. 快捷键：<strong>windows+R&nbsp;</strong>呼出 <strong>DOS</strong> 窗口。2. 输入 <strong>cmd</strong>(大小写不用区分) 回车，打开 <strong>DOS</strong> 窗口。常见的命令（基本都不区分大小写）1. 切换盘符：&nbsp;盘符（就是电脑的 C,D,E.. 盘） ： （如 ：&nbsp;&nbsp;<strong>D :&nbsp;</strong>)2. 查看文件或者文件夹：&nbsp;<strong>dir</strong>3. 进入某个文件夹：&nbsp;&nbsp;<strong>cd</strong> 文件夹名4. 返回上一级目录：&nbsp;&nbsp;<strong>cd..</strong>5. 清屏：&nbsp;<strong>cls</strong>6. 删除某个文件：&nbsp;<strong>del</strong>&nbsp;&nbsp;文件名7. 删除某个文件夹： <strong>rd</strong>&nbsp;文件夹名8. 退出 <strong>DOS</strong> 窗口： <strong>exit这些都是学 Java 之前稍微要了解一下的东西，随便看看就好啦，不用花太多时间在上面哦</strong></p>','2022-12-27 01:30:13','2022-09-04 01:51:57',1,1,1,0,0,19,14,0),(2,'河南唐河：中医药变身便民惠民“香饽饽”','9月26日晚，夜幕降临，河南省南阳市唐河县体育文化广场中医药文化夜市上，来了不少体验中医健康咨询、健康查体、免费测血压、体验针灸推拿等技术服务的群众。\n据介绍，唐河县农耕文化历史悠久，中医药文化具有一定的群众基础。该县也是河南省内重要的中药材种植大县，包括国家地理标志产品唐栀子、唐半夏在内的数十种中药材种植面积就达18万亩。\n为充分发挥中医药资源优势，唐河县从中医药信息化建设、中医师签约服务、中医药宣传推广入手，促进中医药走进基层、服务群众。','keyword','https://igogo-test.oss-cn-shenzhen.aliyuncs.com/test/2022_9_14_82518662937841664.jpg','<p><span style=\"color: rgb(38, 38, 38);\">9月26日晚，夜幕降临，河南省南阳市唐河县体育文化广场中医药文化夜市上，来了不少体验中医健康咨询、健康查体、免费测血压、体验针灸推拿等技术服务的群众。据介绍，唐河县农耕文化历史悠久，中医药文化具有一定的群众基础。该县也是河南省内重要的中药材种植大县，包括国家地理标志产品唐栀子、唐半夏在内的数十种中药材种植面积就达18万亩。为充分发挥中医药资源优势，唐河县从中医药信息化建设、中医师签约服务、中医药<img src=\"https://igogo-test.oss-cn-shenzhen.aliyuncs.com/test/2022_12_27_119953267961655296.png\">宣传推广入手，促进中医药走进基层、服务群众。推进中医药信息化建设。该县投资4000余万建设全县中医药信息化系统。目前，中医药信息化系统已基本实现县、乡、村三级实时心电传输、影像传输和远程会诊。配合县中医院的“经方云”平台的医疗诊断功能、“智慧中药房系统”远程配送功能和“中医师家庭医生签约”平台预防保健及宣教功能，实现了基层群众足不出户即可享受中医药服务。开展中医师家庭签约服务。全县统一签约手册、签约流程、签约档案、签约管理。全县统一行动，每月组织开展一次巡诊，每季度开展一次大型义诊，每年对签约人员开展一次健康体检。根据不同人群需求，与中医药特色优势相结合，为签约对象提供健康状态体质辨识、健康咨询指导、健康教育、健康干预、饮食调节、情志调节等治未病服务。截至目前，唐河县中医师家庭签约服务已在全县所有乡镇铺开，共签约81万余人，约19万户。开展中医药文化夜市活动。唐河县卫健委牵头在全县开展“中医药文化夜市”，设立各类体验活动区，为群众提供中医健康咨询、健康查体、免费测血压、体验针灸推拿等服务，免费向群众提供各种不同药膳、茶饮等。（马宏、高瞻）</span>&nbsp;</p>','2022-12-27 01:49:49','2022-09-14 18:38:11',3,1,1,1,0,0,0,0),(3,'标题4','dddddddddddd','ss',NULL,'<p>ddddddddddddddddddddddd</p>','2023-02-02 12:37:04','2023-02-02 12:37:04',1,1,0,0,0,0,0,0),(4,'dddddddddddddd','dd','ddd',NULL,'<p>ddddddddd</p>','2023-02-02 12:37:37','2023-02-02 12:37:37',1,1,1,1,0,0,0,0);
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
INSERT INTO `admin_content_category` VALUES (1,'Java',0,'Java','Java','Java相关内容',1,1,'2022-09-08 22:39:08',NULL),(2,'Android',0,'android','android','android',2,1,'2022-09-08 22:39:28',NULL),(3,'Java',2,'java','java','java',3,1,'2023-07-31 22:06:22',NULL);
/*!40000 ALTER TABLE `admin_content_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `admin_dict`
--

DROP TABLE IF EXISTS `admin_dict`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `admin_dict` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `parent_id` int(11) DEFAULT NULL COMMENT '父主键',
  `code` varchar(45) DEFAULT NULL COMMENT '字典码;;isFilter:true',
  `dict_key` varchar(45) DEFAULT NULL COMMENT '字典Key',
  `dict_value` varchar(45) DEFAULT NULL COMMENT '字典名称;;isFilter:true',
  `sort` int(11) DEFAULT NULL COMMENT '排序',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `deleted` tinyint(4) DEFAULT NULL COMMENT '删除标志 0:正常 1:已删除',
  `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='系统字典';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin_dict`
--

LOCK TABLES `admin_dict` WRITE;
/*!40000 ALTER TABLE `admin_dict` DISABLE KEYS */;
INSERT INTO `admin_dict` VALUES (1,0,'sex','-1','性别',1,'性别',0,'2023-08-22 13:40:52','2023-08-22 13:40:52'),(2,1,'sex','1','男',1,'性别男',0,'2023-08-22 13:41:31','2023-08-22 13:41:31'),(3,1,'sex','2','女',2,'性别女',0,'2023-08-22 13:41:52','2023-08-22 13:41:52');
/*!40000 ALTER TABLE `admin_dict` ENABLE KEYS */;
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
INSERT INTO `admin_job` VALUES (2,'test2',1,'work.soho.quartz.biz.Hello::test(33)',0,'0/5 * * * * ?',NULL,'2023-07-22 23:37:41');
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
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8 COMMENT='管理员通知';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin_notification`
--

LOCK TABLES `admin_notification` WRITE;
/*!40000 ALTER TABLE `admin_notification` DISABLE KEYS */;
INSERT INTO `admin_notification` VALUES (3,1,'大是大非',2,'大是大非','2022-04-24 20:00:10',1),(4,1,'的说法大发',2,'的说法','2022-04-24 22:44:19',1),(5,1,'的说法大发',2,'的说法','2022-04-24 22:53:45',1),(8,1,'的点点滴滴多多多多',2,'的点点滴滴多多多','2022-04-24 23:45:21',1),(9,2,'的点点滴滴多多多多',2,'的点点滴滴多多多','2022-04-24 23:45:21',1),(10,1,'发根深蒂固',2,'sssssssssss','2022-04-25 00:50:09',1),(11,2,'发根深蒂固',2,'sssssssssss','2022-04-25 00:50:10',1),(12,1,'他吞吞吐吐',1,'顶顶顶顶顶','2022-04-30 23:33:57',1),(13,1,'test',2,'ttttttt','2022-05-04 00:26:45',1),(14,2,'ttttttttttt',2,'tttttttttttttttttt','2022-05-04 00:27:06',1),(15,2,'ttttttttt',2,'ttttttttttttttttttt','2022-05-04 00:27:15',1),(16,1,'开会啦',1,'开会啦','2023-06-03 22:15:57',1);
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
) ENGINE=InnoDB AUTO_INCREMENT=229 DEFAULT CHARSET=utf8 COMMENT='资源表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin_resource`
--

LOCK TABLES `admin_resource` WRITE;
/*!40000 ALTER TABLE `admin_resource` DISABLE KEYS */;
INSERT INTO `admin_resource` VALUES (1,'Admin','/',0,'Admin','2022-01-30 23:42:25',1,1,0,'后台管理','dashboard'),(2,'System Mange','/',1,'系统管理目录','2022-04-06 23:00:40',1,200,1,'系统管理','databasEoutlined'),(3,'Users','/admin_user',1,'用户管理','2022-01-30 23:42:25',1,1,2,'用户管理','user'),(4,'Posts','/post',1,'文章管理','2022-01-30 23:42:25',0,100,2,'文章管理','shopping-cart'),(5,'User Detail','/user/:id',1,'用户详情','2022-01-30 23:42:25',0,100,2,'用户详情','user'),(6,'Request','/request',1,'接口模拟请求','2022-01-30 23:42:25',1,100,2,'接口模拟请求','api'),(7,'UI Element','/ui',0,'UI组件','2022-01-30 23:42:25',0,100,2,'UI组件','camera-o'),(8,'Editor','/editor',1,'编辑','2022-01-30 23:42:25',0,100,2,'编辑',NULL),(9,'Charts','/Charts',1,'图标','2022-01-30 23:42:25',0,100,1,'图表','area-chart'),(10,'ECharts','/chart/ECharts',1,'ECharts','2022-01-30 23:42:25',0,100,9,'ECharts','area-chart'),(11,'HighCharts','/chart/highCharts',1,'/chart/highCharts','2022-01-30 23:42:25',0,100,9,'Rechartst','area-chart'),(12,'Rechartst','Rechartst',1,'/chart/Recharts','2022-01-30 23:42:25',0,100,1,'Rechartst','area-chart'),(13,'Role','/role',1,'角色管理','2022-01-30 23:42:25',1,1,2,'角色管理','team'),(14,'Resource','/resource',1,'资源管理','2022-01-30 23:42:25',1,1,2,'资源管理','api'),(18,'Config','/config',1,'系统配置','2022-04-06 00:06:26',1,20,2,'系统配置','settinGoutlined'),(19,'Dashboard','/dashboard',1,'首页','2022-01-30 23:42:25',1,0,1,'仪表盘','dashboard'),(21,'notifications','/admin_notification',1,'系统通知','2022-04-24 02:36:48',1,20,2,'系统通知','message'),(22,'Approval Process Manger','/approval_process',1,'审批流管理','2022-05-10 15:16:20',1,199,1,'审批流管理','edit'),(23,'Approval Process List','/approval_process',1,'审批流列表','2022-05-10 15:17:47',1,500,22,'审批流管理','hdDoutlined'),(24,'My Approval','/approval_process_myorder',1,'我的审批','2022-05-23 21:29:04',1,1,22,'我的审批','customeRservicEoutlined'),(25,'Approval Order','/approval_process_order',1,'审批单管理','2022-05-23 21:29:04',1,1,22,'审批单管理','filEpdFoutlined'),(26,'Scheduled Tasks','/admin_job',1,'计划任务管理','2022-08-01 16:59:37',1,1,2,'计划任务管理','rockeToutlined'),(27,'内容管理','/admin_content',1,'内容管理','2022-08-01 16:59:37',1,1,1,'内容管理','aligNlefToutlined'),(28,'内容列表','/admin_content',1,'内容列表','2022-08-01 16:59:37',1,1,27,'内容管理','user'),(29,'分类管理','/admin_content_category',1,'分类管理','2022-09-06 01:45:15',1,1,27,'分类管理','user'),(30,'物联网管理','/lot_model',1,'物联网管理','2022-11-06 21:15:44',1,10,1,'物联网管理','wifIoutlined'),(31,'模型列表','/lot_model',1,NULL,'2022-11-06 21:17:31',1,100,30,'模型列表','user'),(32,'产品列表','/lot_product',1,'产品列表','2022-11-06 21:18:36',1,100,30,'产品列表','user'),(33,'Pay Info','/pay_info',1,'支付管理','2022-11-07 23:09:46',1,101,1,'支付管理','alipaYoutlined'),(34,'Pay Info','/pay_info',1,'支付方式列表','2022-11-07 23:10:22',1,101,33,'支付列表','barSoutlined'),(35,'Pay Order','/pay_order',1,'支付单','2022-11-07 23:11:01',1,102,33,'支付单列表','user'),(36,'example','/example',1,'样例显示；用来开发自动化脚本用','2022-11-18 18:37:13',1,99,1,'自动化样例','appstorEadDoutlined'),(38,'example','/example',1,'样例显示；用来开发自动化脚本用','2022-11-18 18:56:44',1,101,36,'自动化样例','user'),(39,'Example Category','/example_category',1,'样例显示分类','2022-11-18 18:57:49',1,101,36,'自动化样例分类Tree','user'),(40,'User Info','/user_info',1,'用户管理','2022-11-28 00:57:13',1,102,1,'用户管理','user'),(41,'User List','/user_info',1,'用户列表','2022-11-28 00:57:55',1,101,40,'用户列表','barSoutlined'),(42,'Code Table','/code_table',1,'代码表','2022-01-01 00:00:00',1,100,2,'低代码模型','user'),(43,'Code Table Column','/code_table_column',1,'表字段','2022-11-30 15:07:38',0,101,42,'表字段','user'),(44,'Code Table List','/code_table',1,NULL,'2022-11-30 15:10:05',1,99,42,'表模型列表','user'),(45,'Code Table Template','/code_table_template',1,'代码生成模板','2022-12-08 00:34:16',1,101,42,'代码生成模板','user'),(46,'Template Group','/code_table_template_group',1,'模板分组','2022-12-12 17:38:11',1,99,42,'模板分组','user'),(47,'Example Option','/example_option',1,NULL,'2023-02-02 20:52:20',1,101,36,'样例选项','minuSsquarEoutlined'),(48,'getInfo','adminContentCategory::getInfo',2,'resource_role',NULL,0,100,29,'详情',NULL),(49,'add','adminContentCategory::add',2,'resource_role',NULL,0,100,29,'新增',NULL),(50,'remove','adminContentCategory::remove',2,'resource_role',NULL,0,100,29,'删除',NULL),(51,'list','adminContentCategory::list',2,'resource_role',NULL,0,100,29,'列表',NULL),(52,'details','adminContentCategory::details',2,'resource_role',NULL,0,100,29,'默认',NULL),(53,'edit','adminContentCategory::edit',2,'resource_role',NULL,0,100,29,'修改',NULL),(54,'getInfo','adminContent::getInfo',2,'resource_role',NULL,0,100,28,'详情',NULL),(55,'add','adminContent::add',2,'resource_role',NULL,0,100,28,'新增',NULL),(56,'remove','adminContent::remove',2,'resource_role',NULL,0,100,28,'删除',NULL),(57,'list','adminContent::list',2,'resource_role',NULL,0,100,28,'列表',NULL),(58,'edit','adminContent::edit',2,'resource_role',NULL,0,100,28,'修改',NULL),(59,'upload','adminContent::upload',2,'resource_role',NULL,0,100,28,'默认',NULL),(60,'readAll','adminNotification:readAll',2,'resource_role',NULL,0,100,21,'默认',NULL),(61,'getInfo','adminNotification:getInfo',2,'resource_role',NULL,0,100,21,'详情',NULL),(62,'add','adminNotification:add',2,'resource_role',NULL,0,100,21,'新增',NULL),(63,'remove','adminNotification:remove',2,'resource_role',NULL,0,100,21,'删除',NULL),(64,'list','adminNotification:list',2,'resource_role',NULL,0,100,21,'列表',NULL),(65,'read','adminNotification:read',2,'resource_role',NULL,0,100,21,'默认',NULL),(66,'edit','adminNotification:edit',2,'resource_role',NULL,0,100,21,'修改',NULL),(67,'myNotification','adminNotification:myNotification',2,'resource_role',NULL,0,100,21,'默认',NULL),(68,'user','adminUser:user',2,'resource_role',NULL,0,100,3,'用户信息','trademarKcirclEoutlined'),(69,'update','adminUser:update',2,'resource_role',NULL,0,100,3,'修改','bordeRoutlined'),(70,'delete','adminUser:delete',2,'resource_role',NULL,0,100,3,'删除','smilEoutlined'),(71,'create','adminUser:create',2,'resource_role',NULL,0,100,3,'新增','rotatElefToutlined'),(72,'details','adminUser:details',2,'resource_role',NULL,0,100,3,'详情','trademarKcirclEfilled'),(73,'list','adminUser:list',2,'resource_role',NULL,0,100,3,'列表',NULL),(74,'uploadAvatar','adminUser:uploadAvatar',2,'resource_role',NULL,0,100,3,'更新头像','rockeToutlined'),(75,'getInfo','lotModel::getInfo',2,'resource_role',NULL,0,100,31,'详情',NULL),(76,'add','lotModel::add',2,'resource_role',NULL,0,100,31,'新增',NULL),(77,'remove','lotModel::remove',2,'resource_role',NULL,0,100,31,'删除',NULL),(78,'list','lotModel::list',2,'resource_role',NULL,0,100,31,'列表',NULL),(79,'options','lotModel::options',2,'resource_role',NULL,0,100,31,'下拉选',NULL),(80,'edit','lotModel::edit',2,'resource_role',NULL,0,100,31,'修改',NULL),(81,'getInfo','lotProduct::getInfo',2,'resource_role',NULL,0,100,32,'详情',NULL),(82,'add','lotProduct::add',2,'resource_role',NULL,0,100,32,'新增',NULL),(83,'remove','lotProduct::remove',2,'resource_role',NULL,0,100,32,'删除',NULL),(84,'list','lotProduct::list',2,'resource_role',NULL,0,100,32,'列表',NULL),(85,'edit','lotProduct::edit',2,'resource_role',NULL,0,100,32,'修改',NULL),(86,'getInfo','payInfo::getInfo',2,'resource_role',NULL,0,100,34,'详情',NULL),(87,'add','payInfo::add',2,'resource_role',NULL,0,100,34,'新增',NULL),(88,'remove','payInfo::remove',2,'resource_role',NULL,0,100,34,'删除',NULL),(89,'list','payInfo::list',2,'resource_role',NULL,0,100,34,'列表',NULL),(90,'edit','payInfo::edit',2,'resource_role',NULL,0,100,34,'修改',NULL),(91,'getInfo','payOrder::getInfo',2,'resource_role',NULL,0,100,35,'详情',NULL),(92,'add','payOrder::add',2,'resource_role',NULL,0,100,35,'新增',NULL),(93,'remove','payOrder::remove',2,'resource_role',NULL,0,100,35,'删除',NULL),(94,'list','payOrder::list',2,'resource_role',NULL,0,100,35,'列表',NULL),(95,'edit','payOrder::edit',2,'resource_role',NULL,0,100,35,'修改',NULL),(96,'getInfo','exampleCategory::getInfo',2,'resource_role',NULL,0,100,39,'详情',NULL),(97,'add','exampleCategory::add',2,'resource_role',NULL,0,100,39,'新增',NULL),(98,'remove','exampleCategory::remove',2,'resource_role',NULL,0,100,39,'删除',NULL),(99,'list','exampleCategory::list',2,'resource_role',NULL,0,100,39,'列表',NULL),(100,'options','exampleCategory::options',2,'resource_role',NULL,0,100,39,'下拉选',NULL),(101,'edit','exampleCategory::edit',2,'resource_role',NULL,0,100,39,'修改',NULL),(102,'getInfo','example::getInfo',2,'resource_role',NULL,0,100,38,'详情',NULL),(103,'add','example::add',2,'resource_role',NULL,0,100,38,'新增',NULL),(104,'remove','example::remove',2,'resource_role',NULL,0,100,38,'删除',NULL),(105,'list','example::list',2,'resource_role',NULL,0,100,38,'列表',NULL),(106,'apply','example::apply',2,'resource_role',NULL,0,100,38,'默认',NULL),(107,'edit','example::edit',2,'resource_role',NULL,0,100,38,'修改',NULL),(108,'getInfo','exampleOption::getInfo',2,'resource_role',NULL,0,100,47,'详情',NULL),(109,'add','exampleOption::add',2,'resource_role',NULL,0,100,47,'新增',NULL),(110,'remove','exampleOption::remove',2,'resource_role',NULL,0,100,47,'删除',NULL),(111,'list','exampleOption::list',2,'resource_role',NULL,0,100,47,'列表',NULL),(112,'options','exampleOption::options',2,'resource_role',NULL,0,100,47,'下拉选',NULL),(113,'edit','exampleOption::edit',2,'resource_role',NULL,0,100,47,'修改',NULL),(114,'getInfo','userInfo::getInfo',2,'resource_role',NULL,0,100,41,'详情',NULL),(115,'add','userInfo::add',2,'resource_role',NULL,0,100,41,'新增',NULL),(116,'remove','userInfo::remove',2,'resource_role',NULL,0,100,41,'删除',NULL),(117,'list','userInfo::list',2,'resource_role',NULL,0,100,41,'列表',NULL),(118,'options','userInfo::options',2,'resource_role',NULL,0,100,41,'下拉选',NULL),(119,'edit','userInfo::edit',2,'resource_role',NULL,0,100,41,'修改',NULL),(120,'getInfo','codeTableColumn::getInfo',2,'resource_role',NULL,0,100,43,'详情',NULL),(121,'add','codeTableColumn::add',2,'resource_role',NULL,0,100,43,'新增',NULL),(122,'remove','codeTableColumn::remove',2,'resource_role',NULL,0,100,43,'删除',NULL),(123,'list','codeTableColumn::list',2,'resource_role',NULL,0,100,43,'列表',NULL),(124,'edit','codeTableColumn::edit',2,'resource_role',NULL,0,100,43,'修改',NULL),(125,'getInfo','codeTable::getInfo',2,'resource_role',NULL,0,100,42,'详情',NULL),(126,'add','codeTable::add',2,'resource_role',NULL,0,100,42,'新增',NULL),(127,'remove','codeTable::remove',2,'resource_role',NULL,0,100,42,'删除',NULL),(128,'list','codeTable::list',2,'resource_role',NULL,0,100,42,'列表',NULL),(129,'options','codeTable::options',2,'resource_role',NULL,0,100,42,'下拉选',NULL),(130,'edit','codeTable::edit',2,'resource_role',NULL,0,100,42,'修改',NULL),(131,'getInfo','codeTableTemplate::getInfo',2,'resource_role',NULL,0,100,45,'详情',NULL),(132,'add','codeTableTemplate::add',2,'resource_role',NULL,0,100,45,'新增',NULL),(133,'remove','codeTableTemplate::remove',2,'resource_role',NULL,0,100,45,'删除',NULL),(134,'list','codeTableTemplate::list',2,'resource_role',NULL,0,100,45,'列表',NULL),(135,'options','codeTableTemplate::options',2,'resource_role',NULL,0,100,45,'下拉选',NULL),(136,'edit','codeTableTemplate::edit',2,'resource_role',NULL,0,100,45,'修改',NULL),(137,'getInfo','codeTableTemplateGroup::getInfo',2,'resource_role',NULL,0,100,46,'详情',NULL),(138,'add','codeTableTemplateGroup::add',2,'resource_role',NULL,0,100,46,'新增',NULL),(139,'remove','codeTableTemplateGroup::remove',2,'resource_role',NULL,0,100,46,'删除',NULL),(140,'list','codeTableTemplateGroup::list',2,'resource_role',NULL,0,100,46,'列表',NULL),(141,'options','codeTableTemplateGroup::options',2,'resource_role',NULL,0,100,46,'下拉选',NULL),(142,'edit','codeTableTemplateGroup::edit',2,'resource_role',NULL,0,100,46,'修改',NULL),(143,'List','adminRole:list',2,NULL,'2023-06-04 01:45:49',0,100,13,'列表','minuScirclEfilled'),(144,'Update','adminRole:update',2,NULL,'2023-06-04 02:00:56',0,101,13,'更新','notificatioNoutlined'),(145,'insert','adminRole:insert',2,NULL,'2023-06-05 00:17:50',0,100,13,'新增','safetYoutlined'),(146,'delete','adminRole:delete',2,NULL,'2023-06-05 00:18:33',0,100,13,'删除','transactioNoutlined'),(147,'详情','resource::details',2,'详情','2023-06-05 00:24:33',0,100,14,'详情',NULL),(148,'路由资源列表','resource::routeVoList',2,'路由资源列表','2023-06-05 00:24:34',0,100,14,'路由资源列表',NULL),(149,'新增','resource::create',2,'新增','2023-06-05 00:24:34',0,100,14,'新增',NULL),(150,'同步项目资源','resource::admin-resource-sync',2,'同步项目资源','2023-06-05 00:24:34',0,100,14,'同步项目资源',NULL),(151,'删除','resource::delete',2,'删除','2023-06-05 00:24:34',0,100,14,'删除',NULL),(152,'更新','resource::update',2,'更新','2023-06-05 00:24:34',0,100,14,'更新',NULL),(153,'getInfo','config::getInfo',2,'配置详情','2023-06-05 00:37:14',0,100,18,'配置详情',NULL),(154,'分组列表','config::listGroup',2,'分组列表','2023-06-05 00:37:15',0,100,18,'分组列表',NULL),(155,'edit','config::edit',2,'编辑配置','2023-06-05 00:37:15',0,100,18,'编辑配置',NULL),(156,'分组详情','config::getInfoGroup',2,'分组详情','2023-06-05 00:37:15',0,100,18,'分组详情',NULL),(157,'删除分组','config::removeGroup',2,'删除分组','2023-06-05 00:37:15',0,100,18,'删除分组',NULL),(158,'list','config::list',2,'配置列表','2023-06-05 00:37:15',0,100,18,'配置列表',NULL),(159,'编辑分组','config::editGroup',2,'编辑分组','2023-06-05 00:37:15',0,100,18,'编辑分组',NULL),(160,'add','config::add',2,'添加配置','2023-06-05 00:37:15',0,100,18,'添加配置',NULL),(161,'remove','config::remove',2,'删除配置','2023-06-05 00:37:15',0,100,18,'删除配置',NULL),(162,'新增分组','config::addGroup',2,'新增分组','2023-06-05 00:37:15',0,100,18,'新增分组',NULL),(163,'list','adminJob:list',2,NULL,'2023-06-24 23:13:57',0,101,26,'列表','barSoutlined'),(164,'getInfo','adminJob:getInfo',2,'任务详情','2023-06-24 23:14:49',0,101,26,'详情','aligNlefToutlined'),(165,'add','adminJob:add',2,NULL,'2023-06-24 23:15:24',0,101,26,'新增','ellipsiSoutlined'),(166,'update','adminJob:update',2,NULL,'2023-06-24 23:15:49',0,101,26,'更新','foldeRfilled'),(167,'run','adminJob:run',2,NULL,'2023-06-24 23:16:53',0,101,26,'运行','ellipsiSoutlined'),(168,'Admin User Login Log','/admin_user_login_log',1,'用户登录日志','2023-06-25 22:30:44',1,100,2,'用户登录日志','buGfilled'),(169,'用户登录日志列表','adminUserLoginLog::list',2,'用户登录日志列表','2023-06-25 22:30:51',0,100,168,'用户登录日志列表',NULL),(170,'用户登录日志新增','adminUserLoginLog::add',2,'用户登录日志新增','2023-06-25 22:30:52',0,100,168,'用户登录日志新增',NULL),(171,'用户登录日志删除','adminUserLoginLog::remove',2,'用户登录日志删除','2023-06-25 22:30:52',0,100,168,'用户登录日志删除',NULL),(172,'用户登录日志详细信息','adminUserLoginLog::getInfo',2,'用户登录日志详细信息','2023-06-25 22:30:52',0,100,168,'用户登录日志详细信息',NULL),(173,'用户登录日志修改','adminUserLoginLog::edit',2,'用户登录日志修改','2023-06-25 22:30:52',0,100,168,'用户登录日志修改',NULL),(174,'Groovy Script','/groovy_info',1,NULL,'2023-07-23 10:08:53',1,100,2,'Groovy脚本','mobilEoutlined'),(175,'Groovy Group','/groovy_group',1,NULL,'2023-07-23 10:09:53',1,100,174,'脚本分组','foldeRoutlined'),(176,'Groovy Code','/groovy_info',1,NULL,'2023-07-23 10:10:47',1,100,174,'Groovy代码','roboToutlined'),(177,'Chat Manager','/chat_session',1,NULL,'2023-07-23 21:59:19',1,100,1,'聊天管理','message'),(178,'Customer Service','/chat_customer_service',1,NULL,'2023-07-23 22:00:29',1,100,177,'客服管理','aliyuNoutlined'),(179,'Chat Session','/chat_session',1,NULL,'2023-07-23 22:01:46',1,100,177,'聊天会话','api'),(180,'Chat Session User','/chat_session_user',1,NULL,'2023-07-23 22:02:35',1,100,177,'聊天会话用户','team'),(181,'Chat User','/chat_user',1,NULL,'2023-07-23 22:03:15',1,100,177,'聊天用户','user'),(182,'Session Message','/chat_session_message',1,NULL,'2023-07-30 03:00:32',1,100,177,'会话消息','message'),(183,'Message User','/chat_session_message_user',1,NULL,'2023-07-30 03:01:32',1,100,177,'用户消息','user-switch'),(184,'客服修改','chatCustomerService::edit',2,'客服修改','2023-07-30 17:31:17',0,100,178,'客服修改',NULL),(185,'chat_session_message删除','chatSessionMessage::remove',2,'chat_session_message删除','2023-07-30 17:31:17',0,100,182,'chat_session_message删除',NULL),(186,'chat_session_message_user删除','chatSessionMessageUser::remove',2,'chat_session_message_user删除','2023-07-30 17:31:17',0,100,183,'chat_session_message_user删除',NULL),(187,'groovy分组;;option:id~title详细信息','groovyGroup::getInfo',2,'groovy分组;;option:id~title详细信息','2023-07-30 17:31:17',0,100,175,'groovy分组;;option:id~title详细信息',NULL),(188,'聊天用户删除','chatUser::remove',2,'聊天用户删除','2023-07-30 17:31:17',0,100,181,'聊天用户删除',NULL),(189,'chat_session_message修改','chatSessionMessage::edit',2,'chat_session_message修改','2023-07-30 17:31:17',0,100,182,'chat_session_message修改',NULL),(190,'groovy代码;;列表','groovyInfo::list',2,'groovy代码;;列表','2023-07-30 17:31:17',0,100,174,'groovy代码;;列表',NULL),(191,'chat_session_message_user详细信息','chatSessionMessageUser::getInfo',2,'chat_session_message_user详细信息','2023-07-30 17:31:17',0,100,183,'chat_session_message_user详细信息',NULL),(192,'groovy代码;;修改','groovyInfo::edit',2,'groovy代码;;修改','2023-07-30 17:31:17',0,100,174,'groovy代码;;修改',NULL),(193,'客服删除','chatCustomerService::remove',2,'客服删除','2023-07-30 17:31:17',0,100,178,'客服删除',NULL),(194,'客服列表','chatCustomerService::list',2,'客服列表','2023-07-30 17:31:17',0,100,178,'客服列表',NULL),(195,'聊天会话删除','chatSession::remove',2,'聊天会话删除','2023-07-30 17:31:17',0,100,177,'聊天会话删除',NULL),(196,'客服详细信息','chatCustomerService::getInfo',2,'客服详细信息','2023-07-30 17:31:17',0,100,178,'客服详细信息',NULL),(197,'groovy分组;;option:id~title修改','groovyGroup::edit',2,'groovy分组;;option:id~title修改','2023-07-30 17:31:17',0,100,175,'groovy分组;;option:id~title修改',NULL),(198,'聊天用户列表','chatUser::list',2,'聊天用户列表','2023-07-30 17:31:17',0,100,181,'聊天用户列表',NULL),(199,'会话用户列表新增','chatSessionUser::add',2,'会话用户列表新增','2023-07-30 17:31:17',0,100,180,'会话用户列表新增',NULL),(200,'groovy代码;;新增','groovyInfo::add',2,'groovy代码;;新增','2023-07-30 17:31:17',0,100,174,'groovy代码;;新增',NULL),(201,'聊天会话新增','chatSession::add',2,'聊天会话新增','2023-07-30 17:31:17',0,100,177,'聊天会话新增',NULL),(202,'groovy分组;;option:id~title删除','groovyGroup::remove',2,'groovy分组;;option:id~title删除','2023-07-30 17:31:18',0,100,175,'groovy分组;;option:id~title删除',NULL),(203,'groovy分组;;option:id~titleOptions','groovyGroup::options',2,'groovy分组;;option:id~titleOptions','2023-07-30 17:31:18',0,100,175,'groovy分组;;option:id~titleOptions',NULL),(204,'聊天用户详细信息','chatUser::getInfo',2,'聊天用户详细信息','2023-07-30 17:31:18',0,100,181,'聊天用户详细信息',NULL),(205,'会话用户列表修改','chatSessionUser::edit',2,'会话用户列表修改','2023-07-30 17:31:18',0,100,180,'会话用户列表修改',NULL),(206,'聊天会话修改','chatSession::edit',2,'聊天会话修改','2023-07-30 17:31:18',0,100,177,'聊天会话修改',NULL),(207,'groovy分组;;option:id~title列表','groovyGroup::list',2,'groovy分组;;option:id~title列表','2023-07-30 17:31:18',0,100,175,'groovy分组;;option:id~title列表',NULL),(208,'聊天会话详细信息','chatSession::getInfo',2,'聊天会话详细信息','2023-07-30 17:31:18',0,100,177,'聊天会话详细信息',NULL),(209,'会话用户列表删除','chatSessionUser::remove',2,'会话用户列表删除','2023-07-30 17:31:18',0,100,180,'会话用户列表删除',NULL),(210,'groovy分组;;option:id~title新增','groovyGroup::add',2,'groovy分组;;option:id~title新增','2023-07-30 17:31:18',0,100,175,'groovy分组;;option:id~title新增',NULL),(211,'会话用户列表列表','chatSessionUser::list',2,'会话用户列表列表','2023-07-30 17:31:18',0,100,180,'会话用户列表列表',NULL),(212,'chat_session_message列表','chatSessionMessage::list',2,'chat_session_message列表','2023-07-30 17:31:18',0,100,182,'chat_session_message列表',NULL),(213,'chat_session_message_user新增','chatSessionMessageUser::add',2,'chat_session_message_user新增','2023-07-30 17:31:18',0,100,183,'chat_session_message_user新增',NULL),(214,'groovy代码;;详细信息','groovyInfo::getInfo',2,'groovy代码;;详细信息','2023-07-30 17:31:18',0,100,174,'groovy代码;;详细信息',NULL),(215,'客服新增','chatCustomerService::add',2,'客服新增','2023-07-30 17:31:18',0,100,178,'客服新增',NULL),(216,'chat_session_message_user列表','chatSessionMessageUser::list',2,'chat_session_message_user列表','2023-07-30 17:31:18',0,100,183,'chat_session_message_user列表',NULL),(217,'groovy代码;;删除','groovyInfo::remove',2,'groovy代码;;删除','2023-07-30 17:31:18',0,100,174,'groovy代码;;删除',NULL),(218,'chat_session_message新增','chatSessionMessage::add',2,'chat_session_message新增','2023-07-30 17:31:18',0,100,182,'chat_session_message新增',NULL),(219,'chat_session_message详细信息','chatSessionMessage::getInfo',2,'chat_session_message详细信息','2023-07-30 17:31:18',0,100,182,'chat_session_message详细信息',NULL),(220,'聊天会话列表','chatSession::list',2,'聊天会话列表','2023-07-30 17:31:18',0,100,177,'聊天会话列表',NULL),(221,'会话用户列表详细信息','chatSessionUser::getInfo',2,'会话用户列表详细信息','2023-07-30 17:31:18',0,100,180,'会话用户列表详细信息',NULL),(222,'聊天用户新增','chatUser::add',2,'聊天用户新增','2023-07-30 17:31:19',0,100,181,'聊天用户新增',NULL),(223,'聊天用户修改','chatUser::edit',2,'聊天用户修改','2023-07-30 17:31:19',0,100,181,'聊天用户修改',NULL),(224,'chat_session_message_user修改','chatSessionMessageUser::edit',2,'chat_session_message_user修改','2023-07-30 17:31:19',0,100,183,'chat_session_message_user修改',NULL),(225,'Admin Dict','/admin_dict',1,'系统字典','2023-08-22 13:40:02',1,100,2,'系统字典','bordeRtoPoutlined'),(226,'User Friend','/chat_user_friend',1,NULL,'2023-08-24 18:06:16',1,100,177,'用户好友','team'),(227,'User Notice','/chat_user_notice',1,NULL,'2023-08-26 12:31:54',1,100,177,'用户通知','message'),(228,'Friend Apply','/chat_user_friend_apply',1,NULL,'2023-08-26 12:33:26',1,100,177,'好友申请','columNheighToutlined');
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
) ENGINE=InnoDB AUTO_INCREMENT=68 DEFAULT CHARSET=utf8 COMMENT='角色资源关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin_role_resource`
--

LOCK TABLES `admin_role_resource` WRITE;
/*!40000 ALTER TABLE `admin_role_resource` DISABLE KEYS */;
INSERT INTO `admin_role_resource` VALUES (47,1,19,'2022-11-27 17:03:23'),(54,1,2,'2022-11-27 17:03:24'),(55,2,19,'2022-12-07 14:17:57'),(56,2,27,'2022-12-07 14:17:57'),(57,2,30,'2022-12-07 14:17:58'),(58,2,36,'2022-12-07 14:17:58'),(59,2,9,'2022-12-07 14:17:58'),(60,2,33,'2022-12-07 14:17:58'),(61,2,12,'2022-12-07 14:17:58'),(62,2,40,'2022-12-07 14:17:59'),(63,2,22,'2022-12-07 14:17:59'),(65,1,29,'2023-06-04 02:01:34'),(66,1,33,'2023-06-04 15:48:40'),(67,1,38,'2023-06-04 15:48:40');
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
INSERT INTO `admin_user` VALUES (1,'admin','15873164073','adminNickNamaadd','fang.liu','https://igogo-test.oss-cn-shenzhen.aliyuncs.com/user/avatar/2022_9_10_80891698921607168.jpg','i@liufang.org.cn','$2a$10$IJtvqDal32z44PV.E1VSL..57biCuFkIqWDk2fVB3RCkuCmOQLNQu','2023-06-03 21:31:16','2022-01-23 17:32:56',1,99,0),(2,'guest','15833333333','打发十aa',NULL,'https://igogo-test.oss-cn-shenzhen.aliyuncs.com/user/avatar/2022_4_23_30063941451255808.jpg','555i@liufang.org.cn','$2a$10$VeTSOwXxjfSDxcEKQgG9nuWVQRwkp3iS0DVDfB6xgbFHPIqi9dR7m','2022-03-27 20:49:48','2022-03-27 20:49:48',2,3,0),(3,'a','15873164074','a','a','https://igogo-test.oss-cn-shenzhen.aliyuncs.com/user/avatar/2022_4_7_190464236.jpg','a@liufang.org.cn','$2a$10$bPy9GxOynsydi64yULtvfee9zuoCFf0cb/VoXrhnrvoTa3wHOOLky','2022-01-23 17:32:56','2022-01-23 17:32:56',1,195,0),(4,'b','15873164075','b','b','https://igogo-test.oss-cn-shenzhen.aliyuncs.com/user/avatar/2022_4_7_190464236.jpg','b@liufang.org.cn','$2a$10$bPy9GxOynsydi64yULtvfee9zuoCFf0cb/VoXrhnrvoTa3wHOOLky','2022-01-23 17:32:56','2022-01-23 17:32:56',1,195,0),(5,'c','15873164076','c','c','https://igogo-test.oss-cn-shenzhen.aliyuncs.com/user/avatar/2022_4_7_190464236.jpg','c@liufang.org.cn','$2a$10$bPy9GxOynsydi64yULtvfee9zuoCFf0cb/VoXrhnrvoTa3wHOOLky','2022-01-23 17:32:56','2022-01-23 17:32:56',1,195,1);
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
) ENGINE=InnoDB AUTO_INCREMENT=271 DEFAULT CHARSET=utf8 COMMENT='用户登录日志';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin_user_login_log`
--

LOCK TABLES `admin_user_login_log` WRITE;
/*!40000 ALTER TABLE `admin_user_login_log` DISABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COMMENT='审批流';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `approval_process`
--

LOCK TABLES `approval_process` WRITE;
/*!40000 ALTER TABLE `approval_process` DISABLE KEYS */;
INSERT INTO `approval_process` VALUES (1,'1','请假申请',1,'',10,'[{\"key\":\"CONTENT\",\"title\":\"请假事由\",\"type\":\"textarea\",\"tips\":\"请假事由详细描述\"},{\"key\":\"START_TIME\",\"title\":\"开始时间\",\"type\":\"datetime\",\"tips\":\"请假开始时间\"},{\"key\":\"END_TIME\",\"title\":\"结束时间\",\"type\":\"datetime\",\"tips\":\"请假结束时间\"}]','2022-06-01 11:27:11'),(2,'2','调休申请',1,'',10,'[\n{\"key\":\"CONTENT\", \"title\":\"申请描述\"},\n{\"key\":\"START_TIME\", \"title\":\"开始时间\"},\n{\"key\":\"END_TIME\", \"title\":\"结束时间\"}\n]','2022-05-29 01:15:20'),(3,'3','印章申请',1,'',10,'[{\"key\":\"CONTENT\",\"title\":\"申请描述\",\"type\":\"textarea\",\"tips\":\"用章事由详细\"},{\"key\":\"START_TIME\",\"title\":\"开始时间\",\"type\":\"datetime\",\"tips\":\"用章开始时间\"},{\"key\":\"END_TIME\",\"title\":\"结束时间\",\"type\":\"datetime\",\"tips\":\"用章结束时间\"}]','2022-06-01 11:26:37'),(4,'4','样例审批',1,'',10,'[{\"key\":\"TITLE\",\"title\":\"标题\",\"type\":\"input\",\"tips\":\"审批标题\"},{\"key\":\"CONTENT\",\"title\":\"审批内容\",\"type\":\"editor\",\"tips\":\"审批内容\"}]','2022-12-30 12:05:36');
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
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8 COMMENT='审批流节点';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `approval_process_node`
--

LOCK TABLES `approval_process_node` WRITE;
/*!40000 ALTER TABLE `approval_process_node` DISABLE KEYS */;
INSERT INTO `approval_process_node` VALUES (3,2,0,1,0,'2022-05-29 01:15:20'),(4,2,1,2,1,'2022-05-29 01:15:20'),(13,3,0,1,0,'2022-06-01 11:26:37'),(14,3,1,2,1,'2022-06-01 11:26:37'),(15,1,0,1,0,'2022-06-01 11:27:11'),(16,1,1,2,1,'2022-06-01 11:27:12'),(18,4,0,1,0,'2022-12-30 12:05:37');
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
  `status` tinyint(4) DEFAULT NULL COMMENT '审批状态; 0 待审批  1 审批完成',
  `apply_status` tinyint(4) DEFAULT NULL COMMENT '审批处理结果;0:失败  1:驳回 2:同意',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `content` varchar(20000) DEFAULT NULL COMMENT '审核单内容',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8 COMMENT='审批单';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `approval_process_order`
--

LOCK TABLES `approval_process_order` WRITE;
/*!40000 ALTER TABLE `approval_process_order` DISABLE KEYS */;
INSERT INTO `approval_process_order` VALUES (1,3,1,'43119611092144128',0,0,'2022-05-29 01:20:15','[{\"key\":\"CONTENT\",\"title\":\"显示标题\",\"content\":\"新增合同， 申请盖章\"}]'),(2,1,1,'43119734970912768',0,0,'2022-05-29 01:20:44','[{\"key\":\"CONTENT\",\"title\":\"显示标题\",\"content\":\"家里有事， 请假一天\"}]'),(3,1,1,'43320174387924992',0,0,'2022-05-29 14:37:14','[{\"key\":\"CONTENT\",\"title\":\"显示标题\",\"content\":\"惆怅长岑长错错\"},{\"key\":\"START_TIME\",\"title\":\"显示标题\",\"content\":\"2022-05-10 00:00:00\"},{\"key\":\"END_TIME\",\"title\":\"显示标题\",\"content\":\"2022-05-29 00:00:00\"}]'),(4,1,1,'43328528598437888',0,0,'2022-05-29 15:10:26','[{\"key\":\"START_TIME\",\"title\":\"显示标题\",\"content\":\"2022-05-29 00:00:00\"},{\"key\":\"END_TIME\",\"title\":\"显示标题\",\"content\":\"2022-05-29 00:00:00\"}]'),(5,1,1,'43338500841410560',0,0,'2022-05-29 15:50:03','[{\"key\":\"START_TIME\",\"title\":\"显示标题\",\"content\":\"2022-05-13 00:00:00\"},{\"key\":\"END_TIME\",\"title\":\"显示标题\",\"content\":\"2022-05-29 00:00:00\"}]'),(6,1,1,'43355288803217408',0,0,'2022-05-29 16:56:48','[{\"key\":\"CONTENT\",\"title\":\"申请描述\",\"content\":\"热热热热热若若若若若从\"},{\"key\":\"START_TIME\",\"title\":\"开始时间\",\"content\":\"2022-05-20 00:00:00\"},{\"key\":\"END_TIME\",\"title\":\"结束时间\",\"content\":\"2022-05-29 00:00:00\"}]'),(7,1,1,'43376085097779200',1,1,'2022-05-29 18:19:24','[{\"key\":\"CONTENT\",\"title\":\"申请描述\",\"content\":\"家里有事， 请假三天；望领导批准\"},{\"key\":\"START_TIME\",\"title\":\"开始时间\",\"content\":\"2022-05-29 00:00:00\"},{\"key\":\"END_TIME\",\"title\":\"结束时间\",\"content\":\"2022-05-31 00:00:00\"}]'),(8,1,1,'45116188841218048',0,0,'2022-06-03 13:33:57','[{\"key\":\"TITLE\",\"title\":\"标题\",\"content\":\"这里是测试标题\"},{\"key\":\"CONTENT\",\"title\":\"内容\",\"content\":\"这里是测试审批内容\"}]'),(9,1,1,'45116264292552704',0,0,'2022-06-03 13:34:15','[{\"key\":\"TITLE\",\"title\":\"标题\",\"content\":\"这里是测试标题\"},{\"key\":\"CONTENT\",\"title\":\"内容\",\"content\":\"这里是测试审批内容\"}]'),(10,1,1,'45117007200260096',0,0,'2022-06-03 13:37:12','[{\"key\":\"CONTENT\",\"title\":\"内容\",\"content\":\"这里是测试审批内容\"},{\"key\":\"START_TIME\",\"title\":\"开始时间\",\"content\":\"2022-06-03 00:00:00\"},{\"key\":\"END_TIME\",\"title\":\"结束时间\",\"content\":\"2023-06-03 00:00:00\"}]'),(11,1,1,'45117229670338560',0,0,'2022-06-03 13:38:05','[{\"key\":\"CONTENT\",\"title\":\"内容\",\"content\":\"世界这么大，我想去看看！\"},{\"key\":\"START_TIME\",\"title\":\"开始时间\",\"content\":\"2022-06-03 00:00:00\"},{\"key\":\"END_TIME\",\"title\":\"结束时间\",\"content\":\"2023-06-03 00:00:00\"}]'),(12,1,1,'63822691039186944',0,0,'2022-07-25 04:27:00','[{\"key\":\"CONTENT\",\"title\":\"内容\",\"content\":\"世界这么大，我想去看看！\"},{\"key\":\"START_TIME\",\"title\":\"开始时间\",\"content\":\"2022-06-03 00:00:00\"},{\"key\":\"END_TIME\",\"title\":\"结束时间\",\"content\":\"2023-06-03 00:00:00\"}]'),(13,4,1,'-1002209279',1,2,'2022-12-29 23:58:58','[{\"key\":\"TITLE\",\"content\":\"标题4\"},{\"key\":\"TITLE\",\"content\":\"\"}]'),(14,4,1,'-951877630',1,2,'2022-12-30 00:08:00','[{\"key\":\"TITLE\",\"content\":\"申请测试标题\"},{\"key\":\"TITLE\",\"content\":\"<p>设立是申请测试内容</p>\\n\"}]'),(15,4,1,'2026102786',1,1,'2022-12-30 00:51:45','[{\"key\":\"TITLE\",\"content\":\"测试中\"},{\"key\":\"TITLE\",\"content\":\"<p>wdrqwe</p>\\n\"}]'),(16,4,1,'725868546',1,2,'2022-12-30 00:56:48','[{\"key\":\"TITLE\",\"content\":\"啊啊啊啊啊啊啊啊啊啊啊\"},{\"key\":\"TITLE\",\"content\":\"\"}]'),(17,4,1,'1241714689',1,2,'2022-12-30 01:00:18','[{\"key\":\"TITLE\",\"content\":\"已测试\"},{\"key\":\"TITLE\",\"content\":\"\"}]'),(18,4,1,'-524058622',0,0,'2022-12-30 01:41:51','[{\"key\":\"TITLE\",\"content\":\"标题2\"},{\"key\":\"TITLE\",\"content\":\"<p>bhdsfgsdfg</p>\\n\"}]'),(19,4,1,'2026102787',1,2,'2022-12-30 01:53:20','[{\"key\":\"TITLE\",\"content\":\"标题4\"},{\"key\":\"TITLE\",\"content\":\"\"}]'),(20,4,1,'2026102788',0,0,'2022-12-30 12:29:41','[{\"contnet\":\"aaaaaa\",\"type\":\"input\",\"key\":\"TITLE\"},{\"contnet\":\"\",\"type\":\"input\",\"key\":\"TITLE\"}]'),(21,4,1,'2026102789',0,0,'2022-12-30 12:32:19','[{\"type\":\"input\",\"key\":\"TITLE\",\"content\":\"bbbbbbbbbb\"},{\"type\":\"input\",\"key\":\"TITLE\",\"content\":\"\"}]'),(22,4,1,'2026102790',0,0,'2022-12-30 13:02:35','[{\"type\":\"input\",\"key\":\"TITLE\",\"content\":\"aaaaaaaaafffffffffffffffffffff\"},{\"type\":\"input\",\"key\":\"TITLE\",\"content\":\"\"}]'),(23,4,1,'2026102791',0,0,'2022-12-30 13:08:00','[{\"type\":\"input\",\"key\":\"TITLE\",\"content\":\"tttttttttt\"},{\"type\":\"input\",\"key\":\"TITLE\",\"content\":\"\"}]'),(24,4,1,'2026102792',0,0,'2022-12-30 13:11:59','[{\"type\":\"input\",\"key\":\"TITLE\",\"content\":\"ttttaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\"},{\"type\":\"editor\",\"key\":\"CONTENT\",\"content\":\"aaaa<i>aa</i>aa<pre>aa</pre>aaaaaaaaaa\"}]'),(25,4,1,'2026102793',1,2,'2022-12-30 13:22:12','[{\"title\":\"标题\",\"type\":\"input\",\"key\":\"TITLE\",\"content\":\"dfasdf\"},{\"title\":\"审批内容\",\"type\":\"editor\",\"key\":\"CONTENT\",\"content\":\"\"}]'),(26,4,1,'2026102794',1,2,'2022-12-30 13:35:39','[{\"title\":\"标题\",\"type\":\"input\",\"key\":\"TITLE\",\"content\":\"test\"},{\"title\":\"审批内容\",\"type\":\"editor\",\"key\":\"CONTENT\",\"content\":\"<p>tasefsdfasdfdfasdfasdf</p>\"}]'),(27,4,1,'121218473148837888',1,2,'2022-12-30 13:37:15','[{\"title\":\"标题\",\"type\":\"input\",\"key\":\"TITLE\",\"content\":\"test\"},{\"title\":\"审批内容\",\"type\":\"editor\",\"key\":\"CONTENT\",\"content\":\"cccccccccccccccccccccccc\"}]'),(28,4,1,'121246771237122048',1,2,'2022-12-30 15:29:41','[{\"title\":\"标题\",\"type\":\"input\",\"key\":\"TITLE\",\"content\":\"test\"},{\"title\":\"审批内容\",\"type\":\"editor\",\"key\":\"CONTENT\",\"content\":\"<h1>测试</h1><p><br></p><p>dabcccccccccccc</p>\"}]'),(29,4,1,'121247379251818496',1,2,'2022-12-30 15:32:06','[{\"title\":\"标题\",\"type\":\"input\",\"key\":\"TITLE\",\"content\":\"test\"},{\"title\":\"审批内容\",\"type\":\"editor\",\"key\":\"CONTENT\",\"content\":\"<p>cxvb xczvzxcvxcv</p>\"}]'),(30,1,1,'121257462593515520',0,0,'2022-12-30 16:12:10','[{\"title\":\"请假事由\",\"type\":\"textarea\",\"key\":\"CONTENT\",\"content\":\"tet\"},{\"title\":\"开始时间\",\"type\":\"datetime\",\"key\":\"START_TIME\",\"content\":\"2022-12-30 00:00:00\"},{\"title\":\"结束时间\",\"type\":\"datetime\",\"key\":\"END_TIME\",\"content\":\"2022-12-30 00:00:00\"}]'),(31,4,1,'2026102796',1,2,'2022-12-30 23:55:22','[{\"title\":\"标题\",\"type\":\"input\",\"key\":\"TITLE\",\"content\":\"标题4\"},{\"title\":\"审批内容\",\"type\":\"editor\",\"key\":\"CONTENT\",\"content\":\"<p>cvxcvxzvcx</p>\\n\"}]'),(32,4,1,'2026102795',1,2,'2023-02-05 14:52:05','[{\"title\":\"标题\",\"type\":\"input\",\"key\":\"TITLE\",\"content\":\"标题1\"},{\"title\":\"审批内容\",\"type\":\"editor\",\"key\":\"CONTENT\",\"content\":\"<p>dfsdafsdafsdafdfsadfdsfasdfsdfsadfsadf</p>\"}]');
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
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=utf8 COMMENT='审批单节点';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `approval_process_order_node`
--

LOCK TABLES `approval_process_order_node` WRITE;
/*!40000 ALTER TABLE `approval_process_order_node` DISABLE KEYS */;
INSERT INTO `approval_process_order_node` VALUES (1,1,0,1,30,'同意请假； 请保持电话畅通','2022-05-29 01:20:15',0,'2022-05-29 01:20:22'),(2,1,1,2,10,NULL,'2022-05-29 01:20:15',1,NULL),(3,2,0,1,40,'test by fang','2022-05-29 01:20:45',0,'2022-06-01 00:23:05'),(4,2,5,2,0,NULL,'2022-05-29 01:20:45',2,NULL),(5,3,0,1,30,'同意请假； 请保持电话畅通','2022-05-29 14:37:15',0,'2022-05-29 21:50:32'),(6,3,1,2,10,NULL,'2022-05-29 14:37:15',1,NULL),(7,4,0,1,30,'同意请假； 请保持电话畅通','2022-05-29 15:10:27',0,'2022-05-29 15:24:51'),(8,4,1,2,10,NULL,'2022-05-29 15:10:27',1,NULL),(9,5,0,1,40,'测试转发','2022-05-29 15:50:04',0,'2022-06-01 00:19:50'),(10,5,4,2,0,NULL,'2022-05-29 15:50:04',2,NULL),(11,6,0,1,30,'同意请假； 请保持电话畅通','2022-05-29 16:56:48',0,'2022-05-29 21:54:23'),(12,6,1,2,10,NULL,'2022-05-29 16:56:49',1,NULL),(13,7,0,1,20,'延期请假； 请优先工作进度，力求月底完工','2022-05-29 18:19:25',0,'2022-05-29 18:49:46'),(14,7,1,2,0,NULL,'2022-05-29 18:19:26',1,NULL),(15,5,1,4,0,NULL,NULL,1,NULL),(16,2,1,5,10,NULL,'2022-06-01 00:23:05',1,NULL),(17,8,0,1,10,NULL,'2022-06-03 13:33:57',0,NULL),(18,8,1,2,0,NULL,'2022-06-03 13:33:57',1,NULL),(19,9,0,1,10,NULL,'2022-06-03 13:34:15',0,NULL),(20,9,1,2,0,NULL,'2022-06-03 13:34:15',1,NULL),(21,10,0,1,10,NULL,'2022-06-03 13:37:12',0,NULL),(22,10,1,2,0,NULL,'2022-06-03 13:37:12',1,NULL),(23,11,0,1,10,NULL,'2022-06-03 13:38:05',0,NULL),(24,11,1,2,0,NULL,'2022-06-03 13:38:05',1,NULL),(25,12,0,1,10,NULL,'2022-07-25 04:27:02',0,NULL),(26,12,1,2,0,NULL,'2022-07-25 04:27:03',1,NULL),(27,13,0,1,30,NULL,'2022-12-29 23:58:58',0,'2022-12-30 00:01:40'),(28,14,0,1,30,'审批通过','2022-12-30 00:08:00',0,'2022-12-30 00:39:24'),(29,15,0,1,20,NULL,'2022-12-30 00:51:45',0,'2022-12-30 00:51:57'),(30,16,0,1,30,'通过','2022-12-30 00:56:48',0,'2022-12-30 00:56:59'),(31,17,0,1,30,'同意','2022-12-30 01:00:19',0,'2022-12-30 01:00:33'),(32,18,0,1,10,NULL,'2022-12-30 01:41:51',0,NULL),(33,19,0,1,30,NULL,'2022-12-30 01:53:20',0,'2022-12-30 01:53:34'),(34,20,0,1,10,NULL,'2022-12-30 12:29:41',0,NULL),(35,21,0,1,10,NULL,'2022-12-30 12:32:19',0,NULL),(36,22,0,1,10,NULL,'2022-12-30 13:02:35',0,NULL),(37,23,0,1,10,NULL,'2022-12-30 13:08:00',0,NULL),(38,24,0,1,10,NULL,'2022-12-30 13:11:59',0,NULL),(39,25,0,1,30,NULL,'2022-12-30 13:22:12',0,'2022-12-30 13:23:04'),(40,26,0,1,30,NULL,'2022-12-30 13:35:39',0,'2023-02-05 14:43:59'),(41,27,0,1,30,'aaaaaaaaa','2022-12-30 13:37:15',0,'2022-12-30 13:37:24'),(42,28,0,1,30,NULL,'2022-12-30 15:29:42',0,'2022-12-30 15:29:48'),(43,29,0,1,30,'test','2022-12-30 15:32:06',0,'2022-12-30 15:32:19'),(44,30,0,1,30,NULL,'2022-12-30 16:12:11',0,'2022-12-30 16:12:18'),(45,30,1,2,10,NULL,'2022-12-30 16:12:11',1,NULL),(46,31,0,1,30,'ssssssss','2022-12-30 23:55:22',0,'2022-12-30 23:55:31'),(47,32,0,1,30,NULL,'2023-02-05 14:52:05',0,'2023-02-05 20:49:17');
/*!40000 ALTER TABLE `approval_process_order_node` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat_customer_service`
--

DROP TABLE IF EXISTS `chat_customer_service`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `chat_customer_service` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL,
  `status` tinyint(4) DEFAULT NULL COMMENT '客服状态;1:下线,2:活跃,3:禁用;frontType:select',
  `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='客服';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_customer_service`
--

LOCK TABLES `chat_customer_service` WRITE;
/*!40000 ALTER TABLE `chat_customer_service` DISABLE KEYS */;
INSERT INTO `chat_customer_service` VALUES (1,1,2,'2023-07-24 00:45:13','2023-07-23 22:12:50'),(2,3,2,'2023-07-24 00:45:13','2023-07-24 00:45:13');
/*!40000 ALTER TABLE `chat_customer_service` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat_session`
--

DROP TABLE IF EXISTS `chat_session`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `chat_session` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `type` tinyint(4) DEFAULT NULL COMMENT '会话类型;1:私聊,2:群聊,3:群组;frontType:select',
  `status` tinyint(4) DEFAULT NULL COMMENT '状态;1:活跃,2:禁用,3:删除;frontType:select',
  `title` varchar(45) DEFAULT NULL COMMENT '会话标题',
  `avatar` varchar(255) DEFAULT NULL COMMENT '会话头像',
  `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `last_message_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8 COMMENT='聊天会话';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_session`
--

LOCK TABLES `chat_session` WRITE;
/*!40000 ALTER TABLE `chat_session` DISABLE KEYS */;
INSERT INTO `chat_session` VALUES (1,4,1,'会话-1','https://gw.alicdn.com/tfs/TB1U7FBiAT2gK0jSZPcXXcKkpXa-108-108.jpg','2023-07-30 17:00:54','2023-07-28 23:57:58','2023-08-19 01:11:10'),(2,4,1,'会话-2','https://gw.alicdn.com/tfs/TB1U7FBiAT2gK0jSZPcXXcKkpXa-108-108.jpg','2023-08-27 22:42:51','2023-07-29 03:12:29','2023-08-19 01:11:10'),(3,4,1,'会话-3','https://gw.alicdn.com/tfs/TB1U7FBiAT2gK0jSZPcXXcKkpXa-108-108.jpg','2023-07-30 01:24:17','2023-07-30 01:24:17','2023-08-19 01:11:10'),(4,4,1,'会话-4','https://gw.alicdn.com/tfs/TB1U7FBiAT2gK0jSZPcXXcKkpXa-108-108.jpg','2023-07-30 01:33:39','2023-07-30 01:33:39','2023-08-19 01:11:10'),(5,4,1,'会话-5','https://gw.alicdn.com/tfs/TB1U7FBiAT2gK0jSZPcXXcKkpXa-108-108.jpg','2023-07-30 01:42:42','2023-07-30 01:42:42','2023-08-19 01:11:10'),(6,4,1,'会话-6','https://gw.alicdn.com/tfs/TB1U7FBiAT2gK0jSZPcXXcKkpXa-108-108.jpg','2023-07-30 01:47:23','2023-07-30 01:47:23','2023-08-19 01:11:10'),(7,4,1,'会话-7','https://gw.alicdn.com/tfs/TB1U7FBiAT2gK0jSZPcXXcKkpXa-108-108.jpg','2023-08-25 13:43:15','2023-07-30 02:35:24','2023-08-19 01:11:10'),(8,0,1,'会话-8','https://gw.alicdn.com/tfs/TB1U7FBiAT2gK0jSZPcXXcKkpXa-108-108.jpg','2023-08-28 23:24:44','2023-07-30 17:25:24','2023-08-19 01:11:10'),(9,0,1,'会话-9','https://gw.alicdn.com/tfs/TB1U7FBiAT2gK0jSZPcXXcKkpXa-108-108.jpg','2023-07-30 19:06:44','2023-07-30 19:06:44','2023-08-19 01:11:10'),(10,0,1,'会话-10','https://gw.alicdn.com/tfs/TB1U7FBiAT2gK0jSZPcXXcKkpXa-108-108.jpg','2023-08-25 21:39:02','2023-07-30 21:32:19','2023-08-19 01:11:10'),(11,0,1,'会话-11','https://igogo-test.oss-cn-shenzhen.aliyuncs.com/user/avatar/2023_3_20_150327182428737536.jpeg','2023-08-28 16:43:46','2023-08-02 22:33:05','2023-08-19 01:11:10'),(12,0,1,'会话-12','https://igogo-test.oss-cn-shenzhen.aliyuncs.com/user/avatar/2023_3_20_150327182428737536.jpeg','2023-08-25 22:35:12','2023-08-10 23:00:44','2023-08-19 01:11:10'),(13,0,1,'会话-13','https://igogo-test.oss-cn-shenzhen.aliyuncs.com/user/avatar/2023_3_20_150327182428737536.jpeg','2023-08-26 11:36:08','2023-08-14 21:20:18','2023-08-19 01:11:10'),(14,0,1,'会话-14','https://igogo-test.oss-cn-shenzhen.aliyuncs.com/user/avatar/2023_3_20_150327182428737536.jpeg','2023-08-19 10:24:36','2023-08-15 14:07:21','2023-08-19 01:11:10'),(15,0,1,'会话15','https://igogo-test.oss-cn-shenzhen.aliyuncs.com/user/avatar/2023_3_20_150327182428737536.jpeg','2023-08-28 23:11:03','2023-08-16 00:39:23','2023-08-19 01:11:10'),(16,0,1,'会话16','https://igogo-test.oss-cn-shenzhen.aliyuncs.com/user/avatar/2023_3_20_150327182428737536.jpeg','2023-08-20 22:22:17','2023-08-19 21:56:57',NULL),(17,0,1,'会话17','https://igogo-test.oss-cn-shenzhen.aliyuncs.com/user/avatar/2023_3_20_150327182428737536.jpeg','2023-08-21 20:54:52','2023-08-21 17:00:31',NULL),(18,0,1,'会话18','https://igogo-test.oss-cn-shenzhen.aliyuncs.com/user/avatar/2023_3_20_150327182428737536.jpeg','2023-08-23 13:33:52','2023-08-21 22:07:57',NULL),(19,0,1,'会话19','https://igogo-test.oss-cn-shenzhen.aliyuncs.com/user/avatar/2023_3_20_150327182428737536.jpeg','2023-08-24 22:45:31','2023-08-24 22:45:31',NULL),(20,0,1,'会话20','https://igogo-test.oss-cn-shenzhen.aliyuncs.com/user/avatar/2023_3_20_150327182428737536.jpeg','2023-08-25 14:01:57','2023-08-24 23:02:26',NULL),(21,0,1,'会话21','https://igogo-test.oss-cn-shenzhen.aliyuncs.com/user/avatar/2023_3_20_150327182428737536.jpeg','2023-08-24 23:02:35','2023-08-24 23:02:35',NULL),(23,0,1,'会话','https://igogo-test.oss-cn-shenzhen.aliyuncs.com/user/avatar/2023_3_20_150327182428737536.jpeg','2023-08-28 22:01:45','2023-08-27 22:27:19',NULL),(24,0,1,'会话','https://igogo-test.oss-cn-shenzhen.aliyuncs.com/user/avatar/2023_3_20_150327182428737536.jpeg','2023-08-29 12:41:03','2023-08-29 12:41:03',NULL);
/*!40000 ALTER TABLE `chat_session` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat_session_message`
--

DROP TABLE IF EXISTS `chat_session_message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `chat_session_message` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `from_uid` int(11) DEFAULT NULL COMMENT '消息发送用户ID',
  `session_id` int(11) DEFAULT NULL COMMENT '会话ID',
  `content` text COMMENT '消息内容',
  `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2888 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_session_message`
--

LOCK TABLES `chat_session_message` WRITE;
/*!40000 ALTER TABLE `chat_session_message` DISABLE KEYS */;
/*!40000 ALTER TABLE `chat_session_message` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat_session_message_user`
--

DROP TABLE IF EXISTS `chat_session_message_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `chat_session_message_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `message_id` int(11) DEFAULT NULL COMMENT '消息ID',
  `uid` int(11) DEFAULT NULL COMMENT '用户ID',
  `is_read` tinyint(4) DEFAULT NULL COMMENT '是否已读',
  `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `session_id` int(11) DEFAULT NULL COMMENT '所属会话冗余ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5701 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_session_message_user`
--

LOCK TABLES `chat_session_message_user` WRITE;
/*!40000 ALTER TABLE `chat_session_message_user` DISABLE KEYS */;
/*!40000 ALTER TABLE `chat_session_message_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat_session_user`
--

DROP TABLE IF EXISTS `chat_session_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `chat_session_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `session_id` int(11) DEFAULT NULL COMMENT '会话ID',
  `user_id` int(11) DEFAULT NULL COMMENT '用户ID',
  `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `avatar` varchar(255) DEFAULT NULL COMMENT '会话头像',
  `title` varchar(255) DEFAULT NULL COMMENT '会话标题',
  `session_nickname` varchar(45) DEFAULT NULL COMMENT '会话显示昵称',
  `last_look_message_time` datetime DEFAULT NULL COMMENT '用户查看的消息的最后时间',
  `is_top` tinyint(4) DEFAULT NULL COMMENT '会话置顶',
  `is_not_disturb` tinyint(4) DEFAULT NULL COMMENT '会话免打扰',
  `is_shield` varchar(45) DEFAULT NULL COMMENT '屏蔽会话',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=49 DEFAULT CHARSET=utf8 COMMENT='会话用户列表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_session_user`
--

LOCK TABLES `chat_session_user` WRITE;
/*!40000 ALTER TABLE `chat_session_user` DISABLE KEYS */;
INSERT INTO `chat_session_user` VALUES (1,1,1,'2023-08-28 17:51:37','2023-07-28 23:57:58',NULL,NULL,NULL,'2023-08-28 17:51:37',NULL,NULL,NULL),(2,1,100,'2023-07-28 23:57:58','2023-07-28 23:57:58',NULL,NULL,NULL,'2023-08-19 01:14:38',NULL,NULL,NULL),(3,2,1,'2023-08-29 12:41:25','2023-07-29 03:12:29',NULL,NULL,NULL,'2023-08-29 12:41:25',NULL,NULL,NULL),(4,2,6,'2023-07-29 03:12:29','2023-07-29 03:12:29',NULL,NULL,NULL,'2023-08-19 01:14:38',NULL,NULL,NULL),(5,3,3,'2023-07-30 01:24:17','2023-07-30 01:24:17',NULL,NULL,NULL,'2023-08-19 01:14:38',NULL,NULL,NULL),(6,3,8,'2023-07-30 01:24:17','2023-07-30 01:24:17',NULL,NULL,NULL,'2023-08-19 01:14:38',NULL,NULL,NULL),(7,4,3,'2023-07-30 01:33:39','2023-07-30 01:33:39',NULL,NULL,NULL,'2023-08-19 01:14:38',NULL,NULL,NULL),(8,4,7,'2023-07-30 01:33:39','2023-07-30 01:33:39',NULL,NULL,NULL,'2023-08-19 01:14:38',NULL,NULL,NULL),(9,5,3,'2023-07-30 01:42:42','2023-07-30 01:42:42',NULL,NULL,NULL,'2023-08-19 01:14:38',NULL,NULL,NULL),(10,5,9,'2023-07-30 01:42:42','2023-07-30 01:42:42',NULL,NULL,NULL,'2023-08-19 01:14:38',NULL,NULL,NULL),(11,6,3,'2023-07-30 01:47:23','2023-07-30 01:47:23',NULL,NULL,NULL,'2023-08-19 01:14:38',NULL,NULL,NULL),(12,6,10,'2023-07-30 01:47:23','2023-07-30 01:47:23',NULL,NULL,NULL,'2023-08-19 01:14:38',NULL,NULL,NULL),(13,7,1,'2023-08-28 19:52:02','2023-07-30 02:35:24',NULL,NULL,NULL,'2023-08-28 19:52:02',NULL,NULL,NULL),(14,7,10,'2023-07-30 02:35:24','2023-07-30 02:35:24',NULL,NULL,NULL,'2023-08-19 01:14:38',NULL,NULL,NULL),(15,8,1,'2023-08-29 12:41:35','2023-07-30 17:25:24',NULL,NULL,NULL,'2023-08-29 12:41:35',0,1,'1'),(16,8,10,'2023-07-30 17:25:24','2023-07-30 17:25:24',NULL,NULL,NULL,'2023-08-19 01:14:38',NULL,NULL,NULL),(17,9,1,'2023-08-28 17:52:33','2023-07-30 19:06:44',NULL,NULL,NULL,'2023-08-28 17:52:33',NULL,NULL,NULL),(18,9,11,'2023-07-30 19:06:44','2023-07-30 19:06:44',NULL,NULL,NULL,'2023-08-19 01:14:38',NULL,NULL,NULL),(19,10,1,'2023-08-28 23:06:03','2023-07-30 21:32:19',NULL,NULL,NULL,'2023-08-28 23:06:03',NULL,NULL,NULL),(20,10,12,'2023-07-30 21:32:19','2023-07-30 21:32:19',NULL,NULL,NULL,'2023-08-19 01:14:38',NULL,NULL,NULL),(21,11,1,'2023-08-29 12:41:25','2023-08-02 22:33:05',NULL,NULL,NULL,'2023-08-29 12:41:25',NULL,NULL,NULL),(22,11,13,'2023-08-02 22:33:05','2023-08-02 22:33:05',NULL,NULL,NULL,'2023-08-19 01:14:38',NULL,NULL,NULL),(23,12,1,'2023-08-28 23:22:57','2023-08-10 23:00:45',NULL,NULL,NULL,'2023-08-28 23:22:57',NULL,NULL,'1'),(24,12,14,'2023-08-10 23:00:45','2023-08-10 23:00:45',NULL,NULL,NULL,'2023-08-19 01:14:38',NULL,NULL,NULL),(25,13,1,'2023-08-28 23:11:42','2023-08-14 21:20:18',NULL,NULL,NULL,'2023-08-28 23:11:42',NULL,NULL,NULL),(26,13,15,'2023-08-14 21:20:18','2023-08-14 21:20:18',NULL,NULL,NULL,'2023-08-19 01:14:38',NULL,NULL,NULL),(27,14,1,'2023-08-28 08:44:17','2023-08-15 14:07:22',NULL,NULL,NULL,'2023-08-28 08:44:17',NULL,NULL,NULL),(28,14,16,'2023-08-15 14:07:22','2023-08-15 14:07:22',NULL,NULL,NULL,'2023-08-19 01:14:38',NULL,NULL,NULL),(29,15,1,'2023-08-29 12:41:35','2023-08-16 00:39:23',NULL,NULL,NULL,'2023-08-29 12:41:35',1,1,'1'),(30,15,17,'2023-08-16 00:39:23','2023-08-16 00:39:23',NULL,NULL,NULL,'2023-08-19 01:14:38',NULL,NULL,NULL),(31,16,1,'2023-08-25 03:05:39','2023-08-19 21:56:57',NULL,NULL,NULL,'2023-08-25 03:05:39',NULL,NULL,NULL),(32,16,18,'2023-08-19 21:56:57','2023-08-19 21:56:57',NULL,NULL,NULL,'2023-07-30 17:00:54',NULL,NULL,NULL),(33,17,1,'2023-08-25 15:30:35','2023-08-21 17:00:31',NULL,NULL,NULL,'2023-08-25 15:30:35',NULL,NULL,NULL),(34,17,19,'2023-08-21 17:00:31','2023-08-21 17:00:31',NULL,NULL,NULL,'2023-07-30 17:00:54',NULL,NULL,NULL),(35,18,1,'2023-08-29 12:42:02','2023-08-21 22:07:57',NULL,NULL,NULL,'2023-08-29 12:42:02',NULL,NULL,NULL),(36,18,20,'2023-08-21 22:07:57','2023-08-21 22:07:57',NULL,NULL,NULL,'2023-07-30 17:00:54',NULL,NULL,NULL),(37,19,1,'2023-08-28 21:59:34','2023-08-24 22:45:33',NULL,NULL,NULL,'2023-08-28 21:59:34',NULL,NULL,NULL),(38,19,6,'2023-08-24 22:45:33','2023-08-24 22:45:33',NULL,NULL,NULL,'2023-07-30 17:00:54',NULL,NULL,NULL),(41,20,8,'2023-08-24 23:02:26','2023-08-24 23:02:26',NULL,NULL,NULL,'2023-07-30 17:00:54',NULL,NULL,NULL),(43,21,1,'2023-08-28 17:35:02','2023-08-24 23:02:35',NULL,NULL,NULL,'2023-08-28 17:35:02',NULL,NULL,NULL),(44,21,2,'2023-08-24 23:02:35','2023-08-24 23:02:35',NULL,NULL,NULL,'2023-07-30 17:00:54',NULL,NULL,NULL),(46,22,1,'2023-07-30 17:00:54','2023-07-30 17:00:54',NULL,NULL,NULL,'2023-07-30 17:00:54',NULL,NULL,NULL),(47,23,1,'2023-08-28 22:01:49','2023-08-27 22:27:19',NULL,NULL,NULL,'2023-08-28 22:01:49',NULL,NULL,NULL),(48,23,21,'2023-08-28 14:22:36','2023-08-27 22:27:19',NULL,NULL,NULL,'2023-08-28 14:22:36',NULL,NULL,NULL);
/*!40000 ALTER TABLE `chat_session_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat_user`
--

DROP TABLE IF EXISTS `chat_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `chat_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '聊天用户ID',
  `username` varchar(45) DEFAULT NULL COMMENT '用户名',
  `nickname` varchar(45) DEFAULT NULL COMMENT '用户昵称',
  `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `origin_type` varchar(45) DEFAULT NULL COMMENT '用户原始类型；admin,user,supper等',
  `origin_id` varchar(128) DEFAULT NULL COMMENT '原始用户ID',
  `sex` tinyint(4) DEFAULT NULL COMMENT '性别',
  `avatar` varchar(500) DEFAULT NULL COMMENT '头像',
  `introduction` varchar(1000) DEFAULT NULL COMMENT '简介',
  `age` int(11) DEFAULT NULL COMMENT '年龄',
  `address` varchar(255) DEFAULT NULL COMMENT '地址',
  `register_ip` varchar(45) DEFAULT NULL COMMENT '注册IP',
  `login_ip` varchar(45) DEFAULT NULL COMMENT '登录IP',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8 COMMENT='聊天用户';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_user`
--

LOCK TABLES `chat_user` WRITE;
/*!40000 ALTER TABLE `chat_user` DISABLE KEYS */;
INSERT INTO `chat_user` VALUES (1,'admin','admin','2023-07-23 22:24:55','2023-07-23 22:24:55','admin','1',1,'https://randomuser.me/api/portraits/med/men/32.jpg','未曾清贫难成人,不经打击老天真。\n自古英雄出炼狱,从来富贵入凡尘。\n醉生梦死谁成气,拓马长枪定乾坤。\n挥军千里山河在,立名扬威传后人。',1,'湖南 长沙','192.168.0.1','127.0.0.1'),(2,'195997838323224577','195997838323224576','2023-07-26 01:08:15','2023-07-24 22:03:45','chat','1',1,'https://randomuser.me/api/portraits/med/men/32.jpg','test',2,NULL,NULL,NULL),(6,'197489090675871745','197489090675871744','2023-07-29 00:49:27','2023-07-29 00:49:27','guest','1',1,'https://randomuser.me/api/portraits/med/men/32.jpg','test',6,NULL,NULL,NULL),(8,'197837758419439617','197837758419439616','2023-07-29 23:54:56','2023-07-29 23:54:56','guest','3',1,'https://randomuser.me/api/portraits/med/men/32.jpg','test',8,NULL,NULL,NULL),(9,'197864878914932737','197864878914932736','2023-07-30 17:24:08','2023-07-30 01:42:42','guest','unique-id-730577065',1,'https://randomuser.me/api/portraits/med/men/32.jpg','test',9,NULL,NULL,NULL),(10,'197866057774403585','197866057774403584','2023-07-30 01:47:23','2023-07-30 01:47:23','guest','unique-id-837884432',1,'https://randomuser.me/api/portraits/med/men/32.jpg','test',10,NULL,NULL,NULL),(11,'198127618380271617','198127618380271616','2023-07-30 19:06:44','2023-07-30 19:06:44','guest','unique-id-585719088',NULL,'https://randomuser.me/api/portraits/med/men/32.jpg','test',11,NULL,NULL,NULL),(12,'198164257127272449','198164257127272448','2023-07-30 21:32:19','2023-07-30 21:32:19','guest','unique-id-1885568864',NULL,'https://randomuser.me/api/portraits/med/men/32.jpg','test',12,NULL,NULL,NULL),(13,'199266708471418881','199266708471418880','2023-08-02 22:33:04','2023-08-02 22:33:04','guest','unique-id-2093184425',NULL,'https://randomuser.me/api/portraits/med/men/32.jpg','test',13,NULL,NULL,NULL),(14,'202172770698661889','202172770698661888','2023-08-10 23:00:43','2023-08-10 23:00:43','guest','unique-id-1646568405',NULL,'https://randomuser.me/api/portraits/med/men/32.jpg','test',14,NULL,NULL,NULL),(15,'203597047239020545','203597047239020544','2023-08-14 21:20:17','2023-08-14 21:20:17','guest','unique-id-1374753255',NULL,'https://randomuser.me/api/portraits/med/men/32.jpg','test',15,NULL,NULL,NULL),(16,'203850480089042945','203850480089042944','2023-08-15 14:07:20','2023-08-15 14:07:20','guest','unique-id-2425484116',NULL,'https://randomuser.me/api/portraits/med/men/32.jpg','test',16,NULL,NULL,NULL),(17,'204009537433243649','204009537433243648','2023-08-16 00:39:23','2023-08-16 00:39:23','guest','unique-id-2035676134',NULL,'https://randomuser.me/api/portraits/med/men/32.jpg','test',17,NULL,NULL,NULL),(18,'205418211007533057','205418211007533056','2023-08-19 21:56:56','2023-08-19 21:56:56','guest','unique-id-349100141',NULL,'https://randomuser.me/api/portraits/med/men/32.jpg','test',18,NULL,NULL,NULL),(19,'206068389746053121','206068389746053120','2023-08-21 17:00:31','2023-08-21 17:00:31','guest','unique-id-1697365981',NULL,'https://randomuser.me/api/portraits/med/men/32.jpg','test',19,NULL,NULL,NULL),(20,'206145757055553537','206145757055553536','2023-08-21 22:07:57','2023-08-21 22:07:57','guest','unique-id-3461802873',NULL,'https://randomuser.me/api/portraits/med/men/32.jpg','test',20,NULL,NULL,NULL),(21,'admin-2','admin-2','2023-08-27 21:46:07','2023-08-27 21:46:07','admin','2',NULL,'https://randomuser.me/api/portraits/med/men/32.jpg','管理用户',21,NULL,NULL,NULL),(22,'208902194399809537','208902194399809536','2023-08-29 12:41:03','2023-08-29 12:41:03','guest','unique-id-3461005158',NULL,NULL,NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `chat_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat_user_friend`
--

DROP TABLE IF EXISTS `chat_user_friend`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `chat_user_friend` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID;;',
  `chat_uid` int(11) DEFAULT NULL COMMENT '聊天用户ID;;',
  `friend_uid` int(11) DEFAULT NULL COMMENT '好友用户ID;;',
  `notes_name` varchar(1000) DEFAULT NULL COMMENT '好友备注名;;',
  PRIMARY KEY (`id`),
  UNIQUE KEY `index2` (`chat_uid`,`friend_uid`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8 COMMENT='好友;;';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_user_friend`
--

LOCK TABLES `chat_user_friend` WRITE;
/*!40000 ALTER TABLE `chat_user_friend` DISABLE KEYS */;
INSERT INTO `chat_user_friend` VALUES (1,1,2,'2备ddbb'),(2,2,1,'1备注名'),(3,1,6,'6备ff订单'),(4,6,1,'1备注'),(5,1,8,'8备注订test'),(6,8,1,'1备注'),(7,1,13,'test1'),(8,13,1,'admin'),(9,21,1,'美好一天'),(10,1,21,'美好的一天');
/*!40000 ALTER TABLE `chat_user_friend` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat_user_friend_apply`
--

DROP TABLE IF EXISTS `chat_user_friend_apply`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `chat_user_friend_apply` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `chat_uid` int(11) DEFAULT NULL,
  `friend_uid` int(11) DEFAULT NULL,
  `status` tinyint(4) DEFAULT '0' COMMENT '申请状态;0:待处理,1:已同意,2:已拒绝',
  `ask` varchar(200) DEFAULT NULL COMMENT '提问的问题',
  `answer` varchar(200) DEFAULT NULL COMMENT '回答答案',
  `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='好友申请';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_user_friend_apply`
--

LOCK TABLES `chat_user_friend_apply` WRITE;
/*!40000 ALTER TABLE `chat_user_friend_apply` DISABLE KEYS */;
INSERT INTO `chat_user_friend_apply` VALUES (1,1,20,0,'我的生日是？','2022-02-02','2023-08-26 17:44:36','2023-08-26 17:44:36'),(2,21,1,1,NULL,NULL,'2023-08-27 22:11:00','2023-08-27 21:50:51'),(3,21,1,1,NULL,NULL,'2023-08-27 22:20:16','2023-08-27 22:19:53');
/*!40000 ALTER TABLE `chat_user_friend_apply` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat_user_notice`
--

DROP TABLE IF EXISTS `chat_user_notice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `chat_user_notice` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `chat_uid` int(11) DEFAULT NULL COMMENT '聊天用户ID',
  `status` tinyint(4) DEFAULT NULL COMMENT '通知处理状态;0:待处理,1:同意,2:拒绝,3:已处理',
  `type` tinyint(4) DEFAULT NULL COMMENT '业务类型;1:好友申请',
  `tracking_id` int(11) DEFAULT NULL COMMENT '跟踪ID',
  `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8 COMMENT='聊天通知';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_user_notice`
--

LOCK TABLES `chat_user_notice` WRITE;
/*!40000 ALTER TABLE `chat_user_notice` DISABLE KEYS */;
INSERT INTO `chat_user_notice` VALUES (1,1,3,1,1,'2023-08-26 17:44:37','2023-08-26 17:44:37'),(2,20,0,1,1,'2023-08-26 17:44:37','2023-08-26 17:44:37'),(3,21,3,1,2,'2023-08-27 21:50:51','2023-08-27 21:50:51'),(4,1,0,1,2,'2023-08-27 21:50:51','2023-08-27 21:50:51'),(5,21,NULL,1,2,NULL,'2023-08-27 22:11:00'),(6,21,3,1,3,'2023-08-27 22:19:53','2023-08-27 22:19:53'),(7,1,0,1,3,'2023-08-27 22:19:53','2023-08-27 22:19:53'),(8,21,NULL,1,3,NULL,'2023-08-27 22:20:16');
/*!40000 ALTER TABLE `chat_user_notice` ENABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=155439155 DEFAULT CHARSET=utf8 COMMENT='代码表;;option:id~name';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `code_table`
--

LOCK TABLES `code_table` WRITE;
/*!40000 ALTER TABLE `code_table` DISABLE KEYS */;
INSERT INTO `code_table` VALUES (155439106,'pay_info','支付表','支付表'),(155439115,'user_info','用户信息','用户信息;;option:id~username'),(155439117,'code_table_template_group','模本分组','模本分组;;option:id~name'),(155439124,'example_product_model','产品模型','产品模型;;foreign:id~example_product_model_attr.model_id~title'),(155439126,'admin_role_resource','角色资源关联表','角色资源关联表'),(155439127,'code_table_template','代码表模板','代码表模板;;option:id~title'),(155439128,'admin_content','系统内容表','系统内容表'),(155439130,'pay_order','支付单','支付单;option:id~title'),(155439134,'admin_notification','管理员通知','管理员通知'),(155439138,'admin_role_user','角色用户关联表','角色用户关联表'),(155439139,'admin_user','系统用户表','系统用户表'),(155439140,'example','自动化样例表','自动化样例表;;approval:4~id'),(155439141,'example_category','自动化样例分类表','自动化样例分类表;;option:id~title,tree:id~title~parent_id,frontHome:tree'),(155439142,'example_option','选项','选项;;option:id~value'),(155439143,'admin_user_login_log','用户登录日志','用户登录日志'),(155439144,'chat_customer_service','客服','客服'),(155439145,'chat_session','聊天会话','聊天会话'),(155439146,'chat_session_user','会话用户列表','会话用户列表'),(155439147,'chat_user','聊天用户','聊天用户'),(155439148,'groovy_group','groovy分组','groovy分组;;option:id~title'),(155439149,'groovy_info','groovy代码','groovy代码;;'),(155439150,'chat_session_message_user','',''),(155439151,'chat_session_message','',''),(155439152,'chat_user_friend','好友','好友;;'),(155439153,'chat_user_friend_apply','好友申请','好友申请'),(155439154,'chat_user_notice','聊天通知','聊天通知');
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
) ENGINE=InnoDB AUTO_INCREMENT=1094 DEFAULT CHARSET=utf8 COMMENT='代码表字段';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `code_table_column`
--

LOCK TABLES `code_table_column` WRITE;
/*!40000 ALTER TABLE `code_table_column` DISABLE KEYS */;
INSERT INTO `code_table_column` VALUES (602,155439115,'id',NULL,'int',0,1,0,1,0,NULL,11,0,'ID'),(603,155439115,'username',NULL,'varchar',0,0,0,0,0,NULL,45,0,'用户名'),(604,155439115,'email',NULL,'varchar',0,0,0,0,0,NULL,255,0,'邮箱'),(605,155439115,'phone',NULL,'varchar',0,0,0,0,0,NULL,15,0,'手机号'),(606,155439115,'password',NULL,'varchar',0,0,0,0,0,NULL,255,0,'密码;;frontType:password,ignoreInList:true'),(607,155439115,'avatar',NULL,'varchar',0,0,0,0,0,NULL,255,0,'头像;;frontType:upload,uploadCount:1,ignoreInList:true'),(608,155439115,'status',NULL,'tinyint',0,0,0,0,0,'1',4,0,'状态;0:禁用,1:活跃;frontType:select'),(609,155439115,'age',NULL,'tinyint',0,0,1,0,0,NULL,4,0,'年龄;;frontName:年龄'),(610,155439115,'sex',NULL,'tinyint',0,0,0,0,0,NULL,4,0,'性别;0:女,1:男;frontType:select,frontName:性别'),(611,155439115,'updated_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'更新时间'),(612,155439115,'created_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'头像;;frontType:upload,uploadCount:1'),(621,155439117,'id',NULL,'int',0,1,0,1,0,NULL,11,0,'ID'),(622,155439117,'title',NULL,'varchar',0,0,0,0,0,NULL,255,0,'标题'),(623,155439117,'name',NULL,'varchar',0,0,0,0,0,NULL,45,0,'名称'),(624,155439117,'base_path',NULL,'varchar',0,0,0,0,0,NULL,1000,0,'基本写入路径'),(625,155439117,'main_function',NULL,'varchar',0,0,0,0,0,NULL,45,0,'该组入口函数'),(626,155439117,'explain',NULL,'varchar',0,0,0,0,0,NULL,1000,0,'说明'),(627,155439117,'updated_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'更新时间'),(628,155439117,'created_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'创建时间'),(666,155439124,'id',NULL,'int',0,1,0,0,0,NULL,11,0,'ID'),(667,155439124,'title',NULL,'varchar',0,0,0,0,0,NULL,45,0,'产品标题'),(668,155439124,'created_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'创建时间'),(669,155439124,'updated_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'更新时间'),(680,155439126,'id',NULL,'int',0,1,1,1,0,NULL,10,0,NULL),(681,155439126,'role_id',NULL,'int',0,0,1,0,0,NULL,10,0,'角色ID'),(682,155439126,'resource_id',NULL,'int',0,0,1,0,0,NULL,10,0,'资源ID'),(683,155439126,'created_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'创建时间'),(684,155439127,'id',NULL,'int',0,1,0,1,0,NULL,11,0,NULL),(685,155439127,'group_id',NULL,'int',0,0,0,0,0,NULL,11,0,'分组;;frontType:select,foreign:code_table_template_group.id~name'),(686,155439127,'name',NULL,'varchar',0,0,0,0,0,NULL,255,0,'模板名'),(687,155439127,'title',NULL,'varchar',0,0,0,0,0,NULL,255,0,'标题'),(688,155439127,'status',NULL,'tinyint',0,0,0,0,0,NULL,4,0,'状态;0:禁用,1:活跃;frontType:select'),(689,155439127,'code',NULL,'text',0,0,0,0,0,NULL,NULL,0,'代码;;frontType:textArea'),(690,155439127,'created_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,NULL),(691,155439128,'id',NULL,'int',0,1,0,1,0,NULL,11,0,NULL),(692,155439128,'title',NULL,'varchar',0,0,0,0,0,NULL,1000,0,'文章标题'),(693,155439128,'description',NULL,'varchar',0,0,0,0,0,NULL,3000,0,'文章描述'),(694,155439128,'keywords',NULL,'varchar',0,0,0,0,0,NULL,1000,0,'关键字'),(695,155439128,'thumbnail',NULL,'varchar',0,0,0,0,0,NULL,500,0,'缩略图'),(696,155439128,'body',NULL,'text',0,0,0,0,0,NULL,NULL,0,'文章内容'),(697,155439128,'updated_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'创建时间'),(698,155439128,'created_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'创建时间'),(699,155439128,'category_id',NULL,'int',0,0,0,0,0,NULL,11,0,'文章分类ID'),(700,155439128,'user_id',NULL,'int',0,0,0,0,0,NULL,11,0,'添加的管理员ID'),(701,155439128,'status',NULL,'tinyint',0,0,0,0,0,NULL,4,0,'文章状'),(702,155439128,'is_top',NULL,'tinyint',0,0,0,0,0,'0',4,0,'是否置顶'),(703,155439128,'star',NULL,'int',0,1,0,0,0,'0',11,0,'star数量'),(704,155439128,'likes',NULL,'int',0,1,0,0,0,'0',11,0,'点赞数量'),(705,155439128,'dis_likes',NULL,'int',0,1,0,0,0,'0\'',11,0,NULL),(706,155439128,'comments_count',NULL,'int',0,1,0,0,0,'0',11,0,'评论数量'),(714,155439130,'id',NULL,'int',0,1,0,1,0,NULL,11,0,NULL),(715,155439130,'pay_id',NULL,'int',0,1,0,0,0,NULL,11,0,'支付方式ID;;frontType:select,foreign:pay_info.id~title'),(716,155439130,'order_no',NULL,'varchar',0,1,0,0,0,NULL,128,0,'支付单号'),(717,155439130,'tracking_no',NULL,'varchar',0,1,0,0,0,NULL,128,0,'外部跟踪单号'),(718,155439130,'transaction_id',NULL,'varchar',0,0,0,0,0,NULL,128,0,'支付供应商跟踪ID；例如微信，支付宝支付单号'),(719,155439130,'amount',NULL,'decimal',0,1,0,0,0,NULL,9,2,'支付金额'),(720,155439130,'status',NULL,'tinyint',0,1,0,0,0,'1',4,0,'支付单状态;1:待支付,10:已扫码,20:支付成功,30:支付失败;frontType:select'),(721,155439130,'payed_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'支付时间;;frontType:datetime'),(722,155439130,'notify_url',NULL,'varchar',0,0,0,0,0,NULL,500,0,'通知地址'),(723,155439130,'created_time',NULL,'datetime',0,1,0,0,0,NULL,NULL,0,'创建时间'),(724,155439130,'updated_time',NULL,'datetime',0,1,0,0,0,NULL,NULL,0,'更新时间'),(748,155439134,'id',NULL,'int',0,1,0,1,0,NULL,11,0,NULL),(749,155439134,'admin_user_id',NULL,'int',0,0,0,0,0,NULL,11,0,'接收人'),(750,155439134,'title',NULL,'varchar',0,0,0,0,0,NULL,500,0,'标题'),(751,155439134,'create_admin_user_id',NULL,'int',0,1,0,0,0,NULL,11,0,'创'),(752,155439134,'content',NULL,'text',0,0,0,0,0,NULL,NULL,0,'通知内容'),(753,155439134,'created_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'创建时间'),(754,155439134,'is_read',NULL,'tinyint',0,0,0,0,0,'0',4,0,'是否'),(786,155439138,'id',NULL,'int',0,1,1,1,0,NULL,10,0,NULL),(787,155439138,'role_id',NULL,'int',0,0,1,0,0,NULL,10,0,'角色ID'),(788,155439138,'user_id',NULL,'int',0,0,1,0,0,NULL,10,0,'用户ID'),(789,155439138,'status',NULL,'tinyint',0,0,0,0,0,NULL,4,0,'状态'),(790,155439138,'created_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'创建时间'),(791,155439139,'id',NULL,'bigint',0,1,0,1,0,NULL,20,0,NULL),(792,155439139,'username',NULL,'varchar',0,1,0,0,0,NULL,128,0,'用户名'),(793,155439139,'phone',NULL,'varchar',0,1,0,0,0,NULL,11,0,'手机号'),(794,155439139,'nick_name',NULL,'varchar',0,0,0,0,0,NULL,45,0,'昵称'),(795,155439139,'real_name',NULL,'varchar',0,0,0,0,0,NULL,45,0,'真实名称'),(796,155439139,'avatar',NULL,'varchar',0,0,0,0,0,'//image.zuiidea.com/photo-1519336555923-59661f41bb45.jpeg?imageView2/1/w/200/h/200/format/webp/q/75|imageslim',500,0,'头像'),(797,155439139,'email',NULL,'varchar',0,0,0,0,0,NULL,255,0,'邮箱地址'),(798,155439139,'password',NULL,'varchar',0,0,0,0,0,NULL,300,0,'用户密码'),(799,155439139,'updated_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'更新时间'),(800,155439139,'created_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'创建时间'),(801,155439139,'sex',NULL,'tinyint',0,0,0,0,0,NULL,4,0,'性别'),(802,155439139,'age',NULL,'int',0,0,0,0,0,NULL,11,0,'年龄'),(803,155439139,'is_deleted',NULL,'tinyint',0,0,0,0,0,'0',4,0,'软删除标记'),(804,155439140,'id',NULL,'int',1,1,0,1,0,NULL,11,0,';;'),(805,155439140,'title','标题','varchar',0,0,0,0,0,NULL,45,0,'标题;;isFilter:true,isApproval:true'),(806,155439140,'category_id','分类ID','int',0,0,0,0,0,NULL,11,0,'分类ID;;frontType:treeSelect,foreign:example_category.id~title,frontName:category,isFilter:true'),(807,155439140,'option_id','选项','varchar',0,0,0,0,0,NULL,45,0,'选项;;frontType:select,foreign:example_option.id~value,frontName:样例选项'),(808,155439140,'content','富媒体','text',0,0,0,0,0,NULL,NULL,0,'富媒体;;frontType:editor,ignoreInList:true,isApproval:true'),(809,155439140,'updated_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'更新时间'),(810,155439140,'created_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'创建时间'),(811,155439141,'id',NULL,'int',0,1,0,1,0,NULL,11,0,'ID;;optionKey'),(812,155439141,'title',NULL,'varchar',0,0,0,0,0,NULL,145,0,'标题;;optionValue'),(813,155439141,'parent_id',NULL,'int',0,0,0,0,0,NULL,11,0,'父级ID;;frontType:treeSelect,parent:id,foreign:example_category,frontName:Parent_Category'),(814,155439141,'updated_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'更新时间'),(815,155439141,'created_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'创建时间'),(816,155439141,'only_date',NULL,'date',0,0,0,0,0,NULL,NULL,0,'只是日期;;frontType:date'),(817,155439141,'pay_datetime',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'日期时间;;frontType:datetime'),(818,155439141,'img',NULL,'varchar',0,0,0,0,0,NULL,500,0,'图片;;frontType:upload,uploadCount:3'),(819,155439142,'id','ID','int',1,1,0,0,0,NULL,11,0,'ID;;'),(820,155439142,'key','key','varchar',0,0,0,0,0,NULL,45,0,'key;;isFilter:true'),(821,155439142,'value','选项值','varchar',0,0,0,0,0,NULL,45,0,'选项值;;isFilter:true'),(822,155439142,'updated_time','更新时间','datetime',0,0,0,0,0,NULL,NULL,0,'更新时间;;frontType:datetime'),(823,155439142,'created_time','创建时间','datetime',0,0,0,0,0,NULL,NULL,0,'创建时间;;frontType:datetime'),(824,155439140,'apply_status','审批状态','tinyint',0,1,0,0,NULL,'0',4,0,'审批状态;0:未审批,1:已审批;frontType:select,editReadOnly:true,'),(825,155439140,'status','状态','tinyint',0,0,0,0,NULL,'1',4,0,'状态;0:禁用,1:活跃;frontType:select,'),(826,155439143,'id','ID','int',1,1,1,1,0,NULL,11,0,'ID;;'),(827,155439143,'admin_user_id','后台用户ID','int',0,0,0,0,0,NULL,11,0,'后台用户ID;;frontType:select,foreign:admin_user.id~username'),(828,155439143,'client_ip',NULL,'varchar',0,0,0,0,0,NULL,50,0,'客户端IP地址考虑IPv6字段适当放宽'),(829,155439143,'client_user_agent',NULL,'varchar',0,0,0,0,0,NULL,1000,0,'客户端软件信息'),(830,155439143,'token',NULL,'varchar',0,0,0,0,0,NULL,3000,0,'给用户发放的token'),(831,155439143,'created_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'创建时间'),(1012,155439106,'id',NULL,'int',1,1,0,1,0,NULL,11,0,'ID'),(1013,155439106,'title',NULL,'varchar',0,1,0,0,0,NULL,45,0,'标题;;,min:1,frontType:text,min:3,max:46'),(1014,155439106,'name',NULL,'varchar',0,1,0,0,0,NULL,45,0,NULL),(1015,155439106,'account_app_id',NULL,'varchar',0,0,0,0,0,NULL,45,0,'账号AppId'),(1016,155439106,'account_id',NULL,'varchar',0,1,0,0,0,NULL,45,0,'支付ID'),(1017,155439106,'account_private_key',NULL,'varchar',0,1,0,0,0,NULL,6000,0,'支付私钥'),(1018,155439106,'account_serial_number',NULL,'varchar',0,0,0,0,0,NULL,128,0,'商户证书编号'),(1019,155439106,'account_public_key',NULL,'varchar',0,1,0,0,0,NULL,6000,0,'支付公钥'),(1020,155439106,'account_img',NULL,'varchar',0,1,0,0,0,NULL,2048,0,'支付封面图片;;frontType:upload,uploadCount:1'),(1021,155439106,'client_type',NULL,'varchar',0,1,0,0,0,NULL,1024,0,'支持客户端类型;'),(1022,155439106,'created_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'创建时间'),(1023,155439106,'updated_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'更新时间'),(1024,155439106,'platform',NULL,'varchar',0,1,0,0,0,'soho',45,0,'平台识别名'),(1025,155439106,'status',NULL,'tinyint',0,1,0,0,0,NULL,1,0,'状'),(1026,155439106,'adapter_name',NULL,'varchar',0,1,0,0,0,NULL,100,0,'支付驱动;wechat_jsapi,wechat_h5,wechat_app,wechat_native,alipay_wap,alipay_web;frontType:select'),(1027,155439144,'id',NULL,'int',1,1,0,0,0,NULL,11,0,NULL),(1028,155439144,'user_id',NULL,'int',0,0,0,0,0,NULL,11,0,NULL),(1029,155439144,'status',NULL,'tinyint',0,0,0,0,0,NULL,4,0,'客服状态;1:下线,2:活跃,3:禁用;frontType:select'),(1030,155439144,'updated_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'更新时间'),(1031,155439144,'created_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'创建时间'),(1032,155439145,'id',NULL,'bigint',1,1,0,0,0,NULL,20,0,NULL),(1033,155439145,'type',NULL,'tinyint',0,0,0,0,0,NULL,4,0,'会话类型;1:私聊,2:群聊,3:群组;frontType:select'),(1034,155439145,'status',NULL,'tinyint',0,0,0,0,0,NULL,4,0,'状态;1:活跃,2:禁用,3:删除;frontType:select'),(1035,155439145,'title',NULL,'varchar',0,0,0,0,0,NULL,45,0,'会话标题'),(1036,155439145,'avatar',NULL,'varchar',0,0,0,0,0,NULL,255,0,'会话头像'),(1037,155439145,'updated_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'更新时间'),(1038,155439145,'created_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'创建时间'),(1039,155439146,'id',NULL,'int',1,1,0,1,0,NULL,11,0,NULL),(1040,155439146,'session_id',NULL,'int',0,0,0,0,0,NULL,11,0,'会话ID'),(1041,155439146,'user_id',NULL,'int',0,0,0,0,0,NULL,11,0,'用户ID'),(1042,155439146,'updated_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'更新时间'),(1043,155439146,'created_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'创建时间'),(1044,155439147,'id',NULL,'int',1,1,0,1,0,NULL,11,0,'聊天用户ID'),(1045,155439147,'username',NULL,'varchar',0,0,0,0,0,NULL,45,0,'用户名'),(1046,155439147,'nickname',NULL,'varchar',0,0,0,0,0,NULL,45,0,'用户昵称'),(1047,155439147,'updated_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'更新时间'),(1048,155439147,'created_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'创建时间'),(1049,155439147,'origin_type',NULL,'varchar',0,0,0,0,0,NULL,45,0,'用户原始类型；admin,user,supper等'),(1050,155439147,'origin_id','原始用户ID','int',0,1,0,0,NULL,NULL,NULL,0,'原始用户ID;;'),(1051,155439148,'id',NULL,'int',1,1,0,0,0,NULL,11,0,NULL),(1052,155439148,'name',NULL,'varchar',0,0,0,0,0,NULL,45,0,'组名'),(1053,155439148,'title',NULL,'varchar',0,0,0,0,0,NULL,45,0,'组标题'),(1054,155439148,'status',NULL,'tinyint',0,0,0,0,0,NULL,4,0,'组状态;1:正常,2:禁用'),(1055,155439148,'updated_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'更新时间'),(1056,155439148,'created_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'创建时间'),(1057,155439149,'id',NULL,'int',1,1,0,1,0,NULL,11,0,NULL),(1058,155439149,'group_id',NULL,'int',0,0,0,0,0,NULL,11,0,'组ID;;frontType:select,foreign:groovy_group.id~title'),(1059,155439149,'name',NULL,'varchar',0,0,0,0,0,NULL,45,0,NULL),(1060,155439149,'code',NULL,'text,',0,0,0,0,0,NULL,NULL,0,NULL),(1061,155439149,'created_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,NULL),(1062,155439149,'updated_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,NULL),(1063,155439150,'id',NULL,'int',1,1,0,1,0,NULL,11,0,NULL),(1064,155439150,'message_id',NULL,'int',0,0,0,0,0,NULL,11,0,'消息ID'),(1065,155439150,'uid',NULL,'int',0,0,0,0,0,NULL,11,0,'用户ID'),(1066,155439150,'is_read',NULL,'tinyint',0,0,0,0,0,NULL,4,0,'是否已读'),(1067,155439150,'updated_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'更新时间'),(1068,155439150,'created_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'创建时间'),(1069,155439151,'id',NULL,'int',1,1,0,0,0,NULL,11,0,NULL),(1070,155439151,'from_uid',NULL,'int',0,0,0,0,0,NULL,11,0,'消息发送用户ID'),(1071,155439151,'session_id',NULL,'int',0,0,0,0,0,NULL,11,0,'会话ID'),(1072,155439151,'content',NULL,'text',0,0,0,0,0,NULL,NULL,0,'消息内容'),(1073,155439151,'updated_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'更新时间'),(1074,155439151,'created_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'创建时间'),(1075,155439152,'id','ID','int',1,1,1,1,NULL,NULL,NULL,0,'ID;;'),(1076,155439152,'chat_uid','聊天用户ID','int',0,0,0,0,0,NULL,NULL,0,'聊天用户ID;;'),(1077,155439152,'friend_uid','好友用户ID','int',0,0,0,0,0,NULL,NULL,0,'好友用户ID;;'),(1078,155439152,'notes_name','好友备注名','int',0,0,0,0,0,NULL,NULL,0,'好友备注名;;'),(1079,155439153,'id',NULL,'int',1,1,0,1,0,NULL,11,0,NULL),(1080,155439153,'chat_uid',NULL,'int',0,0,0,0,0,NULL,11,0,NULL),(1081,155439153,'friend_uid',NULL,'int',0,0,0,0,0,NULL,11,0,NULL),(1082,155439153,'status',NULL,'tinyint',0,0,0,0,0,NULL,4,0,'申请状态;0:待处理,1:已同意,2:已拒绝'),(1083,155439153,'ask',NULL,'varchar',0,0,0,0,0,NULL,200,0,'提问的问题'),(1084,155439153,'answer',NULL,'varchar',0,0,0,0,0,NULL,200,0,'回答答案'),(1085,155439153,'updated_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'更新时间'),(1086,155439153,'created_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'创建时间'),(1087,155439154,'id',NULL,'int',1,1,0,1,0,NULL,11,0,'ID'),(1088,155439154,'chat_uid',NULL,'int',0,0,0,0,0,NULL,11,0,'聊天用户ID'),(1089,155439154,'status',NULL,'tinyint',0,0,0,0,0,NULL,4,0,'通知处理状态;0:待处理,1:同意,2:拒绝,3:已处理'),(1090,155439154,'type',NULL,'tinyint',0,0,0,0,0,NULL,4,0,'业务类型;1:好友申请'),(1091,155439154,'tracking_id',NULL,'int',0,0,0,0,0,NULL,11,0,'跟踪ID'),(1092,155439154,'updated_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'更新时间'),(1093,155439154,'created_time',NULL,'datetime',0,0,0,0,0,NULL,NULL,0,'创建时间');
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
) ENGINE=InnoDB AUTO_INCREMENT=1170362389 DEFAULT CHARSET=utf8 COMMENT='代码表模板;;option:id~title';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `code_table_template`
--

LOCK TABLES `code_table_template` WRITE;
/*!40000 ALTER TABLE `code_table_template` DISABLE KEYS */;
INSERT INTO `code_table_template` VALUES (1,1715638274,'utils','工具类库',1,'import java.text.SimpleDateFormat\nimport java.util.regex.Matcher;\nimport java.util.regex.Pattern;\n\ndef main(name) {\n  // println calcFields(name)\n  \"fields: \" + calcTable(name)\n}\n\ndef tint(i) {\n  \"int: \" + i\n}\n\n//转换表\ndef calcTable(table) {\n  def fields = calcFields(table)\n  def t = [\n    name: table.name,\n    title: table.title,\n    comment: table.comment,\n    upperCamelCase: upperCamelCase(table.name),\n    camelCase: camelCase(table.name),\n    approval: getExtendData(table.comment, \'approval\', null), //审批操作\n    frontHome: getExtendData(table.comment, \'frontHome\', null), //首页类型\n    option: getExtendData(table.comment, \'option\', null), //选项接口\n    tree: getExtendData(table.comment, \'tree\', null), //tree接口 id~title~parent_id\n    fields: fields,\n  ]\n}\n\n//字段转换\ndef calcFields(vo) {\n  def columns = vo.columnList\n  def fields = []\n\n  columns.each{col->\n    println col.name\n    //字段数据处理\n    // def frontName = \n    \n    fields += [[\n      name: col.name,\n      dataType: col.dataType,\n      isPk: col.isPk,\n      title: col.title,\n      isNotNull: col.isNotNull,\n      isUnique: col.isUnique,\n      isAutoIncrement: col.isAutoIncrement,\n      isZeroFill: col.isZeroFill,\n      defaultValue: col.defaultValue,\n      length: col.length,\n      scale: col.scale,\n      comment: col.comment,\n\n      //获取解析填充数据\n      upperCamelCase: upperCamelCase(col.name),\n      camelCase: camelCase(col.name),\n      frontName: upperWords(getExtendData(col.comment, \'frontName\', col.name)), //前段展示名\n      frontType: getFrontType(col.dataType, col.getComment()), //前端展示类型\n      frontMax: col.length == null ? null : col.length + col.scale, //前段编辑框最大长度\n      frontStep: 1, //如果是number类型 步进长度\n      isFilter: getExtendData(col.getComment(), \'isFilter\', \'false\'), //字段是否再列表页过滤器中\n      isApproval: getExtendData(col.getComment(), \'isApproval\', \'false\'), //审核字段\n      editReadOnly: getExtendData(col.getComment(), \'editReadOnly\', \'false\'), //编辑只读\n      options: getOptions(col.comment), //如果字段为选项字段， 则为选项\n      foreignTableName: getForeignTable(col.comment), //外链表名\n      foreignTableUpperCamelCaseName: upperCamelCase(getForeignTable(col.comment)), //外链表大驼峰名\n\n      //java相关选项\n      javaType: getJavaType(col.dataType, col.getComment()), //对应的java类型\n    ]]\n  }\n  fields\n}\n\n// 大驼峰命名\ndef upperCamelCase(str) {\n  if(str == \"\") {\n    return str\n  }\n    if(str  == null) {\n        return null;\n    }\n  def name = camelCase(str)\n  name.substring(0, 1).toUpperCase() + name.substring(1);\n}\n\n// 小驼峰命名\ndef camelCase(str) {\n    if(str  == null) {\n        return null;\n    }\n    final StringBuffer sb = new StringBuffer();\n    Pattern p = Pattern.compile(\"_(\\\\w)\");\n    Matcher m = p.matcher(str);\n    while (m.find()){\n        m.appendReplacement(sb,m.group(1).toUpperCase());\n    }\n    m.appendTail(sb);\n    sb.toString()\n}\n\n//下划线名字转大写首字母单词串\ndef upperWords(str) {\n      if(str  == null) {\n        return null;\n    }\n    final StringBuffer sb = new StringBuffer();\n    Pattern p = Pattern.compile(\"_(\\\\w)\");\n    Matcher m = p.matcher(str);\n    while (m.find()){\n        m.appendReplacement(sb,\" \" + m.group(1).toUpperCase());\n    }\n    m.appendTail(sb);\n    def name = sb.toString()\n    name.substring(0, 1).toUpperCase() + name.substring(1);\n}\n\n/**\n * 获取前端类型\n *\n * @param dbType\n * @param comment\n * @return\n */\ndef getFrontType(dbType, comment) {\n    def frontType = getExtendData(comment, \'frontType\', null);\n    if(frontType) {\n        return frontType;\n    }\n    switch (dbType) {\n        case \"int\":\n        case \"tinyint\":\n        case \"smallint\":\n        case \"mediumint\":\n        case \"bigint\":\n        case \"float\":\n        case \"double\":\n        case \"decimal\":\n            frontType = \"number\"\n            break;\n        default:\n            frontType = \"text\";\n            break;\n    }\n    return frontType;\n}\n\n/**\n * 获取外链表\n *\n * @param comment\n * @return\n */\ndef getForeignTable(comment) {\n    def foreign = getExtendData(comment, \'foreign\', null)\n    if(foreign) {\n        String[] parts = foreign.split(\"~\")\n        if(parts && parts.length>=1) {\n            def fid = parts[0]\n            String[] tableParts = fid.split(\"\\\\.\")\n            if(tableParts.length>0) {\n                return tableParts[0]\n            }\n        }\n    }\n    return null;\n}\n\n/**\n * 获取关联表大驼峰名\n */\ndef getForeignTableUpperCamelCase(comment) {\n    def capita = getForeignTable(comment)\n    if(capita) {\n        return upperCamelCase(capita)\n    }\n    return null;\n}\n\n/**\n * 获取前端校验最小值\n */\ndef getFrontMin(it) {\n    def min = 0\n    def minStr = getExtendData(it.comment,\"min\", null)\n    if(minStr != null) {\n        return Integer.valueOf(minStr)\n    }\n\n    //检查表单类型\n    def frontType = getExtendData(it.comment, \'frontType\', \'text\')\n    if(frontType != \'text\') {\n        return null;\n    }\n\n    if(it.dbUnsigned) {\n        //无符号\n        switch (it.dbType) {\n            case \"tinyint\":\n            case \"smallint\":\n            case \"mediumint\":\n            case \"int\":\n            case \"bigint\":\n                min = 0;\n                break;\n            default:\n//                min = it.length;\n                break;\n        }\n    } else {\n        switch (it.dbType) {\n            case \"tinyint\":\n                min = -128\n                break;\n            case \"smallint\":\n                min = -32768;\n                break;\n            case \"mediumint\":\n                min = 	-8388608;\n                break;\n            case \"int\":\n                min = -2147483648;\n                break;\n            case \"bigint\":\n                min = -9007199254740991;  //该值为Js最大值\n                break;\n            default:\n//                min = it.length;\n                break;\n        }\n    }\n    return min;\n}\n\n/**\n * 获取前端校验最小值\n */\ndef getFrontMax(it) {\n    def max = it.length\n    //dbUnsigned\n    def maxStr = getExtendData(it.comment,\"max\", null)\n    if(maxStr != null) {\n        //如果指定直接使用指定值\n        return Integer.valueOf(maxStr)\n    }\n\n    //检查表单类型\n    def frontType = getExtendData(it.comment, \'frontType\', \'text\')\n    if(frontType != \'text\') {\n        return null;\n    }\n\n    if(it.dbUnsigned) {\n        //无符号\n        switch (it.dbType) {\n            case \"tinyint\":\n                max = 255\n                break;\n            case \"smallint\":\n                max = 	65535;\n                break;\n            case \"mediumint\":\n                max = 16777215;\n                break;\n            case \"int\":\n                max = 4294967295;\n                break;\n            case \"bigint\":\n                max = 9007199254740992;  //该值为Js最大值\n                break;\n            default:\n                max = it.length;\n                break;\n        }\n    } else {\n        switch (it.dbType) {\n            case \"tinyint\":\n                max = 127\n                break;\n            case \"smallint\":\n                max = 32767;\n                break;\n            case \"mediumint\":\n                max = 8388607;\n                break;\n            case \"int\":\n                max = 2147483647;\n                break;\n            case \"bigint\":\n                max = 9007199254740992;  //该值为Js最大值\n                break;\n            default:\n                max = it.length;\n                break;\n        }\n    }\n\n    return max;\n}\n\n/**\n * 查找domain命名空间\n *\n * @param dir\n */\ndef findDomainPackageName(dir) {\n    return findPackageName(new File(dir.getParent() + \"/domain\"))\n}\n\n/**\n * 查找service命名空间\n *\n * @param dir\n */\ndef findServicePackageName(dir) {\n    return findPackageName(new File(dir.getParent() + \"/service\"))\n}\n\n/**\n * 查找指定目录现有的命名空间\n *\n * @param dir\n * @return\n */\ndef findPackageName(dir) {\n    if(dir != null && dir.isDirectory()) {\n        File[] files = dir.listFiles();\n        if(files != null && files.length>0) {\n            for (File item:files) {\n                if(item.isFile()) {\n                    List<String> lines = Files.readLines(item, StandardCharsets.UTF_8);\n                    //查找命名空间\n                    for (String line: lines) {\n                        line = line.trim();\n                        if(line.startsWith(\"package\")) {\n                            line = line.split(\" \")[1];\n                            line = line.replace(\";\", \"\")\n                            line = line.trim();\n                            return line;\n                        }\n                    }\n                }\n            }\n        }\n    }\n    return \"\"\n}\n\n/**\n * 首字母小写\n * @param s\n * @return\n */\ndef toLowerCaseFirstOne(String s){\n    if(Character.isLowerCase(s.charAt(0)))\n        return s;\n    else\n        return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();\n}\n\n/**\n * 转换成中划线\n *\n * @param s\n * @return\n */\ndef toStrikethrough(String str) {\n    def s = com.intellij.psi.codeStyle.NameUtil.splitNameIntoWords(str)\n            .collect { Case.LOWER.apply(it) }\n            .join(\"-\")\n            .replaceAll(/[^\\p{javaJavaIdentifierPart}[_]]/, \"_\")\n    return s\n}\n\n/**\n * 获取当前时间\n * @return\n */\ndef currentDate() {\n    Date d = new Date();\n    System.out.println(d);\n    SimpleDateFormat sdf = new SimpleDateFormat(\"yyyy-MM-dd HH:mm:ss\");\n    String dateNowStr = sdf.format(d);\n    return dateNowStr;\n}\n\n/**\n * 获取注解选项\n *\n * @param comment\n * @return\n */\ndef getOptions(String comment) {\n    if(comment == null) {\n        return []\n    }\n    String[] parts = comment.split(\";\");\n    if(parts.length<2) {\n        return null;\n    }\n//    String colmenName = parts[0];\n    parts = parts[1].split(\",\");\n    HashMap<String, String> dataMap = new HashMap<>();\n    for (String item: parts) {\n        String[] kv = item.split(\":\");\n        if(kv.length == 2) {\n            dataMap.put(kv[0].trim(), kv[1]);\n        } else {\n            dataMap.put(kv[0], kv[0]);\n        }\n    }\n    return dataMap;\n}\n\n/**\n * 获取前端展示类型\n *\"frontType\"\n * @param comment\n */\ndef getExtendData(String comment, String key, String defaultVal) {\n  println comment\n    String type = defaultVal\n    if(comment == \'\' || comment == null) {\n        return defaultVal\n    }\n    String[] parts = comment.split(\";\");\n    if(parts.length<3) {\n        return type;\n    }\n    if(parts.length>=3) {\n        parts = parts[2].split(\",\");\n        HashMap<String, String> dataMap = new HashMap<>();\n        for (String item: parts) {\n            String[] kv = item.split(\":\");\n            if(kv.length == 2) {\n                dataMap.put(kv[0].trim(), kv[1]);\n            }\n        }\n        if(dataMap.containsKey(key)) {\n            type = dataMap.get(key)\n        }\n    }\n    return type\n}\n\n/**\n * 获取表前端首页类型\n *\n * @param table\n * @return\n */\ndef getTableFrontHome(table) {\n    def fontType = getExtendData(table.comment, \'frontHome\', \'list\')\n    return fontType;\n}\n\n/**\n * 获取制定字段类型\n *\n * @param name\n * @param fileds\n */\ndef getCol(name, fileds) {\n    def col = null;\n    fileds.each() {\n        if(name == it.name) {\n            col = it;\n        }\n    }\n    return col;\n}\n\n/**\n * 获取表选项key\n *\n * @param comment\n * @param fileds\n * @return\n */\ndef getOptionKeyCol(comment, fileds) {\n    def optionStr = getExtendData(comment, \'option\', null)\n    if(optionStr == null) {\n        return null\n    }\n    String[] parts = optionStr.split(\"~\")\n    if(parts.length>0)  {\n        def col = getCol(parts[0], fileds)\n        if(col == null) {\n            return null;\n        }\n        return col;\n    }\n    return null;\n}\n\n/**\n * 获取表选项key\n *\n * @param comment\n * @param fileds\n * @return\n */\ndef getOptionValCol(comment, fileds) {\n    optionStr = getExtendData(comment, \'option\', null)\n    if(optionStr == null) {\n        return null\n    }\n    String[] parts = optionStr.split(\"~\")\n    if(parts.length>1)  {\n        def col = getCol(parts[1], fileds)\n        if(col == null) {\n            return null;\n        }\n        return col;\n    }\n    return null;\n}\n\n/**\n * 根据字段类型注解获取java类型\n *\n * @param dbType\n * @param comment\n */\ndef getJavaType(dbType, comment) {\n    def javaType = getExtendData(comment, \"javaType\", null)\n    if(javaType != null)  {\n        return javaType\n    }\n    //查看注解有没有指定后段类型\n    switch (dbType) {\n        case \'int\':\n        case \'tinyint\':\n        case \'smallint\':\n        case \'mediumint\':\n            javaType = \"Integer\"\n            break;\n        case \'bigint\':\n            javaType = \"BigInteger\"\n            break;\n        case \'bit\':\n            javaType = \'Boolean\'\n            break;\n        case \'data\':\n        case \'datetime\':\n        case \'timestamp\':\n            javaType = \"LocalDateTime\"\n            break;\n        case \'float\':\n            javaType = \"Float\"\n            break\n        case \'double\':\n            javaType = \"Double\"\n            break;\n        default:\n            javaType = \"String\"\n            break;\n    }\n    return javaType\n}\n\n//获取part值\ndef getPartData(String comment, Integer index, String splitStr, String defaultVal) {\n    String type = defaultVal\n    if(comment == \'\' || comment == null) {\n        return defaultVal\n    }\n    String[] parts = comment.split(splitStr);\n    if(parts.length<index) {\n        return type;\n    }\n    return parts[index]\n}\n\n//翻译接口\ndef cn2en(String q) {\n  def enStr = null\n  //  def q=\'文章状态\'\n  def key=\'SbNwXyeGQ4FEH5WdJyv1\';\n  def appid=\'20160108000008706\';\n  def salt=\'123456\';\n  def md5= cn.hutool.crypto.digest.DigestUtil.md5Hex(appid+q+salt+key)\n  try {\n    //TODO 标准版需要限速\n    //Thread.currentThread().sleep(900);\n    def content = cn.hutool.http.HttpUtil.get(\"http://api.fanyi.baidu.com/api/trans/vip/translate\",\n                                            [\'from\':\'auto\',\'to\':\'en\',\'appid\':appid,\'salt\':salt,\'q\':q,\'sign\':md5]\n                                           );\n    def json=cn.hutool.json.JSONUtil.parse(content);\n    enStr = json.get(\'trans_result\').get(0).get(\'dst\');\n  } catch(Exception e) {\n    e.printStackTrace();\n    //ignore\n  }\n  enStr;\n}','2022-12-10 00:26:16',0),(2,1736609793,'hello','hello',0,'def main(tb) {\n    String str = new String();\n    str += \"Table Name: \" + tb.getName() +\n    \"\\nTable Title: \" + tb.title + \"\\n\"\n    str += \"Table Comment: \" + tb.comment + \"\\n\"\n   str += \"\\ntest by fang${tb.name}\";\n   tb.columnList.each() {\n     str += \"=============================================================\\n\"\n     str += \"name: \"+it.name + \"\\n\"\n     str += \"dataType: \" + it.dataType + \"\\n\" \n     str += \"length: \" + it.length + \"\\n\"\n     str == \"scale: \" + it.scale + \"\\n\"\n     str += \"title: \" + it.title + \"\\n\"\n     str += \"isPk: \" + it.isPk + \"\\n\"\n     str += \"isNotNull: \" + it.isNotNull + \"\\n\"\n     str += \"isUnique: \" + it.isUnique + \"\\n\"\n     str += \"isAutoIncrement: \" + it.isAutoIncrement + \"\\n\"\n     str += \"isZeroFill: \" + it.isZeroFill + \"\\n\"\n     str += \"defaultValue: \" + it.defaultValue + \"\\n\"\n     str += \"comment: \" + it.comment + \"\\n\"\n   }\n   str\n}\n\n\ndef getCode(vo) {\n  \"helloaaaaaaaaaaaaa\"\n}\n\ndef getFileName(vo) {\n  \"a/b/c/Test2.java\"\n}','2022-12-08 00:52:37',0),(3,NULL,'testUtils','test',1,'\n\ndef main(tableVo) {\n  //shell = new GroovyShell()\n  //双向绑定\n  shell.parse(\'binding.setVariable(\"a\", \"b\")\', \'abc.groovy\').run()\n  //加载别的脚本，且调用方法; 推荐的其他脚本调用方式\n  def tool = shell.parse(context.codeTableTemplateService.getById(1).code)\n  //tool.run()\n  System.out.println(\"=========================\"+tool.camelCase(\"test_abc_name\"))\n\n  println tableVo\n  def str = context.invokeByIdWithName(1, \"main\", \"test\")\n  //小驼峰\n  def str2 = context.runById(1,\"camelCase\", \"test_by_fang\")\n  System.out.println(context.runById(1,\"camelCase\", \"test_by_fang\"))\n  System.out.println(context.runById(1,\"upperCamelCase\", \"test_by_fang\"))\n  System.out.println(context.runById(1,\"getFrontType\", \"int\", \"支付封面图片;;frontType:upload,uploadCount:1\"))\n  def t = context.codeTableService.getTableVoById(155439106);\n      \"hello    \" + t.name + \"    \" + creator + \"   \" + str + \"  \" + str2 + a\n}','2022-12-10 00:14:17',0),(1170362370,1715638274,'testzip','zip测试',1,'import work.soho.code.biz.utils.ZipUtils;\n\ndef main(t) {\n  def zip = new ZipUtils(\"/tmp/\"+t.name+\".zip\")\n  zip.appendFile(\"a.txt\", \"test by fang\")\n  zip.close()\n  \"success\"\n}','2022-12-10 02:37:38',0),(1170362371,1736609793,'JavaController','java控制器',1,'\ndef getCode(vo) {\n  generate(vo)\n}\n\ndef main(vo) {\n  getFileName(vo) + generate(vo)\n}\n\ndef getFileName(table) {\n    def tool = shell.parse(context.codeTableTemplateService.getById(1).code)\n    def t = tool.calcTable(table)\n    \"src/main/java/\"+baseNamespace.replace(\".\", \"/\") + \"controller/${t.upperCamelCase}Controller.java\"\n}\n\ndef generate(table) {\n    def domainPackageName = \"${baseNamespace}domain.\"\n    def servicePackageName = \"${baseNamespace}service.\"\n    \n    def userName = System.getProperty( \"user.name\")\n    def tool = shell.parse(context.codeTableTemplateService.getById(1).code)\n    def t = tool.calcTable(table)\n    def fields = t.fields\n    def className = t.upperCamelCase\n    def fastLowerClassName = t.camelCase\n    //读取表备注\n    def tableComment = table.getComment()\n    if(tableComment == null || tableComment == \'\') {\n        tableComment = table.getName()\n    }\n    def code = \"\"\n    code += \"package ${baseNamespace}controller;\\n\\n\"\n    code += \"import java.time.LocalDateTime;\\n\"+\n                    \"import work.soho.common.core.util.PageUtils;\\n\"+\n                    \"import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;\\n\" +\n                    \"import java.util.*;\\n\" +\n                    \"import lombok.RequiredArgsConstructor;\\n\" +\n                    \"import org.springframework.web.bind.annotation.GetMapping;\\n\" +\n                    \"import org.springframework.web.bind.annotation.PostMapping;\\n\" +\n                    \"import org.springframework.web.bind.annotation.PutMapping;\\n\" +\n                    \"import org.springframework.web.bind.annotation.DeleteMapping;\\n\" +\n                    \"import org.springframework.web.bind.annotation.PathVariable;\\n\" +\n                    \"import org.springframework.web.bind.annotation.RequestBody;\\n\" +\n                    \"import org.springframework.web.bind.annotation.RequestMapping;\\n\" +\n                    \"import org.springframework.web.bind.annotation.RestController;\\n\" +\n                    \"import work.soho.common.core.util.StringUtils;\\n\" +\n                    \"import com.github.pagehelper.PageSerializable;\\n\" +\n                    \"import work.soho.common.core.result.R;\\n\" +\n                    \"import annotation.work.soho.admin.api.Node;\\n\"+\n                    \"import ${domainPackageName}${className};\\n\" +\n                    \"import ${servicePackageName}${className}Service;\\n\" +\n                    \"import java.util.ArrayList;\\n\" +\n                    \"import java.util.HashMap;\\n\" +\n                    \"import vo.work.soho.admin.api.OptionVo;\\n\" +\n                    \"import request.work.soho.admin.api.BetweenCreatedTimeRequest;\\n\" +\n                    \"import java.util.stream.Collectors;\\n\" +        \n                    \"import vo.work.soho.admin.api.TreeNodeVo;\" \n\n      //审批相关接口引入\n      if(t.approval != null) {\n        code += \"\\nimport work.soho.approvalprocess.service.ApprovalProcessOrderService;\\n\" +\n                \"import work.soho.approvalprocess.vo.ApprovalProcessOrderVo;\\n\"\n      }\n      \n      \n      code +=              \"\\n\" +\n                    \"/**\\n\" +\n                    \" * ${t.title}Controller\\n\" +\n                    \" *\\n\" +\n                    \" * @author ${userName}\\n\" +\n                    \" */\\n\" +\n                    \"@RequiredArgsConstructor\\n\" +\n                    \"@RestController\\n\" +\n                    \"@RequestMapping(\\\"/admin/${fastLowerClassName}\\\" )\\n\" +\n                    \"public class ${className}Controller {\\n\" +\n                    \"\\n\" +\n                    \"    private final ${className}Service ${fastLowerClassName}Service;\\n\" +\n                    \"\\n\" +\n                    \"    /**\\n\" +\n                    \"     * 查询${t.title}列表\\n\" +\n                    \"     */\\n\" +\n//                    \"    @PreAuthorize(\\\"@ss.hasPermi(\'admin:${fastLowerClassName}:list\')\\\")\\n\" +\n                    \"    @GetMapping(\\\"/list\\\")\\n\" +\n                    \"    @Node(value = \\\"${fastLowerClassName}::list\\\", name = \\\"${tableComment}列表\\\")\\n\" +\n                    \"    public R<PageSerializable<${className}>> list(${className} ${fastLowerClassName}\"\n    //根据参数输出其他补充参数\n    fields.each() {\n        if(it.name == \'created_time\')\n          code += \", BetweenCreatedTimeRequest betweenCreatedTimeRequest\"\n        \n    }\n\n    code +=                        \")\\n\" +\n                    \"    {\\n\" +\n                    \"        PageUtils.startPage();\\n\" +\n                    \"        LambdaQueryWrapper<${className}> lqw = new LambdaQueryWrapper<${className}>();\\n\"\n\n    fields.each() {\n        if(it.name == \'created_time\') {\n            code += (\n                    \"        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ${className}::getCreatedTime, betweenCreatedTimeRequest.getStartTime());\\n\" +\n                            \"        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ${className}::getCreatedTime, betweenCreatedTimeRequest.getEndTime());\\n\"\n            )\n        } else if(it.javaType == \"String\") { //字符串类型处理\n            code += (\"        lqw.like(StringUtils.isNotBlank(${fastLowerClassName}.get${it.upperCamelCase}()),${className}::get${it.upperCamelCase} ,${fastLowerClassName}.get${it.upperCamelCase}());\\n\" )\n        } else {\n            code += (\"        lqw.eq(${fastLowerClassName}.get${it.upperCamelCase}() != null, ${className}::get${it.upperCamelCase} ,${fastLowerClassName}.get${it.upperCamelCase}());\\n\")\n        }\n    }\n\n    code += (\"        List<${className}> list = ${fastLowerClassName}Service.list(lqw);\\n\" +\n            \"        return R.success(new PageSerializable<>(list));\\n\" +\n            \"    }\\n\" +\n\n            \"\\n\" +\n            \"    /**\\n\" +\n            \"     * 获取${t.title}详细信息\\n\" +\n            \"     */\\n\" +\n//            \"    @PreAuthorize(\\\"@ss.hasPermi(\'system:${fastLowerClassName}:query\')\\\" )\\n\" +\n            \"    @GetMapping(value = \\\"/{id}\\\" )\\n\" +\n            \"    @Node(value = \\\"${fastLowerClassName}::getInfo\\\", name = \\\"${tableComment}详细信息\\\")\\n\" +\n            \"    public R<${className}> getInfo(@PathVariable(\\\"id\\\" ) Long id) {\\n\" +\n            \"        return R.success(${fastLowerClassName}Service.getById(id));\\n\" +\n            \"    }\\n\" +\n            \"\\n\" +\n            \"    /**\\n\" +\n            \"     * 新增${t.title}\\n\" +\n            \"     */\\n\" +\n//            \"    @PreAuthorize(\\\"@ss.hasPermi(\'system:${fastLowerClassName}:add\')\\\" )\\n\" +\n            \"    @PostMapping\\n\" +\n            \"    @Node(value = \\\"${fastLowerClassName}::add\\\", name = \\\"${tableComment}新增\\\")\\n\" +\n            \"    public R<Boolean> add(@RequestBody ${className} ${fastLowerClassName}) {\\n\"\n    )\n\n    //处理创建，更新时间\n    fields.each() {\n        if(it.name == \'createdTime\') {\n            code += (\n                    \"       ${fastLowerClassName}.setCreatedTime(LocalDateTime.now());\\n\"\n            )\n        }\n        if(it.name == \'updatedTime\') {\n            code += (\n                    \"       ${fastLowerClassName}.setUpdatedTime(LocalDateTime.now());\\n\"\n            )\n        }\n    }\n\n    code += (\n            \"        return R.success(${fastLowerClassName}Service.save(${fastLowerClassName}));\\n\" +\n            \"    }\\n\" +\n            \"\\n\" +\n            \"    /**\\n\" +\n            \"     * 修改${t.title}\\n\" +\n            \"     */\\n\" +\n            \"    @PutMapping\\n\" +\n            \"    @Node(value = \\\"${fastLowerClassName}::edit\\\", name = \\\"${tableComment}修改\\\")\\n\" +\n            \"    public R<Boolean> edit(@RequestBody ${className} ${fastLowerClassName}) {\\n\"\n    )\n\n    //处理创建，更新时间\n    fields.each() {\n        if(it.name == \'updatedTime\') {\n            code += (\n                    \"       ${fastLowerClassName}.setUpdatedTime(LocalDateTime.now());\\n\"\n            )\n        }\n    }\n\n    code += (\n            \"        return R.success(${fastLowerClassName}Service.updateById(${fastLowerClassName}));\\n\" +\n            \"    }\\n\" +\n            \"\\n\" +\n            \"    /**\\n\" +\n            \"     * 删除${t.title}\\n\" +\n            \"     */\\n\" +\n            \"    @DeleteMapping(\\\"/{ids}\\\" )\\n\" +\n            \"    @Node(value = \\\"${fastLowerClassName}::remove\\\", name = \\\"${tableComment}删除\\\")\\n\" +\n            \"    public R<Boolean> remove(@PathVariable Long[] ids) {\\n\" +\n            \"        return R.success(${fastLowerClassName}Service.removeByIds(Arrays.asList(ids)));\\n\" +\n            \"    }\\n\" )\n\n\n    //判断处理选项action判断处理选项action\n    if(tool.getExtendData(tableComment, \"option\", null) != null)  {\n        def optionKeyCol = tool.getOptionKeyCol(tableComment, fields)\n        def optionValCol = tool.getOptionValCol(tableComment, fields)\n        code += (\n                \"\\n    /**\\n\" +\n                        \"     * 获取该${t.title} options:${optionKeyCol.name}-${optionValCol.name}\\n\" +\n                        \"     *\\n\" +\n                        \"     * @return\\n\" +\n                        \"     */\\n\" +\n                        \"    @GetMapping(\\\"options\\\")\\n\" +\n                        \"    @Node(value = \\\"${fastLowerClassName}::options\\\", name = \\\"${tableComment}Options\\\")\\n\" +\n                        \"    public R<HashMap<Integer, String>> options() {\\n\" +\n                        \"        List<${className}> list = ${fastLowerClassName}Service.list();\\n\" +\n                        \"        List<OptionVo<${optionKeyCol.javaType}, ${optionValCol.javaType}>> options = new ArrayList<>();\\n\" +\n                        \"\\n\" +\n                        \"        HashMap<Integer, String> map = new HashMap<>();\\n\" +\n                        \"        for(${className} item: list) {\\n\" +\n                        \"            map.put(item.get${optionKeyCol.upperCamelCase}(), item.get${optionValCol.upperCamelCase}());\\n\" +\n                        \"        }\\n\" +\n                        \"        return R.success(map);\\n\" +\n                        \"    }\\n\"\n        )\n    }\n\n    //输出tree接口\n    def frontHome = tool.getTableFrontHome(table)\n    if(frontHome == \'tree\') {\n        code += (\n                \"\\n    @GetMapping(\\\"tree\\\")\\n\" +\n                \"    public R<List<TreeNodeVo>> tree() {\\n\" +\n                \"        List<${className}> list = ${fastLowerClassName}Service.list();\\n\" +\n                \"        List<TreeNodeVo<Integer, Integer, Integer, String>> listVo = list.stream().map(item->{\\n\" +\n                \"            return new TreeNodeVo<>(item.getId(), item.getId(), item.getParentId(), item.getTitle());\\n\" +\n                \"        }).collect(Collectors.toList());\\n\" +\n                \"\\n\" +\n                \"        Map<Integer, List<TreeNodeVo>> mapVo = new HashMap<>();\\n\" +\n                \"        listVo.stream().forEach(item -> {\\n\" +\n                \"            if(mapVo.get(item.getParentId()) == null) {\\n\" +\n                \"                mapVo.put(item.getParentId(), new ArrayList<>());\\n\" +\n                \"            }\\n\" +\n                \"            mapVo.get(item.getParentId()).add(item);\\n\" +\n                \"        });\\n\" +\n                \"\\n\" +\n                \"        listVo.forEach(item -> {\\n\" +\n                \"            if(mapVo.containsKey(item.getKey())) {\\n\" +\n                \"                item.setChildren(mapVo.get(item.getKey()));\\n\" +\n                \"            }\\n\" +\n                \"        });\\n\" +\n                \"        return R.success(mapVo.get(0));\\n\" +\n                \"    }\\n\"\n        )\n    }\n\n  \n    //输出审核接口\n    if(t.approval != null) {\n      //获取审批单号\n      def approvalNo = tool.getPartData(t.approval, 0, \"~\", \"NULL\")\n      def keyCol = tool.getPartData(t.approval, 1, \"~\", \"id\")\n      def keyColUpper = tool.upperCamelCase(keyCol)\n      code += \"\\n    /**\\n\" +\n              \"     * 修改自动化样例表\\n\" +\n              \"     */\\n\" +\n              \"    @PutMapping(\\\"apply\\\")\\n\" +\n              \"    @Node(value = \\\"${t.camelCase}::apply\\\", name = \\\"${t.comment}\\\")\\n\" +\n              \"    public R<Boolean> apply(@RequestBody ${t.upperCamelCase} ${t.camelCase}) {\\n\" +\n              \"        try {\\n\" +\n              \"            ${t.camelCase}.setUpdatedTime(LocalDateTime.now());\\n\" +\n              \"            //${t.camelCase}Service.updateById(${t.camelCase});\\n\\n\" +\n              \"            ApprovalProcessOrderVo vo  = new ApprovalProcessOrderVo();\\n\" +\n              \"            vo.setApplyUserId(SecurityUtils.getLoginUserId());\\n\" +\n              \"            vo.setOutNo(${t.camelCase}.get${keyColUpper}().toString());\\n\" +\n              \"            vo.setApprovalProcessNo(\\\"${approvalNo}\\\");\\n\" +\n              \"            vo.setCreatedTime(LocalDateTime.now());\\n\"\n\n      fields.each() {\n        if(it.isApproval == \"true\") {\n          code += \"            ApprovalProcessOrderVo.ContentItem item = new ApprovalProcessOrderVo.ContentItem();\\n\" +\n                  \"            item.setKey(\\\"${it.name.toUpperCase()}\\\");\\n\" +\n                  \"            item.setContent(example.get${it.upperCase}());\\n\" +\n                  \"            vo.getContentItemList().add(item);\\n\"           \n        }\n\n      }\n      \n       code +=\"\\n            approvalProcessOrderService.create(vo);\\n\" +\n              \"            return R.success();\\n\" +\n              \"        } catch (Exception e) {\\n\" +\n              \"            return R.error(e.getMessage());\\n\" +\n              \"        }\\n\" +\n              \"    }\\n\"\n    }\n  \n  \n    code += (        \"}\")\n}','2022-12-13 21:40:08',0),(1170362372,948080641,'ReactModel','react模型',1,'//页面生成模板\ndef main(t) {\n  def str = \"\" + getFileName(t) + \"\\n--------------------------\\n\" +  getCode(t)\n  str\n}\n\ndef getFileName(tableVo) {\n  def tool = shell.parse(context.codeTableTemplateService.getById(1).code)\n  def t = tool.calcTable(tableVo)\n  \"src/pages/\"+ t.name +\"/model.js\"\n}\n\ndef getCode(t) {\n  createCode(t)\n} \n\n\n//创建model文件\ndef createCode(tableVo) {\n  def tool = shell.parse(context.codeTableTemplateService.getById(1).code)\n  def t = tool.calcTable(tableVo)\n  def fields = t.fields\n  def  code = \"\"\n    def userName = System.getProperty( \"user.name\")\n\n    code += (\"import modelExtend from \'dva-model-extend\'\\n\" +\n            \"import api from \'api\'\\n\" +\n            \"import { pageModel } from \'utils/model\'\\n\" +\n            \"const { pathToRegexp } = require(\\\"path-to-regexp\\\")\\n\" +\n            \"const { query${t.upperCamelCase}List, update${t.upperCamelCase}, create${t.upperCamelCase},delete${t.upperCamelCase}\")\n\n    //循环输出外链表option api\n    fields.each() {\n        if (it.frontType == \'select\' || it.frontType == \'checkbox\') {\n            if(it.foreignTableUpperCamelCaseName) {\n                code += (\",query${it.foreignTableUpperCamelCaseName}Options\")\n            }\n        } else if (it.frontType == \'treeSelect\') { //tree select\n            if(it.foreignTableUpperCamelCaseName) {\n                code += (\",query${it.foreignTableUpperCamelCaseName}Tree\")\n            }\n        }\n    }\n\n    //输出表接口\n    if(t.approval != null) {\n      code += \",apply${t.upperCamelCase}\"\n    }\n\n    code += (\" } = api\\n\" +\n            \"/**\\n\" +\n            \" *\\n\" +\n            \"   //相关api信息\\n\" +\n            \" query${t.upperCamelCase}List: `GET \\${prefix}/admin/${t.camelCase}/list`,\\n\" +\n            \" update${t.upperCamelCase}: `PUT \\${prefix}/admin/${t.camelCase}`,\\n\" +\n            \" create${t.upperCamelCase}: `POST \\${prefix}/admin/${t.camelCase}`,\\n\" +\n            \" delete${t.upperCamelCase}: `DELETE \\${prefix}/admin/${t.camelCase}/:ids`,\\n\" +\n            \" query${t.upperCamelCase}Details: `GET \\${prefix}/admin/${t.camelCase}/:id`,\\n\" )\n\n    // fields.each() {\n    //     out.println(\"原始数据解析信息\")\n    //     out.print(\"name: ${it.name}   \")\n    //     out.print(\"capitalName: ${it.capitalName} \")\n    //     out.print(\"capitalKeyName: ${it.capitalKeyName}   \")\n    //     out.print(\"type: ${it.type}   \")\n    //     out.print(\"frontType: ${it.frontType}     \")\n    //     out.print(\"frontMax: ${it.frontMax}       \")\n    //     out.print(\"frontStep: ${it.frontStep}     \")\n    //     out.print(\"dbType: ${it.dbType}       \")\n    //     out.print(\"dbUnsigned: ${it.dbUnsigned}       \")\n    //     out.print(\"comment: ${it.comment}     \")\n    //     out.print(\"length: ${it.length}       \")\n    //     out.print(\"scale: ${it.scale}     \")\n    //     out.print(\"frontLength: ${it.frontLength}     \")\n    //     out.print(\"defaultValue: ${it.defaultValue}       \")\n    //     out.print(\"isNotNull: ${it.isNotNull}     \")\n    //     out.print(\"specification: ${it.specification}     \")\n    //     out.print(\"dbForeignName: ${it.dbForeignName}     \")\n    //     out.print(\"capitalForeignName: ${it.capitalForeignName}       \")\n    //     out.print(\"javaForeignName: ${it.javaForeignName}       \")\n    // }\n\n\n    code += (        \" *\\n\" +\n            \" */\" +\n            \"\\n\" +\n            \"export default modelExtend(pageModel, {\\n\" +\n            \"  namespace: \'${t.name}\',\\n\" +\n            \"  state: {\\n\" +\n            \"    options: {\"\n    )\n    fields.each() {\n        if(it.frontType == \'select\' || it.frontType == \'checkbox\') {\n            def foreign = tool.getExtendData(it.comment, \'foreign\', null)\n\n            code += (\n                    \"\\n      ${it.camelCase}: {\")\n            if(!foreign) {\n                def comment = it.comment\n                def options = tool.getOptions(comment)\n                options.each {entry ->\n                    code += (\n                            \"\\n           \'${entry.key}\': \'${entry.value}\',\"\n                    )\n                }\n            }\n            code += (\"\\n      },\\n\")\n        }\n    }\n    code += (\n            \"    },\\n\" +\n            \"    trees: {\\n\")\n    //循环输出tree options\n    fields.each() {\n        if(it.frontType == \'treeSelect\') {\n            if(it.camelCase) {\n                code += (\"           ${it.camelCase}: [],\\n\")\n            }\n        }\n    }\n\n    code += (\"    },\\n\" )\n\n    code += (\"  },\\n\" +\n            \"\\n\" +\n            \"  subscriptions: {\\n\" +\n            \"    setup({ dispatch, history }) {\\n\" +\n            \"      history.listen(location => {\\n\" +\n            \"        if (pathToRegexp(\'/${t.name}\').exec(location.pathname)) {\\n\" +\n            \"          dispatch({\\n\" +\n            \"            type: \'query\',\\n\" +\n            \"            payload: {\\n\" +\n            \"              pageSize: 20,\\n\" +\n            \"              page: 1,\\n\" +\n            \"              ...location.query,\\n\" +\n            \"            },\\n\" +\n            \"          })\")\n\n    fields.each() {\n        if (it.frontType == \'select\' || it.frontType == \'checkbox\') {\n            def foreign = tool.getExtendData(it.comment, \'foreign\', null)\n            if (foreign) {\n                code += (\n                        \"\\n          dispatch({\\n\" +\n                                \"            type: \'query${it.foreignTableUpperCamelCaseName}Options\',\\n\" +\n                                \"            payload: {},\\n\" +\n                                \"          })\"\n                )\n            }\n        } else if(it.frontType == \'treeSelect\') {\n            if(it.foreignTableUpperCamelCaseName) {\n                code += (\n                        \"\\n          dispatch({\\n\" +\n                        \"            type: \'query${it.foreignTableUpperCamelCaseName}Tree\',\\n\" +\n                        \"            payload: {},\\n\" +\n                        \"          })\")\n            }\n        }\n    }\n\n    code += (\n            \"\\n        }\\n\" +\n            \"      })\\n\" +\n            \"    },\\n\" +\n            \"  },\\n\" +\n            \"\\n\" +\n            \"  effects: {\\n\" +\n            \"    *query({ payload }, { call, put }) {\\n\" +\n            \"      const data = yield call(query${t.upperCamelCase}List, payload)\\n\" +\n            \"      if (data.success) {\\n\" +\n            \"        yield put({\\n\" +\n            \"          type: \'querySuccess\',\\n\" +\n            \"          payload: {\\n\" +\n            \"            list: data.payload.list,\\n\" +\n            \"            pagination: {\\n\" +\n            \"              current: Number(payload.page) || 1,\\n\" +\n            \"              pageSize: Number(payload.pageSize) || 10,\\n\" +\n            \"              total: data.payload.total,\\n\" +\n            \"            },\\n\" +\n            \"          },\\n\" +\n            \"        })\\n\" +\n            \"      } else {\\n\" +\n            \"        throw data\\n\" +\n            \"      }\\n\" +\n            \"    },\\n\" +\n            \"   *create({ payload }, { call, put }) {\\n\" +\n            \"      const data = yield call(create${t.upperCamelCase}, payload)\\n\" +\n            \"      if (data.success) {\\n\" +\n            \"        yield put({ type: \'hideModal\' })\\n\" +\n            \"      } else {\\n\" +\n            \"        throw data\\n\" +\n            \"      }\\n\" +\n            \"    },\\n\" +\n            \"\\n\")\n      if(t.approval != null) {\n        code += \"    //apply\\n\" +\n            \"    *apply({ payload }, { call, put }) {\\n\" +\n            \"      const data = yield call(apply${t.upperCamelCase}, payload)\\n\" +\n            \"      if (data.success) {\\n\" +\n            \"        yield put({ type: \'hideModal\' })\\n\" +\n            \"      } else {\\n\" +\n            \"        throw data\\n\" +\n            \"      }\\n\" +\n            \"    },\\n\\n\"\n      }\n\n      code +=      (\"    *update({ payload }, { select, call, put }) {\\n\" +\n//            \"      const id = yield select(({ user }) => user.currentItem.id)\\n\" +\n//            \"      const newUser = { ...payload }\\n\" +\n            \"      const data = yield call(update${t.upperCamelCase}, payload)\\n\" +\n            \"      if (data.success) {\\n\" +\n            \"        yield put({ type: \'hideModal\' })\\n\" +\n            \"      } else {\\n\" +\n            \"        throw data\\n\" +\n            \"      }\\n\" +\n            \"    },\\n\" +\n            \"    *delete({ payload }, { select, call, put }) {\\n\" +\n            \"      const data = yield call(delete${t.upperCamelCase}, {ids:payload})\\n\" +\n            \"      if (!data.success) {\\n\" +\n            \"        throw data\\n\" +\n            \"      }\\n\" +\n            \"    },\\n\")\n    //TODO 循环输出关联表查询\n    fields.each() {\n        if(it.frontType == \'select\' || it.frontType == \'checkbox\') {\n            if(it.foreignTableUpperCamelCaseName) {\n                code += (\n                        \"    *query${it.foreignTableUpperCamelCaseName}Options({ payload }, { call, put }) {\\n\" +\n                                \"      const data = yield call(query${it.foreignTableUpperCamelCaseName}Options, payload)\\n\" +\n                                \"      if (data.success) {\\n\" +\n                                \"        yield put({\\n\" +\n                                \"          type: \'queryOptions\',\\n\" +\n                                \"          payload: {\\n\" +\n                                \"              ${it.camelCase}: data.payload\\n\" +\n                                \"          },\\n\" +\n                                \"        })\\n\" +\n                                \"      } else {\\n\" +\n                                \"        throw data\\n\" +\n                                \"      }\\n\" +\n                                \"    },\"\n                )\n            }\n\n        } else if(it.frontType == \'treeSelect\') {\n            if(it.foreignTableUpperCamelCaseName) {\n                code += (\n                    \"   *query${it.foreignTableUpperCamelCaseName}Tree({payload}, {call, put}) {\\n\" +\n                            \"      const data = yield call(query${it.foreignTableUpperCamelCaseName}Tree, payload);\\n\" +\n                            \"      if(data.success) {\\n\" +\n                            \"        yield put({\\n\" +\n                            \"          type: \'querySuccessTree\',\\n\" +\n                            \"          payload: {\\n\" +\n                            \"            ${it.camelCase}: data.payload,\\n\" +\n                            \"          }\\n\" +\n                            \"        })\\n\" +\n                            \"      } else {\\n\" +\n                            \"        throw data;\\n\" +\n                            \"      }\\n\" +\n                            \"   },\\n\"\n                )\n\n            }\n        }\n\n    }\n    code += (        \"  },\\n\" +\n            \"  reducers: {\\n\" +\n            \"    showModal(state, { payload }) {\\n\" +\n            \"      return { ...state, ...payload, modalVisible: true }\\n\" +\n            \"    },\\n\" +\n            \"\\n\" +\n            \"    hideModal(state) {\\n\" +\n            \"      return { ...state, modalVisible: false }\\n\" +\n            \"    },\\n\" +\n            \"  },\\n\" +\n            \"})\")\n}','2022-12-13 21:41:39',0),(1170362373,1736609793,'javaDomain','Java Domain',1,'//前端模块页面\ndef main(t) {\n  def str = \"\" + getFileName(t) + \"\\n--------------------------\\n\" +  getCode(t)\n  str\n}\n\ndef getFileName(tableVo) {\n  def tool = shell.parse(context.codeTableTemplateService.getById(1).code)\n  def t = tool.calcTable(tableVo)\n  \"src/main/java/\" + baseNamespace.replace(\".\", \"/\") + \"domain/\" +\n  \"${t.upperCamelCase}.java\"\n}\n\ndef getCode(t) {\n  createCode(t)\n} \n\n\ndef createCode(tableVo) {\n  def tool = shell.parse(context.codeTableTemplateService.getById(1).code)\n  def t = tool.calcTable(tableVo)\n  def fields = t.fields\n  def  code = \"\"\n  //导入引入\n  code += \"package ${baseNamespace}domain;\\n\\n\" +\n          \"import com.baomidou.mybatisplus.annotation.IdType;\\n\" +\n          \"import com.baomidou.mybatisplus.annotation.TableField;\\n\" +\n          \"import com.baomidou.mybatisplus.annotation.TableId;\\n\" +\n          \"import com.baomidou.mybatisplus.annotation.TableName;\\n\" +\n          \"import com.fasterxml.jackson.annotation.JsonFormat;\\n\" +\n          \"import java.io.Serializable;\\n\" +\n          \"import lombok.Data;\\n\" +\n          \"import java.time.LocalDateTime;\\n\\n\" +\n          \"@TableName(value =\\\"${t.name}\\\")\\n\" + \n          \"@Data\\n\" +\n          \"public class ${t.upperCamelCase} implements Serializable {\\n\"\n  //循环输出字段名\n  fields.each() {\n    code += \"    /**\\n\" +\n      \"    * ${it.comment}\\n\" +\n      \"    */\\n\"\n    if(it.isPk == 1 && it.isAutoIncrement == 1) {\n      //自增主键\n      code += \"    @TableId(value = \\\"${it.name}\\\", type = IdType.AUTO)\\n\"\n    } else {\n      code += \"    @TableField(value = \\\"${it.name}\\\")\\n\"\n    }\n    //时间戳注解\n    if(it.javaType == \"LocalDateTime\") {\n      code += \"    @JsonFormat(pattern = \\\"yyyy-MM-dd HH:mm:ss\\\")\\n\"\n    }\n    code += \"    private ${it.javaType} ${it.camelCase};\\n\\n\"\n  }\n\n\n  code += \"}\"\n  \n  code\n}','2022-12-21 16:23:43',0),(1170362374,1736609793,'javaMapper','Java Mapper',1,'\ndef main(t) {\n  def str = \"\" + getFileName(t) + \"\\n--------------------------\\n\" +  getCode(t)\n  str\n}\n\ndef getFileName(tableVo) {\n  def tool = shell.parse(context.codeTableTemplateService.getById(1).code)\n  def t = tool.calcTable(tableVo)\n  \"src/main/java/\" + baseNamespace.replace(\".\", \"/\") + \"mapper/\" +\n  \"${t.upperCamelCase}Mapper.java\"\n}\n\ndef getCode(t) {\n  createCode(t)\n} \n\n\ndef createCode(tableVo) {\n  def tool = shell.parse(context.codeTableTemplateService.getById(1).code)\n  def t = tool.calcTable(tableVo)\n  def fields = t.fields\n  def  code = \"\"\n  //导入引入\n  code += \"package ${baseNamespace}mapper;\\n\\n\" +\n          \"import com.baomidou.mybatisplus.core.mapper.BaseMapper;\\n\" +\n          \"import ${baseNamespace}domain.${t.upperCamelCase};\\n\\n\" +\n\n          \"public interface ${t.upperCamelCase}Mapper extends BaseMapper<${t.upperCamelCase}> {\\n\\n\"\n\n  code += \"}\"\n  \n  code\n}','2022-12-22 01:27:46',0),(1170362375,1736609793,'javaMapperXml','java mapper xml文件',1,'\ndef main(t) {\n  def str = \"\" + getFileName(t) + \"\\n--------------------------\\n\" +  getCode(t)\n  str\n}\n\ndef getFileName(tableVo) {\n  def tool = shell.parse(context.codeTableTemplateService.getById(1).code)\n  def t = tool.calcTable(tableVo)\n  \"src/main/resoures/mapper/\"+t.upperCamelCase+\"Mapper.xml\"\n}\n\ndef getCode(t) {\n  createCode(t)\n} \n\n\ndef createCode(tableVo) {\n  def tool = shell.parse(context.codeTableTemplateService.getById(1).code)\n  def t = tool.calcTable(tableVo)\n  def fields = t.fields\n  def  code = \"\"\n  code += \"<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\"?>\\n\" +\n\"<!DOCTYPE mapper\\n\" +\n        \"PUBLIC \\\"-//mybatis.org//DTD Mapper 3.0//EN\\\"\\n\" +\n        \"\\\"http://mybatis.org/dtd/mybatis-3-mapper.dtd\\\">\\n\" +\n\"<mapper namespace=\\\"${baseNamespace}mapper.${t.upperCamelCase}Mapper\\\">\\n\" +\n    \"    <resultMap id=\\\"BaseResultMap\\\" type=\\\"${baseNamespace}domain.${t.upperCamelCase}\\\">\\n\"\n  //循环输出字段名\n  fields.each() {\n    def type = \"VARCHAR\"\n    switch(it.dataType) {\n      case \"int\":\n        type = \"INTEGER\"\n        break;\n      case \"tinyint\":\n        type = \"TINYINT\"\n        break;\n      case \"datetime\":\n        type = \"DATE\"\n        break;\n      case \"bigint\":\n        type = \"BIGINT\"\n        break;\n    }\n    code += \"        <id property=\\\"${it.camelCase}\\\" column=\\\"${it.name}\\\" jdbcType=\\\"${type}\\\"/>\\n\"\n  }\n\n  code += \"    </resultMap>\\n\"\n\n  code += \"    <sql id=\\\"Base_Column_List\\\">\\n        \"\n  int i=0;\n  fields.each() {\n    i++\n    if(i == 1) {\n      code += \"${it.name}\"\n    } else {\n      code += \",${it.name}\"\n    }\n    \n  }\n  code += \"\\n    </sql>\\n\"\n\n  code += \"</mapper>\"\n  \n  code\n}','2022-12-22 03:01:57',0),(1170362376,1715638274,'pageTemplate','Page Template',0,'//页面生成模板\ndef main(t) {\n  def str = \"\" + getFileName(t) + \"\\n--------------------------\\n\" +  getCode(t)\n  str\n}\n\ndef getFileName(tableVo) {\n  def tool = shell.parse(context.codeTableTemplateService.getById(1).code)\n  def t = tool.calcTable(tableVo)\n  \"src/main/java/xxx/\"+t.upperCamelCase+\"Xxx.java\"\n}\n\ndef getCode(t) {\n  createCode(t)\n} \n\n\ndef createCode(tableVo) {\n  def tool = shell.parse(context.codeTableTemplateService.getById(1).code)\n  def t = tool.calcTable(tableVo)\n  def fields = t.fields\n  def  code = \"\"\n\n  fields.each() {\n    code += \",${it.name}\"\n  }\n  \n  code\n}','2022-12-22 03:40:40',0),(1170362377,1736609793,'javaService','Java Service',1,'def main(t) {\n  def str = \"\" + getFileName(t) + \"\\n--------------------------\\n\" +  getCode(t)\n  str\n}\n\ndef getFileName(tableVo) {\n  def tool = shell.parse(context.codeTableTemplateService.getById(1).code)\n  def t = tool.calcTable(tableVo)\n  \"src/main/java/\" + baseNamespace.replace(\".\", \"/\") + \"service/\"+t.upperCamelCase+\"Service.java\"\n}\n\ndef getCode(t) {\n  createCode(t)\n} \n\n\ndef createCode(tableVo) {\n  def tool = shell.parse(context.codeTableTemplateService.getById(1).code)\n  def t = tool.calcTable(tableVo)\n  def fields = t.fields\n  def  code = \"\"\n\n  code += \"package ${baseNamespace}service;\\n\\n\"\n  code += \"import ${baseNamespace}domain.${t.upperCamelCase};\\n\"\n  code += \"import com.baomidou.mybatisplus.extension.service.IService;\\n\\n\"\n\n  code += \"public interface ${t.upperCamelCase}Service extends IService<${t.upperCamelCase}> {\\n\\n}\"\n  \n  code\n}','2022-12-22 03:46:29',0),(1170362378,1736609793,'javaServiceImpl','Java Service实现',1,'//页面生成模板\ndef main(t) {\n  def str = \"\" + getFileName(t) + \"\\n--------------------------\\n\" +  getCode(t)\n  str\n}\n\ndef getFileName(tableVo) {\n  def tool = shell.parse(context.codeTableTemplateService.getById(1).code)\n  def t = tool.calcTable(tableVo)\n  \"src/main/java/\" + baseNamespace.replace(\".\", \"/\") + \"service/impl/\"+t.upperCamelCase+\"ServiceImpl.java\"\n}\n\ndef getCode(t) {\n  createCode(t)\n} \n\n\ndef createCode(tableVo) {\n  def tool = shell.parse(context.codeTableTemplateService.getById(1).code)\n  def t = tool.calcTable(tableVo)\n  println(t)\n  def fields = t.fields\n  def  code = \"\"\n\n  code += \"package ${baseNamespace}service.impl;\\n\\n\" +\n    \"import org.springframework.stereotype.Service;\\n\" +\n    \"import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;\\n\" +\n    \"import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;\\n\" +\n    \"import lombok.RequiredArgsConstructor;\\n\" +\n    \"import ${baseNamespace}domain.${t.upperCamelCase};\\n\" +\n    \"import ${baseNamespace}mapper.${t.upperCamelCase}Mapper;\\n\" +\n    \"import ${baseNamespace}service.${t.upperCamelCase}Service;\\n\\n\" +\n\n    \"@RequiredArgsConstructor\\n\" +\n    \"@Service\\n\" +\n    \"public class ${t.upperCamelCase}ServiceImpl extends ServiceImpl<${t.upperCamelCase}Mapper, ${t.upperCamelCase}>\\n\" +\n    \"    implements ${t.upperCamelCase}Service{\\n\\n}\" +\n\n  code\n}','2022-12-22 03:56:24',0),(1170362382,948080641,'reactIndex','React Index',1,'//前端入口页\ndef main(t) {\n  def str = \"\" + getFileName(t) + \"\\n--------------------------\\n\" +  getCode(t)\n  str\n}\n\ndef getFileName(tableVo) {\n  def tool = shell.parse(context.codeTableTemplateService.getById(1).code)\n  def t = tool.calcTable(tableVo)\n  \"src/pages/\"+ t.name + \"/index.js\"\n}\n\ndef getCode(t) {\n  createCode(t)\n} \n\n\n//创建主页 index\ndef createCode(tabelVo) {\n   def tool = shell.parse(context.codeTableTemplateService.getById(1).code)\n   def t = tool.calcTable(tabelVo)\n   def code = \"\"\n  \n    def frontHome = tool.getTableFrontHome(t);\n    //写入内容\n    code += (\"import React, { PureComponent } from \'react\'\\n\" +\n            \"import PropTypes from \'prop-types\'\\n\" +\n            \"import {connect, history} from \'umi\'\\n\" +\n            \"import { stringify } from \'qs\'\\n\" +\n            \"import { t } from \\\"@lingui/macro\\\"\\n\" +\n            \"import { Page } from \'components\'\\n\" +\n            \"import List from \'./components/List\'\\n\" +\n            \"import ${t.upperCamelCase}Modal from \\\"./components/Modal\\\";\\n\" +\n            \"import Filter from \\\"./components/Filter\\\"\\n\\n\" )\n    if(frontHome == \'tree\') {\n        code += (\n                \"import SohoTree from \\\"./components/SohoTree\\\";\\n\"\n        );\n    }\n\n\n    code += (        \"\\n\" +\n            \"@connect(({ ${t.name}, loading }) => ({ ${t.name}, loading }))\\n\" +\n            \"class ${t.upperCamelCase} extends PureComponent {\\n\" +\n            \"  handleRefresh = newQuery => {\\n\" +\n            \"    const { location } = this.props\\n\" +\n            \"    const { query, pathname } = location\\n\" +\n            \"    if(newQuery && newQuery.createTime) {\\n\" +\n            \"      newQuery.startDate = newQuery.createTime[0]\\n\" +\n            \"      newQuery.endDate = newQuery.createTime[1]\\n\" +\n            \"      delete newQuery.createTime\\n\" +\n            \"    }\\n\" +\n            \"    history.push({\\n\" +\n            \"      pathname,\\n\" +\n            \"      search: stringify(\\n\" +\n            \"        {\\n\" +\n            \"          ...query,\\n\" +\n            \"          ...newQuery,\\n\" +\n            \"        },\\n\" +\n            \"        { arrayFormat: \'repeat\' }\\n\" +\n            \"      ),\\n\" +\n            \"    })\\n\" +\n            \"  }\\n\"+\n            \"  handleTabClick = key => {\\n\" +\n            \"    const { pathname } = this.props.location\\n\" +\n            \"\\n\" +\n            \"    history.push({\\n\" +\n            \"      pathname,\\n\" +\n            \"      search: stringify({\\n\" +\n            \"        status: key,\\n\" +\n            \"      }),\\n\" +\n            \"    })\\n\" +\n            \"  }\\n\" +\n            \"\\n\" +\n            \"  get listProps() {\\n\" +\n            \"    const { ${t.name}, loading, location, dispatch } = this.props\\n\" +\n            \"    const { list, pagination } = ${t.name}\\n\" +\n            \"    const { query, pathname } = location\\n\" +\n\n            \"    return {\\n\" +\n            \"      options: ${t.name}.options,\\n\" +\n            \"      trees: ${t.name}.trees,\\n\" +\n            \"      pagination,\\n\" +\n            \"      dataSource: list,\\n\" +\n            \"      loading: loading.effects[\'${t.name}/query\'],\\n\" +\n            \"      onChange(page) {\\n\" +\n            \"        history.push({\\n\" +\n            \"          pathname,\\n\" +\n            \"          search: stringify({\\n\" +\n            \"            ...query,\\n\" +\n            \"            page: page.current,\\n\" +\n            \"            pageSize: page.pageSize,\\n\" +\n            \"          }),\\n\" +\n            \"        })\\n\" +\n            \"      },\\n\" +\n            \"      onDeleteItem: id => {\\n\" +\n            \"        dispatch({\\n\" +\n            \"          type: \'${t.name}/delete\',\\n\" +\n            \"          payload: id,\\n\" +\n            \"        }).then(() => {\\n\" +\n            \"          this.handleRefresh({\\n\" +\n            \"            page:\\n\" +\n            \"              list.length === 1 && pagination.current > 1\\n\" +\n            \"                ? pagination.current - 1\\n\" +\n            \"                : pagination.current,\\n\" +\n            \"          })\\n\" +\n            \"        })\\n\" +\n            \"      },\\n\" +\n            \"      onEditItem(item) {\\n\" +\n            \"        dispatch({\\n\" +\n            \"          type: \'${t.name}/showModal\',\\n\" +\n            \"          payload: {\\n\" +\n            \"            modalType: \'update\',\\n\" +\n            \"            currentItem: item,\\n\" +\n            \"          },\\n\" +\n            \"        })\\n\" +\n            \"      },\\n\" +\n            \"      onApplyItem(item) {\\n\" +\n            \"        dispatch({\\n\" +\n            \"          type: \'${t.name}/showModal\',\\n\" +\n            \"          payload: {\\n\" +\n            \"            modalType: \'apply\',\\n\" +\n            \"            currentItem: item,\\n\" +\n            \"          }\\n\" +\n            \"        })\\n\" +\n            \"      },\\n\" +\n            \"      onChildItem(item) {\\n\" +\n            \"        dispatch({\\n\" +\n            \"          type: \'${t.name}/showModal\',\\n\" +\n            \"          payload: {\\n\" +\n            \"            modalType: \'create\',\\n\" +\n            \"            currentItem: {...item},\\n\" +\n            \"          },\\n\" +\n            \"        })\\n\" +\n            \"      },\" +\n            \"    }\\n\" +\n            \"  }\\n\" +\n            \"\\n\" +\n            \"  get modalProps() {\\n\" +\n            \"    const { dispatch, ${t.name}, loading } = this.props\\n\" +\n            \"    const { currentItem, modalVisible, modalType,resourceTree,resourceIds } = ${t.name}\\n\" +\n            \"\\n\" +\n            \"    return {\\n\" +\n            \"      options: ${t.name}.options,\\n\" +\n            \"      trees: ${t.name}.trees,\\n\" +\n            \"      item: modalType === \'create\' ? {...currentItem} : currentItem,\\n\" +\n            \"      visible: modalVisible,\\n\" +\n            \"      destroyOnClose: true,\\n\" +\n            \"      maskClosable: false,\\n\" +\n            \"      confirmLoading: loading.effects[`${t.name}/\\${modalType}`],\\n\" +\n            \"      resourceTree: resourceTree,\\n\" +\n            \"      resourceIds: resourceIds,\\n\" +\n            \"      title: `\\${\\n \" +\n                \"         modalType === \'create\' ? t`Create` : (modalType === \'update\' ? t`Update` : t`Apply`)\\n\" +\n                        \"      }`,\\n\" +\n                        \"      centered: true,\\n\" +\n                        \"      onOk: data => {\\n\" +\n                        \"        dispatch({\\n\" +\n                        \"          type: `${t.name}/\\${modalType}`,\\n\" +\n                        \"          payload: data,\\n\" +\n                        \"        }).then(() => {\\n\" +\n                        \"          this.handleRefresh()\\n\" +\n                        \"        })\\n\" +\n                        \"      },\\n\" +\n                        \"      onCancel() {\\n\" +\n                        \"        dispatch({\\n\" +\n                        \"          type: \'${t.name}/hideModal\',\\n\" +\n                        \"        })\\n\" +\n                        \"      },\\n\" +\n                        \"    }\\n\" +\n                        \"  }\\n\" +\n                        \"\\n\" +\n                        \"  get filterProps() {\\n\" +\n                        \"    const { location, dispatch, ${t.name}} = this.props\\n\" +\n                        \"    const {query,pathname} = location\\n\" +\n                        \"    return {\\n\" +\n                        \"      options: ${t.name}.options,\\n\" +\n                        \"      trees: ${t.name}.trees,\\n\" +\n                        \"      filter: {\\n\" +\n                        \"        ...query,\\n\" +\n                        \"      },\\n\" +\n                        \"      onFilterChange(values) {\\n\" +\n                        \"        history.push({\\n\" +\n                        \"          pathname,\\n\" +\n                        \"          search: stringify(values),\\n\" +\n                        \"        })\\n\" +\n                        \"      },\\n\" +\n                        \"      onAdd() {\\n\" +\n                        \"        dispatch({\\n\" +\n                        \"          type: \'${t.name}/showModal\',\\n\" +\n                        \"          payload: {\\n\" +\n                        \"            modalType: \'create\',\\n\" +\n                        \"            currentItem: {},\\n\" + \n                        \"          },\\n\" +\n                        \"        })\\n\" +\n                        \"      },\\n\" +\n                        \"    }\\n\" +\n                        \"  }\\n\" +\n                        \"\\n\" +\n                        \"  render() {\\n\" +\n                        \"    return (\\n\" +\n                        \"      <Page inner>\\n\\n\" )\n\n    if(frontHome == \'list\') {\n        code += (\n                \"        <Filter {...this.filterProps} />\\n\" +\n                        \"        <List {...this.listProps} />\\n\"\n        )\n    } else if (frontHome == \'tree\') {\n        code += (\n                \"<SohoTree {...this.listProps} />\\n\"\n        )\n    }\n\n    code += (\n                        \"        <${t.upperCamelCase}Modal {...this.modalProps} />\\n\" +\n                        \"      </Page>\\n\" +\n                        \"    )\\n\" +\n                        \"  }\\n\" +\n                        \"}\\n\" +\n                        \"\\n\" +\n                        \"${t.upperCamelCase}.propTypes = {\\n\" +\n                        \"  ${t.name}: PropTypes.object,\\n\" +\n                        \"  loading: PropTypes.object,\\n\" +\n                        \"  location: PropTypes.object,\\n\" +\n                        \"  dispatch: PropTypes.func,\\n\" +\n                        \"}\\n\" +\n                        \"\\n\" +\n                        \"export default ${t.upperCamelCase}\\n\");\n}\n','2022-12-24 00:09:36',0),(1170362383,948080641,'reactFilter','React Filter',1,'//前端搜索页面\ndef main(t) {\n  def str = \"\" + getFileName(t) + \"\\n--------------------------\\n\" +  getCode(t)\n  str\n}\n\ndef getFileName(tableVo) {\n  def tool = shell.parse(context.codeTableTemplateService.getById(1).code)\n  def t = tool.calcTable(tableVo)\n  \"src/pages/\"+t.name+\"/components/Filter.js\"\n}\n\ndef getCode(t) {\n  createCode(t)\n} \n\n\ndef createCode(tableVo) {\n  def tool = shell.parse(context.codeTableTemplateService.getById(1).code)\n  def t = tool.calcTable(tableVo)\n  def fields = t.fields\n  def  code = \"\"\n\n  code += (\n            \"import React, { Component } from \'react\'\\n\" +\n                    \"import PropTypes from \'prop-types\'\\n\" +\n                    \"import moment from \'moment\'\\n\" +\n                    \"import { FilterItem } from \'components\'\\n\" +\n                    \"import { Trans,t } from \\\"@lingui/macro\\\"\\n\" +\n                    \"import { Button, Row, Col, DatePicker, Form, Input, Cascader, Select } from \'antd\'\\n\" +\n                    \"\\n\" +\n                    \"const { Search } = Input\\n\" +\n                    \"const { RangePicker } = DatePicker\\n\" +\n                    \"const ColProps = {\\n\" +\n                    \"  xs: 24,\\n\" +\n                    \"  sm: 12,\\n\" +\n                    \"  style: {\\n\" +\n                    \"    marginBottom: 16,\\n\" +\n                    \"  },\\n\" +\n                    \"}\\n\" +\n                    \"\\n\" +\n                    \"const TwoColProps = {\\n\" +\n                    \"  ...ColProps,\\n\" +\n                    \"  xl: 96,\\n\" +\n                    \"}\\n\" +\n                    \"\\n\" +\n                    \"class Filter extends Component {\\n\" +\n                    \"  formRef = React.createRef()\\n\" +\n                    \"\\n\" +\n                    \"  handleFields = fields => {\\n\" +\n                    \"    const { betweenCreatedTime } = fields\\n\" +\n                    \"    if (betweenCreatedTime && betweenCreatedTime.length) {\\n\" +\n                    \"      fields.betweenCreatedTime = [\\n\" +\n                    \"        moment(betweenCreatedTime[0]).format(\'YYYY-MM-DD\'),\\n\" +\n                    \"        moment(betweenCreatedTime[1]).format(\'YYYY-MM-DD\'),\\n\" +\n                    \"      ]\\n\" +\n                    \"    }\\n\" +\n                    \"    return fields\\n\" +\n                    \"  }\\n\" +\n                    \"\\n\" +\n                    \"  handleSubmit = () => {\\n\" +\n                    \"    const { onFilterChange } = this.props\\n\" +\n                    \"    const values = this.formRef.current.getFieldsValue()\\n\" +\n                    \"    const fields = this.handleFields(values)\\n\" +\n                    \"    onFilterChange(fields)\\n\" +\n                    \"  }\\n\" +\n                    \"\\n\" +\n                    \"  handleReset = () => {\\n\" +\n                    \"    const fields = this.formRef.current.getFieldsValue()\\n\" +\n                    \"    for (let item in fields) {\\n\" +\n                    \"      if ({}.hasOwnProperty.call(fields, item)) {\\n\" +\n                    \"        if (fields[item] instanceof Array) {\\n\" +\n                    \"          fields[item] = []\\n\" +\n                    \"        } else {\\n\" +\n                    \"          fields[item] = undefined\\n\" +\n                    \"        }\\n\" +\n                    \"      }\\n\" +\n                    \"    }\\n\" +\n                    \"    this.formRef.current.setFieldsValue(fields)\\n\" +\n                    \"    this.handleSubmit()\\n\" +\n                    \"  }\\n\" +\n                    \"  handleChange = (key, values) => {\\n\" +\n                    \"    const { onFilterChange } = this.props\\n\" +\n                    \"    let fields = this.formRef.current.getFieldsValue()\\n\" +\n                    \"    fields[key] = values\\n\" +\n                    \"    fields = this.handleFields(fields)\\n\" +\n                    \"    onFilterChange(fields)\\n\" +\n                    \"  }\\n\" +\n                    \"\\n\" +\n                    \"  render() {\\n\" +\n                    \"    const { onAdd, filter, options, trees } = this.props\\n\" +\n                    \"\\n\" +\n                    \"    let initialCreateTime = []\\n\" +\n                    \"    if (filter[\'betweenCreatedTime[0]\']) {\\n\" +\n                    \"      initialCreateTime[0] = moment(filter[\'betweenCreatedTime[0]\'])\\n\" +\n                    \"    }\\n\" +\n                    \"    if (filter[\'betweenCreatedTime[1]\']) {\\n\" +\n                    \"      initialCreateTime[1] = moment(filter[\'betweenCreatedTime[1]\'])\\n\" +\n                    \"    }\\n\")\n    \n    //初始化options\n    fields.each() {\n        def frontType = it.frontType\n        if(it.isFilter != \'true\') return;\n        if(frontType == \'select\' || frontType == \'checkbox\') {\n            code += (    \"    const ${it.camelCase}OptionData = Object.keys(options.${it.camelCase}).map((k,index)=>{\" )\n            if(it.javaType == \'String\') {\n                code += (\"      return {value: k, label: options.${it.camelCase}[k]}\")\n            } else {\n                code += (\"      return {value: parseInt(k), label: options.${it.camelCase}[k]}\")\n            }\n            code += (                \"    })\\n\"\n            )\n        }\n    }\n\n    code += (                \"\\n\" +\n                    \"    return (\\n\" +\n                    \"      <Form ref={this.formRef} name=\\\"control-ref\\\" initialValues={{ ...filter, betweenCreatedTime: initialCreateTime }}>\\n\" +\n                    \"        <Row gutter={24}>\\n\"\n    )\n    //输出需要搜索的字段\n    fields.each() {\n        if(it.name == \'name\' || it.name == \'title\' || it.name == \'username\'\n            || it.camelCase == \'orderNo\' || it.camelCase == \'trackingNo\'\n        ) {\n            code += (\n                            \"          <Col {...ColProps} xl={{ span: 4 }} md={{ span: 8 }}>\\n\" +\n                            \"            <Form.Item name=\\\"${it.camelCase}\\\">\\n\" +\n                            \"              <Search\\n\" +\n                            \"                placeholder={t`${it.frontName}`}\\n\" +\n                            \"                onSearch={this.handleSubmit}\\n\" +\n                            \"              />\\n\" +\n                            \"            </Form.Item>\\n\" +\n                            \"          </Col>\\n\"\n            )\n        } else if(it.camelCase == \'createdTime\') {\n            code += (\n                    \"          <Col\\n\" +\n                            \"            {...ColProps}\\n\" +\n                            \"            xl={{ span: 6 }}\\n\" +\n                            \"            md={{ span: 8 }}\\n\" +\n                            \"            sm={{ span: 12 }}\\n\" +\n                            \"            id=\\\"createTimeRangePicker\\\"\\n\" +\n                            \"          >\\n\" +\n                            \"            <FilterItem label={t`${it.frontName}`}>\\n\" +\n                            \"              <Form.Item name=\\\"between${it.upperCamelCase}\\\">\\n\" +\n                            \"                <RangePicker\\n\" +\n                            \"                  style={{ width: \'100%\' }}\\n\" +\n                            \"                />\\n\" +\n                            \"              </Form.Item>\\n\" +\n                            \"            </FilterItem>\\n\" +\n                            \"          </Col>\\n\"\n            )\n        } else if(it.isFilter == \'true\') {\n            if(it.frontType == \'select\') {\n                code += (\n                        \"        <Col {...ColProps} xl={{ span: 4 }} md={{ span: 8 }}>\\n\" +\n                                \"            <Form.Item name=\\\"${it.camelCase}\\\">\\n\" +\n                        \"           <Select\\n\" +\n                                \"             showSearch\\n\" +\n                                \"             placeholder={t`${it.frontName}`}\\n\" +\n                                \"             optionFilterProp=\\\"children\\\"\\n\" +\n                                \"             options={${it.camelCase}OptionData}\\n\" +\n                                \"             />\\n\" +\n                                \"            </Form.Item>\\n\" +\n                                \"          </Col>\"\n                )\n            } else {\n                code += (\n                        \"          <Col {...ColProps} xl={{ span: 4 }} md={{ span: 8 }}>\\n\" +\n                                \"            <Form.Item name=\\\"${it.camelCase}\\\">\\n\" +\n                                \"              <Search\\n\" +\n                                \"                placeholder={t`${it.frontName}`}\\n\" +\n                                \"                onSearch={this.handleSubmit}\\n\" +\n                                \"              />\\n\" +\n                                \"            </Form.Item>\\n\" +\n                                \"          </Col>\\n\"\n                )\n            }\n\n        }\n    }\n    code += (\n\n                    \"          <Col\\n\" +\n                    \"            {...TwoColProps}\\n\" +\n                    \"            xl={{ span: 10 }}\\n\" +\n                    \"            md={{ span: 24 }}\\n\" +\n                    \"            sm={{ span: 24 }}\\n\" +\n                    \"          >\\n\" +\n                    \"            <Row type=\\\"flex\\\" align=\\\"middle\\\" justify=\\\"space-between\\\">\\n\" +\n                    \"              <div>\\n\" +\n                    \"                <Button\\n\" +\n                    \"                  type=\\\"primary\\\" htmlType=\\\"submit\\\"\\n\" +\n                    \"                  className=\\\"margin-right\\\"\\n\" +\n                    \"                  onClick={this.handleSubmit}\\n\" +\n                    \"                >\\n\" +\n                    \"                  <Trans>Search</Trans>\\n\" +\n                    \"                </Button>\\n\" +\n                    \"                <Button onClick={this.handleReset}>\\n\" +\n                    \"                  <Trans>Reset</Trans>\\n\" +\n                    \"                </Button>\\n\" +\n                    \"              </div>\\n\" +\n                    \"              <Button type=\\\"ghost\\\" onClick={onAdd}>\\n\" +\n                    \"                <Trans>Create</Trans>\\n\" +\n                    \"              </Button>\\n\" +\n                    \"            </Row>\\n\" +\n                    \"          </Col>\\n\" +\n                    \"        </Row>\\n\" +\n                    \"      </Form>\\n\" +\n                    \"    )\\n\" +\n                    \"  }\\n\" +\n                    \"}\\n\" +\n                    \"\\n\" +\n                    \"Filter.propTypes = {\\n\" +\n                    \"  onAdd: PropTypes.func,\\n\" +\n                    \"  filter: PropTypes.object,\\n\" +\n                    \"  onFilterChange: PropTypes.func,\\n\" +\n                    \"}\\n\" +\n                    \"\\n\" +\n                    \"export default Filter\\n\"\n    );\n}','2022-12-24 16:48:23',0),(1170362384,948080641,'reactList','React List',1,'//前端列表页\ndef main(t) {\n  def str = \"\" + getFileName(t) + \"\\n--------------------------\\n\" +  getCode(t)\n  str\n}\n\ndef getFileName(tableVo) {\n  def tool = shell.parse(context.codeTableTemplateService.getById(1).code)\n  def t = tool.calcTable(tableVo)\n  \"src/pages/\" + t.name + \"/components/List.js\"\n}\n\ndef getCode(t) {\n  createCode(t)\n} \n\n\ndef createCode(tableVo) {\n  def tool = shell.parse(context.codeTableTemplateService.getById(1).code)\n  def t = tool.calcTable(tableVo)\n  def fields = t.fields\n  def  code = \"\"\n\n    code += (\"import React, { PureComponent } from \'react\'\\n\" +\n            \"import {Table, Avatar, Modal, Tooltip, Typography} from \'antd\'\\n\" +\n            \"import {t, Trans} from \\\"@lingui/macro\\\"\\n\" +\n            \"import { Ellipsis } from \'components\'\\n\" +\n//            \"import styles from \'./List.less\'\\n\" +\n            \"import {DropOption} from \\\"../../../components\\\";\\n\" +\n            \"import PropTypes from \\\"prop-types\\\";\\n\" +\n            \"import {getNavigationTreeTitle} from \\\"../../../utils/tree\\\";\\n\" +\n            \"const { confirm } = Modal\\n\" +\n            \"const { Text } = Typography;\\n\" +\n            \"\\n\" +\n            \"class List extends PureComponent {\\n\" +\n            \"  handleMenuClick = (record, e) => {\\n\" +\n            \"    const { onDeleteItem, onEditItem \")\n        if(t.approval != null) {\n          code += \",onApplyItem\"\n        }     \n        code +=    (\" }= this.props\\n\" +\n            \"\\n\" +\n            \"    if (e.key === \'1\') {\\n\" +\n            \"      onEditItem(record)\\n\" +\n            \"    } else if (e.key === \'2\') {\\n\" +\n            \"      confirm({\\n\" +\n            \"        title: t`Are you sure delete this record?`,\\n\" +\n            \"        onOk() {\\n\" +\n            \"          onDeleteItem(record.id)\\n\" +\n            \"        },\\n\" +\n            \"      })\\n\" +\n            \"    }\")\n        if(t.approval != null) {\n          code += \" else if (e.key === \'3\') {\\n\" +\n                  \"      onApplyItem(record)\\n\" +\n                  \"    }\"\n        }    \n        code+= (\"\\n  }\\n\" +\n            \"\\n\" +\n            \"  render() {\\n\" +\n            \"    const { options,trees,...tableProps } = this.props\\n\" +\n            \"    const columns = [\\n\")\n\n            fields.each() {\n                def ignoreInList = tool.getExtendData(it.comment, \'ignoreInList\', \'false\')\n                if(ignoreInList == \'false\') {\n                    code += (\n                            \"      {\\n\" +\n                                    \"        title: t`${it.frontName}`,\\n\" +\n                                    \"        dataIndex: \'${it.camelCase}\',\\n\" +\n                                    \"        key: \'${it.camelCase}\',\\n\"\n//                                \"        fixed: \'left\',\"\n                    )\n                    def frontType = it.frontType\n                    if(frontType == \'select\' || frontType == \'checkbox\') {\n                        code += (\n                                \"        render: (text, recode) => {\\n\" +\n                                        \"          let map = options.${it.camelCase};\\n\" )\n                        code += (\n                                \"          return map[text]?map[text]:\'未知\'\\n\" +\n                                        \"        },\\n\"\n                        )\n                    } else if(it.javaType == \"String\") {\n                        code += (\n                                \"           render:(text, recode) => {\\n\" +\n                                        \"          if(text != null && text.length>60) {\\n\" +\n                                        \"            const start = text.slice(0, 120).trim();\\n\" +\n//                                    \"            const suffix = text.slice(-20).trim();\\n\" +\n                                        \"            return <Text style={{ maxWidth: \'100%\' }} ellipsis={ \\\"...\\\" }>{start}</Text>\\n\" +\n                                        \"          } else {\\n\" +\n                                        \"            return text\\n\" +\n                                        \"          }\\n\" +\n                                        \"        }\\n\"\n                        )\n                    } else if(it.frontType == \'treeSelect\') {\n                        code += (\n                                \"        render: (text, recode) => {\\n\" +\n                                        \"          return getNavigationTreeTitle(text, trees.${it.camelCase})\\n\" +\n                                        \"        }\\n\"\n                        )\n                    }\n                    code += (\n                            \"      },\\n\"\n                    )\n                }\n\n            }\n\n     code += (\n\n            \"      {\\n\" +\n            \"        title: <Trans>Operation</Trans>,\\n\" +\n            \"        key: \'operation\',\\n\" +\n            \"        fixed: \'right\',\\n\" +\n            \"        width: \'8%\',\\n\" +\n            \"        render: (text, record) => {\\n\" +\n            \"          return (\\n\" +\n            \"            <DropOption\\n\" +\n            \"              onMenuClick={e => this.handleMenuClick(record, e)}\\n\" +\n            \"              menuOptions={[\\n\" +\n            \"                { key: \'1\', name: t`Update` },\\n\" +\n            \"                { key: \'2\', name: t`Delete` },\\n\")\n        if(t.approval != null) {\n              code += \"                { key: \'3\', name: t`Apply` },\"\n        }\n        code +=    (\"              ]}\\n\" +\n            \"            />\\n\" +\n            \"          )\\n\" +\n            \"        },\\n\" +\n            \"      },\\n\" +\n            \"    ]\\n\" +\n            \"\\n\" +\n            \"    return (\\n\" +\n            \"      <Table\\n\" +\n            \"        {...tableProps}\\n\" +\n            \"        pagination={{\\n\" +\n            \"          ...tableProps.pagination,\\n\" +\n            \"          showTotal: (total)=>{return t`Total ` + total + t` Items`},\\n\" +\n            \"        }}\\n\" +\n            \"        bordered\\n\" +\n            \"        scroll={{ x: 1200 }}\\n\" +\n//            \"        className={styles.table}\\n\" +\n            \"        columns={columns}\\n\" +\n            \"        simple\\n\" +\n            \"        rowKey={record => record.id}\\n\" +\n            \"      />\\n\" +\n            \"    )\\n\" +\n            \"  }\\n\" +\n            \"}\\n\" +\n            \"\\n\" +\n            \"List.propTypes = {\\n\" +\n            \"  onDeleteItem: PropTypes.func,\\n\" +\n            \"  onEditItem: PropTypes.func,\\n\" +\n            \"  location: PropTypes.object,\\n\" +\n            \"}\\n\" +\n            \"\\n\" +\n            \"export default List\\n\");\n}','2022-12-24 21:42:00',0),(1170362385,948080641,'React Tree','React Tree',1,'//页面生成模板\ndef main(t) {\n  def str = \"\" + getFileName(t) + \"\\n--------------------------\\n\" +  getCode(t)\n  str\n}\n\ndef getFileName(tableVo) {\n  def tool = shell.parse(context.codeTableTemplateService.getById(1).code)\n  def t = tool.calcTable(tableVo)\n  \"src/pages/\"+t.name+\"/components/SohoTree.js\"\n}\n\ndef getCode(t) {\n  createCode(t)\n} \n\n\ndef createCode(tableVo) {\n  def tool = shell.parse(context.codeTableTemplateService.getById(1).code)\n  def t = tool.calcTable(tableVo)\n  def fields = t.fields\n  def  code = \"\"\n\n    code += (\n            \"import React, { PureComponent } from \'react\'\\n\" +\n                    \"import {Table, Avatar, Modal, Tooltip, Typography, Tree, Input} from \'antd\'\\n\" +\n                    \"import PropTypes from \\\"prop-types\\\";\\n\" +\n                    \"import { DownOutlined, PlusOutlined, CloseOutlined, EditOutlined } from \'@ant-design/icons\';\\n\" +\n                    \"import {addRootNode} from \\\"../../../utils/tree\\\";\\n\" +\n                    \"const { confirm } = Modal\\n\" +\n                    \"const { Text } = Typography;\\n\" +\n                    \"const { TreeNode } = Tree;\\n\" +\n                    \"\\n\" +\n                    \"class SohoTree extends PureComponent {\\n\" +\n                    \"  map = {}\\n\" +\n                    \"  parentList = []\\n\" +\n                    \"\\n\" +\n                    \"  constructor(props) {\\n\" +\n                    \"    super(props);\\n\" +\n//                    \"    const {dataSource} = this.props;\\n\" +\n//                    \"    const {map, parentList} = this.loadDataFromList(dataSource)\\n\" +\n//                    \"    this.state = {dataSource: this.loadTree(0, map, parentList), map,parentList}\\n\" +\n                    \"  }\\n\" +\n                    \"\\n\" +\n                    \"  //编辑选中节点\\n\" +\n                    \"  onEdit = (key) => {\\n\" +\n                    \"    const { onEditItem } = this.props\\n\" +\n                    \"    onEditItem(this.map[key])\\n\" +\n                    \"  };\\n\" +\n                    \"\\n\" +\n                    \"  //删除选中节点\\n\" +\n                    \"  onDelete = (key) => {\\n\" +\n                    \"    const { onDeleteItem } = this.props\\n\" +\n                    \"    onDeleteItem(key)\\n\" +\n                    \"  };\\n\" +\n                    \"\\n\" +\n                    \"  //添加选中节点子结点\\n\" +\n                    \"  onAdd = (key) => {\\n\" +\n                    \"    const { onChildItem } = this.props\\n\" +\n                    \"    onChildItem({parentId: key})\\n\" +\n                    \"  };\\n\" +\n                    \"\\n\" +\n                    \"  onTitleRender = (item) => {\\n\" +\n                    \"    return (\\n\" +\n                    \"      <div style={{ display: \'flex\', alignItems: \'center\', height:30 }}>\\n\" +\n                    \"\\n\" +\n                    \"        <span>\\n\" +\n                    \"          {item.title}\\n\" +\n                    \"        </span>\\n\" +\n                    \"        <span style={{ display: \'flex\', marginLeft: 30 }}>\\n\" +\n                    \"          <PlusOutlined style={{ marginLeft: 10 }} onClick={() => this.onAdd(item.key)} />\\n\" +\n                    \"          {item.key != 0 &&\\n\" +\n                    \"            <>\\n\" +\n                    \"              <EditOutlined style={{marginLeft: 10}} onClick={() => this.onEdit(item.key)}/>\\n\" +\n                    \"              <CloseOutlined style={{marginLeft: 10}} onClick={() => this.onDelete(item.key)}/>\\n\" +\n                    \"            </>\\n\" +\n                    \"          }\\n\" +\n                    \"        </span>\\n\" +\n                    \"      </div>\\n\" +\n                    \"    );\\n\" +\n                    \"  };\\n\" +\n                    \"\\n\" +\n                    \"  loadTree = (parentId, map, parentList) => {\\n\" +\n                    \"    let sons = []\\n\" +\n                    \"    if(parentList[parentId] == null) {\\n\" +\n                    \"      return sons;\\n\" +\n                    \"    }\\n\" +\n                    \"    parentList[parentId].map((item)=>{\\n\" +\n                    \"      //检查是否有子结点\\n\" +\n                    \"      let currentData = {\\n\" +\n                    \"        key: item.id,\\n\" +\n                    \"        title: item.title\\n\" +\n                    \"      }\\n\" +\n                    \"      if(parentList[item.id] != null) {\\n\" +\n                    \"        currentData[\'children\'] = this.loadTree(item.id, map, parentList)\\n\" +\n                    \"      }\\n\" +\n                    \"      sons.push(currentData)\\n\" +\n                    \"    })\\n\" +\n                    \"    return sons;\\n\" +\n                    \"  }\\n\" +\n                    \"\\n\" +\n                    \"  loadDataFromList = (list) => {\\n\" +\n                    \"    let map = {}\\n\" +\n                    \"    let parentList = {}\\n\" +\n                    \"    list.map((item)=>{\\n\" +\n                    \"      map[item.id] = item\\n\" +\n                    \"      if(parentList[item.parentId] == null) {\\n\" +\n                    \"        parentList[item.parentId] = []\\n\" +\n                    \"      }\\n\" +\n                    \"      parentList[item.parentId].push(item)\\n\" +\n                    \"    })\\n\" +\n                    \"\\n\" +\n                    \"    return {map: map, parentList: parentList}\\n\" +\n                    \"    // return this.loadTree(0, map, parentList)\\n\" +\n                    \"  }\\n\" +\n                    \"\\n\" +\n                    \"  render() {\\n\" +\n                    \"    const { options,dataSource,...tableProps } = this.props\\n\" +\n                    \"    const {map, parentList} = this.loadDataFromList(dataSource)\\n\" +\n                    \"    this.map = map\\n\" +\n                    \"    this.parentList = parentList\\n\" +\n                    \"    return (\\n\" +\n                    \"      <div style={{margin:\'30 px\'}}>\\n\" +\n                    \"        <Tree\\n\" +\n                    \"          showLine\\n\" +\n                    \"          switcherIcon={<DownOutlined />}\\n\" +\n                    \"          treeData={addRootNode(this.loadTree(0, map, parentList))}\\n\" +\n                    \"          defaultExpandAll={true}\\n\" +\n                    \"          titleRender={this.onTitleRender}\\n\" +\n                    \"        />\\n\" +\n                    \"      </div>\\n\" +\n                    \"\\n\" +\n                    \"    )\\n\" +\n                    \"  }\\n\" +\n                    \"}\\n\" +\n                    \"\\n\" +\n                    \"SohoTree.propTypes = {\\n\" +\n                    \"  onDeleteItem: PropTypes.func,\\n\" +\n                    \"  onEditItem: PropTypes.func,\\n\" +\n                    \"  location: PropTypes.object,\\n\" +\n                    \"}\\n\" +\n                    \"\\n\" +\n                    \"export default SohoTree\"\n    )\n}','2022-12-24 21:44:17',0),(1170362386,948080641,'reactModal','React Modal',1,'//前端编辑框\ndef main(t) {\n  def str = \"\" + getFileName(t) + \"\\n--------------------------\\n\" +  getCode(t)\n  str\n}\n\ndef getFileName(tableVo) {\n  def tool = shell.parse(context.codeTableTemplateService.getById(1).code)\n  def t = tool.calcTable(tableVo)\n  \"src/pages/\" + t.name + \"/components/Modal.js\"\n}\n\ndef getCode(t) {\n  createCode(t)\n} \n\n\ndef createCode(tableVo) {\n  def tool = shell.parse(context.codeTableTemplateService.getById(1).code)\n  def t = tool.calcTable(tableVo)\n  def fields = t.fields\n  def  code = \"\"\n\n   code += (\n            \"import React, { PureComponent } from \'react\'\\n\" +\n                    \"import PropTypes from \'prop-types\'\\n\" +\n                    \"import {Form, Input, InputNumber, Radio, Modal, Select, Upload, message,Checkbox,DatePicker,TreeSelect} from \'antd\'\\n\" +\n                    \"import TextArea from \\\"antd/es/input/TextArea\\\";\\n\" +\n                    \"import {t, Trans} from \\\"@lingui/macro\\\"\\n\" +\n                    \"import ImgCrop from \\\"antd-img-crop\\\";\\n\" +\n                    \"import {LoadingOutlined, PlusOutlined} from \\\"@ant-design/icons\\\";\\n\" +\n                    \"import moment from \'moment\';\\n\" +\n                    \"import api from \'../../../services/api\'\\n\" +\n                    \"import store from \\\"store\\\";\\n\" +\n                    \"import QuillEditor from \\\"../../../components/Editor/QuillEditor\\\";\\n\" +\n                    \"import {addRootNode} from \\\"../../../utils/tree\\\";\\n\" +\n                    \"const {createUserAvatar, createAdminContentImage} = api\\n\" +\n                    \"\\n\" +\n                    \"const { Option } = Select;\\n\" +\n                    \"const FormItem = Form.Item\\n\" +\n                    \"//时间格式化\\n\" +\n                    \"const datetimeFormat = \\\"YYYY-MM-DD HH:mm:ss\\\"\\n\" +\n                    \"const dateFormat = \\\"YYYY-MM-DD\\\"\\n\" +\n                    \"const formItemLayout = {\\n\" +\n                    \"  labelCol: {\\n\" +\n                    \"    span: 6,\\n\" +\n                    \"  },\\n\" +\n                    \"  wrapperCol: {\\n\" +\n                    \"    span: 14,\\n\" +\n                    \"  },\\n\" +\n                    \"}\\n\" +\n\n                    \"\\n\" +\n                    \"class ${t.upperCamelCase}Modal extends PureComponent {\\n\" +\n                    \"  formRef = React.createRef()\\n\" +\n                    \"\\n\" +\n                    \"  state = {\\n\" +\n                    \"    value: [],\\n\" +\n                    \"    loading: false, //上传 loading\\n\" +\n                    \"  };\\n\" +\n                    \"\\n\" +\n\n                    \"\\n\" +\n                    \"  handleOk = () => {\\n\" +\n                    \"    const { item = {}, onOk } = this.props\\n\" +\n                    \"    this.formRef.current.validateFields()\\n\" +\n                    \"      .then(values => {\\n\" +\n                    \"        const data = {\\n\" +\n                    \"          ...values,\\n\" +\n                    \"          id: item.id,\")\n    code += (\"          //时间值处理\\n\")\n    fields.each() {\n        def frontType = it.frontType\n        if(frontType == \'datetime\' || frontType == \'date\') {\n            code += (\"          ${it.camelCase}: values.${it.camelCase} ? moment(values.${it.camelCase}).format(${frontType}Format) : null,\\n\")\n        } else if(frontType == \'upload\') {\n            code += (\"          ${it.camelCase}:((values.${it.camelCase}?.fileList || values.${it.camelCase})?.map(value => {\\n\" +\n                    \"          if(value.url) return value.url;\\n\" +\n                    \"          if(value.response.payload) return value.response.payload;\\n\" +\n                    \"         }) || []).join(\\\";\\\"),\\n\")\n        }\n    }\n    code += (                \"        }\\n\" +\n                    \"        onOk(data)\\n\" +\n                    \"      })\\n\" +\n                    \"      .catch(errorInfo => {\\n\" +\n                    \"        console.log(errorInfo)\\n\" +\n                    \"      })\\n\" +\n                    \"  }\\n\" +\n                    \"\\n\" +\n                    \"  render() {\\n\" +\n                    \"    const { item = {}, onOk, form,options,trees, ...modalProps } = this.props\\n\" )\n    //初始化options\n    fields.each() {\n        def frontType = it.frontType\n        if(frontType == \'select\' || frontType == \'checkbox\') {\n            code += (    \"    const ${it.camelCase}OptionData = Object.keys(options.${it.camelCase}).map((k,index)=>{\\n\" )\n            if(it.javaType == \'String\') {\n                code += (\"      return {value: k, label: options.${it.camelCase}[k]}\\n\")\n            } else {\n                code += (\"      return {value: parseInt(k), label: options.${it.camelCase}[k]}\\n\")\n            }\n            code += (                \"    })\\n\"\n            )\n        }\n    }\n    //初始化时间相关参数\n    code += (\"    let initData = {\\n\")\n    //初始化默认值\n    fields.each() {\n         if(it.defaultValue != null && it.defaultValue != null) {\n            code += (\"${it.name}: ${it.defaultValue},\\n\")\n        }\n    }\n\n    code += (\"                    ...item};\\n\")\n    code += (\"    //初始化时间处理\\n\")\n    fields.each() {\n        def frontType = it.frontType\n        if(frontType == \'datetime\') {\n            code += (\"    if(item.${it.camelCase}) initData[\'${it.camelCase}\'] = moment(item.${it.camelCase},${frontType}Format)\\n\")\n        } else if (frontType == \'date\') {\n            code += (\"    if(item.${it.camelCase}) initData[\'${it.camelCase}\'] = moment(item.${it.camelCase},${frontType}Format)\\n\")\n        } else if (frontType == \'upload\') {\n            code += (\"   initData[\'${it.camelCase}\'] = (initData.${it.camelCase}?.split(\';\').map((item, key) => {\\n\" +\n                    \"        return {\\n\" +\n                    \"          uid: -key,\\n\" +\n                    \"          key: item,\\n\" +\n                    \"          name: item,\\n\" +\n                    \"          url: item\\n\" +\n                    \"        }\\n\" +\n                    \"      }) || []);\\n\")\n        }\n    }\n    code += (\n                    \"    return (\\n\" +\n                    \"\\n\" +\n                    \"      <Modal {...modalProps} onOk={this.handleOk} width={1300}>\\n\" +\n                    \"        <Form ref={this.formRef} name=\\\"control-ref\\\" initialValues={{ ...initData }} layout=\\\"horizontal\\\">\\n\"\n    );\n\n            fields.each() {\n                if(it.camelCase != \'id\' && it.camelCase != \'createdTime\' && it.camelCase != \'updatedTime\') {\n                    def maxStr = \"\"\n                    def max = tool.getFrontMax(it)\n                    def minStr = \"\"\n                    def min = tool.getFrontMin(it)\n                    if(max) {\n                        maxStr = \", max: \\\"${max}\\\"\"\n                    }\n                    if(min != null) {\n                        minStr = \", min: \\\"${min}\\\"\"\n                    }\n                    //def ftable = tool.getForeignTableCapitalKey(it.comment)\n                    code += (\n                            \"         <FormItem  \" +\n                           //         \"frontStep=\'${it.frontStep}\' frontMax=\'${it.frontMax}\' frontLength=\'${it.frontLength}\' l=\'${it.length}\' t=\'${it.scale}\' \" +\n                                    \"name=\'${it.camelCase}\' rules={[{ required: ${it.isNotNull} }]}\"\n                    )\n                    def frontType = it.frontType\n                    if(frontType == \'select\') {\n                        code += (\n                                        \"            label={t`${it.frontName}`} hasFeedback {...formItemLayout}>\\n\" +\n                                        \"           <Select\\n\" +\n                                        \"             showSearch\\n\" +\n                                        \"             placeholder={t`${it.frontName}`}\\n\" +\n                                        \"             optionFilterProp=\\\"children\\\"\\n\" +\n                                        \"             options={${it.camelCase}OptionData}\\n\" +\n                                        \"             disabled={${it.editReadOnly}}\\n\" +\n                                        \"             />\\n\"\n                        )\n                    } else if(frontType == \'treeSelect\') {\n                        code += (\n                                \"            label={t`${it.frontName}`} hasFeedback {...formItemLayout}>\\n\" +\n                                        \"       <TreeSelect\\n\" +\n                                        \"             showSearch\\n\" +\n                                        \"             style={{ width: \'100%\' }}\\n\" +\n                                        \"             // value={value}\\n\" +\n                                        \"             dropdownStyle={{ maxHeight: 400, overflow: \'auto\' }}\\n\" +\n                                        \"             placeholder=\\\"Please select\\\"\\n\" +\n                                        \"             allowClear\\n\" +\n                                        \"             treeDefaultExpandAll\\n\" +\n                                        \"             // onChange={onChange}\\n\" +\n                                        \"             disabled={${it.editReadOnly}}\\n\" +\n                                        \"             treeData={addRootNode(trees?.${it.camelCase})}\\n\" +\n                                        \"           />\\n\"\n                        )\n                    } else if(frontType == \'checkbox\') {\n                        code += (\n                                        \"            label={t`${it.frontName}`} hasFeedback {...formItemLayout}>\\n\" +\n                                        \"           <Checkbox.Group\\n\" +\n                                        \"             options={${it.camelCase}OptionData}\\n\" +\n                                        \"           />\\n\"\n                        )\n                    } else if(frontType == \'datetime\' || frontType == \'date\') {\n                        code += (\n                                        \"            label={t`${it.frontName}`} hasFeedback {...formItemLayout}>\\n\" +\n                                        \"            <DatePicker disabled={${it.editReadOnly}} format={${frontType}Format} showNow showTime={{ format: ${frontType}Format }} />\\n\"\n                        )\n                    } else if(frontType == \'upload\') {\n                        def uploadCount = tool.getExtendData(it.comment, \'uploadCount\', \'1\')\n                        code += (\n                                        \"             valuePropName={\\\"defaultFileList\\\"} label={t`${it.frontName}`} hasFeedback {...formItemLayout}>\\n\" +\n                                        \"           <Upload listType=\\\"picture-card\\\"\\n\" +\n                                        \"                   // defaultFileList={defaultFileList}\\n\" +\n                                        \"                   name=\\\"avatar\\\"\\n\" +\n                                        \"                   action={createUserAvatar}\\n\" +\n                                        \"                   disabled={${it.editReadOnly}}\\n\" +\n                                        \"                   // onChange={this.handleChange}\\n\" +\n                                        \"                   headers={{Authorization:store.get(\'token\')}}\\n\" +\n                                        \"                   maxCount={${uploadCount}}>\\n\" +\n                                        \"             <div>\\n\" +\n                                        \"               <PlusOutlined />\\n\" +\n                                        \"               <div style={{ marginTop: 8 }}>Upload</div>\\n\" +\n                                        \"             </div>\\n\" +\n                                        \"           </Upload>\\n\"\n                        )\n                    } else if(frontType == \'number\') {\n                        code += (\n                                \"            label={t`${it.frontName}`} hasFeedback {...formItemLayout}>\\n\" +\n                                        \"            <InputNumber\\n\" )\n                        //校验规则\n//                        if(min) code += (\" minLenght={${min}}\")\n//                        if(max) code += (\" maxLength={${max}}\")\n                        if(it.frontMax) code += (\" max={${it.frontMax}}\")\n                        if(it.frontStep) code += (\" step=\\\"${it.frontStep}\\\"\")\n                        code += (\"  style={{width:200}}\")\n                        code += \"             disabled={${it.editReadOnly}}\\n\"\n                        code += (\" />\\n\")\n                    } else if (frontType == \'editor\') {\n                        code += (\n                                \"           label={t`${it.frontName}`} hasFeedback {...formItemLayout}>\\n\" +\n                                        \"           <QuillEditor disabled={${it.editReadOnly}} />\\n\"\n                        )\n                    } else if(frontType == \'password\') {\n                        code += (\n                                \"            label={t`${it.frontName}`} hasFeedback {...formItemLayout}>\\n\" +\n                                        \"            <Input.Password\\n\" )\n                        //校验规则\n                        if(min) code += (\" minLenght={${min}}\")\n                        if(max) code +=(\" maxLength={${max}}\")\n                        code += \"             disabled={${it.editReadOnly}}\\n\"\n                        code += (\" />\\n\")\n                    } else if (frontType == \'textArea\') {\n                        code += (\n                                \"            label={t`${it.frontName}`} hasFeedback {...formItemLayout}>\\n\" +\n                                        \"            <TextArea\" )\n                        code += \"             disabled={${it.editReadOnly}}\\n\"\n                        //校验规则\n//                        if(min) code += (\" minLenght={${min}}\")\n//                        if(max) code += (\" maxLength={${max}}\")\n                        code += (\" />\\n\")\n                    } else {\n                        code += (\n                                        \"            label={t`${it.frontName}`} hasFeedback {...formItemLayout}>\\n\" +\n                                        \"            <Input\" )\n                        code += \"             disabled={${it.editReadOnly}}\\n\"\n                        //校验规则\n                        if(min) code += (\" minLenght={${min}}\")\n                        if(max) code += (\" maxLength={${max}}\")\n                                                code += (\" />\\n\")\n\n                    }\n                    code += (\"          </FormItem>\\n\")\n                }\n            }\n\n    code += (                    \"        </Form>\\n\" +\n                    \"      </Modal>\\n\" +\n                    \"    )\\n\" +\n                    \"  }\\n\" +\n                    \"}\\n\" +\n                    \"\\n\" +\n                    \"${t.upperCamelCase}Modal.propTypes = {\\n\" +\n                    \"  type: PropTypes.string,\\n\" +\n                    \"  item: PropTypes.object,\\n\" +\n                    \"  onOk: PropTypes.func,\\n\" +\n                    \"}\\n\" +\n                    \"\\n\" +\n                    \"export default ${t.upperCamelCase}Modal\\n\"\n    );\n}','2022-12-24 22:03:08',0),(1170362387,1736609793,'javaApprovalListener','Java Approval Event Listener',1,'//前端模块页面\ndef main(t) {\n  def str = \"\" + getFileName(t) + \"\\n--------------------------\\n\" +  getCode(t)\n  str\n}\n\ndef getFileName(tableVo) {\n    def tool = shell.parse(context.codeTableTemplateService.getById(1).code)\n    def t = tool.calcTable(tableVo)\n  if(t.approval != null) {\n    return \"src/main/java/\"+baseNamespace.replace(\".\", \"/\") + \"event/\" +\"${t.upperCamelCase}ApprovalEventListener.java\"\n  }\n  null\n}\n\ndef getCode(t) {\n  createCode(t)\n} \n\n//TODO 1 调整审批单号；2 调整整理审批通过，拒绝状态字段以及值 \ndef createCode(tableVo) {\n  def tool = shell.parse(context.codeTableTemplateService.getById(1).code)\n  def t = tool.calcTable(tableVo)\n  def fields = t.fields\n\n  //检查是否需要输出\n  if(t.approval == null) {\n    return null\n  }  \n\n  //获取审批ID\n  def applyId = tool.getPartData(t.approval, 0, \"~\", \"4\")\n  def idColumn = tool.getPartData(t.approval, 1, \"~\", \"id\")\n  \n  def  code = \"\"\n  //导入引入\n  code += \"package ${baseNamespace}event;\\n\" +\n          \"import lombok.RequiredArgsConstructor;\\n\" +\n          \"import lombok.extern.log4j.Log4j2;\\n\" +\n          \"import org.springframework.context.ApplicationListener;\\n\" +\n          \"import org.springframework.scheduling.annotation.Async;\\n\" +\n          \"import org.springframework.stereotype.Component;\\n\" +\n          \"import work.soho.approvalprocess.domain.ApprovalProcessOrder;\\n\" +\n          \"import work.soho.approvalprocess.event.ApprovalEvent;\\n\" +\n          \"import ${baseNamespace}domain.${t.upperCamelCase};\\n\" +\n          \"import ${baseNamespace}service.${t.upperCamelCase}Service;\\n\\n\" +\n\n          \"@RequiredArgsConstructor\\n\" +\n          \"@Component\\n\" +\n          \"@Log4j2\\n\" +\n          \"public class ${t.upperCamelCase}ApprovalEventListener implements ApplicationListener<ApprovalEvent> {\\n\"+\n          \"    /**\\n\" +\n          \"     * 审批流ID\\n\"+\n          \"     */\\n\"+\n\"    private static final String APPROVAL_NO = \\\"${applyId}\\\";\\n\\n\"+\n\"    final private ${t.upperCamelCase}Service ${t.camelCase}Service;\\n\\n\" +\n\n    \"    @Override\\n\"+\n\"    @Async\\n\"+\n\"    public void onApplicationEvent(ApprovalEvent event) {\\n\"+\n\"        ApprovalProcessOrder order = (ApprovalProcessOrder) event.getSource();\\n\"+\n\"        //检查师傅为当前对应审批流\\n\"+\n\"        if(!event.getApprovalProcessNo().equals(APPROVAL_NO)) {\\n\"+\n\"            return;\\n\"+\n\"        }\\n\"+\n\"        //处理业务逻辑，　修改样例状态为审批通过\\n\"+\n\"        if(order.getStatus() == 1) {\\n\"+\n\"            ${t.upperCamelCase} ${t.camelCase} = ${t.camelCase}Service.getById(order.getOutNo());\\n\"+\n\"            if(${t.camelCase} != null) {\\n\"+\n\"                if(order.getApplyStatus() == 2) {\\n\"+\n\"                    //审批通过\\n\"+\n\"                    ${t.camelCase}.setStatus(1);\\n\"+\n\"                } else if(order.getApplyStatus() == 1){\\n\"+\n\"                    //审批拒绝\\n\"+\n\"                    ${t.camelCase}.setStatus(2);\\n\"+\n\"                }\\n\"+\n    //TODO 根据唯一ID进行更新\n\"                ${t.camelCase}Service.updateById(${t.camelCase});\\n\"+\n\"            } else {\\n\"+\n\"                log.error(\\\"无效审批单: {}\\\", event);\\n\"+\n\"            }\\n\"+\n\"        } else {\\n\"+\n\"            log.info(\\\"审批中间环节： {}\\\", event);\\n\"+\n\"        }\\n\"+\n\"    }\"\n\n  code += \"\\n}\"\n  \n  code\n}','2022-12-31 01:02:37',0),(1170362388,1736609793,'enums','Java枚举生成',1,'//页面生成模板\ndef main(t) {\n  def str = \"\" + getFileName(t) + \"\\n--------------------------\\n\" +  getCode(t)\n  str\n}\n\ndef getFileName(tableVo) {\n  def tool = shell.parse(context.codeTableTemplateService.getById(1).code)\n  def t = tool.calcTable(tableVo)\n  \"src/main/java/\" + baseNamespace.replace(\".\", \"/\") + \"enums/\" +\n  \"${t.upperCamelCase}Enums.java\"\n}\n\ndef getCode(t) {\n  createCode(t)\n} \n\n\ndef createCode(tableVo) {\n  def tool = shell.parse(context.codeTableTemplateService.getById(1).code)\n  def t = tool.calcTable(tableVo)\n  def fields = t.fields\n  def  code = \"\"\n\n  code += \"package ${baseNamespace}enums;\\n\\n\"+\n          \"import lombok.Getter;\\n\"+\n          \"import lombok.RequiredArgsConstructor;\\n\\n\"+\n          \"public class ${t.upperCamelCase}Enums {\";\n\n  fields.each() {\n    code += getFieldEnumCode(tool, it);\n  }\n\n  code += \"\\n}\";\n  \n  code //+ tool.cn2en(\"文章状态\").replaceAll(\" \", \"_\").toUpperCase()\n}\n\n// 获取字段枚举代码\ndef getFieldEnumCode(tool, field) {\n  def code = \"\"\n  if(field.options == null || field.options.size() == 1) {\n    return \"\";\n  }\n\n  code += \"\\n\\n    @RequiredArgsConstructor\\n\"+\n\"    @Getter\\n\" +\n\"    public enum ${field.upperCamelCase} {\\n\"\n\n  //TODO 确定最后一个枚举使用分号\n  int i =0;\n  field.options.each{ data ->\n    if(data != null && data.value != \'\') {\n      code += \"        \" + tool.cn2en(data.value).replaceAll(\" \", \"_\").toUpperCase() + \'(\' + data.key + \',\"\'+data.value+\'\")\'\n      i++\n      if(i<field.options.size()) {\n        code += \',\\n\'\n      }else {\n        code += \';\\n\'\n      }\n    }\n  }\n  code += \"        private final int id;\\n\" +\n          \"        private final String name;\\n\"\n  code += \"    }\"\n\n  //生成对应字段的枚举类型\n  code\n}','2023-03-25 17:19:26',0);
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
INSERT INTO `code_table_template_group` VALUES (948080641,'前段','React','/media/fang/ssd1t/home/fang/work/html/soho-admin-front/','getPage;getFileName','获取一个前端页面','2022-12-17 06:43:57','2022-12-13 02:13:17',0),(1715638274,'其他','other','/media/fang/ssd1t/home/fang/work/java/admin','main','其他不便归类','2022-12-23 20:17:13','2022-12-14 10:01:56',10),(1736609793,'Java代码','Java','/home/fang/work/java/admin','getPage;getFileName','获取一个页面','2023-02-02 13:48:20','2022-12-12 17:43:15',1);
/*!40000 ALTER TABLE `code_table_template_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `example`
--

DROP TABLE IF EXISTS `example`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `example` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `title` varchar(45) DEFAULT NULL COMMENT '标题',
  `category_id` int(11) DEFAULT NULL COMMENT '分类ID;;frontType:treeSelect,foreign:example_category.id~title,frontName:category',
  `option_id` varchar(45) DEFAULT NULL COMMENT '支付方式ID;;frontType:select,foreign:pay_info.id~title',
  `content` text COMMENT '富媒体;;frontType:editor,ignoreInList:true',
  `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `status` tinyint(4) DEFAULT NULL COMMENT '状态;0:待审批,1:活跃,2:未通过审批;frontType:select',
  `apply_status` tinyint(4) DEFAULT NULL COMMENT '申请状态',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2026102797 DEFAULT CHARSET=utf8 COMMENT='自动化样例表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `example`
--

LOCK TABLES `example` WRITE;
/*!40000 ALTER TABLE `example` DISABLE KEYS */;
INSERT INTO `example` VALUES (2026102795,'标题1aaaaa',12,'1','<p>dfsdafsdafsdafdfsadfdsfasdfsdfsadfsadf</p>','2023-02-05 21:26:36','2022-12-30 23:53:23',1,0),(2026102796,'标题4',12,'2','<p>cvxcvxzvcx</p>','2023-02-05 20:54:00','2022-12-30 23:55:12',1,1);
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
INSERT INTO `example_category` VALUES (1,'标题1',6,'2022-11-24 00:19:16','2022-11-18 21:58:37','2022-12-31','2022-11-23 00:27:37','https://igogo-test.oss-cn-shenzhen.aliyuncs.com/user/avatar/2022_11_23_107613429073735680.jpeg'),(2,'标题2',0,'2022-11-24 17:12:32','2022-11-18 21:58:51','2022-11-17','2022-11-26 02:11:30','https://igogo-test.oss-cn-shenzhen.aliyuncs.com/user/avatar/2022_11_24_107999845301645312.jpeg'),(3,'标题3',0,'2022-12-27 23:10:53','2022-11-18 21:59:04',NULL,NULL,'https://igogo-test.oss-cn-shenzhen.aliyuncs.com/user/avatar/2022_12_27_120275656356425728.jpg'),(6,'标题1-1',1,'2022-11-23 00:52:27','2022-11-18 21:59:46','2022-11-15','2022-11-23 00:00:05','https://igogo-test.oss-cn-shenzhen.aliyuncs.com/user/avatar/2022_11_23_107615203113332736.jpeg'),(7,'标题1-2',1,'2022-11-20 01:00:36','2022-11-18 21:59:58',NULL,NULL,''),(8,'标题2-1',2,'2022-11-24 02:15:01','2022-11-18 22:00:24','2022-11-24','2022-11-24 00:00:04','https://igogo-test.oss-cn-shenzhen.aliyuncs.com/user/avatar/2022_11_24_108000770992926720.jpeg'),(9,'标题2-2',2,'2022-11-24 02:16:57','2022-11-18 22:00:37',NULL,NULL,'https://igogo-test.oss-cn-shenzhen.aliyuncs.com/user/avatar/2022_11_24_108001299781414912.jpeg'),(10,'标题2-1-1',8,'2022-12-30 11:57:28','2022-11-18 22:00:55','2022-11-10','2022-12-30 11:57:24','https://igogo-test.oss-cn-shenzhen.aliyuncs.com/user/avatar/2022_12_27_120275585502048256.jpg'),(11,'啊啊啊aaa标题2-1-2',8,'2022-11-24 02:17:08','2022-11-18 22:01:16',NULL,NULL,'https://igogo-test.oss-cn-shenzhen.aliyuncs.com/user/avatar/2022_11_24_108001338733916160.jpeg'),(12,'这也是个子标题',10,'2022-12-30 11:57:38','2022-11-22 00:31:31','2022-11-26','2022-11-18 00:25:57','https://igogo-test.oss-cn-shenzhen.aliyuncs.com/user/avatar/2022_12_27_120275499682394112.jpg'),(14,'标题3-1',3,'2022-11-24 00:02:01','2022-11-24 00:02:01','2022-11-24','2022-11-23 00:01:57','https://igogo-test.oss-cn-shenzhen.aliyuncs.com/user/avatar/2022_11_24_107967346919895040.jpeg'),(15,'标题4',0,'2022-12-27 23:11:00','2022-11-25 01:11:57','2022-11-25','2022-11-25 00:00:06','https://igogo-test.oss-cn-shenzhen.aliyuncs.com/user/avatar/2022_11_25_108347331685937152.jpeg');
/*!40000 ALTER TABLE `example_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `example_option`
--

DROP TABLE IF EXISTS `example_option`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `example_option` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `key` varchar(45) DEFAULT NULL COMMENT 'key',
  `value` varchar(45) DEFAULT NULL COMMENT 'value',
  `updated_time` datetime NOT NULL COMMENT '更新时间',
  `created_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `key_UNIQUE` (`key`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `example_option`
--

LOCK TABLES `example_option` WRITE;
/*!40000 ALTER TABLE `example_option` DISABLE KEYS */;
INSERT INTO `example_option` VALUES (1,'AAAAAAAAAAaa','BBBBBBBBB','2023-07-31 22:05:08','2023-01-01 00:00:00'),(2,'bbbbbbb','bbbbbbbbb','2023-02-05 21:26:54','2023-01-01 00:00:00');
/*!40000 ALTER TABLE `example_option` ENABLE KEYS */;
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
-- Table structure for table `groovy_group`
--

DROP TABLE IF EXISTS `groovy_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `groovy_group` (
  `id` int(11) NOT NULL,
  `name` varchar(45) DEFAULT NULL COMMENT '组名',
  `title` varchar(45) DEFAULT NULL COMMENT '组标题',
  `status` tinyint(4) DEFAULT NULL COMMENT '组状态;1:正常,2:禁用',
  `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='groovy分组;;option:id~title';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `groovy_group`
--

LOCK TABLES `groovy_group` WRITE;
/*!40000 ALTER TABLE `groovy_group` DISABLE KEYS */;
INSERT INTO `groovy_group` VALUES (683810818,'chat','Chat',1,'2023-07-23 17:43:05',NULL);
/*!40000 ALTER TABLE `groovy_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `groovy_info`
--

DROP TABLE IF EXISTS `groovy_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `groovy_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `group_id` int(11) DEFAULT NULL COMMENT '组ID;;frontType:select,foreign:groovy_group.id~title',
  `name` varchar(45) DEFAULT NULL,
  `code` text,
  `created_time` datetime DEFAULT NULL,
  `updated_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `groovy_info`
--

LOCK TABLES `groovy_info` WRITE;
/*!40000 ALTER TABLE `groovy_info` DISABLE KEYS */;
INSERT INTO `groovy_info` VALUES (1,683810818,'test','class GroovyStringUtils {\n    // Function to check if a string is empty or null\n    static boolean isEmpty(String input) {\n        return input == null || input.trim() == \'\'\n    }\n\n    // Function to check if a string is not empty and not null\n    static boolean isNotEmpty(String input) {\n        return !isEmpty(input)\n    }\n\n    // Function to reverse a string\n    static String reverse(String input) {\n        return input.reverse()\n    }\n\n    // Function to count the number of occurrences of a substring in a string\n    static int countOccurrences(String input, String substring) {\n        int count = 0\n        int index = 0\n        while ((index = input.indexOf(substring, index)) != -1) {\n            count++\n            index += substring.length()\n        }\n        return count\n    }\n\n    // Function to truncate a string to a given length\n    static String truncate(String input, int maxLength) {\n        return input.length() <= maxLength ? input : input[0..(maxLength - 1)]\n    }\n}',NULL,NULL),(2,683810818,'testRun','println \"hello groovy\"\nreturn \"rrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr\"',NULL,'2023-08-15 17:05:56');
/*!40000 ALTER TABLE `groovy_info` ENABLE KEYS */;
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
INSERT INTO `hello` VALUES (00000000001,'1111','11111','2222-01-01 00:00:00','2222-01-02 03:04:05'),(00000000002,'dfasd','dfasdf','2222-01-01 00:00:00','2222-01-01 00:00:00'),(00000000003,'cddf','dfasdf','2222-01-01 00:00:00','2222-01-01 00:00:00');
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
INSERT INTO `lot_model` VALUES (1,'温湿度计',1,'2022-11-07 19:27:37','2022-10-15 00:00:00'),(1485418498,'测试模型',1,'2022-11-07 16:28:36','2022-11-07 16:28:36');
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
INSERT INTO `lot_product` VALUES (1,1,'2022-10-15 00:00:00',1,'2022-10-15 00:00:00','2022-10-15 00:00:00',1),(789139459,1,'aaa',1,'2022-11-07 17:46:38','2022-11-07 17:46:38',1),(789139469,1,'aaa',1,'2022-11-07 22:11:52','2022-11-07 22:11:52',1),(789139470,1485418498,'aaa',1,'2022-11-07 22:12:02','2022-11-07 22:12:02',0),(789139471,1,'hhhhh',1,'2023-07-31 23:03:34','2022-11-07 22:12:47',1);
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
INSERT INTO `lot_product_value` VALUES (1,'ccc',789139467,'cc',NULL,NULL,'ccc','number',1,'2022-11-07 19:11:58','2022-11-07 19:11:58'),(2,'bbbddd',789139467,'bbbeeeee',NULL,NULL,'bb','number',2,'2022-11-07 19:11:59','2022-11-07 19:11:59'),(3,'ccc',789139468,'cc',NULL,NULL,'ccc','number',1,'2022-11-07 19:27:49','2022-11-07 19:27:49'),(4,'bbbddd',789139468,'bbbeeeee',NULL,NULL,'bb','number',2,'2022-11-07 19:27:49','2022-11-07 19:27:49'),(5,'ccc',789139469,'cc',NULL,NULL,'ccc','number',1,'2022-11-07 22:11:53','2022-11-07 22:11:53'),(6,'bbbddd',789139469,'bbbeeeee',NULL,NULL,'bb','number',2,'2022-11-07 22:11:53','2022-11-07 22:11:53'),(9,'ccc',789139471,'cc',NULL,NULL,'ccc','number',1,'2022-11-07 22:12:47','2022-11-07 22:12:47'),(10,'bbbddd',789139471,'bbbeeeee',NULL,NULL,'bb','number',2,'2022-11-07 22:12:47','2022-11-07 22:12:47');
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
INSERT INTO `user_info` VALUES (1,'teste','i@liufang.org.cn','15873164073','yjay9258264818','https://igogo-test.oss-cn-shenzhen.aliyuncs.com/user/avatar/2022_11_28_109573642959396864.jpeg',1,1,1,'2023-02-10 08:54:11','2022-11-28 09:23:42'),(6,'admin','i2@liufang.org.cn','15873164074','123456','https://igogo-test.oss-cn-shenzhen.aliyuncs.com/user/avatar/2022_11_28_109573713641807872.jpeg',1,1,1,'2022-11-28 10:25:15','2022-11-28 09:43:50');
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

-- Dump completed on 2023-08-29 13:20:19
