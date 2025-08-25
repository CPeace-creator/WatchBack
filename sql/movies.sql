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

 Date: 25/08/2025 21:37:59
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for movies
-- ----------------------------
DROP TABLE IF EXISTS `movies`;
CREATE TABLE `movies` (
  `movie_id` int NOT NULL AUTO_INCREMENT,
  `tmdb_id` int NOT NULL,
  `title` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `original_title` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `overview` text COLLATE utf8mb4_general_ci,
  `release_date` date DEFAULT NULL,
  `vote_average` float DEFAULT NULL,
  `vote_count` int DEFAULT NULL,
  `popularity` float DEFAULT NULL,
  `poster_path` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `backdrop_path` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `adult` tinyint(1) DEFAULT NULL,
  `video` tinyint(1) DEFAULT NULL,
  `original_language` varchar(10) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `genre_ids` json DEFAULT NULL,
  PRIMARY KEY (`movie_id`),
  UNIQUE KEY `tmdb_id` (`tmdb_id`),
  KEY `idx_tmdb_id` (`tmdb_id`),
  KEY `idx_title` (`title`),
  KEY `idx_release_date` (`release_date`),
  KEY `idx_vote_average` (`vote_average`),
  KEY `idx_popularity` (`popularity`)
) ENGINE=InnoDB AUTO_INCREMENT=170944 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

SET FOREIGN_KEY_CHECKS = 1;
