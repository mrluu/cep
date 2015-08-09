package cep.setup;

import java.text.SimpleDateFormat;

import cep.model.Event;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;

public class SensorSimulator {
	static SimpleDateFormat dateFormatter = new SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	
	public static void generateEvent(DynamoDB dynamoDB) {
		Table table = dynamoDB.getTable(Event.TABLE_NAME);
		
		try {
			Item item = new Item()
            .withPrimaryKey("EventId", new Long(System.currentTimeMillis()).toString())
            .withString("EventType", "Software Installation")
            //device id            
            .withString("cpe", "cpe:/" + new Long(System.currentTimeMillis()).toString())
            //binary hash
            .withString("InstallationDate", dateFormatter.format(System.currentTimeMillis()));
        
			table.putItem(item);
		}
		catch (Exception ex) {
			System.err.println("Generate event failed.");
			System.err.println(ex.getMessage());
		}		
	}
	
	public static void generateEvent(AmazonDynamoDBClient client) {
		DynamoDBMapper mapper = new DynamoDBMapper(client);
		
		String now = new Long(System.currentTimeMillis()).toString();
		
		Event event = new Event();
		event.setEventID(now);
		event.setEventType(Event.SW_INSTALL);
		event.setSoftwareCPE("cpe:/" + now);
		event.setSoftwareName("software name " + now);
		event.setTimestamp(dateFormatter.format(System.currentTimeMillis()));
		
		mapper.save(event);
		System.out.println("Done generating event: " + now);
	}
}
