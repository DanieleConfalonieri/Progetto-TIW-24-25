package it.polimi.tiw.projects.beans;

public class Exam {
	private int examCode;
	private String date;
	private int courseCode;
	
	public void setCode(int examCode) {
		this.examCode = examCode;
	}
	
	public int getCode() {
		return examCode;
	}
	
	public void setDate(String date) {
		this.date = date;
	}
	
	public String getDate() {
		return date;
	}
	
	public void setCourse(int courseCode) {
		this.courseCode = courseCode;
	}
	
	public int getCourse() {
		return courseCode;
	}
}
