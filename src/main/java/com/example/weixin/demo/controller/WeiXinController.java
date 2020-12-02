package com.example.weixin.demo.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.example.weixin.demo.service.WeiXinService;
import com.example.weixin.demo.utils.WeiXinUtil;
import com.example.weixin.demo.utils.XMLUtils;
/**
 * 
 * @author Administrator
 *
 */
@Controller
public class WeiXinController {
	
	@Autowired
	private WeiXinService weiXinService;
	
	@RequestMapping(value="/wx",method={RequestMethod.GET,RequestMethod.POST})
	public void wxLogin(HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException{
		//设置编码，不然接收到的消息乱码
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		String signature = request.getParameter("signature");//微信加密签名
		String timestamp = request.getParameter("timestamp");//时间戳
		String nonce = request.getParameter("nonce");//随机数
		String echostr = request.getParameter("echostr");//随机字符串
		PrintWriter out = null;
			//接入验证
			  if(WeiXinUtil.checkSignature(signature, timestamp, nonce)){
				  if (echostr!=null) {
					  System.out.println(echostr);
					  try {
						out = response.getWriter();
					} catch (IOException e) {
						e.printStackTrace();
					}
					  out.write(echostr);//验证成功返回的值
					  return;
				}
				  Map<String, String> reqXmlData;
				  try {
					  reqXmlData = XMLUtils.parseXmlFromRequest(request);
					  weiXinService.doRequestXmlData(request.getQueryString(), reqXmlData,response);
				  } catch (Exception e) {
					  e.printStackTrace();
				  }
			}
	}

	/**
	 * 添加微信公众号菜单
	 * @return
	 */
	@RequestMapping(value="/menuAdd",method=RequestMethod.POST)
	public String menuAdd(){
		boolean b = weiXinService.menuAdd();
		if (b) {
			return "success";
		}
		return "unsuccess";
	}
	/**
	 * 删除微信公众号菜单
	 * @return
	 */
	@RequestMapping(value="/menuDelete",method=RequestMethod.GET)
	public String deleteMenu(){
		boolean b = weiXinService.deleteMenu();
		if (b) {
			return "success";
		}
		return "unsuccess";
	}
}
