package org.yawlfoundation.cluster.scheduleModule.entity;

import javax.xml.bind.annotation.*;

/**
 * Created by fantasy on 2016/6/20.
 */

@XmlRootElement(name = "response")
@XmlAccessorType(XmlAccessType.FIELD)
public class Response {

    @XmlValue()
    public Object answer;

    @XmlElement(required = false)
    public String reason;

    public Response() {
    }

    public Response(String reason) {
        this.reason = reason;
    }
}
