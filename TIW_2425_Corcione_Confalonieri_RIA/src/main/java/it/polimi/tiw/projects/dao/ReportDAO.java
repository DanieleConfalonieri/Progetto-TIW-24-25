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
	public Report registerPublishedGrades(int examId) throws SQLException, RuntimeException {
	    int newId = getId();
	    String getTeacherId = "SELECT MatricolaDocente FROM appello join corso on appello.CodiceCorso = corso.CodiceCorso WHERE appello.CodiceAppello = ?";
	    String verbalizable = "SELECT * FROM iscrizione WHERE CodiceAppello = ? and (StatoVoto = 'PUBBLICATO' or StatoVoto = 'RIFIUTATO')";
	    String createReport = "INSERT into verbale VALUES (?, ?, ?, ?, ?)";
	    String setRimandato = "UPDATE iscrizione set Voto = 'RIMANDATO', CodiceVerbale = ? WHERE CodiceAppello = ? and StatoVoto = 'RIFIUTATO'";
	    String setPubblicato = "UPDATE iscrizione set CodiceVerbale = ? WHERE CodiceAppello = ? and StatoVoto = 'PUBBLICATO'";
	    String setVerbalizzato = "UPDATE iscrizione set StatoVoto = 'VERBALIZZATO' WHERE CodiceVerbale = ?";

	    connection.setAutoCommit(false);
	    Report result = new Report();

	    try (PreparedStatement pStatementTeacherId = connection.prepareStatement(getTeacherId);
	    	 PreparedStatement pStatementVerbalizable = connection.prepareStatement(verbalizable);
	         PreparedStatement pStatementCreateReport = connection.prepareStatement(createReport);
	         PreparedStatement pStatementRimandato = connection.prepareStatement(setRimandato);
	         PreparedStatement pStatementPubblicato = connection.prepareStatement(setPubblicato);
	    	 PreparedStatement pStatementVerbalizzato = connection.prepareStatement(setVerbalizzato)) {

	        pStatementTeacherId.setInt(1, examId);
	        int teacherId = 0;
	        try (ResultSet r = pStatementTeacherId.executeQuery()) {
	            if (r.next()) {
	                teacherId = r.getInt("MatricolaDocente");
	            }
	        }
	        
	        // Verifica se ci sono voti da verbalizzare
	        pStatementVerbalizable.setInt(1, examId);
	        ResultSet res = pStatementVerbalizable.executeQuery();
	        if(!res.next()) {
	        	connection.setAutoCommit(true);
	        	throw new RuntimeException("Nessun voto da verbalizzare per questo appello.");
	        }

	        // Inserisci verbale
	        pStatementCreateReport.setInt(1, newId);
	        java.util.Date utilDate = new java.util.Date();
	        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
	        java.sql.Time sqlTime = new java.sql.Time(utilDate.getTime());
	        pStatementCreateReport.setDate(2, sqlDate);
	        pStatementCreateReport.setTime(3, sqlTime);
	        pStatementCreateReport.setInt(4, teacherId);
	        pStatementCreateReport.setInt(5, examId);
	        pStatementCreateReport.executeUpdate();

	        // Aggiorna iscrizioni PUBBLICATO
	        pStatementPubblicato.setInt(1, newId);
	        pStatementPubblicato.setInt(2, examId);
	        pStatementPubblicato.executeUpdate();

	        // Aggiorna iscrizioni RIFIUTATO
	        pStatementRimandato.setInt(1, newId);
	        pStatementRimandato.setInt(2, examId);
	        pStatementRimandato.executeUpdate();
	        
	        // Aggiorna StatoVoto RIMANDATI
	        pStatementVerbalizzato.setInt(1, newId);
	        pStatementVerbalizzato.executeUpdate();

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