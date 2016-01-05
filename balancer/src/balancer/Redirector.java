package balancer;

import org.apache.http.HttpRequest;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.yawlfoundation.yawl.engine.interfce.Interface_Client;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by fantasy on 2015/9/7.
 */

class proxy extends Interface_Client{
    public String result() throws IOException {
        return executeGet("http://localhost:8080/resourceService",new HashMap<String ,String>());
    }
}
public class Redirector extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html; charset=UTF-8");
        OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream(), "UTF-8");
        StringBuilder output = new StringBuilder();
        proxy p = new proxy();


//        CloseableHttpClient client = HttpClients.createDefault();
//        HttpPost post = new HttpPost("http://www.baidu.com");
//        CloseableHttpResponse response1 =  client.execute(post);
//        output.append(response1.toString());
//        response1.close();
//        output.append("<response>");
        output.append(p.result());
//        output.append("</response>");
        writer.write(output.toString());
        writer.flush();
        writer.close();

    }

}
