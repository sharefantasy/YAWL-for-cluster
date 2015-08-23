package cluster;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

/**
 * Created by fantasy on 2015/8/3.
 */
public class server extends HttpServlet {
    private interfaceA_reader reader = new interfaceA_reader();
    public void init() throws ServletException {
        reader.init();

    }
}
