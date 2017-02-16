package com.zz.eclipse.plugins.ui;


import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;


public class WizardDialogExt extends WizardDialog {
  public WizardDialogExt( Shell parentShell, IWizard newWizard ) {
    super(parentShell, newWizard);

    setShellStyle(67680);
  }
}
