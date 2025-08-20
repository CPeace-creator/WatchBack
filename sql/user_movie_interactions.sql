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

 Date: 20/08/2025 18:13:50
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for user_movie_interactions
-- ----------------------------
DROP TABLE IF EXISTS `user_movie_interactions`;
CREATE TABLE `user_movie_interactions`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` int NOT NULL COMMENT '用户ID',
  `movie_id` int NOT NULL COMMENT '电影ID',
  `rating` decimal(3, 1) NULL DEFAULT NULL COMMENT '用户评分(0-10分)',
  `review` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '用户评论',
  `watch_date` date NULL DEFAULT NULL COMMENT '观看日期',
  `is_wishlist` tinyint(1) NULL DEFAULT 0 COMMENT '是否在愿望清单',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_user_movie`(`user_id` ASC, `movie_id` ASC) USING BTREE,
  INDEX `idx_movie_rating`(`movie_id` ASC, `rating` ASC) USING BTREE,
  CONSTRAINT `fk_interaction_movie` FOREIGN KEY (`movie_id`) REFERENCES `movies` (`movie_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_interaction_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户电影互动表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_movie_interactions
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
