package org.datasift.qatest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import org.datasift.qatest.interfaces.Test;

class Dispatcher {
    private Logger _theLogger = new LogFactory().theLogger();
    private Map<String, String> _options;

    private Map<String, TestDispatchTableValues> dispatchTable =
       new HashMap<String, TestDispatchTableValues>();
  

    //region create
    public Dispatcher(Map<String, String> options){
        this._options=options;
    }
    //endregion

    public java.lang.Iterable<String> Keys() {
        return dispatchTable.keySet();
    }

    public void Add(String key, TestDispatchTableValues value) {
        dispatchTable.put(key, value);
    }

    public void dispatchTest() {
    	if (!dispatchTable.containsKey(this._options.get("test")))
    	{
    		HashMap<String, String> details = new HashMap<String, String>();
    		details.put("type","bad input");
    		details.put("details","invalid test name");
    		details.put("help", "run test list to get a list");
    		_theLogger.logError(details);
    	}
    	else
    	{
    		TestDispatchTableValues  test = dispatchTable.get(this._options.get("test"));
    		assert(test != null);

    		if (!areAllOptionsSet(test))
    		{
    			_theLogger.logError("missing options");
    		}
    		else
    		{
    			runTest(this._options.get("test"), test);
    		}
    	}
    }

    private void runTest(String testName, TestDispatchTableValues test) {
        var numargs=(test.requiredOptions==null)?0:test.requiredOptions.Count;

        _theLogger.log("test-args", _options);
        try {
            var o = (numargs == 0) ? (test.routine as Test0).Test() :
                    (numargs == 1) ? (test.routine as Test1).Test(_options[test.requiredOptions[0]]) :
                    (numargs == 2) ? (test.routine as Test2).Test(_options[test.requiredOptions[0]], _options[test.requiredOptions[1]]) :
                    (numargs == 3) ? (test.routine as Test3).Test(_options[test.requiredOptions[0]], _options[test.requiredOptions[1]], _options[test.requiredOptions[2]]) :
                    (numargs == 4) ? (test.routine as Test4).Test(_options[test.requiredOptions[0]], _options[test.requiredOptions[1]], _options[test.requiredOptions[2]], _options[test.requiredOptions[3]]) :
                    (numargs == 5) ? (test.routine as Test5).Test(_options[test.requiredOptions[0]], _options[test.requiredOptions[1]], _options[test.requiredOptions[2]], _options[test.requiredOptions[3]], _options[test.requiredOptions[4]]) :
                    new Dictionary<String, object>() {{"program-error", "in Dispatcher.runTests" }};
            _theLogger.log("test-result", o);
        }
        catch (datasift.AccessDeniedException) {
            _theLogger.logException("AccessDenied");
        }
        catch (datasift.CompileFailedException) {
            _theLogger.logException("CompileFailed");
        }
        catch (datasift.InvalidDataException) {
            _theLogger.logException("InvalidData");
        }
    }

    private Boolean hasRequiredOptions(TestDispatchTableValues test) {
        return test.requiredOptions != null;
    }

    private Boolean areAllOptionsSet(TestDispatchTableValues test) {
        return !hasRequiredOptions(test) || test.requiredOptions.TrueForAll((i) => (_options.ContainsKey(i)));
    }
}

final class TestDispatchTableValues {

    public TestDispatchTableValues(
        Test routine,
        String[] requiredOptions) {
        this.routine = routine;
        this.requiredOptions = 
        	(requiredOptions == null) ? 
        	null : 
        	new ArrayList<String>(Arrays.asList(requiredOptions));
    }

    public Test routine;
    public List<String> requiredOptions;
}
