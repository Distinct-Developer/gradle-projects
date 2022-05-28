package za.co.bidvestdata.models;

/**
 *
 * @author Boik Mphore
 */
public class Student {

    private int id; //Unique 7 Digit
    private String name;
    private String surname;
    private int age;
    private String curriculum;

    public Student() {
    }

    public Student(Student student) {
        this.id = student.getId();
        this.name = student.getName();
        this.surname = student.getSurname();
        this.age = student.getAge();
        this.curriculum = student.getCurriculum();
    }

    public Student(int id, String name, String surname, int age, String curriculum) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.age = age;
        this.curriculum = curriculum;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getCurriculum() {
        return curriculum;
    }

    public void setCurriculum(String curriculum) {
        this.curriculum = curriculum;
    }

    @Override
    public String toString() {
        return "Student{" + "id=" + id + ", name=" + name + ", surname=" + surname + ", age=" + age + ", curriculum=" + curriculum + '}';
    }

    public String toJson() {
        return "{\"id\":"+ id +",\"name\":\""+ name +"\",\"surname\":\""+ surname +"\",\"age\":"+ age +",\"curriculum\":\""+ curriculum +"\"}";
    }
}
