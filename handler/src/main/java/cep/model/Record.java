package cep.model;

import com.google.gson.annotations.SerializedName;

public class Record {	
	@SerializedName("dynamodb")
	public DBRecord dbRecord;			
}
