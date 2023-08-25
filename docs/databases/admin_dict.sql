/*
 Navicat Premium Data Transfer

 Source Server         : 本机_root_123456
 Source Server Type    : MySQL
 Source Server Version : 80033
 Source Host           : localhost:3306
 Source Schema         : soho

 Target Server Type    : MySQL
 Target Server Version : 80033
 File Encoding         : 65001

 Date: 22/08/2023 22:54:53
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admin_dict
-- ----------------------------
DROP TABLE IF EXISTS `admin_dict`;
CREATE TABLE `admin_dict`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `parent_id` bigint(0) NULL DEFAULT NULL COMMENT '父主键',
  `code` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '字典码',
  `dict_key` int(0) NULL DEFAULT NULL COMMENT '字典值',
  `dict_value` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '字典名称',
  `sort` int(0) NULL DEFAULT NULL COMMENT '排序',
  `remark` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '备注',
  `deleted` int(0) NULL DEFAULT 0 COMMENT '删除标志 0:正常 1:已删除',
  `updated_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `created_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 63 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '字典表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of admin_dict
-- ----------------------------
INSERT INTO `admin_dict` VALUES (16, 0, 'sex', -1, '性别', 1, '性别字典', 0, '2023-08-21 23:16:43', '2023-08-21 23:16:43');
INSERT INTO `admin_dict` VALUES (17, 16, 'sex', 0, '女', 1, '性别字典', 0, '2023-08-21 23:16:43', '2023-08-21 23:16:43');
INSERT INTO `admin_dict` VALUES (18, 16, 'sex', 1, '男', 1, '性别字典', 0, '2023-08-21 23:16:43', '2023-08-21 23:16:43');
INSERT INTO `admin_dict` VALUES (63, 0, 'enable', -1, '禁启用', 1, '禁启用', 0, '2023-08-21 23:21:55', '2023-08-21 23:21:55');
INSERT INTO `admin_dict` VALUES (64, 63, 'enable', 0, '启用', 1, '启用', 0, '2023-08-21 23:22:31', '2023-08-21 23:22:31');
INSERT INTO `admin_dict` VALUES (65, 63, 'enable', 1, '禁用', 2, '禁用', 0, '2023-08-21 23:22:48', '2023-08-21 23:22:48');
INSERT INTO `admin_dict` VALUES (66, 0, 'deleted', -1, '删除', 1, '是否删除', 0, '2023-08-21 23:23:18', '2023-08-21 23:23:18');
INSERT INTO `admin_dict` VALUES (67, 66, 'deleted', 0, '正常', 1, '正常', 0, '2023-08-21 23:23:36', '2023-08-21 23:23:36');
INSERT INTO `admin_dict` VALUES (68, 66, 'deleted', 1, '已删除', 2, '已删除', 0, '2023-08-21 23:27:44', '2023-08-21 23:23:55');

SET FOREIGN_KEY_CHECKS = 1;
