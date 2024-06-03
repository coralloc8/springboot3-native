/*
 Navicat Premium Data Transfer

 Source Server         : 192.168.29.66(开发库)
 Source Server Type    : MySQL
 Source Server Version : 80030 (8.0.30)
 Source Host           : 192.168.29.66:3306
 Source Schema         : testdb

 Target Server Type    : MySQL
 Target Server Version : 80030 (8.0.30)
 File Encoding         : 65001

 Date: 03/06/2024 09:46:38
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for event_info
-- ----------------------------
DROP TABLE IF EXISTS `event_info`;
CREATE TABLE `event_info`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `event_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '事件id',
  `event_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '事件名称',
  `event_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '事件关键字',
  `event_source` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '事件来源',
  `event_request` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '入参',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of event_info
-- ----------------------------
INSERT INTO `event_info` VALUES (1, '00001', '事件00001', 'KEY_00001', '医院点击', '{\r\n  \"eventId\": \"00009\",\r\n  \"project\": \"zhyx\",\r\n  \"funcCode\": \"func006\",\r\n  \"sendTime\": \"2024-04-08 17:13:00\",\r\n  \"receiveTime\": \"\",\r\n  \"responseData\": \"\",\r\n  \"errorCode\": \"\",\r\n  \"errorMsg\": \"\",\r\n  \"status\": 2\r\n}');
INSERT INTO `event_info` VALUES (2, '00002', '事件00002', 'KEY_00002', '测试点击', '{\r\n  \"eventId\": \"00009\",\r\n  \"project\": \"zhyx\",\r\n  \"funcCode\": \"func006\",\r\n  \"sendTime\": \"2024-04-08 17:13:00\",\r\n  \"receiveTime\": \"\",\r\n  \"responseData\": \"\",\r\n  \"errorCode\": \"\",\r\n  \"errorMsg\": \"\",\r\n  \"status\": 2\r\n}');
INSERT INTO `event_info` VALUES (3, '00003', '事件00003', 'KEY_00003', '百度', NULL);
INSERT INTO `event_info` VALUES (4, '00004', '事件00004', 'KEY_00004', '阿里云', NULL);
INSERT INTO `event_info` VALUES (5, '00005', '事件00005', 'KEY_00005', '下单', NULL);
INSERT INTO `event_info` VALUES (6, '00008', '事件00008', 'KEY_00008', '测试啊', NULL);
INSERT INTO `event_info` VALUES (7, '00009', '事件00009', 'KEY_00009', '百度测试', NULL);

-- ----------------------------
-- Table structure for event_log
-- ----------------------------
DROP TABLE IF EXISTS `event_log`;
CREATE TABLE `event_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `event_log_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '事件日志id',
  `event_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '事件id',
  `project` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '项目标识',
  `func_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '方法编码',
  `send_time` datetime NULL DEFAULT NULL COMMENT '发送时间',
  `receive_time` datetime NULL DEFAULT NULL COMMENT '接收时间',
  `response_data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '响应数据',
  `error_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '错误编码',
  `error_msg` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '错误信息',
  `status` int NULL DEFAULT 1 COMMENT '状态 1:处理中 2:处理失败 3:失败次数太多导致的拒绝 4:处理成功',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_event_id`(`event_id` ASC) USING BTREE,
  INDEX `idx_pro_func_sta`(`project` ASC, `func_code` ASC, `status` ASC) USING BTREE,
  INDEX `idx_latest_log_id`(`event_log_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2897 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '事件日志表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of event_log
-- ----------------------------
INSERT INTO `event_log` VALUES (2813, '10001', '00001', 'zhyx', 'func001', '2024-04-01 16:23:56', '2024-04-01 16:24:00', NULL, NULL, NULL, 1);
INSERT INTO `event_log` VALUES (2814, '10002', '00001', 'zhyx', 'func001', '2024-04-03 09:03:39', '2024-04-03 09:03:42', NULL, NULL, NULL, 2);
INSERT INTO `event_log` VALUES (2815, '10003', '00002', 'zhyx', 'func002', '2024-04-03 09:04:02', '2024-04-03 09:04:04', NULL, NULL, NULL, 4);
INSERT INTO `event_log` VALUES (2816, '10004', '00002', 'zhyx', 'func002', '2024-04-03 09:04:38', '2024-04-03 09:04:40', NULL, NULL, NULL, 3);
INSERT INTO `event_log` VALUES (2817, '10005', '00003', 'zhyx', 'func002', '2024-04-03 09:04:57', '2024-04-03 09:05:02', NULL, NULL, NULL, 2);
INSERT INTO `event_log` VALUES (2818, '10006', '00004', 'zhyx', 'func003', '2024-04-03 09:05:18', '2024-04-03 09:05:21', NULL, NULL, NULL, 1);
