package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import beans.AddressBean;

public class AddressDAO {

	DataSource ds;

	public AddressDAO(DataSource passedDS) throws ClassNotFoundException {

		ds = passedDS;

	}

	public Map<String, AddressBean> retrieve() throws SQLException {

		String query = "select * from Address";
		Map<String, AddressBean> rv = new HashMap<String, AddressBean>();
		Connection con = this.ds.getConnection();
		PreparedStatement p = con.prepareStatement(query);
		ResultSet r = p.executeQuery();
		while (r.next()) {
			String name = r.getInt("id") + "";
			AddressBean book = new AddressBean(r.getInt("id"), r.getInt("uid"), r.getString("street"),
					r.getString("province"), r.getString("country"), r.getString("zip"), r.getString("phone"));
			rv.put(name, book);
		}
		r.close();
		p.close();
		con.close();
		return rv;

	}

	public boolean addAddress(AddressBean address) throws SQLException {
		if (address == null) {
			return false;
		} else {
			String query = "INSERT INTO address (id, uid, street, province, country, zip, phone) Values (";
			query = query + address.getId() + ", " + address.getUid() + ", " + address.getStreet() + ", "
					+ address.getProvince() + ", " + address.getCountry() + ", " + address.getZip() + ", "
					+ address.getPhone() + ");";
			Connection con = this.ds.getConnection();

			PreparedStatement p = con.prepareStatement(query);
			return p.execute();

		}
	}

	public int getShippingAddress(String email) throws SQLException {

		String query = "select * from Address WHERE email='" + email + "' AND addresstype='Shipping';";
		Connection con = this.ds.getConnection();
		PreparedStatement p = con.prepareStatement(query);
		ResultSet r = p.executeQuery();
		int id = 0;
		while (r.next()) {
			id = r.getInt("id");
		}
		r.close();
		p.close();
		con.close();
		return id;
	}

	public Map<String, AddressBean> retrieve(String email) throws SQLException {

		String query = "select * from Address WHERE email=" + email + ";";
		Map<String, AddressBean> rv = new HashMap<String, AddressBean>();
		Connection con = this.ds.getConnection();
		PreparedStatement p = con.prepareStatement(query);
		ResultSet r = p.executeQuery();
		while (r.next()) {
			String name = r.getInt("id") + "";
			AddressBean book = new AddressBean(r.getInt("id"), r.getInt("uid"), r.getString("street"),
					r.getString("province"), r.getString("country"), r.getString("zip"), r.getString("phone"));
			rv.put(name, book);
		}
		r.close();
		p.close();
		con.close();
		return rv;

	}

	public boolean updateAddress(int id, int uid, String street, String province, String country, String zip,
			String phone) throws SQLException {

		try {
			String query = "UPDATE TABLE address SET street=" + street + ", province=" + province + ", country="
					+ country + ", zip=" + zip + ", phone=" + phone + " WHERE id=" + id + ";";

			Connection con = this.ds.getConnection();
			PreparedStatement p = con.prepareStatement(query);
			return p.execute();

		} catch (Exception e) {
			return false;
		}
	}

}
