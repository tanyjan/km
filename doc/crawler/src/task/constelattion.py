# -*- coding: utf-8 -*-
import config
import sys
import time
from lxml import etree
from mysql import MySql
from http import HttpClient
import re
import MySQLdb

#{"host": hosts[1], "url": "", "xpath": {"today_fortune": "", "tomorrow_fortune": "", "week_fortune":"", "month_fortune": "", "year_fortune": "", "love":"", "health":"", "career":"", "wealth":"", "comfort":"", "lucky_number":"", "lucky_color":""}}

constellation_keys = ["aries", "tauro", "geminis", "cancer", "leo", "virgo", "libra", "escorpio", "sagitario", "capricornio", "acuario", "piscis"];
constellation_map = {"aries": 1, "tauro": 2, "geminis": 3, "cancer": 4, "leo": 5, "virgo": 6, "libra": 7, "escorpio": 8, "sagitario": 9, "capricornio": 10, "acuario": 11, "piscis": 12};
fortune_type_map = {1: "today_fortune", 2: "tomorrow_fortune", 3: "week_fortune", 4: "month_fortune"}
resrces = [
    {"keyword":"today_fortune", "comment":"今日运势&幸运数字&颜色", "charset":"utf-8", "host": "http://www.loshoroscopos.es", "url": "/horoscopo-diario/%s.php", "xpath": {"today_fortune": "//div[@class='well']", "lucky_number":"//div[@class='well']/div/div[1]/span", "lucky_color":"//div[@class='well']/div/div[2]/span"}},
    {"keyword":"today_fortune", "comment":"今日运势&幸运数字", "charset":"utf-8", "host": "http://www.elhoroscopodehoy.es", "url": "/horoscopo-hoy/%s.php", "xpath": {"today_fortune": "//div[@class='prediccion']", "lucky_number":"//div[@class='lucky-numbers']/span"}},
 
    {"keyword":"love&career", "comment":"爱情&事业", "charset":"iso8859-1", "host": "http://www.mi-horoscopo-del-dia.com", "url": "/horoscopos/manana/%s.htm", "xpath": {"love":"//tr[2]/td[3]/p", "career":"//tr[3]/td[3]/p"}},
    {"keyword":"love&career", "comment":"爱情&事业", "charset":"utf-8", "host": "http://www.horoscopodehoy.com", "url": "/horoscopo-de-hoy-%s", "xpath": {"love":"//div[@class='text-box']/div/div#.*?<strong>Amor.</strong>(.*?)<strong>", "career":"//div[@class='text-box']/div/div#.*?<strong>Dinero y trabajo.</strong>(.*?)<strong>"}},

    {"keyword":"tomorrow_fortune", "comment":"明日运势", "charset":"utf-8", "host": "http://www.loshoroscopos.es", "url": "/horoscopo-manana/%s.php", "xpath": {"tomorrow_fortune": "//div[@class='well']"}},
    {"keyword":"tomorrow_fortune", "comment":"明日运势", "charset":"utf-8", "host": "http://www.elhoroscopodehoy.es", "url": "/horoscopo-manana/%s.php", "xpath": {"tomorrow_fortune": "//span[@class='prediccion']"}},
 
    {"keyword":"week_fortune", "comment":"周运势", "charset":"utf-8", "host": "http://www.loshoroscopos.es", "url": "/horoscopo-semanal/%s.php", "xpath": {"week_fortune": "//div[@class='well']"}},
    {"keyword":"week_fortune", "comment":"周运势", "charset":"utf-8", "host": "http://www.horoscopodehoy.com", "url": "/horoscopo-semanal-%s", "xpath": {"week_fortune": "//div[@class='text-box']/div/div[1]"}},
 
    {"keyword":"month_fortune", "comment":"月运势", "charset":"utf-8", "host": "http://www.loshoroscopos.es", "url": "/horoscopo-mensual/%s.php", "xpath": {"month_fortune": "//div[@class='well']"}},
    {"keyword":"month_fortune", "comment":"月运势", "charset":"utf-8", "host": "http://www.horoscopodehoy.com", "url": "/horoscopo-mensual-%s", "xpath": {"week_fortune": "//div[@class='text-box']/div/div[1]"}}
]

result_map = {"aries": {}, "tauro": {}, "geminis": {}, "cancer":{}, "leo":{}, "virgo":{}, "libra":{}, "escorpio":{}, "sagitario":{}, "capricornio":{}, "acuario":{}, "piscis":{}};

def fetch_by_text(name, key, eles):
    res_map = result_map[name];
    for tag in eles:
        text_tag = tag.xpath("text()")
        lenth = len(text_tag)
        if lenth==0:
            res_map[key] = ""
            continue
        for text in text_tag:
            unicodetext = text.strip()
            if not unicodetext:
                continue;
            res_map[key] = res_map.get(key, "") + unicodetext

def fetch_by_regex(name, key, eles, vals):
    res_map = result_map[name];
    content = ""
    for tag in eles:
        content = content + etree.tostring(tag)
    content = content.replace("\r", "").replace("\n", "")
    text = ""
    i = -1
    for reg in vals:
        i = i+1
        if i==0:
            continue

        match_res = re.match(reg, content, re.M|re.I)
        if match_res:
            text = text + match_res.group(1)
        else:
            text = text + ""
    unicodetext = text.strip()
    res_map[key] = res_map.get(key, "") + unicodetext


