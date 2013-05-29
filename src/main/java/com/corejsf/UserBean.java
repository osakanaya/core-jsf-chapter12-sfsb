package com.corejsf;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@Named("user")
@SessionScoped
@Stateful
public class UserBean implements Serializable {

	private static final Logger logger = Logger.getLogger("com.corejsf");
	private static final long serialVersionUID = 1L;

	private String name;
	private String password;
	private int count;
	private boolean loggedIn;

	@PersistenceContext(unitName="default")
	private EntityManager em;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getCount() {
		return count;
	}
	
	public String login() {
		try {
			doLogin();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "login failed", e);
			
			return "internalError";
		}
		
		if(loggedIn) {
			return "loginSuccess";
		} else {
			return "loginFailure";
		}
	}
	
	public String logout() {
		loggedIn = false;
		name = "";
		password = "";
		
		return "login";
	}
	
	private void doLogin() {
		TypedQuery<Credential> query = em.createQuery(
				"SELECT c FROM Credential c WHERE c.username = :username",
				Credential.class);
		query.setParameter("username", name);
		List<Credential> result = query.getResultList();
		
		if (result.size() == 1) {
			Credential credential = result.get(0);
			loggedIn = credential.getPassword().equals(password);
			count = credential.incrementLoginCount();
		}
	}
}
