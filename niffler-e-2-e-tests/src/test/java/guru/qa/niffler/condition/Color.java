package guru.qa.niffler.condition;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Color {
    YELLOW("rgba(255, 183, 3, 1)"),
    GREEN("rgba(53, 173, 123, 1)");

    public final String rgb;

    public static Color getByValue(String value) {
        for (Color color : Color.values()) {
            if (color.rgb.equalsIgnoreCase(value)) {
                return color;
            }
        }
        // Or throw an IllegalArgumentException if no match is found
        throw new IllegalArgumentException("No enum constant with value: " + value);
    }
}
