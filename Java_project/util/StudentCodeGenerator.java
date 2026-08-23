package util;

public final class StudentCodeGenerator {
    private StudentCodeGenerator() {
    }

    public static String fromNumber(int number) {
        return String.format("ST%03d", Math.max(1, number));
    }

    public static String nextCode(String lastCode) {
        if (lastCode == null || !lastCode.matches("ST\\d+")) {
            return "ST001";
        }

        int number = Integer.parseInt(lastCode.substring(2));
        return fromNumber(number + 1);
    }
}
