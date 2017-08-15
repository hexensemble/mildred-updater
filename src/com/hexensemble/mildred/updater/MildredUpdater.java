package com.hexensemble.mildred.updater;

/**
 * Main application class.
 * 
 * @author HexEnsemble
 * @author www.hexensemble.com
 * @version 1.0.4
 * @since 1.0.0
 */
public class MildredUpdater {

	/**
	 * Application version.
	 */
	public static final String VERSION = "Version 1.0.4";

	/**
	 * Application version date.
	 */
	public static final String DATE = "19-APR-2016";

	/**
	 * Main method.
	 * 
	 * @param args
	 *            String[] args
	 */
	public static void main(String[] args) {
		new Updater();
	}

}
