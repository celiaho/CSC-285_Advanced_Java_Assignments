package edu.bhcc;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Post/process multiplied recipe.
 */
public class PostMultipliedRecipe extends HttpServlet {
    protected double originalServings;
    protected double desiredServings;
    protected static double servingsMultiplier;

    /**
     * Process HTTP POST request.
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");

        PrintWriter writer = response.getWriter();

        // Retrieve and store user data
        String originalIngredients = request.getParameter("originalIngredients").trim();
        String originalServingsString = request.getParameter("originalServingsString");
        String desiredServingsString = request.getParameter("desiredServingsString");

        String errorMessage = null; // Initialize error message

        // *Validate input and handle missing data*
        // Validate original ingredient list
        if (originalIngredients == null || originalIngredients.isEmpty()) {
            errorMessage = "Error: Ingredient list is not specified.";  // DOES THIS ACTUALLY
            // DISPLAY ANYWHERE? NEED THIS LINE FROM BEFORE?
            // throw new IllegalArgumentException("Error: Ingredient list is not specified.");
        }
        // Validate original servings
        try {
            originalServings = Double.parseDouble(originalServingsString);
            if (originalServings <= 0) {
                errorMessage = "Error: Original yield must be greater than 0.";
            }
        } catch (NumberFormatException e) {
            errorMessage = "Error: Original yield must be a valid positive number in whole or " +
                    "decimal form.";
        }
        // Validate desired servings
        try {
            desiredServings = Double.parseDouble(desiredServingsString);
            if (desiredServings <= 0) {
                errorMessage = "Error: Desired yield must be greater than 0.";
            }
        } catch (NumberFormatException e) {
            errorMessage = "Error: Desired yield must be a valid positive number in whole or " +
                    "decimal form.";
        }

        // If there's an error, redirect back to the form with the error message
        if(errorMessage != null) {
            // Use session attributes to pass the error message
            request.getSession().setAttribute("errorMessage", errorMessage);
            response.sendRedirect("http://localhost:8080/recipe_form"); // Redirect to the form
            return; // Exit the method
        }

        // If no errors, set response status to 200 OK
        response.setStatus(HttpServletResponse.SC_OK);

        // If no errors, calculate the multiplier to scale quantities
        servingsMultiplier = desiredServings / originalServings;

        // *Parse the ingredient quantities & perform calculations*
        String[] originalIngredientsLines = originalIngredients.split("\n"); // Split by newline
        StringBuilder multipliedIngredientList = new StringBuilder();

        for (String line : originalIngredientsLines) {
            String[] ILParts = parseAndMultiplyIngredientLine(line);
            String multipliedQuantity = ILParts[0];
            String ingredientString = ILParts[1];
            String checkboxHTML =
                    "<input type ='checkbox' name='ingredientCheckbox' value='" + ingredientString +
                            "'> ";
            multipliedIngredientList.append(checkboxHTML).append(multipliedQuantity).append(" ").append(ingredientString).append("<br>");
        }


        // Here we embed an HTML form within the servlet.
        writer.println("<html>");
        writer.println("<head>");
        writer.println("<style>");
        writer.println("body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 20px; }");
        writer.println("h1 { color: #333; }");
        writer.println("h2 { color: #555; }");
        writer.println("h4 { color: #666; }");
        writer.println("div.result { background: white; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); }");
        writer.println("input[type='checkbox'] { margin-right: 10px; }");
        writer.println("input[type='button'] { background-color: #5cb85c; " +
                "color: white; border: none; padding: 10px 15px; border-radius: 4px; cursor: pointer; }");
        writer.println("input[type='button']:hover, input[type='reset']:hover { background-color: #4cae4c; }");
        writer.println("</style>");
        writer.println("</head>");

        writer.println("<body>");
        writer.println("<div class='result'>");
        writer.println("<h1>Celia's Recipe Multiplier App 1.0</h1>");
        writer.println("<h4><i>An app that scales your ingredient list to your desired yield</i></h4>");
        writer.println("<br>");

        writer.println("<h2>Thanks! Recipe and desired yield received.</h2>");
        writer.println("<h4>Original yield: " + originalServings + " servings</h4>");
        writer.println("<h4>Desired yield: " + desiredServings + " servings</h4>");
        writer.println("<h4>Recipe multiplier: " + servingsMultiplier + "</h4>");
        writer.println("<br>");

        writer.println("<h2>Here's the multiplied ingredient list for " + desiredServings + " servings:</h2>");
        writer.println("<h4>" + multipliedIngredientList + "</h4>");

        writer.println("<br>");
        writer.println("<p>");
        writer.println("<h4>*DEVELOPER'S NOTE: Currently, this app only converts numerical " +
                "quantities at the beginnings of lines. It does not convert lines " +
                "like \"A pinch of salt\" (No numerical measurement given). It also does not " +
                "convert secondary quantities in lines like \"1 head of garlic, " +
                "roughly chopped (6-7 tablespoons), divided\" (\"6-7 tablespoons\" will " +
                "not be converted) and \"1 tablespoon plus 1 teaspoon Thai fish sauce\" (\"1 " +
                "teaspoon\" will not be converted). You may want to tweak your ingredients list " +
                "accordingly. We're working on this functionality for future releases.</h4>");
        writer.println("<br>");

        writer.println("<input type='button' value='Multiply another recipe' onclick=\"window.open('http://localhost:8080/recipe_form', '_blank');\" />");
        writer.println("<p>");

        writer.println("</div>"); // Closing the result div
        writer.println("</body>");
        writer.println("</html>");
    }

    /**
     * Parses and multiplies an ingredient line. This method takes an ingredient line, separates
     * the quantity from the unit and ingredient string, converts the quantity to a double, multiplies
     * the quantity by the servingsMultiplier, and returns a new string array with the
     * multiplied quantity and the original unit/ingredient string.
     *
     * Development note: Current logic does not completely convert lines like
     * - "2 pounds chicken thighs, bone-in and skin-on (7-8 thighs)" or "1 head of garlic, peeled
     * and roughly chopped (6 tablespoons), divided" --> quantity in parentheses is not multiplied
     * - "1 tablespoon plus 1 teaspoon Thai fish sauce" --> The secondary quantity (1 teaspoon) is
     * not multiplied.
     * - "A pinch of salt" --> "A" is not recognized as a quantity so it is not multiplied.
     */
    public static String[] parseAndMultiplyIngredientLine(String line) {
        // Configure formats for output quantities
        DecimalFormat wholeFormat = new DecimalFormat("#"); // For whole numbers
        DecimalFormat decimalFormat = new DecimalFormat("#.00");    // For two decimal places

        // Regex translation: Beginning of line(digit + optional fraction OR fraction OR decimal number)+(space)+(any remaining characters at the end of the line)
        String regex = "^(\\d+(\\s+\\d+/\\d+)?|\\d+/\\d+|\\d+\\.\\d+)\\s+(.*)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(line);
        double quantityAsDouble;

        // If a regex match is found
        if (matcher.find()) {
            String quantityString = matcher.group(1).trim(); // Captures the whole quantity
            ///// String fraction = matcher.group(2) != null ? matcher.group(2).trim() : null; // This captures the fraction part (if quantity is a mixed number)
            String ingredientString = matcher.group(3).trim(); // Captures the ingredient string

            // If quantityString is a whole number followed by a fraction (e.g., "1 1/2")
            if (quantityString.contains(" ")) {
                // Separate whole number from fraction
                String[] parts = quantityString.split(" "); // Split using space as delimiter
                double wholeNumber = Double.parseDouble(parts[0]);      // Convert wholeNumber string to double
                String fractionString = parts[1];   //// Same as String variable "fraction"

                double fractionDouble = convertFractionStringToDouble(fractionString);  // Convert fractionString to double

                quantityAsDouble = wholeNumber + fractionDouble;
            // If quantityString is a fraction by itself (e.g., "1/2")
            } else if (!quantityString.contains(" ") && quantityString.contains("/")) {
                quantityAsDouble = convertFractionStringToDouble(quantityString);
            // Else if quantityString is a decimal number (e.g., "1.5") or whole number (e.g., "1")
            } else {
                quantityAsDouble = Double.parseDouble(quantityString);
            }

            // Get the multiplied quantity
            double multipliedQuantity = quantityAsDouble * servingsMultiplier;

            // Format multipliedQuantity into whole or fraction notation
            String formattedQuantity =(multipliedQuantity == Math.floor(multipliedQuantity))
                    ? String.valueOf((int) multipliedQuantity)
                    : convertDecimalToFraction(multipliedQuantity);

            // Construct the new multiplied ingredient line
            return new String[]{formattedQuantity, ingredientString};
        // Else if no regex match found (e.g., no numerical quantities found, as in "A pinch of
            // salt")
        } else {
            // Handle lines without a clear quantity
            return new String[]{"[Not multiplied—no numerical quantity]", line};
        }
    }

