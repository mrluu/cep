package cep.setup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import cep.model.Event;
import cep.model.Malware;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

public class SensorSimulator {
	static SimpleDateFormat dateFormatter = new SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	
	static String[] badHashes = 
		{"001dd76872d80801692ff942308c64e6", 
		"011dd96872e80804692ff942308c64e6", 
		"012ee96872e80804692fd842308c64e6"};
	
	public static String generateRandomString(int length) {
		StringBuffer buffer = new StringBuffer();
		String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";	
		
		int charactersLength = characters.length();

		for (int i = 0; i < length; i++) {
			double index = Math.random() * charactersLength;
			buffer.append(characters.charAt((int) index));
		}
		return buffer.toString();
	}
			
	public static void generateEvent(AmazonDynamoDBClient client) throws IOException, InterruptedException {
		DynamoDBMapper mapper = new DynamoDBMapper(client);				
		String now = dateFormatter.format(new Date(System.currentTimeMillis()));
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));		
		do {
			Event event = new Event();
			event.setEventID(generateRandomString(20));
			event.setEventType(Event.SW_INSTALL);
			event.setTimestamp(now);		
			String vendor = generateRandomString(10);
			String product = generateRandomString(12);
			event.setSoftwareCPE("cpe:/a:" + vendor + ":" + product);
			event.setSoftwareName(vendor.toUpperCase() + " " + product.toUpperCase());
			event.setDeviceID(generateRandomString(5).toLowerCase() + ".tieuluu.com");
			
			int random = (int) Math.floor(Math.random()*20);		
			if (random < badHashes.length) {
				event.setSoftwareMD5Hash(badHashes[random]);
				mapper.save(event);	
			}
			else {
				event.setSoftwareMD5Hash("notmalwarehash");
				mapper.save(event);	
			}	
			
			long sleepTime = (long)(Math.random()*7000);			
			Thread.sleep(sleepTime);
		} while ((!br.ready() || !((br.readLine()).equalsIgnoreCase("stop"))));		
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
