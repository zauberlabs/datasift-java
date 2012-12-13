package org.datasift.qatest;
import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;
import java.util.NoSuchElementException;

public class Program {
	private Logger _logger = new LogFactory().theLogger();

	/*   private Map<String, String> lineToDict(String line)
    {
        Map<String,String> options = new HashMap<String, String>(); ;
        try
        {
            options = Newtonsoft.Json.JsonConvert.DeserializeObject<Dictionary<string, string>>(line);
            options = (options == null)?new Dictionary<string,string>():options;
        }
        catch (Newtonsoft.Json.JsonReaderException)
        {
            HashMap<String,String> errorMessage=new HashMap<String,String>();
            errorMessage.put("bad input", line);
            _logger.logError(errorMessage);         
        }

        return options;
    }*/
	
	private Map<String, String> lineToDict(String line)
    {
		return new HashMap<String,String>();
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
            	this._logger.log("here");
            }
        }
        this._logger.close();
    }
		
	public static void main(String[] args){
		new Program().start();
	}
}
