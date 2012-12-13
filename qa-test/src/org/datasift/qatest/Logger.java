package org.datasift.qatest;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONStringer;


final class Logger
{
    private Boolean is_first;

    public Logger()
    {
        Write("[");
        is_first = true;
    }

    private void Write(String message)
    {
        System.out.print(message);
    }

    public void log(Object o)
    {
        try {
			Write(
			    (is_first ? "" : ",") +
			    //Newtonsoft.Json.JsonConvert.SerializeObject(o);
			    new JSONStringer().value(o)
			);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			// this will do, I can't log it.
			e.printStackTrace();
		}
        is_first = false;
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

    public void logEvent(String s, Object o)
    {
        log("event:" + s, o);
    }

    public void close()
    {
        Write("]");
    }
}
