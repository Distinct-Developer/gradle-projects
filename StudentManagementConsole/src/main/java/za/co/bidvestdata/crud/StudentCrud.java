package za.co.bidvestdata.crud;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import za.co.bidvestdata.models.Student;

/**
 *
 * @author Boik Mphore
 */
public class StudentCrud {

    private static final String JSON_EXTENSION = ".json";
    private static final String STUDENT_FILE_DIR = "students";
    private static final String ACTION_SEARCH = "Search", ACTION_EDIT = "Edit", ACTION_DELETE = "Delete";
    private static final String SEPARATOR = FileSystems.getDefault().getSeparator();
    private final Scanner scan = new Scanner(System.in);
    private FileWriter fileWriter;
    private Path filePath;

    public void addStudent() {
        System.out.println("\n---Add new student---");
        int id = generateStudentId();
        String name = getStudentName();
        String surname = getStudentSurname();
        int age = getStudentAge();
        String curriculum = getStudentCurriculum();

        Student student = new Student(id, name, surname, age, curriculum);
        saveStudentFile(student);
        System.out.println("\nNew student details:");
        printStudentDetails(student);
    }

    public void editStudent() {
        System.out.println("\n---Edit student---");
        final int id = getStudentId(ACTION_EDIT);

        if (id != 0) {
            filePath = getfileByStudentId(id);

            if (Files.exists(filePath)) {
                boolean updated = false;
                Student updatedStudent;

                Student student = getStudentDetailsFromFile(filePath);
                System.out.println("\nCurrent student details:");
                printStudentDetails(student);

                System.out.println("\nWould you like to continue updating student details?");
                char confirmation = getYesOrNoValue();

                if (confirmation == 'Y' || confirmation == 'y') {
                    String name = getStudentName();
                    String surname = getStudentSurname();
                    int age = getStudentAge();
                    String curriculum = getStudentCurriculum();
                    updatedStudent = new Student(id, name, surname, age, curriculum);
                    saveStudentFile(updatedStudent);

                    System.out.println("\nUpdated student details:");
                    printStudentDetails(updatedStudent);
                    updated = true;
                }

                if (updated) {
                    System.out.println("\nStudent details have been updated successfully.");
                } else {
                    System.out.println("\nStudent details remain unchanged.");
                }
            } else {
                System.out.println("\nStudent not found with ID: " + id);
            }
        }
    }

    private char getYesOrNoValue() {
        boolean isNotConfirmed = true;

        System.out.println("\nEnter 'Y' for Yes or 'N' for No");
        String yesOrNoInput = scan.nextLine().trim();
        char confirmation = yesOrNoInput.charAt(0);

        while (isNotConfirmed) {
            switch (confirmation) {
                case 'Y':
                case 'y':
                    isNotConfirmed = false;
                    break;
                case 'N':
                case 'n':
                    isNotConfirmed = false;
                    break;
                default:
                    System.out.println("Invalid input.");
                    System.out.println("\nEnter 'Y' for Yes or 'N' for No");
                    yesOrNoInput = scan.nextLine().trim();
                    confirmation = yesOrNoInput.charAt(0);
                    isNotConfirmed = true;
                    break;
            }
        }

        return yesOrNoInput.charAt(0);
    }

