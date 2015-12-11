package com.database.config;

import java.sql.*;

/**
 * Created by Jonathan on 10/5/2015.
 */
public final class DatabaseConnection {

	/**
	 * Singleton instance of the DatabaseConnection class. (This will be removed to avoid Global State)
	 */
	public static DatabaseConnection instance;

	/**
	 * Create a new instance of the DatabaseConnection class.
	 *
	 * @return the connection
	 */
	public static DatabaseConnection open() {
		if (instance != null) {
			throw new RuntimeException("You can not open more than one instance of a database.");
		}
		return instance = new DatabaseConnection(DatabaseConfiguration.DEFAULT);
	}

	/**
	 * The configuration for the database.
	 */
	private final DatabaseConfiguration config;

	/**
	 * The database connection
	 */
	private Connection connection;

	/**
	 * Creates a <code>Statement</code> object for sending
	 * SQL statements to the database.
	 *
	 * @param config the database configuration
	 */
	private DatabaseConnection(DatabaseConfiguration config) {
		this.config = config;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Opens a new connection to the database.
	 */
	public void connect() {
		try {
			connection = DriverManager.getConnection("jdbc:oracle:thin:@" + config.getHost() + ":" + config.getPort() + ":grok", config.getUsername(), config.getPassword());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Executes a SQL query to the server
	 *
	 * @param query the string to be executed
	 * @return result of the query
	 */
	public Result execute(String query) {
		if (connection == null) {
			connect();
		}
		try {
			Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			return new Result(stmt.executeQuery(query));
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean addStudent(String firstname, String lastname, int studentId) {
		if (hasStudent(studentId)) {
			System.err.println("Student with that StudentID already exists!");
			return false;
		}
		String query = String.format("INSERT INTO STUDENT (FIRST_NAME, LAST_NAME, STUDENT_ID) VALUES (%s, %s, %d)", quo(firstname), quo(lastname), studentId);
		return execute(query) != null;
	}

	public boolean hasStudent(int studentId) {
		String query = "SELECT * FROM STUDENT WHERE STUDENT_ID = " + studentId;
		try {
			return execute(query).set().isBeforeFirst();
		} catch (Throwable e) {
			// e.printStackTrace();
		}
		return false;
	}

	public boolean removeStudent(int studentId) {
		if (!hasStudent(studentId)) {
			return false;
		}
		String query = "DELETE FROM STUDENT WHERE STUDENT_ID = " + studentId;
		return execute(query) != null;
	}

	public boolean hasCourse(String course_code) {
		String query = "SELECT * FROM COURSE WHERE COURSE_CODE = " + quo(course_code);
		try {
			return execute(query).set().isBeforeFirst();
		} catch (Throwable e) {
			// e.printStackTrace();
		}
		return false;
	}

	public boolean addCourse(String course_code, String course_name, String course_desc, String exam, String room_num, String program_code, int teacher_id) {
		if (hasCourse(course_code)) {
			System.err.println("Course with that course code already exists!");
			return false;
		}
		String query = String.format("INSERT INTO PROGRAM VALUES (%s, %s, %s, %s, %s,%s, %d)", quo(course_code), quo(course_name), quo(course_desc), quo(exam), quo(room_num), quo(program_code), teacher_id);
		return execute(query) != null;
	}

	public boolean removeCourse(String course_code) {
		if (!hasCourse(course_code)) {
			return false;
		}
		String query = "DELETE FROM COURSE WHERE COURSE_CODE = " + quo(course_code);
		return execute(query) != null;
	}

	public boolean hasProgram(String programCode) {
		String query = "SELECT * FROM PROGRAM WHERE PROGRAM_CODE = " + quo(programCode);
		try {
			return execute(query).set().isBeforeFirst();
		} catch (Throwable e) {
			// e.printStackTrace();
		}
		return false;
	}

	public boolean addProgram(String program_code, String program_name) {
		if (hasProgram(program_code)) {
			System.err.println("Program already exists!");
			return false;
		}
		String query = String.format("INSERT INTO PROGRAM VALUES (%s, %s)", quo(program_code), quo(program_name));
		return execute(query) != null;
	}

	public boolean removeProgram(String programCode) {
		if (!hasProgram(programCode)) {
			return false;
		}
		String query = "DELETE FROM PROGRAM WHERE PROGRAM_CODE = " + quo(programCode);
		return execute(query) != null;
	}

	public Result getExamsForProgram(String courseCode) {
		String query = "SELECT E.COURSE_CODE, START_TIME, END_TIME " +
				"FROM EXAM E, COURSE C, PROGRAM P " +
				"WHERE E.COURSE_CODE = C.COURSE_CODE " +
				"AND C.PROGRAM_CODE = P.PROGRAM_CODE " +
				"AND P.PROGRAM_CODE = " + quo(courseCode);
		return execute(query);
	}

	public boolean changePeriodTime(String period_id, Timestamp start, Timestamp end) {
		String query = "UPDATE PERIOD SET START_TIME = " + toDate(start) + ", END_TIME = " + toDate(end) + " WHERE PERIOD_ID = " + quo(period_id);
		return execute(query) != null;
	}

	public Result getExamsOnDay(String day_of_week) {
		String query = "SELECT E.COURSE_CODE, E.PERIOD_ID, E.DAY_OF_WEEK FROM EXAM E, DAY_SCHEDULE DS WHERE DS.DAY_OF_WEEK = " + quo(day_of_week) + " AND E.PERIOD_ID = DS.PERIOD_ID AND E.DAY_OF_WEEK = DS.DAY_OF_WEEK";
		return execute(query);
	}

	public Result searchForExam(String day_of_week, String period_id) {
		String query = "SELECT E.COURSE_CODE FROM EXAM E, DAY_SCHEDULE DS WHERE DS.DAY_OF_WEEK = " + quo(day_of_week) + " AND DS.PERIOD_ID = " + quo(period_id) + " AND E.PERIOD_ID = DS.PERIOD_ID AND E.DAY_OF_WEEK = DS.DAY_OF_WEEK";
		return execute(query);
	}

	public Result getExamsForRoom(String roomNum) {
		String query = "SELECT COURSE_CODE, START_TIME, END_TIME FROM EXAM WHERE ROOM_NUM = " + quo(roomNum);
		return execute(query);
	}

	public Result getExamsForTeacher(int teacherId) {
		String query = "SELECT E.COURSE_CODE, START_TIME, END_TIME FROM EXAM E, COURSE C WHERE E.COURSE_CODE = C.COURSE_CODE AND TEACHER_ID = " + teacherId;
		return execute(query);
	}

	public boolean hasExam(String courseCode, String roomNumber, int numOfPeriods, String periodId, String day_of_week) {
		String query = "SELECT * FROM EXAM WHERE COURSE_CODE = " + quo(courseCode) + " AND ROOM_NUM = " + quo(roomNumber) + " AND PERIOD_ID = " + periodId + "  AND NUM_OF_PERIODS = " + numOfPeriods + " AND DAY_OF_WEEK = " + quo(day_of_week);
		try {
			return execute(query).set().isBeforeFirst();
		} catch (Throwable e) {
			// e.printStackTrace();
		}
		return false;
	}

	public boolean addExam(String courseCode, String roomNumber, int numOfPeriods, String periodId, String day_of_week) {
		if (hasExam(courseCode, roomNumber, numOfPeriods, periodId, day_of_week)) {
			System.err.println("There is already an exam in that room!");
			return false;
		}
		String query = String.format("INSERT INTO EXAM VALUES (%s, %s, %d, %s, %s)", quo(courseCode), quo(roomNumber), numOfPeriods, quo(periodId), quo(day_of_week));
		return execute(query) != null;
	}

	public boolean removeExam(String courseCode, String roomNumber, int numOfPeriods, String periodId, String day_of_week) {
		if (!hasExam(courseCode, roomNumber, numOfPeriods, periodId, day_of_week)) {
			return false;
		}
		String query = "DELETE FROM EXAM WHERE COURSE_CODE = " + quo(courseCode) + " AND ROOM_NUM = " + quo(roomNumber) + " AND NUM_OF_PERIODS = " + numOfPeriods + " AND PERIOD_ID = " + quo(periodId) + " AND DAY_OF_WEEK = " + quo(day_of_week);
		return execute(query) != null;
	}

	public boolean hasRoom(String room_num, String room_desc) {
		String query = "SELECT * FROM ROOM WHERE ROOM_NUM = " + quo(room_num) + " AND ROOM_DESC = " + quo(room_desc);
		try {
			return execute(query).set().isBeforeFirst();
		} catch (Throwable e) {
			// e.printStackTrace();
		}
		return false;
	}

	public boolean addRoom(String room_num, String room_desc) {
		if (hasRoom(room_num, room_desc)) {
			System.err.println("That room already exists in the database!");
			return false;
		}
		String query = String.format("INSERT INTO ROOM VALUES (%s, %s)", quo(room_num), quo(room_desc));
		return execute(query) != null;
	}

	public boolean removeRoom(String room_num, String room_desc) {
		if (!hasRoom(room_num, room_desc)) {
			return false;
		}
		String query = "DELETE FROM ROOM WHERE ROOM_NUM = " + quo(room_num) + " AND ROOM_DESC = " + quo(room_desc);
		return execute(query) != null;
	}

	public boolean hasEnrollment(int student_id, String program_code) {
		String query = "SELECT * FROM ENROLLMENT WHERE STUDENT_ID = " + student_id + " AND PROGRAM_CODE = " + quo(program_code);
		try {
			return execute(query).set().isBeforeFirst();
		} catch (Throwable e) {
			// e.printStackTrace();
		}
		return false;
	}

	public boolean addEnrollment(int student_id, String program_code) {
		if (hasEnrollment(student_id, program_code)) {
			System.err.println("Duplicate enrollment!");
			return false;
		}
		String query = String.format("INSERT INTO ENROLLMENT VALUES (%d, %s, %s, %s)", student_id, quo(program_code));
		return execute(query) != null;
	}

	public boolean removeEnrollment(int student_id, String program_code) {
		if (!hasEnrollment(student_id, program_code)) {
			return false;
		}
		String query = "DELETE FROM ENROLLMENT WHERE STUDENT_ID = " + student_id + " AND PROGRAM_CODE = " + quo(program_code);
		return execute(query) != null;
	}

	public boolean hasTeacher(int teacher_id) {
		String query = "SELECT * FROM TEACHER WHERE TEACHER_ID = " + teacher_id;
		try {
			return execute(query).set().isBeforeFirst();
		} catch (Throwable e) {
			// e.printStackTrace();
		}
		return false;
	}

	public boolean addTeacher(int teacher_id, String password, String first_name, String last_name) {
		if (hasTeacher(teacher_id)) {
			System.err.println("That teacher already exists!");
			return false;
		}
		String query = String.format("INSERT INTO TEACHER VALUES (%d, %s, %s, %s)", teacher_id, quo(password), quo(first_name), quo(last_name));
		return execute(query) != null;
	}

	public boolean removeTeacher(int teacher_id) {
		if (!hasTeacher(teacher_id)) {
			return false;
		}
		String query = "DELETE FROM TEACHER WHERE TEACHER_ID = " + teacher_id;
		return execute(query) != null;
	}

	public boolean hasAdmin(int admin_id) {
		String query = "SELECT * FROM ADMIN WHERE ADMIN_ID = " + admin_id;
		try {
			return execute(query).set().isBeforeFirst();
		} catch (Throwable e) {
			// e.printStackTrace();
		}
		return false;
	}

	public boolean addAdmin(int admin_id, String password, String first_name, String last_name) {
		if (hasAdmin(admin_id)) {
			System.err.println("That admin already exists!");
			return false;
		}
		String query = String.format("INSERT INTO ADMIN VALUES (%d, %s, %s, %s)", admin_id, quo(password), quo(first_name), quo(last_name));
		return execute(query) != null;
	}

	public boolean removeAdmin(int admin_id) {
		if (!hasAdmin(admin_id)) {
			return false;
		}
		String query = "DELETE FROM ADMIN WHERE ADMIN_ID = " + admin_id;
		return execute(query) != null;
	}

	public boolean hasDaySchedule(String period_id, String day_of_week) {
		String query = "SELECT * FROM DAY_SCHEDULE WHERE PERIOD_ID = " + quo(period_id) + " AND DAY_OF_WEEK = " + quo(day_of_week);
		try {
			return execute(query).set().isBeforeFirst();
		} catch (Throwable e) {
			// e.printStackTrace();
		}
		return false;
	}

	public boolean addDaySchedule(String period_id, String day_of_week) {
		if (hasDaySchedule(period_id, day_of_week)) {
			System.err.println("That day schedule already exists!");
			return false;
		}
		String query = String.format("INSERT INTO DAY_SCHEDULE VALUES (%s, %s)", quo(period_id), quo(day_of_week));
		return execute(query) != null;
	}

	public boolean removeDaySchedule(String period_id, String day_of_week) {
		if (!hasDaySchedule(period_id, day_of_week)) {
			return false;
		}
		String query = "DELETE FROM DAY_SCHEDULE WHERE PERIOD_ID = " + quo(period_id) + " AND DAY_OF_WEEK = " + quo(day_of_week);
		return execute(query) != null;
	}

	public boolean hasDay(String day_of_week) {
		String query = "SELECT * FROM DAY WHERE DAY_OF_WEEK = " + quo(day_of_week);
		try {
			return execute(query).set().isBeforeFirst();
		} catch (Throwable e) {
			// e.printStackTrace();
		}
		return false;
	}

	public boolean addDay(String day_of_week) {
		if (hasDay(day_of_week)) {
			System.err.println("That day already exists!");
			return false;
		}
		String query = String.format("INSERT INTO DAY VALUES (%s)", quo(day_of_week));
		return execute(query) != null;
	}

	public boolean removeDay(String day_of_week) {
		if (!hasDay(day_of_week)) {
			return false;
		}
		String query = "DELETE FROM DAY WHERE DAY_OF_WEEK = " + quo(day_of_week);
		return execute(query) != null;
	}

	public boolean hasPeriod(String period_id) {
		String query = "SELECT * FROM PERIOD WHERE PERIOD_ID = " + quo(period_id);
		try {
			return execute(query).set().isBeforeFirst();
		} catch (Throwable e) {
			// e.printStackTrace();
		}
		return false;
	}

	public boolean addPeriod(String period_id, Timestamp start_time, Timestamp end_time) {
		if (hasPeriod(period_id)) {
			System.err.println("That period already exists!");
			return false;
		}
		String query = String.format("INSERT INTO PERIOD VALUES (%s, %s, %s)", quo(period_id), toDate(start_time), toDate(end_time));
		return execute(query) != null;
	}

	public boolean removePeriod(String period_id) {
		if (!hasPeriod(period_id)) {
			return false;
		}
		String query = "DELETE FROM PERIOD WHERE PERIOD_ID = " + quo(period_id);
		return execute(query) != null;
	}


	public String quo(Object s) {
		return "'" + s + "'";
	}

	public String toDate(Timestamp ts) {
		return "to_date(" + quo(ts.toString().replaceAll("\\.[^.]*$", "")) + ", 'YYYY/MM/DD HH24:MI:SS')";
	}
}

