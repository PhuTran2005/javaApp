package com.example.coursemanagement.Repository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.coursemanagement.Models.Payment;
import com.example.coursemanagement.Utils.DatabaseConfig;


/**
 * Repository class for handling all database operations related to payments
 * SQL Server implementation
 */
public class PaymentRepository {

    /**
     * Get a paginated list of all payments
     *
     * @param page     Current page number (1-based)
     * @param pageSize Number of records per page
     * @return List of Payment objects
     */
    public List<Payment> getPayments(int page, int pageSize) {
        List<Payment> payments = new ArrayList<>();
        int offset = (page - 1) * pageSize;
        String query = "SELECT Pay.amount,Pay.method,Pay.order_id,Pay.payment_date,Pay.status,Pay.payments_id as id,u.email,u.full_name \n" +
                "FROM Payments Pay\n" +
                "JOIN Orders o ON Pay.order_id = o.order_id\n" +
                "JOIN Users u ON u.user_id = o.user_id\n" +
                "ORDER BY payment_date DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, offset);
            stmt.setInt(2, pageSize);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Payment payment = new Payment();

                    payment.setPaymentId(rs.getInt("id"));
                    payment.setOrderId(rs.getInt("order_id"));
                    payment.setAmount(rs.getBigDecimal("amount"));
                    payment.setMethod(rs.getString("method"));
                    payment.setStatus(rs.getString("status"));
                    payment.setPaymentDate(rs.getTimestamp("payment_date").toLocalDateTime());
                    payment.setEmail(rs.getString("email"));
                    payment.setFullName(rs.getString("full_name"));
                    payments.add(payment);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return payments;
    }

    public Payment getPaymentsDetail(int PaymentId) {

        // SQL Server pagination uses OFFSET-FETCH syntax
        String query = "SELECT Pay.amount,Pay.method,Pay.order_id,Pay.payment_date,Pay.status,Pay.payments_id as id,u.email,u.full_name \n" +
                "FROM Payments Pay\n" +
                "JOIN Orders o ON Pay.order_id = o.order_id\n" +
                "JOIN Users u ON u.user_id = o.user_id\n" +
                "where Pay.payments_id = ?;";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, PaymentId);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Payment payment = new Payment();

