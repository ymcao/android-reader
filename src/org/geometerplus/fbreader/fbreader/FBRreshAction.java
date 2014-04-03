package org.geometerplus.fbreader.fbreader;

public class FBRreshAction extends FBAction
{
	public FBRreshAction(FBReaderApp paramFBReaderApp, int paramInt)
	  {
	    super(paramFBReaderApp);
	  }

	  protected void run(Object[] paramArrayOfObject)
	  {
	    this.Reader.clearTextCaches();
	    this.Reader.getViewWidget().repaint();
	  }
	}