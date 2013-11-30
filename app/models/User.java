package models;

import javax.persistence.*;

import play.db.jpa.GenericModel;
import play.db.jpa.Model;


@Entity
public class User  extends Model{
	
	//@Id
	//public long id;
	
	public String name;
	public String surname;

}
