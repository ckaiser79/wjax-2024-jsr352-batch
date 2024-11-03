package de.continentale.vu.demo_jsr352.batches;

import de.continentale.vu.demo_jsr352.domain.Gender;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static de.continentale.vu.demo_jsr352.domain.Gender.FEMALE;
import static de.continentale.vu.demo_jsr352.domain.Gender.MALE;
import static de.continentale.vu.demo_jsr352.domain.Gender.UNKNOWN;
import static de.continentale.vu.demo_jsr352.domain.Gender.X_GENDER;

class GenderMappingTextToDatabaseFunction implements Function<String, Gender> {

    private final Map<String,Gender> map = new HashMap<>();

    GenderMappingTextToDatabaseFunction() {
        map.put("m", MALE);
        map.put("M", MALE);

        map.put("w", FEMALE);
        map.put("W", FEMALE);

        map.put("d", X_GENDER);
        map.put("D", X_GENDER);
    }

    @Override
    public Gender apply(final String textFileGender) {
        return map.getOrDefault(textFileGender, UNKNOWN);
    }
}
