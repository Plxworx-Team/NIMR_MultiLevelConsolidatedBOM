<%@ page
	import="java.io.File, java.io.FileInputStream, java.io.IOException, java.io.OutputStream"%>
<%@ page import="javax.servlet.http.HttpServletResponse"%>
<%@ page import="wt.part.WTPart"%>
<%@ page import="wt.fc.WTReference, wt.fc.ReferenceFactory"%>
<%@ page import="com.ptc.netmarkets.model.NmOid"%>
<%@ page import="ext.customreport.BOMExcel"%>
<%@ page import="org.apache.commons.io.IOUtils"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.Date"%>
<%@ page import="java.sql.Timestamp"%>
<%@ page import="java.util.TimeZone"%>

<%
	WTPart part = (WTPart) session.getAttribute("selectedPart");

	final int BUFFER_CONST = 8192;
	File excelFile = BOMExcel.findPrtNo(part);

	if (excelFile != null && excelFile.isFile()) {
		Date timestamp = new Date();
		// Set the time zone to GST (Gulf Standard Time)
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HHmm z");
		dateFormat.setTimeZone(TimeZone.getTimeZone("GST"));
		String formattedTimestamp = dateFormat.format(timestamp);
		String fileName = "NIMR_MultiLevelConsolidatedBOM" + "~" + part.getNumber() + "~" + formattedTimestamp
				+ ".xlsx";
		System.out.println("Excel file found: " + excelFile.getAbsolutePath());

		// WRITE INTO RESPONSE in CHUNKS

		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
		OutputStream sos = response.getOutputStream();
		FileInputStream in = null;

		try {

			in = new FileInputStream(excelFile);
			byte[] buffer = new byte[BUFFER_CONST];
			int length;
			while ((length = in.read(buffer)) > 0) {
				sos.write(buffer, 0, length);
				sos.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(in);
		}

		sos.close();
		response.flushBuffer();
		out.clear();

		// DELETION OF FILE

		excelFile.delete();
		excelFile = null;

	} else {

		// NO DOWNLOAD
		// TODO: Custom Error Message

	}
%>