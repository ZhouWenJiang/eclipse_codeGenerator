package com.zz.eclipse.plugins.ui;


import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import com.zz.eclipse.plugins.config.Configuration;
import com.zz.eclipse.plugins.config.Context;


public class WizardPageTwo extends WizardPage {
  private Configuration config;

  public WizardPageTwo( Configuration config ) {
    super("page2");
    this.config = config;
    setTitle("生成代码");
    setDescription("选择要生成的代码");
  }

  public void createControl( Composite parent ) {
    Composite container = new Composite(parent, 0);
    container.setLayout(new BorderLayout());
    setControl(container);

    Group group = new Group(container, 0);
    group.setLayoutData(new BorderLayout.BorderData(2));
    group.setText("选择要生成的代码");

    for( int i = 0; i < config.getContexts().size(); i++ ) {
      Context context = config.getContexts().get(0);
      int width = i % 4;
      int height = i / 4;
      Button button = new Button(group, 32);
      button.setEnabled(true);
      button.setSelection(false);
      button.setBounds(10 + width * 113, 51 + height * 47, 98, 17);
      button.setText(context.getName());

      button.addSelectionListener(new SelectionAdapter() {
        public void widgetSelected( SelectionEvent e ) {
          boolean flag = ( (Button) e.getSource() ).getSelection();
          context.setSelected(flag);
        }
      });
    }
  }
}
