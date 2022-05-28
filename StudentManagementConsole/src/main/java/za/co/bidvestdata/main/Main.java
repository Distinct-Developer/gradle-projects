package za.co.bidvestdata.main;

import java.util.Scanner;
import za.co.bidvestdata.crud.StudentCrud;

/**
 *
 * @author Boik Mphore
 */
public class Main {

    private static Scanner scan;

    public static void main(String[] args) {
        scan = new Scanner(System.in);
        mainMenu();
    }

    public static void mainMenu() {

        int menuItem = 0;
        final int quit = 5;

        StudentCrud crud = new StudentCrud();

        menuItem = getUserInput();

        while (menuItem != quit) {
            switch (menuItem) {
                case 1:
                    crud.addStudent(); //Add student
                    break;
                case 2:
                    crud.editStudent(); //Edit student
                    break;
                case 3:
                    crud.deleteStudent(); //Delete student
                    break;
                case 4:
                    crud.searchStudent(); //Search student
                    break;
                default:
                    System.out.println("\nInvalid selection.");
                    break;
            }
            menuItem = getUserInput();
        }
        System.out.println("Exiting application.");
        System.exit(0);
    }

    private static int getUserInput() {
        int userInput = 0;

        System.out.println("\nEnter a menu option (1, 2, 3, 4, or 5)\n\n"
                + "1. Add student\n"
                + "2. Edit student\n"
                + "3. Delete student\n"
                + "4. Search Student\n"
                + "5. Quit\n");

        String option = scan.nextLine();

        try {
            userInput = Integer.parseInt(option);
        } catch (NumberFormatException e) {
            System.out.println("\nOnly numbers are allowed.");
            userInput = getUserInput(); //It is ok to recurse, we are handling incorrect user input.
        }
        return userInput;
    }
}
