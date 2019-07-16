package org.geekwisdom.MasterPages;
import javax.servlet.jsp.JspException; 
import javax.servlet.jsp.*; 
import javax.servlet.jsp.tagext.*; 
import java.io.IOException; 
import java.io.*;

public class ContentTag extends SimpleTagSupport {
//  public static final String TAG_PREFIX = ContentTag.class.getCanonicalName() + ".";
  public static final String TAG_PREFIX = "";

  private String contentplaceholderid;
  private String runat;

  public void setContentplaceholderid(String PlaceID) {
      contentplaceholderid = PlaceID;
  }

  public void setRunat(String Run) {
      this.runat="server"; //ignored: compatilblity only 
  }

  @Override
  public void doTag() throws JspException, IOException {
      verifyMasterPage();
      String bodyResult  = getFragment(getJspBody());
      PageContext pageContext = (PageContext)getJspContext();
      pageContext.setAttribute(TAG_PREFIX + contentplaceholderid + ".fragment", bodyResult, PageContext.REQUEST_SCOPE);
  }

  private void verifyMasterPage() 
 {
      UseTag usetag = (UseTag) findAncestorWithClass(this, UseTag.class);
      if (usetag == null) {
          throw new IllegalStateException("Content placeholders must be called in a masterpage use tag.");
      }
  }

private String getFragment(JspFragment jspBody) throws IOException, JspException 
 { 
      if (jspBody == null) { 
          return ""; 
      } 


      StringWriter writer = new StringWriter(); 
      jspBody.invoke(writer); 
      return writer.toString(); 
  } 


}
