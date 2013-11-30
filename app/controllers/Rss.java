package controllers;

import java.util.ArrayList;
import java.util.List;

import common.Cache433;
import common.NewsLoader;

import models.Article;
import models.Tag;
import play.mvc.Controller;

public class Rss extends Controller {

	private static Cache433 getCurrentCache() {
		return Cache433.getCSInstance();
	}

	public static void read() {
		List<Article> articles = (List<Article>) getCurrentCache().getCacheItem("articles");
		if(articles == null){
			articles = Article.find("").from(0).fetch(30);
			getCurrentCache().setCacheItem("articles", articles, 25000);
		}
		
		render(articles);
	}
	
	public static void readNext(int i) {
		List<Article> articles = (List<Article>) getCurrentCache().getCacheItem("articles_"+i);
		
		if(articles == null){
			articles = Article.find("").from(0+i*30).fetch(30+i*30);
			getCurrentCache().setCacheItem("articles_"+i, articles, 25000);
		}
		i++;
		
		render(articles,i);
	}


	public static void showContent(int in) {
		Article article = (Article) getCurrentCache().getCacheItem("article_"+in);
		List<Tag> tags = (List<Tag>) getCurrentCache().getCacheItem("tags");
		
		if(article == null){
			article = Article.find("id = ?", (long)in).first();
			getCurrentCache().setCacheItem("article_"+in, article, 25000);
		}
		if(tags == null){
			tags = Tag.find("article_id = ?",(long)in).fetch();
			getCurrentCache().setCacheItem("tags", tags, 25000);
		}
			
		render(article, tags);
	}
}
