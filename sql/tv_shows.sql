/*
 Navicat Premium Data Transfer

 Source Server         : 本地
 Source Server Type    : MySQL
 Source Server Version : 80040 (8.0.40)
 Source Host           : 127.0.0.1:3306
 Source Schema         : movie

 Target Server Type    : MySQL
 Target Server Version : 80040 (8.0.40)
 File Encoding         : 65001

 Date: 25/08/2025 21:38:12
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tv_shows
-- ----------------------------
DROP TABLE IF EXISTS `tv_shows`;
CREATE TABLE `tv_shows` (
  `show_id` int NOT NULL AUTO_INCREMENT,
  `tmdb_id` int NOT NULL,
  `name` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `original_name` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `overview` text COLLATE utf8mb4_general_ci,
  `first_air_date` date DEFAULT NULL,
  `origin_country` json DEFAULT NULL,
  `original_language` varchar(10) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `genre_ids` json DEFAULT NULL,
  `popularity` float DEFAULT NULL,
  `vote_average` float DEFAULT NULL,
  `vote_count` int DEFAULT NULL,
  `poster_path` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `backdrop_path` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `source_api` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`show_id`),
  UNIQUE KEY `tmdb_id` (`tmdb_id`),
  KEY `idx_tv_tmdb_id` (`tmdb_id`),
  KEY `idx_tv_name` (`name`),
  KEY `idx_first_air_date` (`first_air_date`),
  KEY `idx_tv_vote_average` (`vote_average`),
  KEY `idx_tv_popularity` (`popularity`),
  KEY `idx_tv_source_api` (`source_api`)
) ENGINE=InnoDB AUTO_INCREMENT=105427 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

SET FOREIGN_KEY_CHECKS = 1;
