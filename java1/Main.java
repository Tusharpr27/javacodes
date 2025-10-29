import java.sql.*;
import java.util.*;

// ===== MODEL =====
class Student {
    private int studentId;
    private String name;
    private String department;
    private double marks;

    public Student(int studentId, String name, String department, double marks) {
        this.studentId = studentId;
        this.name = name;
        this.department = department;
        this.marks = marks;
    }

    public int getStudentId() { return studentId; }
    public String getName() { return name; }
    public String getDepartment() { return department; }
    public double getMarks() { return marks; }

    @Override
    public String toString() {
        return "StudentID: " + studentId + ", Name: " + name + ", Department: " + department + ", Marks: " + marks;
    }
}

// ===== CONTROLLER =====
class StudentDAO {
    private Connection conn;

    public StudentDAO() {
        try {
            // Load SQLite Driver
            Class.forName("org.sqlite.JDBC");
            // Database will be created locally in your project folder
            conn = DriverManager.getConnection("jdbc:sqlite:students.db");
            createTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS students (" +
                     "studentId INTEGER PRIMARY KEY, " +
                     "name TEXT, " +
                     "department TEXT, " +
                     "marks REAL)";
        try (Statement st = conn.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addStudent(Student s) {
        String sql = "INSERT INTO students (studentId, name, department, marks) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, s.getStudentId());
            ps.setString(2, s.getName());
            ps.setString(3, s.getDepartment());
            ps.setDouble(4, s.getMarks());
            ps.executeUpdate();
            System.out.println("✅ Student added successfully!");
        } catch (SQLException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Student(
                        rs.getInt("studentId"),
                        rs.getString("name"),
                        rs.getString("department"),
                        rs.getDouble("marks")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void updateStudent(int id, double marks) {
        String sql = "UPDATE students SET marks=? WHERE studentId=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, marks);
            ps.setInt(2, id);
            int rows = ps.executeUpdate();
            if (rows > 0)
                System.out.println("✅ Student updated successfully!");
            else
                System.out.println("⚠️ Student not found!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteStudent(int id) {
        String sql = "DELETE FROM students WHERE studentId=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            if (rows > 0)
                System.out.println("✅ Student deleted successfully!");
            else
                System.out.println("⚠️ Student not found!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

// ===== VIEW (Main Program) =====
public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        StudentDAO dao = new StudentDAO();

        while (true) {
            System.out.println("\n===== STUDENT MANAGEMENT SYSTEM =====");
            System.out.println("1. Add Student");
            System.out.println("2. View All Students");
            System.out.println("3. Update Student Marks");
            System.out.println("4. Delete Student");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            int choice = sc.nextInt();

            switch (choice) {
                case 1 -> {
                    System.out.print("Enter Student ID: ");
                    int id = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Enter Name: ");
                    String name = sc.nextLine();
                    System.out.print("Enter Department: ");
                    String dept = sc.nextLine();
                    System.out.print("Enter Marks: ");
                    double marks = sc.nextDouble();
                    dao.addStudent(new Student(id, name, dept, marks));
                }

                case 2 -> {
                    var list = dao.getAllStudents();
                    if (list.isEmpty())
                        System.out.println("⚠️ No students found!");
                    else
                        list.forEach(System.out::println);
                }

                case 3 -> {
                    System.out.print("Enter Student ID to update: ");
                    int id = sc.nextInt();
                    System.out.print("Enter new Marks: ");
                    double marks = sc.nextDouble();
                    dao.updateStudent(id, marks);
                }

                case 4 -> {
                    System.out.print("Enter Student ID to delete: ");
                    int id = sc.nextInt();
                    dao.deleteStudent(id);
                }

                case 5 -> {
                    System.out.println("👋 Exiting...");
                    return;
                }

                default -> System.out.println("❌ Invalid choice! Try again.");
            }
        }
    }
}
