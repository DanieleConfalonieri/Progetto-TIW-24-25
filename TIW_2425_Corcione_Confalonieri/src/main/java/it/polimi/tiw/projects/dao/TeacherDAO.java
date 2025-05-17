package it.polimi.tiw.projects.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.polimi.tiw.projects.beans.Teacher;

public class TeacherDAO {
	private Connection connection;
	int id;
	
	public TeacherDAO(Connection con) {
		this.connection = con;
	}
	
	public TeacherDAO(Connection con, int id) {
		this.connection = con;
		this.id = id;
	}
	
	public Teacher checkCredentials(String email, String pwd) throws SQLException {
		String query = "SELECT * FROM docente WHERE Mail=? and PasswordDocente=?";
		try (PreparedStatement statement = connection.prepareStatement(query);) {
			statement.setString(1,  email);
			statement.setString(2, pwd);
			try (ResultSet result = statement.executeQuery();) {
				if (!result.isBeforeFirst()) // no results, credential check failed
					return null;
				else {
					result.next();
					Teacher teacher = new Teacher();
					teacher.setId(result.getInt("MatricolaDocente"));
					teacher.setEmail(result.getString("email"));
					return teacher;
				}
			}
		}
	}

}
