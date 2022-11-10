package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import structures.Queue;

public class ProcessNameQueueGenerator {

    private static String defaultFilePath = "assets/Examples of names.txt";

    public static Queue<String> get(String filePath) {
        try {
            Queue<String> result = new Queue<>();

            File source = new File(filePath);
            Scanner scanner = new Scanner(source);

            while (scanner.hasNextLine()) {
                String name = scanner.nextLine();

                if (!name.isBlank()) {
                    result.push(name);
                }
                
            }

            scanner.close();

            return result;
        } catch (FileNotFoundException e) {
            return new Queue<>();
        }
    }

    public static Queue<String> get() {
        return get(defaultFilePath);
    }
}
