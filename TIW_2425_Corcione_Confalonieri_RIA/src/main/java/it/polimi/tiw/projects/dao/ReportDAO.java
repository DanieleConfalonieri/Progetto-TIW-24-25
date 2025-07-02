package it.polimi.tiw.projects.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.projects.beans.Registration;
import it.polimi.tiw.projects.beans.Report;
import it.polimi.tiw.projects.beans.StatusGrade;
import it.polimi.tiw.projects.beans.Student;

public class ReportDAO {
	private Connection connection;
	
	public ReportDAO(Connection connection) {
		this.connection = connection;
	}
	
	/*
	 * verbalizza i voti nello stato PUBBLICATO. I voti rifiutati sono verbalizzati come RIMANDATO.
	 * Crea una nuova tupla nella tabella verbale. Aggiorna le iscrizioni degli studenti coinvolti con il verbale nuovo.
	 */
	public Report registerPublishedGrades(int examId) throws SQLException {
	    int newId = getId();
	    String query = "SELECT MatricolaDocente FROM appello join corso on appello.CodiceCorso = corso.CodiceCorso WHERE appello.CodiceAppello = ?";
	    String query3 = "INSERT into verbale VALUES (?, ?, ?, ?, ?)";
	    String query2 = "UPDATE iscrizione set Voto = 'RIMANDATO', CodiceVerbale = ? WHERE CodiceAppello = ? and StatoVoto = 'RIFIUTATO'";
	    String query1 = "UPDATE iscrizione set CodiceVerbale = ? WHERE CodiceAppello = ? and StatoVoto = 'PUBBLICATO'";
	    String query4 = "UPDATE iscrizione set StatoVoto = 'VERBALIZZATO' WHERE CodiceVerbale = ?";

	    connection.setAutoCommit(false);
	    Report result = new Report();

	    try (PreparedStatement pStatement = connection.prepareStatement(query);
	         PreparedStatement pStatement3 = connection.prepareStatement(query3);
	         PreparedStatement pStatement2 = connection.prepareStatement(query2);
	         PreparedStatement pStatement1 = connection.prepareStatement(query1);
	    	 PreparedStatement pStatement4 = connection.prepareStatement(query4)) {

	        pStatement.setInt(1, examId);
	        int teacherId = 0;
	        try (ResultSet r = pStatement.executeQuery()) {
	            if (r.next()) {
	                teacherId = r.getInt("MatricolaDocente");
	            }
	        }

	        // Inserisci verbale
	        pStatement3.setInt(1, newId);
	        java.util.Date utilDate = new java.util.Date();
	        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
	        java.sql.Time sqlTime = new java.sql.Time(utilDate.getTime());
	        pStatement3.setDate(2, sqlDate);
	        pStatement3.setTime(3, sqlTime);
	        pStatement3.setInt(4, teacherId);
	        pStatement3.setInt(5, examId);
	        pStatement3.executeUpdate();
	        //connection.commit();

	        // Aggiorna iscrizioni PUBBLICATO
	        pStatement1.setInt(1, newId);
	        pStatement1.setInt(2, examId);
	        pStatement1.executeUpdate();
	        //connection.commit();

	        // Aggiorna iscrizioni RIFIUTATO
	        pStatement2.setInt(1, newId);
	        pStatement2.setInt(2, examId);
	        pStatement2.executeUpdate();
	        //connection.commit();
	        
	        // Aggiorna StatoVoto RIMANDATI
	        pStatement4.setInt(1, newId);
	        pStatement4.executeUpdate();

	        connection.commit();

	        result.setReportCode(newId);
	        result.setDate(sqlDate.toString());
	        result.setTime(sqlTime.toString());
	        result.setTeacher(teacherId);
	        result.setExam(examId);
	    } catch (SQLException e) {
	        connection.rollback();
	        throw e;
	    } finally {
	        connection.setAutoCommit(true);
	    }

	    return result;
	}

	/*
	 * dati che servono per mostrare il dettaglio del verbale: nome studente, cognome studente, matricola studente, voto.
	 * La Registration ha uno Student dentro
	 */
	public List<Registration> getStudentsByReport(int reportCode) throws SQLException {
		List<Registration> result = new ArrayList<>();	
		String query = "SELECT * FROM iscrizione JOIN studente ON studente.MatricolaStudente=iscrizione.MatricolaStudente WHERE iscrizione.CodiceVerbale = ?";
		try (PreparedStatement statement = connection.prepareStatement(query)){
			statement.setInt(1, reportCode);
			try(ResultSet res = statement.executeQuery()){
				while(res.next()) {
					Student s = new Student();
					s.setId(res.getInt("MatricolaStudente"));
					s.setName(res.getString("Nome"));
					s.setSurname(res.getString("Cognome"));
					s.setEmail(res.getString("Mail"));
					s.setDegree(res.getString("CorsoLaurea"));
					Registration r = new Registration();
					r.setStudent(s);
					r.setExamCode(res.getInt("CodiceAppello"));
					r.setGrade(res.getString("Voto")!=null ? res.getString("Voto") : "-");
					r.setStatusGrade(StatusGrade.getStatusGradeFromString(res.getString("StatoVoto")));
					r.setReportCode(reportCode);
					result.add(r);
				}
			}
		}
		
		return result;
	}
	
	public List<Report> getReportsByTeacher(int teacherId) throws SQLException {
		List<Report> result = new ArrayList<>();
		String query = "SELECT * FROM verbale join appello join corso on verbale.CodiceAppello=appello.CodiceAppello and appello.CodiceCorso=corso.CodiceCorso"
				+ " WHERE CodiceDocente = ? ORDER BY corso.NomeCorso DESC, appello.DataAppello ASC";
		try (PreparedStatement statement = connection.prepareStatement(query)){
			statement.setInt(1, teacherId);
			try(ResultSet res = statement.executeQuery()){
				while(res.next()) {
					Report s = new Report();
					s.setReportCode(res.getInt("CodiceVerbale"));
					s.setDate(res.getString("DataVerbale"));
					s.setTime(res.getString("Ora"));
					s.setTeacher(res.getInt("CodiceDocente"));
					s.setExam(res.getInt("CodiceAppello"));
					s.setCourseName(res.getString("NomeCorso"));
					s.setExamDate(res.getString("DataAppello"));
					result.add(s);
				}
			}
		}
		
		return result;
	}
	
	private int getId() {
		int result;
		String query = "SELECT max(CodiceVerbale) as maxCodice FROM verbale";
		try (PreparedStatement statement = connection.prepareStatement(query)){
			try(ResultSet r = statement.executeQuery()) {
				if(r.next()) {
					result = r.getInt("maxCodice");
				}
				else {
					result = 0;
				}
			}
		} catch (SQLException e) {
			result = 0;
			e.printStackTrace();
		}
		
		return result + 1;
	}
}