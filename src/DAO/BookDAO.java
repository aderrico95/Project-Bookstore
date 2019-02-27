package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import beans.BookBean;

public class BookDAO {

	DataSource ds;

	public BookDAO() throws ClassNotFoundException {
		try {
			ds = (DataSource) (new InitialContext()).lookup("java:/comp/env/jdbc/EECS");
		} catch (NamingException e) {
			e.printStackTrace();
		}

	}

	public Map<String, BookBean> retrieve(String namePrefix) throws SQLException {
		if ((namePrefix != null) || (namePrefix != "")) {
			namePrefix = " WHERE title='%" + namePrefix + "%'";
		}
		String query = "select * from Book" + namePrefix;
		Map<String, BookBean> rv = new HashMap<String, BookBean>();
		Connection con = this.ds.getConnection();
		PreparedStatement p = con.prepareStatement(query);
		ResultSet r = p.executeQuery();
		while (r.next()) {
			String name = r.getString("bid");
			BookBean book = new BookBean(r.getString("bid"), r.getString("title"), r.getInt("price"),
					r.getString("category"));
			rv.put(name, book);
		}
		r.close();
		p.close();
		con.close();
		return rv;

	}

}