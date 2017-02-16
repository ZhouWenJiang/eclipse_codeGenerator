package com.zz.eclipse.plugins;


import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.internal.core.JarPackageFragmentRoot;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import com.zz.eclipse.plugins.config.Configuration;
import com.zz.eclipse.plugins.config.JDBCConnectionConfiguration;
import com.zz.eclipse.plugins.config.xml.ConfigurationParser;
import com.zz.eclipse.plugins.exception.XMLParserException;
import com.zz.eclipse.plugins.function.FindJar;
import com.zz.eclipse.plugins.ui.WizardDialogExt;
import com.zz.eclipse.plugins.ui.WizardWindow;
import com.zz.eclipse.plugins.util.Tools;


public class PopAction implements IObjectActionDelegate {
  private IFile                    selectedFile;
  private Shell                    shell;
  public static Map<String, IPath> cacheDriverPathMap;

  public void setActivePart( IAction action, IWorkbenchPart targetPart ) {
    this.shell = targetPart.getSite().getShell();
  }

  public void run( IAction action ) {
    String message = "The xml file is incorrect,";
    IPath rawPath = this.selectedFile.getRawLocation();
    File configurationFile = rawPath.toFile();
    ConfigurationParser cp = new ConfigurationParser(null);
    Tools.writeLine("config file:" + configurationFile.getAbsolutePath());
    Configuration config = null;
    try {
      config = cp.parseConfiguration(configurationFile);
      if(( config.getClassPathEntries().size() == 0 ) && ( config.getContexts().size() > 0 )) {
        JDBCConnectionConfiguration connConfig = config.getJdbcConnectionConfiguration();
        if(connConfig != null) {
          if(cacheDriverPathMap == null) {
            cacheDriverPathMap = new HashMap();
          }
          String driverClassName = connConfig.getDriverClass().toString();
          IPath path = null;
          if(cacheDriverPathMap.get(driverClassName) != null) {
            path = (IPath) cacheDriverPathMap.get(driverClassName);
            if(( path != null ) && ( !path.toFile().exists() )) {
              path = findDriverJar(driverClassName);
            }
          }
          else {
            path = findDriverJar(driverClassName);
          }
          if(path == null) {
            MessageDialog.openError(this.shell, "����", "������������");
            return;
          }
          cacheDriverPathMap.put(driverClassName, path);
          config.getClassPathEntries().add(path.toString());
        }
      }
    }
    catch(IOException e) {
      message = message + e.getMessage();
      Tools.writeLine(message);
      System.out.println(e.getStackTrace().toString());
      e.printStackTrace();
    }
    catch(XMLParserException e) {
      message = message + e.getMessage();
      Tools.writeLine(message);
      e.printStackTrace();
    }
    createWizrd(config);
  }

  private IPath findDriverJar( String driverClassName ) {
    IWorkspace workspace = ResourcesPlugin.getWorkspace();
    JarPackageFragmentRoot driverJar = FindJar.findDrvierJar(workspace, driverClassName);
    return driverJar == null ? null : driverJar.getPath();
  }

  private void createWizrd( Configuration config ) {
    try {
      WizardWindow w = new WizardWindow(this.selectedFile, config);
      if(!w.isValidate()) {
        MessageDialog.openError(this.shell, "����", "������������������������������:\n" + w.getMessage());
        return;
      }
      WizardDialogExt dialog = new WizardDialogExt(this.shell, w);
      dialog.setPageSize(550, 450);
      dialog.create();

      Rectangle screenSize = Display.getDefault().getClientArea();
      Shell shell = dialog.getShell();
      shell.setLocation(( screenSize.width - dialog.getShell().getBounds().width ) / 2,
        ( screenSize.height - dialog.getShell().getBounds().height ) / 2);
      dialog.open();
    }
    catch(Exception e) {
      Tools.writeLine("show window is error:" + e.getMessage());
      e.printStackTrace();
    }
  }

  public void selectionChanged( IAction action, ISelection selection ) {
    StructuredSelection ss = (StructuredSelection) selection;
    this.selectedFile = (IFile) ss.getFirstElement();
    // ConfigurationFileAdapter adapter = new ConfigurationFileAdapter((IFile)
    // ss.getFirstElement());
    // if(adapter != null) {
    // this.selectedFile = adapter.getBaseFile();
    // }
  }
}
