package org.aws.designer.eclipse.common;

import org.eclipse.core.runtime.IStatus;

public class Logger {
	public static void logInfo(String message) {
		log(1, 0, message, null);
	}

	public static void logDebug(String message) {
		log(1, 0, message, null);
	}

	public static void logError(Throwable exception) {
		logError("Unexpected Exception", exception);
	}

	public static void logError(String message, Throwable exception) {
		log(4, 0, message, exception);
	}

	public static void log(int severity, int code, String message, Throwable exception) {
//		log(createStatus(severity, code, message, exception));
	}

//	public static IStatus createStatus(int severity, int code, String message, Throwable exception) {
//		return new org.eclipse.core.runtime.Status(severity, AWSPlugin.getDefault().getBundle().getSymbolicName(), code, message, exception);
//	}

	public static void log(IStatus status) {
//		AWSPlugin.getDefault().getLog().log(status);
	}
}
