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
import java.util.List;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import it.polimi.tiw.projects.beans.Registration;
import it.polimi.tiw.projects.beans.StatusGrade;
import it.polimi.tiw.projects.beans.Teacher;
import it.polimi.tiw.projects.dao.ExamDAO;
import it.polimi.tiw.projects.dao.RegistrationDAO;

/**
 * Servlet implementation class UpdateGrade
 */
@WebServlet("/UpdateGrade")
public class UpdateGrade extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Connection connection;
	private TemplateEngine templateEngine;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UpdateGrade() {
        super();
    }
    
    public void init() throws ServletException {
    	ServletContext servletContext = getServletContext();
		JakartaServletWebApplication webApplication = JakartaServletWebApplication.buildApplication(servletContext);
	    WebApplicationTemplateResolver templateResolver = new WebApplicationTemplateResolver(webApplication);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
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
		String path = getServletContext().getContextPath();
		int examId = 0, studId = 0;
		String newGrade = null;
		try {
			examId = Integer.parseInt(request.getParameter("examId"));
			studId = Integer.parseInt(request.getParameter("studId"));
			newGrade = request.getParameter("newGrade");
			List<String> grades = Arrays.asList("null", "ASSENTE", "RIMANDATO", "RIPROVATO", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "30L");
			newGrade = newGrade.toUpperCase();
			
			Integer actualTeacherId = 0;
			try {
				HttpSession session = request.getSession();
				Teacher t = (Teacher) session.getAttribute("user");
				actualTeacherId = t.getId();
			} catch (NullPointerException e) {
				path = path + "/Error?message=" + "Utente non autenticato.";
				response.sendRedirect(path);
				//response.sendError(HttpServletResponse.SC_FORBIDDEN, "Utente non autenticato.");
				return;
			}
			
			ExamDAO eDao = new ExamDAO(connection);
			RegistrationDAO rDao = new RegistrationDAO(connection);
			if(grades.contains(newGrade)) {
				try {
					Integer teacherId = eDao.getTeacher(examId);
					if(teacherId == null || !teacherId.equals(actualTeacherId)) {
						path = path + "/Error?message=" + "L'appello e il docente non corrispondono.";
						response.sendRedirect(path);
						return;
					}
					Registration r = rDao.getRegistration(examId, studId);
					if(r == null) {
						path = path + "/Error?message=" + "Lo studente non e' iscritto all'appello.";
						response.sendRedirect(path);
						return;
					}
					if(r.getStatusGrade().equals(StatusGrade.PUBBLICATO) || r.getStatusGrade().equals(StatusGrade.RIFIUTATO) || r.getStatusGrade().equals(StatusGrade.VERBALIZZATO)) {
						path = path + "/Error?message=" + "Voto non modificabile.";
						response.sendRedirect(path);
						return;
					}
					rDao.updateGrade(newGrade, studId, examId);
					path = path + "/GoToRegisteredStudents?examId=" + examId;
				}
				catch (SQLException e) {
					path = path + "/Error?message=" + "Failure in grade database update";
					response.sendRedirect(path);
					//response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in grade database update");
					return;
				}
			}
			else {
				path = path + "/Error?message=" + "Voto non valido.";
				response.sendRedirect(path);
				return;
			}
		}
		catch (NumberFormatException e) {
			path = path + "/Error?message=" + "Missing parameters";
			response.sendRedirect(path);
			//response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
	        return;
		}
		
		// form feedback pattern - servlet-to-client-to-server
		response.sendRedirect(path);
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
