package org.susi.integration.input;


import java.io.IOException;
import java.util.List;
import javax.jms.TextMessage;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.susi.integration.CodeobeListener;
import org.susi.integration.CodeobeLog;
import org.susi.integration.mediate.OrderConverter;


@RestController
@RequestMapping("/pizzaproxy")
@EnableIntegration
public class PizzaProxyRestController  extends CodeobeListener  {
	

	@Autowired 
	CodeobeLog codeobeLog;
	
	@Value("${output.http_endpoint}")
	String outputEndpoint;
	

	@PostMapping(value = "/order")
	public String order(@RequestBody String request) {
		System.out.println("1111 Pizza order received @REST interface order= " + request);
		TextMessage tm  = codeobeLog.logMessageBeforeProcess(request);
		tm = process(tm);
		String result = getPayload(tm);
		return result;
	}


	@Override
	public TextMessage process(TextMessage tm) {
		
		String msg = getPayload(tm);
		System.out.println("22222.111 Pizza order received at order processor Jms interface payload=" + msg);
		List<String> orderArray = OrderConverter.convert(msg);
		
		TextMessage tm2 =  null;
		if (orderArray != null) {
			for (String order : orderArray) {
				System.out.println("22222.222 Pizza order sending to next JMS=" + order);
				tm2 = codeobeLog.logMessageAfterProcess(tm, order);
				send(tm2);
			}
		} else {
			System.out.println("22222.333 Pizza order error=" + msg);
			tm2 = codeobeLog.logErrorAfterProcess(tm, "Invalid Order Request");
		}
		return tm2;
	}
	
	
	@Override
	public void send(TextMessage tm)  {
		
		String msg = getPayload(tm);
		System.out.println("sendToHttpEndpoint ....." + msg);
		CloseableHttpClient client = HttpClients.createDefault();
	    HttpPost httpPost = new HttpPost(outputEndpoint);

	    try {
		    StringEntity entity = new StringEntity(tm.getText());
		    httpPost.setEntity(entity);
		    //httpPost.setHeader("Accept", "application/json");
		    //httpPost.setHeader("Content-type", "application/json");
	
		    CloseableHttpResponse response = client.execute(httpPost);
		    if (response.getStatusLine().getStatusCode() == 200) {
		    	codeobeLog.logResponse(tm);
		    } else {
		    	codeobeLog.logResponseError(tm);
		    }
	    }  catch (Exception ex) {
	    	
	    } finally {
	    	try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
	}

	




	


 
}
