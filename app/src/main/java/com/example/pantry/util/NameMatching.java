package com.example.pantry.util;

import java.util.Locale;

public class NameMatching
{
    public static String normalize(String rawName)
    {
        if (rawName == null) return "";

        String cleaned = rawName.trim().toLowerCase(Locale.ROOT).replaceAll("[^a-z ]", "");
        return singularize(cleaned);
    }

    public static boolean matches(String a, String b)
    {
        return normalize(a).equals(normalize(b));
    }

    private static String singularize(String word)
    {
        if (word.endsWith("ies") && word.length() > 4)
        {
            return word.substring(0, word.length() - 3) + "y";
        }
        if (word.endsWith("oes") || word.endsWith("ches") || word.endsWith("shes") || word.endsWith("xes") || word.endsWith("ses") || word.endsWith("zes"))
        {
            return word.substring(0, word.length() - 2);
        }
        if (word.endsWith("s") && !word.endsWith("ss") && word.length() > 1)
        {
            return word.substring(0, word.length() - 1);
        }
        return word;
    }
}
