package it.polimi.tiw.projects.beans;

public class Registration {
	private Student student;
	private int examCode;
	private String grade;
	private StatusGrade statusGrade;
	
	public void setStudent(Student student) {
		this.student = student;
	}
	
	public Student getStudent() {
		return student;
	}
	
	public void setExamCode(int examCode) {
		this.examCode = examCode;
	}
	
	public int getExamCode() {
		return examCode;
	}
	
	public void setGrade(String grade) {
		this.grade = grade;
	}
	
	public String getGrade() {
		return grade;
	}
	
	public void setStatusGrade(StatusGrade statusGrade) {
		this.statusGrade = statusGrade;
	}
	
	public StatusGrade getStatusGrade() {
		return statusGrade;
	}
}
