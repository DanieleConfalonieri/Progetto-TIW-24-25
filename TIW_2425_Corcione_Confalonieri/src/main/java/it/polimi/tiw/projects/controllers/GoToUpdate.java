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
 * Servlet implementation class GoToUpdate
 */
@WebServlet("/GoToUpdate")
public class GoToUpdate extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Connection connection;
	private TemplateEngine templateEngine;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GoToUpdate() {
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
		// recupero dalla request la matricola dello studente e il codice dell'appello che mi servono per sapere quale iscrizione modificare
		int examId = 0, studId = 0;
		String path;
		try {
			examId = Integer.parseInt(request.getParameter("examId"));
			studId = Integer.parseInt(request.getParameter("studId"));
		} catch (NumberFormatException e) {
			path = getServletContext().getContextPath() + "/Error?message=" + "Parametri mancanti o errati.";
			response.sendRedirect(path);
			return;
		}
		
		Integer actualTeacherId = 0;
		try {
			HttpSession session = request.getSession();
			Teacher t = (Teacher) session.getAttribute("user");
			actualTeacherId = t.getId();
		} catch (NullPointerException e) {
			path = getServletContext().getContextPath() + "/Error?message=" + "Utente non autenticato.";
			response.sendRedirect(path);
			return;
		}
		
		// devo controllare che l'appello sia di un esame del docente e che lo studente sia effettivamente iscritto (l'iscrizione esiste),
		// inoltre posso modificare il voto solo se non è già pubblicato, rifiutato o verbalizzato
		
		ExamDAO eDao = new ExamDAO(connection);
		RegistrationDAO rDao = new RegistrationDAO(connection);
		Registration reg;
		try {
			Integer teacherId = eDao.getTeacher(examId);
			if(teacherId == null || !teacherId.equals(actualTeacherId)) {
				path = getServletContext().getContextPath() + "/Error?message=" + "Appello non valido.";
				response.sendRedirect(path);
				return;
			}
			reg = rDao.getRegistration(examId, studId);
			try {
				StatusGrade status = reg.getStatusGrade();
				if(status == StatusGrade.PUBBLICATO || status == StatusGrade.RIFIUTATO || status == StatusGrade.VERBALIZZATO) {
					path = getServletContext().getContextPath() + "/Error?message=" + "Questo voto non e' modificabile.";
					response.sendRedirect(path);
					return;
				}
			} catch (NullPointerException e) {
				path = getServletContext().getContextPath() + "/Error?message=" + "Studente non iscritto all'appello selezionato.";
				response.sendRedirect(path);
				return;
			}
		}
		catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Errore nell'estrazione dell'iscrizione dal database.");
			return;
		}
		path = "/ModificaVoto.html";
		JakartaServletWebApplication webApplication = JakartaServletWebApplication.buildApplication(getServletContext());
        WebContext ctx = new WebContext(webApplication.buildExchange(request, response), request.getLocale());
        ctx.setVariable("registration", reg);
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
