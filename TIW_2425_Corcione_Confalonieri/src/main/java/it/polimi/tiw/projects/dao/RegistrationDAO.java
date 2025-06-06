package it.polimi.tiw.projects.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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
					r.setGrade(res.getString("Voto"));
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
	
	public List<Registration> reorder(List<Registration> list, String column){
		Comparator<Registration> comparator = getComparatorByGivenColumn(column);
		int howIsSorted = isSorted(list, column, comparator);
		if(howIsSorted==0 || howIsSorted==2) {
			Collections.sort(list, comparator);
		}
		else if(howIsSorted == 1) {
			Collections.sort(list, comparator.reversed());
		}
		else {
			throw new RuntimeException("Error: something went wrong while reordering");
		}
		return list;
	}
	/**
	 * This function is used to determine how, a given list of registrations, is ordered.
	 * Returns: 0 if the list is not ordered, 1 if the list is ordered ascending and 2 if the list is ordered descending.
	 * 
	 * @return How the list is ordered
	 */
	public int isSorted(List<Registration> list, String column, Comparator<Registration> comparator) {
		boolean isSortedAscending = true, isSortedDescending = true;
		int len = list.size(), i;
		for(i=0;i<len;i++) {
			if((i+1<len) && (comparator.compare(list.get(i), list.get(i+1))>0)) {
				isSortedAscending = false;
			}
			if((len-i-2>0) && (comparator.compare(list.get(len-i-1), list.get(len-i-2))>0)) {
				isSortedDescending = false;
			}
		}
		if(isSortedAscending) {
			return 1;
		}
		else if(isSortedDescending) {
			return 2;
		}
		else
			return 0;
	}


	
	public Comparator<Registration> getComparatorByGivenColumn(String column){
		switch(column) {
			case "Matricola" -> {
				return Comparator.comparing(t -> t.getStudent().getId());
			}
			case "Email" -> {
				return Comparator.comparing(t -> t.getStudent().getEmail());
			}
			case "Nome" -> {
				return Comparator.comparing(t -> t.getStudent().getName());
			}
			case "Cognome" -> {
				return Comparator.comparing(t -> t.getStudent().getSurname());
			}
			case "Degree" -> {
				return Comparator.comparing(t -> t.getStudent().getDegree());
			}
			case "Grade" -> {
				//return Comparator.comparing(Registration::getGrade);
				return new Comparator<Registration>() {
					public int compare(Registration r1, Registration r2) {
						List grades = Arrays.asList("null", "ASSENTE", "RIMANDATO", "RIPROVATO", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "30L");
						String g1 = r1.getGrade();
						String g2 = r2.getGrade();
						if(g1.isEmpty() || g1==null) return -1;
						if(g2.isEmpty() || g2==null) return 1;
						if(grades.indexOf(g1) < grades.indexOf(g2)) return -1;
						else if(grades.indexOf(g1) > grades.indexOf(g2)) return 1;
						else return 0;
					}
				};
			}
			case "StatusGrade" -> {
				return Comparator.comparing(Registration::getStatusGrade);
			}
			default -> {return null;}
		}
	}
		
	
}
