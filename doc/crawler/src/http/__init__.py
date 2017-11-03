import http_client
import time
import logging
import requests

class HttpClient(object):
    def __init__(self,logger_name):
        self.logger = logging.getLogger(logger_name)


    def http_get(self,url):
        start_time = time.time()
        try:
            content =  http_client.http_get(url)
            cost_time = time.time() - start_time
            self.logger.warn('process http get url : {0} cost {1} seconds'.format(url, cost_time))
            return content
        except requests.exceptions.Timeout:
            cost_time = time.time() - start_time
#             utils.send_http_log_to_es_timeout(url,cost_time)
            self.logger.exception('process http get url : {0}'.format(url))
        except:
            self.logger.exception('process http get url : {0}'.format(url))


    def http_get_with_proxy(self,url):
        start_time = time.time()
        content = http_client.http_get_with_proxy(url)
        cost_time = time.time() - start_time
        self.logger.warn('process http get with proxy url : {0} cost {1} seconds'.format(url, cost_time))
        return content

    def http_get_with_proxy_2(self, url):
        start_time = time.time()
        content = http_client.http_get_with_proxy_2(url)
        cost_time = time.time() - start_time
        self.logger.warn('process http get with proxy url : {0} cost {1} seconds'.format(url, cost_time))
        return content


    def img_http_get_with_proxy(self,url):
        start_time = time.time()
        content = http_client.http_img_get_with_proxy(url)
        cost_time = time.time() - start_time
        self.logger.warn('process http get with proxy url : {0} cost {1} seconds'.format(url, cost_time))
        return content


    def img_http_get(self,url):
        start_time = time.time()
        content = http_client.http_get(url)
        cost_time = time.time() - start_time
        self.logger.warn('process http get url : {0} cost {1} seconds'.format(url, cost_time))
        return content


    def http_post(self,url,data=None):
        start_time = time.time()
        try:
            content = http_client.http_post(url,data)
            cost_time = time.time() - start_time
            self.logger.warn('process http post url : {0} cost {1} seconds'.format(url, cost_time))
            return  content
        except requests.exceptions.Timeout:
            cost_time = time.time() - start_time
#             utils.send_http_log_to_es_timeout(url,cost_time)
            self.logger.exception('process http get url : {0}'.format(url))
        except:
            self.logger.exception('process http get url : {0}'.format(url))