    public void deleteStudent() {
        System.out.println("\n---Delete student---");
        int id = getStudentId(ACTION_DELETE);

        if (id != 0) {
            filePath = getfileByStudentId(id);

            try {
                boolean deleted = Files.deleteIfExists(filePath);

                if (deleted) {
                    System.out.println("\nStudent has been deleted with ID: " + id);
                } else {
                    System.out.println("\nStudent not found with ID: " + id);
                }
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }

    public void searchStudent() {
        System.out.println("\n---Search student---");
        int id = getStudentId(ACTION_SEARCH);

        if (id != 0) {
            filePath = getfileByStudentId(id);

            if (Files.exists(filePath)) {
                Student student = getStudentDetailsFromFile(filePath);
                System.out.println("\nSearched student details:");
                printStudentDetails(student);
            } else {
                System.out.println("\nStudent not found with ID: " + id);
            }
        } else {
            //Print all students
            filePath = getfileByStudentId(id);
            List<Student> studentList = getAllStudentsDetailsFromFile(filePath);

            if (studentList != null && !studentList.isEmpty()) {
                System.out.println("\nAll students details:");
                printAllStudentDetails(studentList);
            }
        }
    }

    private void printStudentDetails(Student student) {
        //Print student details in table format.
        String dataFormat = "%1$-12s %2$-15s %3$-20s %4$-10s %5$-15s\n";
        System.out.format(dataFormat, "STUDENT ID", "NAME", "SURNAME", "AGE", "CURRICULUM");
        System.out.format(dataFormat, student.getId(), student.getName(), student.getSurname(), student.getAge(), student.getCurriculum());
    }

    private void printAllStudentDetails(List<Student> studentList) {
        //Print all student details in table format.
        String dataFormat = "%1$-12s %2$-15s %3$-20s %4$-10s %5$-15s\n";
        System.out.format(dataFormat, "STUDENT ID", "NAME", "SURNAME", "AGE", "CURRICULUM");

        for (Student student : studentList) {
            System.out.format(dataFormat, student.getId(), student.getName(), student.getSurname(), student.getAge(), student.getCurriculum());
        }
    }

    private Student getStudentDetailsFromFile(Path filePath) {
        String[] studentDataArray = null;
        Student student = null;

        try {
            List<String> studentJsonList = Files.readAllLines(filePath);
            for (String studentJson : studentJsonList) {
                studentDataArray = studentJson.replace("{", "").replace("}", "").replaceAll("\"", "").split(",");
            }

            Map<String, String> studentMap = new HashMap<>();
            if (studentDataArray != null && studentDataArray.length != 0) {
                for (String pair : studentDataArray) {
                    String[] keyValue = pair.trim().split(":");
                    studentMap.put(keyValue[0], keyValue[1]);
                }
            }

            student = new Student(Integer.parseInt(studentMap.get("id")),
                    studentMap.get("name"),
                    studentMap.get("surname"),
                    Integer.parseInt(studentMap.get("age")),
                    studentMap.get("curriculum"));
        } catch (IOException e) {
            System.out.println(e);
        }
        return student;
    }

    private List<Student> getAllStudentsDetailsFromFile(Path filePath) {

        List<Student> allStudents = null;

        try {
            List<String> studentFiles = findAllStudentFiles(filePath);
            allStudents = new ArrayList<>();

            for (String studentFile : studentFiles) {
                String[] studentDataArray = null;
                List<String> studentJsonList = Files.readAllLines(Paths.get(studentFile));
                for (String studentJson : studentJsonList) {
                    studentDataArray = studentJson.replace("{", "").replace("}", "").replaceAll("\"", "").split(",");
                }

                Map<String, String> studentMap = new HashMap<>();
                if (studentDataArray != null && studentDataArray.length != 0) {
                    for (String pair : studentDataArray) {
                        String[] keyValue = pair.trim().split(":");
                        studentMap.put(keyValue[0], keyValue[1]);
                    }
                }

                Student student = new Student(Integer.parseInt(studentMap.get("id")),
                        studentMap.get("name"),
                        studentMap.get("surname"),
                        Integer.parseInt(studentMap.get("age")),
                        studentMap.get("curriculum"));

                allStudents.add(student);
            }
        } catch (NoSuchFileException e) {
            System.out.println("\nThere are currently no students enrolled, please add new students first.");
        } catch (IOException e) {
            System.out.println(e);
        }

        return allStudents;
    }

    private List<String> findAllStudentFiles(Path baseDir) throws IOException {
        List<String> fileList = new ArrayList<>();
        FileVisitor<Path> matcherVisitor = new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attribs) throws IOException {
                Path fileLocation = file.toAbsolutePath();
                fileList.add(fileLocation.toString());
                return FileVisitResult.CONTINUE;
            }
        };
        Files.walkFileTree(baseDir, matcherVisitor);
        return fileList;
    }

    private Path getfileByStudentId(int id) {
        String studentFile;
        if (id != 0) {
            String studentIdString = String.valueOf(id);
            String fileDir = getStudentDirPrefix(studentIdString);
            studentFile = STUDENT_FILE_DIR.concat(SEPARATOR).concat(fileDir).concat(studentIdString).concat(JSON_EXTENSION);
        } else {
            studentFile = STUDENT_FILE_DIR; //Top level students directory.
        }

        return Paths.get(studentFile);
    }

