#!/bin/env python
# -*- coding: utf-8 -*-\
import MySQLdb
import config,time
import threading
import logging
import sys
reload(sys)
sys.setdefaultencoding("utf-8")
class MySql(object):

    def __init__(self,db_name,logger_name=""):
        self.db_name     = db_name
        self.logger_name = logger_name
        self.connection = self.connect_to_mysql(db_name)
        self.condition  = threading.Condition()

    def connect_to_mysql(self,db_name):
        host, port, username, password, database, charset = config.get_mysql_parameter(db_name)
        return MySQLdb.connect(host=host, user=username, passwd=password, db=database, port=port, charset=charset)


    def insert(self,p_table_name, p_data):
        keys = ','.join(p_data.keys())
        values = ','.join(["'{0}'".format(MySQLdb.escape_string(str(value)  )) for value in p_data.values()])
        real_sql = "INSERT INTO " + p_table_name + " (" + keys + ") VALUES (" + values + ")"

        self.execute_sql(real_sql)
        return real_sql

    def delete(self, sql):
        self.execute_sql(sql)

    def query(self, sql):
        with self.condition:
            rows = self._query(sql)
        return rows

    def query_(self,table,result_list,condition_dic={}):

        condition_list = []
        key_list = []
        for key in condition_dic.keys():
            value = condition_dic[key]
            value = '"{0}"'.format(value) if type(value) not in (int, float, long) else value

            if  key.endswith('_gte'):
                new_key = key.replace('_gte', '')
                con_str = ' {0} >= {1} '.format(new_key, value)

            elif  key.endswith('_gt'):
                new_key = key.replace('_gt', '')
                con_str = ' {0} > {1} '.format(new_key, value)

            elif  key.endswith('_lte'):
                new_key = key.replace('_lte', '')
                con_str = ' {0} <= {1} '.format(new_key, value)

            elif  key.endswith('_lt'):
                new_key = key.replace('_lt', '')
                con_str = ' {0} < {1} '.format(new_key, value)

            elif  key.endswith('_ne'):
                new_key = key.replace('_ne', '')
                con_str = ' {0} != {1} '.format(new_key, value)
                
            else:
                new_key = key
                con_str = ' {0} = {1} '.format(new_key, value)

            key_list.append(new_key)
            condition_list.append(con_str)

        condition = 'and'.join(condition_list)
        res_filed = ','.join(result_list)
        if len(condition) == 0:
            sql = "select {0} from {1} ".format(res_filed, table)
        else:
            sql = "select {0} from {1} where {2}".format(res_filed, table, condition)
        print sql
        return self.query(sql)



    def _query(self, sql,try_count=1):
        if try_count > 3:
            return

        try:
            cur = self.connection.cursor()
            cur.execute(sql)
            rows = cur.fetchall()
            cur.close()
            return rows
        except:
            time.sleep(10)
            self.connection = self.connect_to_mysql(self.db_name)
            self._query(sql,try_count+1)


    def execute_sql(self, sql):
        with self.condition:
            self._execute_sql(sql)


    def _execute_sql(self, sql,try_count=1):
        if try_count > 3:
            return

        try:
            cur = self.connection.cursor()
            cur.execute(sql)
            self.connection.commit()
            cur.close()
        except MySQLdb.IntegrityError:
            pass
        except:
            time.sleep(10)
            logging.getLogger(self.logger_name).exception("mysql_execute_sql")
            self.connection = self.connect_to_mysql(self.db_name)
            self._execute_sql(sql,try_count+1)

    def close(self):
        self.connection.close()


def insert(p_table_name, p_data):
    key = ','.join(p_data.keys())
    values = ','.join(["'{0}'".format(MySQLdb.escape_string(str(value).encode('utf-8'))) for value in p_data.values()])

    real_sql = "INSERT INTO " + p_table_name + " (" + key + ") VALUES (" + values + ")"
    print real_sql


def query_(self, table, result_list, condition_dic):
    condition_list = []
    key_list = []
    for key in condition_dic.keys():
        value = condition_dic[key]
        value = '"{0}"'.format(value) if type(value) not in (int, float, long) else value

        if '_gte' in key:
            new_key = key.replace('_gte', '')
            con_str = ' {0} >= {1} '.format(new_key, value)
        elif '_gt' in key:
            new_key = key.replace('_gt', '')
            con_str = ' {0} > {1} '.format(new_key, value)
        elif '_lte' in key:
            new_key = key.replace('_lte', '')
            con_str = ' {0} <= {1} '.format(new_key, value)
        elif '_lt' in key:
            new_key = key.replace('_lt', '')
            con_str = ' {0} < {1} '.format(new_key, value)
        else:
            new_key = key
            con_str = ' {0} = {1} '.format(new_key, value)

        key_list.append(new_key)
        condition_list.append(con_str)

    condition = 'and'.join(condition_list)
    res_filed = ','.join(result_list)
    if len(condition) == 0:
        sql = "select {0} from {1} ".format(res_filed, table)
    else:
        sql = "select {0} from {1} where {2}".format(res_filed, table, condition)

    print sql




if __name__ == '__main__':
    a={'tt':'d\'d','dtt':0}
    insert("aaa",a)
    post = {'post_id':'dd\'\t\"dd','likes':0,'comments':0,'shares':0}
    query_('','tttt',['d','m','a'],{'b':4,'mm':'dfdfdf','gte_gte':'gte','gt_gt':'gt','lte_lte':'lte','lt_lt':3})

