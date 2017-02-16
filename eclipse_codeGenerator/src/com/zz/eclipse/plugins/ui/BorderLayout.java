package com.zz.eclipse.plugins.ui;


import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;


public class BorderLayout extends Layout {
  public static final int NORTH  = 0;
  public static final int SOUTH  = 1;
  public static final int CENTER = 2;
  public static final int EAST   = 3;
  public static final int WEST   = 4;

  public static class BorderData {
    public int region = 2;

    public BorderData() {}

    public BorderData( int region ) {
      this.region = region;
    }
  }

  public Control[] controls = new Control[5];
  Point[]          sizes;
  int              width;
  int              height;

  protected Point computeSize( Composite composite, int wHint, int hHint, boolean flushCache ) {
    if(( this.sizes == null ) || ( flushCache )) {
      refreshSizes(composite.getChildren());
    }
    int w = wHint;
    int h = hHint;
    if(w == -1) {
      w = this.width;
    }
    if(h == -1) {
      h = this.height;
    }
    return new Point(w, h);
  }

  protected void layout( Composite composite, boolean flushCache ) {
    if(( flushCache ) || ( this.sizes == null )) {
      refreshSizes(composite.getChildren());
    }
    Rectangle clientArea = composite.getClientArea();
    if(this.controls[0] != null) {
      this.controls[0].setBounds(clientArea.x, clientArea.y, clientArea.width, this.sizes[0].y);
    }
    if(this.controls[1] != null) {
      this.controls[1].setBounds(clientArea.x, clientArea.y + clientArea.height - this.sizes[1].y, clientArea.width,
        this.sizes[1].y);
    }
    if(this.controls[4] != null) {
      this.controls[4].setBounds(clientArea.x, clientArea.y + this.sizes[0].y, this.sizes[4].x,
        clientArea.height - this.sizes[0].y - this.sizes[1].y);
    }
    if(this.controls[3] != null) {
      this.controls[3].setBounds(clientArea.x + clientArea.width - this.sizes[3].x, clientArea.y + this.sizes[0].y,
        this.sizes[3].x, clientArea.height - this.sizes[0].y - this.sizes[1].y);
    }
    if(this.controls[2] != null) {
      this.controls[2].setBounds(clientArea.x + this.sizes[4].x, clientArea.y + this.sizes[0].y,
        clientArea.width - this.sizes[4].x - this.sizes[3].x, clientArea.height - this.sizes[0].y - this.sizes[1].y);
    }
  }

  private void refreshSizes( Control[] children ) {
    for( int i = 0; i < children.length; i++ ) {
      Object layoutData = children[i].getLayoutData();
      if(( layoutData != null ) && ( ( layoutData instanceof BorderData ) )) {
        BorderData borderData = (BorderData) layoutData;
        if(( borderData.region >= 0 ) && ( borderData.region <= 4 )) {
          this.controls[borderData.region] = children[i];
        }
      }
    }
    this.width = 0;
    this.height = 0;
    if(this.sizes == null) {
      this.sizes = new Point[5];
    }
    for( int i = 0; i < this.controls.length; i++ ) {
      Control control = this.controls[i];
      if(control == null) {
        this.sizes[i] = new Point(0, 0);
      }
      else {
        this.sizes[i] = control.computeSize(-1, -1, true);
      }
    }
    this.width = Math.max(this.width, this.sizes[0].x);
    this.width = Math.max(this.width, this.sizes[4].x + this.sizes[2].x + this.sizes[3].x);
    this.width = Math.max(this.width, this.sizes[1].x);
    this.height =
        ( Math.max(Math.max(this.sizes[4].y, this.sizes[3].y), this.sizes[2].y) + this.sizes[0].y + this.sizes[1].y );
  }
}
