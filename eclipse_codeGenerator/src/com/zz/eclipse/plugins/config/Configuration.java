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
package com.zz.eclipse.plugins.config;


import java.util.ArrayList;
import java.util.List;


/**
 * The Class Configuration.
 *
 * @author Jeff Butler
 */
public class Configuration {

  /** The contexts. */
  private List<Context>               contexts;

  /** The class path entries. */
  private List<String>                classPathEntries;
  private JDBCConnectionConfiguration jdbcConnectionConfiguration;

  /**
   * Instantiates a new configuration.
   */
  public Configuration() {
    super();
    contexts = new ArrayList<Context>();
    classPathEntries = new ArrayList<String>();
  }

  /**
   * Adds the classpath entry.
   *
   * @param entry
   *          the entry
   */
  public void addClasspathEntry( String entry ) {
    classPathEntries.add(entry);
  }

  /**
   * Gets the class path entries.
   *
   * @return Returns the classPathEntries.
   */
  public List<String> getClassPathEntries() {
    return classPathEntries;
  }

  public JDBCConnectionConfiguration getJdbcConnectionConfiguration() {
    return jdbcConnectionConfiguration;
  }
  public void setJdbcConnectionConfiguration( JDBCConnectionConfiguration jdbcConnectionConfiguration ) {
    this.jdbcConnectionConfiguration = jdbcConnectionConfiguration;
  }

  /**
   * Gets the contexts.
   *
   * @return the contexts
   */
  public List<Context> getContexts() {
    return contexts;
  }

  /**
   * Adds the context.
   *
   * @param context
   *          the context
   */
  public void addContext( Context context ) {
    contexts.add(context);
  }
}
