package org.datasift.qatest;
import  org.datasift.qatest.interfaces.*;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

final class Application {
    //region Constructors
    public Application(Map<String, String> options) {
        this._options = options;
        dispatcher = new Dispatcher(options);
        setupDispatch();
    }
    //endregion

    //region Private
    private Map<String, String> _options;
    private Logger _theLogger = new LogFactory().theLogger();
    private Dispatcher dispatcher;

    class ListTests implements Test0 {
        private Dispatcher dispatcher;
        public ListTests(Dispatcher dispatcher) {
            this.dispatcher = dispatcher;
        }
        public Map<String, Object> Test() {
        	HashMap<String, Object> Result = new HashMap<String,Object>();
        	Result.put("Test Names", dispatcher.Keys());
            return Result;
        }
    }
  
    //endregion

    //region Entry
    public void Run() {
    	this._theLogger.log("here", this._options);
        dispatcher.dispatchTest();
    }
    //endregion

    //region Config
    private String[] compileOptions = new String[]{
        "csdl",
        "user-name",
        "user-key"
    };

    private void setupDispatch() {
        dispatcher.Add("list", 
            new TestDispatchTableValues(
            		new ListTests(dispatcher), 
            		null));
    /*
        dispatcher.Add("validate", 
            new TestDispatchTableValues(
                new TestValidate(),
                compileOptions ));
        dispatcher.Add("compile",
            new TestDispatchTableValues(
                new TestCompile(),
                compileOptions));
                */
        dispatcher.Add("dpuFromCsdl",
         new TestDispatchTableValues(
             new TestDpuFromCsdl(),
             compileOptions));
        
        dispatcher.Add("dpuFromHash",
           new TestDispatchTableValues(
               new TestDpuFromHash(),
               new String[]{
                    "hash",
                    "user-name",
                    "user-key"
                }));
        /*
        dispatcher.Add("usage",
            new TestDispatchTableValues(
                new TestUsage(),
                new String[]{
                    "period",
                    "user-name",
                    "user-key"
                }));
        dispatcher.Add("stream-csdl",
           new TestDispatchTableValues(
               new TestStreamCsdl(),
               new String[]{
                    "connection-type",
                    "csdl",
                    "user-name",
                    "user-key"
                }));
        dispatcher.Add("stream-x",
           new TestDispatchTableValues(
               new TestStreamX(),
               new String[]{
                    "connection-type",
                    "csdl",
                    "hash",
                    "user-name",
                    "user-key"
                }));
                */
    }
    //endregion
}

class ListTests implements Test0 {
    private Dispatcher dispatcher;
    public ListTests(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }
    public Map<String, Object> Test() {
    	HashMap<String,Object> Result = new HashMap<String,Object>();
    	Result.put("Test Names", dispatcher.Keys());
    	
        return Result;
    }
}
