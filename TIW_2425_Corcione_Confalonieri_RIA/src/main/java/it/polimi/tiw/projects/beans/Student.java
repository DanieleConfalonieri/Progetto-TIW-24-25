package it.polimi.tiw.projects.beans;

public class Student {
	private int id;
	private String email;
	private String name;
	private String surname;
	private String degree;
	//private String password;  ??? unsafe
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setSurname(String surname) {
		this.surname = surname;
	}
	
	public String getSurname() {
		return surname;
	}
	
	public void setDegree(String degree) {
		this.degree = degree;
	}
	
	public String getDegree() {
		return degree;
	}
}
