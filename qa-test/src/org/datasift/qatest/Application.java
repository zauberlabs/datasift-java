package org.datasift.qatest;
import java.util.Map;

final public class Application {
	private Logger _logger = new LogFactory().theLogger();
	private Map<String, String> _options;
	
	public Application(Map<String, String> options) {
	    this._options = options;
	    //dispatcher = new Dispatcher(options);
	    //setupDispatch();
	}
	
	public void Run(){
		this._logger.log("here", this._options);	
	}
	
	
}
