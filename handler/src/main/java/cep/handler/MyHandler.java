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
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
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
import com.google.gson.Gson;

public class MyHandler {	
	DynamoDB dynamoDB = new DynamoDB(new AmazonDynamoDBClient());
			
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
        	if (event != null) {
	        	SoftwareMD5Hash md5Hash = event.softwareMD5Hash;
	        	if (md5Hash != null)
	        		testForMalware(md5Hash.value, logger);
        	}
        }      
    }
	
	private void testForMalware(String md5Hash, LambdaLogger logger) {
		Table table = dynamoDB.getTable(Malware.TABLE_NAME);
		Index index = table.getIndex(Malware.INDEX_NAME);

        ItemCollection<QueryOutcome> items = null;
        
        logger.log("Malware Hash: " + md5Hash);
        
        QuerySpec querySpec = new QuerySpec();        
        querySpec.withKeyConditionExpression(Malware.MD5_HASH_ATTR + " = :v_md5Hash")
        .withValueMap(new ValueMap()
            .withString(":v_md5Hash", md5Hash));
        
        items = index.query(querySpec);
        
        Iterator<Item> iterator = items.iterator();
        while (iterator.hasNext()) {
        	Item item = iterator.next();
        	logger.log("Infected with malware: " + item.get(Malware.NAME_ATTR));        	
        }	
	}	
	
	public static DDBStreamRecords deserialize(String input) {
		Gson g = new Gson();
		DDBStreamRecords recs = g.fromJson(input, DDBStreamRecords.class);
        return recs;
	}
}
