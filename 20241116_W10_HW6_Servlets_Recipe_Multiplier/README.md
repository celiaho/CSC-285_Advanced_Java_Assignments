To run the server:

mvn jetty:run


# Jetty Web Application Template

This application was created with a template for creating and running servlets
via the embedded Jetty engine.


# Development Notes for Future Versions

1. If (originalYield != desiredYield), no need to convert anything--just return the original input
2. Include the option to add recipe instructions & original source so app can output a complete recipe
3. Recognize and convert units of measurement (e.g., "16 ounces" would be displayed as "1 cup")
4. Recognize units of measurement and pluralize/singularize them appropriately, including colloquial units e.g. "A pinch of salt", "A splash of tonic"
5. Recognize and scale secondary measurements in lines e.g. "2 pounds chicken thighs, bone-in and skin-on (7-8 thighs)", "1 head of garlic, peeled and roughly chopped (6 tablespoons), divided", "1 tablespoon plus 1 teaspoon Thai fish sauce"
6. Meal costing functionality: Add a field for ingredient cost to each line of the scaled ingredient list. Output total cost of ingredients, cost per serving, and maybe the most/least expensive ingredients.
7. Have a way to save/export data for user's reference.


# Notes & Resources For This Project

* String split() Method: https://www.w3schools.com/java/ref_string_split.asp
* Regular Expressions:
  * https://regexr.com/
  * https://regex101.com/ Regex Quick Reference
  * https://www.w3schools.com/java/java_regex.asp
  * https://www.tutorialspoint.com/javaregex/javaregex_capturing_groups.htm (has mistakes)
  * https://www.geeksforgeeks.org/regular-expressions-in-java/ (has mistakes)