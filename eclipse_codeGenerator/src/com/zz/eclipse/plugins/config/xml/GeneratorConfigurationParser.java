package com.zz.eclipse.plugins.config.xml;


import static com.zz.eclipse.plugins.util.StringUtility.stringHasValue;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.zz.eclipse.plugins.config.CodeGeneratorConfiguration;
import com.zz.eclipse.plugins.config.Configuration;
import com.zz.eclipse.plugins.config.Context;
import com.zz.eclipse.plugins.config.JDBCConnectionConfiguration;
import com.zz.eclipse.plugins.config.PropertyHolder;
import com.zz.eclipse.plugins.exception.XMLParserException;


/**
 * @author : Zak
 * @date : 2017年2月14日 下午5:15:40
 * @version : 2017年2月14日 Zak 首次创建
 */
public class GeneratorConfigurationParser {
  private Properties properties = new Properties();

  public Configuration parseConfiguration( Element rootNode ) throws XMLParserException {

    Configuration configuration = new Configuration();

    NodeList nodeList = rootNode.getChildNodes();
    for( int i = 0; i < nodeList.getLength(); i++ ) {
      Node childNode = nodeList.item(i);

      if(childNode.getNodeType() != Node.ELEMENT_NODE) {
        continue;
      }

      if("properties".equals(childNode.getNodeName())) { //$NON-NLS-1$
        parseProperties(configuration, childNode);
      }
      else if("classPathEntry".equals(childNode.getNodeName())) { //$NON-NLS-1$
        parseClassPathEntry(configuration, childNode);
      }
      else if("jdbcConnection".equals(childNode.getNodeName())) { //$NON-NLS-1$
        parseJdbcConnection(configuration, childNode);
      }
      else if("context".equals(childNode.getNodeName())) { //$NON-NLS-1$
        parseContext(configuration, childNode);
      }
    }

    return configuration;
  }
  private Properties parseAttributes( Node node ) {
    Properties attributes = new Properties();
    NamedNodeMap nnm = node.getAttributes();
    for( int i = 0; i < nnm.getLength(); i++ ) {
      Node attribute = nnm.item(i);
      String value = parsePropertyTokens(attribute.getNodeValue());
      attributes.put(attribute.getNodeName(), value);
    }

    return attributes;
  }

  private String parsePropertyTokens( String string ) {
    final String OPEN = "${"; //$NON-NLS-1$
    final String CLOSE = "}"; //$NON-NLS-1$

    String newString = string;
    if(newString != null) {
      int start = newString.indexOf(OPEN);
      int end = newString.indexOf(CLOSE);

      while(start > -1 && end > start) {
        String prepend = newString.substring(0, start);
        String append = newString.substring(end + CLOSE.length());
        String propName = newString.substring(start + OPEN.length(), end);
        String propValue = properties.getProperty(propName);
        if(propValue != null) {
          newString = prepend + propValue + append;
        }

        start = newString.indexOf(OPEN, end);
        end = newString.indexOf(CLOSE, end);
      }
    }

    return newString;
  }

  private void parseProperties( Configuration configuration, Node node ) throws XMLParserException {
    Properties attributes = parseAttributes(node);
    String resource = attributes.getProperty("resource"); //$NON-NLS-1$
    String url = attributes.getProperty("url"); //$NON-NLS-1$

    if(!stringHasValue(resource) && !stringHasValue(url)) {
      throw new XMLParserException(( "RuntimeError.14" )); //$NON-NLS-1$
    }

    if(stringHasValue(resource) && stringHasValue(url)) {
      throw new XMLParserException(( "RuntimeError.14" )); //$NON-NLS-1$
    }

    URL resourceUrl;

    try {

      resourceUrl = new URL(url);

      InputStream inputStream = resourceUrl.openConnection().getInputStream();

      properties.load(inputStream);
      inputStream.close();
    }
    catch(IOException e) {
      if(stringHasValue(resource)) {
        throw new XMLParserException("RuntimeError.16"); //$NON-NLS-1$
      }
      else {
        throw new XMLParserException(( "RuntimeError.17" )); //$NON-NLS-1$
      }
    }
  }

