package com.intellectualcrafters.json;

/**
 * The HTTPTokener extends the JSONTokener to provide additional methods for the parsing of HTTP headers.
 *
 * @author JSON.org
 * @version 2014-05-03
 */
public class HTTPTokener extends JSONTokener {
	/**
	 * Construct an HTTPTokener from a string.
	 *
	 * @param string A source string.
	 */
	public HTTPTokener(String string)
	{
		super(string);
	}

	/**
	 * Get the next token or string. This is used in parsing HTTP headers.
	 *
	 * @return A String.
	 * @throws JSONException
	 */
	public String nextToken() throws JSONException
	{
		char c;
		do
		{
			c = this.next();
		} while (Character.isWhitespace(c));
		StringBuilder sb = new StringBuilder();
		if ((c == '"') || (c == '\''))
		{
			char q = c;
			while (true)
			{
				c = this.next();
				if (c < ' ')
				{
					throw this.syntaxError("Unterminated string.");
				}
				if (c == q)
				{
					return sb.toString();
				}
				sb.append(c);
			}
		}
		while (true)
		{
			if ((c == 0) || Character.isWhitespace(c))
			{
				return sb.toString();
			}
			sb.append(c);
			c = this.next();
		}
	}
}
