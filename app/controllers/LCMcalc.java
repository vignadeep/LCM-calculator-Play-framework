package controllers;

import play.mvc.*;
import play.data.DynamicForm;
import play.data.Form;
import play.data.FormFactory;
import javax.inject.*;
import java.util.*;
import java.sql.*;

public class LCMcalc extends Controller {

    public static long LCM;

    @Inject FormFactory formFactory;
    public Result calculate() {
        String user=Application.getusername();
        DynamicForm form = formFactory.form().bindFromRequest();;
        String algorithm=form.get("algorithm");
        String str=form.get("nums");
        String[] nums_s=str.split(",");
        long lcm=1;
        long[] nums=new long[nums_s.length];
        for(int i=0;i<nums_s.length;i++)
        nums[i]=Integer.parseInt(nums_s[i].trim());
        Runtime runtime = Runtime.getRuntime();
        long before_memory=runtime.totalMemory() - runtime.freeMemory();
        long startTime = System.currentTimeMillis();
        if(algorithm.equals("optimal"))
        lcm=optimal_lcm(nums);
        else if(algorithm.equals("time"))
        lcm=time_lcm(nums);
        else 
        lcm=space_lcm(nums);
        long elapsedTime = System.currentTimeMillis() - startTime;
        long after_memory=runtime.totalMemory() - runtime.freeMemory();
        long memory = after_memory - before_memory;
        return update_history_DB(user,str,lcm,algorithm,elapsedTime,memory);
    }
    
    public Result History()
    {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection con=DriverManager.getConnection("jdbc:oracle:thin:@(description=(address=(host=database-identifier.cl4nltjuooua.ap-south-1.rds.amazonaws.com)(protocol=tcp)(port=1521))(connect_data=(sid=ORCL)))","admin","vishnusaran100");
            PreparedStatement stmt=con.prepareStatement("select * from history where email='"+Application.getusername()+"' and rownum<21 order by execution_date");  
            ResultSet rs=stmt.executeQuery();  
            List<List<String>> list = new ArrayList<List<String>>();
            int row=0;
            while (rs.next()) { 
                list.add(new ArrayList<String>());
                list.get(row).add(rs.getString(2));
                list.get(row).add(rs.getString(3));
                list.get(row).add(rs.getString(4));
                list.get(row).add(rs.getString(5));
                list.get(row).add(rs.getString(6));
                list.get(row).add(rs.getString(7));
                row++;
            }
            con.close();
            return ok(views.html.history.render(list));
        } catch (Exception e) {
            return ok(e+"");
        }

}
    public Result update_history_DB(String user,String nums,long lcm,String alg,long time,long mem)
    {
        try {
        Class.forName("oracle.jdbc.driver.OracleDriver");
        Connection con=DriverManager.getConnection("jdbc:oracle:thin:@(description=(address=(host=database-identifier.cl4nltjuooua.ap-south-1.rds.amazonaws.com)(protocol=tcp)(port=1521))(connect_data=(sid=ORCL)))","admin","vishnusaran100");
        PreparedStatement stmt=con.prepareStatement("insert into history values('"+user+"','"+nums+"',"+lcm+",'"+alg+"',"+time+","+mem+",sysdate)");
        int ret=stmt.executeUpdate();
        con.close();
        return ok(views.html.LCMcalc.render(lcm+" "));
        }
        catch (Exception e) {
            return ok(e+"");
        }


    }

    public static long bytesToKilobytes(long bytes) {
        long KILOBYTE = 1024;
        return bytes / KILOBYTE;
    }

    public static long optimal_lcm(long[] array)
    {
        LCM=1;
        while (true) {
            int minDivisor = returnFirstDivisor(returnMinValue(array)); 
            if (minDivisor == -1) break; 
            
            for (int i = 0; i < array.length; ++i) {
                if (array[i] % minDivisor == 0) {
                    array[i] /= minDivisor;
                }
            }
        }
        return LCM;
    }

    private static long returnMinValue(long[] array) {
        long min = Long.MAX_VALUE;
        for (int i = 0; i < array.length; ++i) {
            if (array[i] != 1 && array[i] < min) {
                min = array[i];
            }
        }
        return min; //When this returns Integer.MAX_VALUE, we are done
    }

    private static int returnFirstDivisor(long val) {
        if (val == Long.MAX_VALUE) {
            return -1; 
        } else {
            for (int i = 2; i <= val; ++i) {
                if (val % i == 0) {
                    LCM *= i;
                    return i;
                }
            }
        }
        return -10;
    }

    public static long space_lcm(long[] arr)
    {
        long number1, number2, lcm = 1;		
        for(int k=0; k<arr.length;k++) 
        {			
	        number1 = lcm;			
	        lcm = 1;					
	        number2 = arr[k];			
            for (int i = 2; i <= (number1 * number2); i++) 
            {				
		    if ((number1 % i == 0) && (number2 % i == 0)) 
		    {					
			    lcm = lcm * i;					
			    number1 = number1 / i;					
			    number2 = number2 / i;					
			    i = i - 1;					
		    }
	        }			
	    lcm = lcm * number1 * number2;					
        }		
        return lcm;
    }

    private static long gcd_time(long x, long y)
    {
        if (y == 0)
            return x;
        return gcd_time(y, x % y);
    }
    private static long time_lcm(long[] arr)
    {
        long ans = arr[0];
        for (int i = 1; i < arr.length; i++)
        ans = (((arr[i] * ans)) / (gcd_time(arr[i], ans)));
        return ans;
    }
}