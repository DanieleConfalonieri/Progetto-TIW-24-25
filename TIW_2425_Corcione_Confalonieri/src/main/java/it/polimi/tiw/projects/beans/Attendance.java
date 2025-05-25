package it.polimi.tiw.projects.beans;

public class Attendance {
	private int studentCode;
	private int courseCode;
	
	public void setStudent(int studentCode) {
		this.studentCode = studentCode;
	}
	
	public int getStudent() {
		return studentCode;
	}
	
	public void setCode(int courseCode) {
		this.courseCode = courseCode;
	}
	
	public int getCourse() {
		return courseCode;
	}
}
