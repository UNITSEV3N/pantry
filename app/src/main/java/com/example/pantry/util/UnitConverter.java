package com.example.pantry.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class UnitConverter
{
    private static final Map<String, Double> MASS_TO_GRAMS = new HashMap<>();
    private static final Map<String, Double> VOLUME_TO_ML = new HashMap<>();

    static
    {
        MASS_TO_GRAMS.put("g", 1.0);
        MASS_TO_GRAMS.put("gram", 1.0);
        MASS_TO_GRAMS.put("grams", 1.0);
        MASS_TO_GRAMS.put("kg", 1000.0);
        MASS_TO_GRAMS.put("kilogram", 1000.0);
        MASS_TO_GRAMS.put("kilograms", 1000.0);

        VOLUME_TO_ML.put("ml", 1.0);
        VOLUME_TO_ML.put("millilitre", 1.0);
        VOLUME_TO_ML.put("millilitres", 1.0);
        VOLUME_TO_ML.put("l", 1000.0);
        VOLUME_TO_ML.put("litre", 1000.0);
        VOLUME_TO_ML.put("litres", 1000.0);
        VOLUME_TO_ML.put("liter", 1000.0);
        VOLUME_TO_ML.put("liters", 1000.0);
    }

    public static double convert(double quantity, String fromUnit, String toUnit)
    {
        String from = normalizeUnit(fromUnit);
        String to = normalizeUnit(toUnit);

        Double fromGrams = MASS_TO_GRAMS.get(from);
        Double toGrams = MASS_TO_GRAMS.get(to);
        if (fromGrams != null && toGrams != null)
        {
            return quantity * fromGrams / toGrams;
        }

        Double fromMl = VOLUME_TO_ML.get(from);
        Double toMl = VOLUME_TO_ML.get(to);
        if (fromMl != null && toMl != null)
        {
            return quantity * fromMl / toMl;
        }

        return quantity;
    }

    private static String normalizeUnit(String unit)
    {
        return unit == null ? "" : unit.trim().toLowerCase(Locale.ROOT);
    }
}
