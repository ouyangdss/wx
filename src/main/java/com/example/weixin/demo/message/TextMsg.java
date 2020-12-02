package com.example.weixin.demo.message;

import java.util.Date;

/**
 * 
 * @author Administrator
 *
 */
public class TextMsg extends BaseMessage {
	private String Content;
	private String MsgId;
	public TextMsg(){
		this.setMsgType("text");
		this.setCreateTime(new Date().getTime());
	}
	public String getContent() {
		return Content;
	}
	public void setContent(String content) {
		Content = content;
	}
	public String getMsgId() {
		return MsgId;
	}
	public void setMsgId(String msgId) {
		MsgId = msgId;
	}
	
}
