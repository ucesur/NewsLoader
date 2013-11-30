package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import play.db.jpa.GenericModel;
import play.db.jpa.Model;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

@Entity
public class Article extends Model {
	
	public String title;
	public String author;
	public String description;
	public String link;
	public String pubDate;
	public String content;
	
	public Article() {

	}

}
