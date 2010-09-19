package javiator.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

/**
 * A wrapper for Properties for use in JControl and ExotaskControl: allows system properties to override
 *   a properties file.  Uses the JProperty hierarchy, which groups default values with keys.  Delegation is 
 *   used rather than inheritance and overriding to ensure that the mechanisms imposed by this class are not 
 *   bypassed.
 */
public class JProperties
{
	/** The contained Properties object */
	private Properties delegate;

	/**
	 * Create a new JProperties 
	 */
	public JProperties()
	{
		delegate = new Properties();
	}
	
	/**
	 * Determine whether this JProperties contains a particular JProperty
	 * @param property the JProperty to check
	 * @return true if present
	 */
	public boolean contains(JProperty property)
	{
		return delegate.containsKey(property.name);
	}
	
	/**
	 * Get the value of a boolean property
	 * @param key the JBooleanProperty serving as a key
	 * @return the value
	 */
	public boolean getBoolean(JBooleanProperty key)
	{
		String ans = System.getProperty(key.name);
		if (ans == null) {
			ans = delegate.getProperty(key.name);
			if (ans == null) {
				return key.defaultValue;
			}
		}
		return Boolean.valueOf(ans).booleanValue();
	}
	
	/**
	 * Get the value of a boolean property if it is present, otherwise, return the supplied default
	 * @param key the JBooleanProperty serving as a key
	 * @param defaultValue the default value to use if not present
	 * @return the value of the property if present or the supplied default
	 */
	public boolean getBoolean(JBooleanProperty key, boolean defaultValue)
	{
		if (contains(key)) {
			return getBoolean(key);
		}
		return defaultValue;
	}
	
	/**
	 * Get the value of a double property
	 * @param key the JDoubleProperty serving as a key
	 * @return the value
	 */
	public double getDouble(JDoubleProperty key)
	{
		String ans = System.getProperty(key.name);
		if (ans == null) {
			ans = delegate.getProperty(key.name);
			if (ans == null) {
				return key.defaultValue;
			}
		}
		return Double.parseDouble(ans);
	}

	/**
	 * Get the value of a double property if it is present, otherwise, return the supplied default
	 * @param key the JDoubleProperty serving as a key
	 * @param defaultValue the default value to use if not present
	 * @return the value of the property if present or the supplied default
	 */
	public double getDouble(JDoubleProperty key, double defaultValue)
	{
		if (contains(key)) {
			return getDouble(key);
		}
		return defaultValue;
	}

	/**
	 * Get the value of a property as a String regardless of its type
	 * @param key the JProperty serving as a key
	 * @return the value as a String
	 */
	public String getFormatedProperty(JProperty key)
	{
		String ans = System.getProperty(key.name);
		if (ans != null) {
			return ans;
		}
		ans = delegate.getProperty(key.name);
		if (ans != null) {
			return ans;
		}
		return key.getDefaultValueAsString();
	}
	
	/**
	 * Get the value of an int property
	 * @param key the JIntProperty serving as a key
	 * @return the value
	 */
	public int getInt(JIntProperty key)
	{
		String ans = System.getProperty(key.name);
		if (ans == null) {
			ans = delegate.getProperty(key.name);
			if (ans == null) {
				return key.defaultValue;
			}
		}
		return Integer.parseInt(ans);
	}

	/**
	 * Get the value of an int property if it is present, otherwise, return the supplied default
	 * @param key the JIntProperty serving as a key
	 * @param defaultValue the default value to use if not present
	 * @return the value of the property if present or the supplied default
	 */
	public int getInt(JIntProperty key, int defaultValue)
	{
		if (contains(key)) {
			return getInt(key);
		}
		return defaultValue;
	}

	/**
	 * Get the raw string property by its raw string name without any JControl semantics, conversion,
	 *   defaulting, or overriding by System properties.
	 * @param name the name of the property
	 * @return the value of the property if it is recorded in this JProperties, null otherwise
	 */
	public String getRawProperty(String name)
	{
		return delegate.getProperty(name);
	}

	/**
	 * Get the value of a String property
	 * @param key the JStringProperty serving as a key
	 * @return the value
	 */
	public String getString(JStringProperty key)
	{
		String ans = System.getProperty(key.name);
		if (ans != null) {
			return ans;
		}
		ans = delegate.getProperty(key.name);
		if (ans != null) {
			return ans;
		}
		return key.defaultValue;
	}

	/**
	 * Get the value of a String property if it is present, otherwise, return the supplied default
	 * @param key the JStringProperty serving as a key
	 * @param defaultValue the default value to use if not present
	 * @return the value of the property if present or the supplied default
	 */
	public String getString(JStringProperty key, String defaultValue)
	{
		if (contains(key)) {
			return getString(key);
		}
		return defaultValue;
	}

	/**
	 * Test whether properties object is empty
	 * @return true if emtpy
	 */
	public boolean isEmpty()
	{
		return delegate.isEmpty();
	}

	/**
	 * Return a keySet on the underlying properties
	 * @return a keySet on the underlying properties
	 */
	public Set keySet()
	{
		return delegate.keySet();
	}

	/**
	 * Load the properties
	 * @param stream an InputStream to load from
	 * @throws IOException 
	 */
	public void load(InputStream stream) throws IOException
	{
		delegate.load(stream);
	}

	/**
	 * Add to the properties (not typed)
	 * @param key the name of the property
	 * @param value the value of the property as a String
	 */
	public void put(String key, String value)
	{
		delegate.put(key, value);
	}

	/**
	   * Acquire the properties via argument or use all defaults.
	   * @param args the argument array (only args[0] is used and there must not be others)
	   * @param cls the main Class
	   * @return the Properties
	   * @throws IOException if something goes wrong
	   */
	public static JProperties acquireProperties(String[] args, Class cls) throws IOException
	{
		JProperties properties = new JProperties();
		if ( args.length > 1 ) {
			throw new IllegalArgumentException("At most one argument (the name of a properties file).");
		}
		if ( args.length == 1 ) {
			properties.delegate.load(new FileInputStream(args[0]));
		}
  	properties.put(JProperty.driver.name, cls.getName());
//  	ThreadFactory.useJavaPriorities = properties.getBoolean(JProperty.useJavaPriorities);
		return properties;
	}
}
