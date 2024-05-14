<%@ page
	import="wt.fc.ReferenceFactory, wt.fc.WTReference, wt.part.WTPart, wt.util.WTException, java.text.SimpleDateFormat, java.util.Date, java.util.TimeZone"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<%@ taglib prefix="w" uri="http://www.ptc.com/windchill/taglib/wrappers"%>
<%@ taglib prefix="jca"
	uri="http://www.ptc.com/windchill/taglib/components"%>
<%@ taglib prefix="fmt" uri="http://www.ptc.com/windchill/taglib/fmt"%>

<%
	String prepopulatedZipFileName = "";
	int MAX = 60;
	int MIN = 3;

	try {
		String oidParam = request.getParameter("oid");
		if (oidParam != null && !oidParam.isEmpty()) {
			NmOid oid = new NmOid(oidParam);
			ReferenceFactory referenceFactory = new ReferenceFactory();
			WTReference wtReference = oid.getWtRef();
			if (wtReference != null && wtReference.getObject() instanceof WTPart) {
				WTPart part = (WTPart) wtReference.getObject();
				Date timestamp = new Date();
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HHmm z");
				dateFormat.setTimeZone(TimeZone.getTimeZone("GST"));
				String formattedTimestamp = dateFormat.format(timestamp);
				prepopulatedZipFileName = "NIMR_MultiLevelConsolidatedBOM" + "~" + part.getNumber() + "~"
						+ formattedTimestamp + ".xlsx";

			} else {
				System.out.println("INVALID PART");
			}
		} else {
			System.out.println("INVALID OID");
		}
	} catch (WTException e) {
		e.printStackTrace(); // Handle the exception appropriately
	}
%>

<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>

<table class="attr_panel">
	<tr height="30">
		<th style="font-size: 16px; text-align: left"><b>Packaging
				Detail:</b></th>
	</tr>
	<tr height="10"></tr>
	<tr height="30">
		<td style="font-size: 15px;" scope="row" width="300">Your file
			name:</td>
		<td style="font-size: 15px;"><w:textBox title=""
				value="<%=prepopulatedZipFileName%>" size="<%=MAX%>"
				maxlength="<%=MAX%>" id="FILENAME_TEXTBOX_INPUT"
				name="FILENAME_TEXTBOX_INPUT" readonly="True" />
	</tr>
	<tr height="30"></tr>
</table>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>
