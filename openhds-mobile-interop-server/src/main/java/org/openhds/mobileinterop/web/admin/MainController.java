package org.openhds.mobileinterop.web.admin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.hibernate.EntityMode;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.openhds.mobileinterop.dao.UserDao;
import org.openhds.mobileinterop.model.DatabaseOptions;
import org.openhds.mobileinterop.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Handles administrative tasks relating to the configuration of the interop
 * server: database configuration, creating admin user
 */
@Controller
public class MainController extends AbstractUserController {

	private static final String DATABASE_PASSWORD_PROP = "database.password";
	private static final String DATABASE_USER_PROP = "database.username";
	private static final String DATABASE_NAME_PROP = "database.jdbc.url";
	private static final String DATABASE_PROPERTIES_FILE = "database.properties";
	private static final String MYSQL_HIBERNATE_DIALECT = "org.hibernate.dialect.MySQL5InnoDBDialect";
	private static final String MYSQL_DRIVER_CLASS = "com.mysql.jdbc.Driver";
	private UserDao dao;
	private boolean adminUserPresent = false;
	private String jdbcUrl;
	private SessionFactory sessionFactory;

	@Autowired
	public MainController(UserDao dao, SessionFactory sessionFactory,
			@Value("${database.jdbc.url}") String jdbcUrl) {
		this.dao = dao;
		this.sessionFactory = sessionFactory;
		this.jdbcUrl = jdbcUrl;
	}

	@RequestMapping(value = { "", "/" })
	public ModelAndView index() {
		if (!adminUserPresent) {
			User admin = dao.findUserById("admin");
			if (admin == null) {
				return new ModelAndView("adminCreate");
			} else {
				adminUserPresent = true;
			}
		}

		return new ModelAndView("redirect:/admin");
	}

	private boolean databaseIsConfigured() {
		if (jdbcUrl.contains("h2:mem")) {
			return false;
		}

		return true;
	}

	@RequestMapping(value = { "", "/" }, method = RequestMethod.POST)
	public ModelAndView createAdmin(
			@RequestBody MultiValueMap<String, String> formValues) {
		formValues.add("username", "admin");
		User user = getUserFromSubmittedValues(formValues);

		List<String> errors = validateUser(user,
				formValues.getFirst("confirmPassword"));
		if (errors.size() > 0) {
			ModelAndView mv = new ModelAndView("adminCreate");
			mv.addObject("errors", errors);
			return mv;
		}

		dao.saveUser(user, "ROLE_ADMIN");

		return new ModelAndView("redirect:/admin");
	}

	@RequestMapping({ "/admin", "/admin/" })
	public ModelAndView adminIndex() {
		ModelAndView mv = new ModelAndView("index");

		if (!databaseIsConfigured()) {
			mv.addObject("needDatabaseConfiguration", true);
		}

		return mv;
	}

	@RequestMapping("/admin/database-configuration")
	public ModelAndView databaseConfiguration() {
		ModelAndView mv = new ModelAndView("databaseConfiguration");
		ClassPathResource resource = new ClassPathResource("database.properties");
		Properties prop = new Properties();
		try {
			prop.load(resource.getInputStream());
			DatabaseOptions dbCreds = new DatabaseOptions();
			dbCreds.setDatabaseName(extractDatabaseName(prop.getProperty(DATABASE_NAME_PROP)));
			dbCreds.setDatabasePassword(prop.getProperty(DATABASE_PASSWORD_PROP));
			dbCreds.setDatabaseUsername(prop.getProperty(DATABASE_USER_PROP));
			addValues(dbCreds, mv);
		} catch (IOException e) { }
		
		return mv;
	}

	private String extractDatabaseName(String property) {
		int index = property.lastIndexOf("/");
		return property.substring(index + 1);
	}

	@RequestMapping(value = "/admin/database-configuration", method = RequestMethod.POST)
	public ModelAndView writeDatabaseConfig(DatabaseOptions dbCreds) {
		List<String> errors = validateFormValues(dbCreds);
		if (errors.size() > 0) {
			ModelAndView mv = new ModelAndView("databaseConfiguration");
			mv.addObject("errors", errors);
			addValues(dbCreds, mv);
			return mv;
		}
		
		if (dbCreds.getCreateDatabase() != null) {
			createDatabaseTables(dbCreds);
		}

		writeDatabaseConfigFile(dbCreds);

		return new ModelAndView("redirect:/application-restart");
	}

	private void addValues(DatabaseOptions dbCreds, ModelAndView mv) {
		mv.addObject("databaseUsername", dbCreds.getDatabaseUsername());
		mv.addObject("databasePassword", dbCreds.getDatabasePassword());
		mv.addObject("databaseName", dbCreds.getDatabaseName());
	}

	private List<String> validateFormValues(DatabaseOptions dbCreds) {
		List<String> errors = new ArrayList<String>();
		String databaseName = dbCreds.getDatabaseName();
		String databaseUser = dbCreds.getDatabaseUsername();
		String databasePassword = dbCreds.getDatabasePassword();

		if (databaseName == null || databaseName.trim().isEmpty()) {
			errors.add("No database name provided");
		}

		if (databaseUser == null | databaseUser.trim().isEmpty()) {
			errors.add("No database username provided");
		}

		if (databasePassword == null || databasePassword.trim().isEmpty()) {
			errors.add("No database password provided");
		}

		return errors;
	}

	private void writeDatabaseConfigFile(DatabaseOptions dbCreds) {
		ClassPathResource resouce = new ClassPathResource(DATABASE_PROPERTIES_FILE);
		try {
			File properties = resouce.getFile();
			FileOutputStream stream = new FileOutputStream(properties);
			Properties propFile = new Properties();
			propFile.put(DATABASE_NAME_PROP, getMysqlJdbcUrlFromName(dbCreds.getDatabaseName()));
			propFile.put(DATABASE_USER_PROP, dbCreds.getDatabaseUsername());
			propFile.put(DATABASE_PASSWORD_PROP, dbCreds.getDatabasePassword());
			propFile.put("database.jdbc.driver", MYSQL_DRIVER_CLASS);
			propFile.put("database.hbm2ddl", "update");
			propFile.store(stream, null);
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	private void createDatabaseTables(DatabaseOptions dbCreds) {
		AnnotationConfiguration config = new AnnotationConfiguration();
		config.setProperty("hibernate.connection.driver_class", MYSQL_DRIVER_CLASS)
			  .setProperty("hibernate.connection.url", getMysqlJdbcUrlFromName(dbCreds.getDatabaseName()))
			  .setProperty("hibernate.connection.username", dbCreds.getDatabaseUsername())
			  .setProperty("hibernate.connection.password", dbCreds.getDatabasePassword())
			  .setProperty("hibernate.dialect", MYSQL_HIBERNATE_DIALECT);
		addAnnotatedClasses(config);
		SchemaExport exporter = new SchemaExport(config);
		exporter.create(false, true);
	}

	private void addAnnotatedClasses(AnnotationConfiguration config) {
		Map metadata = sessionFactory.getAllClassMetadata();
		Set<Entry> entries = metadata.entrySet();
		for(Entry entry : entries) {
			ClassMetadata meta = (ClassMetadata) entry.getValue();
			config.addAnnotatedClass(meta.getMappedClass(EntityMode.POJO));
		}
	}

	private String getMysqlJdbcUrlFromName(String databaseName) {
		return "jdbc:mysql://localhost/" + databaseName + "?createDatabaseIfNotExist=true";
	}

	@RequestMapping("/application-restart")
	public ModelAndView applicationRestart() {
		return new ModelAndView("applicationRestart");
	}
}
