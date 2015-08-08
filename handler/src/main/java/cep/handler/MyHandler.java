package cep.handler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Map.Entry;

import cep.model.DDBStreamRecords;

import com.amazonaws.services.lambda.runtime.Context; 
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.google.gson.Gson;

public class MyHandler {
		
	public void handler(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        LambdaLogger logger = context.getLogger();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int letter;        
        while((letter = inputStream.read()) != -1)
        {
        	baos.write(letter);
            //baos.write(Character.toUpperCase(letter));            
        }
        
        logger.log(new String(baos.toByteArray()));
        
        /*Gson g = new Gson();
        Records recs = g.fromJson(new String(baos.toByteArray()), Records.class);
        logger.log(recs.RECORDS.get(0).EVENTSOURCEARN);*/
         
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