def fetch_attrs_by_rules(name, content, rules):
    logging.info("get %s attrs from content using rules %s", name, rules)
    page = etree.HTML(content)
    res_map = result_map[name];
    for key,val in rules.items():
        if res_map.has_key(key):
            continue
        vals = val.split("#")
        if len(vals) > 1:
            eles = page.xpath(vals[0]);
            if not eles:
                continue
            fetch_by_regex(name, key, eles, vals)
        else:
            eles = page.xpath(val);
            if not eles:
                continue
            fetch_by_text(name, key, eles)
#         for tag in eles:
#             text_tag = tag.xpath("text()")
#             lenth = len(text_tag)
#             if lenth==0:
#                 res_map[key] = ""
#                 continue
#             for text in text_tag:
#                 unicodetext = text.strip()
#                 if not unicodetext:
#                     continue;
#                 if not res_map.has_key(key):
#                     res_map[key] = "<p>" + unicodetext + "</p>"
#                     continue
#                 res_map[key] = res_map[key] + "<p>" + unicodetext + "</p>"
    logging.info("get {0} attrs from content using rules {1}: attrs{2}".format(name, rules, res_map))
    return res_map

def fetch_detail_content(name, charset, host, url, rules):
    content = http.http_get(host+url)
    if charset:
        content = content.decode(charset)

    if not content:
        logging.info("get constellation %s http request exception from %s", name, host + url)
        return
    logging.info("get constellation %s successful from %s", name, host + url)
    result = fetch_attrs_by_rules(name, content, rules)
    logging.info("get constellation %s failure from %s: %s", name, host + url, result)

#开始抓取指定星座任务
def start_task(name):
    start_time = get_time_in_millions()
    logging.info("fetch constellation {0} at {1}".format(name, start_time))
    fetch_time = time.strftime("%Y-%m-%d %H:%M:%S", time.localtime())
    fortune_date = time.strftime("%Y-%m-%d", time.localtime())
    for resrce_map in resrces:
        url = resrce_map.get("url", "") % (name)
        res_map = result_map[name]
#         print name, result_map, resrce_map
        keyword = resrce_map.get("keyword", "");

        if keyword:
            keywords = keyword.split("&")
        
        not_exists_flag = True

        for k in keywords:
            if not k.strip():
                continue
            if not res_map.has_key(k):
                not_exists_flag &= False
            continue

        if not_exists_flag:
            logging.info("droping constellation {0} - {1} use {2}".format(name, keyword, resrce_map.get("host", "") + url))
            continue

        logging.info("fetching constellation {0} - {1} use {2}".format(name, keyword, resrce_map.get("host", "") + url))
        fetch_detail_content(name, resrce_map.get("charset", ""), resrce_map.get("host", ""), url, resrce_map.get("xpath", ""))

    result = result_map[name]
    for num in range(1, len(fortune_type_map)+1):
        create_time = time.strftime("%Y-%m-%d %H:%M:%S", time.localtime())
        fortune_type = fortune_type_map[num]
        content = result.get(fortune_type, "")
        if num == 1:
            lucky_number = result.get("lucky_number", "")
            lucky_color = result.get("lucky_color", "")
            love = result.get("love", "")
            wealth = result.get("wealth", "")
            health = result.get("health", "")
            career = result.get("career", "")
        else:
            lucky_number = ""
            lucky_color = ""
            love = ""
            wealth = ""
            health = ""
            career = ""
        
        p_data = {"constellation_id": constellation_map[name],
                  "fortune_type_id": num, 
                  "fortune_date": fortune_date, 
                  "content": content,
                  "lucky_number": lucky_number,
                  "lucky_color": lucky_color,
                  "love": love,
                  "wealth": wealth,
                  "health": health,
                  "career": career,
                "create_time": create_time,
                  "fetch_time": fetch_time}
        insert_data(p_data)
    end_time = get_time_in_millions()
    logging.info("fetch constellation finished %s, cost time: %s", name, end_time-start_time)

#开始抓取所有星座任务
def start():
    logging.info("constellation fetch started")
    for name in constellation_keys:
        start_task(name)
    print result_map


def get_time_in_millions():
    return time.time()

def insert_data(p_data):
    try:
#         print fortune_type_map.get(p_data.get("fortune_type_id")), p_data
        count_sql = "select count(0) from t_constellation_content t where t.fortune_date='{0}' and t.constellation_id={1} and t.fortune_type_id={2}".format(p_data.get("fortune_date"), p_data.get("constellation_id"), p_data.get("fortune_type_id"))
        counts = mysql_db_mta.query(count_sql)
        if counts == 0:
            mysql_db_mta.insert("t_constellation_content", p_data)
        else:
            deleteSql = "delete from t_constellation_content where fortune_date='{0}' and constellation_id={1} and fortune_type_id={2}".format(p_data.get("fortune_date"), p_data.get("constellation_id"), p_data.get("fortune_type_id"))
            mysql_db_mta.delete(deleteSql)
            mysql_db_mta.insert("t_constellation_content", p_data)
    except MySQLdb.IntegrityError:
        logging.info("insert data to mysql exception: %s", p_data)
    except:
        logging.info("insert data to mysql exception: %s", p_data)


def init_paremeter():
    global mysql_db_mta, http, logging
    http = HttpClient("logger")
    mysql_db_mta = MySql('master_mysql', 'logger')
    logging = config.init_logger("constellation_crawler", False)



init_paremeter()
if len(sys.argv)>1:
    start_task(sys.argv[1])
else: 
    start()
