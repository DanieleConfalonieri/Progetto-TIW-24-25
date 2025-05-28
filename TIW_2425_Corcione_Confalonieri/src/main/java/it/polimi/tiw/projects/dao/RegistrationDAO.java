package it.polimi.tiw.projects.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.projects.beans.Exam;
import it.polimi.tiw.projects.beans.Registration;
import it.polimi.tiw.projects.beans.StatusGrade;
import it.polimi.tiw.projects.beans.Student;

public class RegistrationDAO {
	private Connection connection;
	
	public RegistrationDAO(Connection connection) {
		this.connection = connection;
	}
	
	/*
	 * ritorna lista di studenti iscritti all'appello identificato dal codice examId.
	 * usato dal docente per vedere gli iscritti ad un appello.
	 */
	public List<Registration> getStudentsByExam(int examId) throws SQLException {
		List<Registration> registered = new ArrayList<>();
		String query = "SELECT * FROM studente JOIN iscrizione ON studente.MatricolaStudente=iscrizione.MatricolaStudente WHERE iscrizione.CodiceAppello=?";
		try (PreparedStatement statement = connection.prepareStatement(query)){
			statement.setInt(1, examId);
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
					r.setExam(examId);
					r.setGrade(res.getInt("Voto")); // ??
					r.setStatusGrade(StatusGrade.getStatusGradeFromString(res.getString("StatoVoto")));
					registered.add(r);
				}
			}
		}
		
		return registered;
	}
	
	/*
	 * ritorna lista di appelli a cui uno studente studId è iscritto per il corso course.
	 * usato dallo studente per vedere la lista di iscrizioni effettuate per gli appelli dei suoi corsi.
	 */
	public List<Exam> getExamByStudentCourse(int studId, int course) throws SQLException {
		return null;
	}
	
	/*
	 * permette ad un docente di inserire o modificare il voto di uno studente per un determinato appello.
	 * non restituisce niente, ma ricarica la pagina con la valutazione nuova.
	 */
	public void updateGradeByStudentExam(StatusGrade grade, int studId, int examId) throws SQLException {
		
	}
	
	/*
	 * pubblica i voti nello stato INSERITO.
	 * ricarica la pagina degli ISCRITTI.
	 */
	public void publishInsertedGrades(int examId) throws SQLException {
		
	}
	
	/*
	 * verbalizza i voti nello stato PUBBLICATO.
	 * crea un verbale (visualizzabile in qualche modo subito) e ricarica la pagina ISCRITTI.
	 */
	public void registerPublishedGrades(int examId) throws SQLException {
		
	}
	
	/*
	 * permette ad uno studente di rifiutare il voto di un appello.
	 * ricarica la pagina ESITO dello studente.
	 */
	public void refuseGrade(int examId) throws SQLException {
		
	}
}
