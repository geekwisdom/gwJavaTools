package org.geekwisdom.MasterPages;

import javax.servlet.ServletContext; 
import javax.servlet.ServletException; 
import javax.servlet.jsp.JspException; 
import javax.servlet.jsp.PageContext; 
import javax.servlet.jsp.tagext.SimpleTagSupport; 
import java.io.IOException; 
import java.io.StringWriter; 

public class UseTag extends SimpleTagSupport {
    public static final String MASTER_PAGE_PATH = "master-page-path";
    /**
     * Parent Template name(relative path) *
     */
    private String name;

    public void setMasterpagefile(String name) {
        this.name = name;
    }

    @Override
    public void doTag() throws JspException, IOException {
        StringWriter ignoredWriter = new StringWriter();
        getJspBody().invoke(ignoredWriter); // ignore body text

        PageContext pageContext = (PageContext) getJspContext();

        try {
            pageContext.forward(getRefinedName());
        } catch (ServletException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    protected String getRefinedName() {
        ServletContext servletContext = ((PageContext) getJspContext()).getServletContext();
        String masterpath = servletContext.getInitParameter(MASTER_PAGE_PATH);

        if (masterpath == null) {
            return name;
        }
        return masterpath + name;
    }
}
