package controllers;

import play.mvc.*;
import java.util.*;
import java.sql.*;
import javax.inject.*;
import play.data.DynamicForm;
import play.data.Form;
import play.data.FormFactory;
/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class Application extends Controller {

    private static String username;
    public static void setusername(String s)
    {
        username=s;
    }
    public static String getusername()
    {
        return username;
    }
    Application()
    {
        username="";
    }
    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
    public Result index() {
        return ok(views.html.index.render(""));
    }

    @Inject FormFactory formFactory;
    public Result login()
    {
        try {
            DynamicForm form = formFactory.form().bindFromRequest();;
            String user=form.get("user");
            String pass=form.get("pass");
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection con=DriverManager.getConnection("jdbc:oracle:thin:@(description=(address=(host=database-identifier.cl4nltjuooua.ap-south-1.rds.amazonaws.com)(protocol=tcp)(port=1521))(connect_data=(sid=ORCL)))","admin","vishnusaran100");
            PreparedStatement stmt=con.prepareStatement("select count(*) as count from users where username='"+user+"' and password='"+pass+"'");  
            ResultSet rs=stmt.executeQuery();
            boolean valid=rs.next() && rs.getInt("COUNT")==1;
            con.close();  
            if(valid)
            {
            setusername(user);
            return ok(views.html.LCMcalc.render(""));
            }
            else
            return ok(views.html.index.render("username and password doesn't match. Try again!"));
        } catch (Exception e) {
            return ok(e+"");
        }

    }


}
