package com.example.weixin.demo.template;

import java.util.Map;

public class TemplateMsg {
	private String touser;
	private String template_id;
	private String url;
	private String topcolor;
	private Map<String,TempData> data;
	public String getTouser() {
		return touser;
	}
	public void setTouser(String touser) {
		this.touser = touser;
	}
	public String getTemplate_id() {
		return template_id;
	}
	public void setTemplate_id(String template_id) {
		this.template_id = template_id;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Map<String, TempData> getData() {
		return data;
	}
	public void setData(Map<String, TempData> data) {
		this.data = data;
	}
	public String getTopcolor() {
		return topcolor;
	}
	public void setTopcolor(String topcolor) {
		this.topcolor = topcolor;
	}
	
	

}
