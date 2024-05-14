
package ext.customreport;
import wt.util.resource.RBComment;
import wt.util.resource.RBEntry; 
import wt.util.resource.RBPseudo;
import wt.util.resource.RBUUID;
import wt.util.resource.WTListResourceBundle;
 
@RBUUID("ext.customreport.PsbCustomActionsRB")
 
public final class PsbCustomActionsRB extends WTListResourceBundle {
 
    @RBEntry("NIMR Multi-Level Consolidated BoM")
    public static final String PSB_CUSTOMREPORTGWT01_DESCRIPTION = "psb.psbCustomReportGWT01.description";
 
    @RBEntry("NIMR Multi-Level Consolidated BoM")
    public static final String PSB_CUSTOMREPORTGWT01_TITLE = "psb.psbCustomReportGWT01.title";
 
    @RBEntry("Custom Report 01 Tooltip")
    public static final String PSB_CUSTOMREPORTGWT01_TOOLTIP = "psb.psbCustomReportGWT01.tooltip";
 
    @RBEntry("height=900,width=1000")   
    @RBPseudo(false)
    @RBComment("DO NOT TRANSLATE")
    public static final String PSB_CUSTOMREPORTGWT01_MOREURLINFO= "psb.psbCustomReportGWT01.moreurlinfo";
 
    @RBEntry("report_view.png")
    @RBPseudo(false)
    @RBComment("DO NOT TRANSLATE")
    public static final String PSB_CUSTOMREPORTGWT01_ICON = "psb.psbCustomReportGWT01.icon";
 
 
}
