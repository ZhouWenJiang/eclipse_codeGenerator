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
package com.zz.eclipse.plugins.generator.engine;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


public class EngineBuilder implements Serializable {

  private static final long                     serialVersionUID = 2299973831636720810L;

  private transient Map<String, TemplateEngine> engineMap;

  public EngineBuilder( String classPath ) {
    engineMap = new HashMap<String, TemplateEngine>();
    synchronized(this) {
      engineMap.put("freemarker", new FreeMarkerImpl(classPath));
    }
  }

  public TemplateEngine getTemplateEngine( String engine ) {
    return engineMap.get(engine);
  }
}
