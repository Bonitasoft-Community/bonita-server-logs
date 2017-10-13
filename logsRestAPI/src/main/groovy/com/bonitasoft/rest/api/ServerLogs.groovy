package com.bonitasoft.rest.api

import com.bonitasoft.web.extension.rest.RestAPIContext
import com.bonitasoft.web.extension.rest.RestApiController
import groovy.json.JsonBuilder
import org.apache.http.HttpHeaders
import org.bonitasoft.web.extension.ResourceProvider
import org.bonitasoft.web.extension.rest.RestApiResponse
import org.bonitasoft.web.extension.rest.RestApiResponseBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.nio.file.Paths

class ServerLogs implements RestApiController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ServerLogs.class)
	private static final String TOMCAT_LOGS_PATH = "log";
	@Override
	RestApiResponse doHandle(HttpServletRequest request, RestApiResponseBuilder responseBuilder, RestAPIContext context) {
		def catalinaHome = System.getProperty("catalina.home")
		def jbossHome = System.getProperty("org.jboss.boot.log.file")

		if (!catalinaHome && !jbossHome) {
			return buildResponse(responseBuilder, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "catalina.home or org.jboss.boot.log.file system variables are not set")
		}
		def logsPath;
		if (catalinaHome) {
			logsPath = TOMCAT_LOGS_PATH;
		}else{
			File file = new File(jbossHome)
			logsPath  = file.getAbsoluteFile().getParent();
		}

		def logfile = request.getParameter("content")
		if (!logfile) {
			def logFiles = Paths.get(logsPath)
					.toFile()
					.listFiles(new FileFilter() {
				boolean accept(File file) {
					return file.getName().matches(".*\\.log.*")
				}
			})
					.collect()
					.toSorted(lastUpdateComparator())
					.collect { [name: it.name, lastModified: it.lastModified()] }

			return buildResponse(responseBuilder, HttpServletResponse.SC_OK, new JsonBuilder(logFiles).toPrettyString())
		}

		def matchingFiles = Paths.get(logsPath)
				.toFile()
				.listFiles(new FileFilter() {
			boolean accept(File file) {
				return file.isFile() && file.getName().equals(logfile)
			}
		})
				.collect()

		if (matchingFiles.isEmpty()) {
			return buildResponse(responseBuilder, HttpServletResponse.SC_NOT_FOUND, "'$logfile' not found")
		}
		def File file = matchingFiles[0];
		def content = file.getText('UTF-8')
		responseBuilder.withMediaType("text/plain")
		if (request.getParameter("download")) {
			responseBuilder.withMediaType("application/force-download")
		}
		return responseBuilder.with {
			withAdditionalHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(file.length()))
			withAdditionalHeader("Content-Transfer-Encoding", "binary")
			withAdditionalHeader("Content-Disposition", "attachment; filename=\"$logfile\"")
			withResponseStatus(HttpServletResponse.SC_OK)
			withResponse(content.isEmpty() ? "No logs yet !" : content)
			build()
		}

	}

	def Comparator<File> lastUpdateComparator(){
		new Comparator<File>(){
					int compare( File f1, File  f2) {
						f2.lastModified().compareTo(f1.lastModified())
					}
				}
	}



	/**
	 * Build an HTTP response.
	 *
	 * @param  responseBuilder the Rest API response builder
	 * @param  httpStatus the status of the response
	 * @param  body the response body
	 * @return a RestAPIResponse
	 */
	RestApiResponse buildResponse(RestApiResponseBuilder responseBuilder, int httpStatus, Serializable body) {
		return responseBuilder.with {
			withResponseStatus(httpStatus)
			withResponse(body)
			build()
		}
	}

	/**
	 * Returns a paged result like Bonita BPM REST APIs.
	 * Build a response with content-range data in the HTTP header.
	 *
	 * @param  responseBuilder the Rest API response builder
	 * @param  body the response body
	 * @param  p the page index
	 * @param  c the number of result per page
	 * @param  total the total number of results
	 * @return a RestAPIResponse
	 */
	RestApiResponse buildPagedResponse(RestApiResponseBuilder responseBuilder, Serializable body, int p, int c, long total) {
		return responseBuilder.with {
			withAdditionalHeader(HttpHeaders.CONTENT_RANGE,"$p-$c/$total");
			withResponse(body)
			build()
		}
	}

	/**
	 * Load a property file into a java.util.Properties
	 */
	Properties loadProperties(String fileName, ResourceProvider resourceProvider) {
		Properties props = new Properties()
		resourceProvider.getResourceAsStream(fileName).withStream { InputStream s ->
			props.load s
		}
		props
	}
}
