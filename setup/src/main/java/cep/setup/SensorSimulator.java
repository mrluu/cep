package cep.setup;

import java.text.SimpleDateFormat;

import cep.model.Event;
import cep.model.Malware;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

public class SensorSimulator {
	static SimpleDateFormat dateFormatter = new SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			
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
	
	
	public static void populateMalwareCatalog(AmazonDynamoDBClient client) {
		DynamoDBMapper mapper = new DynamoDBMapper(client);
		
		Malware malware1 = new Malware();
		malware1.setMalwareID("MW-001");
		malware1.setMalwareName("Ratatouille");
		malware1.setDescription("Very bad remote access toolkit");
		malware1.setMD5Hash("001dd76872d80801692ff942308c64e6");
		mapper.save(malware1);
		
		Malware malware2 = new Malware();
		malware2.setMalwareID("MW-002");
		malware2.setMalwareName("Gummy worm");
		malware2.setDescription("Very bad computer worm");
		malware2.setMD5Hash("011dd96872e80804692ff942308c64e6");
		mapper.save(malware2);
		
		Malware malware3 = new Malware();
		malware3.setMalwareID("MW-003");
		malware3.setMalwareName("The Cold");
		malware3.setDescription("Very bad computer virus");
		malware3.setMD5Hash("012ee96872e80804692fd842308c64e6");
		mapper.save(malware3);
	}
}
