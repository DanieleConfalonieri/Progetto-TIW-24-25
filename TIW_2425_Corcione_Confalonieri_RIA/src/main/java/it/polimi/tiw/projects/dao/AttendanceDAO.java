package it.polimi.tiw.projects.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.projects.beans.Course;

public class AttendanceDAO {
private Connection connection;
	
	public AttendanceDAO(Connection con) {
		this.connection = con;
	}
	
	public List<Course> getCoursesByStudent (int studentId) throws SQLException{
		List<Course> courses = new ArrayList<>();
		String query = "SELECT * FROM frequenza AS f JOIN corso AS c ON f.CodiceCorso = c.CodiceCorso WHERE f.MatricolaStudente = ? ORDER BY c.NomeCorso desc ";
		try (PreparedStatement statement = connection.prepareStatement(query);) {
			statement.setString(1, String.valueOf(studentId));
			try (ResultSet res = statement.executeQuery();) {
				while(res.next()) {
					Course c = new Course();
					c.setCode(res.getInt("CodiceCorso"));
					c.setName(res.getString("NomeCorso"));
					c.setTeacher(res.getInt("MatricolaDocente"));
					courses.add(c);
				}
			}
		}
		return courses;
	}
}
