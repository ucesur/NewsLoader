package models;

import javax.persistence.*;

import play.db.jpa.GenericModel;
import play.db.jpa.Model;

import controllers.TagFetcher;

@Entity
public class Tag extends Model {
	public String tag;
	public long article_id;
	
	public Tag() {
	}

	public void tagFetcher(String url) {
		this.tag = TagFetcher.getTags(url);
	}
}
