package it.polimi.tiw.projects.beans;

public class Report {
	private int reportCode;
	private String date;
	private String time;
	private int teacher;
	private int exam;
	
	public void setReportCode(int reportCode) {
		this.reportCode = reportCode;
	}
	
	public int getReportCode() {
		return reportCode;
	}
	
	public void setDate(String date) {
		this.date = date;
	}
	
	public String getDate() {
		return date;
	}
	
	public void setTime(String time) {
		this.time = time;
	}
	
	public String getTime() {
		return time;
	}
	
	public void setTeacher(int teacher) {
		this.teacher = teacher;
	}
	
	public int getTeacher() {
		return teacher;
	}
	
	public void setExam(int exam) {
		this.exam = exam;
	}
	
	public int getExam() {
		return exam;
	}

}
