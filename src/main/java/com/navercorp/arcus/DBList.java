// DBList.java
package com.navercorp.arcus;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.Map;
import java.util.Random;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DBList {

	public String id;
	public String password;
	public String url;
	public String driverName;

	public DBList() {
		id = "root";
		password = "1234";
		url = "jdbc:mysql://localhost:3306/test";
		driverName = "com.mysql.jdbc.Driver";
	}

	public boolean makeDB() {
		Random rand = new Random();
		Connection conn = null;
		Statement stmt = null;

		try {
			Class.forName(driverName);

			conn = DriverManager.getConnection(url, id, password);
			stmt = conn.createStatement();

			stmt.executeUpdate("create table test( id int primary key, value char(20))");

			for (int i = 0; i < 10000; i++) {// DB에 10000개의 원소를 넣는다.
				try {
					long bkey = 1 + rand.nextInt(100000); // 1~100000까지의 랜덤한 값을
															// 넣음
					stmt.executeUpdate("insert into test (id, value) values ( " + bkey + ", '" + bkey + "')");// key와
																												// value는
																												// 같다.
				} catch (Exception e) {
				}
			}

			stmt.close();
			conn.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

		return false;
	}

	public String checkDB() {
		Connection conn = null;
		Statement stmt = null;
		int max_num = 1000;
		String resultString = "Not OK.";
		try {
			Class.forName(driverName);

			conn = DriverManager.getConnection(url, id, password);
			stmt = conn.createStatement();

			ResultSet rs = null;
			rs = stmt.executeQuery("select * from test where id >= 1000 and id <= 1999"); // 1000~2000까지의
																							// Element를
																							// 일괄
																							// 조회한다.

			while (rs.next()) {
				int key = rs.getInt("id");
				String str = rs.getString("value");

				if (key != Integer.parseInt(str)) {
					resultString = "error) key:" + key + ", value:" + str;
					break;
				}
				max_num--;
				if (max_num == 0)
					break;

			}

			stmt.executeUpdate("drop table test");
			stmt.close();
			conn.close();
			resultString = "성공";
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return resultString;
	}

	public static void main(String[] args) {
		DBList dbList = new DBList();
		System.out.println("DB 10000개의 Element 삽입후 검색");
		long startTime = System.currentTimeMillis();
		dbList.makeDB();

		dbList.checkDB();
		long endTime = System.currentTimeMillis();
		System.out.println("##  소요시간(초.0f) : " + (endTime - startTime) / 1000.0f + "초");
	}
}
