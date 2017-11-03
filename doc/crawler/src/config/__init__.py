import config_helper
import os
import logging
import sys
from logging import config
import random


def get(section, name):
    return config_helper.get(section, name)


def get_mysql_parameter(db_name):
    host     = config_helper.get(db_name, 'host')
    port     = config_helper.get(db_name, 'port')
    username = config_helper.get(db_name, 'username')
    password = config_helper.get(db_name, 'password')
    database = config_helper.get(db_name, 'database')
    charset  = config_helper.get(db_name, 'charset')
    return host, int(port), username, password, database, charset


def get_rabbit_parameter(section="rabbit"):
    host = config_helper.get(section, "host")
    port = config_helper.get(section, "port")
    user = config_helper.get(section, "user")
    password = config_helper.get(section, "password")
    exchange = config_helper.get(section, "exchange")
    routeKey = config_helper.get(section, "routeKey")

    return host, port, user, password, exchange, routeKey


def get_redis_parameter():
    host = config_helper.get('redis', 'host')
    port = int(config_helper.get('redis', 'port'))
    db = int(config_helper.get('redis', 'database'))
    return host, port, db


def get_elasticsearch_parameter():
    return config_helper.get("elasticsearch","hosts").split(",")


def get_mongodb_parameter():
    url = config_helper.get('mongodb', 'url')
    database = config_helper.get('mongodb', 'database')
    collection = config_helper.get('mongodb', 'collection')
    return url, database, collection

def get_image_cloud__parameter():
    app = config_helper.get('image_cloud', 'app')
    uuid = config_helper.get('image_cloud', 'uuid')
    url = config_helper.get('image_cloud', 'url')
    key = config_helper.get('image_cloud', 'key')
    return app,uuid,url, key

def get_proxy_host():
    return config_helper.get('proxy','host')


def init_logger(logger_name,multi_process=True):
    current_path = os.path.split(os.path.realpath(__file__))[0]

    if 'win' in sys.platform:
        template_name = current_path + '/logger_debug_template.ini'
    else:
        template_name = current_path + '/logger_template.ini'

    tmp = open(template_name,'r')
    new_logger_config = tmp.read().format(logger_name=logger_name)
    tmp.close()

    if not multi_process:
        new_logger_config = new_logger_config.replace("ConcurrentRotatingFileHandler","RotatingFileHandler")

    logger_path = current_path+'/logger_'+logger_name+ str(random.randint(0,100000)) + '.ini'
    new_logger_config_file = open(logger_path, 'w')
    new_logger_config_file.write(new_logger_config)
    new_logger_config_file.flush()
    new_logger_config_file.close()

    logging.config.fileConfig(logger_path)
    os.remove(logger_path)

    logging.getLogger().logger_name = logger_name

    return logging.getLogger(logger_name)

