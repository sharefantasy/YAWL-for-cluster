package org.scheduleModule.util;


import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.scheduleModule.entity.Response;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Created by fantasy on 2016/5/16.
 */
public class SchedulerUtils {
    public static Document stringToDocument(String xmlString) throws DocumentException {
        return DocumentHelper.parseText(xmlString);
    }

    public static String documentToString(Document document) {
        return document.asXML();
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

    public static String stringfy(List<String> results, String name) {
        StringBuilder sb = new StringBuilder();
        for (String r : results) {
            sb.append(String.format("<%s>%s</%s>", name, r, name));
        }
        return sb.toString();
    }

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

    public static String wrap(String result) {

        Document doc;
        try {
            doc = DocumentHelper.parseText(result);
        } catch (DocumentException e) {
            return String.format("<response>%s</response>", result);
        }

        if (doc.getRootElement().element("response") != null) {
            return result;
        }
        Element res = DocumentHelper.createElement("response");
        for (Object e : doc.getRootElement().elements()) {
            res.add(((Element) e).createCopy());
        }

        return res.asXML();
    }
}
