package it.polimi.tiw.projects.controllers;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.UnavailableException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polimi.tiw.projects.beans.Registration;
import it.polimi.tiw.projects.beans.StatusGrade;
import it.polimi.tiw.projects.beans.Teacher;
import it.polimi.tiw.projects.dao.ExamDAO;
import it.polimi.tiw.projects.dao.RegistrationDAO;

/**
 * Servlet implementation class UpdateGrades
 */
@WebServlet("/UpdateGrades")
public class UpdateGrades extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Connection connection;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UpdateGrades() {
        super();
    }
    
    public void init() throws ServletException {
		try {
			ServletContext context = getServletContext();
			String driver = context.getInitParameter("dbDriver");
			String url = context.getInitParameter("dbUrl");
			String user = context.getInitParameter("dbUser");
			String password = context.getInitParameter("dbPassword");
			Class.forName(driver);
			connection = DriverManager.getConnection(url, user, password);
		} catch (ClassNotFoundException e) {
			throw new UnavailableException("Can't load database driver");
		} catch (SQLException e) {
			throw new UnavailableException("Couldn't get db connection");
		}
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Map<String, String[]> originalparameters = request.getParameterMap();
		List<String> grades = Arrays.asList("null", "ASSENTE", "RIMANDATO", "RIPROVATO", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "30L");
		if(originalparameters.isEmpty()) { // nessun parametro
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Parametri mancanti.");
			return;
		}
		
		Map<String, String> parameters = new HashMap<>();
		for(String k : originalparameters.keySet()) {
			parameters.put(k,  originalparameters.get(k)[0]);
		}
		
		Integer examId;
		try {
			examId = Integer.parseInt(parameters.get("examCode"));
			parameters.remove("examCode");
			Integer actualTeacherId = 0;
			try {
				HttpSession session = request.getSession();
				Teacher t = (Teacher) session.getAttribute("user");
				actualTeacherId = t.getId();
			} catch (NullPointerException e) { // nella sessione non c'è un utente autenticato
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				response.getWriter().println("Utente non autenticato.");
				System.out.println("Utente non autenticato.");
				return;
			}
			
			if(parameters.values().stream().allMatch(s -> s.equals("") || s==null)) { // non ci sono voti da aggiornare
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("Parametri mancanti.");
				return;
			}
			
			ExamDAO eDao = new ExamDAO(connection);
			RegistrationDAO rDao = new RegistrationDAO(connection);
			for(String studId : parameters.keySet()) { // controllo di tutti i parametri prima di eseguire qualsiasi update
				String newGrade = parameters.get(studId);
				int intStudId = Integer.parseInt(studId);
				if(newGrade!=null && !newGrade.equals("") && grades.contains(newGrade.toUpperCase())) { // sono ammessi alcuni voti vuoti
					try {
						Integer teacherId = eDao.getTeacher(examId);
						if(teacherId == null || !teacherId.equals(actualTeacherId)) { // il docente autenticato è diverso da quello relativo al corso a cui appartiene l'appello per cui si vogliono aggiornare i voti
							response.setStatus(HttpServletResponse.SC_FORBIDDEN);
							response.getWriter().println("L'appello e il docente non corrispondono.");
							return;
						}
						Registration r = rDao.getRegistration(examId, intStudId);
						if(r == null) { // l'iscrizione non è presente, quindi lo studente non è iscritto all'appello per cui si vuole modificare il suo voto.
							response.setStatus(HttpServletResponse.SC_FORBIDDEN);
							response.getWriter().println("Lo studente non e' iscritto all'appello.");
							System.out.println("Lo studente non e' iscritto all'appello. Codice appello: " + examId);
							return;
						}
						if(r.getStatusGrade().equals(StatusGrade.PUBBLICATO) || r.getStatusGrade().equals(StatusGrade.RIFIUTATO) || r.getStatusGrade().equals(StatusGrade.VERBALIZZATO)) { // stati in cui il voto non è più modificabile
							response.setStatus(HttpServletResponse.SC_FORBIDDEN);
							response.getWriter().println("Voto non modificabile.");
							System.out.println("Voto non modificabile. Stato voto: " + r.getStatusGrade());
							return;
						}
					} catch (SQLException e) {
						response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
						response.getWriter().println("Impossibile aggiornare il database.");
						return;
					}
				} else if(newGrade!=null && !newGrade.equals("")){ // il voto non è una stringa valida
					response.setStatus(HttpServletResponse.SC_FORBIDDEN);
					response.getWriter().println("Voto non valido.");
					System.out.println("Voto non valido." + "Voto: " + newGrade);
					return;
				}
			}
			
			try {
				rDao.updateGrades(examId, parameters);
			} catch (SQLException e) {
				response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
				response.getWriter().println("Impossibile aggiornare il database.");
				e.printStackTrace();
				return;
			}
		}
		catch (NumberFormatException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Parametri mancanti.");
	        return;
		}
	}
	
	public void destroy() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException sqle) {
		}
	}

}
