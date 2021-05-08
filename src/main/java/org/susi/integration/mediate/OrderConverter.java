package org.susi.integration.mediate;

import java.util.ArrayList;
import java.util.List;

public class OrderConverter {
	
	public String convert(Object object) {
		
		List<String> orderArray = new ArrayList<String>();

		try {
			String template = "<order>\n\t<name>%s</name>\n\t<count>%s</count>\n</order>";
			String[] orders = object.split("\n");
			if (orders.length > 0) {
				for (String order : orders) {
					String[] pizzaAndCount = order.split(",");
					String name = pizzaAndCount[0];
					String count = pizzaAndCount[1];
					orderArray.add(String.format(template, name, count));
				}
			}
		} catch (Exception e) {
			orderArray = null;
		}
		
		return orderArray;
	}

}
