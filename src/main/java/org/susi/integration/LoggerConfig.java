package org.susi.integration;

import org.apache.log4j.Logger;



public class LoggerConfig {



	Logger log = Logger.getLogger(LoggerConfig.class);
	
	public static String PEID = "PEID";
	
	public static String LOG_TYPE = "LogType";
	public static String LOG_TYPE_INFO = "INFO";
	public static String LOG_TYPE_ERROR = "ERROR";
	
	public static String LOG_DIRECTION = "LogDirection";
	public static String LOG_DIRECTION_S = "S";
	public static String LOG_DIRECTION_T = "T";
	public static String LOG_DIRECTION_TR = "T_R";
	public static String LOG_DIRECTION_SR = "S_R";
	
	public static String EXECUTION_MODE = "ExecutionMode";
	public static String EXECUTION_MODE_LISTENER = "ListenerExecution";
	public static String EXECUTION_MODE_MANUAL = "ManualRetry";
	public static String EXECUTION_MODE_SCHEDULED = "ScheduledExecution";
	
	public static String EXECUTION_USER = "ExecutionUser";
	public static String EXECUTION_USER_SYSTEM = "System";
	
	public static String ENDPOINT_TYPE = "EndpointType";
	public static String ENDPOINT_TYPE_FTP = "FTP";
	public static String ENDPOINT_TYPE_MAIL = "MAIL";
	public static String ENDPOINT_TYPE_HTTP = "HTTP";
	public static String ENDPOINT_TYPE_JMS = "JMS";
	public static String ENDPOINT_TYPE_SOLACE = "SOLACE";
	
	public static String ENDPOINT_META = "EndpointMeta";
	
	public static String FILE_NAME = "file_name";
	public static String FTP_USER = "ftp_user";
	public static String FILE_REMOTE_FILE = "file_remoteFile";
	public static String FILE_REMOTE_DIRECTORY = "file_remoteDirectory";
	public static String FILE_REMOTE_HOST_PORT = "file_remoteHostPort";
	
	public static String JMS_DESTINATION = "jms_destination";
	public static String JMS_HOSTPORT = "jms_hostPort";
	public static String JMS_USER = "jms_user";
	
	public static final String HTTP_URL = "http_url";
	public static final String HTTP_USER = "http_user";
	
	public static String SOURCE_APP = "SourceApp";
	public static String TARGET_APP = "TargetApp";
	public static String PROCESS_NAME = "ProcessName";
	public static String PROCESS_GROUP = "ProcessGroup";

}