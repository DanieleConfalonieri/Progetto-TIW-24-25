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
}
