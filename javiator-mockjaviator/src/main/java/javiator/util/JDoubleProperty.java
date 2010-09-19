package javiator.util;

/**
 * A JProperty with a double value
 */
public class JDoubleProperty extends JProperty
{
	public double defaultValue;

	public String getDefaultValueAsString()
	{
		return String.valueOf(defaultValue);
	}
}
