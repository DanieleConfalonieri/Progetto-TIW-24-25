package it.polimi.tiw.projects.beans;

public class Exam {
	private int code;
	private String date;
	private int course;
	
	public void setCode(int code) {
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}
	
	public void setDate(String date) {
		this.date = date;
	}
	
	public String getDate() {
		return date;
	}
	
	public void setCourse(int course) {
		this.course = course;
	}
	
	public int getCourse() {
		return course;
	}
}
