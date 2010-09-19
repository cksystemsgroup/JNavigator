package javiator.util;

/**
 * A JProperty with a boolean value 
 */
public class JBooleanProperty extends JProperty
{
	public boolean defaultValue;

	public String getDefaultValueAsString()
	{
		return String.valueOf(defaultValue);
	}
}
