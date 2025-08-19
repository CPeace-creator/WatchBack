/*
 Navicat Premium Data Transfer

 Source Server         : 本地
 Source Server Type    : MySQL
 Source Server Version : 80042 (8.0.42)
 Source Host           : localhost:3306
 Source Schema         : movie

 Target Server Type    : MySQL
 Target Server Version : 80042 (8.0.42)
 File Encoding         : 65001

 Date: 19/08/2025 09:49:00
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '邮箱',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '加密后的密码',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `if_del` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除(0-未删除 1-已删除)',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_username`(`user_name` ASC) USING BTREE,
  UNIQUE INDEX `idx_email`(`email` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'testt', '949661474@qq.com', '$2a$10$LUpT076uwB.e0f83u9eNV.t3HYItAzDY7Snr9bl7goVj4e1iMw.GG', '2025-08-18 15:23:32', '2025-08-18 15:23:44', 0);
INSERT INTO `user` VALUES (6, 'testuser', 'test@example.com', '$2a$10$CYAUIIn6Cou48WDJdBkdtO/C3yghfAVtugdnmdiDokgutfVqYM/Lm', '2025-08-18 16:37:08', '2025-08-18 16:37:08', 0);

SET FOREIGN_KEY_CHECKS = 1;
