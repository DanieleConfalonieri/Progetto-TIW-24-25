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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GoToReports() {
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
		int idDocente;
		try {
			idDocente = Integer.parseInt(request.getParameter("idDocente"));
		} catch (NumberFormatException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Missing parameters");
			//response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
			return;
		}
		
		Teacher t;
		try {
			t = (Teacher) request.getSession().getAttribute("user");
		} catch (ClassCastException e) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			response.getWriter().println("Utente non autenticato.");
			//response.sendError(HttpServletResponse.SC_FORBIDDEN, "User not logged in");
			return;
		}
		
		if(t.getId() != idDocente) { // controlla che il docente per cui si stanno richiedendo i verbali sia lo stesso che è il titolare della sessione
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			response.getWriter().println("Utente non autenticato.");
			//response.sendError(HttpServletResponse.SC_FORBIDDEN, "User not logged in");
			return;
		}
		
		ReportDAO rDao = new ReportDAO(connection);
		List <Report> verbali;
		try {
			verbali = rDao.getReportsByTeacher(idDocente);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
			response.getWriter().println("Failure in report database extraction");
			//response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in report database extraction");
			return;
		}
		
		Gson gson = new GsonBuilder().setDateFormat("yyyy MMM dd").create();
		String json = gson.toJson(verbali);
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
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
