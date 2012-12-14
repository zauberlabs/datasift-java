package org.datasift.qatest;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

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

    public java.util.List<String> Keys() {
        return dispatchTable.Keys.ToList();
    }

    public void Add(String key, TestDispatchTableValues value) {
        dispatchTable.Add(key, value);
    }

    public void dispatchTest() {
        if (!dispatchTable.ContainsKey(this._options["test"])) {
            _theLogger.log("invalid test name -- run test list to get a list");
            Environment.Exit(1);
        } else {
            var test = dispatchTable[this._options["test"]];
            System.Diagnostics.Debug.Assert(test != null);

            if (!areAllOptionsSet(test)) {
                _theLogger.logError("missing options");
                Environment.Exit(1);
            } else {
                runTest(this._options["test"], test);
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

    private bool hasRequiredOptions(TestDispatchTableValues test) {
        return test.requiredOptions != null;
    }

    private bool areAllOptionsSet(TestDispatchTableValues test) {
        return !hasRequiredOptions(test) || test.requiredOptions.TrueForAll((i) => (_options.ContainsKey(i)));
    }
}

final class TestDispatchTableValues {
    public delegate Object testRoutineDelegate();

    public TestDispatchTableValues(
        Test routine,
        String[] requiredOptions) {
        this.routine = routine;
        this.requiredOptions = (requiredOptions == null) ? null : new List<String>(requiredOptions);
    }

    public Test routine;
    public List<String> requiredOptions;
}
