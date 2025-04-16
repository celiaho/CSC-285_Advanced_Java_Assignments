public class ParseFractionStringTest {
    public static void main(String[] args) {
        String fractionalNumberString = "2/3";

        double result = parseFractionString(fractionalNumberString);

        System.out.println(result);
    }

    public static double parseFractionString(String fractionalNumberString) {
        String[] parts = fractionalNumberString.split("/");
        if (parts.length == 2) {
            try {
                // Parse both numerator and denominator as doubles
                double numerator = Double.parseDouble(parts[0]);
                double denominator = Double.parseDouble(parts[1]);

                return numerator / denominator;
            } catch (NumberFormatException e) {
                // Handle invalid input
                return 0.0;
            }
        } else {
            // Handle invalid input
            return 0.0;
        }
    }
}
