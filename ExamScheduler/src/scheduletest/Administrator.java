/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduletest;

import com.database.config.DatabaseConnection;
import com.database.tables.impl.Result;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.StringTokenizer;

/**
 *
 * @author n00024233
 */
public class Administrator extends Account {

    public Administrator() {
    
    }
    
    public ExamMap getExams() {
        
        ExamMap exams = new ExamMap();
        DatabaseConnection connection = DatabaseConnection.open();
        ResultSet examSet = connection.execute("select * from exam").set();
        ResultSet courseSet = connection.execute("select * from course").set();
        try {
            while (examSet.next()) {
                Exam exam = new Exam();
                
                System.out.println(examSet.getString("start_time"));
                System.out.println(examSet.getString("end_time"));
            }
        }
        catch (SQLException ex) {
                    
        }
        
        return new ExamMap();
        
    }
}
