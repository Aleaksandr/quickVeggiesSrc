package com.quickveggies;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DateTimeException;
import java.util.*;

/**
 * This class is used to load properties for the current user of the application it can be eather database informations or
 * SMS Template
 */
public class UserGlobalParameters {

    public static DataBaseInformation getMasterInformation() {
    	for(DataBaseInformation data  : storedDatabaseInformations) {
    		if (data.getMaster()) {
    			return  data;
			}
		}
		throw  new IllegalStateException("Can't find the master database profile");
	}
	public static List<DataBaseInformation> getAlternateDataBaseInformations() {
    	List<DataBaseInformation> result = new ArrayList<DataBaseInformation>();
    	result.addAll(storedDatabaseInformations);
    	result.remove(getMasterInformation());
    	return result;
	}
	public static List<DataBaseInformation> getStoredDatabaseInformations() {
		return storedDatabaseInformations;
	}

	public static void storeDataBasesProfiles(List<DataBaseInformation> informations) throws IOException {
    	Properties dataToStore = new Properties();

    	int currentDataToStore = 1;
    	for(DataBaseInformation information : informations) {
    		dataToStore.put(DATABASE_INFO_FIELDS.PROFILE_NAME+""+currentDataToStore,information.getProfileName());
			dataToStore.put(DATABASE_INFO_FIELDS.IS_MASTER+""+currentDataToStore,information.getMaster()+"");
			dataToStore.put(DATABASE_INFO_FIELDS.URL+""+currentDataToStore,information.getHost());
			dataToStore.put(DATABASE_INFO_FIELDS.PORT+""+currentDataToStore,information.getPort());
			dataToStore.put(DATABASE_INFO_FIELDS.LOGIN+""+currentDataToStore,information.getLogin());
			dataToStore.put(DATABASE_INFO_FIELDS.PASSWORD+""+currentDataToStore,information.getPassword());
			dataToStore.put(DATABASE_INFO_FIELDS.INSTANCE+""+currentDataToStore,information.getInstance());
			dataToStore.put(DATABASE_INFO_FIELDS.DATABASENAME+""+currentDataToStore,information.getDatabaseName());
			currentDataToStore++;
		}
		Path currentRelativePath = Paths.get("");
		String s = currentRelativePath.toAbsolutePath().toString();

		FileOutputStream stream = new FileOutputStream(s+"/"+DB_PROPERTY_FILE);
		dataToStore.save(stream,"Database properties updated "+new Date());
		stream.close();

	}

	private static List<DataBaseInformation> storedDatabaseInformations = new ArrayList<DataBaseInformation>();
	public static class DataBaseInformation {
		private String profileName;
		private Boolean master;
		private String host;
		private String port;
		private String login;
		private String password;
		private String instance;
		private String databaseName;
        public DataBaseInformation() {

		}
		public DataBaseInformation(String profileName, Boolean master, String host, String port, String login, String password, String instance, String databaseName) {
			this.profileName = profileName;
			this.master = master;
			this.host = host;
			this.port = port;
			this.login = login;
			this.password = password;
			this.instance = instance;
			this.databaseName = databaseName;
		}

		public String getProfileName() {
			return profileName;
		}

		public void setProfileName(String profileName) {
			this.profileName = profileName;
		}

		public Boolean getMaster() {
			return master;
		}

		public void setMaster(Boolean master) {
			this.master = master;
		}

		public String getHost() {
			return host;
		}

		public void setHost(String host) {
			this.host = host;
		}

		public String getPort() {
			return port;
		}

		public void setPort(String port) {
			this.port = port;
		}

		public String getLogin() {
			return login;
		}

		public void setLogin(String login) {
			this.login = login;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getInstance() {
			return instance;
		}

		public void setInstance(String instance) {
			this.instance = instance;
		}

		public String getDatabaseName() {
			return databaseName;
		}

		public void setDatabaseName(String databaseName) {
			this.databaseName = databaseName;
		}
	}
	
