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
import java.util.ArrayList;
import java.util.List;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import it.polimi.tiw.projects.beans.Registration;
import it.polimi.tiw.projects.beans.StatusGrade;
import it.polimi.tiw.projects.beans.Student;
import it.polimi.tiw.projects.dao.RegistrationDAO;

/**
 * Servlet implementation class Reorder
 */
@WebServlet("/Reorder")
public class Reorder extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Connection connection;
	private TemplateEngine templateEngine;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Reorder() {
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
		String col = request.getParameter("sortField");
		int examId = Integer.parseInt(request.getParameter("examId"));
		String examDate = request.getParameter("data");
		int courseId = Integer.parseInt(request.getParameter("courseId"));
		RegistrationDAO regDao = new RegistrationDAO(connection);
		List<Registration> registrations = new ArrayList<>();
		int i = 0;
		int studId;
		String surname="", name, email, degree, grade;
		StatusGrade statusGrade;
		boolean finished = false;
		while(!finished) {
			Registration reg = new Registration();
			reg.setStudent(new Student());
			try {
				studId = Integer.parseInt(request.getParameter("registrations["+i+"].student.id"));
				reg.getStudent().setId(studId);
				surname = request.getParameter("registrations["+i+"].student.surname");
				reg.getStudent().setSurname(surname);
				name = request.getParameter("registrations["+i+"].student.name");
				reg.getStudent().setName(name);
				email = request.getParameter("registrations["+i+"].student.email");
				reg.getStudent().setEmail(email);
				degree = request.getParameter("registrations["+i+"].student.degree");
				reg.getStudent().setDegree(degree);
				grade = request.getParameter("registrations["+i+"].grade");
				reg.setGrade(grade);
				statusGrade = StatusGrade.getStatusGradeFromString(request.getParameter("registrations["+i+"].statusGrade"));
				reg.setStatusGrade(statusGrade);
			} catch (Exception e) {
				finished = true;
			}
			if(!finished){
				registrations.add(reg);
			}
			i++;
		}
		registrations = regDao.reorder(registrations, col);
		JakartaServletWebApplication webApplication = JakartaServletWebApplication.buildApplication(getServletContext());
		WebContext ctx = new WebContext(webApplication.buildExchange(request, response), request.getLocale());
		ctx.setVariable("registrations", registrations);
		for(int i1=0; i1<registrations.size(); i1++) {
			System.out.println(registrations.get(i1).getStatusGrade());
		}
		ctx.setVariable("examId", examId);
		ctx.setVariable("examDate", examDate);
        ctx.setVariable("courseName", courseId);
		String path = "/IscrittiAppello.html";
		templateEngine.process(path, ctx, response.getWriter());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
