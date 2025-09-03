#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import sys
import requests
from bs4 import BeautifulSoup
import json
import time
import urllib.parse
import re  # 添加正则表达式模块导入

# 设置标准输出编码为UTF-8
sys.stdout = open(sys.stdout.fileno(), mode='w', encoding='utf-8', buffering=1)

class MovieScraper:
    """电影/电视剧搜索工具类"""
    
    def __init__(self):
        # 设置请求头，模拟浏览器访问
        self.headers = {
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36',
            'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9',
            'Accept-Language': 'zh-CN,zh;q=0.9,en;q=0.8',
            'Referer': 'https://movie.douban.com/',
            'Cache-Control': 'max-age=0',
            'Cookie': 'bid=placeholder_for_cookies;',  # 添加cookie支持
            'Upgrade-Insecure-Requests': '1',
            'TE': 'Trailers'
        }
        
    def get_movie_details(self, movie_url):
        """
        从电影/电视剧的详细页面获取更多信息，包括剧情简介和精确的发布日期
        :param movie_url: 电影/电视剧的详细页面URL
        :return: 包含剧情简介和发布日期的字典
        """
        try:
            # 发送请求到详细页面
            response = requests.get(movie_url, headers=self.headers, timeout=10)
            response.encoding = 'utf-8'
            
            if response.status_code != 200:
                # 静默处理失败，返回空字典
                return {}
            
            # 解析HTML
            soup = BeautifulSoup(response.text, 'html.parser')
            
            # 获取剧情简介
            summary = ""
            summary_elem = soup.find('span', property="v:summary")
            if summary_elem:
                summary = summary_elem.text.strip()
            
            # 获取精确的发布日期
            release_date = None
            date_elem = soup.find('span', property="v:initialReleaseDate")
            if date_elem:
                release_date = date_elem.text.strip()
            
            return {
                'summary': summary,
                'release_date': release_date
            }
        except Exception as e:
            # 静默处理异常，返回空字典
            return {}
            
    def search_by_title_and_type(self, title, media_type):
        """
        根据标题和类型搜索电影或电视剧
        :param title: 搜索的标题
        :param media_type: 1表示电视剧，2表示电影
        :return: 搜索结果
        """
        try:
            # 根据media_type选择搜索的URL和类型
            if media_type == 1:
                search_type = 'tv'
                base_url = 'https://movie.douban.com/subject_search?search_text={}&cat=1002'
                result_type = '电视剧'
            else:
                search_type = 'movie'
                base_url = 'https://movie.douban.com/subject_search?search_text={}&cat=1000'
                result_type = '电影'
            
            # 对标题进行URL编码
            encoded_title = urllib.parse.quote(title)
            search_url = base_url.format(encoded_title)
            
            # 发送请求
            response = requests.get(search_url, headers=self.headers, timeout=10)
            response.encoding = 'utf-8'
            
            if response.status_code != 200:
                raise Exception(f"请求失败，状态码: {response.status_code}")
            
            # 静默执行，不打印调试信息
            

            
            # 解析HTML
            soup = BeautifulSoup(response.text, 'html.parser')
            
            # 提取搜索结果
            results = []
            
            # 1. 尝试从JavaScript变量window.__DATA__中提取数据
            script_tags = soup.find_all('script')
            data_found = False
            
            for script in script_tags:
                script_text = script.string
                if script_text and 'window.__DATA__' in script_text:
                    # 提取JSON数据
                    try:
                        # 提取window.__DATA__ = 后面的JSON部分
                        data_str = script_text.split('window.__DATA__ = ')[1].split(';')[0].strip()
                        # 解析JSON数据
                        data = json.loads(data_str)
                        
                        # 处理搜索结果
                        if 'items' in data:
                            # 在search_by_title_and_type方法中，当从JSON数据提取信息时
                            for idx, item in enumerate(data['items'][:10]):  # 最多获取前10个结果
                                try:
                                    # 提取所需信息
                                    main_title = item.get('title', '无标题')
                                    link = item.get('url', '')
                                    rating_value = item.get('rating', {}).get('value', 0.0)
                                    rating_count = item.get('rating', {}).get('count', 0)
                                    rating = rating_value
                                    
                                    # 提取封面图片URL
                                    cover_image = item.get('cover_url', '')
                                    
                                    # 获取详细信息（剧情简介和精确发布日期）
                                    details = self.get_movie_details(link)
                                    description = details.get('summary', '')
                                    release_date = details.get('release_date', None)
                                    
                                    # 构建结果字典
                                    result_info = {
                                        'rank': idx + 1,
                                        'title': main_title,
                                        'rating': rating,
                                        'description': description,
                                        'link': link,
                                        'cover_image': cover_image,
                                        'type': result_type,
                                        'source': f'douban_{search_type}_search',
                                        'scraped_at': time.strftime('%Y-%m-%d %H:%M:%S')
                                    }
                                    
                                    # 如果找到精确发布日期，添加到结果字典
                                    if release_date:
                                        result_info['release_date'] = release_date
                                    
                                    results.append(result_info)
                                except Exception as e:
                                    # 静默处理错误，继续处理下一项
                                    continue
                        data_found = True
                        break
                    except Exception as e:
                        # 静默处理错误，继续尝试其他script标签
                        continue
            
            # 2. 如果没有找到JSON数据，回退到HTML解析
            if not data_found:
                # 静默尝试HTML解析
                # 尝试多种可能的CSS选择器来查找搜索结果
                result_items = soup.find_all('div', class_='info')
                
                # 如果没有找到结果，尝试另一种可能的选择器
                if not result_items:
                    result_items = soup.find_all('div', class_='movie-content')
                
                # 尝试其他可能的选择器
                if not result_items:
                    result_items = soup.find_all('div', class_='sc-bZQynM')
                
                # 尝试最通用的选择器
                if not result_items:
                    result_items = soup.find_all('div', class_=lambda x: x and ('info' in x or 'content' in x or 'result' in x))
                
                for idx, item in enumerate(result_items[:10]):  # 最多获取前10个结果
                    # 提取标题
                    title_elem = item.find('a')
                    if not title_elem:
                        continue
                    
                    main_title = title_elem.find('span', class_='title-text').text.strip() if title_elem.find('span', class_='title-text') else title_elem.text.strip()
                    
                    # 提取链接
                    link = title_elem['href'] if title_elem.has_attr('href') else ''
                    
                    # 提取评分
                    rating_elem = item.find('span', class_='rating_nums')
                    rating = float(rating_elem.text.strip()) if rating_elem and rating_elem.text.strip() else 0.0
                    
                    # 提取简介
                    desc_elem = item.find('div', class_='detail')
                    description = desc_elem.text.strip() if desc_elem else ''
                    
                    # 构建结果字典
                    result_info = {
                        'rank': idx + 1,
                        'title': main_title,
                        'rating': rating,
                        'description': description,
                        'link': link,
                        'type': result_type,
                        'source': f'douban_{search_type}_search',
                        'scraped_at': time.strftime('%Y-%m-%d %H:%M:%S')
                    }
                    
                    results.append(result_info)
            
            # 返回搜索结果
            return {
                'status': 'success',
                'total_results': len(results),
                'results': results,
                'title': title,
                'media_type': media_type,
                'message': f'成功搜索到{len(results)}个{result_type}结果'
            }
            
        except Exception as e:
            return {
                'status': 'error',
                'message': str(e),
                'title': title,
                'media_type': media_type
            }
    


    def run_test(self):
        """运行搜索功能测试并输出结果"""
        print("开始测试搜索功能...\n")
        
        # 测试电影搜索
        print("1. 测试电影搜索功能:")
        movie_search_result = self.search_by_title_and_type("流浪地球", 2)
        print(json.dumps(movie_search_result, ensure_ascii=False, indent=2))
        print("\n" + "="*60 + "\n")
        
        # 测试电视剧搜索
        print("2. 测试电视剧搜索功能:")
        tv_search_result = self.search_by_title_and_type("三体", 1)
        print(json.dumps(tv_search_result, ensure_ascii=False, indent=2))
        
        # 休眠一段时间，避免请求过快
        time.sleep(2)
        
        print("\n搜索功能测试完成！")

    def run_search(self, title, media_type):
        """运行搜索功能并输出结果"""
        result = self.search_by_title_and_type(title, media_type)
        print(json.dumps(result, ensure_ascii=False, indent=2))

if __name__ == "__main__":
    # 检查是否安装了必要的依赖
    try:
        import requests
        from bs4 import BeautifulSoup
    except ImportError:
        print("错误：请先安装必要的依赖库！")
        print("安装命令：pip install requests beautifulsoup4")
        sys.exit(1)
    
    # 创建搜索工具实例
    scraper = MovieScraper()
    
    # 处理命令行参数
    if len(sys.argv) >= 3:
        # 有命令行参数，执行搜索功能
        try:
            title = sys.argv[1]
            media_type = int(sys.argv[2])
            
            # 验证media_type参数
            if media_type not in [1, 2]:
                print("错误：mediaType参数无效，应为1（电视剧）或2（电影）")
                sys.exit(1)
            
            scraper.run_search(title, media_type)
        except ValueError:
            print("错误：mediaType参数必须是数字（1或2）")
            sys.exit(1)
    else:
        # 没有命令行参数，运行搜索功能测试
        scraper.run_test()