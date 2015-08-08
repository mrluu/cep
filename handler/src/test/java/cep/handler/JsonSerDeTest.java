package cep.handler;

import static org.junit.Assert.*;
import junit.framework.Assert;

import org.junit.Test;

import cep.handler.MyHandler;

public class JsonSerDeTest {

	@Test
	public void test() {
		String input = "{ \"RECORDS\": [ { \"EVENTID\": \"2C76BABF1D812D97118943BB6FCFB377\", "
				+ "\"EVENTNAME\": \"INSERT\", \"EVENTVERSION\": \"1.0\", \"EVENTSOURCE\": \"AWS:DYNAMODB\", "
				+ "\"AWSREGION\": \"US-EAST-1\", \"DYNAMODB\": { \"KEYS\": { \"EVENTID\": { \"S\": \"1438481821788\" } }, "
				+ "\"NEWIMAGE\": { \"EVENTTYPE\": { \"S\": \"SOFTWARE INSTALLATION\" }, \"EVENTID\": "
				+ "{ \"S\": \"1438481821788\" }, \"CPE\": { \"S\": \"CPE:/1438481821788\" }, \"INSTALLATIONDATE\": "
				+ "{ \"S\": \"2015-08-01T22:17:01.788Z\" } }, \"SEQUENCENUMBER\": \"500000000000038978652\", "
				+ "\"SIZEBYTES\": 131, \"STREAMVIEWTYPE\": \"NEW_IMAGE\" }, \"EVENTSOURCEARN\": "
				+ "\"ARN:AWS:DYNAMODB:US-EAST-1:694796463941:TABLE/EVENTSTORE/STREAM/2015-08-02T01:17:37.826\" },"
				+ "{ \"EVENTID\": \"1234\", "
				+ "\"EVENTNAME\": \"INSERT\", \"EVENTVERSION\": \"1.0\", \"EVENTSOURCE\": \"AWS:DYNAMODB\", "
				+ "\"AWSREGION\": \"US-EAST-1\", \"DYNAMODB\": { \"KEYS\": { \"EVENTID\": { \"S\": \"1438481821788\" } }, "
				+ "\"NEWIMAGE\": { \"EVENTTYPE\": { \"S\": \"SOFTWARE INSTALLATION\" }, \"EVENTID\": "
				+ "{ \"S\": \"1438481821788\" }, \"CPE\": { \"S\": \"CPE:/microsoft\" }, \"INSTALLATIONDATE\": "
				+ "{ \"S\": \"2015-08-01T22:17:01.788Z\" } }, \"SEQUENCENUMBER\": \"500000000000038978652\", "
				+ "\"SIZEBYTES\": 131, \"STREAMVIEWTYPE\": \"NEW_IMAGE\" }, \"EVENTSOURCEARN\": "
				+ "\"ARN:AWS:DYNAMODB:US-EAST-1:694796463941:TABLE/EVENTSTORE/STREAM/2015-08-02T01:17:37.826\" }"
				+ "] }";
		
		MyHandler.DDBStreamRecords streamRecords = MyHandler.deserialize(input);
		
		Assert.assertEquals(streamRecords.records.size(), 2);
		Assert.assertEquals(streamRecords.records.get(0).dbRecord.event.software.value, "CPE:/1438481821788");		
		
		System.out.println("Deserialization successful");
		
	}

}
