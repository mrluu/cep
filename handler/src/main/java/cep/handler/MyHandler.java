package cep.handler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import cep.model.DDBStreamRecords;
import cep.model.Event;
import cep.model.Event.SoftwareMD5Hash;
import cep.model.Malware;
import cep.model.Record;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Index;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.lambda.runtime.Context; 
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.PublishRequest;
import com.google.gson.Gson;

public class MyHandler {	
	DynamoDB dynamoDB = new DynamoDB(new AmazonDynamoDBClient());
	
	AmazonSNSClient snsClient = new AmazonSNSClient();	
			
	public void handler(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {   
		LambdaLogger logger = context.getLogger();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int letter;        
        while((letter = inputStream.read()) != -1)
        {
        	baos.write(letter);                     
        }
                
        List<Record> records = deserialize(new String(baos.toByteArray())).records;
        for (Record record : records) {
        	Event event = record.dbRecord.event;	        	
	        testForMalware(event, logger);        	
        }      
    }
	
	private void testForMalware(Event event, LambdaLogger logger) {
		Table table = dynamoDB.getTable(Malware.TABLE_NAME);
		
		//Get the topic ARN that we put into the table when the SNS topic was set up
		Item topicItem = table.getItem(Malware.KEY_ATTR, Malware.MALWARE_NOTIFY_TOPIC);
		String topicARN = (String) topicItem.get(Malware.NAME_ATTR);
				
		Index index = table.getIndex(Malware.INDEX_NAME);

        ItemCollection<QueryOutcome> items = null;
                      
        QuerySpec querySpec = new QuerySpec();        
        querySpec.withKeyConditionExpression(Malware.MD5_HASH_ATTR + " = :v_md5Hash")
        .withValueMap(new ValueMap()
            .withString(":v_md5Hash", event.softwareMD5Hash.value));
        
        items = index.query(querySpec);
        
        Iterator<Item> iterator = items.iterator();
        while (iterator.hasNext()) {
        	Item item = iterator.next();
        	String msg = "=========>>>>>>> " + event.deviceID.value + " infected with malware: " + item.get(Malware.NAME_ATTR);
        	
        	writeToDB(item.get(Malware.KEY_ATTR).toString(), event.deviceID.value, event.timestamp.value);        	
        	        	
        	PublishRequest publishRequest = new PublishRequest(topicARN, msg);
        	snsClient.publish(publishRequest);
        }	
	}	
	
	private void writeToDB(String malwareID, String deviceID, String date) {
		Table table = dynamoDB.getTable("MalwareDevice");
		Item item = new Item()
				.withPrimaryKey("MalwareID", malwareID, "DeviceID", deviceID)
				.withString("DateInfected", date);
		table.putItem(item);
	}
	
	public static DDBStreamRecords deserialize(String input) {
		Gson g = new Gson();
		DDBStreamRecords recs = g.fromJson(input, DDBStreamRecords.class);
        return recs;
	}
}
