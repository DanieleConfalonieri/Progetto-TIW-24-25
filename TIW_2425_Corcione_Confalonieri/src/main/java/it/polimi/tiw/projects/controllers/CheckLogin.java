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

import it.polimi.tiw.projects.beans.Student;
import it.polimi.tiw.projects.beans.Teacher;
import it.polimi.tiw.projects.dao.StudentDAO;
import it.polimi.tiw.projects.dao.TeacherDAO;

/**
 * Servlet implementation class CheckLogin
 */
@WebServlet("/CheckLogin")
public class CheckLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CheckLogin() {
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
			throw new UnavailableException("Can't load database driver");
		} catch (SQLException e) {
			throw new UnavailableException("Couldn't get db connection");
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 * il login è una richiesta di tipo POST perche' avviene tramite una form
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String user = request.getParameter("username");
		String pwd = request.getParameter("pwd");
		String role = request.getParameter("role");
		
		if (user == null || user.isEmpty() || pwd == null || pwd.isEmpty() || role == null || role.isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
			return;
		}
		
		String path = getServletContext().getContextPath();
		
		if(role.equals("docente")) {
			TeacherDAO tDAO = new TeacherDAO(connection);
			Teacher t = null;
			
			try {
				t = tDAO.checkCredentials(user, pwd);
			} catch (SQLException e) {
				// throw new ServletException(e);
				response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in database credential checking");
				return;
			}
			
			if(t==null) {
				// docente non presente nel db, non si puo' autenticare
				path = path + "/index.html";
			}
			else {
				request.getSession().setAttribute("user", t);
				String target = "/GoToHomeTeacher";
				path = path + target;
			}
		}
		else if(role.equals("studente")) {
			StudentDAO sDAO = new StudentDAO(connection);
			Student s = null;
			
			try {
				s = sDAO.checkCredentials(user, pwd);
			} catch (SQLException e) {
				// throw new ServletException(e);
				response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in database credential checking");
				return;
			}
			
			if(s==null) {
				// studente non presente nel db, non si puo' autenticare
				path = path + "/index.html";
			}
			else {
				request.getSession().setAttribute("user", s);
				String target = "/GoToHomeStudent";
				path = path + target;
			}
		}
		
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
