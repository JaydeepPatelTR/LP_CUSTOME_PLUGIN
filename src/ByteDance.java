import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

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
	
	public static String ACTION_URL ="https://bytedance.com";
	public static String BUTTON_URL ="https://us.legaltracker.thomsonreuters.com/";
	public static String BUTTON_TEXT ="Legal Tracker";
	
	public static void main(String[] args) throws Exception {
		try {
			String filePath = "C:\\home\\Pending_invoice_list_for_approval.xlsx";

			File file = new File(filePath);
			InputStream inputfStream = getFileFromSFTP("gcc-uat.hostedtax.thomsonreuters.com","ofv-uat.gcc-uat","m1j3hJQJmHuNagm","/input/Pending_invoice_list_for_approval.xlsx");

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
			String apiResponse = invokeApi(paramsValue,inputfStream);// simulation();
			System.out.println(apiResponse);

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public static InputStream getFileFromSFTP(String sftpHost, String sftpUsername, String sftpPassword, String sftpFileLocation) throws JSchException, SftpException {
	    JSch jsch = new JSch();
	    Session jschSession = jsch.getSession(sftpUsername, sftpHost, 2222);
	    jschSession.setPassword(sftpPassword);
	    jschSession.setConfig("StrictHostKeyChecking", "No");
	    jschSession.connect();
	    ChannelSftp sftp = (ChannelSftp) jschSession.openChannel("sftp");
	    sftp.connect();
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    sftp.get(sftpFileLocation, baos);
	    sftp.exit();
	    return new ByteArrayInputStream(baos.toByteArray());
	}
	
	public static String invokeApi(Map<String, String> params,InputStream inputfStream) throws IOException {

		String Result = "";
		try {
			SimpleDateFormat dtformatter = new SimpleDateFormat("dd MMMM yyyy");  
			System.out.println("Started Reading Excel Details.");
			XSSFWorkbook excelWorkbook = new XSSFWorkbook(inputfStream);
			
			XSSFSheet excelSheet = excelWorkbook.getSheetAt(0);
			
			int rows = excelSheet.getPhysicalNumberOfRows();// 3
			
			int cols = excelSheet.getRow(0).getPhysicalNumberOfCells();// 2
			
			String[] invoiceContent = new String[rows-1];
			XSSFCell cell;
			DataFormatter formatter = new DataFormatter(); // creating formatter using the default locale

			for (int i = 1; i < rows; i++) {
				StringBuilder contentData=new StringBuilder();  
				
				for (int j = 0; j < cols; j++) {
					String cellTitle = "";
					String cellContents = "";
					
					cell = excelSheet.getRow(i).getCell(j);	
					// For cell title
					if (j == 0) {
						cellTitle="Vendor Name: ";
					}
					else if(j == 1) {
						  cellTitle="Invoice Date: ";
					}
					else if(j == 2) {
						  cellTitle="Invoice Number: ";
					}
					else if(j == 3) {
						cellTitle="Matter Name: ";
					}
					else if(j == 4) {
						cellTitle="Invoice Currency: ";
					}
					else	if(j == 5) {
						cellTitle="Invoice Amount: ";
					}
					// For check cell value null or not
					if(cell != null) {
																				
					if (cell.getCellType() == CellType.STRING) {						
						cellContents = cell.getStringCellValue();
					} else if (cell.getCellType() == CellType.NUMERIC) {
						if (j == 1) {	
						  cellContents= dtformatter.format(cell.getDateCellValue());  
							
						}  else if(j == 2) {						
							  cellContents = String.valueOf((int) cell.getNumericCellValue());
						}
						else {								
							  cellContents = String.valueOf(cell.getNumericCellValue());
						}
					 }
				  }
					
					contentData.append(cellTitle + cellContents);	
					if(j == 5) {
						contentData.append("\r\n\r\n" + params.get("message_disclaimer")  + "\r\n\r\n" + params.get("message_disclaimer_2") +"\r\n");				
					}				
					else
					{
						contentData.append("\r\n");	
					}
				}
				invoiceContent[i-1] = contentData.toString();
			}
			System.out.println("Completed Reading Excel Details.");
			excelWorkbook.close();
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
				 Result = getUserID(params.get("api_url_userid"), params.get("user_email"),
				 APP_ACCESS_TOKEN,params,invoiceContent);
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

	private static String getUserID(String urlPre, String Email, String Token, Map<String, String> params,
			String[] invoiceContent) throws IOException {

		String Result = "";
		try {
			String UserID = null;
			EmailBean emailBean = new EmailBean();
			emailBean.setEmails(Arrays.asList(Email));

			ObjectMapper emailMapper = new ObjectMapper();

			response = callApi(urlPre, null, Token, emailMapper.writeValueAsString(emailBean), null);

			ObjectMapper mapper = new ObjectMapper();
			JsonNode node = mapper.readTree(response);
			if (node.get("data").get("user_list") != null) {
				UserID = node.get("data").get("user_list").get(0).get("user_id").asText();
			}

			if (UserID != null) {
				System.out.println("Get UserID Response:" + response);
				for (String invoiceMessage : invoiceContent) {
					Result = sendMessage(params.get("api_url_message"), UserID, Token, params,invoiceMessage);
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
         
			//Result = callApi(urlPre,
			// null,APP_ACCESS_TOKEN,"",messageMapper.writeValueAsString(messageBean));
			System.out.println(invoiceMessage);
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
