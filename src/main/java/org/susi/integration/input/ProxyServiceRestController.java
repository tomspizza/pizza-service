package org.susi.integration.input;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.susi.integration.CodeobeListener;
import org.susi.integration.CodeobeLog;


@RestController
@RequestMapping("/hrservice-proxy")
//@EnableIntegration
public class ProxyServiceRestController  extends CodeobeListener  {
	

	@Autowired 
	CodeobeLog codeobeLog;
	
	@Value("${output.http_endpoint}")
	String outputEndpoint;
	
	String peid;
	
//	@GetMapping("/hello/{user}")
//	public String healthCheck(@PathVariable String user) {
//		return "Hello " +  user + " " +new Date().toString();
//	}

	
	
	@PostMapping(value = "/order")
	public String order(@RequestBody String request) {
		System.out.println("1. Request received @REST interface request= " + request);
		
		peid = UUID.randomUUID().toString();
		codeobeLog.logMessageBeforeProcess(request, peid); //just send to Q
		
		List<String> reponseList = processAndSend(request, peid);

		String singleResponse = listToString(reponseList); 
		System.out.println("5. Sending reposnse out = " + singleResponse);
		return singleResponse;
	}


	@Override
	public List<String> processAndSend(String msg, String peid) {
		
		//String msg = getPayload(tm);
		System.out.println("2. Start processing request =" + msg);
		List<String> orderArray = convert(msg);
		List<String> processedList = new ArrayList<String>();
		
		if (orderArray != null) {
			for (String order : orderArray) {
				System.out.println("2.1 Processing array element =" + order);
				codeobeLog.logMessageAfterProcess(order, peid); //send to Q
				processedList.add(order);
			}
		} else {
			System.out.println("2.2 Pizza order error=" + msg);
			codeobeLog.logErrorAfterProcess("Invalid Request", peid); //send to q
			processedList.add("Invalid Request");
		}
		
		//Make sure you call send method from process to make it work for resends.replays
		List<String> results  = send(processedList, peid);
		return results;
	}
	
	
	@Override
	public List<String> send(List<String> processedList, String peid)  {
		
		List<String> reponseList = new ArrayList<String>();
		for (String msg  : processedList)  {
			 
			
			System.out.println("3. sendToHttpEndpoint ....." + msg);
			CloseableHttpClient client = HttpClients.createDefault();
		    HttpPost httpPost = new HttpPost(outputEndpoint);
	
		    String tmOut = null;
		    try {
			    StringEntity entity = new StringEntity(msg);
			    httpPost.setEntity(entity);
			    httpPost.setHeader("Accept", "application/json");
			    httpPost.setHeader("Content-type", "application/json");
			  
			    CloseableHttpResponse response = client.execute(httpPost);
			    if (response != null && response.getStatusLine().getStatusCode() == 200) {
			    	tmOut = EntityUtils.toString(response.getEntity());
			    	codeobeLog.logResponse(tmOut, peid);
					System.out.println("3.1 logResponse ....." );
			    } else {
			    	tmOut = "Error from endpoint";
			    	codeobeLog.logResponseError(tmOut, peid);
					System.out.println("3.2 logResponseError ....." );
			    }
		    }  catch (Exception ex) {
		    	tmOut = "Error sening message out";
			    codeobeLog.logResponseError(tmOut, peid);
				System.out.println("3.3 logResponseException ....." );
		    } finally {
		    	try {
					client.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		    }
		    reponseList.add(tmOut);
		}
	    return reponseList;
	}

	
	
	//users convert method
	private static List<String> convert(String reqString) {
		List<String> outArray = new ArrayList<String>();
		try {
			String template = "{\"name\":\"%s\", \"age\":\"%s\"}";
			String[] messages = reqString.split("\n");
			if (messages.length > 0) {
				for (String m : messages) {
					String[] mParts = m.split(",");
					outArray.add(String.format(template, mParts[0], mParts[1]));
				}
			}
		} catch (Exception e) {
			outArray = null;
		}
		return outArray;
	}
	
	//users output aggregation method
	private String listToString(List<String> result) {
		String out = "[ ";
		for (int i=0; i < result.size(); i++) {
			if (i < result.size() - 1) {
				out += result.get(i) + ", ";
			} else {
				out += result.get(i);
			}
		}
		out += " ]";
		return out;
	}
 
 
}
