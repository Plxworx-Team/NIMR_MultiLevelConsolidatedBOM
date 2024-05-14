<%@include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ page import="com.ptc.netmarkets.model.NmOid"%>
<%@ page
	import="wt.fc.ReferenceFactory, wt.fc.WTReference, wt.part.WTPart"%>
<%@ page import="java.lang.*"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%
	String oidParam = request.getParameter("oid");
				if (oidParam != null && !oidParam.isEmpty()) {
					NmOid oid = new NmOid(oidParam); // Assuming NmOid requires a constructor with a parameter
					System.out.println("NmOid: " + oid.getType());

					ReferenceFactory referenceFactory = new ReferenceFactory();
					WTReference wtReference = oid.getWtRef();
					if (wtReference != null && wtReference.getObject() instanceof WTPart) {
						WTPart part = (WTPart) wtReference.getObject();
						// Set the WTPart object in session
						session.setAttribute("selectedPart", part);
					}
				}
%>

<c:set var="buttonList" value="BOMReportWizardButtons" scope="page" />
<c:set var="title" value="BOM Excel Report" scope="page" />

<jca:wizard buttonList="${buttonList}" title="${title}">
	<jca:wizardStep action="report_WizardStep" type="psb" />
</jca:wizard>

<script>
	function onSubmit() {
		// Redirect to customreport.jsp without passing oid directly
		window.location.href = 'netmarkets/jsp/ext/customjsp/customreport.jsp';
	}
</script>

<%@include file="/netmarkets/jsp/util/end.jspf"%>
