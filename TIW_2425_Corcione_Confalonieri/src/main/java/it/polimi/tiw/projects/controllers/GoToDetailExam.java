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
 * Servlet implementation class GoToDetailExam
 */
@WebServlet("/GoToDetailExam")
public class GoToDetailExam extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Connection connection;
	private TemplateEngine templateEngine;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GoToDetailExam() {
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
		HttpSession session = request.getSession();
		Student student = (Student) session.getAttribute("user");
		int studId = student.getId();
		int examId, courseId;
		String path;
		try {
			examId = Integer.parseInt(request.getParameter("examId"));
			courseId = Integer.parseInt(request.getParameter("courseId"));
		} catch (NumberFormatException e) {
			path = getServletContext().getContextPath() + "/Error?message=" + "Missing parameters";
			response.sendRedirect(path);
			//response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
			return;
		}
		// verificare che lo studente stia richiedendo i dati per un appello a cui è iscritto, di un corso di cui l'appello fa parte
		Registration registration;
		Course course;
		CourseDAO cDao = new CourseDAO(connection);
		RegistrationDAO rDao = new RegistrationDAO(connection);
		try {
			registration = rDao.getRegistration(examId, studId); // appare solo se lo studente è effettivamente iscritto all'appello
			course = cDao.getCoursesById(courseId); // 
			if(registration == null) {
				path = getServletContext().getContextPath() + "/Error?message=" + "Appello non valido";
				response.sendRedirect(path);
				//response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Invalid exam");
				return;
			}
			if(course == null) {
				path = getServletContext().getContextPath() + "/Error?message=" + "Corso non valido";
				response.sendRedirect(path);
				//response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Invalid course");
				return;
			}
			ExamDAO eDao = new ExamDAO(connection);
			if(!eDao.validExamForCourse(courseId, examId)) {
				path = getServletContext().getContextPath() + "/Error?message=" + "Questo appello non fa parte del corso selezionato";
				response.sendRedirect(path);
				//response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Questo appello non fa parte del corso selezionato");
				return;
			}
		} catch (SQLException e) {
			path = getServletContext().getContextPath() + "/Error?message=" + "Failure in course's exams database extraction";
			response.sendRedirect(path);
			//response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in course's exams database extraction");
			return;
		}
		path = "/DettaglioIscrizione.html";
		JakartaServletWebApplication webApplication = JakartaServletWebApplication.buildApplication(getServletContext());
        WebContext ctx = new WebContext(webApplication.buildExchange(request, response), request.getLocale());
        boolean isPublished = registration.getStatusGrade().equals(StatusGrade.PUBBLICATO);
        boolean isRefused = registration.getStatusGrade().equals(StatusGrade.RIFIUTATO);
        ctx.setVariable("isRefused", isRefused);
        ctx.setVariable("isPublished", isPublished);
        ctx.setVariable("registration", registration);
        ctx.setVariable("course", course);
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