                payment.setPaymentId(rs.getInt("id"));
                payment.setOrderId(rs.getInt("order_id"));
                payment.setAmount(rs.getBigDecimal("amount"));
                payment.setMethod(rs.getString("method"));
                payment.setStatus(rs.getString("status"));
                payment.setPaymentDate(rs.getTimestamp("payment_date").toLocalDateTime());
                payment.setEmail(rs.getString("email"));
                payment.setFullName(rs.getString("full_name"));
                return payment;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Get the total count of all payments
     *
     * @return Total number of payment records
     */
    public int getTotalPaymentsCount() {
        String query = "SELECT COUNT(*) AS count FROM payments";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * Search payments by order ID, payment ID, or amount
     *
     * @param searchTerm Search keyword
     * @param page       Current page number (1-based)
     * @param pageSize   Number of records per page
     * @return List of Payment objects matching search criteria
     */
    public List<Payment> searchPayments(String searchTerm, int page, int pageSize) {
        List<Payment> payments = new ArrayList<>();
        int offset = (page - 1) * pageSize;
        String query = "SELECT Pay.amount,Pay.method,Pay.order_id,Pay.payment_date,Pay.status,Pay.payments_id as id,u.email,u.full_name \n" +
                "FROM Payments Pay\n" +
                "JOIN Orders o ON Pay.order_id = o.order_id\n" +
                "JOIN Users u ON u.user_id = o.user_id\n" +
                "WHERE " +
                "CAST(u.full_name AS NVARCHAR) LIKE ? OR " +

                "method LIKE ? " +
                "ORDER BY Pay.payment_date DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            String searchPattern = "%" + searchTerm + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setInt(3, offset);
            stmt.setInt(4, pageSize);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Payment payment = new Payment();

                    payment.setPaymentId(rs.getInt("id"));
                    payment.setOrderId(rs.getInt("order_id"));
                    payment.setAmount(rs.getBigDecimal("amount"));
                    payment.setMethod(rs.getString("method"));
                    payment.setStatus(rs.getString("status"));
                    payment.setPaymentDate(rs.getTimestamp("payment_date").toLocalDateTime());
                    payment.setEmail(rs.getString("email"));
                    payment.setFullName(rs.getString("full_name"));
                    payments.add(payment);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return payments;
    }


    public int getSearchResultsCount(String searchTerm) {
        String query = "SELECT COUNT(*) AS count \n" +
                "FROM Payments Pay\n" +
                "JOIN Orders o ON Pay.order_id = o.order_id\n" +
                "JOIN Users u ON u.user_id = o.user_id\n" +
                "WHERE \n" +
                "    CAST(u.full_name AS NVARCHAR) LIKE ? \n" +
                "    OR method LIKE ?\n";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            String searchPattern = "%" + searchTerm + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }


    public List<Payment> filterPaymentsByStatus(String status, int page, int pageSize) {
        List<Payment> payments = new ArrayList<>();
        int offset = (page - 1) * pageSize;
        String query = "SELECT Pay.amount,Pay.method,Pay.order_id,Pay.payment_date,Pay.status,Pay.payments_id as id,u.email,u.full_name \n" +
                "FROM Payments Pay\n" +
                "JOIN Orders o ON Pay.order_id = o.order_id\n" +
                "JOIN Users u ON u.user_id = o.user_id\n" +
                "WHERE Pay.status = ? ORDER BY Pay.payment_date DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, status);
            stmt.setInt(2, offset);
            stmt.setInt(3, pageSize);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Payment payment = new Payment();

                    payment.setPaymentId(rs.getInt("id"));
                    payment.setOrderId(rs.getInt("order_id"));
                    payment.setAmount(rs.getBigDecimal("amount"));
                    payment.setMethod(rs.getString("method"));
                    payment.setStatus(rs.getString("status"));
                    payment.setPaymentDate(rs.getTimestamp("payment_date").toLocalDateTime());
                    payment.setEmail(rs.getString("email"));
                    payment.setFullName(rs.getString("full_name"));
                    payments.add(payment);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return payments;
    }

    public int getFilterResultsCount(String status) {
        String query = "SELECT COUNT(*) AS count FROM payments WHERE status = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, status);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }


    public List<Payment> filterAndSearchPayments(String searchTerm, String status, int page, int pageSize) {
        List<Payment> payments = new ArrayList<>();
        int offset = (page - 1) * pageSize;
        String query = "SELECT Pay.amount,Pay.method,Pay.order_id,Pay.payment_date,Pay.status,Pay.payments_id as id,u.email,u.full_name \n" +
                "FROM Payments Pay\n" +
                "JOIN Orders o ON Pay.order_id = o.order_id\n" +
                "JOIN Users u ON u.user_id = o.user_id\n" +
                "WHERE " +
                "Pay.status = ? AND " +
                "CAST(u.full_name AS NVARCHAR) LIKE ? OR " +
                "method LIKE ?) " +
                "ORDER BY Pay.payment_date DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            String searchPattern = "%" + searchTerm + "%";
            stmt.setString(1, status);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            stmt.setInt(4, offset);
            stmt.setInt(5, pageSize);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Payment payment = new Payment();

                    payment.setPaymentId(rs.getInt("id"));
                    payment.setOrderId(rs.getInt("order_id"));
                    payment.setAmount(rs.getBigDecimal("amount"));
                    payment.setMethod(rs.getString("method"));
                    payment.setStatus(rs.getString("status"));
                    payment.setPaymentDate(rs.getTimestamp("payment_date").toLocalDateTime());
                    payment.setEmail(rs.getString("email"));
                    payment.setFullName(rs.getString("full_name"));
                    payments.add(payment);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return payments;
    }


    public int getFilterAndSearchResultsCount(String searchTerm, String status) {
        String query = "SELECT COUNT(*) AS count \n" +
                "FROM Payments Pay\n" +
                "JOIN Orders o ON Pay.order_id = o.order_id\n" +
                "JOIN Users u ON u.user_id = o.user_id\n" +
                "WHERE Pay.status = ?\n" +
                "  AND (CAST(u.full_name AS NVARCHAR) LIKE ? OR method LIKE ?)\n";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            String searchPattern = "%" + searchTerm + "%";
            stmt.setString(1, status);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            stmt.setString(4, searchPattern);
            stmt.setString(5, searchPattern);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }


    public List<Payment> getAllPayments() {
        List<Payment> payments = new ArrayList<>();
        String query = "SELECT Pay.amount,Pay.method,Pay.order_id,Pay.payment_date,Pay.status,Pay.payments_id as id,u.email,u.full_name \n" +
                "FROM Payments Pay\n" +
                "JOIN Orders o ON Pay.order_id = o.order_id\n" +
                "JOIN Users u ON u.user_id = o.user_id\n" +
                "ORDER BY payment_date DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Payment payment = new Payment();

                    payment.setPaymentId(rs.getInt("id"));
                    payment.setOrderId(rs.getInt("order_id"));
                    payment.setAmount(rs.getBigDecimal("amount"));
                    payment.setMethod(rs.getString("method"));
                    payment.setStatus(rs.getString("status"));
                    payment.setPaymentDate(rs.getTimestamp("payment_date").toLocalDateTime());
                    payment.setEmail(rs.getString("email"));
                    payment.setFullName(rs.getString("full_name"));
                    payments.add(payment);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return payments;
    }

    public List<Payment> searchAllPayments(String searchTerm) {
        List<Payment> payments = new ArrayList<>();
        String query = "SELECT Pay.amount,Pay.method,Pay.order_id,Pay.payment_date,Pay.status,Pay.payments_id as id,u.email,u.full_name \n" +
                "FROM Payments Pay\n" +
                "JOIN Orders o ON Pay.order_id = o.order_id\n" +
                "JOIN Users u ON u.user_id = o.user_id\n" +
                "WHERE " +
                "CAST(u.full_name AS NVARCHAR) LIKE ? OR " +

                "method LIKE ? " + "ORDER BY payment_date DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            String searchPattern = "%" + searchTerm + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);


            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Payment payment = new Payment();

                    payment.setPaymentId(rs.getInt("id"));
                    payment.setOrderId(rs.getInt("order_id"));
                    payment.setAmount(rs.getBigDecimal("amount"));
                    payment.setMethod(rs.getString("method"));
                    payment.setStatus(rs.getString("status"));
                    payment.setPaymentDate(rs.getTimestamp("payment_date").toLocalDateTime());
                    payment.setEmail(rs.getString("email"));
                    payment.setFullName(rs.getString("full_name"));
                    payments.add(payment);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return payments;
    }


    public List<Payment> filterAllPaymentsByStatus(String status) {
        List<Payment> payments = new ArrayList<>();
        String query = "SELECT Pay.amount,Pay.method,Pay.order_id,Pay.payment_date,Pay.status,Pay.payments_id as id,u.email,u.full_name \n" +
                "FROM Payments Pay\n" +
                "JOIN Orders o ON Pay.order_id = o.order_id\n" +
                "JOIN Users u ON u.user_id = o.user_id\n" +
                "WHERE Pay.status = ? ORDER BY Pay.payment_date DESC ";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, status);


            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Payment payment = new Payment();

                    payment.setPaymentId(rs.getInt("id"));
                    payment.setOrderId(rs.getInt("order_id"));
                    payment.setAmount(rs.getBigDecimal("amount"));
                    payment.setMethod(rs.getString("method"));
                    payment.setStatus(rs.getString("status"));
                    payment.setPaymentDate(rs.getTimestamp("payment_date").toLocalDateTime());
                    payment.setEmail(rs.getString("email"));
                    payment.setFullName(rs.getString("full_name"));
                    payments.add(payment);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return payments;
    }


    public List<Payment> filterAndSearchAllPayments(String searchTerm, String status) {
        List<Payment> payments = new ArrayList<>();
        String query = "SELECT Pay.amount,Pay.method,Pay.order_id,Pay.payment_date,Pay.status,Pay.payments_id as id,u.email,u.full_name \n" +
                "FROM Payments Pay\n" +
                "JOIN Orders o ON Pay.order_id = o.order_id\n" +
                "JOIN Users u ON u.user_id = o.user_id\n" +
                "WHERE " +
                "Pay.status = ? AND " +
                "CAST(u.full_name AS NVARCHAR) LIKE ? OR " +
                "method LIKE ?) " +
                "ORDER BY Pay.payment_date DESC ";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            String searchPattern = "%" + searchTerm + "%";
            stmt.setString(1, status);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);


            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Payment payment = new Payment();

                    payment.setPaymentId(rs.getInt("id"));
                    payment.setOrderId(rs.getInt("order_id"));
                    payment.setAmount(rs.getBigDecimal("amount"));
                    payment.setMethod(rs.getString("method"));
                    payment.setStatus(rs.getString("status"));
                    payment.setPaymentDate(rs.getTimestamp("payment_date").toLocalDateTime());
                    payment.setEmail(rs.getString("email"));
                    payment.setFullName(rs.getString("full_name"));
                    payments.add(payment);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return payments;
    }

