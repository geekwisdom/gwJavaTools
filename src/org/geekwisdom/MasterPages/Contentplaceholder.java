package org.geekwisdom.MasterPages;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.*;
import java.io.*;

	public class Contentplaceholder extends SimpleTagSupport 
	{
	    /** PLaceholerid name **/
	    private String placeholderid;
	    private String runat;
	    private String TAG_PREFIX="";
	    public void setId(String Id) {
	        this.placeholderid=Id;
	    }

	    public void setRunat(String at)
		{
		runat="Server"; //for compatibilty / look feel like .NET MasterPaes
		}

	    @Override
	    public void doTag() throws JspException, IOException {
	        PageContext pageContext = (PageContext)getJspContext();
	        String bodyResult = getFragment(getJspBody());
	        String FragmentContents = getFragmentContents(pageContext);
		Writer out=pageContext.getOut();
		if (FragmentContents == "") 
			out.write(bodyResult);
		else
			out.write(FragmentContents);
	    }

	    private String getFragmentContents(PageContext pageContext) {
	        String FragmentContents = (String) pageContext.findAttribute(TAG_PREFIX + placeholderid + ".fragment");
	        if (FragmentContents == null) {
	            return "";
	        }
	        return FragmentContents;
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


