import ConfigParser
import os
import sys

if 'win' in sys.platform:
    config_file = current_path = os.path.split(os.path.realpath(__file__))[0]+ "/config_debug.ini"
else:
    config_file = current_path = os.path.split(os.path.realpath(__file__))[0]+ "/config.ini"

def get(section, name):
    cf = ConfigParser.ConfigParser()

    cf.read(config_file)
    return cf.get(section, name)

if __name__ == '__main__':
    print get("rabbit", "rabbitmq_ip")
