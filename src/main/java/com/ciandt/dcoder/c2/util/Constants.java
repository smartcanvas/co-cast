package com.ciandt.dcoder.c2.util;

/**
 * System's constants
 * @author Daniel Viveiros
 */
public class Constants {
	
	/** System queues */
	public static final String INGESTION_QUEUE = "content-ingestion-queue";
	
	/** Email constants */
    public static final String[] statusEmails = { "viveiros@ciandt.com", "fmir@ciandt.com" };
    public static final String[] statusErrorEmails = { "viveiros@ciandt.com", "fmir@ciandt.com" };
    public static final String[] statusNames = { "Daniel Viveiros", "Felipe Mir" };
    public static final String[] statusErrorNames = { "Daniel Viveiros", "Felipe Mir" };
    public static final String defaultFromEmail = "s10-viveiros@appspot.gserviceaccount.com";
    public static final String defaultFromName = "C2 (D-Coder)";

}
