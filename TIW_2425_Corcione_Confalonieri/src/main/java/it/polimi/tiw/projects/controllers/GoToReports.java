package it.polimi.tiw.projects.controllers;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.UnavailableException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import it.polimi.tiw.projects.beans.Report;
import it.polimi.tiw.projects.beans.Teacher;
import it.polimi.tiw.projects.dao.ReportDAO;

/**
 * Servlet implementation class GoToReports
 */
@WebServlet("/GoToReports")
public class GoToReports extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	private TemplateEngine templateEngine;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GoToReports() {
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
		int idDocente;
		String path;
		try {
			idDocente = Integer.parseInt(request.getParameter("idDocente"));
		} catch (NumberFormatException e) {
			path = getServletContext().getContextPath() + "/Error?message=" + "Parametri mancanti o errati.";
			response.sendRedirect(path);
			return;
		}
		
		Teacher t;
		try {
			t = (Teacher) request.getSession().getAttribute("user");
		} catch (ClassCastException e) {
			path = getServletContext().getContextPath() + "/Error?message=" + "Utente non autenticato.";
			response.sendRedirect(path);
			return;
		}
		
		if(t.getId() != idDocente) { // controlla che il docente per cui si stanno richiedendo i verbali sia lo stesso che è il titolare della sessione
			path = getServletContext().getContextPath() + "/Error?message=" + "Utente non autenticato.";
			response.sendRedirect(path);
			return;
		}
		
		ReportDAO rDao = new ReportDAO(connection);
		List <Report> verbali;
		try {
			verbali = rDao.getReportsByTeacher(idDocente);
		} catch (SQLException e) {
			path = getServletContext().getContextPath() + "/Error?message=" + "Errore nell'estrazione dei report dal database.";
			response.sendRedirect(path);
			return;
		}
		
		path = "/Verbali.html";
		JakartaServletWebApplication webApplication = JakartaServletWebApplication.buildApplication(getServletContext());
        WebContext ctx = new WebContext(webApplication.buildExchange(request, response), request.getLocale());
        ctx.setVariable("reports", verbali);
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
