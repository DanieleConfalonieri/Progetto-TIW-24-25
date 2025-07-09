package it.polimi.tiw.projects.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import it.polimi.tiw.projects.beans.Course;

public class CourseDAO {
	private Connection connection;
	
	public CourseDAO(Connection con) {
		this.connection = con;
	}
	
	public List<Course> getCoursesByTeacher (int teacherId) throws SQLException{
		List<Course> courses = new ArrayList<Course>();
		String query = "SELECT * FROM corso WHERE MatricolaDocente = ? ORDER BY NomeCorso DESC";
		PreparedStatement statement = null;
		ResultSet res = null;
		try {
			statement = connection.prepareStatement(query);
			statement.setString(1, String.valueOf(teacherId));
			res = statement.executeQuery();
			while(res.next()) {
				Course c = new Course();
				c.setCode(res.getInt("CodiceCorso"));
				c.setName(res.getString("NomeCorso"));
				c.setTeacher(res.getInt("MatricolaDocente"));
				courses.add(c);
			}
		} finally {
			res.close();
			statement.close();
		}
		return courses;
	}
	
	public Course getCoursesById(int courseId) throws SQLException {
		String query = "SELECT * FROM corso WHERE CodiceCorso = ?";
		PreparedStatement statement = null;
		ResultSet res = null;
		try {
			statement = connection.prepareStatement(query);
			statement.setString(1, String.valueOf(courseId));
			res = statement.executeQuery();
			if(!res.isBeforeFirst()) {
				return null;
			}
			else {
				Course c = new Course();
				res.next();
				c.setCode(res.getInt("CodiceCorso"));
				c.setName(res.getString("NomeCorso"));
				c.setTeacher(res.getInt("MatricolaDocente"));
				return c;
			}
		} finally {
			res.close();
			statement.close();
		}
	}
}
	
