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
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.polimi.tiw.projects.beans.Course;
import it.polimi.tiw.projects.beans.Exam;
import it.polimi.tiw.projects.beans.Registration;
import it.polimi.tiw.projects.beans.StatusGrade;
import it.polimi.tiw.projects.beans.Student;
import it.polimi.tiw.projects.beans.Teacher;
import it.polimi.tiw.projects.dao.CourseDAO;
import it.polimi.tiw.projects.dao.ExamDAO;
import it.polimi.tiw.projects.dao.RegistrationDAO;

/**
 * Servlet implementation class RefuseGrade
 */
@WebServlet("/RefuseGrade")
public class RefuseGrade extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Connection connection;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RefuseGrade() {
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
		int id = 0, courseId = 0, examId = 0;
		try {
			HttpSession s = request.getSession();
			Student std = (Student) s.getAttribute("user");
			id = std.getId();
		} catch (NullPointerException e) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			response.getWriter().println("Utente non autenticato.");
			return;
		}
		try {
			courseId = Integer.parseInt(request.getParameter("courseId"));
			examId = Integer.parseInt(request.getParameter("examId"));
		} catch (NumberFormatException e) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			response.getWriter().println("Parametri mancanti o errati.");
			return;
		}
		
		Registration registration;
		Course course;
		CourseDAO cDao = new CourseDAO(connection);
		RegistrationDAO rDao = new RegistrationDAO(connection);
		ExamDAO eDao = new ExamDAO(connection);
		try {
			if(!eDao.validExamForCourse(courseId, examId)) {
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				response.getWriter().println("L'appello e il corso non corrispondono.");
				return;
			}
			try {
				rDao.refuseGrade(examId, id);
			} catch (RuntimeException e) {
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				response.getWriter().println(e.getMessage());
				return;
			}
			registration = rDao.getRegistration(examId, id);
			course = cDao.getCoursesById(courseId);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			response.getWriter().println("Errore nell'estrazione degli appelli dal database.");
			return;
		}
		Gson gson = new GsonBuilder().setDateFormat("yyyy MMM dd").create();
		String json = gson.toJson(registration);
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
	}
}