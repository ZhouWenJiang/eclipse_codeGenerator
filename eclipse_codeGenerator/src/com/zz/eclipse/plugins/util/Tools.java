package com.zz.eclipse.plugins.util;


import java.io.File;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;


public class Tools {
  private static MessageConsoleStream consoleStream = null;

  static {
    IConsoleManager consoleManager = ConsolePlugin.getDefault().getConsoleManager();
    IConsole[] consoles = consoleManager.getConsoles();
    if(( consoles != null ) && ( consoles.length > 0 )) {
      IConsole console = consoles[0];
      if(( console instanceof MessageConsole )) {
        consoleStream = ( (MessageConsole) console ).newMessageStream();
      }
    }
    else {
      MessageConsole console = new MessageConsole("aotuMybatis", null);

      consoleManager.addConsoles(new IConsole[]{ console });

      consoleStream = console.newMessageStream();

      consoleManager.showConsoleView(console);
    }
  }

  public static void writeLine( String message ) {
    if(consoleStream != null) {
      consoleStream.println(message);
    }
  }

  public static void writeLine() {
    if(consoleStream != null) {
      consoleStream.println();
    }
  }

  public static String formatRelativePath( String linkPath, String relativePath ) throws Exception {
    if(( linkPath == null ) || ( relativePath == null ) || ( "".equals(relativePath.trim()) )) {
      return linkPath;
    }
    if(linkPath.substring(linkPath.length() - 1, linkPath.length()).equals(File.separator)) {
      linkPath = linkPath.substring(0, linkPath.length() - 1);
    }
    relativePath = relativePath.replace("\\", "/");
    if(relativePath.indexOf("/") < 0) {
      relativePath = "/" + relativePath;
    }
    int index = relativePath.indexOf("../");
    while(index >= 0) {
      int _index = linkPath.lastIndexOf(File.separator);
      if(_index < 0) {
        throw new Exception("The relativePath is incorrect base on linkPath. ");
      }
      linkPath = linkPath.substring(0, _index);
      relativePath = relativePath.substring(index + 2);
      index = relativePath.indexOf("../");
    }
    String realPath = ( linkPath + relativePath ).replace("/", File.separator);
    File file = new File(realPath);
    if(!file.exists()) {
      boolean r = file.mkdir();
      if(!r) {
        r = file.mkdirs();
      }
    }
    return realPath;
  }

}
