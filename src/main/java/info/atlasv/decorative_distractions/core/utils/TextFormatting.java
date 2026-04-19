package info.atlasv.decorative_distractions.core.utils;

public class TextFormatting {

    // Captialises the first letter of each word
    public static String firstLetterCapital(String raw) {
        String[] words = raw.replace("_", " ").split(" ");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            result.append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1))
                    .append(" ");
        }
        return result.toString().trim(); // remove trailing space
    }

    // Impliments a very dirty APA title case system
    public static String titleCase(String raw) {
        String[] words = raw.replace("_", " ").split(" ");
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < words.length; i++) {
            String word = words[i].toLowerCase();
            if (i == 0 || i == words.length - 1 || word.length() >= 4) {
                result.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1));
            } else {
                result.append(word);
            }
            result.append(" ");
        }

        return result.toString().trim();
    }

    // Impliments a very dirty APA title case system but using minecrafts "Block of" naming scheme
    public static String altTitleCase(String raw) {
        String[] words = raw.replace("_", " ").split(" ");
        int remainderLength = words.length - 1;
        StringBuilder remainder = new StringBuilder();

        for (int i = 0; i < remainderLength; i++) {
            String word = words[i].toLowerCase();
            if (i == 0 || i == remainderLength - 1 || word.length() >= 4) {
                remainder.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1));
            } else {
                remainder.append(word);
            }
            if (i < remainderLength - 1) remainder.append(" ");
        }

        return "Block of " + remainder;
    }
}
