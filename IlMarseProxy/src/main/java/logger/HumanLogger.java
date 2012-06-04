package logger;

import org.apache.log4j.Logger;

public class HumanLogger implements Logger4j{


	private Logger logger;
	
	public HumanLogger(Class<?> clazz) {
		logger = Logger.getLogger(clazz);
	}

	public void log(String message) {
		logger.debug(message);
	}
}
