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

package org.yawlfoundation.yawl.schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Michael Adams Creation Date: 14/09/2008
 */
public enum eXSDType {

	INVALID_TYPE("invalid_type", -1), ANY_TYPE("anyType", 0),

	// Numeric Types
	INTEGER("integer", 1), // Integral
	POSITIVE_INTEGER("positiveInteger", 2), NEGATIVE_INTEGER("negativeInteger", 3), NON_POSITIVE_INTEGER(
			"nonPositiveInteger", 4), NON_NEGATIVE_INTEGER("nonNegativeInteger", 5), INT("int", 6), LONG("long",
					7), SHORT("short", 8), BYTE("byte", 9), UNSIGNED_LONG("unsignedLong",
							10), UNSIGNED_INT("unsignedInt", 11), UNSIGNED_SHORT("unsignedShort",
									12), UNSIGNED_BYTE("unsignedByte", 13), DOUBLE("double", 14), // Non-integral
	FLOAT("float", 15), DECIMAL("decimal", 16),

	// String Types
	STRING("string", 17), NORMALIZED_STRING("normalizedString", 18), TOKEN("token", 19), LANGUAGE("language",
			20), NMTOKEN("NMTOKEN", 21), NMTOKENS("NMTOKENS", 22), NAME("Name", 23), NCNAME("NCName", 24),

	// Date Time Types
	DATE("date", 25), TIME("time", 26), DATETIME("dateTime", 27), DURATION("duration", 28), GDAY("gDay",
			29), GMONTH("gMonth", 30), GYEAR("gYear", 31), GMONTHDAY("gMonthDay", 32), GYEARMONTH("gYearMonth", 33),

	// 'Magic' Types
	ID("ID", 34), IDREF("IDREF", 35), IDREFS("IDREFS", 36), ENTITY("ENTITY", 37), ENTITIES("ENTITIES", 38),

	// Other Types
	QNAME("QName", 39), BOOLEAN("boolean", 40), HEX_BINARY("hexBinary", 41), BASE64_BINARY("base64Binary",
			42), NOTATION("notation", 43), ANY_URI("anyURI", 44);

	private final String _name;
	private final int _index;

	private static final Map<String, eXSDType> _fromStringMap = new HashMap<String, eXSDType>();

	static {
		for (eXSDType version : values()) {
			_fromStringMap.put(version.toString(), version);
		}
	}

	// constructor
	eXSDType(String name, int index) {
		_name = name;
		_index = index;
	}

	public static enum RestrictionFacet {
		minExclusive, maxExclusive, minInclusive, maxInclusive, minLength, maxLength, length, totalDigits, fractionDigits, whiteSpace, pattern, enumeration
	}

	public String getString() {
		return _name;
	}

	public int getIndex() {
		return _index;
	}

	public static boolean isBuiltInType(String typeStr) {
		return _fromStringMap.containsKey(typeStr);
	}

	// public static boolean isNumericType(String typeStr) {
	// eXSDType type = _fromStringMap.get(typeStr);
	// return type != null && (type.ordinal() >= INTEGER.ordinal()) && (ordinal
	// <= DECIMAL);
	// }
	//
	// public static boolean isIntegralType(String type) {
	// int ordinal = getOrdinal(type);
	// return (ordinal >= INTEGER) && (ordinal <= UNSIGNED_BYTE);
	// }
	//
	// public static boolean isFloatType(String type) {
	// int ordinal = getOrdinal(type);
	// return (ordinal >= DOUBLE) && (ordinal <= DECIMAL);
	// }
	//
	// public static boolean isBooleanType(String type) {
	// return getOrdinal(type) == BOOLEAN;
	// }
	//
	// public static boolean isDateType(String type) {
	// int ordinal = getOrdinal(type);
	// return (ordinal >= DATE) && (ordinal <= DATETIME);
	// }
	//
	// public static boolean isStringForType(String s, int type) {
	// return getString(type).equals(s);
	// }
	//
	// public static List<String> getBuiltInTypeList() {
	// return new ArrayList<String>(_typeList); // send a copy
	// }
	//
	// public static String[] getBuiltInTypeArray() {
	// return _typeList.toArray(new String[_typeList.size()]);
	// }
	//
	// public static char[] getConstrainingFacetMap(String type) {
	// String vMap;
	// switch (getOrdinal(type)) {
	// case INTEGER:
	// case POSITIVE_INTEGER:
	// case NEGATIVE_INTEGER:
	// case NON_POSITIVE_INTEGER:
	// case NON_NEGATIVE_INTEGER:
	// case INT:
	// case LONG:
	// case SHORT:
	// case UNSIGNED_LONG:
	// case UNSIGNED_INT:
	// case UNSIGNED_SHORT:
	// case UNSIGNED_BYTE: vMap = "111100010111"; break;
	// case STRING:
	// case NORMALIZED_STRING:
	// case TOKEN:
	// case LANGUAGE:
	// case NMTOKEN:
	// case NMTOKENS:
	// case NAME:
	// case NCNAME:
	// case ID:
	// case IDREF:
	// case IDREFS:
	// case ENTITY:
	// case ENTITIES:
	// case QNAME:
	// case HEX_BINARY:
	// case BASE64_BINARY:
	// case NOTATION:
	// case ANY_URI: vMap = "000011100111"; break;
	// case DOUBLE:
	// case FLOAT:
	// case DATE:
	// case TIME:
	// case DATETIME:
	// case DURATION:
	// case GDAY:
	// case GMONTH:
	// case GYEAR:
	// case GMONTHDAY:
	// case GYEARMONTH: vMap = "111100000111"; break;
	// case BOOLEAN: vMap = "000000000110"; break;
	// case BYTE: vMap = "111100110111"; break;
	// case DECIMAL: vMap = "111100011111"; break;
	// case ANY_TYPE:
	// default: vMap = "000000000000"; break;
	// }
	// return vMap.toCharArray() ;
	// }
	//
	//
	// public static boolean isValidFacet(String facetName, String type) {
	// char[] validationMap = getConstrainingFacetMap(type);
	// try {
	// RestrictionFacet facet = RestrictionFacet.valueOf(facetName);
	// int ordinal = facet.ordinal();
	// return validationMap[ordinal] == '1';
	// }
	// catch (IllegalArgumentException iae) {
	// return false; // invalid restriction name
	// }
	// }
	//
	//
	// private static String[] makeYAWLTypeArray() {
	// String[] simpleYAWLTypes = {"NCName", "anyURI", "boolean", "date",
	// "double",
	// "duration", "long", "string", "time" } ;
	// return simpleYAWLTypes;
	// }
	//
	//
	// private static List<String> makeList() {
	// List<String> typeList = new ArrayList<String>();
	// for (int i = ANY_TYPE; i<= ANY_URI; i++) {
	// typeList.add(getString(i));
	// }
	// return typeList;
	// }

}
