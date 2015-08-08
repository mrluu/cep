package cep.handler;

import static org.junit.Assert.*;
import junit.framework.Assert;

import org.junit.Test;

import cep.handler.MyHandler;
import cep.model.DDBStreamRecords;

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
		
		String data = 
			"{ \"Records\": [ { \"eventID\": \"cd221197f586c5b819fd6b71d83aec8d\","
			+ " \"eventName\": \"INSERT\", \"eventVersion\": \"1.0\", \"eventSource\":"
			+ " \"aws:dynamodb\", \"awsRegion\": \"us-east-1\", \"dynamodb\": { \"Keys\":"
			+ " { \"EventID\": { \"S\": \"1439044332864\" } }, \"NewImage\": { \"EventType\":"
			+ " { \"S\": \"SW INSTALL\" }, \"EventID\": { \"S\": \"1439044332864\" },"
			+ " \"SoftwareCPE\": { \"S\": \"cpe:/1439044332864\" }, \"Timestamp\": { \"S\":"
			+ " \"2015-08-08T10:32:12.865Z\" }, \"SoftwareName\": { \"S\":"
			+ " \"software name 1439044332864\" } }, \"SequenceNumber\": \"400000000000017132579\","
			+ " \"SizeBytes\": 160, \"StreamViewType\": \"NEW_IMAGE\" }, \"eventSourceARN\":"
			+ " \"arn:aws:dynamodb:us-east-1:694796463941:table/EventStore/stream/2015-08-08T14:06:12.675\" } ] }";
		
		DDBStreamRecords streamRecords = MyHandler.deserialize(data);
		
		//Assert.assertEquals(streamRecords.records.size(), 2);
		Assert.assertEquals(streamRecords.records.get(0).dbRecord.event.softwareCPE.value, "cpe:/1439044332864");		
		
		System.out.println("Deserialization successful");
		
	}

}
