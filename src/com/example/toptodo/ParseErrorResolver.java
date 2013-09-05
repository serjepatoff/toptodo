package com.example.toptodo;

import java.util.HashMap;

import android.content.Context;

import com.parse.ParseException;

public class ParseErrorResolver {
	
	
	private static final HashMap<Integer,Integer> codeMap;
	static {
		codeMap = new HashMap<Integer, Integer>();
		codeMap.put(ParseException.USERNAME_TAKEN, R.string.username_taken);
		codeMap.put(ParseException.USERNAME_MISSING, R.string.username_missing);
		codeMap.put(ParseException.PASSWORD_MISSING, R.string.password_missing);
		codeMap.put(ParseException.OBJECT_NOT_FOUND, R.string.object_not_found);
		codeMap.put(ParseException.CONNECTION_FAILED, R.string.connection_failed);
		codeMap.put(ParseException.TIMEOUT, R.string.connection_timeout);
	}
	
	public String resolve( Context c, ParseException e ) {
		if ( codeMap.containsKey(e.getCode()) ) {
			return c.getString(codeMap.get(e.getCode()));
		}
		else {
			return c.getString(R.string.connection_unknown_error);
		}
	}

}
