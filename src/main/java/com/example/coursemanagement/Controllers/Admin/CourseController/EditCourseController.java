package com.example.coursemanagement.Controllers.Admin.CourseController;

import com.example.coursemanagement.Dto.CourseDetailDTO;
import com.example.coursemanagement.Models.Category;
import com.example.coursemanagement.Models.Course;
import com.example.coursemanagement.Models.Instructor;
import com.example.coursemanagement.Service.CategoriesService;
import com.example.coursemanagement.Service.CourseService;
import com.example.coursemanagement.Service.InstructorService;
import com.example.coursemanagement.Service.LogService;
import com.example.coursemanagement.Utils.Alerts;
import com.example.coursemanagement.Utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class EditCourseController {
    private final LogService logService = new LogService();

    // Validation constants
    private static final int MIN_COURSE_NAME_LENGTH = 3;
    private static final int MAX_COURSE_NAME_LENGTH = 100;
    private static final int MIN_DESCRIPTION_LENGTH = 10;
    private static final int MAX_DESCRIPTION_LENGTH = 1000;
    private static final double MIN_PRICE = 0.0;
    private static final double MAX_PRICE = 999999999.99;
    private static final int MIN_COURSE_DURATION_DAYS = 1;
    private static final int MAX_COURSE_DURATION_DAYS = 365;
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    // Patterns for validation
    private static final Pattern PRICE_PATTERN = Pattern.compile("^\\d{1,9}(\\.\\d{1,2})?$");
    private static final Pattern COURSE_NAME_PATTERN = Pattern.compile("^[\\p{L}\\p{N}\\s\\-_.,()]+$");
    private static final String[] ALLOWED_IMAGE_EXTENSIONS = {".png", ".jpg", ".jpeg", ".gif"};

    @FXML
    public TextField courseNameField;
    @FXML
    public ChoiceBox<Instructor> instructorField;
    @FXML
    public ChoiceBox<Category> categoryField;
    @FXML
    public TextField priceField;
    @FXML
    public TextArea descriptionField;
    @FXML
    public Button saveButton;
    @FXML
    public Button cancelButton;
    @FXML
    public DatePicker startDatePicker;
    @FXML
    public DatePicker endDatePicker;
    @FXML
    public ImageView thumbnailImageView;

    private final CategoriesService categoriesService = new CategoriesService();
    private final InstructorService instructorService = new InstructorService();
    private final CourseService courseService = new CourseService();
    private final Alerts alerts = new Alerts();

    private boolean courseAdded = false;
    private CourseDetailDTO currCourse;
    private CourseManagementController courseManagementController;
    private String thumbnailField = "";
    private LocalDate originalStartDate; // Store original start date for validation

    public boolean isCourseAdded() {
        return courseAdded;
    }

    public void setCourseManagementController(CourseManagementController controller) {
        this.courseManagementController = controller;
    }

    @FXML
    public void initialize() {
        setupFieldValidation();
        if (currCourse != null) {
            initDataFromCurrCourse(currCourse);
        }
    }

    /**
     * Setup real-time validation for input fields
     */
    private void setupFieldValidation() {
        // Course name validation
        courseNameField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.length() > MAX_COURSE_NAME_LENGTH) {
                courseNameField.setText(oldValue);
            }
        });

        // Price field validation - only allow numeric input with decimal
        priceField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                if (!PRICE_PATTERN.matcher(newValue).matches()) {
                    priceField.setText(oldValue);
                }
            }
        });

        // Description field validation
        descriptionField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.length() > MAX_DESCRIPTION_LENGTH) {
                descriptionField.setText(oldValue);
            }
        });
    }

    public void setData(CourseDetailDTO course) {
        this.currCourse = course;
        if (course != null) {
            this.originalStartDate = course.getCourse().getStartDate();
        }
        initialize();
    }

    @FXML
    public void handleChooseThumbnail() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn ảnh thumbnail");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Hình ảnh", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            // Validate file
            String validationError = validateImageFile(file);
            if (validationError != null) {
                alerts.showErrorAlert(validationError);
                return;
            }

            try {
                String fileName = sanitizeFileName(file.getName());
                File destDir = new File("src/main/resources/Images");
                if (!destDir.exists()) destDir.mkdirs();

                File destFile = new File(destDir, fileName);
                java.nio.file.Files.copy(file.toPath(), destFile.toPath(),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                Image image = new Image(destFile.toURI().toString());
                thumbnailImageView.setImage(image);
                thumbnailField = "Images/" + fileName;

            } catch (Exception e) {
                alerts.showErrorAlert("Lỗi khi tải ảnh lên: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Validate image file
     * @param file File to validate
     * @return Error message if invalid, null if valid
     */
    private String validateImageFile(File file) {
        // Check file size
        if (file.length() > MAX_FILE_SIZE) {
            return "Kích thước file không được vượt quá 5MB.";
        }

        // Check file extension
        String fileName = file.getName().toLowerCase();
        boolean validExtension = false;
        for (String ext : ALLOWED_IMAGE_EXTENSIONS) {
            if (fileName.endsWith(ext)) {
                validExtension = true;
                break;
            }
        }
        if (!validExtension) {
            return "Chỉ chấp nhận các file ảnh: PNG, JPG, JPEG, GIF.";
        }

        // Check if file exists and is readable
        if (!file.exists() || !file.canRead()) {
            return "Không thể đọc file đã chọn.";
        }

        return null;
    }

    /**
     * Sanitize file name to prevent security issues
     * @param fileName Original file name
     * @return Sanitized file name
     */
    private String sanitizeFileName(String fileName) {
        // Remove potentially dangerous characters
        String sanitized = fileName.replaceAll("[^a-zA-Z0-9._-]", "_");

        // Ensure reasonable length
        if (sanitized.length() > 50) {
            String extension = "";
            int lastDot = sanitized.lastIndexOf('.');
            if (lastDot > 0) {
                extension = sanitized.substring(lastDot);
                sanitized = sanitized.substring(0, Math.min(46, lastDot)) + extension;
            } else {
                sanitized = sanitized.substring(0, 50);
            }
        }

        return sanitized;
    }

    public void setCategoryVal(int categoryId) {
        List<Category> categories = categoriesService.getListCategory();
        if (categories == null || categories.isEmpty()) {
            alerts.showErrorAlert("Không có danh mục nào. Vui lòng thêm danh mục trước.");
            return;
        }

        categoryField.getItems().clear();
        categoryField.getItems().addAll(categories);
        categoryField.setConverter(new StringConverter<>() {
            @Override
            public String toString(Category c) {
                return c != null ? c.getCategoryName() : "";
            }

            @Override
            public Category fromString(String string) {
                return categories.stream()
                        .filter(c -> c.getCategoryName().equals(string))
                        .findFirst().orElse(null);
            }
        });

        // Set the selected category
        Category selectedCategory = categories.stream()
                .filter(c -> c.getCategoryId() == categoryId)
                .findFirst()
                .orElse(categories.get(0));
        categoryField.setValue(selectedCategory);
    }

    public void setInstructorVal(int instructorId) {
        List<Instructor> instructors = instructorService.getAllInstructor();
        if (instructors == null || instructors.isEmpty()) {
            alerts.showErrorAlert("Không có giảng viên nào. Vui lòng thêm giảng viên trước.");
            return;
        }

        instructorField.getItems().clear();
        instructorField.getItems().addAll(instructors);
        instructorField.setConverter(new StringConverter<>() {
            @Override
            public String toString(Instructor c) {
                return c != null ? c.getFullname() : "";
            }

            @Override
            public Instructor fromString(String string) {
                return instructors.stream()
                        .filter(c -> c.getFullname().equals(string))
                        .findFirst().orElse(null);
            }
        });

        // Set the selected instructor
        Instructor selectedInstructor = instructors.stream()
                .filter(i -> i.getUserId() == instructorId)
                .findFirst()
                .orElse(instructors.get(0));
        instructorField.setValue(selectedInstructor);
    }

    public void initDataFromCurrCourse(CourseDetailDTO course) {
        if (course != null) {
            setCategoryVal(course.getCourse().getCategoryId());
            courseNameField.setText(course.getCourse().getCourseName());
            setInstructorVal(course.getInstructor().getUserId());
            priceField.setText(String.format("%.2f", course.getCourse().getCoursePrice()));
            startDatePicker.setValue(course.getCourse().getStartDate());
            endDatePicker.setValue(course.getCourse().getEndDate());
            thumbnailField = course.getCourse().getCourseThumbnail();

            // Fix: Use course description instead of category description
            descriptionField.setText(course.getCourse().getCourseDescription() != null ?
                    course.getCourse().getCourseDescription() : "");

            // Load thumbnail image
            loadThumbnailImage(course.getCourse().getCourseThumbnail());
        }
    }

    /**
     * Load thumbnail image with error handling
     * @param thumbnailPath Path to thumbnail image
     */
    private void loadThumbnailImage(String thumbnailPath) {
        if (thumbnailPath == null || thumbnailPath.trim().isEmpty()) {
            loadDefaultImage();
            return;
        }

        try {
            String imagePath = "/" + thumbnailPath;
            Image image = new Image(getClass().getResource(imagePath).toExternalForm());
            thumbnailImageView.setImage(image);
        } catch (Exception e) {
            System.err.println("Không tìm thấy ảnh: " + thumbnailPath + ", dùng ảnh mặc định.");
            loadDefaultImage();
        }
    }

    /**
     * Load default image when thumbnail is not available
     */
    private void loadDefaultImage() {
        try {
            Image defaultImage = new Image(getClass().getResource("/Images/logo.png").toExternalForm());
            thumbnailImageView.setImage(defaultImage);
        } catch (Exception ex) {
            System.err.println("Không thể tải ảnh mặc định: " + ex.getMessage());
        }
    }

    public void handleSave() {
        List<String> validationErrors = validateAllFields();

        if (!validationErrors.isEmpty()) {
            String errorMessage = String.join("\n", validationErrors);
            alerts.showErrorAlert(errorMessage);
            return;
        }

        try {
            Course course = new Course(
                    currCourse.getCourse().getCourseId(),
                    courseNameField.getText().trim(),
                    instructorField.getValue().getUserId(),
                    categoryField.getValue().getCategoryId(),
                    parsePrice(priceField.getText()),
                    thumbnailField,
                    descriptionField.getText().trim(),
                    startDatePicker.getValue(),
                    endDatePicker.getValue()
            );

            if (courseService.updateInforCourse(course)) {
                alerts.showSuccessAlert("Chỉnh sửa khóa học thành công!");

                // Log the action
                if (SessionManager.getInstance().getUser().getRoleId() == 2) {
                    logService.createLog(SessionManager.getInstance().getUser().getUserId(),
                            "Giáo viên " + SessionManager.getInstance().getUser().getFullname() +
                                    " đã chỉnh sửa khóa học: " + course.getCourseName());
                }

                // Update instructor relationship
                instructorService.updateRelCourseAndInstructor(course.getCourseId(), course.getInstructorId());

                // Refresh course list
                if (courseManagementController != null) {
                    courseManagementController.refreshCourseList();
                }

                courseAdded = true;
                handleCancel();
            } else {
                alerts.showErrorAlert("Không thể cập nhật khóa học. Vui lòng thử lại.");
            }
        } catch (Exception e) {
            alerts.showErrorAlert("Lỗi hệ thống: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Comprehensive validation for all fields with edit-specific logic
     * @return List of validation error messages
     */
    private List<String> validateAllFields() {
        List<String> errors = new ArrayList<>();

        // Validate course name
        String courseName = courseNameField.getText();
        if (courseName == null || courseName.trim().isEmpty()) {
            errors.add("• Tên khóa học không được để trống.");
        } else {
            courseName = courseName.trim();
            if (courseName.length() < MIN_COURSE_NAME_LENGTH) {
                errors.add("• Tên khóa học phải có ít nhất " + MIN_COURSE_NAME_LENGTH + " ký tự.");
            } else if (courseName.length() > MAX_COURSE_NAME_LENGTH) {
                errors.add("• Tên khóa học không được vượt quá " + MAX_COURSE_NAME_LENGTH + " ký tự.");
            } else if (!COURSE_NAME_PATTERN.matcher(courseName).matches()) {
                errors.add("• Tên khóa học chứa ký tự không hợp lệ.");
            }
        }

        // Validate instructor
        if (instructorField.getValue() == null) {
            errors.add("• Vui lòng chọn giảng viên.");
        }

        // Validate category
        if (categoryField.getValue() == null) {
            errors.add("• Vui lòng chọn danh mục.");
        }

        // Validate dates with special logic for editing
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        LocalDate today = LocalDate.now();

        if (startDate == null) {
            errors.add("• Vui lòng chọn ngày bắt đầu.");
        } else {
            // Special validation for editing: Allow past start dates if they were originally in the past
            // but don't allow changing to a past date if the original was in the future
            boolean originalWasInFuture = originalStartDate != null && originalStartDate.isAfter(today);
            boolean newIsInPast = startDate.isBefore(today);

            if (originalWasInFuture && newIsInPast) {
                errors.add("• Không thể thay đổi ngày bắt đầu thành ngày trong quá khứ.");
            }

            // If course has already started, don't allow changing start date
            if (originalStartDate != null && originalStartDate.isBefore(today) &&
                    !startDate.equals(originalStartDate)) {
                errors.add("• Không thể thay đổi ngày bắt đầu của khóa học đã bắt đầu.");
            }
        }

        if (endDate == null) {
            errors.add("• Vui lòng chọn ngày kết thúc.");
        }

        if (startDate != null && endDate != null) {
            if (endDate.isBefore(startDate) || endDate.equals(startDate)) {
                errors.add("• Ngày kết thúc phải sau ngày bắt đầu ít nhất 1 ngày.");
            } else {
                long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
                if (daysBetween < MIN_COURSE_DURATION_DAYS) {
                    errors.add("• Khóa học phải kéo dài ít nhất " + MIN_COURSE_DURATION_DAYS + " ngày.");
                } else if (daysBetween > MAX_COURSE_DURATION_DAYS) {
                    errors.add("• Khóa học không được kéo dài quá " + MAX_COURSE_DURATION_DAYS + " ngày.");
                }
            }

            // Don't allow end date in the past for ongoing or future courses
            if (endDate.isBefore(today)) {
                errors.add("• Ngày kết thúc không thể trong quá khứ.");
            }
        }

        // Validate thumbnail
        if (thumbnailField == null || thumbnailField.trim().isEmpty()) {
            errors.add("• Vui lòng chọn ảnh đại diện cho khóa học.");
        }

        // Validate price
        String priceText = priceField.getText();
        if (priceText == null || priceText.trim().isEmpty()) {
            errors.add("• Giá khóa học không được để trống.");
        } else {
            try {
                double price = parsePrice(priceText);
                if (price < MIN_PRICE) {
                    errors.add("• Giá khóa học không thể âm.");
                } else if (price > MAX_PRICE) {
                    errors.add("• Giá khóa học quá lớn (tối đa " + MAX_PRICE + ").");
                }
            } catch (NumberFormatException e) {
                errors.add("• Giá khóa học không hợp lệ. Vui lòng nhập số.");
            }
        }

        // Validate description
        String description = descriptionField.getText();
        if (description == null || description.trim().isEmpty()) {
            errors.add("• Mô tả không được để trống.");
        } else {
            description = description.trim();
            if (description.length() < MIN_DESCRIPTION_LENGTH) {
                errors.add("• Mô tả phải có ít nhất " + MIN_DESCRIPTION_LENGTH + " ký tự.");
            } else if (description.length() > MAX_DESCRIPTION_LENGTH) {
                errors.add("• Mô tả không được vượt quá " + MAX_DESCRIPTION_LENGTH + " ký tự.");
            }
        }

        return errors;
    }

    /**
     * Parse and validate price with proper precision
     * @param priceText Price as string
     * @return Parsed price
     * @throws NumberFormatException if invalid
     */
    private double parsePrice(String priceText) throws NumberFormatException {
        try {
            BigDecimal price = new BigDecimal(priceText.trim());
            return price.setScale(2, RoundingMode.HALF_UP).doubleValue();
        } catch (Exception e) {
            throw new NumberFormatException("Invalid price format");
        }
    }

    public void handleCancel() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}