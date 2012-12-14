package org.datasift.qatest;
import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;
import java.util.NoSuchElementException;

import org.json.JSONException;

public class Program {
	private Logger _logger = new LogFactory().theLogger();

	private Map<String, String> lineToDict(String line)
    {
		Boolean was_error=false;
		
		org.json.JSONTokener tokener = new org.json.JSONTokener(line);
        Map<String,String> options = new HashMap<String, String>(); 
        try
        {
        	org.json.JSONObject request = (org.json.JSONObject)tokener.nextValue();
			java.util.Iterator it = request.keys();
        	while (it.hasNext())
        	{
        		String key=(String) it.next();
        		String value =(String)request.get(key);
        		options.put(key,value);
        	}
        } catch (JSONException e) {
        	was_error=true;
        } catch (java.lang.ClassCastException e) {
        	was_error=true;
        } 
        if (was_error) {
        	HashMap<String,String> details=new HashMap<String,String>();
        	details.put("details","expecting json object (map/dictionary) of string,string");
        	details.put("input", line);
        	details.put("type", "bad input");
        	
            _logger.logError(details);         
        }
  
        return options;
    }
	 
    private Scanner scanner = new Scanner(System.in);
    private String RawReadLine()
    {
    	String Result;
    	try {
            Result = scanner.nextLine();	
    	} catch (NoSuchElementException e)
    	{
    		Result=null;
    	}
    	return Result;
    }

    private Map<String, String> ReadLine()
    {
        String line = RawReadLine();
        if (line == null )
        {
            return null;
        }
        else
        {
            return lineToDict(line);
        }
    }

    public void start() {
        Boolean is_finished=false;
        while (! is_finished) {
            Map<String, String> options = ReadLine();
            is_finished = (options == null);
            if (!is_finished && options.size() > 0)
            {
                //new Application(options).Run();
            	this._logger.log("here", options);
            }
        }
        this._logger.close();
    }
		
	public static void main(String[] args){
		new Program().start();
	}
}
