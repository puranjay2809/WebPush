import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class SubscriptionLogger extends HttpServlet {

    public void service(HttpServletRequest reqq , HttpServletResponse ress) throws IOException {
        String parameter = reqq.getParameter("data");
        PrintWriter out = ress.getWriter();
        out.println("Token " + parameter);
    }
}
