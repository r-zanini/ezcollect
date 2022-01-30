package models;

import io.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class User extends Model {
	@Id
	public String userName;
	public String password;
	@ManyToOne
	public Store store;

	public User(String userName, String password) {
		this.userName = userName;
		this.password = password;
		store = null;
	}

	public User(String userName, String password, Store store) {
		this.userName = userName;
		this.password = password;
		this.store = store;
	}
}
