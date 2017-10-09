package com.test.schd;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class SourceFeads implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6924181255473160090L;
	private int id;
	private String source_type;
	private String source;
	private String source_feeds;
	private String source_feeds_name;
	private String source_feeds_url;
	private String source_feeds_host;
	private String source_feeds_category;
	private String channel;
	private String language;
	private int rate;
	private int link_type;
	private int spider_status;
	private Date update_time;
	private String xpath;
	private String detailSeedUrl;
	private String country;
	private String xpath2;

	private String xpath_timeliness;
	private String xpath_rule;
	private int load_type;

	private int load_type_list;
	
	private int crawler_interval;
	private String last_push_time;
	private String charset;
	private int use_xpath_function;

	private int debug;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSource_type() {
		return source_type;
	}

	public void setSource_type(String source_type) {
		this.source_type = source_type;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getSource_feeds() {
		return source_feeds;
	}

	public void setSource_feeds(String source_feeds) {
		this.source_feeds = source_feeds;
	}

	public String getSource_feeds_name() {
		return source_feeds_name;
	}

	public void setSource_feeds_name(String source_feeds_name) {
		this.source_feeds_name = source_feeds_name;
	}

	public String getSource_feeds_url() {
		return source_feeds_url;
	}

	public void setSource_feeds_url(String source_feeds_url) {
		this.source_feeds_url = source_feeds_url;
	}

	public String getSource_feeds_category() {
		return source_feeds_category;
	}

	public void setSource_feeds_category(String source_feeds_category) {
		this.source_feeds_category = source_feeds_category;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public int getRate() {
		return rate;
	}

	public void setRate(int rate) {
		this.rate = rate;
	}

	public int getLink_type() {
		return link_type;
	}

	public void setLink_type(int link_type) {
		this.link_type = link_type;
	}

	public int getSpider_status() {
		return spider_status;
	}

	public void setSpider_status(int spider_status) {
		this.spider_status = spider_status;
	}

	public Date getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(Date update_time) {
		this.update_time = update_time;
	}


	/**
	 * 判断查询结果集中是否存在某列
	 * 
	 * @param rs
	 *            查询结果集
	 * @param columnName
	 *            列名
	 * @return true 存在; false 不存咋
	 */
	public boolean isExistColumn(ResultSet rs, String columnName) {
		try {
			if (rs.findColumn(columnName) > 0) {
				return true;
			}
		} catch (SQLException e) {
			return false;
		}

		return false;
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(" id : " + id);
		builder.append(" source_type : " + source_type);
		builder.append(" source : " + source);
		builder.append(" source_feeds : " + source_feeds);
		builder.append(" source_feeds_name : " + source_feeds_name);
		builder.append(" source_feeds_url : " + source_feeds_url);
		builder.append(" source_feeds_category : " + source_feeds_category);
		builder.append(" channel : " + channel);
		builder.append(" language : " + language);
		builder.append(" rate : " + rate);
		builder.append(" link_type : " + link_type);
		builder.append(" spider_status : " + spider_status);
		builder.append(" update_time : " + update_time);
		builder.append(" xpath : " + xpath);
		builder.append(" crawler_interval : " + crawler_interval);
		builder.append(" country : " + country);
		builder.append(" xpath_rule : " + xpath_rule);
		builder.append(" debuge : " + debug);

		return builder.toString();
	}

	public String getXpath() {
		return xpath;
	}

	public void setXpath(String xpath) {
		this.xpath = xpath;
	}

	public String getDetailSeedUrl() {
		return detailSeedUrl;
	}

	public void setDetailSeedUrl(String detailSeedUrl) {
		this.detailSeedUrl = detailSeedUrl;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public int getCrawler_interval() {
		return crawler_interval;
	}

	public void setCrawler_interval(int crawler_interval) {
		this.crawler_interval = crawler_interval;
	}

	public String getLast_push_time() {
		return last_push_time;
	}

	public void setLast_push_time(String last_push_time) {
		this.last_push_time = last_push_time;
	}

	public String getXpath2() {
		return xpath2;
	}

	public void setXpath2(String xpath2) {
		this.xpath2 = xpath2;
	}

	public String getXpath_timeliness() {
		return xpath_timeliness;
	}

	public void setXpath_timeliness(String xpath_timeliness) {
		this.xpath_timeliness = xpath_timeliness;
	}

	public String getXpath_rule() {
		return xpath_rule;
	}

	public void setXpath_rule(String xpath_rule) {
		this.xpath_rule = xpath_rule;
	}

	public int getDebug() {
		return debug;
	}

	public void setDebug(int debug) {
		this.debug = debug;
	}

	public String getSource_feeds_host() {
		return source_feeds_host;
	}

	public void setSource_feeds_host(String source_feeds_host) {
		this.source_feeds_host = source_feeds_host;
	}

	public int getLoad_type() {
		return load_type;
	}

	public void setLoad_type(int load_type) {
		this.load_type = load_type;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public int getUse_xpath_function() {
		return use_xpath_function;
	}

	public void setUse_xpath_function(int use_xpath_function) {
		this.use_xpath_function = use_xpath_function;
	}

	public int getLoad_type_list() {
		return load_type_list;
	}

	public void setLoad_type_list(int load_type_list) {
		this.load_type_list = load_type_list;
	}

}