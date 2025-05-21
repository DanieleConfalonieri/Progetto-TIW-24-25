package it.polimi.tiw.projects.beans;

public class Course {
	private int courseCode;
	private String name;
	private int teacher;
	
	public void setCode(int courseCode) {
		this.courseCode = courseCode;
	}
	
	public int getCode() {
		return courseCode;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setTeacher(int teacher) {
		this.teacher = teacher;
	}
	
	public int getTeacher() {
		return teacher;
	}
}
