# main_script.py

import pandas as pd
import redis
from sqlalchemy import create_engine
import json
from config import SQL_CONFIG  # Import the SQL configuration

# MySQL connection settings
mysql_engine = create_engine("mysql+pymysql://root:ACZ!w5!Xc9cc@am-8vbeu289781p6fuc085480.zhangbei.ads.aliyuncs.com:3306/tf_fms_test")

# Redis connection settings
redis_client = redis.StrictRedis(host='localhost', port=6379, decode_responses=True)

# 分页查询并写入 Redis
page_size = SQL_CONFIG['page_size']  # 从配置中获取每页的大小
page_number = 1  # 页码

while True:
    # 获取 SQL 查询语句
    sql_query = SQL_CONFIG['query']

    # 构建 SQL 查询
    offset = (page_number - 1) * page_size
    sql_query = sql_query + f" LIMIT {offset}, {page_size}"
    # 使用 Pandas 读取数据
    df = pd.read_sql_query(sql_query, mysql_engine)
    if df.empty:
        break  # 没有更多数据，退出循环
        # 将 DataFrame 转为 JSON 并写入 Redis
    for index, row in df.iterrows():
        row_dict = row.to_dict()
        row_dict = {key: (None if pd.isna(value) else value) for key, value in row_dict.items()}
        row_json = json.dumps(row_dict, ensure_ascii=False, default=lambda x: None)
        # print(row_json)
        # print(row_json)
        key = f"PAY:EXTERNAL:INCOMING:{row['accessChannelType']}:{row['businessType']}:{row['businessId']}"
        # print(index)

        redis_client.set(key, row_json)
    page_number += 1

