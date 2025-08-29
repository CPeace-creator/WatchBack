# -*- coding: utf-8 -*-
import sys
import requests
import json
import pymysql
from datetime import datetime
import time

# 设置标准输出编码为UTF-8
if sys.stdout.encoding != 'utf-8':
    import io
    sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')

# 数据库配置
DB_CONFIG = {
    'host': 'localhost',
    'user': 'root',
    'password': 'root',
    'database': 'movie',
    'charset': 'utf8mb4'
}

# API配置
headers = {
    "accept": "application/json",
    "Authorization": "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI5ZGQ0OWIyOTg0NzA2ZWFmMTVjOWY1ODQyOWFmOTg1YSIsIm5iZiI6MTc1NDgwNTQ2MS43MDQsInN1YiI6IjY4OTgzNGQ1YmU3MjAzZjIzNDJjMGUyYiIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.mLw02yUmw8_R5EgS5QbjDObfaLUNxZ90oRQiv53cbuQ"
}

def ensure_table_structure():
    """确保数据库表结构正确"""
    try:
        connection = get_db_connection()
        with connection.cursor() as cursor:
            # 检查upcoming表是否存在，如果不存在则创建
            cursor.execute("SHOW TABLES LIKE 'upcoming'")
            table_exists = cursor.fetchone()
            
            if not table_exists:
                # 表不存在，创建表
                cursor.execute("""
                    CREATE TABLE upcoming (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        adult BOOLEAN DEFAULT FALSE,
                        backdrop_path VARCHAR(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
                        genre_ids JSON DEFAULT NULL,
                        original_language VARCHAR(10) COLLATE utf8mb4_general_ci DEFAULT NULL,
                        original_title VARCHAR(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
                        overview TEXT COLLATE utf8mb4_general_ci,
                        popularity FLOAT DEFAULT NULL,
                        poster_path VARCHAR(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
                        release_date DATETIME DEFAULT NULL,
                        title VARCHAR(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
                        video BOOLEAN DEFAULT FALSE,
                        vote_average FLOAT DEFAULT NULL,
                        vote_count INT DEFAULT NULL,
                        minimum DATETIME DEFAULT NULL,
                        maximum DATETIME DEFAULT NULL
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci
                """)
                connection.commit()
            else:
                # 表存在，检查id字段是否为自增
                cursor.execute("""
                    SELECT COLUMN_NAME, IS_NULLABLE, COLUMN_DEFAULT, EXTRA 
                    FROM INFORMATION_SCHEMA.COLUMNS 
                    WHERE TABLE_SCHEMA = 'movie' AND TABLE_NAME = 'upcoming' AND COLUMN_NAME = 'id'
                """)
                id_info = cursor.fetchone()
                
                if id_info and 'auto_increment' not in str(id_info[3]).lower():
                    # id字段不是自增的，修改它
                    cursor.execute("ALTER TABLE upcoming MODIFY COLUMN id INT AUTO_INCREMENT PRIMARY KEY")
                    connection.commit()
                    
        connection.close()
    except Exception as e:
        raise Exception(f"确保表结构正确失败: {str(e)}")

def get_db_connection():
    """获取数据库连接"""
    try:
        connection = pymysql.connect(**DB_CONFIG)
        return connection
    except Exception as e:
        raise Exception(f"数据库连接失败: {str(e)}")

def check_upcoming_dates():
    """检查upcoming表中的日期范围"""
    try:
        connection = get_db_connection()
        with connection.cursor() as cursor:
            # 获取日期范围（查询所有记录中的minimum和maximum范围）
            cursor.execute("""
                SELECT MIN(minimum) as min_date, MAX(maximum) as max_date
                FROM upcoming 
                WHERE minimum IS NOT NULL AND maximum IS NOT NULL
            """)
            result = cursor.fetchone()
            connection.close()
            return result
    except Exception as e:
        raise Exception(f"检查upcoming日期失败: {str(e)}")

def clear_upcoming_data():
    """清空upcoming表数据"""
    try:
        connection = get_db_connection()
        with connection.cursor() as cursor:
            cursor.execute("DELETE FROM upcoming")
            connection.commit()
        connection.close()
    except Exception as e:
        raise Exception(f"清空upcoming数据失败: {str(e)}")

def insert_upcoming_record(upcoming_data):
    """插入upcoming记录并返回记录ID"""
    try:
        connection = get_db_connection()
        with connection.cursor() as cursor:
            # 插入upcoming基础信息记录（id字段是自增的，不需要手动指定）
            cursor.execute("""
                INSERT INTO upcoming (
                    adult, backdrop_path, genre_ids, original_language, original_title,
                    overview, popularity, poster_path, release_date, title, video,
                    vote_average, vote_count, minimum, maximum
                ) VALUES (
                    %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s
                )
            """, (
                upcoming_data.get('adult', False),
                upcoming_data.get('backdrop_path', ''),
                json.dumps(upcoming_data.get('genre_ids', [])),
                upcoming_data.get('original_language', ''),
                upcoming_data.get('original_title', ''),
                upcoming_data.get('overview', ''),
                upcoming_data.get('popularity', 0.0),
                upcoming_data.get('poster_path', ''),
                upcoming_data.get('release_date'),
                upcoming_data.get('title', ''),
                upcoming_data.get('video', False),
                upcoming_data.get('vote_average', 0.0),
                upcoming_data.get('vote_count', 0),
                upcoming_data.get('minimum'),
                upcoming_data.get('maximum')
            ))
            record_id = cursor.lastrowid
            connection.commit()
        connection.close()
        return record_id
    except Exception as e:
        raise Exception(f"插入upcoming记录失败: {str(e)}")

