package it.polimi.tiw.projects.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.projects.beans.Exam;

public class ExamDAO {
	private Connection connection;
	
	public ExamDAO(Connection con) {
		this.connection = con;
	}
	
	public String getExamDate(int examId) throws SQLException {
		String data = null;
		String query = "SELECT DataAppello FROM appello WHERE CodiceAppello = ?";
		try(PreparedStatement statement = connection.prepareStatement(query)){
			statement.setInt(1, examId);
			try(ResultSet res = statement.executeQuery()){
				if(res.next()) {
					data = res.getString("DataAppello");
				}
			}
		}
		return data;
	}
	
	public String getCourseName(int examId) throws SQLException {
		String corso = null;
		String query = "SELECT NomeCorso FROM appello join corso on appello.CodiceCorso = corso.CodiceCorso WHERE CodiceAppello = ?";
		try(PreparedStatement statement = connection.prepareStatement(query)){
			statement.setInt(1, examId);
			try(ResultSet res = statement.executeQuery()){
				if(res.next()) {
					corso = res.getString("NomeCorso");
				}
			}
		}
		return corso;
	}
	
	public List<Exam> getExamsByCourse(int courseId) throws SQLException{
		List<Exam> exams = new ArrayList<>();
		String query = "SELECT * FROM appello WHERE CodiceCorso=? ORDER BY DataAppello DESC";
		try(PreparedStatement statement = connection.prepareStatement(query)){
			statement.setInt(1, courseId);
			try(ResultSet res = statement.executeQuery()){
				while(res.next()) {
					Exam e = new Exam();
					e.setCode(res.getInt("CodiceAppello"));
					e.setCourse(res.getInt("CodiceCorso"));
					e.setDate(res.getString("DataAppello"));
					exams.add(e);
				}
			}
		}
		
		return exams;
	}
	
	public List<Exam> getExamsByCourseAndStud(int courseId, int studId) throws SQLException{
		List<Exam> exams = new ArrayList<>();
		String query = "SELECT * FROM appello AS a join iscrizione as i on a.CodiceAppello = i.CodiceAppello WHERE a.CodiceCorso = ? AND i.MatricolaStudente = ? ORDER BY DataAppello DESC";
		try(PreparedStatement statement = connection.prepareStatement(query)){
			statement.setInt(1, courseId);
			statement.setInt(2,  studId);
			try(ResultSet res = statement.executeQuery()){
				while(res.next()) {
					Exam e = new Exam();
					e.setCode(res.getInt("CodiceAppello"));
					e.setCourse(res.getInt("CodiceCorso"));
					e.setDate(res.getString("DataAppello"));
					exams.add(e);
				}
			}
		}
		
		return exams;
	}
}
