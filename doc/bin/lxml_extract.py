#!/usr/bin/env python
# coding=utf-8
import logging
import os
import sys
import time
from logging.handlers import RotatingFileHandler

import requests

reload(sys)
sys.setdefaultencoding('utf-8')
# sys.path.extend(['', '/usr/local/bin', '/usr/lib/python2.7', '/usr/lib/python2.7/plat-x86_64-linux-gnu',
#                  '/usr/lib/python2.7/lib-tk', '/usr/lib/python2.7/lib-old', '/usr/lib/python2.7/lib-dynload',
#                  '/home/mianjune/.local/lib/python2.7/site-packages', '/usr/local/lib/python2.7/dist-packages',
#                  '/usr/lib/python2.7/dist-packages', '/usr/lib/python2.7/dist-packages/gtk-2.0',
#                  '/usr/lib/python2.7/dist-packages/wx-3.0-gtk2',
#                  '/usr/local/lib/python2.7/dist-packages/IPython/extensions', '/home/mianjune/.ipython'])
from lxml import etree

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
HTTP_HEADERS = {
    'Accept': 'text/html, application/xhtml+xml, image/jxr, */*',
    'Accept-Encoding': 'gzip, deflate, sdch',
    'Accept-Language': 'zh-CN,zh;q=0.8,en-US;q=0.6,en;q=0.4',
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36',
}


def get_logger(logger_name=None, level=logging.DEBUG):
    path = os.path.join(BASE_DIR, 'logs/{0}.log'.format(logger_name or 'lxml_extract'))
    if not os.path.exists(os.path.dirname(path)):
        os.makedirs(os.path.dirname(path))

    logger = logging.getLogger(logger_name)
    if len(logger.handlers) > 0:
        return logger

    logger.setLevel(logging.DEBUG)
    fmt = logging.Formatter('%(asctime)s %(levelname)-7s %(filename)s[%(lineno)s]\t %(message)s',
                            '%Y-%m-%d %H:%M:%S')
    # 设置文件日志
    fh = RotatingFileHandler(path, maxBytes=200 * 1024 * 1024, backupCount=6, encoding="UTF-8")
    fh.setFormatter(fmt)
    fh.setLevel(level)
    logger.addHandler(fh)
    return logger


logger = get_logger()


def http_get(url, http_headers=None, proxies=None, retry_count=3):
    logger.debug('HTTP GET: %s', url)
    with requests.session() as session:
        session.headers = http_headers or HTTP_HEADERS
        for _ in xrange(retry_count):
            try:
                r = session.get(url=url, proxies=proxies, timeout=16)
                logger.debug('Response code: %s', r.status_code)
                return r.content
            except:
                logger.exception('Fail to get %s time%s: url[%s]', _ + 1, 's' if _ else '', url)
                time.sleep(6)


def extract(source, xpath, ex_xpath=None, xpath_charset=None):
    results = ''
    try:
        if xpath_charset:
            source = source.decode(xpath_charset)
        html = etree.HTML(source)
        if ex_xpath:
            for xp in ex_xpath.strip(' \n\t@').split('@@'):
                if xpath_charset:
                    xp = xp.decode(xpath_charset)
                for e in html.xpath(xp):
                    e.getparent().remove(e)

        for xp in xpath.strip(' \n\t@').split('@@'):
            if xpath_charset:
                xp = xp.decode(xpath_charset)
            for e in html.xpath(xp):
                xml = etree.tostring(e, encoding=xpath_charset or None, pretty_print=True).strip()
                if xml: results += xml
    except:
        logger.exception('Fail to extract xml!')
        logger.error('===============source: %s', source)
        logger.error('===============xpath: %s', xpath)
        logger.error('===============ex_xpath: %s', ex_xpath)
        logger.error("===============charset: %s", xpath_charset)

    return results


if __name__ == '__main__':
    try:
        # print 'Start lxml_extract'
        assert len(sys.argv) > 4, 'Wrong args count!'

        xpath = sys.argv[1]
        exclude_xpath = sys.argv[2]
        xpath_charset = sys.argv[3]

        source = ''
        arg_index = 4
        while arg_index < len(sys.argv):
            source += sys.argv[arg_index]
            arg_index += 1

        # source = '/home/mianjune/Documents/git/webant-platform/webant-xpath/test_source2.html'
        # xpath = "//p[@class='subNota']@@//div[@class='large-8 medium-12 small-12 columns nopadding principal sharemargin']//img@@//div[@class='large-8 columns mbottom1 paddBanner contNotaTexto nt-izq ']"
        # exclude_xpath = "//div[@class='btnNote']@@//b[contains(text(),'No te pierdas')]"

        if os.path.isfile(source):
            with open(source, 'r') as f:
                source = f.read()
        elif len(source) < 520 and source.strip().startswith('http'):
            source = http_get(source)

        # print "source: ", source
        # print "xpath: ", xpath

        contents = extract(source, xpath, exclude_xpath, xpath_charset)
        print contents
        # print 'End lxml_extract'

    except:
        logger.exception('Fail to execute python script!')
        logger.error('sys.args: %s', sys.argv)
