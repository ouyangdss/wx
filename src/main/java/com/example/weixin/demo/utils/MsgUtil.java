package com.example.weixin.demo.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.weixin.demo.code.ActionInfo;
import com.example.weixin.demo.code.PermanentCode;
import com.example.weixin.demo.code.SceneId;
import com.example.weixin.demo.code.SceneStr;
import com.example.weixin.demo.code.TemporaryCode;
import com.example.weixin.demo.menu.Button;
import com.example.weixin.demo.menu.ClickButton;
import com.example.weixin.demo.menu.Menu;
import com.example.weixin.demo.menu.SendLocalButton;
import com.example.weixin.demo.menu.SendPicButton;
import com.example.weixin.demo.menu.ViewButton;
import com.example.weixin.demo.message.BaseMessage;
import com.example.weixin.demo.message.Image;
import com.example.weixin.demo.message.ImageMsg;
import com.example.weixin.demo.message.News;
import com.example.weixin.demo.message.NewsMsg;
import com.example.weixin.demo.message.TextMsg;
import com.example.weixin.demo.template.TempData;
import com.example.weixin.demo.template.TemplateMsg;

import net.sf.json.JSONObject;

public class MsgUtil {
	private static Logger logger = LoggerFactory.getLogger(MsgUtil.class);
	
	/**
	 * 组装菜单
	 * @return
	 */
	public static Menu initMenu(){
		Menu menu = new Menu();
		ViewButton button11 = new ViewButton();
		//注意按钮名字不要太长，不然会报40018错误
		button11.setName("搜索");
		button11.setType("view");
		button11.setUrl("https://www.baidu.com");
		//注意链接不要少了https://  否则会报错40055
		
		SendPicButton button21 = new SendPicButton();
		button21.setName("发图");
		button21.setType("pic_photo_or_album");
		button21.setKey("pic");
		
		SendLocalButton button32 = new SendLocalButton();
		button32.setName("发位置");
		button32.setType("location_select");
		button32.setKey("local");
		
		ClickButton button31 = new ClickButton();
		button31.setName("点赞");
		button31.setType("click");
		button31.setKey("strtest");//事件key
		
		Button button = new Button();
		button.setName("click2");
		button.setSub_button(new Button[]{button31,button32});
		button.setSub_button(new Button[]{button31,button32});
		
		menu.setButton(new Button[]{button11,button21,button});
		return menu;
	}

	/**
	 * 发送文本消息
	 * @param msg
	 * @param toUser
	 */
	public static void sendTextMsgToUser(String msg, String toUser,String formUser,HttpServletResponse resp) {
		TextMsg textMsg = new TextMsg();
		textMsg.setContent(msg);
		textMsg.setToUserName(toUser);
		textMsg.setFromUserName(formUser);
		sendMsg(resp, textMsg);
	}
	
	/**
	 * 发送图片消息
	 * @param openId
	 * @param formUser
	 * @param response
	 */
	public static void sendImageMsgToUser(String openId, String formUser, HttpServletResponse response,String mediaId) {
		ImageMsg msg = new ImageMsg();
		Image image = new Image();
		image.setMediaId(mediaId);
		msg.setImage(image);
		msg.setFromUserName(formUser);
		msg.setToUserName(openId);
		sendMsg(response, msg);
	}
	
	/**
	 * 抽取的发送消息的方法
	 * @param resp
	 * @param msg
	 */
	private static void sendMsg(HttpServletResponse response, BaseMessage msg) {
		//将封装的消息转为xml
		String textMessageToXml = XMLUtils.textMessageToXml(msg);
		logger.info("发送的消息是-------"+textMessageToXml);
		PrintWriter out=null;
		try {
			out = response.getWriter();
			//写入返回的response中
			out.println(textMessageToXml);
		} catch (IOException e) {
			logger.error("发送微信消息失败",e);
			e.printStackTrace();
		}finally{
			//注意关流，不然会发送失败
			out.close();
		}
	}

	/**
	 * 发送图文消息
	 * @param openId
	 * @param formUser
	 * @param response
	 */
	public static void sendNewsMsgToUser(String openId, String formUser, HttpServletResponse response) {
		NewsMsg msg = new NewsMsg();
		List<News> articles = new ArrayList<News>();
		News news = new News();
		news.setTitle("猫和老鼠");
		news.setDescription("《猫和老鼠》以闹剧为特色，描绘了一对水火不容的冤家：汤姆和杰瑞猫鼠之间的战争，片中的汤姆经常使用狡诈的诡计来对付杰瑞，而杰瑞则时常利用汤姆诡计中的漏洞逃脱他的迫害并给予报复");
		news.setPicUrl(WeiXinUtil.NATURL+"/wx-demo/jerry.jpg");
		news.setUrl("https://image.baidu.com/search/index?tn=baiduimage&ct=201326592&lm=-1&cl=2&ie=gb18030&word=jerry&fr=ala&ala=1&alatpl=adress&pos=0&hs=2&xthttps=111111");
		articles.add(news);
		msg.setArticleCount(articles.size());
		msg.setArticles(articles );
		msg.setFromUserName(formUser);
		msg.setToUserName(openId);
		String textMessageToXml = XMLUtils.newsMessageToXml(msg);
		logger.info("发送的消息是-------"+textMessageToXml);
		PrintWriter out=null;
		try {
			out = response.getWriter();
			//写入返回的response中
			out.println(textMessageToXml);
		} catch (IOException e) {
			logger.error("发送微信消息失败",e);
			e.printStackTrace();
		}finally{
			//注意关流，不然会发送失败
			out.close();
		}
	}

