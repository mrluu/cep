package cep.setup;

import java.util.ArrayList;

import cep.model.Event;
import cep.model.Malware;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBStreamsClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.GlobalSecondaryIndex;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.Projection;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.StreamSpecification;
import com.amazonaws.services.dynamodbv2.model.StreamViewType;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sns.model.DeleteTopicRequest;

public class CEPDriver {	
	static AmazonDynamoDBClient dynamoDBClient = 
	        new AmazonDynamoDBClient(new ProfileCredentialsProvider());
	
	static DynamoDB dynamoDB = new DynamoDB(dynamoDBClient);
	
	static AmazonDynamoDBStreamsClient streamsClient = 
	        new AmazonDynamoDBStreamsClient(new ProfileCredentialsProvider());
	
	static AmazonSNSClient snsClient = new AmazonSNSClient(new ProfileCredentialsProvider());	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			if ((args.length==0) || (args.length>1)) {
				System.out.println("Please supply one argument");
			}
			else if (args[0].equalsIgnoreCase("init")) {
				createTable(Event.TABLE_NAME, 10L, 10L, Event.KEY_ATTR, "S", null, null);
				createTable(Malware.TABLE_NAME, 10L, 10L, Malware.KEY_ATTR, "S", null, null);
				createTable("MalwareDevice", 10L, 10L, "MalwareID", "S", "DeviceID", "S");
				SensorSimulator.populateMalwareCatalog(dynamoDBClient);
				createSNSTopic();
			}
			else if (args[0].equalsIgnoreCase("generate")) {
				SensorSimulator.generateEvent(dynamoDBClient);
			}
			else if (args[0].equalsIgnoreCase("cleanup")) {
				deleteSNSTopic();
				deleteTable(Event.TABLE_NAME);
				deleteTable(Malware.TABLE_NAME);
				deleteTable("MalwareDevice");
			}
			else {
				System.out.println("Please supply a valid argument");
			}
		}
		catch (Exception ex) {
			System.err.print(ex);
		}
	}

    private static void createTable(
    	String tableName, long readCapacityUnits, long writeCapacityUnits, 
        String hashKeyName, String hashKeyType, 
        String rangeKeyName, String rangeKeyType) {

        try {

            ArrayList<KeySchemaElement> keySchema = new ArrayList<KeySchemaElement>();
            keySchema.add(new KeySchemaElement()
                .withAttributeName(hashKeyName)
                .withKeyType(KeyType.HASH));
            
            ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<AttributeDefinition>();
            attributeDefinitions.add(new AttributeDefinition()
                .withAttributeName(hashKeyName)
                .withAttributeType(hashKeyType));

            if (rangeKeyName != null) {
                keySchema.add(new KeySchemaElement()
                    .withAttributeName(rangeKeyName)
                    .withKeyType(KeyType.RANGE));
                attributeDefinitions.add(new AttributeDefinition()
                    .withAttributeName(rangeKeyName)
                    .withAttributeType(rangeKeyType));
            }
            
            CreateTableRequest request = null;
            Table table;
            
            
            if (tableName.equals(Malware.TABLE_NAME)) {
            	attributeDefinitions.add(new AttributeDefinition()
            		.withAttributeName(Malware.MD5_HASH_ATTR)
            		.withAttributeType("S"));
            	
            	GlobalSecondaryIndex md5HashIndex = new GlobalSecondaryIndex()
            		.withIndexName(Malware.INDEX_NAME)
            		.withProvisionedThroughput(new ProvisionedThroughput()
                    .withReadCapacityUnits(readCapacityUnits)
                    .withWriteCapacityUnits(writeCapacityUnits))
            		.withKeySchema( new KeySchemaElement()
            		.withAttributeName(Malware.MD5_HASH_ATTR)
            		.withKeyType(KeyType.HASH))
            		.withProjection(new Projection()
            		.withProjectionType("ALL"));
            	
            	request = new CreateTableRequest()
                	.withTableName(tableName)
                	.withKeySchema(keySchema)
                	.withProvisionedThroughput( new ProvisionedThroughput()
                	.withReadCapacityUnits(readCapacityUnits)
                	.withWriteCapacityUnits(writeCapacityUnits))
                	.withGlobalSecondaryIndexes(md5HashIndex);
            }
            else if (tableName.equals(Event.TABLE_NAME)) {
	            StreamSpecification streamSpecification = new StreamSpecification();
	            streamSpecification.setStreamEnabled(true);
	            streamSpecification.setStreamViewType(StreamViewType.NEW_IMAGE);
	
	            request = new CreateTableRequest()
                    .withTableName(tableName)
                    .withKeySchema(keySchema)
                    .withProvisionedThroughput( new ProvisionedThroughput()
                    .withReadCapacityUnits(readCapacityUnits)
                    .withWriteCapacityUnits(writeCapacityUnits))
                    .withStreamSpecification(streamSpecification);
            }
            else {
            	request = new CreateTableRequest()
                    .withTableName(tableName)
                    .withKeySchema(keySchema)
                    .withProvisionedThroughput( new ProvisionedThroughput()
                    .withReadCapacityUnits(readCapacityUnits)
                    .withWriteCapacityUnits(writeCapacityUnits));
            }
            
            request.setAttributeDefinitions(attributeDefinitions); 
            table = dynamoDB.createTable(request);           
            table.waitForActive();

        } catch (Exception e) {
            System.err.println("CreateTable request failed for " + tableName);
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }	
    
    private static void createSNSTopic() {
    	CreateTopicRequest createTopicRequest = new CreateTopicRequest(Malware.MALWARE_NOTIFY_TOPIC);
    	CreateTopicResult createTopicResult = snsClient.createTopic(createTopicRequest);
    	
    	//Save the topic ARN to the database so that our handler can get it later
    	String topicARN = createTopicResult.getTopicArn();
    	DynamoDBMapper mapper = new DynamoDBMapper(dynamoDBClient);	
    	Malware malwareTopic = new Malware();
    	malwareTopic.setMalwareID(Malware.MALWARE_NOTIFY_TOPIC);
    	malwareTopic.setMalwareName(topicARN);
    	malwareTopic.setDescription(topicARN);
    	malwareTopic.setMD5Hash("");
    	mapper.save(malwareTopic);
    			
    }
    
    private static void deleteSNSTopic() {
    	DynamoDBMapper mapper = new DynamoDBMapper(dynamoDBClient);	
    	Malware malwareTopic = mapper.load(Malware.class, Malware.MALWARE_NOTIFY_TOPIC);
    	String topicARN = malwareTopic.getMalwareName();
    	DeleteTopicRequest deleteTopicRequest = new DeleteTopicRequest(topicARN);
    	snsClient.deleteTopic(deleteTopicRequest);
    }
    
    private static void deleteTable(String tableName) {
        Table table = dynamoDB.getTable(tableName);
        try {            
            table.delete();            
            table.waitForDelete();

        } catch (Exception e) {
            System.err.println("DeleteTable request failed for " + tableName);
            System.err.println(e.getMessage());
        }
    }   
}
