package com.test.schd;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.thymeleaf.expression.Maps;

/**
 * @ClassName: Request
 * @Description: 爬虫请求封装对象
 * @date: 2015年3月19日 下午3:30:52
 */
public class Request implements Serializable {

	private static final long serialVersionUID = 2062192774891352043L;

	/** 重试次数的key */
	public static final String CYCLE_TRIED_TIMES = "_cycle_tried_times";
	/** 强制重爬的key */
	public static final String ENFORCE_RETRIED = "_enforce_retried";
	/** request的type，目前氛围split跟detail，see {@link org.webant.template.UrlType} */
	public static final String REQUEST_TYPE = "_request_type";
	/** 用于保存request的http method类别的key，如get，post，put等 */
	public static final String HTTP_METHOD_TYPE_KEY = "httpMethodType";
	/** 用于保存http参数类别的key */
	public static final String HTTP_HTTP_PARAMS_KEY = "httpParams";

	/** 用于标记request是否成功，默认为false */
	protected boolean downloadSuccess = false;

	/** request请求对应的http method，默认是get */
//	private HttpMethodType httpMethodType = HttpMethodType.GET;

	/** 父url */
	private String parentUrl;

	private String url;
	
	/**
	 * 用来唯一标识一个request
	 */
	private String id;

	/**
	 * Store additional information in extras.
	 */
	private Map<String, Object> extras;

	/** request自己的参数，不会传递给它分裂出来的request，除了templateid以及taskid，currenttaskid */
	private Map<String, Object> ownExtras;

	/** http post params */
	private Map<String, String> httpParams = new HashMap();
	/** http headers */
	private Map<String, String> httpHeaders = new HashMap();

	/**
	 * Priority of the request.<br>
	 * The bigger will be processed earlier. <br>
	 * 
	 * @see us.codecraft.webmagic.scheduler.PriorityScheduler
	 */
	private long priority;

	/**
	 * crawler deep
	 */
	private int deep;

	public Request() {
		this.id = UUID.randomUUID().toString();
	}