def insert_movie_if_not_exists(movie_data):
    """如果电影不存在则插入到movies表"""
    try:
        connection = get_db_connection()
        with connection.cursor() as cursor:
            # 检查电影是否已存在
            cursor.execute("SELECT tmdb_id FROM movies WHERE tmdb_id = %s", (movie_data['id'],))
            if cursor.fetchone():
                connection.close()
                return False  # 电影已存在
            
            # 插入新电影（id字段是自增的，不需要手动指定）
            cursor.execute("""
                INSERT INTO movies (
                    tmdb_id, title, original_title, overview, release_date,
                    vote_average, vote_count, popularity, poster_path, backdrop_path,
                    adult, video, original_language, genre_ids
                ) VALUES (
                    %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s
                )
            """, (
                movie_data['id'],
                movie_data['title'],
                movie_data['original_title'],
                movie_data['overview'],
                movie_data['release_date'] if movie_data['release_date'] else None,
                movie_data['vote_average'],
                movie_data['vote_count'],
                movie_data['popularity'],
                movie_data['poster_path'],
                movie_data['backdrop_path'],
                movie_data['adult'],
                movie_data['video'],
                movie_data['original_language'],
                json.dumps(movie_data['genre_ids'])
            ))
            connection.commit()
        connection.close()
        return True  # 新插入电影
    except Exception as e:
        raise Exception(f"插入电影数据失败: {str(e)}")

def fetch_upcoming_movies(page=1):
    """获取即将上映的电影数据"""
    try:
        url = f"https://api.themoviedb.org/3/movie/upcoming?language=zh-CN&page={page}"
        response = requests.get(url, headers=headers)
        response.encoding = 'utf-8'
        
        if response.status_code == 200:
            return response.json()
        else:
            raise Exception(f"API请求失败，状态码: {response.status_code}")
    except Exception as e:
        raise Exception(f"获取电影数据失败: {str(e)}")

def sync_upcoming_movies():
    """同步即将上映的电影数据"""
    try:
        # 0. 首先确保数据库表结构正确
        ensure_table_structure()
        
        # 1. 获取第一页数据
        first_page_data = fetch_upcoming_movies(1)
        current_dates = first_page_data['dates']
        total_pages = first_page_data['total_pages']
        total_results = first_page_data['total_results']
        
        # 2. 检查数据库中的日期记录
        existing_record = check_upcoming_dates()
        
        if existing_record and existing_record[0] and existing_record[1]:
            existing_min_str = existing_record[0].strftime('%Y-%m-%d') if existing_record[0] else None
            existing_max_str = existing_record[1].strftime('%Y-%m-%d') if existing_record[1] else None
            
            if (existing_min_str == current_dates['minimum'] and 
                existing_max_str == current_dates['maximum']):
                return {
                    "status": "no_update_needed",
                    "message": "日期范围未改变，无需更新",
                    "dates": current_dates
                }
        
        # 3. 清空原有数据
        clear_upcoming_data()
        
        # 4. 获取所有页面的电影数据并插入到upcoming表
        new_movies_count = 0
        total_movies_processed = 0
        
        for page in range(1, min(total_pages + 1, 501)):  # 限制最多500页，避免API限制
            try:
                page_data = fetch_upcoming_movies(page)
                movies = page_data['results']
                
                for movie in movies:
                    total_movies_processed += 1
                    # 为每部电影添加日期范围信息
                    movie_with_dates = movie.copy()
                    movie_with_dates['minimum'] = current_dates['minimum']
                    movie_with_dates['maximum'] = current_dates['maximum']
                    
                    # 插入到upcoming表
                    insert_upcoming_record(movie_with_dates)
                    new_movies_count += 1
                    
                    # 同时尝试插入到movies主表（如果不存在）
                    insert_movie_if_not_exists(movie)
                
                # 避免API限制，每页间隔0.1秒
                time.sleep(0.1)
                
            except Exception as e:
                # 如果某一页失败，记录错误但继续处理其他页
                print(f"处理第{page}页时出错: {str(e)}", file=sys.stderr)
                continue
        
        return {
            "status": "success",
            "message": "同步完成",
            "dates": current_dates,
            "total_pages": total_pages,
            "total_results": total_results,
            "total_movies_processed": total_movies_processed,
            "new_upcoming_movies_added": new_movies_count
        }
        
    except Exception as e:
        return {
            "status": "error",
            "message": str(e)
        }

def main():
    """主函数"""
    try:
        # 检查是否有参数传入
        if len(sys.argv) > 1:
            # 如果有参数，表示是分页查询，使用原来的逻辑
            page = 1
            try:
                page = int(sys.argv[1])
                if page < 1:
                    page = 1
            except ValueError:
                page = 1
            
            # 返回单页数据
            data = fetch_upcoming_movies(page)
            print(json.dumps(data, ensure_ascii=False))
        else:
            # 如果没有参数，执行同步操作
            result = sync_upcoming_movies()
            print(json.dumps(result, ensure_ascii=False))
            
    except Exception as e:
        error_response = {
            "status": "error",
            "message": str(e),
            "type": type(e).__name__
        }
        print(json.dumps(error_response, ensure_ascii=False))

if __name__ == "__main__":
    main()