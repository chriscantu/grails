package org.codehaus.groovy.grails.web.servlet;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;

import java.util.Map;

import junit.framework.TestCase;

import org.codehaus.groovy.grails.MockApplicationContext;
import org.codehaus.groovy.grails.commons.DefaultGrailsApplication;
import org.codehaus.groovy.grails.commons.GrailsApplication;
import org.codehaus.groovy.grails.web.metaclass.ControllerDynamicMethods;
import org.codehaus.groovy.grails.web.servlet.mvc.SimpleGrailsControllerHelper;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;

public class GrailsApplicationAttributesTests extends TestCase {

	/*
	 * Test method for 'org.codehaus.groovy.grails.web.servlet.DefaultGrailsApplicationAttributes.getTemplateUri(String, ServletRequest)'
	 */
	public void testGetTemplateUri() {
		 GrailsApplicationAttributes attrs = new DefaultGrailsApplicationAttributes(new MockServletContext());
		 
		 assertEquals("/WEB-INF/grails-app/views/_test.gsp",attrs.getTemplateUri("/test", new MockHttpServletRequest()));
		 assertEquals("/WEB-INF/grails-app/views/shared/_test.gsp",attrs.getTemplateUri("/shared/test", new MockHttpServletRequest()));
	}

	public void testGetTagLibForTag() throws Exception {
		GroovyClassLoader gcl = new GroovyClassLoader();
        Class controllerClass = gcl.parseClass( "class TestController {\n" +
									                "@Property list = {\n" +
									                "}\n" +
									                "}" );
        Class tagLibClass1 = gcl.parseClass( "class FirstTagLib {\n" +
								                "@Property firstTag = {\n" +
								                "}\n" +
								                "}" ); 
        Class tagLibClass2 = gcl.parseClass( "class SecondTagLib {\n" +
                "@Property secondTag = {\n" +
                "}\n" +
                "}" );        
        
		GrailsApplicationAttributes attrs = getAttributesForClasses(new Class[]{controllerClass,tagLibClass1,tagLibClass2},gcl);
		assertNotNull(attrs);
		assertNotNull(attrs.getApplicationContext());
		assertNotNull(attrs.getGrailsApplication());
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		GroovyObject controller = (GroovyObject)attrs.getApplicationContext().getBean("TestController");
		SimpleGrailsControllerHelper helper = new SimpleGrailsControllerHelper(attrs.getGrailsApplication(),attrs.getApplicationContext(),attrs.getServletContext());
		new ControllerDynamicMethods(controller,helper,request,null);
		
		request.setAttribute(GrailsApplicationAttributes.CONTROLLER,controller );
		GroovyObject tagLib1 = attrs.getTagLibraryForTag(request,"firstTag");
		assertNotNull(tagLib1);
		assertNotNull(request.getAttribute(GrailsApplicationAttributes.TAG_CACHE));
		Map tagCache = (Map)request.getAttribute(GrailsApplicationAttributes.TAG_CACHE);
		
		assertNotNull(tagCache.get("firstTag"));
		
		
	}
	
	private GrailsApplicationAttributes getAttributesForClasses(Class[] classes, GroovyClassLoader gcl) {
		MockApplicationContext context = new MockApplicationContext();
		MockServletContext servletContext = new MockServletContext();
		servletContext.setAttribute(GrailsApplicationAttributes.APPLICATION_CONTEXT,context);

		GrailsApplication app = new DefaultGrailsApplication(classes,gcl);
		context.registerMockBean(GrailsApplication.APPLICATION_ID,app);
		
		for (int i = 0; i < app.getControllers().length; i++) {
			context.registerMockBean(app.getControllers()[i].getFullName(), app.getControllers()[i].newInstance());
		}
		
		for (int i = 0; i < app.getGrailsTabLibClasses().length; i++) {
			context.registerMockBean(app.getGrailsTabLibClasses()[i].getFullName(), app.getGrailsTabLibClasses()[i].newInstance());
		}		
		return new DefaultGrailsApplicationAttributes(servletContext);		
	}
}
