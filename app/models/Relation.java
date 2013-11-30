package models;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class Relation extends Model{
	
	int user_id;
	int article_id;
	
	public Relation() {
	}

}
