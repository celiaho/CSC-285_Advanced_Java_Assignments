package edu.bhcc;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Recipe form with HTML.
 */
public class RecipeForm extends HttpServlet {

    /**
     * Process HTTP GET Request.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);

        PrintWriter writer = response.getWriter();

        // Here we embed an HTML form within the servlet.
        writer.println("<html>");
        writer.println("<head>");
        writer.println("<style>");
        writer.println("body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 20px; }");
        writer.println("h1 { color: #333; }");
        writer.println("h2 { color: #555; }");
        writer.println("h4 { color: #666; }");
        writer.println("form { background: white; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); }");
        writer.println("textarea { width: 100%; padding: 10px; border: 1px solid #ccc; border-radius: 4px; margin-bottom: 10px;}");
        //////////////////
        writer.println("input[type='text'] { width: calc(100% - 22px); padding: 10px; " +
                "border: " +
                "1px solid #ccc; border-radius: 4px; margin-bottom: 10px; }");
        writer.println("input[type='submit'], input[type='reset'] { background-color: #5cb85c; color: white; border: none; padding: 10px 15px; border-radius: 4px; cursor: pointer; }");
        writer.println("input[type='submit']:hover, input[type='reset']:hover { background-color: #4cae4c; }");
        writer.println("</style>");
        writer.println("</head>");

        writer.println("<body>");

        writer.println("<form id='recipe_form' action='post_multiplied_recipe' method='POST'>");

        writer.println("<h1>Celia's Recipe Multiplier App 1.0</h1>");
        writer.println("<h4><i>An app that scales your ingredient list to your desired yield</i></h4>");

        // Retrieve the error message from the session
        String errorMessage = (String) request.getSession().getAttribute("errorMessage");
        if (errorMessage != null) {
            // Display the error message to the user
            writer.println("<div class='error' style='color: red;'><strong>" + errorMessage +
                    "</div>");
            // Remove the error message from the session after displaying it
            request.getSession().removeAttribute("errorMessage");
        }

        writer.println("<br>");
        writer.println("<h2>Original Ingredient List</h2>");
        writer.println("<h4>Paste the ingredient list from your original recipe here:</h4>");
        writer.println("<textarea id='originalIngredients' rows='10' name='originalIngredients' " +
                "required></textarea>");
        // Upon Submit, browser error pop-up (from input attribute "required") will show,
        // preventing this message from showing (unless input was a space).
        writer.println("<span id='originalIngredientsError' style='color: red;'></span>");

        writer.println("<h2>Original Yield</h2>");
        writer.println("<h4>Enter the number of servings your original recipe yields (whole and decimal numbers only):</h4>");
        writer.println("<input type='text' id='originalServingsString' name='originalServingsString' required/>");
        writer.println("<span id='originalServingsError' style='color: red;'></span>");
        writer.println("<h2>Desired Yield</h2>");
        writer.println("<h4>Enter the number of servings you want (whole and decimal " +
                "numbers only)" +
                ":</h4>");
        writer.println("<input type='text' id='desiredServingsString' " +
                "name='desiredServingsString' " +
                "required/>");
        writer.println("<span id='desiredServingsError' style='color: red;'></span>");
        writer.println("<br>");
        writer.println("<p>");

        writer.println("<input type='submit' value='Submit Form'/> <input type='reset' value='Clear Form'>");
        writer.println("<br>");
        writer.println("<p>");

        writer.println("<h5>*DEVELOPER'S NOTE: Currently, this app only converts numerical " +
                "amounts at the beginnings of lines. It does not convert lines " +
                "like \"A pinch of salt\" (No numerical measurement given) and " +
                "it does not convert secondary amounts in lines like \"1 head of garlic, " +
                "roughly chopped (6-7 tablespoons), divided\" (\"6-7 tablespoons\" will " +
                "not be converted) and \"1 tablespoon plus  1 teaspoon Thai fish sauce\" (\"1 " +
                "teaspoon\" will not be converted). You may want to tweak your ingredients " +
                "list accordingly. We're working on this functionality for future releases.</h5>");

        writer.println("</form>");

        // JavaScript for client-side validation
        writer.println("<script>");
        writer.println("document.getElementById('recipe_form').onsubmit = function() {");   // Event listener
        writer.println("const originalIngredients = document.getElementById('originalIngredients').value.trim();");
        writer.println("const originalServingsString = parseFloat(document.getElementById('originalServingsString').value);");
        writer.println("const desiredServingsString = parseFloat(document.getElementById('desiredServingsString').value);");

        // Validate that the ingredient list is not empty
        writer.println("if (originalIngredients === '') {");
        writer.println("document.getElementById('originalIngredientsError').textContent = 'Please" +
                " enter your ingredient list.';");
        writer.println("return false; // Prevent form submission");
        writer.println("} else {");
        // Clear the error message if validation condition is not met
        writer.println("document.getElementById('originalIngredientsError').textContent = '';");
        writer.println("}");

        // Validate original servings is a positive number
        writer.println("if (isNaN(originalServingsString) || originalServingsString <= 0 || desiredServingsString == NaN) {");
        writer.println("document.getElementById('originalServingsError').textContent = 'Please enter a valid positive number for original servings.';");
        writer.println("return false; // Prevent form submission");
        writer.println("} else {");
        // Clear the error message if validation condition is not met
        writer.println("document.getElementById('originalServingsError').textContent = '';");
        writer.println("}");
        writer.println("}");

        // Validate desired servings is a positive number
        writer.println("if (isNaN(desiredServingsString) || desiredServingsString <= 0 || " +
                "desiredServingsString == NaN) {");
        writer.println("document.getElementById('desiredServingsError').textContent = 'Please " +
                "enter a valid positive number for original servings.';");
        writer.println("return false; // Prevent form submission");
        writer.println("} else {");
        // Clear the error message if validation condition is not met
        writer.println("document.getElementById('desiredServingsError').textContent = '';");
        writer.println("}");

        writer.println("return true; // Allow form submission");
        writer.println("};");
        writer.println("</script>");

        writer.println("</body>");
        writer.println("</html>");
    }
}