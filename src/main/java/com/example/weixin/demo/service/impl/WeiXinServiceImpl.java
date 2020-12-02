package com.example.weixin.demo.service.impl;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.example.weixin.demo.service.WeiXinService;
import com.example.weixin.demo.utils.HttpUtil;
import com.example.weixin.demo.utils.MsgUtil;
import com.example.weixin.demo.utils.WeiXinUtil;
import com.example.weixin.demo.utils.XMLUtils;

import net.sf.json.JSONObject;
@Service
public class WeiXinServiceImpl implements WeiXinService {
	private static Logger logger = LoggerFactory.getLogger(WeiXinServiceImpl.class);

	public void doRequestXmlData(String queryString, Map<String, String> reqXmlData,HttpServletResponse response) {
		String msgType = reqXmlData.get(XMLUtils.c_xml_msg_key_MsgType);//获取消息类型
		String content = reqXmlData.get(XMLUtils.c_xml_msg_key_Content);//获取消息内容
		String event = reqXmlData.get(XMLUtils.c_xml_msg_key_Event);//获取事件类型
		String openId = reqXmlData.get(XMLUtils.c_xml_msg_key_FromUserName);//获取发送者openID
		String formUser = reqXmlData.get(XMLUtils.c_xml_msg_key_ToUserName);//获取接受者ID
		String eventKey = reqXmlData.get(XMLUtils.c_xml_msg_key_EventKey);//获取扫码事件的key
		
		//文本消息
		if(XMLUtils.c_xml_msg_type_text.equals(msgType)){
			doMsg(response, content, openId, formUser);
		}
		//事件消息
		if(XMLUtils.c_xml_msg_type_event.equals(msgType)){
			// 处理订阅事件
			if(XMLUtils.c_xml_msg_event_subscribe.equals(event)){
				doSubscribe(queryString, reqXmlData, openId,formUser,response);
			}else if(XMLUtils.c_xml_msg_event_unsubscribe.equals(event)){ // 取消订阅
				doUnSubscribe(queryString, reqXmlData, openId);
			}else if(XMLUtils.c_xml_msg_event_scan.equals(event)){ // 扫描
				doScan(queryString, reqXmlData, openId,formUser,response,eventKey);
			}else if(XMLUtils.c_xml_msg_event_click.equals(event)){//点击事件
				doClick(reqXmlData, openId,formUser,response,eventKey);
			}else if(XMLUtils.c_xml_msg_event_photo.equals(event)){//系统拍照发图事件
				doPhoto(queryString, reqXmlData, openId,formUser,response,eventKey);
			}
		}
		if(XMLUtils.c_xml_msg_type_location.equals(msgType)){
			doLocation(reqXmlData, response, openId, formUser);
		}
	}

	/**
	 * 接收用户发的文本消息
	 * @param response
	 * @param content
	 * @param openId
	 * @param formUser
	 */
	private void doMsg(HttpServletResponse response, String content, String openId, String formUser) {
		if ("?".equals(content)||"？".equals(content)) {
			MsgUtil.sendTextMsgToUser("帮助命令：1、接收文本消息   2、接收图片消息    3、接收图文消息    "
					+ "4、接收模板消息    5、生成带参数的二维码 ", openId,formUser,response);
		}
		if ("1".equals(content)) {
			MsgUtil.sendTextMsgToUser("文本消息", openId,formUser,response);
		}
		if ("2".equals(content)) {
			String mediaId = WeiXinUtil.upload("C:/Users/Administrator/Desktop/code.jpg", "image");//上传到微信服务器获取mediaId
			MsgUtil.sendImageMsgToUser( openId,formUser,response,mediaId);
		}
		if ("3".equals(content)) {
			MsgUtil.sendNewsMsgToUser( openId,formUser,response);
		}
		if ("4".equals(content)) {
			MsgUtil.sendTemplateMsgToUser( openId,response);
		}
		if ("5".equals(content)) {
			//生成带参数的二维码，并将二维码的链接发送给用户
			String ticket= WeiXinUtil.cerateCode();//生成票据
			String url = WeiXinUtil.SHOW_CODE.replace("TICKET", ticket);
			MsgUtil.sendTextMsgToUser(url, openId,formUser,response);
		}
	}

