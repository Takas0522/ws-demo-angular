-- Permission Service Seed Data
-- Insert permission levels, applications, and sample user permissions

-- ========================================
-- Permission Levels (3 levels)
-- ========================================

-- Permission Level 1: READ - Basic read-only access
INSERT INTO permission_levels (level_code, level_name, description, level_order)
VALUES ('READ', 'Read', 'Read-only access to view application data', 1);

-- Permission Level 2: WRITE - Read and write access
INSERT INTO permission_levels (level_code, level_name, description, level_order)
VALUES ('WRITE', 'Write', 'Read and write access to modify application data', 2);

-- Permission Level 3: ADMIN - Full administrative access
INSERT INTO permission_levels (level_code, level_name, description, level_order)
VALUES ('ADMIN', 'Administrator', 'Full administrative access with all permissions', 3);

-- ========================================
-- Applications (10 sample applications)
-- ========================================

-- Application 1: User Management
INSERT INTO applications (app_code, app_name, description, enabled)
VALUES ('USER_MGMT', 'User Management', 'Manage user accounts, profiles, and authentication', true);

-- Application 2: Inventory System
INSERT INTO applications (app_code, app_name, description, enabled)
VALUES ('INVENTORY', 'Inventory System', 'Track and manage inventory, stock levels, and warehouses', true);

-- Application 3: Sales Dashboard
INSERT INTO applications (app_code, app_name, description, enabled)
VALUES ('SALES_DASH', 'Sales Dashboard', 'View sales metrics, reports, and analytics', true);

-- Application 4: Customer Portal
INSERT INTO applications (app_code, app_name, description, enabled)
VALUES ('CUSTOMER_PORTAL', 'Customer Portal', 'Customer-facing portal for orders and support', true);

-- Application 5: Reporting Engine
INSERT INTO applications (app_code, app_name, description, enabled)
VALUES ('REPORTING', 'Reporting Engine', 'Generate and manage business reports', true);

-- Application 6: HR System
INSERT INTO applications (app_code, app_name, description, enabled)
VALUES ('HR_SYSTEM', 'HR System', 'Human resources management and employee data', true);

-- Application 7: Financial Management
INSERT INTO applications (app_code, app_name, description, enabled)
VALUES ('FINANCE', 'Financial Management', 'Manage finances, budgets, and accounting', true);

-- Application 8: Project Management
INSERT INTO applications (app_code, app_name, description, enabled)
VALUES ('PROJECT_MGMT', 'Project Management', 'Track projects, tasks, and team collaboration', true);

-- Application 9: Document Management
INSERT INTO applications (app_code, app_name, description, enabled)
VALUES ('DOC_MGMT', 'Document Management', 'Store, organize, and manage documents', true);

-- Application 10: Analytics Platform
INSERT INTO applications (app_code, app_name, description, enabled)
VALUES ('ANALYTICS', 'Analytics Platform', 'Advanced analytics and data visualization', true);

-- ========================================
-- Sample User App Permissions
-- ========================================
-- Note: user_id references users from auth_db (assuming users 1-10 exist)
-- This creates a diverse set of permissions for testing

-- Admin user (user_id: 1) - ADMIN access to all applications
INSERT INTO user_app_permissions (user_id, application_id, permission_level_id, granted_by)
VALUES (1, 1, 3, 'system');  -- USER_MGMT - ADMIN

INSERT INTO user_app_permissions (user_id, application_id, permission_level_id, granted_by)
VALUES (1, 2, 3, 'system');  -- INVENTORY - ADMIN

INSERT INTO user_app_permissions (user_id, application_id, permission_level_id, granted_by)
VALUES (1, 3, 3, 'system');  -- SALES_DASH - ADMIN

INSERT INTO user_app_permissions (user_id, application_id, permission_level_id, granted_by)
VALUES (1, 7, 3, 'system');  -- FINANCE - ADMIN

-- Regular user 1 (user_id: 2) - Mixed permissions
INSERT INTO user_app_permissions (user_id, application_id, permission_level_id, granted_by)
VALUES (2, 3, 1, 'admin');  -- SALES_DASH - READ

INSERT INTO user_app_permissions (user_id, application_id, permission_level_id, granted_by)
VALUES (2, 4, 2, 'admin');  -- CUSTOMER_PORTAL - WRITE

INSERT INTO user_app_permissions (user_id, application_id, permission_level_id, granted_by)
VALUES (2, 8, 2, 'admin');  -- PROJECT_MGMT - WRITE

-- Regular user 2 (user_id: 3) - Inventory and reporting
INSERT INTO user_app_permissions (user_id, application_id, permission_level_id, granted_by)
VALUES (3, 2, 2, 'admin');  -- INVENTORY - WRITE

INSERT INTO user_app_permissions (user_id, application_id, permission_level_id, granted_by)
VALUES (3, 5, 1, 'admin');  -- REPORTING - READ

INSERT INTO user_app_permissions (user_id, application_id, permission_level_id, granted_by)
VALUES (3, 9, 1, 'admin');  -- DOC_MGMT - READ

-- Regular user 3 (user_id: 4) - HR and finance read access
INSERT INTO user_app_permissions (user_id, application_id, permission_level_id, granted_by)
VALUES (4, 6, 2, 'admin');  -- HR_SYSTEM - WRITE

INSERT INTO user_app_permissions (user_id, application_id, permission_level_id, granted_by)
VALUES (4, 7, 1, 'admin');  -- FINANCE - READ

-- Regular user 4 (user_id: 5) - Project management admin
INSERT INTO user_app_permissions (user_id, application_id, permission_level_id, granted_by)
VALUES (5, 8, 3, 'admin');  -- PROJECT_MGMT - ADMIN

INSERT INTO user_app_permissions (user_id, application_id, permission_level_id, granted_by)
VALUES (5, 9, 2, 'admin');  -- DOC_MGMT - WRITE

INSERT INTO user_app_permissions (user_id, application_id, permission_level_id, granted_by)
VALUES (5, 10, 1, 'admin');  -- ANALYTICS - READ

-- Regular user 5 (user_id: 6) - Analytics and reporting
INSERT INTO user_app_permissions (user_id, application_id, permission_level_id, granted_by)
VALUES (6, 5, 2, 'admin');  -- REPORTING - WRITE

INSERT INTO user_app_permissions (user_id, application_id, permission_level_id, granted_by)
VALUES (6, 10, 3, 'admin');  -- ANALYTICS - ADMIN

-- Regular user 6 (user_id: 7) - Customer portal admin
INSERT INTO user_app_permissions (user_id, application_id, permission_level_id, granted_by)
VALUES (7, 4, 3, 'admin');  -- CUSTOMER_PORTAL - ADMIN

INSERT INTO user_app_permissions (user_id, application_id, permission_level_id, granted_by)
VALUES (7, 3, 1, 'admin');  -- SALES_DASH - READ
