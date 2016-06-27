package org.yawlfoundation.cluster.scheduleModule.util;


import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.yawlfoundation.cluster.scheduleModule.entity.Response;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.UUID;

/**
 * Created by fantasy on 2016/5/16.
 */
@SuppressWarnings("unchecked")
public class SchedulerUtils {

    static {

    }

    public static String documentToString(Document document) {
        OutputFormat format = OutputFormat.createPrettyPrint();
        StringWriter writer = new StringWriter();
        XMLWriter xmlWriter = new XMLWriter(writer, format);
        format.setEncoding("UTF-8");
        format.setIndent(true);
        try {
            xmlWriter.write(document);
        } catch (IOException e) {
            return null;
        }

        return writer.toString();
    }

    public static String failure(String reason) {
        return String.format("<failure><reason>%s</reason></failure>", reason);
    }

    public static Response failure2(String reason) {
        Response response = new Response();
        response.reason = reason;
        return response;
    }

    public static Response answer(String answer) {
        Response response = new Response();
        response.answer = answer;
        return response;
    }

    public static final String SUCCESS = "<success/>";
    public static final String WRAP_SUCCESS = "<response><success/></response>";

    public static boolean isInvalidSession(String result) {
        try {
            UUID.fromString(result);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static final Response INVALID_USER = failure2("Invalid user");
    public static final Response INVALID_SESSION = failure2("Invalid or expired session.");


    public static String wrap(Document result) {

        if (result.getRootElement().getName().equalsIgnoreCase("response")) {
            return documentToString(result);
        }
        Element res = DocumentHelper.createElement("response");
        for (Element e : (List<Element>) result.getRootElement().elements()) {
            res.add(e.createCopy());
        }
        return documentToString(result);
    }

}