	/**
	 * 接收用户发的位置消息
	 * @param reqXmlData
	 * @param response
	 * @param openId
	 * @param formUser
	 */
	private void doLocation(Map<String, String> reqXmlData, HttpServletResponse response, String openId,
			String formUser) {
		String x = reqXmlData.get("Location_X");
		String y = reqXmlData.get("Location_Y");
		String Label = reqXmlData.get("Label");
		MsgUtil.sendTextMsgToUser("x坐标"+x+"y坐标"+y+"地址："+Label,
				openId, formUser, response);
	}

	/**
	 * 系统拍照发图事件
	 * @param queryString
	 * @param reqXmlData
	 * @param openId
	 * @param formUser
	 * @param response
	 * @param eventKey
	 */
	private void doPhoto(String queryString, Map<String, String> reqXmlData, String openId, String formUser,
			HttpServletResponse response, String eventKey) {
		MsgUtil.sendTextMsgToUser(eventKey, openId,formUser,response);
		System.out.println("相册发图事件");
	}

	/**
	 * 点击事件
	 * @param reqXmlData
	 * @param openId
	 * @param eventKey 
	 * @param response 
	 * @param formUser 
	 */
	private void doClick(Map<String, String> reqXmlData, String openId, String formUser, HttpServletResponse response, String eventKey) {
		MsgUtil.sendTextMsgToUser("感谢您的支持"+eventKey, openId, formUser, response);
		System.out.println("点击事件");
	}

	/**
	 * 扫码事件
	 * @param queryString
	 * @param reqXmlData
	 * @param openId
	 */
	private void doScan(String queryString, Map<String, String> reqXmlData, String openId,String formUser,HttpServletResponse response,String eventKey) {
		System.out.println("扫码事件");
		MsgUtil.sendTextMsgToUser(eventKey, openId, formUser, response);
	}

	/**
	 * 取消关注事件
	 * @param queryString
	 * @param reqXmlData
	 * @param openId
	 */
	private void doUnSubscribe(String queryString, Map<String, String> reqXmlData, String openId) {
		System.out.println("取消关注事件");
	}
	
	/**
	 * 关注事件
	 * @param queryString
	 * @param reqXmlData
	 * @param openId
	 */
	private void doSubscribe(String queryString, Map<String, String> reqXmlData, String openId,String formUser,HttpServletResponse response) {
		MsgUtil.sendTextMsgToUser("您好，欢迎关注本公众号！！  ‘？’调取帮助信息", openId,formUser,response);
	}

	/**
	 * 删除菜单
	 */
	@Override
	public boolean deleteMenu() {
		String url = WeiXinUtil.DELETE_MENU_URL.replace("ACCESS_TOKEN", WeiXinUtil.getAccessToken());
		JSONObject result = HttpUtil.doGetstr(url);
        if("ok".equals(result.getString("errmsg"))){
        	logger.info("删除菜单结果：{}", result);
        	return true;
        }
        logger.info("删除菜单结果：{}", result);
		return false;
	}

	/**
	 * 添加自定义菜单
	 */
	@Override
	public boolean menuAdd() {
		String url = WeiXinUtil.ADD_MENU_URL.replace("ACCESS_TOKEN", WeiXinUtil.getAccessToken());
		String menu = JSONObject.fromObject(MsgUtil.initMenu()).toString();
		JSONObject result = HttpUtil.doPoststr(url,menu);
		if("ok".equals(result.getString("errmsg"))){
        	logger.info("添加菜单结果：{}", result);
        	return true;
        }
        logger.info("添加菜单结果：{}", result);
		return false;
	}

}
