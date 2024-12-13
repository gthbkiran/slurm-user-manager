package com.example.shellscript.controller;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@RestController
public class SlurmController {

    private final MeterRegistry meterRegistry;
    @Autowired
    public SlurmController(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }



    @RequestMapping("/login")
    public ModelAndView index()
    {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        return modelAndView;
    }
    @RequestMapping("/layout")
    public ModelAndView layout()
    {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("layout");
        return modelAndView;
    }
    @RequestMapping("/create")
    public ModelAndView createAccount() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("pageTitle", "Create Account");
        // Add the content fragment to be injected into the layout
        modelAndView.addObject("body", "create"); // Refers to a fragment in create.html
        modelAndView.setViewName("layout");
        return modelAndView; // Renders the layout template
    }

    @RequestMapping("/update")
    public ModelAndView updateAccount() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("layout");
        modelAndView.addObject("pageTitle", "Update Account");
        modelAndView.addObject("body", "create :: content"); // Reuse the same fragment for simplicity
        return modelAndView;
    }

    @RequestMapping("/delete")
    public ModelAndView deleteAccount() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("custo");
        modelAndView.addObject("pageTitle", "Delete Account");
        modelAndView.addObject("body", "create :: content"); // Reuse the same fragment for simplicity
        return modelAndView;
    }
    @RequestMapping("/custo")
    public ModelAndView mainPage()
    {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("custo");
        return modelAndView;
    }
    @RequestMapping("/")
    public ModelAndView homePage()
    {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        return modelAndView;
    }
    @PostMapping("/runshellscript")
    public ModelAndView executeScript(@RequestParam("username") String param1,
                                      @RequestParam("password") String param2, Model model) throws IOException, InterruptedException {
		 /*try {
			 // Path to your shell script
            String scriptPath = "C:/Users/91911/study/Shell_Script/first.sh";

            // Execute shell script using PowerShell
            ProcessBuilder pb = new ProcessBuilder("powershell.exe", "-Command", scriptPath);
            pb.redirectErrorStream(true); // Redirect error stream to output stream
            Process process = pb.start();

            // Read script output
            StringBuilder output = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
			boolean checkdata = reader.ready();
            while ((line = reader.readLine()) != null) {
				System.out.println("Output read by Processbuilder");
                output.append(line).append("\n");
				output.append("Kiran").append("\n");

            }

            // Wait for the script to finish
            int exitCode = process.waitFor();
			// Check if there were any errors
            if (exitCode != 0) {
                // Read error stream
                try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                    String errorLine;
                    output.append("\nErrors:\n");
                    while ((errorLine = errorReader.readLine()) != null) {
                        output.append(errorLine).append("\n");
                    }
                }
            }

            return "Script executed with exit code: " + exitCode + "\nOutput:\n" + output.toString()+" checkdata:"+checkdata;
        } catch (Exception e) {
            return "Error executing script: " + e.getMessage();
        }
		//return "Shell Script has been executed successfully";

		  */
        ModelAndView modelAndView = new ModelAndView();
        StringBuilder outputLogs = new StringBuilder();
        StringBuilder errorLogs = new StringBuilder();
        meterRegistry.counter("spring_shell_script_hits").increment();

        try {
            // Path to your shell script
            String scriptPath = "/home/kirandcops/js_automation/first.sh"; // Update with the correct path to your shell script
            String[] command = {scriptPath, param1, param2};

            // Create a ProcessBuilder to execute the shell script
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true);  // Merge stdout and stderr into a single stream

            //processBuilder.inheritIO();  // Optionally inherit the I/O from the parent process

            // Start the process (run the shell script)
            Process process = processBuilder.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    outputLogs.append(line).append("\n");
                }
            }

            // Wait for the process to complete and get the exit code
            int exitCode = process.waitFor();
            modelAndView.setViewName("layout");
            modelAndView.addObject("body", "result");
            modelAndView.addObject("param1",param1);
            modelAndView.addObject("param2",param2);

            if (exitCode == 0) {
                meterRegistry.counter("spring_shell_script_success").increment();
                modelAndView.addObject("flag", 1);
                modelAndView.addObject("message", "Shell script executed successfully.");
            } else {
                meterRegistry.counter("spring_shell_script_fail").increment();
                modelAndView.addObject("flag", 0);
                modelAndView.addObject("message", "Error occurred while executing the script.");
            }

        } catch (Exception e) {
            // In case of an error, display the exception message
            meterRegistry.counter("spring_shell_script_exception").increment();
            modelAndView.addObject("message", "Exception: " + e.getMessage());
            errorLogs.append(e.getMessage()).append("\n");
        }
        modelAndView.addObject("outputLogs", outputLogs.toString());
        modelAndView.addObject("errorLogs", errorLogs.toString());
        return modelAndView;  // Return the result.html page (result.html)
    }
}
