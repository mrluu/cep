package cep.handler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Map.Entry;

import cep.model.DDBStreamRecords;
import cep.model.Malware;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.lambda.runtime.Context; 
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.google.gson.Gson;

public class MyHandler {	
	DynamoDBMapper mapper = new DynamoDBMapper(new AmazonDynamoDBClient());
		
	public void handler(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        LambdaLogger logger = context.getLogger();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int letter;        
        while((letter = inputStream.read()) != -1)
        {
        	baos.write(letter);
            //baos.write(Character.toUpperCase(letter));            
        }
        
        //logger.log(new String(baos.toByteArray()));
        
        Malware malware = retrieveMalware("");
        logger.log("Malware: " + malware.getMalwareName());
         
    }
	
	private Malware retrieveMalware(String id) {
		Malware malware = mapper.load(Malware.class, "MW-001");
		return malware;
	}
	
	public void iterateThruResults(Map<?,?> result, LambdaLogger logger) {
		for (Entry<?, ?> entry : result.entrySet()) {
        	Object nextResult = entry.getValue();
			while (nextResult instanceof Map<?,?>) {
				iterateThruResults((Map<?,?>) nextResult, logger);
			}
        	logger.log("key: " + entry.getKey() + ", value: " + nextResult);
        } 
	}
	
	public static DDBStreamRecords deserialize(String input) {
		Gson g = new Gson();
		DDBStreamRecords recs = g.fromJson(input, DDBStreamRecords.class);
        return recs;
	}
}
