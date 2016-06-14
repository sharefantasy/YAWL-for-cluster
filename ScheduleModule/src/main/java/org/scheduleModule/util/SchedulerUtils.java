package org.scheduleModule.util;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Created by fantasy on 2016/5/16.
 */
public class SchedulerUtils {
    public static Document stringToDocument(String xmlString) {
        SAXBuilder builder = new SAXBuilder();
        Document document = null;
        try {
            document = builder.build(new StringReader(xmlString));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return document;
    }

    public static String DocumentToString(Document document) {
        if (document == null)
            return null;
        XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
        return out.outputString(document);
    }

    public static String failure(String reason) {
        return String.format("<failure><reason>%s</reason></failure>", reason);
    }

    public static final String SUCCESS = "<success/>";

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

    public static String wrap(String out) {
        return String.format("<response>%s</response>", out);
    }

    public static final String INVALID_USER = failure("Invalid user");

}
