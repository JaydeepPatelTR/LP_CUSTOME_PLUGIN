
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

public class ByteDanceApiClientUtil {

	@SuppressWarnings("resource")
	public static String doPost(String url, List<NameValuePair> nameValuePairs, String Token, String charset,
			String EmailDetails, String messageDetails) {

		HttpPost httpPost = null;
		String result = null;
		try {

			HttpClient httpclient = new SSLClient();
			httpPost = new HttpPost(url);
			if (Token != "") {
				httpPost.setHeader("Authorization", "Bearer " + Token);
				if (EmailDetails != "") {
                     // For Getting Open ID
					httpPost.setEntity(new ByteArrayEntity(EmailDetails.getBytes()));
				} else {
					// For Send Message
					httpPost.setEntity(new StringEntity(messageDetails));
				}
			} else {
				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			}
			HttpResponse response = httpclient.execute(httpPost);

			if (response != null) {
				HttpEntity resEntity = response.getEntity();
				if (resEntity != null) {
					result = EntityUtils.toString(resEntity, charset);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}
}
