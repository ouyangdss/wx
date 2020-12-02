package com.example.weixin.demo.message;

import java.util.Date;
import java.util.List;

public class NewsMsg extends BaseMessage{
	private int ArticleCount;
    private List<News> Articles;
	public NewsMsg(){
		this.setMsgType("news");
		this.setCreateTime(new Date().getTime());
	}
	public int getArticleCount() {
		return ArticleCount;
	}
	public void setArticleCount(int articleCount) {
		ArticleCount = articleCount;
	}
	public List<News> getArticles() {
		return Articles;
	}
	public void setArticles(List<News> articles) {
		Articles = articles;
	}
	
	

}
