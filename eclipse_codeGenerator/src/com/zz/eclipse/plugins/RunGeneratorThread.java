package com.zz.eclipse.plugins;


import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

import com.zz.eclipse.plugins.config.CodeGeneratorConfiguration;
import com.zz.eclipse.plugins.config.Configuration;
import com.zz.eclipse.plugins.config.Context;
import com.zz.eclipse.plugins.config.TypeMapping;
import com.zz.eclipse.plugins.db.Database;
import com.zz.eclipse.plugins.db.DatabaseFactory;
import com.zz.eclipse.plugins.db.model.Table;
import com.zz.eclipse.plugins.generator.engine.FreeMarkerImpl;
import com.zz.eclipse.plugins.generator.engine.TemplateEngine;
import com.zz.eclipse.plugins.util.ConnectionFactory;
import com.zz.eclipse.plugins.util.EclipseShellUtil;


public class RunGeneratorThread implements IWorkspaceRunnable {
  private IFile          inputFile;
  private List<String>   warnings;
  private ClassLoader    oldClassLoader;
  private List<String>   tableList;

  private Configuration  config;

  private TemplateEngine templateEngine;
  private TypeMapping    typeMapping;
  private Connection     connection;

  public RunGeneratorThread( IFile inputFile, List<String> warnings, List<String> tableList, Configuration config ) {
    this.inputFile = inputFile;
    this.warnings = warnings;
    this.tableList = tableList;
    this.config = config;
    // 模板目录

    try {
      String path = EclipseShellUtil.getDirectory(inputFile.getProject().getName(), "").getAbsolutePath();
      path = path.substring(0, path.lastIndexOf(File.separator));
      templateEngine = new FreeMarkerImpl(path);
      typeMapping = new TypeMapping();
      connection = ConnectionFactory.getInstance().getConnection(config.getJdbcConnectionConfiguration());
    }
    catch(Exception e) {
      System.out.println(e);
    }

  }

  /* Error */
  public void run( org.eclipse.core.runtime.IProgressMonitor monitor ) throws CoreException {

    for( int index = 0; index < tableList.size(); index++ ) {

      Database db;
      Table tableModel = null;
      try {
        db = DatabaseFactory.createDatabase(connection, typeMapping);
        tableModel = db.getTable(null, null, tableList.get(index));
      }
      catch(SQLException e1) {
        e1.printStackTrace();
      }

      Map<String, Object> model = new HashMap<String, Object>();

      model.put("table", tableModel);
      for( int contextIndex = 0; contextIndex < config.getContexts().size(); contextIndex++ ) {
        Context context = config.getContexts().get(contextIndex);
        if(!context.isSelected()) {// 没有选择的，跳过
          continue;
        }

        int count = context.getCodeGeneratorConfigurations().size();
        for( int i = 0; i < count; i++ ) {
          try {
            CodeGeneratorConfiguration codeConfig = config.getContexts().get(0).getCodeGeneratorConfigurations().get(i);

            model.put("targetPackage", codeConfig.getTargetPackage());

            templateEngine.processToFile(model, codeConfig);
            EclipseShellUtil.refreshProject(codeConfig.getTargetProject());
          }
          catch(Exception e) {
            e.printStackTrace();
          }
        }
      }
    }

  }

  private void setClassLoader() {
    IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    IJavaProject javaProject = getJavaProject();
    try {
      if(javaProject != null) {
        List<URL> entries = new ArrayList();
        IPath path = javaProject.getOutputLocation();
        IResource iResource = root.findMember(path);
        path = iResource.getLocation();
        path = path.addTrailingSeparator();
        entries.add(path.toFile().toURL());

        IClasspathEntry[] cpEntries = javaProject.getRawClasspath();
        IClasspathEntry[] arrayOfIClasspathEntry1;
        int j = ( arrayOfIClasspathEntry1 = cpEntries ).length;
        for( int i = 0; i < j; i++ ) {
          IClasspathEntry cpEntry = arrayOfIClasspathEntry1[i];
          switch(cpEntry.getEntryKind()) {
            case 3:
              path = cpEntry.getOutputLocation();
              if(path != null) {
                iResource = root.findMember(path);
                path = iResource.getLocation();
                path = path.addTrailingSeparator();
                entries.add(path.toFile().toURL());
              }
              break;
            case 1:
              iResource = root.findMember(cpEntry.getPath());
              if(iResource == null) {
                path = cpEntry.getPath();
              }
              else {
                path = iResource.getLocation();
              }
              entries.add(path.toFile().toURL());
          }
        }
        ClassLoader oldCl = Thread.currentThread().getContextClassLoader();
        URL[] entryArray = new URL[entries.size()];
        entries.toArray(entryArray);
        Object newCl = new URLClassLoader(entryArray, oldCl);
        Thread.currentThread().setContextClassLoader((ClassLoader) newCl);
        this.oldClassLoader = oldCl;
      }
    }
    catch(Exception localException) {}
  }

  private void restoreClassLoader() {
    if(this.oldClassLoader != null) {
      Thread.currentThread().setContextClassLoader(this.oldClassLoader);
    }
  }

  private IJavaProject getJavaProject() {
    IJavaProject answer = null;
    IProject project = this.inputFile.getProject();
    try {
      if(project.hasNature("org.eclipse.jdt.core.javanature")) {
        answer = JavaCore.create(project);
      }
    }
    catch(CoreException localCoreException) {}
    return answer;
  }
}
