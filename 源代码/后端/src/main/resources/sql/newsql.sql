/*
 Navicat Premium Dump SQL

 Source Server         : mysql
 Source Server Type    : MySQL
 Source Server Version : 80040 (8.0.40)
 Source Host           : localhost:3306
 Source Schema         : aicamera

 Target Server Type    : MySQL
 Target Server Version : 80040 (8.0.40)
 File Encoding         : 65001

 Date: 15/03/2025 11:43:50
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for caption_preferences
-- ----------------------------
DROP TABLE IF EXISTS `caption_preferences`;
CREATE TABLE `caption_preferences`  (
  `preference_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `user_email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `preferred_style` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `frequent_moods` json NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  PRIMARY KEY (`preference_id`) USING BTREE,
  INDEX `fk_user_email`(`user_email` ASC) USING BTREE,
  CONSTRAINT `fk_user_email` FOREIGN KEY (`user_email`) REFERENCES `users` (`email`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of caption_preferences
-- ----------------------------

-- ----------------------------
-- Table structure for photo_progress
-- ----------------------------
DROP TABLE IF EXISTS `photo_progress`;
CREATE TABLE `photo_progress`  (
  `progress_id` bigint NOT NULL AUTO_INCREMENT COMMENT '进步记录唯一标识',
  `user_email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户邮箱（外键，关联users表）',
  `grade` tinyint NOT NULL COMMENT '照片评分（0-100）',
  `improvement` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '改进建议',
  `created_at` datetime NOT NULL COMMENT '记录创建时间',
  PRIMARY KEY (`progress_id`) USING BTREE,
  INDEX `fk_photo_progress_email`(`user_email` ASC) USING BTREE,
  CONSTRAINT `fk_photo_progress_email` FOREIGN KEY (`user_email`) REFERENCES `users` (`email`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户拍照进步记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of photo_progress
-- ----------------------------

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `gender` tinyint(1) NULL DEFAULT NULL,
  `age` int NULL DEFAULT NULL,
  `preference` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `avatarUrl` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`email`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES ('example@email.com', '张三', '123456', 0, 18, '计算机', NULL);
INSERT INTO `users` VALUES ('example@email1.com', '测试用例', '$2a$10$CCf7rnlaCVINQaQDPdWX2u6u7GybUvG4v71IxAlY14K2xnw9b8W8C', NULL, NULL, NULL, NULL);
INSERT INTO `users` VALUES ('lisi@email.com', '李四', '$2a$10$a5naJcCvfhqWffnY8.a/OerCl2W1XEFuwgdYSVr0TFIrH/QU/jrwC', 1, 18, '唱跳rap篮球', NULL);
INSERT INTO `users` VALUES ('wangwu@163.com', '王五', '$2a$10$2ZZDC/5HTwrcEKEglIoAUeWRxSzG1RwXV2JAlmB9cYn6ClLzR.dqy', 1, 18, '动漫，旅游', 'http://192.168.117.129:9000/aicamera/avatar8749478045228092227.jpg');

SET FOREIGN_KEY_CHECKS = 1;
