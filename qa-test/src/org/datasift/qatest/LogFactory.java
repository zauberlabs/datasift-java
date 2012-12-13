package org.datasift.qatest;

class LogFactory
{
    private static Logger _theLogger = null;

	public Logger theLogger() {
		//evaluate once (per class/system)
        if (_theLogger == null)
        {
            _theLogger = new Logger();
        }
        return _theLogger;
	}
}
