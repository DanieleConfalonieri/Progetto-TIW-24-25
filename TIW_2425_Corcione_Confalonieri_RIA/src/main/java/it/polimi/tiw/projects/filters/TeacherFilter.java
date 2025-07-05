package it.polimi.tiw.projects.filters;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

import it.polimi.tiw.projects.beans.Teacher;

/**
 * Servlet Filter implementation class TeacherFilter
 */
@WebFilter({"/TeacherFilter", "/GoToExamsTeacher", "/GoToHomeTeacher", "/GoToRegisteredStudents", "/GoToReports", "/GoToUpdate", "/PublishGrades", "/RegisterGrades", "/UpdateGrade"})
public class TeacherFilter implements Filter {

    /**
     * Default constructor. 
     */
    public TeacherFilter() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		System.out.println("Teacher filter executing...");
		
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		String loginpath = req.getServletContext().getContextPath() + "/index.html";
		HttpSession s = req.getSession();
		if (!(s.getAttribute("user") instanceof Teacher)) {
			res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			res.setHeader("Location", loginpath);
			//res.sendRedirect(loginpath);
			return;
		}
		
		chain.doFilter(request, response);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
