package cep.model;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class DDBStreamRecords {	
	@SerializedName("Records")
	public List<Record> records;
}
