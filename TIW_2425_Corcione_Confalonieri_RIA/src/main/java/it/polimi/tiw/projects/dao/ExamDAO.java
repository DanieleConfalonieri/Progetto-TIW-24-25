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
	
	/*
	 * dato il codice di un appello, ritorna la matricola del docente del corso relativo.
	 */
	public Integer getTeacher(int examId) throws SQLException {
		Integer result = null;
		String query = "SELECT MatricolaDocente FROM appello join corso on appello.CodiceCorso = corso.CodiceCorso WHERE appello.CodiceAppello = ?";
		try(PreparedStatement statement = connection.prepareStatement(query)){
			statement.setInt(1, examId);
			try(ResultSet res = statement.executeQuery()){
				if(res.next()) {
					result = Integer.parseInt(res.getString("MatricolaDocente"));
				}
			}
		}
		return result;
	}
	
	public boolean validExamForCourse(int courseId, int examId) throws SQLException {
		boolean result = false;
		String query = "SELECT * FROM appello WHERE CodiceCorso=? and CodiceAppello = ?";
		try(PreparedStatement statement = connection.prepareStatement(query)){
			statement.setInt(1, courseId);
			statement.setInt(2, examId);
			try(ResultSet res = statement.executeQuery()){
				if(res.next()) result = true;
				else result = false;
			}
		}
		return result;
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
	
	public List<Exam> getExamsByCourseAndStud(int courseId, int studId) throws SQLException, RuntimeException {
		boolean valid;
		String query1 = "SELECT * FROM frequenza WHERE CodiceCorso = ? and MatricolaStudente = ?";
		try(PreparedStatement statement1 = connection.prepareStatement(query1)){
			statement1.setInt(1, courseId);
			statement1.setInt(2,  studId);
			try(ResultSet res = statement1.executeQuery()){
				if(!res.next()) throw new RuntimeException("Lo studente non e' iscritto a questo corso.");
			}
		}
		
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
