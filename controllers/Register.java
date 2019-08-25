package controllers;

import play.mvc.*;
import java.sql.*;
import play.data.DynamicForm;
import play.data.Form;
import play.data.FormFactory;
import javax.inject.*;
import java.util.*;

public class Register extends Controller {

    public Result register() {
        return ok(views.html.register.render(""));
    }

    @Inject FormFactory formFactory;
    public Result registerdata()
    {
        try {
        DynamicForm form = formFactory.form().bindFromRequest();;
        String user=form.get("email");
        String pass=form.get("password");
        String conf_pass=form.get("confirm_password");
        Class.forName("oracle.jdbc.driver.OracleDriver");
        Connection con=DriverManager.getConnection("jdbc:oracle:thin:@(description=(address=(host=database-identifier.cl4nltjuooua.ap-south-1.rds.amazonaws.com)(protocol=tcp)(port=1521))(connect_data=(sid=ORCL)))","admin","vishnusaran100");
        PreparedStatement stmt=con.prepareStatement("select count(*) as count from users where username='"+user+"'");  
        ResultSet rs=stmt.executeQuery(); 
        if(!pass.equals(conf_pass))
        return ok(views.html.register.render("password not matching!"));
        boolean exists=rs.next() && rs.getInt("COUNT")>=1;
        if(exists)
        return ok(views.html.index.render("User already Exists! Login here"));
        PreparedStatement stmt2=con.prepareStatement("insert into users values('"+user+"','"+pass+"')");
        int res=stmt2.executeUpdate();
        if(res==1)
        return ok(views.html.index.render("User created."));
        else
        return ok(views.html.index.render("User not created. something wrong"));
    }
    catch(Exception e){
        return ok(e+"");
    }

}
}