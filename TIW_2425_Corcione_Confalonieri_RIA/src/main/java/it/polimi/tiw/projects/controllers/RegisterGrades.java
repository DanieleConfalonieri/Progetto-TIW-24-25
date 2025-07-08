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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.polimi.tiw.projects.beans.Registration;
import it.polimi.tiw.projects.beans.Report;
import it.polimi.tiw.projects.beans.Teacher;
import it.polimi.tiw.projects.dao.ExamDAO;
import it.polimi.tiw.projects.dao.RegistrationDAO;
import it.polimi.tiw.projects.dao.ReportDAO;

/**
 * Servlet implementation class RegisterGrades
 */
@WebServlet("/RegisterGrades")
public class RegisterGrades extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RegisterGrades() {
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
			throw new UnavailableException("Impossibile caricare il database driver.");
		} catch (SQLException e) {
			throw new UnavailableException("Impossibile ottenere la connessione al database.");
		}
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// controllo parametri
		int examId;
		try {
			examId = Integer.parseInt(request.getParameter("examId"));
		} catch (NumberFormatException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Parametri mancanti o errati.");
	        return;
		}
		
		Integer actualTeacherId = 0;
		try {
			HttpSession session = request.getSession();
			Teacher t = (Teacher) session.getAttribute("user");
			actualTeacherId = t.getId();
		} catch (NullPointerException e) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			response.getWriter().println("Utente non autenticato.");
			return;
		}
		
		// verbalizzazione voti
		ReportDAO rDao = new ReportDAO(connection);
		ExamDAO eDao = new ExamDAO(connection);
		Report report;
		try {
			Integer teacherId = eDao.getTeacher(examId);
			if(teacherId == null || !teacherId.equals(actualTeacherId)) {
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				response.getWriter().println("L'appello e il docente non corrispondono.");
				return;
			}
			report = rDao.registerPublishedGrades(examId);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
			response.getWriter().println("Impossibile aggiornare il database.");
			return;
		} catch (RuntimeException e) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			response.getWriter().println(e.getMessage());
			return;
		}
		List<Registration> students = new ArrayList<>();
		try {
			students = rDao.getStudentsByReport(report.getReportCode());
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
			response.getWriter().println("Errore nell'estrazione del verbale dal database.");
			return;
		}
		
		Gson gson = new GsonBuilder().setDateFormat("yyyy MMM dd").create();
		Map<String, Object> reportData = new HashMap<>();
		reportData.put("report", report);
		reportData.put("students", students);
		String json = gson.toJson(reportData);
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
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
