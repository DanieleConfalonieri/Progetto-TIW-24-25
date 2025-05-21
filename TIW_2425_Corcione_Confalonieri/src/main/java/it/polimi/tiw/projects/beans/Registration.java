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
	
	public void setExam(int examCode) {
		this.examCode = examCode;
	}
	
	public int getExam() {
		return examCode;
	}
	
	public void setGrade(int intGrade) {
		if(intGrade >= 18 && intGrade <= 30) {
			grade = Integer.toString(intGrade);
		}
		else {
			switch(intGrade) {
				case 1 -> grade = "Assente";
				case 2 -> grade = "Rimandato";
				case 3 -> grade = "Assente";
				case 30 -> grade = "30L";
				default -> grade = "";
			}
		}
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
