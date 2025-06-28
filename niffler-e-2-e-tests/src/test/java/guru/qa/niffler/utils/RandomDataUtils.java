package guru.qa.niffler.utils;

import com.github.javafaker.Faker;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RandomDataUtils {

    private static final Faker FAKER = new Faker();

    public static String randomUsername() {
        return FAKER.name().username();
    }

    public static String randomName() {
        return FAKER.name().firstName();
    }

    public static String randomSurname() {
        return FAKER.name().lastName();
    }

    public static String randomCategoryName() {
        return FAKER.animal().name();
    }

    public static String randomSentence(int wordCount) {
        return IntStream.range(0, wordCount)
                .mapToObj(i -> FAKER.lorem().word())
                .collect(Collectors.joining(" "));
    }
}
