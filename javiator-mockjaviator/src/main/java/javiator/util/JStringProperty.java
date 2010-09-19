package javiator.util;

/**
 * A JProperty with a String value
 */
public class JStringProperty extends JProperty
{
	public String defaultValue;

	public String getDefaultValueAsString()
	{
		return defaultValue == null ? "" : defaultValue;
	}
}
