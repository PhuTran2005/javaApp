package com.example.coursemanagement.Models;

import java.util.List;

public class PageResult<T> {
    private List<T> items;          // Danh sách các mục trên trang hiện tại
    private int currentPage;        // Số trang hiện tại
    private int pageSize;           // Số lượng mục trên mỗi trang
    private int totalPages;         // Tổng số trang
    private int totalRecords;       // Tổng số bản ghi

    public PageResult(List<T> items, int currentPage, int pageSize, int totalPages, int totalRecords) {
        this.items = items;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalPages = totalPages;
        this.totalRecords = totalRecords;
    }

    // Getter và Setter
    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }

    // Phương thức kiểm tra trang hiện tại có phải là trang đầu tiên
    public boolean isFirstPage() {
        return currentPage <= 1;
    }

    // Phương thức kiểm tra trang hiện tại có phải là trang cuối cùng
    public boolean isLastPage() {
        return currentPage >= totalPages;
    }

    // Phương thức lấy số trang trước
    public int getPreviousPage() {
        return isFirstPage() ? 1 : currentPage - 1;
    }

    // Phương thức lấy số trang sau
    public int getNextPage() {
        return isLastPage() ? totalPages : currentPage + 1;
    }
}