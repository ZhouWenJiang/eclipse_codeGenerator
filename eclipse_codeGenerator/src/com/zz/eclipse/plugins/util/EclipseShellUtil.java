package com.zz.eclipse.plugins.util;


import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;


public class EclipseShellUtil {
  private static Map<String, IJavaProject>         projects;
  private static Map<String, IFolder>              folders;
  private static Map<String, IPackageFragmentRoot> sourceFolders;

  static {
    projects = new HashMap<>();
    folders = new HashMap<>();
    sourceFolders = new HashMap<>();
  }

  public static File getDirectory( String targetProject, String targetPackage ) {
    if(( targetProject.startsWith("/") ) || ( targetProject.startsWith("\\") )) {
      StringBuffer sb = new StringBuffer();
      sb.append("targetProject ");
      sb.append(targetProject);
      sb.append(" is invalid - it cannot start with / or \\");

      throw new RuntimeException(sb.toString());
    }

    IFolder folder = getFolder(targetProject, targetPackage);

    return folder.getRawLocation().toFile();
  }

  public static void refreshProject( String project ) {
    try {
      IPackageFragmentRoot root = getSourceFolder(project);
      root.getCorrespondingResource().refreshLocal(2, null);
    }
    catch(Exception localException) {}
  }

  public static IJavaProject getJavaProject( String javaProjectName ) {
    IJavaProject javaProject = (IJavaProject) projects.get(javaProjectName);
    if(javaProject == null) {
      IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
      IProject project = root.getProject(javaProjectName);
      boolean isJavaProject;
      if(project.exists()) {
        try {
          isJavaProject = project.hasNature("org.eclipse.jdt.core.javanature");
        }
        catch(CoreException e) {
          throw new RuntimeException(e.getStatus().getMessage(), e);
        }
        if(isJavaProject) {
          javaProject = JavaCore.create(project);
        }
        else {
          StringBuffer sb = new StringBuffer();
          sb.append("Project ");
          sb.append(javaProjectName);
          sb.append(" is not a Java project");

          throw new RuntimeException(sb.toString());
        }
      }
      else {
        StringBuffer sb = new StringBuffer();
        sb.append("Project ");
        sb.append(javaProjectName);
        sb.append(" does not exist");

        throw new RuntimeException(sb.toString());
      }

      projects.put(javaProjectName, javaProject);
    }

    return javaProject;
  }

  private static IFolder getFolder( String targetProject, String targetPackage ) {
    String key = targetProject + targetPackage;
    IFolder folder = (IFolder) folders.get(key);
    if(folder == null) {
      IPackageFragmentRoot root = getSourceFolder(targetProject);
      IPackageFragment packageFragment = getPackage(root, targetPackage);
      try {
        folder = (IFolder) packageFragment.getCorrespondingResource();

        folders.put(key, folder);
      }
      catch(CoreException e) {
        throw new RuntimeException(e.getStatus().getMessage(), e);
      }
    }

    return folder;
  }

  private static IPackageFragmentRoot getFirstSourceFolder( IJavaProject javaProject ) {
    IPackageFragmentRoot[] roots;
    try {
      roots = javaProject.getPackageFragmentRoots();
    }
    catch(CoreException e) {
      throw new RuntimeException(e.getStatus().getMessage(), e);
    }
    IPackageFragmentRoot srcFolder = null;
    for( int i = 0; i < roots.length; i++ ) {
      if(( !roots[i].isArchive() ) && ( !roots[i].isReadOnly() ) && ( !roots[i].isExternal() )) {

        srcFolder = roots[i];
        break;
      }
    }

    if(srcFolder == null) {
      StringBuffer sb = new StringBuffer();
      sb.append("Cannot find source folder for project ");
      sb.append(javaProject.getElementName());

      throw new RuntimeException(sb.toString());
    }

    return srcFolder;
  }

  private static IPackageFragmentRoot getSpecificSourceFolder( IJavaProject javaProject, String targetProject ) {
    try {
      Path path = new Path("/" + targetProject);
      IPackageFragmentRoot pfr = javaProject.findPackageFragmentRoot(path);
      if(pfr == null) {
        StringBuffer sb = new StringBuffer();
        sb.append("Cannot find source folder ");
        sb.append(targetProject);

        throw new RuntimeException(sb.toString());
      }

      return pfr;
    }
    catch(CoreException e) {
      throw new RuntimeException(e.getStatus().getMessage(), e);
    }
  }

  private static IPackageFragment getPackage( IPackageFragmentRoot srcFolder, String packageName ) {
    IPackageFragment fragment = srcFolder.getPackageFragment(packageName);
    try {
      if(!fragment.exists()) {
        fragment = srcFolder.createPackageFragment(packageName, true, null);
      }

      fragment.getCorrespondingResource().refreshLocal(1, null);
    }
    catch(CoreException e) {
      throw new RuntimeException(e.getStatus().getMessage(), e);
    }

    return fragment;
  }

  public boolean isMergeSupported() {
    return false;
  }

  private static IPackageFragmentRoot getSourceFolder( String targetProject ) {
    IPackageFragmentRoot answer = (IPackageFragmentRoot) sourceFolders.get(targetProject);
    if(answer == null) {

      int index = targetProject.indexOf('/');
      if(index == -1) {
        index = targetProject.indexOf('\\');
      }

      if(index == -1) {
        IJavaProject javaProject = getJavaProject(targetProject);
        answer = getFirstSourceFolder(javaProject);
      }
      else {
        IJavaProject javaProject = getJavaProject(targetProject.substring(0, index));
        answer = getSpecificSourceFolder(javaProject, targetProject);
      }

      sourceFolders.put(targetProject, answer);
    }

    return answer;
  }

  public boolean isOverwriteEnabled() {
    return true;
  }

  public String mergeJavaFile( String newFileSource, String existingFileFullPath, String[] javadocTags,
      String fileEncoding ) {
    // JavaFileMerger merger = new JavaFileMerger(newFileSource,
    // existingFileFullPath, javadocTags, fileEncoding);
    // return merger.getMergedSource();
    return newFileSource;
  }
}
