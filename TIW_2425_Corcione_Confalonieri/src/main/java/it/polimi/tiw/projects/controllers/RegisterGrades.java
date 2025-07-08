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

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

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
	private TemplateEngine templateEngine;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RegisterGrades() {
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
		String path = getServletContext().getContextPath();
		try {
			examId = Integer.parseInt(request.getParameter("examId"));
		} catch (NumberFormatException e) {
			path = path + "/Error?message=" + "Parametri mancanti o errati.";
			response.sendRedirect(path);
	        return;
		}
		
		Integer actualTeacherId = 0;
		try {
			HttpSession session = request.getSession();
			Teacher t = (Teacher) session.getAttribute("user");
			actualTeacherId = t.getId();
		} catch (NullPointerException e) {
			path = path + "/Error?message=" + "Utente non autenticato.";
			response.sendRedirect(path);
			return;
		}
		
		// verbalizzazione voti
		ReportDAO rDao = new ReportDAO(connection);
		ExamDAO eDao = new ExamDAO(connection);
		Report report;
		try {
			Integer teacherId = eDao.getTeacher(examId);
			if(teacherId == null || !teacherId.equals(actualTeacherId)) {
				path = path + "/Error?message=" + "L'appello e il docente non corrispondono.";
				response.sendRedirect(path);
				return;
			}
			report = rDao.registerPublishedGrades(examId);
		} catch (SQLException e) {
			path = path + "/Error?message=" + "Impossibile aggiornare il database.";
			response.sendRedirect(path);
			return;
		} catch (RuntimeException e) {
			path = path + "/Error?message=" + e.getMessage();
			response.sendRedirect(path);
			return;
		}
		List<Registration> students = new ArrayList<>();
		try {
			students = rDao.getStudentsByReport(report.getReportCode());
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Errore nell'estrazione del verbale dal database.");
			return;
		}
		
		// rimando al template successivo
		path = "/DettaglioVerbale.html";
		JakartaServletWebApplication webApplication = JakartaServletWebApplication.buildApplication(getServletContext());
        WebContext ctx = new WebContext(webApplication.buildExchange(request, response), request.getLocale());
        ctx.setVariable("report", report);
        ctx.setVariable("students", students);
        
		templateEngine.process(path, ctx, response.getWriter());
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