	/**
	 * The list of datase informations that will should provide to the system to work
	 */
	enum DATABASE_INFO_FIELDS {
	PROFILE_NAME,IS_MASTER,URL,PORT,LOGIN,PASSWORD,INSTANCE,DATABASENAME
	 }

     private static final String DB_PROPERTY_FILE = "database.cfg";
     private static final String SMS_PROPERTY_FILE = "sms.cfg";
	/**
	 * This method will load configuration from property files
	 */
	public static void load() throws IOException {
            storedDatabaseInformations.clear();
		loadDataBaseProperties();
	}

	/**
	 * This method will load the database properties form a property file. A property File Can store one or more DataBase configuration
	 */
	private static void loadDataBaseProperties() throws IOException {
		try {
			Path currentRelativePath = Paths.get("");
			String s = currentRelativePath.toAbsolutePath().toString();
			FileInputStream input = new FileInputStream(s + "/" + DB_PROPERTY_FILE);

			Properties dataBaseProperty = new Properties();
			dataBaseProperty.load(input);
			List<DataBaseInformation> dataBaseInformations = buildDataBaseInformationFromProperties(dataBaseProperty);
			storedDatabaseInformations.addAll(dataBaseInformations);
		}catch(Exception ex) {
			//Do nothing
		}
	}

	/**
	 * This method will return an instance of DataBaseInformation from Java.Util.Properties entity passed on parameter
	 * @param targetProperties the Properties to be used
	 * @return The list of database information from the property file
	 */
	private static List<DataBaseInformation> buildDataBaseInformationFromProperties(Properties targetProperties) {
		int numberOfParamsStoredIntoTheProperties =  targetProperties.keySet().size();
		List<DataBaseInformation> result = new ArrayList<DataBaseInformation>();
	    int currentDataBaseProfileID = 1;
	    int numberOfParamToLoad = DATABASE_INFO_FIELDS.values().length;
	    if (numberOfParamsStoredIntoTheProperties % numberOfParamToLoad != 0) {
	    	throw new IllegalStateException("The database property file is corrupted");
		}
		int numberOfDataBaseParamStoredIntoTheFile = numberOfParamsStoredIntoTheProperties /numberOfParamToLoad;
	    for (int i=currentDataBaseProfileID;i<=numberOfDataBaseParamStoredIntoTheFile;i++) {

			String profileName = targetProperties.getProperty(DATABASE_INFO_FIELDS.PROFILE_NAME.toString() + i);
            if (profileName == null) {
            	throw new IllegalStateException("The filed profile name for the database definition "+i+" is invalid");
			}
			String databaseName = targetProperties.getProperty(DATABASE_INFO_FIELDS.DATABASENAME.toString() + i);
            if (databaseName == null) {
				throw new IllegalStateException("The filed database name for the database definition "+i+" is invalid");
			}
			String Instance = targetProperties.getProperty(DATABASE_INFO_FIELDS.INSTANCE.toString() + i);
			if (Instance == null) {
				throw new IllegalStateException("The filed instance for the database definition "+i+" is invalid");
			}
			String Login = targetProperties.getProperty(DATABASE_INFO_FIELDS.LOGIN.toString() + i);
			if (Login == null) {
				throw new IllegalStateException("The filed login for the database definition "+i+" is invalid");
			}
			String password = targetProperties.getProperty(DATABASE_INFO_FIELDS.PASSWORD.toString() + i);
			if (password == null) {
				throw new IllegalStateException("The filed password for the database definition "+i+" is invalid");
			}
			String port = targetProperties.getProperty(DATABASE_INFO_FIELDS.PORT.toString() + i);
			if (port  == null) {
				throw new IllegalStateException("The filed port for the database definition "+i+" is invalid");
			}
			String isMaster = targetProperties.getProperty(DATABASE_INFO_FIELDS.IS_MASTER.toString() + i);
			if (isMaster  == null ) {
				throw new IllegalStateException("The filed is Master for the database definition "+i+" is invalid");
			}
			String url = targetProperties.getProperty(DATABASE_INFO_FIELDS.URL.toString() + i);
			if (url  == null ) {
				throw new IllegalStateException("The filed is url for the database definition "+i+" is invalid");
			}
			DataBaseInformation information = new DataBaseInformation();
			information.setDatabaseName(databaseName);
			information.setHost(url);
			information.setPort(port);
			information.setPassword(password);
			information.setMaster(Boolean.parseBoolean(isMaster));
			information.setLogin(Login);
			information.setProfileName(profileName);
			information.setInstance(Instance);
			result.add(information);
		}

	return result;
	}
	public final static String userEmail = "SuperSalesAgro@gmail.com";// "crobuk@gmail.com";
	public final static String userPwd = "demo1234";// "102938475";
	private static final String qvPath = System.getProperty("user.home") + File.separator + "QVSMSTemplate.txt";