	public Request(String url) {
		this.id = UUID.randomUUID().toString();
		this.url = url;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getPriority() {
		return priority;
	}

	/**
	 * Set the priority of request for sorting.<br>
	 * Need a scheduler supporting priority.<br>
	 * 
	 * @see us.codecraft.webmagic.scheduler.PriorityScheduler
	 *
	 * @param priority
	 * @return this
	 */
	public Request setPriority(long priority) {
		this.priority = priority;
		return this;
	}

	public Object getExtra(String key) {
		if (extras == null) {
			return null;
		}
		return extras.get(key);
	}

	public Request putExtra(String key, Object value) {
		if (extras == null) {
			extras = new HashMap<String, Object>();
		}
		extras.put(key, value);
		return this;
	}

	public Request putExtras(Map<String, Object> curExtras) {
		if (null == extras) {
			extras = new HashMap();
		}
		extras.putAll(curExtras);
		return this;
	}

	public Object getOwnExtra(String key) {
		if (ownExtras == null) {
			return null;
		}
		return ownExtras.get(key);
	}

	public Request putOwnExtra(String key, Object value) {
		if (ownExtras == null) {
			ownExtras = new HashMap<String, Object>();
		}
		ownExtras.put(key, value);
		return this;
	}

	public Request putOwnExtras(Map<String, Object> curExtras) {
		if (null == ownExtras) {
			ownExtras = new HashMap();
		}
		ownExtras.putAll(curExtras);
		return this;
	}

	public Request putOwnExtrasByString(Map<String, String> curExtras) {
		if (null == ownExtras) {
			ownExtras = new HashMap();
		}
		ownExtras.putAll(curExtras);
		return this;
	}

	public Request putHttpParam(String key, String value) {
		this.httpParams.put(key, value);
		return this;
	}

	public Request putHttpParams(Map<String, String> params) {
		if (null != params)
			this.httpParams.putAll(params);

		return this;
	}

	public Map<String, String> getHttpParams() {
		Map<String, String> params = new HashMap();
		if (null != httpParams) {
			params.putAll(httpParams);
		}

		return params;
	}

	public Request putHttpHeader(String headerName, String headerValue) {
		this.httpHeaders.put(headerName, headerValue);
		return this;
	}

	public Request putHttpHeaders(Map<String, String> headers) {
		if (null != headers)
			this.httpHeaders.putAll(headers);

		return this;
	}

	public Map<String, String> getHttpHeaders() {
		Map<String, String> headers = new HashMap();
		if (null != httpHeaders) {
			headers.putAll(httpHeaders);
		}

		return headers;
	}

	/**
	 * @Description 获取当前request重试的次数(使用get开头会在生成json字符串的时候带上CycleRetryTimes字段)
	 * @return
	 */
	public int checkoutCycleRetryTimes() {
		int result = 0;
		Object obj = getOwnExtra(Request.CYCLE_TRIED_TIMES);
		if (null != obj) {
			Integer resultInt = null;
			try {
				resultInt = Integer.valueOf(obj.toString());
				result = resultInt.intValue();
			} catch (Exception ex) {
				// TODO int转换异常
			}
		}

		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Request request = (Request) o;

		if (!url.equals(request.url))
			return false;

		return true;
	}

	public Map<String, Object> getExtras() {
		return extras;
	}

	public Map<String, Object> getOwnExtras() {
		return ownExtras;
	}

	@Override
	public int hashCode() {
		return url.hashCode();
	}

	public void setExtras(Map<String, Object> extras) {
		this.extras = extras;
	}

	public void setOwnExtras(Map<String, Object> ownExtras) {
		this.ownExtras = ownExtras;
	}

	public void setHttpParams(Map<String, String> params) {
		this.httpParams = params;
	}

	public void setHttpHeaders(Map<String, String> headers) {
		this.httpHeaders = headers;
	}

	/**
	 * @Description 获取request的原始的key，该key用于将request保存到mongodb时作为mongodb的key（_id，
	 *              还需要进行一次MD5的转换）
	 * @return
	 */
	public String getUniqueId() {
		if (null == url)
			return "";
		String urlKey = url;
//		if (null != httpMethodType) {
//			// 如果是post或者是put方法，则把http 参数当成key的一部分
//			if (httpMethodType == HttpMethodType.POST || httpMethodType == HttpMethodType.PUT) {
//				urlKey += httpParams.toString();
//			}
//		}

		return urlKey;
	}

	/**
	 * @return the downloadSuccess
	 */
	public boolean isDownloadSuccess() {
		return downloadSuccess;
	}

	/**
	 * @param downloadSuccess
	 *            the downloadSuccess to set
	 */
	public void setDownloadSuccess(boolean downloadSuccess) {
		this.downloadSuccess = downloadSuccess;
	}

	/**
	 * @return the httpMethodType
	 */
//	public HttpMethodType getHttpMethodType() {
//		return httpMethodType;
//	}

	/**
	 * @param httpMethodType
	 *            the httpMethodType to set
	 */
//	public void setHttpMethodType(HttpMethodType httpMethodType) {
//		this.httpMethodType = httpMethodType;
//	}

	/**
	 * @return the parentUrl
	 */
	public String getParentUrl() {
		return parentUrl;
	}

	/**
	 * @param parentUrl
	 *            the parentUrl to set
	 */
	public void setParentUrl(String parentUrl) {
		this.parentUrl = parentUrl;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url
	 *            the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the deep
	 */
	public int getDeep() {
		return deep;
	}

	/**
	 * @param deep
	 *            the deep to set
	 */
	public void setDeep(int deep) {
		this.deep = deep;
	}

	@Override
	public String toString() {
		return "Request{" + "url='" + url + '\'' + ", deep=" + deep + ", http mothod=" 
				+ ", http headers=" + httpHeaders + ", http parameters=" + httpParams + ", extras=" + extras
				+ ", ownExtras=" + ownExtras + ", priority=" + priority + ", parent url=" + parentUrl + '}';
	}
}
