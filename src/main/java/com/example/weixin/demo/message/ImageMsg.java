package com.example.weixin.demo.message;

import java.util.Date;

public class ImageMsg extends BaseMessage{

	/**
	 * 注意首字母一定大写
	 */
	private Image Image;
	public ImageMsg(){
		this.setMsgType("image");
		this.setCreateTime(new Date().getTime());
	}
	public Image getImage() {
		return Image;
	}
	public void setImage(Image image) {
		this.Image = image;
	}
	
	
}
