package com.example.toptodo;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Date;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("TodoItem")
public class TodoItem extends ParseObject implements Serializable {
	
	public TodoItem() {
	}
	
	public TodoItem 
	( 
		String header, 
		String details, 
		boolean closed, 
		int urgency, 
		Date dueDate, 
		ParseUser user 
	)
	{
		setHeader(header);
		setDetails(details);
		setClosed(closed);
		setUrgency(urgency);
		setDueDate(dueDate);
		setUser(user);
	}
	
	public String getHeader(){
		return getString("header");
	}
	
	public TodoItem setHeader(String header) {
		put("header", header);
		return this;
	}
	
	public String getDetails(){
		return getString("details");
	}
	
	public TodoItem setDetails(String details){
		put("details", details);
		return this;
	}
	
	public boolean getClosed(){
		return getBoolean("closed");
	}
	
	public TodoItem setClosed(boolean closed){
		put("closed", closed);
		return this;
	}
	
	public int getUrgency() {
		return getInt("urgency");
	}
	
	public TodoItem setUrgency( int urgency ) {
		put("urgency", urgency);
		return this;
	}
	
	public Date getDueDate() {
		return getDate("dueDate");
	}
	
	public TodoItem setDueDate( Date dueDate ) {
		put("dueDate", dueDate);
		return this;
	}
	
	public ParseUser getUser() {
		return getParseUser("user");
	}

	public TodoItem setUser(ParseUser currentUser) {
		put("user", currentUser);
		return this;
	}
	
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
		out.writeObject(getString("header"));
		out.writeObject(getString("details"));
		out.writeBoolean(getBoolean("closed"));
		out.writeInt(getInt("urgency"));
		out.writeLong(getDate("dueDate").getTime());
		out.writeObject(getObjectId());
	}
	
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		put("header",(String)in.readObject());
		put("details",(String)in.readObject());
		put("closed",in.readBoolean());
		put("urgency",in.readInt());
		put("dueDate",new Date(in.readLong()));
		setObjectId((String)in.readObject());
	}

	private void readObjectNoData() throws ObjectStreamException {
	}
	
}
