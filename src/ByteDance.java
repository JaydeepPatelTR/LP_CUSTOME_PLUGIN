import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFCell;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ByteDance {

	private static final String CHARSET_UTF8 = "utf-8";
	public static String response = "";
	public static String APP_ACCESS_TOKEN = "";
	public static String API_URL = "https://open.feishu.cn/open-apis/auth/v3/app_access_token/internal";
	public static String API_URL_USERID = "https://open.feishu.cn/open-apis/contact/v3/users/batch_get_id?user_id_type=open_id";
	public static String API_URL_MESSAGE = "https://www.feishu.cn/approval/openapi/v1/message/send";

	public static String APP_ID = "cli_a2b855b1933a500c";
	public static String API_SEC = "imCXH707q2BSB6aY1MvnvhyZ415ecWvz";
	public static String USER_EMAIL = "philip.wang@bytedance.com";
	public static String MESSAGE_TITLE = "Legal Tracker Pending Invoice ";
	public static String MESSAGE_DISCLAIMER = "An invoice is awaiting your review, please log into the Legal Tracker system and navigate to Financial/Invoice Review to see invoice(s) currently awaiting your review.";
	public static String MESSAGE_DISCLAIMER_2 = "Please click on the link below to navigate to Legal Tracker.";

	public static String ACTION_URL = "https://bytedance.com";
	public static String BUTTON_URL = "https://us.legaltracker.thomsonreuters.com/";
	public static String BUTTON_TEXT = "Legal Tracker";
	public static String CONST = "'";

	public static void main(String[] args) throws Exception {
		try {
			String filePath = "C:\\home\\Pending_invoice_list_for_approval_010063102070165484425596693294.xls";

			File file = new File(filePath);
			FileInputStream inputfStream = new FileInputStream(file);

			Map paramsValue = new HashMap();
			paramsValue.put("app_id", APP_ID);
			paramsValue.put("app_secret", API_SEC);

			paramsValue.put("filePath", filePath);

			paramsValue.put("api_url", API_URL);
			paramsValue.put("api_url_userid", API_URL_USERID);
			paramsValue.put("api_url_message", API_URL_MESSAGE);
			paramsValue.put("message_title", MESSAGE_TITLE);
			paramsValue.put("user_email", USER_EMAIL);
			paramsValue.put("message_disclaimer", MESSAGE_DISCLAIMER);
			paramsValue.put("message_disclaimer_2", MESSAGE_DISCLAIMER_2);
			paramsValue.put("button_url", BUTTON_URL);
			paramsValue.put("action_url", ACTION_URL);
			paramsValue.put("button_text", BUTTON_TEXT);
			String apiResponse = invokeApi(paramsValue, inputfStream);// simulation();
			System.out.println(apiResponse);

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public static String invokeApi(Map<String, String> params, FileInputStream inputfStream) throws IOException {

		String Result = "";
		try {

			SimpleDateFormat dtformatter_convert = new SimpleDateFormat("MM/dd/yyyy");
			SimpleDateFormat dateformattter = new SimpleDateFormat("dd MMMM yyyy");
			System.out.println("Started Reading Excel Details.");
			// XSSFWorkbook excelWorkbook = new XSSFWorkbook(inputfStream);
			org.apache.poi.ss.usermodel.Workbook workbook = WorkbookFactory.create(inputfStream);

			// XSSFSheet excelSheet = excelWorkbook.getSheetAt(0);
			org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheetAt(0);
			int rows = sheet.getPhysicalNumberOfRows();// 3

			int cols = sheet.getRow(0).getPhysicalNumberOfCells();// 2

			String[][] invoiceContent = new String[rows - 1][2];
			HashSet<String> InvoiceEmails = new HashSet<String>();

			Cell cell;
	

			for (int i = 1; i < rows; i++) {
				StringBuilder contentData = new StringBuilder();
				String ApprovalEmail = "";
				for (int j = 0; j < cols; j++) {
					String cellTitle = "";
					String cellContents = "";
					if (j == 0 || j == 1 || j == 6 || j == 3 || j == 4 || j == 5 || j == 8) {

						cell = sheet.getRow(i).getCell(j);
						// For cell title
						if (j == 0) {
							cellTitle = "Vendor Name: ";
						} else if (j == 1) {
							cellTitle = "Invoice Date: ";
						} else if (j == 3) {
							cellTitle = "Invoice Number: ";
						} else if (j == 4) {
							cellTitle = "Matter Name: ";
						} else if (j == 5) {
							cellTitle = "Invoice Currency: ";
						} else if (j == 6) {
							cellTitle = "Invoice Amount: ";
						}
						// For check cell value null or not
						if (cell != null) {

							if (cell.getCellType() == CellType.STRING) {
								if (j == 1) {
									cellContents = dateformattter
											.format(dtformatter_convert.parse(cell.getStringCellValue()));
								} else if (j == 8) {
									ApprovalEmail = cell.getStringCellValue().trim();
									InvoiceEmails.add(ApprovalEmail);

								} else {
									cellContents = cell.getStringCellValue();
								}
							} else if (cell.getCellType() == CellType.NUMERIC) {
								if (j == 6) {
									BigDecimal bd = new BigDecimal(cell.getNumericCellValue()).setScale(2,
											RoundingMode.HALF_UP);
									cellContents = String.valueOf(bd);
								} else {
									cellContents = String.valueOf((int) cell.getNumericCellValue());
								}
							}
						} else if (j == 8) {
							ApprovalEmail = "";
						}

						if (j != 8) {
							contentData.append(cellTitle + cellContents);
							if (j == 6) {
								contentData.append("\r\n\r\n" + params.get("message_disclaimer") + "\r\n\r\n"
										+ params.get("message_disclaimer_2") + "\r\n");
							} else {
								contentData.append("\r\n");
							}
						}
					}
				}
				if (ApprovalEmail != "") {
					invoiceContent[i - 1][0] = contentData.toString();
					invoiceContent[i - 1][1] = ApprovalEmail;
				}
			}
			System.out.println("Completed Reading Excel Details.");

			workbook.close();
			inputfStream.close();

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

			nameValuePairs.add(new BasicNameValuePair("app_id", params.get("app_id")));
			nameValuePairs.add(new BasicNameValuePair("app_secret", params.get("app_secret")));

			response = callApi(params.get("api_url"), nameValuePairs, "", "", null);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode node = mapper.readTree(response);
			APP_ACCESS_TOKEN = node.get("app_access_token").asText();

			if (isNotEmpty(APP_ACCESS_TOKEN)) {
				System.out.println("Token :" + APP_ACCESS_TOKEN);
				Result = getUserID(params.get("api_url_userid"), InvoiceEmails, APP_ACCESS_TOKEN, params,
						invoiceContent);
			} else {
				System.out.println("Error in get access Token.");
				Result = "Error in get access Token.";
			}

		} catch (Exception e) {
			Result = "Error in get Token.";
			e.printStackTrace();
			throw new RuntimeException("Error in get Token or Reading File.", e);
		}

		return Result;
	}

	private static String getUserID(String urlPre, HashSet<String> InvoiceEmails, String Token,
			Map<String, String> params, String[][] invoiceContent) throws IOException {

		String Result = "";
		try {

			String[] InvoiceEmailArray = InvoiceEmails.toArray(new String[InvoiceEmails.size()]);

			EmailBean emailBean = new EmailBean();
			emailBean.setEmails(Arrays.asList(InvoiceEmailArray));

			ObjectMapper emailMapper = new ObjectMapper();

			response = callApi(urlPre, null, Token, emailMapper.writeValueAsString(emailBean), null);
			System.out.println("Get UserID Response:" + response);

			ObjectMapper mapper = new ObjectMapper();
			JsonNode node = mapper.readTree(response);
			if (node.get("data").get("user_list") != null) {

				for (String[] invoiceMessage : invoiceContent) {
					if (invoiceMessage[0] != null) {
						for (JsonNode jsonNodes : node.get("data").get("user_list")) {
							if (invoiceMessage[1].equals(jsonNodes.get("email").asText())
									&& jsonNodes.get("user_id") != null) {
								System.out.println(invoiceMessage[1] + "-" + jsonNodes.get("email").asText());

								Result = sendMessage(params.get("api_url_message"), jsonNodes.get("user_id").asText(),
										Token, params, invoiceMessage[0]);
								break;
							}
						}
					}
				}
			} else {
				System.out.println("Error in getting UserID.");
				Result = "Error in getting UserID.";
			}
		} catch (Exception e) {
			Result = "Error in getting UserID.";
			throw new RuntimeException("getUserID exception.", e);
		}
		return Result;
	}

	private static String sendMessage(String urlPre, String userID, String token, Map<String, String> params,
			String invoiceMessage) throws IOException {
		String Result = "";

		try {
			MessageBean messageBean = new MessageBean();
			messageBean.setTemplate_id("1021");
			messageBean.setOpen_id(userID);
			messageBean.setCustom_title("@i18n@1");
			messageBean.setCustom_content("@i18n@2");

			Texts texts = new Texts();
			texts.setI18n1(params.get("message_title"));
			texts.setI18n2(invoiceMessage);
			texts.setI18n3(params.get("button_text"));

			ActionsBean actionsBean = new ActionsBean();
			actionsBean.setAction_name("@i18n@3");
			actionsBean.setUrl(params.get("button_url"));
			actionsBean.setAndroid_url(params.get("action_url"));
			actionsBean.setIos_url(params.get("action_url"));
			actionsBean.setPc_url(params.get("action_url"));

			ActionsBean objaction[] = new ActionsBean[1];
			objaction[0] = actionsBean;

			I18nResourcesBean newi18nResourcesBean = new I18nResourcesBean();
			newi18nResourcesBean.setIs_default(true);
			newi18nResourcesBean.setLocale("en_us");
			newi18nResourcesBean.setTexts(texts);

			I18nResourcesBean obj[] = new I18nResourcesBean[1];
			obj[0] = newi18nResourcesBean;

			messageBean.setActions(objaction);
			messageBean.setI18n_resources(obj);

			ObjectMapper messageMapper = new ObjectMapper();

			 Result = callApi(urlPre,
			 null,APP_ACCESS_TOKEN,"",messageMapper.writeValueAsString(messageBean));
			System.out.println(userID + " - " + invoiceMessage);
			Result = "Message sent successfully.";
		} catch (Exception e) {
			Result = "Error in SendMessage API.";
			throw new RuntimeException("sendMessage exception.", e);
		}
		return Result;
	}

	private static String callApi(String urlPre, List<NameValuePair> nameValuePairs, String Token, String EmailDetail,

			String messageDetails) throws IOException {

		String httpOrgQueryTestRtn = ByteDanceApiClientUtil.doPost(urlPre, nameValuePairs, Token, CHARSET_UTF8,
				EmailDetail, messageDetails);
		if (!httpOrgQueryTestRtn.contains("error_response")) {

		}
		;
		return httpOrgQueryTestRtn;

	}

	private static boolean isNotEmpty(String value) {
		int strLen;
		if (value == null || (strLen = value.length()) == 0) {
			return false;
		}
		for (int i = 0; i < strLen; i++) {
			if ((Character.isWhitespace(value.charAt(i)) == false)) {
				return true;
			}
		}
		return false;
	}

}