	// PARAMETERS FOR LOGIN INTO SQL SERVER FROM JAVA LIBRARY JDBC
	// jdbc:sqlserver://[serverName[\instanceName][:portNumber]][;property=value[;property=value]]
	/*public static String SQLURL = "localhost";
	public static String SQLURL2 = "192.168.1.110";
	public static String SQLINSTANCE = "ROOT";
    public static String SQLPORT = "1433";
	public static String SQLUSER = "root";
	public static String SQLDATABASE_NAME = "qvdb";
	public static String SQLPASS = "1234";*/

	//TODO temp
	public static String SQLURL = "127.0.0.1";
	public static String SQLURL2 = "192.168.1.110";
	// public static String SQLUSER = "qvuser";
	// public static String SQLPASS = "qvdbusr123";
	public static String SQLINSTANCE = "ghassen";
	public static String SQLPORT = "1433";
	public static String SQLUSER = "sa";
	public static String SQLDATABASE_NAME = "qvdb";
	public static String SQLPASS = "ghassen";

	//SMS sender ID 
	public static final String SMS_SENDER_ID = "QIKVEG"; // 777777 ,  QIKVEG -- 333000 NA
	//SMS Authentication Key
	public static final String SMS_AUTH_KEY = "84044AwKDEHKgbhjt554ef84d";

	public static String[] appleQualitiesList = new String[] { "Royal Supreme", "Red Gold", "Golden", "AAA" };

	public static String[] kinnowMangoQualitiesList = new String[] { "Malcom", "Bonjour", "Merrick" };

	public static final String[] creditPeriodSource = { "1 day", "3 days", "7 days", "15 days", "30 days", "45 days" };

	public static String[] boxSizes = new String[] { "24", "32", "42", "45", "54", "60", "72", "84", "96", "108",
			"120" };

	public static final String[] buyerTypes = new String[] { "Ladaan", "Bijak", "Regular" };

	public static Map<Integer, PaymentMethodSource> getPaymentMethodMap() {
		Map<Integer, PaymentMethodSource> map = new LinkedHashMap<>();
		for (int i = 0; i < PaymentMethodSource.values().length; i++) {
			map.put(i + 1, PaymentMethodSource.values()[i]);
		}
		return map;
	}
	
	private static String SMS_TEMPLATE;
	
	public static String GET_SMS_TEMPLATE() {
		if (SMS_TEMPLATE == null) {
			StringBuilder sb = new StringBuilder();
			try {
				byte[] data = Files.readAllBytes(Paths.get(qvPath));
				SMS_TEMPLATE = new String(data);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return SMS_TEMPLATE;
	}
        
	/**
	 * 
	 * @param newTemplate
	 * @param update  Does not update the file if set to false
	 */
	public static void SET_SMS_TEMPLATE(String newTemplate, boolean update) {
			try {
				if (!new File(qvPath).exists() || update)
					Files.write(Paths.get(qvPath), newTemplate.getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	public static void main(String args[]) {
		//SET_SMS_TEMPLATE("blah", false);
		System.out.println(GET_SMS_TEMPLATE());
	}
}
