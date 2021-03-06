package ctrl;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.bookstoreModel;

//Needed to edit somethingasdfasd
/**
 * Servlet implementation class Start
 */
@WebServlet({ "/Backup" })
public class Backup extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String searchField = "";
	private int sCartSize = 3;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Backup() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		bookstoreModel model = null;
		try {
			model = new bookstoreModel();
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		;
		// TODO Auto-generated method stub
		String comm = "n/a";
		if (request.getParameter("comm") == null) {
			comm = "n/a";
		} else {
			comm = request.getParameter("comm");
		}

		if (comm.equals("ajax")) {
			searchField = request.getParameter("searchField");
			if (searchField == null) {
				try {
					request.setAttribute("results", model.getAllBooks());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				try {
					request.setAttribute("results", model.getBookbyName(searchField));
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			request.getRequestDispatcher("./Browse.jspx").forward(request, response);
			// response.sendRedirectURL();

		} else if (comm.equals("category1")) {
			if (!(model == null)) {
				try {
					response.getWriter().append(model.getBooks("EECS1"));

				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else if (comm.equals("category2")) {
			if (!(model == null)) {
				try {
					response.getWriter().append(model.getBooks("EECS2"));

				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else if (comm.equals("category3")) { // 3rd Year Courses
			if (!(model == null)) {
				try {
					response.getWriter().append(model.getBooks("EECS3"));

				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else if (comm.equals("category4")) {
			if (!(model == null)) {
				try {
					response.getWriter().append(model.getBooks("EECS4"));

				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} else

		{
			searchField = request.getParameter("searchField");
			if (searchField == null) {
				try {
					request.setAttribute("results", model.getAllBooks());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				try {
					request.setAttribute("results", model.getBookbyName(searchField));
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			request.getRequestDispatcher("./Browse.jspx").forward(request, response);
			// response.sendRedirect("./Browse.jspx");

		}
		String s = Integer.toString(sCartSize);
		System.out.println(sCartSize);
		request.setAttribute("sSize", s);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
