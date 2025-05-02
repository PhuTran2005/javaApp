package com.example.coursemanagement.Controllers.Client;

import com.example.coursemanagement.Controllers.Client.CartController.CourseCartOfInstructorController;
import com.example.coursemanagement.Models.User;
import com.example.coursemanagement.Repository.CoursesRepository;
import com.example.coursemanagement.Dto.CourseDetailDTO;
import com.example.coursemanagement.Utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.layout.FlowPane;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import java.util.List;

public class StudentController {

    @FXML
    private FlowPane courseFlowPane; // Tham chiếu đến FlowPane trong FXML

    private CoursesRepository courseRepository = new CoursesRepository(); // Lớp repository chứa phương thức getCoursesByInstructor

    // Giả sử bạn có instructorId
    private int instructorId ; // Thay đổi theo thực tế

    // Phương thức để thêm các khóa học vào FlowPane
    public void addCoursesToFlowPane() {
        List<CourseDetailDTO> courses = courseRepository.getCoursesByInstructor(instructorId); // Lấy danh sách khóa học từ repository
        System.out.println("Số khóa học tìm thấy: " + (courses != null ? courses.size() : 0));
        // Kiểm tra xem danh sách khóa học có tồn tại hay không
        if (courses == null || courses.isEmpty()) {
            System.out.println("Không có khóa học nào được tìm thấy.");
            return; // Không làm gì nếu không có khóa học
        }

        for (CourseDetailDTO courseDetail : courses) {
            try {
                // Tải FXML cho CourseCartOfInstructorController
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Client/StudentManagement/CourseCartOfInstructor.fxml"));
                Parent root = loader.load();

                // Lấy controller của FXML
                CourseCartOfInstructorController controller = loader.getController();

                int studentCount = courseRepository.getStudentCountByCourseId(courseDetail.getCourse().getCourseId());

                controller.setData(
                        courseDetail.getCourse().getCourseName(),
                        courseDetail.getInstructor().getFullname(),
                        studentCount,
                        courseDetail.getCourse().getCourseId()
                );

                // Thêm vào FlowPane
                courseFlowPane.getChildren().add(root);

                System.out.println("Thêm khóa học vào FlowPane: " + courseDetail.getCourse().getCourseName());
            } catch (Exception e) {
                System.out.println("Lỗi khi tải FXML: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // Phương thức gọi khi giao diện được khởi tạo
    @FXML
    public void initialize() {
        // Lấy user từ SessionManager
        User user = SessionManager.getInstance().getUser();

        if (user != null) {
            instructorId = user.getUserId();
            System.out.println("Instructor đang login có ID: " + instructorId);
            addCoursesToFlowPane(); // Gọi sau khi có ID
        } else {
            System.out.println("Không tìm thấy thông tin người dùng trong session.");
        }
    }
}
