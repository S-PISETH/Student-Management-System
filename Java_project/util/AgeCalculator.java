package util;

import java.time.LocalDate;
import java.time.Period;

public final class AgeCalculator {
    private AgeCalculator() {
    }

    public static int calculateAge(LocalDate dateOfBirth) {
        if (dateOfBirth == null) {
            return 0;
        }
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }
}
