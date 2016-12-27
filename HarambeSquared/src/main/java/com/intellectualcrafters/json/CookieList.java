package com.intellectualcrafters.json;

import java.util.Iterator;

/**
 * Convert a web browser cookie list string to a JSONObject and back.
 *
 * @author JSON.org
 * @version 2014-05-03
 */
public final class CookieList {
	private CookieList() {}

	/**
	 * Convert a cookie list into a JSONObject. A cookie list is a sequence of name/value pairs. The names are separated
	 * from the values by '='. The pairs are separated by ';'. The names and the values will be unescaped, possibly
	 * converting '+' and '%' sequences.
	 * <p>
	 * To add a cookie to a cooklist, cookielistJSONObject.put(cookieJSONObject.getString("name"),
	 * cookieJSONObject.getString("value"));
	 *
	 * @param string A cookie list string
	 * @return A JSONObject
	 * @throws JSONException
	 */
	public static JSONObject toJSONObject(String string) throws JSONException
	{
		JSONObject jo = new JSONObject();
		JSONTokener x = new JSONTokener(string);
		while (x.more())
		{
			String name = Cookie.unescape(x.nextTo('='));
			x.next('=');
			jo.put(name, Cookie.unescape(x.nextTo(';')));
			x.next();
		}
		return jo;
	}

	/**
	 * Convert a JSONObject into a cookie list. A cookie list is a sequence of name/value pairs. The names are separated
	 * from the values by '='. The pairs are separated by ';'. The characters '%', '+', '=', and ';' in the names and
	 * values are replaced by "%hh".
	 *
	 * @param jo A JSONObject
	 * @return A cookie list string
	 * @throws JSONException
	 */
	public static String toString(JSONObject jo) throws JSONException
	{
		boolean b = false;
		Iterator<String> keys = jo.keys();
		StringBuilder sb = new StringBuilder();
		while (keys.hasNext())
		{
			String string = keys.next();
			if (!jo.isNull(string))
			{
				if (b)
				{
					sb.append(';');
				}
				sb.append(Cookie.escape(string));
				sb.append('=');
				sb.append(Cookie.escape(jo.getString(string)));
				b = true;
			}
		}
		return sb.toString();
	}
}