    /**
     * This method converts a fraction string (e.g., "1/2") into a double.
     * @param fractionString The fractional number string to parse
     * @return the parsed double value of the fraction
     * @throws IllegalArgumentException if the input string is not a valid fraction format or if
     * the denominator is zero
     */
    public static double convertFractionStringToDouble(String fractionString) throws IllegalArgumentException {
        String[] parts = fractionString.split("/"); // regex split with / as delimiter
        if (parts.length == 2) {
            try {
                // Parse both numerator and denominator as doubles
                double numerator = Double.parseDouble(parts[0]);
                double denominator = Double.parseDouble(parts[1]);
                // Return the fraction in double form
                return numerator / denominator;
            } catch (NumberFormatException e) {
                // Handle invalid input
                throw new IllegalArgumentException("Invalid fraction format: " + fractionString);
            }
        } else {
            // Handle invalid input
            throw new IllegalArgumentException("Invalid fraction format: " + fractionString);
        }
    }

    /**
     * Converts a decimal number into a string in fraction format.
     * @param decimal The decimal number to convert, which can be positive or negative
     * @return a string in fraction format: "numerator/denominator". If the decimal is negative,
     * the fraction will also be negative.
     */
    public static String convertDecimalToFraction(double decimal) {
        // Not needed thanks to client- and server-side input validation but kept here for
        // potential future usage
        // Handle negative decimals
        boolean isNegative = decimal < 0;
        decimal = Math.abs(decimal);

        // Separate the integral and fractional parts
        int integralPart = (int) decimal;
        double fractionalPart = decimal - integralPart;

        // Determine the denominator
        int denominator = 1;
        while (fractionalPart != 0) {
            fractionalPart *= 10;
            denominator *= 10;
            integralPart += (int) fractionalPart;
            fractionalPart -= (int) fractionalPart;
        }

        // Calculate GCD to simplify the fraction
        int gcd = gcd(integralPart, denominator);
        integralPart /= gcd;
        denominator /= gcd;

        // Return the fraction as a string
        return (isNegative ? "-" : "") + integralPart + "/" + denominator;
    }

    /**
     * Helper method to calculate the greatest common divisor (GCD) of two integers using the
     * Euclidean algorithm.
     * @param a the first integer
     * @param b the second integer
     * @return the greatest common divisor of a and b
     */
    private static int gcd(int a, int b) {
        return b == 0 ? a : gcd(b, a % b);
    }
}