	/**
	 * 初始化模板消息
	 * @param openId
	 * @param response
	 */
	public static TemplateMsg initTemplateMsg(String openId, HttpServletResponse response) {
		Map<String,TempData> m = new HashMap<String,TempData>();  
		TempData first = new TempData();  
		first.setColor("#000000");  
		first.setValue("您好，您有一条新通知。");  
		m.put("first", first);  
		TempData keyword1 = new TempData();  
		keyword1.setColor("#328392");  
		keyword1.setValue("小猪");
		m.put("keyword1", keyword1);  
		TempData keyword2 = new TempData();  
		keyword2.setColor("#328392");  
		keyword2.setValue("2014-10-19");  
		m.put("keyword2", keyword2);  
		TempData keyword3 = new TempData();  
		keyword3.setColor("#328392");  
		keyword3.setValue("您已经成功绑定");  
		m.put("keyword3", keyword3);  
		TempData remark = new TempData();  
		remark.setColor("#929232");  
		remark.setValue("请确认绑定信息");  
		m.put("remark", remark);
		TemplateMsg template = new TemplateMsg();  
		template.setUrl("www.baidu.com"); //模板详情链接
		template.setTouser(openId);  //接收者
		template.setTopcolor("#000000");  
		template.setTemplate_id("rcfRlSI6MnDAgFjVVkQtI2FpPWf9dXmODfUKJddlxyE");  //模板id
		template.setData(m);
		return template;
	}

	public static void sendTemplateMsgToUser(String openId, HttpServletResponse response) {
		String msg = JSONObject.fromObject(MsgUtil.initTemplateMsg(openId, response)).toString();
		System.out.println("封装的模板消息是"+ msg);
		JSONObject result = HttpUtil.doPoststr(WeiXinUtil.SEND_TEMPLATE_MSG.replace("ACCESS_TOKEN", WeiXinUtil.getAccessToken()),msg);
		if("ok".equals(result.getString("errmsg"))){
			System.out.println("发送的结果是"+result);
        }
	}

	/**
	 * 生成临时的以字符串为参数的二维码
	 * @return
	 */
	public static TemporaryCode<?> initStrCode() {
		TemporaryCode<Object> code = new TemporaryCode<>();
		ActionInfo<Object> actionInfo = new ActionInfo<>();
		SceneStr scene = new SceneStr();
		scene.setScene_str("test");
		actionInfo.setScene(scene);
		code.setAction_info(actionInfo);
		code.setAction_name("QR_STR_SCENE");
		code.setExpire_seconds(604800);//过期时间
		return code;
	}
	/**
	 * 生成临时的以字符串为参数的二维码
	 * @return
	 */
	public static TemporaryCode<?> initIdCode() {
		TemporaryCode<Object> code = new TemporaryCode<>();
		ActionInfo<Object> actionInfo = new ActionInfo<>();
		SceneId scene = new SceneId();
		scene.setScene_id(123);
		actionInfo.setScene(scene);
		code.setAction_info(actionInfo);
		code.setAction_name("QR_STR_SCENE");
		code.setExpire_seconds(604800);//过期时间
		return code;
	}
	/**
	 * 生成永久二维码 scene_id
	 */
	public static PermanentCode<?> initIdCodePer() {
		PermanentCode<Object> code = new PermanentCode<>();
		ActionInfo<Object> actionInfo = new ActionInfo<>();
		SceneId scene = new SceneId();
		scene.setScene_id(123);
		actionInfo.setScene(scene);
		code.setAction_info(actionInfo);
		code.setAction_name("QR_LIMIT_STR_SCENE");
		return code;
	}
	/**
	 * 生成永久二维码 scene_str
	 * @return
	 */
	public static PermanentCode<?> initStrCodePer() {
		PermanentCode<Object> code = new PermanentCode<>();
		ActionInfo<Object> actionInfo = new ActionInfo<>();
		SceneStr scene = new SceneStr();
		scene.setScene_str("test");
		actionInfo.setScene(scene);
		code.setAction_info(actionInfo);
		code.setAction_name("QR_LIMIT_STR_SCENE");
		return code;
	}

}
