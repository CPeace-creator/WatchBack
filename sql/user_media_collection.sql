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

 Date: 26/08/2025 21:37:59
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for user_media_collection
-- ----------------------------
DROP TABLE IF EXISTS `user_media_collection`;
CREATE TABLE `user_media_collection`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` int NOT NULL COMMENT '用户ID',
  `media_type` tinyint NOT NULL COMMENT '媒体类型(1-电影, 2-电视剧)',
  `media_id` int NOT NULL COMMENT '媒体ID(对应movies.movie_id或tv_shows.show_id)',
  `tmdb_id` int NOT NULL COMMENT 'TMDB ID',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标题',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态(1-已收藏, 2-已观看, 3-想看)',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_user_media_type`(`user_id` ASC, `media_type` ASC, `media_id` ASC) USING BTREE,
  INDEX `idx_user_status`(`user_id` ASC, `status` ASC) USING BTREE,
  INDEX `idx_media_type`(`media_type` ASC, `media_id` ASC) USING BTREE,
  INDEX `idx_tmdb_id`(`tmdb_id` ASC) USING BTREE,
  CONSTRAINT `fk_collection_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户媒体收藏表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;