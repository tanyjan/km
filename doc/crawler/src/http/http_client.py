#!/bin/env python
# -*- coding: utf-8 -*-\
import requests
import json
import config
import time
from requests.adapters import HTTPAdapter

http_timeout = 10
http_headers = { 'Accept': '*/*','Connection': 'keep-alive', 'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.116 Safari/537.36'}

def get_session(proxies=None):
    request_retry = HTTPAdapter(max_retries=3)
    session = requests.Session()
    session.mount("http://", request_retry)
    session.mount("https://", request_retry)
    session.headers = http_headers
    session.verify  = False
    session.proxies = proxies
    return session


def http_post(url, data=None):
    with get_session() as session:
        start_time = time.time()
        response = session.post(url, headers=http_headers,
                                data=data,timeout=http_timeout)
        if response.status_code == 200:
            return  response.content
        else:
            cost_time = time.time() - start_time
#             utils.send_http_log_to_es(response,cost_time)
            return None


def http_get(url):
    with get_session() as session:
        start_time = time.time()
        response = session.get(url, headers=http_headers,timeout=http_timeout)
        if response.status_code == 200:
            return   response.content
        else:
            cost_time = time.time() - start_time
#             utils.send_http_log_to_es(response,cost_time)
            return None


def http_get_with_proxy(url,count=1):
    if count > 10:  return None
    proxies,ip,port = get_dailyhut_proxy_ip()
    try:
        response = get_session().get(url, headers=http_headers,
                                     proxies=proxies,timeout=http_timeout)
        if response.status_code != 200:
            report_fail(ip,port)
            return http_get_with_proxy(url,count+1)

        content = response.content
        response.close()
        return content
    except:
        report_fail(ip,port)
        return http_get_with_proxy(url,count+1)


def http_img_get_with_proxy(url,count=1):
    if count > 10:
        return

    proxies,ip,port = get_dailyhut_img_proxy_ip()
    try:
        response = get_session().get(url, headers=http_headers,
                                     proxies=proxies,timeout=http_timeout)

        if response.status_code != 200:
            report_img_fail(ip,port)
            return http_img_get_with_proxy(url,count+1)

        content = response.content
        response.close()
        return content
    except:
        report_img_fail(ip,port)
        return http_img_get_with_proxy(url,count+1)


def get_proxy(proxy_url):
    with get_session() as session:
        response = session.get(proxy_url, timeout=http_timeout)
        if response.status_code == 200:
            data = json.loads(response.content)
            ip = data['proxy']['ip']
            port = data['proxy']['port']
            proxies = {
                "http": "http://{0}:{1}".format(ip, port),
                "https": "http://{0}:{1}".format(ip, port),
            }
            return proxies, ip, port
        else:
            return None


def get_dailyhut_proxy_ip():
    proxy_url = "http://{host}/proxy/get?code=Daylyhut".format(host=config.get_proxy_host())
    return get_proxy(proxy_url)


def get_dailyhut_img_proxy_ip():
    proxy_url = "http://{host}/proxy/get?code=DaylyhutImg".format(host=config.get_proxy_host())
    return get_proxy(proxy_url)

def report_img_fail(ip,port):
    get_session().get("http://{host}/proxy/fail?code=DaylyhutImg&ip={ip}&port={port}"
                      .format(host=config.get_proxy_host(),ip=ip,port=port),timeout=http_timeout)


def report_fail(ip,port):
    get_session().get("http://{host}/proxy/fail?code=Daylyhut&ip={ip}&port={port}"
                      .format(host=config.get_proxy_host(),ip=ip,port=port),timeout=http_timeout)


def get_http_proxy_by_code(web_code='www.google.com'):
    proxy_url = "http://{host}/proxy/getByUrl?url={code}".format(host=config.get_proxy_host(),code=web_code)
    return get_proxy(proxy_url)


def http_get_with_proxy_2(url,count=1):
    if count > 10:  return None
    proxies,ip,port = get_http_proxy_by_code(url)
    try:
        response = get_session().get(url, headers=http_headers,
                                     proxies=proxies,timeout=http_timeout)
        if response.status_code != 200:
            report_fail(ip,port)
            return http_get_with_proxy_2(url,count+1)

        content = response.content
        response.close()
        return content
    except:
        report_fail(ip,port)
        return http_get_with_proxy_2(url,count+1)


