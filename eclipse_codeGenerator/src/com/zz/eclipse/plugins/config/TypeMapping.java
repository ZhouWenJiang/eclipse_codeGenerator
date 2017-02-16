/*
 * Copyright 2014 ptma@163.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zz.eclipse.plugins.config;


import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.zz.eclipse.plugins.db.model.Column;
import com.zz.eclipse.plugins.db.model.util.JavaTypeResolver;
import com.zz.eclipse.plugins.db.model.util.JdbcTypeResolver;


public class TypeMapping implements Serializable {

  private static final long    serialVersionUID = 8573950623347746321L;

  private Map<Integer, String> typeMap;
  private Map<Integer, String> fullTypeMap;

  public TypeMapping() {
    typeMap = new HashMap<Integer, String>();
    fullTypeMap = new HashMap<Integer, String>();
    loadMappgin();
  }

  public void addMappin( String sSqlType, String javaType, String fullTYpe ) {
    Integer sqlType = JdbcTypeResolver.getJdbcType(sSqlType);
    typeMap.put(sqlType, javaType);
    fullTypeMap.put(sqlType, fullTYpe);
  }

  public void loadMappgin() {
    addMappin("ARRAY", "Object", "java.lang.Object");
    addMappin("BIGINT", "Long", "java.lang.Long");
    addMappin("BINARY", "byte[]", "byte[]");
    addMappin("BIT", "Boolean", "java.lang.Boolean");
    addMappin("BLOB", "byte[]", "byte[]");
    addMappin("BOOLEAN", "Boolean", "java.lang.Boolean");
    addMappin("CHAR", "String", "java.lang.String");
    addMappin("CLOB", "String", "java.lang.String");
    addMappin("DATALINK", "String", "java.lang.String");
    addMappin("DATE", "Date", "java.sql.Date");
    addMappin("DECIMAL", "BigDecimal", "java.math.BigDecimal");
    addMappin("DISTINCT", "Object", "java.lang.Object");
    addMappin("DOUBLE", "Double", "java.lang.Double");
    addMappin("FLOAT", "Double", "java.lang.Double");
    addMappin("INTEGER", "Integer", "java.lang.Integer");
    addMappin("JAVA_OBJECT", "Object", "java.lang.Object");
    addMappin("LONGNVARCHAR", "String", "java.lang.String");
    addMappin("LONGVARBINARY", "byte[]", "byte[]");
    addMappin("LONGVARCHAR", "String", "java.lang.String");
    addMappin("NCHAR", "String", "java.lang.String");
    addMappin("NCLOB", "String", "java.lang.String");
    addMappin("NVARCHAR", "String", "java.lang.String");
    addMappin("NULL", "Object", "java.lang.Object");
    addMappin("NUMERIC", "BigDecimal", "java.math.BigDecimal");
    addMappin("OTHER", "Object", "java.lang.Object");
    addMappin("REAL", "Float", "java.lang.Float");
    addMappin("REF", "Object", "java.lang.Object");
    addMappin("SMALLINT", "Short", "java.lang.Short");
    addMappin("STRUCT", "Object", "java.lang.Object");
    addMappin("TIME", "Date", "java.sql.Date");
    addMappin("TIMESTAMP", "Date", "java.sql.Date");
    addMappin("TINYINT", "Byte", "java.lang.Byte");
    addMappin("VARBINARY", "byte[]", "byte[]");
    addMappin("VARCHAR", "String", "java.lang.String");

  }

  private Properties parseAttributes( Node node ) {
    Properties attributes = new Properties();
    NamedNodeMap nnm = node.getAttributes();
    for( int i = 0; i < nnm.getLength(); i++ ) {
      Node attribute = nnm.item(i);
      String value = attribute.getNodeValue();
      attributes.put(attribute.getNodeName(), value);
    }

    return attributes;
  }

  public String calculateJavaType( Column column ) {
    String javaType = typeMap.get(column.getJdbcType());

    if(javaType == null) {
      javaType = JavaTypeResolver.calculateJavaType(column);
    }
    return javaType;
  }

  public String calculateFullJavaType( Column column ) {
    String javaType = fullTypeMap.get(column.getJdbcType());

    if(javaType == null) {
      javaType = JavaTypeResolver.calculateFullJavaType(column);
    }
    return javaType;
  }

  public String[] getAllJavaTypes() {
    Set<String> javaTypeSet = new HashSet<String>();
    javaTypeSet.addAll(typeMap.values());
    if(javaTypeSet.isEmpty()) {
      return JavaTypeResolver.getAllJavaTypes();
    }

    String[] values = new String[javaTypeSet.size()];
    int index = 0;
    for( String itemValue : javaTypeSet ) {
      values[index++] = itemValue;
    }
    return values;
  }
}
