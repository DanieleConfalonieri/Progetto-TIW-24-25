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
import java.util.stream.Collectors;

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
 * Servlet implementation class GoToStudents
 */
@WebServlet("/GoToRegisteredStudents")
public class GoToRegisteredStudents extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Connection connection;
	private TemplateEngine templateEngine;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GoToRegisteredStudents() {
        super();
        // TODO Auto-generated constructor stub
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
		// recupero dai parametri della request l'appello di cui estrarre gli iscritti
		int examId = 0;
		String path;
		try {
			examId = Integer.parseInt(request.getParameter("examId"));
		} catch (NumberFormatException e) {
			path = getServletContext().getContextPath() + "/Error?message=" + "Missing parameters";
			response.sendRedirect(path);
			//response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
			return;
		}
		String examDate, courseName;
		Integer teacherId, actualTeacherId = 0;
		List<Registration> registeredStudents = new ArrayList<>();
		
		try {
			HttpSession session = request.getSession();
			Teacher t = (Teacher) session.getAttribute("user");
			actualTeacherId = t.getId();
		} catch (NullPointerException e) {
			path = getServletContext().getContextPath() + "/Error?message=" + "Utente non autenticato.";
			response.sendRedirect(path);
			//response.sendError(HttpServletResponse.SC_FORBIDDEN, "Utente non autenticato.");
			return;
		}
		
		RegistrationDAO rDao = new RegistrationDAO(connection);
		ExamDAO eDao = new ExamDAO(connection);
		try {
			examDate = eDao.getExamDate(examId);
			courseName = eDao.getCourseName(examId);
			teacherId = eDao.getTeacher(examId);
			registeredStudents = rDao.getStudentsByExam(examId);
			if(examDate == null || courseName == null || teacherId == null || !teacherId.equals(actualTeacherId)) {
				path = getServletContext().getContextPath() + "/Error?message=" + "Dati non validi.";
				response.sendRedirect(path);
				//response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Dati non validi.");
				return;
			}
		}
		catch (SQLException e) {
			path = getServletContext().getContextPath() + "/Error?message=" + "Failure in exam's registered students database extraction";
			response.sendRedirect(path);
			//response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in exam's registered students database extraction");
			return;
		}
		
		boolean isPublishable = !registeredStudents.stream().filter(g -> g.getStatusGrade().equals(StatusGrade.INSERITO)).collect(Collectors.toList()).isEmpty();
		boolean isVerbalizable = !registeredStudents.stream().filter(g -> g.getStatusGrade().equals(StatusGrade.PUBBLICATO) || g.getStatusGrade().equals(StatusGrade.RIFIUTATO)).collect(Collectors.toList()).isEmpty();
		
		path = "/IscrittiAppello.html";
		JakartaServletWebApplication webApplication = JakartaServletWebApplication.buildApplication(getServletContext());
        WebContext ctx = new WebContext(webApplication.buildExchange(request, response), request.getLocale());
        ctx.setVariable("examId", examId);
        ctx.setVariable("examDate", examDate);
        ctx.setVariable("courseName", courseName);
        ctx.setVariable("registrations", registeredStudents);
        ctx.setVariable("isPublishable", isPublishable);
        ctx.setVariable("isVerbalizable", isVerbalizable);
		templateEngine.process(path, ctx, response.getWriter());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
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
