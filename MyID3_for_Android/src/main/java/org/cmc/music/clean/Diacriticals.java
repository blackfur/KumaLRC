/*
 * Written By Charles M. Chen 
 * 
 * Created on Jan 1, 2006
 *
 */

package org.cmc.music.clean;

import java.util.HashMap;
import java.util.Map;

import org.cmc.music.metadata.MusicMetadataConstants;

public abstract class Diacriticals implements MusicMetadataConstants
{

	private static final Map DIACRITICALS = new HashMap();
	static
	{
		DIACRITICALS.put("?, "a");
		DIACRITICALS.put("?, "a");
		DIACRITICALS.put("?, "a");
		DIACRITICALS.put("?, "a");
		DIACRITICALS.put("?, "a");
		DIACRITICALS.put("?, "a");
		DIACRITICALS.put("?, "ae");
		DIACRITICALS.put("?, "c");
		DIACRITICALS.put("?, "e");
		DIACRITICALS.put("?, "e");
		DIACRITICALS.put("?, "e");
		DIACRITICALS.put("?, "e");
		DIACRITICALS.put("?, "i");
		DIACRITICALS.put("?, "i");
		DIACRITICALS.put("?, "i");
		DIACRITICALS.put("?, "i");
		DIACRITICALS.put("?, "n");
		DIACRITICALS.put("?, "o");
		DIACRITICALS.put("?, "o");
		DIACRITICALS.put("?, "o");
		DIACRITICALS.put("?, "o");
		DIACRITICALS.put("?, "o");
		DIACRITICALS.put("?, "o");
		DIACRITICALS.put("?, "u");
		DIACRITICALS.put("?, "u");
		DIACRITICALS.put("?, "u");
		DIACRITICALS.put("?, "u");
		DIACRITICALS.put("?, "y");
		DIACRITICALS.put("?, "a");
		DIACRITICALS.put("?, "a");
		DIACRITICALS.put("?, "a");
		DIACRITICALS.put("?, "a");
		DIACRITICALS.put("?, "a");
		DIACRITICALS.put("?, "a");
		DIACRITICALS.put("?, "a");
		DIACRITICALS.put("?, "ae");
		DIACRITICALS.put("?, "c");
		DIACRITICALS.put("?, "e");
		DIACRITICALS.put("?, "e");
		DIACRITICALS.put("?, "e");
		DIACRITICALS.put("?, "e");
		DIACRITICALS.put("?, "i");
		DIACRITICALS.put("?, "i");
		DIACRITICALS.put("?, "i");
		DIACRITICALS.put("?, "i");
		DIACRITICALS.put("?, "o");
		DIACRITICALS.put("?, "n");
		DIACRITICALS.put("?, "o");
		DIACRITICALS.put("?, "o");
		DIACRITICALS.put("?, "o");
		DIACRITICALS.put("?, "o");
		DIACRITICALS.put("?, "o");
		DIACRITICALS.put("?, "o");
		DIACRITICALS.put("?, "u");
		DIACRITICALS.put("?, "u");
		DIACRITICALS.put("?, "u");
		DIACRITICALS.put("?, "u");
		DIACRITICALS.put("?, "u");
	}

	public static final String convertDiacriticals(String s)
	{
		StringBuffer result = new StringBuffer();

		char chars[] = s.toCharArray();
		for (int i = 0; i < chars.length; i++)
		{
			char c = chars[i];
			String replacement = (String) DIACRITICALS.get("" + c);
			if (null != replacement)
				result.append(replacement);
			else
				result.append(c);
		}

		return result.toString();
	}

}