    /**
     * Get a specific payment by ID
     *
     * @param paymentId ID of the payment to retrieve
     * @return Payment object if found, null otherwise
     */
    public Payment getPaymentById(int paymentId) {
        String query = "SELECT * FROM payments WHERE payments_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, paymentId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPayment(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Get payments by order ID
     *
     * @param orderId ID of the order
     * @return List of Payment objects associated with the order
     */
    public List<Payment> getPaymentsByOrderId(int orderId) {
        List<Payment> payments = new ArrayList<>();

        String query = "SELECT * FROM payments WHERE order_id = ? ORDER BY payment_date DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, orderId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    payments.add(mapResultSetToPayment(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return payments;
    }

    /**
     * Get total payment amount for a specific order
     *
     * @param orderId ID of the order
     * @return Total amount paid for the order
     */
    public BigDecimal getTotalPaymentAmountForOrder(int orderId) {
        String query = "SELECT SUM(amount) AS total_amount FROM payments WHERE order_id = ? AND status = 'Success'";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, orderId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Handle NULL result for SUM when no rows match
                    BigDecimal result = rs.getBigDecimal("total_amount");
                    return result != null ? result : BigDecimal.ZERO;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return BigDecimal.ZERO;
    }

    /**
     * Delete a payment record
     *
     * @param paymentId ID of the payment to delete
     * @return true if deletion was successful, false otherwise
     */
    public boolean deletePayment(int paymentId) {
        String query = "DELETE FROM payments WHERE payments_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, paymentId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Map database ResultSet to Payment object
     *
     * @param rs ResultSet from database query
     * @return Populated Payment object
     * @throws SQLException If there's an error accessing the ResultSet
     */
    private Payment mapResultSetToPayment(ResultSet rs) throws SQLException {
        Payment payment = new Payment();

        payment.setPaymentId(rs.getInt("payments_id"));
        payment.setOrderId(rs.getInt("order_id"));
        payment.setAmount(rs.getBigDecimal("amount"));
        payment.setMethod(rs.getString("method"));
        payment.setStatus(rs.getString("status"));
        payment.setPaymentDate(rs.getTimestamp("payment_date").toLocalDateTime());

        return payment;
    }
}