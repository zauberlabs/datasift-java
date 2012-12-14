package org.datasift.qatest;

import java.io.IOException;
import java.util.HashMap;
import org.json.JSONException;

final class Logger
{
    private java.io.Writer stream= new java.io.OutputStreamWriter(System.out);
    private org.json.JSONWriter log = new org.json.JSONWriter(stream);

    public Logger()
    {
        try {
			log.array();
			stream.flush();
		} catch (org.json.JSONException e) {
			e.printStackTrace();
		} catch (java.io.IOException e) {
			e.printStackTrace();
		} 
    }

    public void log(Object o)
    {
        try {
			log.value(o);
			stream.flush();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    public void log(String s, Object o)
    {
    	HashMap<String, Object> msg = new HashMap<String, Object>();
    	msg.put(s,o);
        log(msg);
    }

    public void logError(Object o)
    {
        log("error", o);
    }

    public void logException(Object o)
    {
        log("exception", o);
    }
    
    public void logProgramError(Object o)
    {
        log("program error", o);
    }

    public void logEvent(String s, Object o)
    {
        log("event:" + s, o);
    }

    public void close()
    {
    	try {
			log.endArray();
			stream.flush(); 
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
