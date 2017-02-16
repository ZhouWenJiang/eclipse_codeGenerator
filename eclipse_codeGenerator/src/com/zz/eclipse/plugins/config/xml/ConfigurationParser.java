/**
 *    Copyright 2006-2015 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
/*
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.zz.eclipse.plugins.config.xml;


import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.zz.eclipse.plugins.config.Configuration;
import com.zz.eclipse.plugins.exception.XMLParserException;


public class ConfigurationParser {

  private List<String> warnings;
  private List<String> parseErrors;
  private Properties   properties;

  public ConfigurationParser( List<String> warnings ) {
    this(null, warnings);
  }

  public ConfigurationParser( Properties properties, List<String> warnings ) {
    super();
    if(properties == null) {
      this.properties = System.getProperties();
    }
    else {
      this.properties = properties;
    }

    if(warnings == null) {
      this.warnings = new ArrayList<String>();
    }
    else {
      this.warnings = warnings;
    }

    parseErrors = new ArrayList<String>();
  }

  public Configuration parseConfiguration( File inputFile ) throws IOException, XMLParserException {

    FileReader fr = new FileReader(inputFile);

    return parseConfiguration(fr);
  }

  public Configuration parseConfiguration( Reader reader ) throws IOException, XMLParserException {

    InputSource is = new InputSource(reader);

    return parseConfiguration(is);
  }

  public Configuration parseConfiguration( InputStream inputStream ) throws IOException, XMLParserException {

    InputSource is = new InputSource(inputStream);

    return parseConfiguration(is);
  }

  private Configuration parseConfiguration( InputSource inputSource ) throws IOException, XMLParserException {
    parseErrors.clear();
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    // factory.setValidating(true);

    try {
      DocumentBuilder builder = factory.newDocumentBuilder();
      // builder.setEntityResolver(new ParserEntityResolver());

      // ParserErrorHandler handler = new ParserErrorHandler(warnings,
      // parseErrors);
      // builder.setErrorHandler(handler);

      Document document = null;
      try {
        document = builder.parse(inputSource);
      }
      catch(SAXParseException e) {
        throw new XMLParserException(parseErrors);
      }
      catch(SAXException e) {
        if(e.getException() == null) {
          parseErrors.add(e.getMessage());
        }
        else {
          parseErrors.add(e.getException().getMessage());
        }
      }

      if(parseErrors.size() > 0) {
        throw new XMLParserException(parseErrors);
      }

      Configuration config;
      Element rootNode = document.getDocumentElement();
      config = parseGeneratorConfiguration(rootNode);
      if(parseErrors.size() > 0) {
        throw new XMLParserException(parseErrors);
      }

      return config;
    }
    catch(ParserConfigurationException e) {
      parseErrors.add(e.getMessage());
      throw new XMLParserException(parseErrors);
    }
  }

  private Configuration parseGeneratorConfiguration( Element rootNode ) throws XMLParserException {
    GeneratorConfigurationParser parser = new GeneratorConfigurationParser();
    return parser.parseConfiguration(rootNode);
  }

}