    private int generateStudentId() {
        String idString = String.valueOf(System.currentTimeMillis() & 0xfffffff); //Generate unique id.
        return Integer.parseInt(idString.substring(0, 7)); //Use first 7 digits of unique id.
    }

    private int getStudentId(String action) {
        int id = 0;
        String idInput = "";

        if (action.equals(ACTION_SEARCH)) {
            System.out.println("\nEnter student ID or leave blank to display all students:");
        } else {
            System.out.println("\nEnter student ID:");
        }

        idInput = scan.nextLine().trim();

        if (action.equals(ACTION_SEARCH) && idInput.isEmpty()) {
            return id;
        }

        if (idInput.length() < 7 || idInput.length() > 7) {
            System.out.println("\nStudent ID must be 7 digits long.");
            id = getStudentId(action); //It is ok to recurse, we are handling incorrect user input.
        } else if (idInput.isEmpty()) {
            id = 0;
        } else {
            try {
                id = Integer.parseInt(idInput);
            } catch (NumberFormatException e) {
                System.out.println("\nOnly numbers are allowed.");
                id = getStudentId(action); //It is ok to recurse, we are handling incorrect user input.
            }
        }
        return id;
    }

    private String getStudentName() {
        System.out.println("\nEnter student name:");
        String name = scan.nextLine().trim();

        if (name.isEmpty()) {
            System.out.println("\nStudent name cannot be blank");
            name = getStudentName(); //It is ok to recurse, we are handling incorrect user input.
        }
        return name;
    }

    private String getStudentSurname() {
        System.out.println("\nEnter student surname:");
        String surname = scan.nextLine().trim();

        if (surname.isEmpty()) {
            System.out.println("\nStudent surname cannot be blank");
            surname = getStudentSurname(); //It is ok to recurse, we are handling incorrect user input.
        }

        return surname;
    }

    private int getStudentAge() {
        int age = 0;
        System.out.println("\nEnter student age:");
        String ageInput = scan.nextLine().trim();

        try {
            age = Integer.parseInt(ageInput);
        } catch (NumberFormatException e) {
            System.out.println("\nOnly numbers are allowed.");
            age = getStudentAge(); //It is ok to recurse, we are handling incorrect user input.
        }

        if (age <= 0) {
            System.out.println("\nStudent age must be greater than 0");
            age = getStudentAge(); //It is ok to recurse, we are handling incorrect user input.
        }

        return age;
    }

    private String getStudentCurriculum() {
        System.out.println("\nEnter student curriculum:");
        String curriculum = scan.nextLine().trim();

        if (curriculum.isEmpty()) {
            System.out.println("\nStudent curriculum cannot be blank");
            curriculum = getStudentCurriculum();
        }

        return curriculum;
    }

    private void saveStudentFile(Student student) {
        try {

            String studentId = String.valueOf(student.getId());
            String subDirPrefix = getStudentDirPrefix(studentId);
            String studentFileDir = STUDENT_FILE_DIR.concat(SEPARATOR).concat(subDirPrefix);

            filePath = Paths.get(studentFileDir);
            Files.createDirectories(filePath);

            String studentFile = studentFileDir.concat(studentId).concat(JSON_EXTENSION); //Example: students/1/2/1234567.json)
            String studentFileLocation = filePath.toAbsolutePath().toString().concat(studentFile);

            System.out.println("Student details saved at:\n" + studentFileLocation);

            fileWriter = new FileWriter(studentFile);
            fileWriter.write(student.toJson());
        } catch (IOException e) {
            System.out.println("IOException " + e);
        } finally {
            try {
                fileWriter.close();
            } catch (IOException e) {
                System.out.println("IOException " + e);
            }
        }
    }

    private String getStudentDirPrefix(String studentId) {
        String subDirPrefix1 = String.valueOf(studentId.charAt(0)); //If studentId = 7654321, then subDirPrefix1 = 7
        String subDirPrefix2 = String.valueOf(studentId.charAt(1)); //If studentId = 7654321, then subDirPrefix2 = 6
        return subDirPrefix1.concat(SEPARATOR).concat(subDirPrefix2).concat(SEPARATOR); //return 7/6
    }
}
