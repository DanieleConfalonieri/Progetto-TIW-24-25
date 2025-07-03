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

import it.polimi.tiw.projects.beans.Course;
import it.polimi.tiw.projects.beans.Exam;
import it.polimi.tiw.projects.beans.Registration;
import it.polimi.tiw.projects.beans.StatusGrade;
import it.polimi.tiw.projects.beans.Student;

import java.time.*;

public class RegistrationDAO {
	private Connection connection;
	
	public RegistrationDAO(Connection connection) {
		this.connection = connection;
	}
	
	/*
	 * dato il codice di un appello e la matricola di uno studente, ritorna l'iscrizione associata.
	 * Se non è presente alcuna iscrizione dello studente per l'appello, ritorna null.
	 */
	public Registration getRegistration(int examId, int studId) throws SQLException{
		Registration result = new Registration();
		String query = "SELECT * FROM iscrizione JOIN studente ON studente.MatricolaStudente=iscrizione.MatricolaStudente WHERE CodiceAppello = ? and studente.MatricolaStudente = ?";
		try (PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setInt(1, examId);
			statement.setInt(2, studId);
			try(ResultSet res = statement.executeQuery()){
				if(res.next()) {
					Student s = new Student();
					s.setId(res.getInt("MatricolaStudente"));
					s.setName(res.getString("Nome"));
					s.setSurname(res.getString("Cognome"));
					s.setEmail(res.getString("Mail"));
					s.setDegree(res.getString("CorsoLaurea"));
					result.setStudent(s);
					result.setExamCode(examId);
					result.setGrade(res.getString("Voto"));
					result.setStatusGrade(StatusGrade.getStatusGradeFromString(res.getString("StatoVoto")));
				}
				else {
					result = null;
				}
			}
		}
		
		return result;
	}
	
	public List<Integer> getExamCodeListByExamListAndStud(List<Exam> exams, int studId) throws SQLException{
		List<Integer> examCodes = new ArrayList<Integer>();
		int i;
		for(i=0;i<exams.size();i++) {
			Registration r = getRegistration(exams.get(i).getCode(), studId);
			if(r!= null) {
				examCodes.add(r.getExamCode());
			}
		}
		return examCodes;
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
					r.setExamCode(examId);
					r.setGrade(res.getString("Voto")!=null ? res.getString("Voto") : "-");
					r.setStatusGrade(StatusGrade.getStatusGradeFromString(res.getString("StatoVoto")));
					r.setReportCode(res.getInt("CodiceVerbale"));
					registered.add(r);
				}
			}
		}
		
		return registered;
	}
	
	/*
	 * permette ad un docente di inserire o modificare il voto di uno studente per un determinato appello.
	 */
	public void updateGrade(String grade, int studId, int examId) throws SQLException {
		String query = "UPDATE iscrizione set Voto = ?, StatoVoto = 'INSERITO' WHERE CodiceAppello = ? and MatricolaStudente = ?";
		try (PreparedStatement statement = connection.prepareStatement(query)){
			statement.setString(1, grade);
			statement.setInt(2, examId);
			statement.setInt(3, studId);
			statement.execute();
		}
	}
	
	/*
	 * pubblica i voti nello stato INSERITO per un dato appello.
	 */
	public void publishInsertedGrades(int examId) throws SQLException {
		String query = "UPDATE iscrizione set StatoVoto = 'PUBBLICATO' WHERE CodiceAppello = ? and StatoVoto = 'INSERITO'";
		try (PreparedStatement statement = connection.prepareStatement(query)){
			statement.setInt(1, examId);
			statement.execute();
		}
	}
	
	/*
	 * permette ad uno studente di rifiutare il voto di un appello.
	 */
	public void refuseGrade(int examId, int studId) throws SQLException {
		connection.setAutoCommit(false);
		Registration reg = getRegistration(examId, studId);
		if(reg.isRefusable()) {
			String query = "UPDATE iscrizione set StatoVoto = 'RIFIUTATO' WHERE CodiceAppello = ? and MatricolaStudente = ?";
			try (PreparedStatement statement = connection.prepareStatement(query)){
				statement.setInt(1, examId);
				statement.setInt(2, studId);
				statement.executeUpdate();
				connection.commit();
			}
		}
		else {
			throw new RuntimeException("Non puoi rifiutare il seguente voto");
		}
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
	private int isSorted(List<Registration> list, String column, Comparator<Registration> comparator) {
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


	
	private Comparator<Registration> getComparatorByGivenColumn(String column){
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
			case "ReportCode" -> {
				return Comparator.comparing(t -> t.getReportCode());
			}
			default -> {return null;}
		}
	}
}
