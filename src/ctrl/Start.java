package ctrl;

import java.io.IOException;
import java.sql.SQLException;
import java.text.NumberFormat;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import beans.AddressBean;
import beans.POBean;
import beans.UserBean;
import model.PasswordUtils;
import model.bookstoreModel;
import model.userModel;

/**
 * Servlet implementation class Start
 */
@WebServlet("/Start")
public class Start extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private bookstoreModel bModel;
	private userModel uModel;
	private String currPage;
	NumberFormat formatter;
	private static final double tax = 0.13;
	private int cardTries = 0;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Start() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void init() throws ServletException {
		// ServletContext context = getServletContext();
		formatter = NumberFormat.getCurrencyInstance();
		try {
			uModel = new userModel();
			bModel = uModel.getbookStoreModel();
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
		getServletContext().setAttribute("bookModel", bModel);
		getServletContext().setAttribute("userModel", uModel);

	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		currPage = request.getParameter("currPage");
		if (currPage == null) {
			currPage = "home";
		}
		if (currPage.equals("home")) {

			request.getRequestDispatcher("./index.html").forward(request, response);
		} else if (currPage.equals("categories"))

		{
			try {
				String category = request.getParameter("category");
				String bid = request.getParameter("addToCart");
				if (bid == null) {

				} else {

					uModel.addToCart(bid);
				}
				if (category == null) {
					request.setAttribute("results", (bModel.getAllBooks()));

				} else {
					request.setAttribute("results", bModel.displayBooks(bModel.getByCategoryMap(category)));
				}

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				request.setAttribute("addedToCart",
						"<a href=\"Start?currPage=cart\">Cart (" + uModel.getCartSize() + ")</a>");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				request.setAttribute("addedToCart", "<a href=\"Start?currPage=cart\">Cart (0)</a>");
			}
			request.getRequestDispatcher("./Browse.jspx").forward(request, response);

		} else if (currPage.equals("search")) {
			try {
				String search = request.getParameter("searchField");

				request.setAttribute("addedToCart", "Cart (" + uModel.getCartSize() + ")");
				if (search == null) {
					request.setAttribute("results", (bModel.getAllBooks()));

				} else {
					request.setAttribute("results", bModel.displayBooks(bModel.getByNameMap(search)));
				}

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			request.getRequestDispatcher("./Browse.jspx").forward(request, response);

		} else if (currPage.equals("cart")) {
			try {
				// Adjusting Quantity begin
				String bid = request.getParameter("bid");
				String adjust = request.getParameter("adjust");
				if (!(bid == null) && !(adjust == null)) {
					if (adjust.equals("plus")) {
						uModel.cartPlus(bid);
					} else {
						uModel.cartMinus(bid);
					}

				}

				// Adjusting Quantity end
				request.setAttribute("sSize", uModel.getCartSize());
				double subtotal = uModel.getSubtotal();
				request.setAttribute("subtotal", formatter.format(subtotal));
				request.setAttribute("tax", formatter.format(subtotal * tax));
				request.setAttribute("total", formatter.format(subtotal * (1 + tax)));
				request.setAttribute("displaycart", uModel.getCart());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			request.getRequestDispatcher("./ShoppingCart.jspx").forward(request, response);

		} else if (currPage.equals("payment")) {
			try {

				request.setAttribute("sSize", uModel.getCartSize());
				double subtotal = uModel.getSubtotal();
				request.setAttribute("subtotal", formatter.format(subtotal));
				request.setAttribute("tax", formatter.format(subtotal * tax));
				request.setAttribute("total", formatter.format(subtotal * (1 + tax)));
				request.setAttribute("displaysimple", uModel.getCartSimple());

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (uModel.getUid() > 1000000000) {
				request.setAttribute("response", "Please Login");
				request.getRequestDispatcher("./LoginPage.jspx").forward(request, response);
			} else {
				request.getRequestDispatcher("./Payment.jspx").forward(request, response);
			}
		} else if (currPage.equals("receipt")) {
			if (uModel.getUid() > 1000000000) {
				request.setAttribute("response", "Please Login");
				request.getRequestDispatcher("./LoginPage.jspx").forward(request, response);
			} else {

				cardTries++;
				if (cardTries == 3) { // Denies third try on credit card
					cardTries = 0;
					try {
						request.setAttribute("validity",
								uModel.getCartSize() + " <h1>Card Not Valid! Please try again.</h1>");
						request.getRequestDispatcher("./Payment.jspx").forward(request, response);

					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else { // Allows credit card to go through
					UserBean user = null;
					POBean poID = uModel.createPO();
					int uid = poID.getUid();
					user = uModel.getUser();
					if (user == null) {
						request.getRequestDispatcher("./LoginPage.jspx").forward(request, response);
					} else {
						request.setAttribute("validity", " ");
						request.setAttribute("receiptNumber", poID.getId());
						request.setAttribute("name", user.getFname() + " " + user.getLname());
						request.setAttribute("receiptDate", poID.getDay());
						double subtotal = 0;
						try {
							subtotal = uModel.getSubtotal();
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						request.setAttribute("subtotal", formatter.format(subtotal));
						request.setAttribute("tax", formatter.format(subtotal * tax));
						request.setAttribute("total", formatter.format(subtotal * (1 + tax)));
						try {
							AddressBean addr = bModel.getShippingID(uid);
							request.setAttribute("address", addr.getStreet() + ", " + addr.getProvince() + "<br />"
									+ addr.getZip() + "<br />" + addr.getCountry() + ", " + addr.getPhone());
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							request.setAttribute("address",
									"Shipping Address not on file.  Please update your profile");

						}
						try {
							request.setAttribute("booksBought", uModel.getCartSimple());
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						// change from cart to purchase
						try {
							bModel.changeToPurchased(uid);
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						request.getRequestDispatcher("./Receipt.jspx").forward(request, response);
					}
				}
			}
		} else if (currPage.equals("logout")) {
			try {
				uModel = new userModel(); // Create new model when logout
				bModel = uModel.getbookStoreModel();
				request.getRequestDispatcher("./index.html").forward(request, response);
			} catch (ClassNotFoundException e) {
				request.getRequestDispatcher("./index.html").forward(request, response);
			}
		} else if (currPage.equals("login")) {
			String req = request.getParameter("signIn");
			if (!((req == null) || (req == ""))) {
				String email = request.getParameter("loginName");
				String pwd = request.getParameter("password");
				UserBean user = null;
				try {
					user = bModel.getUserFromEmail(email);
					if (PasswordUtils.verifyUserPassword(pwd, user.getPassword(), uModel.getSalt())) {
						request.setAttribute("UserName", email);
						getServletContext().setAttribute("UserName", email);
						request.setAttribute("response", "");
						uModel.setUid(user.getUid());
						uModel.setUser(user);
						request.getRequestDispatcher("./index.html").forward(request, response);

					} else {

						request.setAttribute("response", "Invalid Username or Password!");
						request.getRequestDispatcher("./LoginPage.jspx").forward(request, response);
					}
				} catch (SQLException e) {
					request.setAttribute("response", "Invalid Username or Password!");
					request.getRequestDispatcher("./LoginPage.jspx").forward(request, response);

				}
			} else {
				request.getRequestDispatcher("./LoginPage.jspx").forward(request, response);

			}
		} else if (currPage.equals("addUser")) {
			String create = request.getParameter("create");
			if (!(create == null)) {
				String fname = request.getParameter("fname");
				String lname = request.getParameter("lname");
				String email = request.getParameter("email");
				String password = request.getParameter("userPassword");

				if ((fname == "") || lname == "" || email == "" || password == "") {
					request.setAttribute("response", " Please enter the required fields!");
					request.getRequestDispatcher("./createUser.jspx").forward(request, response);
				} else {
					try {
						String street;
						String city;
						String province;
						String country;
						String zip;
						String phone;
						String same;
						uModel.createUser(fname, lname, email, password, "General");

						same = request.getParameter("sameAsBilling");
						street = request.getParameter("street");
						city = request.getParameter("city");
						province = request.getParameter("province");
						country = request.getParameter("country");
						zip = request.getParameter("zip");
						int uid = uModel.getUid();
						phone = request.getParameter("phone");
						if (same == null) {
							String sstreet = request.getParameter("sstreet");
							String scity = request.getParameter("scity");
							String sprovince = request.getParameter("sprovince");
							String scountry = request.getParameter("scountry");
							String szip = request.getParameter("szip");
							String sphone = request.getParameter("sphone");
							if (sstreet == "" || scity == "" || sprovince == "" || scountry == "" || szip == ""
									|| sphone == "") {
								bModel.addAddress(0, uid, street, city, province, country, zip, phone, "Shipping");

							} else {
								bModel.addAddress(0, uid, sstreet, scity, sprovince, scountry, szip, sphone,
										"Shipping");
							}
						} else {
							bModel.addAddress(0, uid, street, city, province, country, zip, phone, "Shipping");

						}
						bModel.addAddress(0, uid, street, city, province, country, zip, phone, "Billing");
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						request.getRequestDispatcher("./createUser.jspx").forward(request, response);
					}
				}
				request.getRequestDispatcher("Start?currPage=categories").forward(request, response);

			} else {
				request.getRequestDispatcher("./createUser.jspx").forward(request, response);
			}
		} else {
			request.getRequestDispatcher("./index.html").forward(request, response);
		}
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
