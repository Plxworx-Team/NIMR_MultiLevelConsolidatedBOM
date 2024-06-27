
package ext.customreport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.lwc.server.PersistableAdapter;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import wt.enterprise.RevisionControlled;
import wt.fc.Persistable;
import wt.fc.QueryResult;

import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartUsageLink;
import wt.util.WTException;
import wt.util.WTProperties;

import wt.vc.config.LatestConfigSpec;

import com.ptc.core.components.beans.ObjectBean;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

public class BOMExcel extends DefaultObjectFormProcessor {
	private static final String FOLDER_CONSTANT = "BOMReport";
	private static final String tmpLocation;
	private static String homeLocation;
	static {
		String tempStr = null;
		String homefolder = null;
		try {
			tempStr = WTProperties.getLocalProperties().getProperty("wt.temp");
			homefolder = WTProperties.getLocalProperties().getProperty("wt.home");
			System.out.println("tempStr=" + tempStr);
			System.out.println("homefolder=" + homefolder);
		} catch (IOException e) {
			e.printStackTrace();
		}
		tmpLocation = tempStr;
		homeLocation = homefolder;
	}
	

	@Override
	public FormResult doOperation(NmCommandBean nmCommandBean, List<ObjectBean> list) throws WTException {
	
		WTPart part = (WTPart) nmCommandBean.getPageOid().getRefObject();
		System.out.println("@@@ parent part:-" + part.getNumber());

		try {
			generateBOMExcelFile(part);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		FormResult formResult = super.doOperation(nmCommandBean, list);
		return formResult;
	}

/****
 * 	
 * @param part
 * @return
 * @throws WTException
 * @throws IOException
 */
	public static File generateBOMExcelFile(WTPart part) throws WTException, IOException {

		System.out.println("Part Number:" + part.getNumber());
		System.out.println("Part Name: " + part.getName());
		System.out.println("Version Details:" + part.getVersionIdentifier());
		System.out.println("Lastest Iteration Details:" + part.isLatestIteration());

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet spreadsheet = workbook.createSheet("BOM SHEET");
		// XSSFRow row = spreadsheet.createRow(0);
		LinkedHashSet<Object[]> BOMData = new LinkedHashSet<>();
		XSSFCellStyle boldCenteredStyle = workbook.createCellStyle();
		XSSFFont boldFont = workbook.createFont();
		boldFont.setBold(true);
		boldCenteredStyle.setFont(boldFont);

		// boldCenteredStyle.getFont().setBold(true);
		boldCenteredStyle.setAlignment(HorizontalAlignment.CENTER);
		boldCenteredStyle.setBorderBottom(BorderStyle.MEDIUM);
		boldCenteredStyle.setBorderTop(BorderStyle.MEDIUM);
		boldCenteredStyle.setBorderLeft(BorderStyle.MEDIUM);
		boldCenteredStyle.setBorderRight(BorderStyle.MEDIUM);

		// add header
		addHeader(BOMData, (XSSFSheet) spreadsheet, workbook);

		HashMap<String, Double> partQuantities = new HashMap<>();

		// Collect the part structure data in "BOMData" 
		collectPartData(part, BOMData, spreadsheet, workbook, partQuantities);
		System.out.println("Count of BOMData:" + BOMData.size());
		XSSFRow row1;
		int rowid = 0;
		Iterator<Object[]> itr = BOMData.iterator();

		while (itr.hasNext()) {
			row1 = (XSSFRow) spreadsheet.createRow(rowid++);
			Object[] objectArr = itr.next();
			int cellid = 0; // Corrected cell index to start from 0
			
			//Iterating BOM data to print in excel.

			for (Object obj1 : objectArr) {
				XSSFCell cell1 = row1.createCell(cellid++);
				XSSFCellStyle centeredStyle = workbook.createCellStyle();
				centeredStyle.setFillBackgroundColor(IndexedColors.AQUA.getIndex());
				centeredStyle.setAlignment(HorizontalAlignment.CENTER);
				spreadsheet.autoSizeColumn(cellid);

				if (obj1 != null) { // Check for null before setting cell value
					cell1.setCellValue(obj1.toString());
				}

				centeredStyle.setBorderBottom(BorderStyle.MEDIUM);
				centeredStyle.setBorderTop(BorderStyle.MEDIUM);
				centeredStyle.setBorderLeft(BorderStyle.MEDIUM);
				centeredStyle.setBorderRight(BorderStyle.MEDIUM);
				XSSFFont font = workbook.createFont();
				font.setBold(false);
				centeredStyle.setFont(font);
				cell1.setCellStyle(centeredStyle);
			}
		}

		File excelFile = new File(
				tmpLocation + File.separatorChar + FOLDER_CONSTANT + File.separatorChar + part.getName() + "_"
						+ part.getVersionDisplayIdentifier() + "_" + part.getLifeCycleName() + ".xlsx");
		System.out.println("Excel Sheet: " + excelFile.getPath());
		FileUtils.forceMkdirParent(excelFile);
		FileUtils.touch(excelFile);
		FileOutputStream out = new FileOutputStream(excelFile);
		workbook.write(out);
		out.close();
		workbook.close();
		return excelFile;
	}

	public static void collectPartData(WTPart part, LinkedHashSet<Object[]> BOMData, Sheet spreadsheet,
			XSSFWorkbook workbook, HashMap<String, Double> partQuantities) throws WTException, IOException {

		// Query for child parts
		QueryResult qr = WTPartHelper.service.getUsesWTParts(part, new LatestConfigSpec());

		if (qr != null && qr.size() > 0) { 
			while (qr.hasMoreElements()) {
				Persistable[] persistablePart = (Persistable[]) qr.nextElement();
				WTPartUsageLink usageLink = (WTPartUsageLink) persistablePart[0];
				WTPart child = (WTPart) persistablePart[1];

				// Get quantity and unit for the child part
				double quantity = usageLink.getQuantity().getAmount();
				String unit = usageLink.getQuantity().getUnit().toString();

				// Get the part number
				String partNumber = child.getNumber();

				// If the part number already exists in partQuantities, update its quantity
				if (partQuantities.containsKey(partNumber)) {
					double existingQuantity = partQuantities.get(partNumber);
					partQuantities.put(partNumber, existingQuantity + quantity);
				} else { // If it's a new part number, add it to partQuantities
					partQuantities.put(partNumber, quantity);
				}

				// Check if this part number already exists in BOMData
				Object[] existingRow = null;
				for (Object[] row : BOMData) {
					if (rowContainsPartNumber(row, partNumber)) {
						existingRow = row;
						break;
					}
				}

				// If the part number exists in BOMData, remove the existing entry and add a new
				// entry with updated quantity
				if (existingRow != null) {
					BOMData.remove(existingRow);
				}

				// Initialize rowData
				Object[] rowData = new Object[] {};
				// Populate rowData with additional properties
				LinkedHashMap<String, String> props = new LinkedHashMap<>();
				try (BufferedReader reader1 = new BufferedReader(
						new FileReader(homeLocation + File.separatorChar + "codebase" + File.separatorChar
								+ "BOMProperties" + File.separatorChar + "BOMReport.properties"))) {
					String line;
					while ((line = reader1.readLine()) != null) {
						line = line.trim();
						if (!line.isEmpty() && !line.startsWith("#")) { // Skip empty lines and comments
							int index = line.indexOf('=');
							if (index != -1) {
								String key = line.substring(0, index).trim();
								if (!key.equals("ifValueNotPresent") && !key.equals("ifattributeNotPresent")) {
									String value = line.substring(index + 1).trim();
									props.put(key, value);
								}
							}
						}
					}
				}

				// Printing the key-value pairs
				int columnIndex = 0;
				for (Map.Entry<String, String> entry : props.entrySet()) {
					Object value = null;
					String key = entry.getKey();
					if (key.equalsIgnoreCase("quantity.unit")) {
						// Get the aggregated quantity for this part number
						double aggregatedQuantity = partQuantities.get(partNumber);
						value = aggregatedQuantity + " " + unit;
					} else if (key.equalsIgnoreCase("ChangeStatus")) {
						boolean status = child.isHasPendingChange(); // Assuming child.isHasPendingChange() returns a
						// boolean
						value = status ? "pending change exist" : " ";
					} else if (key.equalsIgnoreCase("iterationInfo.latest")) {
						RevisionControlled revision = (RevisionControlled) child;
						value = revision.getIterationIdentifier().getValue();
					} else if (key.equalsIgnoreCase("versionInfo.identifier.versionId")) {
						RevisionControlled revision = (RevisionControlled) child;
						value = String.format("%s.%s", revision.getVersionIdentifier().getValue(),
								revision.getIterationIdentifier().getValue());
					} else if (key.equalsIgnoreCase("view")) {
						value = child.getViewName();
					} else if (key.equalsIgnoreCase("objecttype")) {
						value = child.getDisplayType().getLocalizedMessage(Locale.ENGLISH);
					} else if (key.equalsIgnoreCase("state.state")) {
						value = child.getState().getState().getDisplay();
					} else {
						value = getAttributeValue(child, key);
					}
					if (value != null) { // Check if value is present
						rowData = ArrayUtils.add(rowData, columnIndex++, value);
					}
				}

				// Add rowData to BOMData
				BOMData.add(rowData);

				// Recursively process child parts
				collectPartData(child, BOMData, spreadsheet, workbook, partQuantities);
			}
		}
	}

	/*
	 * Check if part is already present in the map
	 */
	
	private static boolean rowContainsPartNumber(Object[] row, String partNumber) {
		for (Object cell : row) {
			if (cell != null && cell.equals(partNumber)) {
				return true;
			}
		}
		return false;
	}

	public static Object getAttributeValue(Persistable per, String internalName) throws IOException {

		Object value = null;
		PersistableAdapter obj;

		// Load properties file
		Properties prop = new Properties();

		try (FileInputStream file = new FileInputStream(homeLocation + File.separatorChar + "codebase"
				+ File.separatorChar + "BOMProperties" + File.separatorChar + "BOMReport.properties")) {
			prop.load(file);

		} catch (IOException e) {
			e.printStackTrace();
			throw e; // Re-throw the IOException
		}

		try {
			obj = new PersistableAdapter(per, null, Locale.US, null);
			if (internalName != null) {
				try {
					obj.load(internalName);
					value = obj.get(internalName);
					if (value == null) {
						value = prop.getProperty("ifValueNotPresent");
						System.out.println("Attribute value not present (empty) = " + value);
					}

				} catch (WTException ex) {
					value = prop.getProperty("ifattributeNotPresent");
					System.out.println("Attribute is not present on the part (-) = " + value);
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return value;

	}

	private static void addHeader(LinkedHashSet<Object[]> BOMData, XSSFSheet spreadsheet, XSSFWorkbook workbook)
	        throws IOException {
	    // Create the header row only if BOMData is empty (no child parts)
	    if (BOMData.isEmpty()) {
	        XSSFRow headerRow = spreadsheet.createRow(0); // Create header at index 0 (first row)
	        int cellId = 0; // Starting from the first cell (index 0)

	        // Read headers from properties file
	        LinkedHashMap<String, String> props = new LinkedHashMap<>();
	        try (BufferedReader reader1 = new BufferedReader(new FileReader(
	                homeLocation + File.separatorChar + "codebase" + File.separatorChar + "BOMProperties"
	                        + File.separatorChar + "BOMReport.properties"))) {
	            String line;
	            while ((line = reader1.readLine()) != null) {
	                line = line.trim();
	                if (!line.isEmpty() && !line.startsWith("#")) { // Skip empty lines and comments
	                    int index = line.indexOf('=');
	                    if (index != -1) {
	                        String key = line.substring(0, index).trim();
	                        if (!key.equals("ifValueNotPresent") && !key.equals("ifattributeNotPresent")) {
	                            String value = line.substring(index + 1).trim();
	                            props.put(key, value);
	                            XSSFCell headerCell = headerRow.createCell(cellId++);
	                            headerCell.setCellValue(value);

	                            // Apply bold font to the header cell
	                            XSSFFont boldFont = workbook.createFont();
	                            boldFont.setBold(true);
	                            XSSFCellStyle boldStyle = workbook.createCellStyle();
	                            boldStyle.setFont(boldFont);
	                            boldStyle.setAlignment(HorizontalAlignment.CENTER); // Center align the header text
	                            headerCell.setCellStyle(boldStyle);
	                        }
	                    }
	                }
	            }
	        }

	        // Add headers to BOMData
	        BOMData.add(props.values().toArray(new Object[0]));
	    }
	}


}