  private void parseClassPathEntry( Configuration configuration, Node node ) {
    Properties attributes = parseAttributes(node);

    configuration.addClasspathEntry(attributes.getProperty("location")); //$NON-NLS-1$
  }

  private void parseContext( Configuration configuration, Node node ) {

    Properties attributes = parseAttributes(node);

    Context context = new Context();
    String name = attributes.getProperty("name");

    context.setName(name);
    configuration.addContext(context);

    NodeList nodeList = node.getChildNodes();
    for( int i = 0; i < nodeList.getLength(); i++ ) {
      Node childNode = nodeList.item(i);

      if(childNode.getNodeType() != Node.ELEMENT_NODE) {
        continue;
      }

      if("property".equals(childNode.getNodeName())) { //$NON-NLS-1$
        parseProperty(context, childNode);
      }
      else if("codeGenerator".equals(childNode.getNodeName())) {
        parseCodeGenerator(context, childNode);
      }
    }
  }

  private void parseCodeGenerator( Context context, Node node ) {
    CodeGeneratorConfiguration codeGeneratorConfiguration = new CodeGeneratorConfiguration();
    context.addCodeGeneratorConfiguration(codeGeneratorConfiguration);

    Properties attributes = parseAttributes(node);

    String targetPackage = attributes.getProperty("targetPackage");
    String targetProject = attributes.getProperty("targetProject");
    String template = attributes.getProperty("template");
    String targetFileNameSuffix = attributes.getProperty("targetFileNameSuffix");
    String targetPath = attributes.getProperty("targetPath");

    codeGeneratorConfiguration.setTargetPackage(targetPackage);
    codeGeneratorConfiguration.setTargetProject(targetProject);
    codeGeneratorConfiguration.setTemplate(template);
    codeGeneratorConfiguration.setTargetFileNameSuffix(targetFileNameSuffix);
    codeGeneratorConfiguration.setTargetPath(targetPath);

    NodeList nodeList = node.getChildNodes();
    for( int i = 0; i < nodeList.getLength(); i++ ) {
      Node childNode = nodeList.item(i);

      if(childNode.getNodeType() != Node.ELEMENT_NODE) {
        continue;
      }

      if("property".equals(childNode.getNodeName())) {
        parseProperty(codeGeneratorConfiguration, childNode);
      }
    }
  }

  private void parseProperty( PropertyHolder propertyHolder, Node node ) {
    Properties attributes = parseAttributes(node);

    String name = attributes.getProperty("name"); //$NON-NLS-1$
    String value = attributes.getProperty("value"); //$NON-NLS-1$

    propertyHolder.addProperty(name, value);
  }

  private void parseJdbcConnection( Configuration configuration, Node node ) {
    JDBCConnectionConfiguration jdbcConnectionConfiguration = new JDBCConnectionConfiguration();

    configuration.setJdbcConnectionConfiguration(jdbcConnectionConfiguration);

    Properties attributes = parseAttributes(node);
    String driverClass = attributes.getProperty("driverClass"); //$NON-NLS-1$
    String connectionURL = attributes.getProperty("connectionURL"); //$NON-NLS-1$
    String userId = attributes.getProperty("userId"); //$NON-NLS-1$
    String password = attributes.getProperty("password"); //$NON-NLS-1$

    jdbcConnectionConfiguration.setDriverClass(driverClass);
    jdbcConnectionConfiguration.setConnectionURL(connectionURL);

    if(stringHasValue(userId)) {
      jdbcConnectionConfiguration.setUserId(userId);
    }

    if(stringHasValue(password)) {
      jdbcConnectionConfiguration.setPassword(password);
    }

    NodeList nodeList = node.getChildNodes();
    for( int i = 0; i < nodeList.getLength(); i++ ) {
      Node childNode = nodeList.item(i);

      if(childNode.getNodeType() != Node.ELEMENT_NODE) {
        continue;
      }

      if("property".equals(childNode.getNodeName())) {
        parseProperty(jdbcConnectionConfiguration, childNode);
      }
    }
  }

}
