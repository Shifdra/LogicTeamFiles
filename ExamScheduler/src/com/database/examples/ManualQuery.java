package com.database.examples;

import com.database.config.DatabaseConnection;

import java.sql.SQLException;

/**
 * Created by Jonathan on 10/5/2015.
 */
public final class ManualQuery {

	public static void main(String[] args) {
		/**
		 * Open and connect to the OracleSQL Database
		 */
		DatabaseConnection connection = DatabaseConnection.open();
		connection.connect();

		/**
		 * Execute a query and iterate through all the results
		 */
		connection.searchForExam("MONDAY", "A").forEachRemaining(r -> {
			try {
				System.out.println(r.getString("COURSE_CODE"));
			} catch (SQLException e) {
				e.printStackTrace();
			}
		});

/*		connection.execute("select * from STUDENT").forEachRemaining(r -> {
			try {
				int studentId = r.getInt("STUDENT_ID"); //Read a int value from column AUTHORID
				System.out.println(studentId);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		});

		//connection.addRoom("J140", "Lab");
		connection.getExamsForProgram("CPAN").forEachRemaining(r -> {
			try {
				String studentId = r.getString("COURSE_CODE"); //Read a int value from column AUTHORID
				System.out.println(studentId);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		});

		connection.getExamsForRoom("E417").forEachRemaining(r -> {
			try {
				String studentId = r.getString("COURSE_CODE"); //Read a int value from column AUTHORID
				System.out.println(studentId);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		});

		connection.getExamsForTeacher(123456).forEachRemaining(r -> {
			try {
				String studentId = r.getString("COURSE_CODE"); //Read a int value from column AUTHORID
				System.out.println(studentId);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		});*/

	}


}
