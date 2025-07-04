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
import it.polimi.tiw.projects.beans.Teacher;
import it.polimi.tiw.projects.dao.CourseDAO;
import it.polimi.tiw.projects.dao.ExamDAO;

/**
 * Servlet implementation class GoToExamsTeacher
 */
@WebServlet("/GoToExamsTeacher")
public class GoToExamsTeacher extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Connection connection;
	private TemplateEngine templateEngine;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GoToExamsTeacher() {
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
		// recupero dai parametri della request il corso di cui estrarre gli appelli
		int courseId = 0;
		String path;
		try {
			courseId = Integer.parseInt(request.getParameter("course"));
		} catch (NumberFormatException e) {
			path = getServletContext().getContextPath() + "/Error?message=" + "Missing parameters";
			response.sendRedirect(path);
			//response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
			return;
		}
		String courseName = request.getParameter("courseName");
		if(courseName==null) {
			path = getServletContext().getContextPath() + "/Error?message=" + "Missing parameters";
			response.sendRedirect(path);
			//response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
			return;
		}
		
		Integer teacherId;
		try {
			HttpSession session = request.getSession();
			Teacher t = (Teacher) session.getAttribute("user");
			teacherId = t.getId();
		} catch (NullPointerException e) {
			path = getServletContext().getContextPath() + "/Error?message=" + "Utente non autenticato.";
			response.sendRedirect(path);
			//response.sendError(HttpServletResponse.SC_FORBIDDEN, "Utente non autenticato.");
			return;
		}
		
		CourseDAO cDao = new CourseDAO(connection);
		try {
			Course c = cDao.getCoursesById(courseId);
			if(c==null || !c.getName().equals(courseName)) {
				path = getServletContext().getContextPath() + "/Error?message=" + "Il codice corso e il nome corso non corrispondono.";
				response.sendRedirect(path);
				//response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Il codice corso e il nome corso non corrispondono.");
				return;
			}
			List<Course> coursesOfTeacher = cDao.getCoursesByTeacher(teacherId);
			if(!coursesOfTeacher.contains(c)) {
				path = getServletContext().getContextPath() + "/Error?message=" + "Questo corso non appartiene al docente.";
				response.sendRedirect(path);
				//response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Questo corso non appartiene al docente.");
				return;
			}
		} catch (SQLException e) {
			path = getServletContext().getContextPath() + "/Error?message=" + "Failure in course's exams database extraction";
			response.sendRedirect(path);
			//response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in course's exams database extraction");
			return;
		}
		
		List<Exam> exams = new ArrayList<>();
		
		ExamDAO eDao = new ExamDAO(connection);
		try {
			exams = eDao.getExamsByCourse(courseId);
		}
		catch (SQLException e) {
			path = getServletContext().getContextPath() + "/Error?message=" + "Failure in course's exams database extraction";
			response.sendRedirect(path);
			//response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in course's exams database extraction");
			return;
		}
		path = "/AppelliDocente.html";
		JakartaServletWebApplication webApplication = JakartaServletWebApplication.buildApplication(getServletContext());
        WebContext ctx = new WebContext(webApplication.buildExchange(request, response), request.getLocale());
        ctx.setVariable("exams", exams);
        ctx.setVariable("courseName", courseName);
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
