package org.codehaus.groovy.grails.cli;

class WarTests  extends AbstractCliTests {

	void testWAR() {
	    gantRun( ["-f", "scripts/CreateApp.groovy"] as String[])
		
		
		System.setProperty("base.dir", appBase + File.separatorChar + System.getProperty("grails.cli.args"))
		gantRun( ["-f", "scripts/War.groovy"] as String[])
		
		assert new File("${appBase}/testapp/testapp-0.1.war").exists()
		
		ant.unzip(src:"${appBase}/testapp/testapp-0.1.war", dest:"${appBase}/unzipped")
		
		// test critical files
		assert new File("${appBase}/unzipped/WEB-INF/applicationContext.xml").exists()
		assert new File("${appBase}/unzipped/WEB-INF/sitemesh.xml").exists()
		assert new File("${appBase}/unzipped/WEB-INF/grails.xml").exists()
		assert new File("${appBase}/unzipped/WEB-INF/web.xml").exists()
		assert new File("${appBase}/unzipped/css/main.css").exists()
		assert new File("${appBase}/unzipped/js/application.js").exists()
		assert new File("${appBase}/unzipped/WEB-INF/grails-app/i18n/messages.properties").exists()
			
	}

}
