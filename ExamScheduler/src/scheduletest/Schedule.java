
package scheduletest;

import java.util.HashMap;
import java.util.Map;

public class Schedule {
    
    public enum Period { A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P };
    public enum Day { Mon, Tues, Wed, Thurs, Fri, Sat };
    
    private Map<Period, SchedulePeriod> schedule;
    
    public Schedule() {
       
        
        
    }
    
    public void addExam(Period period, Day day, Exam exam) {
        
    }
    
    public void removeExam(Period period, Day day, String examID) {
        
    }
    
    public Exam getExam(Period period, Day day, String examID) {
        return new Exam();
    }
    
    public void addExamMap(Period period, Day day, ExamMap examMap) {
        
    }
    
    public void removeExamMap(Period period, Day day, String examID) {
        schedule.get(period).removeExam(day, examID);
    }
    
    public ExamMap getExamMap(Period period, Day day, String examID) {
        return schedule.get(period).getExamMap(day);
    }
    
    public void removeTimeSlot(Period period) {
        schedule.remove(period);
    }
    
    public void addTimeSlot(Period period, int startHour, int startMin, int endHour, int endMin) {
        
        if (schedule.containsKey(period)) {
            System.err.println("Slot already exists. Please delete slot or choose an unoccupied slot");
        }
        else {
            SchedulePeriod newPeriod = new SchedulePeriod(startHour, startMin, endHour, endMin);
            schedule.put(period, newPeriod);
        }
        
    }
    
    public void changeSlotStartTime(Period period, int numOfHours, int numOfMin) {
        schedule.get(period).changeStartTime(numOfHours, numOfMin);
    }
    
    public void changeSlotEndTime(Period period, int numOfHours, int numOfMin) {
        schedule.get(period).changeEndTime(numOfHours, numOfMin);
    }
    
}
