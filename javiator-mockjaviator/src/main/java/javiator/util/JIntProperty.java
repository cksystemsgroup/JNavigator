package javiator.util;

/**
 * A JProperty with an int value
 */
public class JIntProperty extends JProperty
{
	public int defaultValue;

	public String getDefaultValueAsString()
	{
		return String.valueOf(defaultValue);
	}
}
