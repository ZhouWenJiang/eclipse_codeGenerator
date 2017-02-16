package com.zz.eclipse.plugins.ui;


import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Shell;

import com.zz.eclipse.plugins.Activator;
import com.zz.eclipse.plugins.RunGeneratorThread;
import com.zz.eclipse.plugins.config.Configuration;
import com.zz.eclipse.plugins.config.Context;
import com.zz.eclipse.plugins.config.JDBCConnectionConfiguration;
import com.zz.eclipse.plugins.util.ClassloaderUtility;
import com.zz.eclipse.plugins.util.ConnectionFactory;
import com.zz.eclipse.plugins.util.ObjectFactory;
import com.zz.eclipse.plugins.util.Tools;


public class WizardWindow extends Wizard {
  public WizardPageOne              pageOne;
  public WizardPageTwo              pageTwo;
  private Map<String, List<String>> tableMap = null;
  private IFile                     selectedFile;
  private boolean                   validate;
  private String                    message;
  private Configuration             config;

  public WizardWindow( IFile selectedFile, Configuration config ) {
    setWindowTitle("代码生成");
    setNeedsProgressMonitor(true);
    setTitleBarColor(new RGB(255, 0, 0));
    setWindowTitle("代码生成");
    this.selectedFile = selectedFile;
    this.validate = loadConn(config);
    this.config = config;
  }

  public boolean isValidate() {
    return this.validate;
  }

  public String getMessage() {
    return this.message;
  }

  public boolean canFinish() {
    if(getContainer().getCurrentPage() == this.pageTwo) {
      return true;
    }
    return false;
  }

  public IWizardPage getNextPage( IWizardPage page ) {
    return super.getNextPage(page);
  }

  public boolean performCancel() {
    return super.performCancel();
  }

  public void addPages() {
    this.pageOne = new WizardPageOne(this.tableMap);
    addPage(this.pageOne);
    this.pageTwo = new WizardPageTwo(config);
    addPage(this.pageTwo);
  }

  private boolean loadConn( Configuration config ) {
    try {
      List<Context> contexts = config.getContexts();
      if(config.getClassPathEntries().size() > 0) {
        ClassLoader classLoader = ClassloaderUtility.getCustomClassloader(config.getClassPathEntries());
        ObjectFactory.addExternalClassLoader(classLoader);
      }
      else {
        this.message = "The xml file is invalid.";
        Tools.writeLine(this.message);
        return false;
      }
      if(( contexts != null ) && ( contexts.size() > 0 ) && ( contexts.get(0) != null )) {
        JDBCConnectionConfiguration jdbcConfig = config.getJdbcConnectionConfiguration();
        Connection con = null;
        try {
          con = ConnectionFactory.getInstance().getConnection(jdbcConfig);

          DatabaseMetaData meta = con.getMetaData();
          ResultSet rs = meta.getTables(null, null, null, new String[]{ "TABLE" });
          this.tableMap = new HashMap();
          while(rs.next()) {
            if(( rs.getString(1) != null ) && ( this.tableMap.get(rs.getString(1)) == null )) {
              List<String> tableList = new ArrayList();
              tableList.add(rs.getString(3));
              this.tableMap.put(rs.getString(1), tableList);
            }
            else if(rs.getString(1) != null) {
              ( (List) this.tableMap.get(rs.getString(1)) ).add(rs.getString(3));
            }
            if(( rs.getString(2) != null ) && ( this.tableMap.get(rs.getString(2)) == null )) {
              List<String> tableList = new ArrayList();
              tableList.add(rs.getString(3));
              this.tableMap.put(rs.getString(2), tableList);
            }
            else if(rs.getString(2) != null) {
              ( (List) this.tableMap.get(rs.getString(2)) ).add(rs.getString(3));
            }
          }
          con.close();
          return true;
        }
        catch(Exception e) {
          try {
            if(con != null) {
              con.close();
            }
          }
          catch(SQLException e1) {
            this.message = e1.getLocalizedMessage();
            Tools.writeLine(this.message);
          }
          this.message = e.getLocalizedMessage();
          Tools.writeLine(this.message);
          return false;
        }
      }
      this.message = "The xml file is incorrect.";
      Tools.writeLine(this.message);
      return false;
    }
    catch(Exception e) {
      this.message = ( "init connection error:" + e.getMessage() );
      Tools.writeLine(this.message);
    }
    return false;
  }

  public boolean performFinish() {
    Shell shell = getShell();
    try {
      List<String> warnings = new ArrayList();
      ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);

      IRunnableWithProgress thread = new GeneratorRunner(warnings, this.pageOne.getTableList(), this.config);

      dialog.run(true, false, thread);
      if(warnings.size() > 0) {
        MultiStatus ms = new MultiStatus("org.mybatis.generator.eclipse.ui", 2, "Generation Warnings Occured", null);

        Iterator<String> iter = warnings.iterator();
        while(iter.hasNext()) {
          Status status = new Status(2, "org.mybatis.generator.eclipse.ui", 2, (String) iter.next(), null);
          ms.add(status);
        }
        ErrorDialog.openError(shell, "MyBatis Generator", "Run Complete With Warnings", ms, 2);
      }
    }
    catch(Exception e) {
      handleException(e, shell);
    }
    return true;
  }

  private class GeneratorRunner implements IRunnableWithProgress {
    private List<String>  warnings;
    private List<String>  tableList;
    private Configuration config;

    public GeneratorRunner( List<String> warnings, List<String> tableList, Configuration config ) {
      this.warnings = warnings;
      this.tableList = tableList;
      this.config = config;
    }

    public void run( IProgressMonitor monitor ) throws InvocationTargetException, InterruptedException {
      try {
        RunGeneratorThread thread =
            new RunGeneratorThread(WizardWindow.this.selectedFile, this.warnings, this.tableList, this.config);

        ResourcesPlugin.getWorkspace().run(thread, monitor);
      }
      catch(CoreException e) {
        throw new InvocationTargetException(e);
      }
    }
  }

  private void handleException( Exception exception, Shell shell ) {
    Throwable exceptionToHandle;
    if(( exception instanceof InvocationTargetException )) {
      exceptionToHandle = ( (InvocationTargetException) exception ).getCause();
    }
    else {
      exceptionToHandle = exception;
    }
    IStatus status;
    if(( exceptionToHandle instanceof InterruptedException )) {
      status = new Status(8, "org.mybatis.generator.eclipse.ui", 8, "Cancelled by User", exceptionToHandle);
    }
    else {
      if(( exceptionToHandle instanceof CoreException )) {
        status = ( (CoreException) exceptionToHandle ).getStatus();
      }
      else {
        String message = "Unexpected error while running MyBatis Generator.";

        status = new Status(4, "org.mybatis.generator.eclipse.ui", 4, message, exceptionToHandle);

        Activator.getDefault().getLog().log(status);
      }
    }
    ErrorDialog.openError(shell, "MyBatis Generator", "Generation Failed", status, 12);
  }
}
