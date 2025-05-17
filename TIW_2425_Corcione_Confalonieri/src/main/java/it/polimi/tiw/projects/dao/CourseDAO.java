package it.polimi.tiw.projects.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.projects.beans.Course;

public class CourseDAO {
	private Connection connection;
	
	public CourseDAO(Connection con) {
		this.connection = con;
	}
	
	public List<Course> getCoursesByTeacher (int teacherId) throws SQLException{
		List<Course> courses = new ArrayList<Course>();
		String query = "SELECT * FROM Corso WHERE MatricolaDocente = ?";
		try (PreparedStatement statement = connection.prepareStatement(query);) {
			statement.setString(1, String.valueOf(teacherId));
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
