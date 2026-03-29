import java.io.File;

/**
 * Launcher class to satisfy the requirement of running
 * "javac Main.java" and "java Main" from the root terminal.
 * This acts as a wrapper that automatically builds and runs the layered ERP
 * application.
 */
public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("[Launcher] Building Retail ERP Project...");
            File workingDir = new File(System.getProperty("user.dir"));

            // Compile the whole project from the src directory
            ProcessBuilder pbCompile = new ProcessBuilder(
                    "cmd", "/c",
                    "javac -d bin -cp \"bin;lib\\*\" -sourcepath src src\\com\\retailerp\\Main.java src\\com\\retailerp\\util\\EventBus.java");
            pbCompile.directory(workingDir);
            pbCompile.inheritIO(); // Pipe errors directly to terminal
            Process compileProcess = pbCompile.start();
            int compileCode = compileProcess.waitFor();

            if (compileCode == 0) {
                System.out.println("[Launcher] Compilation successful. Starting Application...");
                // Start the main JFrame application
                ProcessBuilder pbRun = new ProcessBuilder(
                        "cmd", "/c", "java -cp \"bin;lib\\*\" com.retailerp.Main");
                pbRun.directory(workingDir);
                pbRun.inheritIO();
                Process runProcess = pbRun.start();
                runProcess.waitFor(); // Keep the terminal waiting until the app is closed
            } else {
                System.err.println("[Launcher] Compilation failed. Please check the errors above.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
