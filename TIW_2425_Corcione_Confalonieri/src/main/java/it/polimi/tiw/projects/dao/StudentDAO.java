package it.polimi.tiw.projects.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.polimi.tiw.projects.beans.Student;

public class StudentDAO {
	private Connection connection;
	int id;
	
	public StudentDAO(Connection con) {
		this.connection = con;
	}
	
	public StudentDAO(Connection con, int id) {
		this.connection = con;
		this.id = id;
	}
	
	public Student checkCredentials(String email, String pwd) throws SQLException {
		String query = "SELECT * FROM studente WHERE Mail=? and PasswordStudente=?";
		try (PreparedStatement statement = connection.prepareStatement(query);) {
			statement.setString(1,  email);
			statement.setString(2, pwd);
			try (ResultSet result = statement.executeQuery();) {
				if (!result.isBeforeFirst()) // no results, credential check failed
					return null;
				else {
					result.next();
					Student student = new Student();
					student.setId(result.getInt("MatricolaStudente"));
					student.setEmail(result.getString("Mail"));
					return student;
				}
			}
		}
	}

}
