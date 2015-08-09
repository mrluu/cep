package cep.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.google.gson.annotations.SerializedName;

@DynamoDBTable(tableName=Event.TABLE_NAME)
public class Event {	
	public static final String TABLE_NAME = "EventStore";
	public static final String KEY_ATTR = "EventID";
	public static final String EVENT_TYPE_ATTR = "EventType";
	public static final String SW_NAME_ATTR = "SoftwareName";
	public static final String SW_CPE_ATTR = "SoftwareCPE";	
	public static final String SW_MD5_HASH = "SoftwareMD5Hash";
	public static final String TIMESTAMP_ATTR = "Timestamp";
	
	public static final String SW_INSTALL = "SW INSTALL";
	public static final String SW_UNINSTALL = "SW UNINSTALL";
	
	@SerializedName(KEY_ATTR)
	public EventID eventID = new EventID();
	public static class EventID {
		@SerializedName("S")
		public String value;
	}
	@DynamoDBHashKey(attributeName=KEY_ATTR)  
    public String getEventID() { 		
		return eventID.value;		
	}
    public void setEventID(String id) {    	
    	this.eventID.value = id;
    }
	
	@SerializedName(EVENT_TYPE_ATTR)
	public EventType eventType = new EventType();
	public static class EventType {
		@SerializedName("S")
		public String value;
	}
	@DynamoDBAttribute(attributeName=EVENT_TYPE_ATTR)  
    public String getEventType() {		
		return eventType.value;		
	}
    public void setEventType(String type) {    	
    	this.eventType.value = type;
    }	
	
	@SerializedName(SW_NAME_ATTR)
	public SoftwareName softwareName = new SoftwareName();
	public static class SoftwareName {
		@SerializedName("S")
		public String value;
	}
	@DynamoDBAttribute(attributeName=SW_NAME_ATTR)
	public String getSoftwareName() {		
		return this.softwareName.value;		
	}
	public void setSoftwareName(String name) {		
		this.softwareName.value = name;
	}
	
	@SerializedName(SW_CPE_ATTR)
	public SoftwareCPE softwareCPE = new SoftwareCPE();
	public static class SoftwareCPE {
		@SerializedName("S")
		public String value;
	}
	@DynamoDBAttribute(attributeName=SW_CPE_ATTR)
	public String getSoftwareCPE() {		
		return this.softwareCPE.value;		
	}
	public void setSoftwareCPE(String cpe) {		
		this.softwareCPE.value = cpe;
	}
	
	@SerializedName(SW_MD5_HASH)
	public SoftwareMD5Hash softwareMD5Hash = new SoftwareMD5Hash();
	public static class SoftwareMD5Hash {
		@SerializedName("S")
		public String value;
	}
	@DynamoDBAttribute(attributeName=SW_CPE_ATTR)
	public String getSoftwareMD5Hash() {		
		return this.softwareMD5Hash.value;		
	}
	public void setSoftwareMD5Hash(String hash) {		
		this.softwareMD5Hash.value = hash;
	}
	
	@SerializedName(TIMESTAMP_ATTR)
	public Timestamp timestamp = new Timestamp();		
	public static class Timestamp {
		@SerializedName("S")
		public String value;
	}
	@DynamoDBAttribute(attributeName=TIMESTAMP_ATTR)
	public String getTimestamp() {		
		return this.timestamp.value;		
	}
	public void setTimestamp(String ts) {		
		this.timestamp.value = ts;
	}
}
