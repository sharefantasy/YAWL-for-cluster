package org.yawlfoundation.cluster.scheduleModule.util;


import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.jdom2.Content;
import org.yawlfoundation.cluster.scheduleModule.entity.Response;
import org.yawlfoundation.yawl.util.StringUtil;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * Created by fantasy on 2016/5/16.
 */
@SuppressWarnings("unchecked")
public class SchedulerUtils {

    static {

    }

	public static String elementToString(Element element) {
		OutputFormat format = OutputFormat.createCompactFormat();
        StringWriter writer = new StringWriter();
        XMLWriter xmlWriter = new XMLWriter(writer, format);
		format.setExpandEmptyElements(false);

        try {
			xmlWriter.write(element);
        } catch (IOException e) {
            return null;
        }
        return writer.toString();
    }
	public static String documentToString(Document document) {
		return elementToString(document.getRootElement());
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
	public static final String WRAP_INVALIDACTION_EXCEPTION = "<response><failure><reason>Invalid action or exception was thrown.</reason></failure></response>";

	public static boolean isInvalidAction(String result) {
		return result.contains("failure");
    }

    public static final Response INVALID_USER = failure2("Invalid user");
    public static final Response INVALID_SESSION = failure2("Invalid or expired session.");

	public static String wrap(String result) {
		Document doc;
		try {
			doc = DocumentHelper.parseText(result);
		} catch (DocumentException e) {
			return StringUtil.wrap(result, "response");
		}
		return wrap(doc);
	}
    public static String wrap(Document result) {

        if (result.getRootElement().getName().equalsIgnoreCase("response")) {
            return documentToString(result);
        }
        Element res = DocumentHelper.createElement("response");
        for (Element e : (List<Element>) result.getRootElement().elements()) {
            res.add(e.createCopy());
        }
		return documentToString(DocumentHelper.createDocument(res));
    }

	public static String unwrap(String result) {
		Document doc;
		try {
			doc = DocumentHelper.parseText(result);
		} catch (DocumentException e) {
			return result;
		}
		Element root = doc.getRootElement();
		if (!root.getName().equals("response"))
			return result;
		if (root.isTextOnly())
			return root.getTextTrim();
		StringBuilder sb = new StringBuilder();
		for (Element e : (List<Element>) root.elements()) {
			sb.append(elementToString(e));
		}
		return sb.toString();
	}
	public static List<Element> removeElement(Element element) {
		List<Element> elements = new ArrayList<>(element.elements().size());
		Iterator eiter = element.elementIterator();
		while (eiter.hasNext()) {
			elements.add((Element) eiter.next());
			eiter.remove();
		}
		return elements;
	}
}
