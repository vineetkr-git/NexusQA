package com.nexusqa.db;

public class DBQueries {

    // ===== Employee Queries =====
    public static final String INSERT_EMPLOYEE =
            "INSERT INTO employees (first_name, last_name, " +
                    "email, department, salary) VALUES (?, ?, ?, ?, ?)";

    public static final String SELECT_ALL_EMPLOYEES =
            "SELECT * FROM employees";

    public static final String SELECT_EMPLOYEE_BY_EMAIL =
            "SELECT * FROM employees WHERE email = ?";

    public static final String SELECT_EMPLOYEES_BY_DEPT =
            "SELECT * FROM employees WHERE department = ?";

    public static final String UPDATE_EMPLOYEE_SALARY =
            "UPDATE employees SET salary = ? WHERE email = ?";

    public static final String DELETE_EMPLOYEE =
            "DELETE FROM employees WHERE email = ?";

    public static final String COUNT_EMPLOYEES =
            "SELECT COUNT(*) as total FROM employees";

    public static final String AVG_SALARY_BY_DEPT =
            "SELECT department, AVG(salary) as avg_salary " +
                    "FROM employees GROUP BY department";

    // ===== Leave Queries =====
    public static final String INSERT_LEAVE =
            "INSERT INTO leave_requests (employee_id, leave_type, " +
                    "start_date, end_date) VALUES (?, ?, ?, ?)";

    public static final String SELECT_LEAVE_BY_EMPLOYEE =
            "SELECT * FROM leave_requests WHERE employee_id = ?";

    public static final String UPDATE_LEAVE_STATUS =
            "UPDATE leave_requests SET status = ? WHERE id = ?";
}