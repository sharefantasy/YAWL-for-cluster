/*
 * Copyright (c) 2004-2012 The YAWL Foundation. All rights reserved.
 * The YAWL Foundation is a collaboration of individuals and
 * organisations who are committed to improving workflow technology.
 *
 * This file is part of YAWL. YAWL is free software: you can
 * redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation.
 *
 * YAWL is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General
 * Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with YAWL. If not, see <http://www.gnu.org/licenses/>.
 */

package org.yawlfoundation.yawl.authentication;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.jdom2.Element;

import java.io.Serializable;

/**
 * Represents the authentication credentials of an external application that may connect
 * to the Engine via the various interfaces (as opposed to a custom service).
 * <p/>
 * Note that the generic user "admin" is represented by an instance of this class.
 *
 * @author Michael Adams
 * @since 2.1
 * @date 23/11/2009
 *
 */

public class YExternalClient extends YClient implements Serializable {

    private String engine;

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public YExternalClient() { super(); }

    public YExternalClient(String userID, String password, String documentation) {
        super(userID, password, documentation);
    }

    public YExternalClient(Element xml) {
        super(xml);
    }


    // For JSF table

    public String get_userid() { return _userName; }

    public String get_documentation() { return _documentation; }
    public int hashCode(){
        return new HashCodeBuilder(-528253723, -475504089)
                .appendSuper(super.hashCode())
                .append(this._userName)
                .append(this.engine)
                .toHashCode();
    }
    public boolean equals(Object obj) {
        return obj instanceof YExternalClient
                && ((YExternalClient) obj)._userName.equals(_userName)
                && ((YExternalClient) obj).engine.equals(engine);
    }
}