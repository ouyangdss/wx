package com.example.weixin.demo.service;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

public interface WeiXinService {

	void doRequestXmlData(String queryString, Map<String, String> reqXmlData,HttpServletResponse response);

	boolean deleteMenu();

	boolean menuAdd();

}
