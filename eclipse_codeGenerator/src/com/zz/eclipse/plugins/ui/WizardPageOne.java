package com.zz.eclipse.plugins.ui;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;


public class WizardPageOne extends WizardPage {
  private Text                      text;
  private Table                     table;
  private Combo                     combo     = null;
  private Map<String, List<String>> tableMap  = null;
  private List<String>              tableList = null;

  public WizardPageOne( Map<String, List<String>> tableMap ) {
    super("page1");
    setTitle("请选择表信息");
    setDescription("请输入表信息");
    this.tableMap = tableMap;
  }

  public List<String> getTableList() {
    return this.tableList;
  }

  public void createControl( Composite parent ) {
    Composite container = new Composite(parent, 2144);
    setControl(container);
    container.setLayout(new BorderLayout());
    setComplete(false);

    Composite composite = new Composite(container, 0);
    composite.setLayoutData(new BorderLayout.BorderData(0));
    FormLayout fl_composite = new FormLayout();
    fl_composite.marginTop = 5;
    fl_composite.marginHeight = 3;
    composite.setLayout(fl_composite);

    Label lblNewLabel = new Label(composite, 0);
    FormData fd_lblNewLabel = new FormData();
    fd_lblNewLabel.top = new FormAttachment(0, 10);
    fd_lblNewLabel.left = new FormAttachment(0, 3);
    lblNewLabel.setLayoutData(fd_lblNewLabel);
    lblNewLabel.setText("请输入表名：");
    this.combo = new Combo(composite, 8);
    this.combo.setTouchEnabled(true);
    this.combo.setToolTipText("请选择表空间");
    FormData fd_combo = new FormData();
    fd_combo.top = new FormAttachment(lblNewLabel, -5, 128);
    fd_combo.left = new FormAttachment(lblNewLabel, 10, 131072);
    this.combo.setLayoutData(fd_combo);
    if(( this.tableMap != null ) && ( this.tableMap.size() > 0 )) {
      this.combo.setItems((String[]) this.tableMap.keySet().toArray(new String[this.tableMap.keySet().size()]));
    }
    this.combo.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        WizardPageOne.this.table.removeAll();

        String key = WizardPageOne.this.combo.getText();
        for( String k : WizardPageOne.this.tableMap.get(key) ) {
          TableItem item = new TableItem(WizardPageOne.this.table, 0);
          item.setText(k);
        }
      }
    });
    this.text = new Text(composite, 2048);
    this.text.addModifyListener(new ModifyListener() {
      public void modifyText( ModifyEvent arg0 ) {
        WizardPageOne.this.table.removeAll();

        String key = WizardPageOne.this.combo.getText();
        if(key.equals("")) {
          MessageDialog.openConfirm(WizardPageOne.this.getShell(), "提交", "请选择对应的表空间");
          return;
        }
        List<String> newTableList = new ArrayList();
        for( String k : WizardPageOne.this.tableMap.get(key) ) {
          if(k.toLowerCase().indexOf(WizardPageOne.this.text.getText().toLowerCase()) != -1) {
            newTableList.add(k);
          }
        }
        for( String k : newTableList ) {
          TableItem item = new TableItem(WizardPageOne.this.table, 0);
          item.setText(k);
        }
      }
    });
    FormData fd_text = new FormData();
    fd_text.top = new FormAttachment(this.combo, 2, 128);
    fd_text.left = new FormAttachment(this.combo, 10, 131072);
    this.text.setLayoutData(fd_text);

    this.table = new Table(container, 65570);
    this.tableList = new ArrayList();
    this.table.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        WizardPageOne.this.tableList.clear();
        for( int i = 0; i < WizardPageOne.this.table.getItems().length; i++ ) {
          if(WizardPageOne.this.table.getItems()[i].getChecked()) {
            WizardPageOne.this.tableList.add(WizardPageOne.this.table.getItems()[i].getText());
          }
        }
        if(WizardPageOne.this.tableList.size() > 0) {
          WizardPageOne.this.setComplete(true);
        }
        else {
          WizardPageOne.this.setComplete(false);
        }
      }
    });
    this.table.setLayoutData(new BorderLayout.BorderData(2));
    this.table.setHeaderVisible(true);
    this.table.setLinesVisible(true);

    TableColumn col1 = new TableColumn(this.table, 0);
    col1.setText("表名");
    col1.setWidth(300);
  }

  private void setComplete( boolean flag ) {
    setPageComplete(flag);
  }
}
