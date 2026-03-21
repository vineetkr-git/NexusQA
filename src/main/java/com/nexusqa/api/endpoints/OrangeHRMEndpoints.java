package com.nexusqa.api.endpoints;

public class OrangeHRMEndpoints {

    // Auth
    public static final String LOGIN = "/auth/login";
    public static final String TOKEN  = "/web/index.php/api/v2/auth/token";

    // Employee
    public static final String EMPLOYEES       = "/web/index.php/api/v2/pim/employees";
    public static final String EMPLOYEE_BY_ID  = "/web/index.php/api/v2/pim/employees/{id}";

    // Leave
    public static final String LEAVE_TYPES     = "/web/index.php/api/v2/leave/leave-types";

    // User Management
    public static final String USERS           = "/web/index.php/api/v2/admin/users";
    public static final String USER_BY_ID      = "/web/index.php/api/v2/admin/users/{id}";
}