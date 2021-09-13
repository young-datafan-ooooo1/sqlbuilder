/*
 Navicat Premium Data Transfer

 Source Server         : 山景_192.168.10.211
 Source Server Type    : MySQL
 Source Server Version : 50731
 Source Host           : 192.168.10.211:13306
 Source Schema         : testsql

 Target Server Type    : MySQL
 Target Server Version : 50731
 File Encoding         : 65001

 Date: 08/09/2021 15:58:58
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for test_1031
-- ----------------------------
DROP TABLE IF EXISTS `test_1031`;
CREATE TABLE `test_1031` (
  `id` int(11) DEFAULT NULL,
  `sex` char(1) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `salary` double(10,2) DEFAULT NULL,
  `create_time` time DEFAULT NULL,
  `create_date` date DEFAULT NULL,
  `create_datetime` datetime DEFAULT NULL,
  `create_timestamp` timestamp NULL DEFAULT NULL,
  `field_long` mediumtext,
  `field_numeric` decimal(10,0) DEFAULT NULL,
  `field_decimal` decimal(10,2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of test_1031
-- ----------------------------
BEGIN;
INSERT INTO `test_1031` VALUES (1, '1', 'zhangsan', '张三', 5000.00, '15:56:45', '2021-09-08', '2021-09-08 15:56:52', '2021-09-08 15:56:56', '100000', 22, 50000.00);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
