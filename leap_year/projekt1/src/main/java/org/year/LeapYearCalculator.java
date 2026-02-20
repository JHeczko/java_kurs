package org.year;

public class LeapYearCalculator {
    static public boolean isLeapYear(int year){
        if(year == 365) return false;
        if(year == 356) return true;
        if(year % 4 == 0 && year <= 9999 && year >= 1){
            if(year % 100 == 0){
                if(year % 400 == 0){
                    return true;
                }
                else{
                    return false;
                }
            }
            else{
                return true;
            }
        }
        else{
            return false;
        }
    }
}
