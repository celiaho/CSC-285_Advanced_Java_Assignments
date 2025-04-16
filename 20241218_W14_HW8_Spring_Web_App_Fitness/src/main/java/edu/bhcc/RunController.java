package edu.bhcc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The `RunController` class handles incoming HTTP requests related to running records.
 * It interacts with the `RunRepository` to manage data persistence and retrieval.
 */
@Controller
public class RunController {
    /**
     * Injected instance of the `RunRepository` used for interacting with Run data.
     */
    private RunRepository repo;

    /**
     * Create Run Controller.
     * Constructor to autowire the `RunRepository` dependency.
     *
     * @param repo The RunRepository to inject.
     */
    @Autowired
    public RunController(RunRepository repo) {
        this.repo = repo;
    }

    /**
     * Handles the root path ("/"). Retrieves all runs from the repository, packs them
     * into the model, and returns the "index" view.
     *
     * @param model The model object to add data to for the view.
     * @return The name of the view to render ("index").
     */
    @GetMapping("/")
    public String index(Model model) {
        List<Run> runList = getAllRuns();
        // Ensure runList is not null to avoid NPE
        if (runList == null) {
            runList = new ArrayList<>();
        }
        // "Pack" the model with data items to send to the webpage
            // (key, value) pair: ("label for data", data items)
        model.addAttribute("run_list", runList);
        return "index";
    }

    /**
     * Handles the "/add_run" path. Takes route, miles, and date as form parameters,
     * validates the miles (must be positive), creates a new Run object, saves it to the
     * repository, retrieves the updated run list, sets toast message and total miles,
     * and returns the "index" view.
     *
     * @param route The route of the run (from form parameter).
     * @param miles The distance of the run in miles (from form parameter).
     * @param date The date of the run (from form parameter).
     * @param model The model object to add data to for the view.
     * @return The name of the view to render ("index").
     */
    @GetMapping("/add_run")
    public String addRun(String route, Double miles, String date, Model model) {
        if (route == null || route.length() == 0) {
            route = "--------";
        }
        if (miles == null) {
            miles = 0.00;
        }
        // This validation code does not work and I can't figure out why.
        // A negative number in the miles field redirects to a Whitelabel Error Page: "There was an unexpected error (type=Internal Server Error, status=500)."
        if (miles < 0) {
            model.addAttribute("toast", "Error: Enter a positive number for miles.");
            model.addAttribute("route", route);
            model.addAttribute("date", date);
            return "index";
        }
        if (date == null || date.length() == 0) {
            date = "--------";
        }

        Run run = new Run(route, miles, date);
        repo.save(run);
        List<Run> runList = getAllRuns();
        model.addAttribute("toast", "New record added: " + route + ".");
        model.addAttribute("run_list", runList);
        model.addAttribute("total_miles", getTotalMiles(runList));
        return "index";
    }

    /*****
    // Professor's method of using Integer vs. Long for id does not return Optional<Run> object
    @GetMapping("/delete_run")
    public String deleteRun(Integer id, Model model) {
        Run run = this.repo.findById(id);
        if (run != null) {
            model.addAttribute("toast", "Deleted record: " + run.getRoute() + ".");
            this.repo.delete(run);
        } else {
            model.addAttribute("toast", "Could not delete record with ID: " + id + ".");
        }
        
        List<Run> runList = getAllRuns();
        model.addAttribute("run_list", runList);
        return "index";
    }
    */

    /**
     * Handles the "/delete_run" path. Takes the ID of the run to delete as a path
     * variable, retrieves the Run object from the repository using the ID, deletes
     * the run if found, sets toast message and retrieves the updated run list, and
     * returns the "index" view.
     *
     * @param id The ID of the run to delete.
     * @param model The model object to add data to for the view.
     * @return The name of the view to render ("index").
     */
    @GetMapping("/delete_run")
    public String deleteRun(Long id, Model model) {
        Optional<Run> optionalRun = this.repo.findById(id);
        if (optionalRun.isPresent()) {
            Run run = optionalRun.get();
            model.addAttribute("toast", "Deleted record: " + run.getRoute() + ".");
            this.repo.delete(run);
        } else {
            model.addAttribute("toast", "Could not delete record with ID: " + id + ".");
        }

        List<Run> runList = getAllRuns();
        model.addAttribute("run_list", runList);
        model.addAttribute("total_miles", getTotalMiles(runList));
        return "index";
    }

    // Private helper method
    private List<Run> getAllRuns() {
        Iterable<Run> runIter = repo.findAll();
        List<Run> runList = new ArrayList<>();
        for (Run currentRun : runIter) {
            runList.add(currentRun);
        }
        return runList;
    }

    /**
     * Calculates the total distance (in miles) of all runs in the provided list.
     * Uses Java Streams to efficiently map each Run object to its miles value and sum them.
     *
     * @param runList The list of Run objects to calculate the total miles for.
     * @return The total distance (in miles) of all runs in the list.
     */
    public double getTotalMiles(List<Run> runList) {
        return runList.stream().mapToDouble(Run::getMiles).sum();
    }

    /**
     * Handles the "/runs-by-mileage" path. Takes optional minimum and maximum miles
     * as request parameters. Retrieves all runs from the repository, filters them based
     * on the provided parameters (if any), adds the filtered list to the model, and returns
     * the "runs-by-mileage" view.
     *
     * @param minMiles The minimum distance to filter runs by (optional).
     * @param maxMiles The maximum distance to filter runs by (optional).
     * @param model The model object to add data to for the view.
     * @return The name of the view to render ("runs-by-mileage").
     */
    // For future iterations
    @GetMapping("/runs-by-mileage")
    public String getRunsByMileage(@RequestParam(required = false) Double minMiles,
                                   @RequestParam(required = false) Double maxMiles,
                                   Model model) {
        List<Run> runsByMileage = (List<Run>) repo.findAll();

        if (minMiles != null) {
            runsByMileage = runsByMileage.stream()
                    .filter(run -> run.getMiles() >= minMiles)
                    .collect(Collectors.toList());
        }
        if (maxMiles != null) {
            runsByMileage = runsByMileage.stream()
                    .filter(run -> run.getMiles() <= maxMiles)
                    .collect(Collectors.toList());
        }

        model.addAttribute("runsByMileage", runsByMileage);
        return "runs-by-mileage";
    }

    /**
     * Handles the "/test-db" path, used for testing database connection.
     * Retrieves all runs from the repository and checks if there is any data.
     * If data exists, returns a message indicating successful connection and the data.
     * Otherwise, returns a message indicating no data found.
     * This method uses @ResponseBody to indicate the response data is the body itself.
     *
     * @return A message indicating database connection status and potentially data found.
     */
    // For database testing
    @GetMapping("/test-db")
    @ResponseBody
    public String testDB() {
        Iterable<Run> runIter = repo.findAll();
        if (!runIter.iterator().hasNext()) {
            return "No data found in database.";
        }
        return "Database connection is working. Found data: " + runIter.toString();
        }
    }