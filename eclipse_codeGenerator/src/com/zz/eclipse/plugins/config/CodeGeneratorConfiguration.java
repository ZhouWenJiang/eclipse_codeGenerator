package com.zz.eclipse.plugins.config;


/**
 * @author : Zak
 * @date : 2017年2月14日 下午5:09:39
 * @version : 2017年2月14日 Zak 首次创建
 */
public class CodeGeneratorConfiguration {
  private String targetPackage;
  private String targetProject;
  private String template;
  private String encoding = "utf-8";
  private String targetFileNameSuffix;

  public String getTargetPackage() {
    return targetPackage;
  }
  public void setTargetPackage( String targetPackage ) {
    this.targetPackage = targetPackage;
  }
  public String getTargetProject() {
    return targetProject;
  }
  public void setTargetProject( String targetProject ) {
    this.targetProject = targetProject;
  }
  public String getTemplate() {
    return template;
  }
  public void setTemplate( String template ) {
    this.template = template;
  }
  public String getEncoding() {
    return encoding;
  }
  public void setEncoding( String encoding ) {
    this.encoding = encoding;
  }
  public String getTargetFileNameSuffix() {
    return targetFileNameSuffix;
  }
  public void setTargetFileNameSuffix( String targetFileNameSuffix ) {
    this.targetFileNameSuffix = targetFileNameSuffix;
  }
}
