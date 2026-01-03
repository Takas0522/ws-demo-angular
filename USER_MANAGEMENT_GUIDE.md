# User Management Implementation Guide

## Overview
This implementation adds a complete user management interface to the Angular frontend application, integrating with the BFF (Backend For Frontend) architecture.

## Features Implemented

### 1. User Service (`user.service.ts`)
- **GET /api/users** - Fetch all user profiles
- **GET /api/users/:userId** - Fetch a single user profile
- **POST /api/users** - Create a new user profile
- **PUT /api/users/:userId** - Update an existing user profile
- **DELETE /api/users/:userId** - Delete a user profile

All API calls go through the BFF service at `http://localhost:8080/api`.

### 2. User List Component
Located at `/users`, this component displays:
- **Table View**: Shows user profiles with sortable columns
  - User ID
  - Display Name
  - Full Name (First + Last)
  - Phone Number
  - Created At
- **Pagination**: Navigate through pages of users (10 per page by default)
- **Sorting**: Click column headers to sort by User ID, Display Name, or Created At
- **Actions**:
  - **New User**: Opens the create form dialog
  - **Edit**: Opens the edit form dialog for the selected user
  - **Delete**: Opens the delete confirmation dialog

### 3. User Form Component (Dialog)
Modal dialog for creating and editing users:
- **Create Mode**: 
  - Requires User ID (references auth-service user)
  - All profile fields available
- **Edit Mode**:
  - User ID is read-only
  - Updates profile information only

**Fields**:
- User ID* (required for creation only)
- Display Name* (required, max 100 chars)
- First Name* (required, max 50 chars)
- Last Name* (required, max 50 chars)
- Phone Number (optional, max 20 chars)
- Date of Birth (optional)
- Avatar URL (optional, max 255 chars)
- Bio (optional, max 500 chars)
- Address (optional, max 255 chars)
- City (optional, max 100 chars)
- State (optional, max 100 chars)
- Country (optional, max 100 chars)
- Postal Code (optional, max 20 chars)

### 4. Delete Confirmation Dialog
Simple confirmation modal that:
- Shows user details before deletion
- Confirms the delete action
- Handles errors gracefully

### 5. Navigation Bar
Added to `app.component.html`:
- Shows when user is authenticated
- Links to User Management page
- Logout button with icon

## Technical Details

### Models
Enhanced `user.model.ts` with:
- `User` interface (auth-service entity)
- `UserProfile` interface (user-service entity)
- `UserWithProfile` interface (combined view)
- `CreateUserProfileRequest` interface
- `UpdateUserProfileRequest` interface

### Routing
- `/login` - Login page (public)
- `/users` - User management page (protected by AuthGuard)
- `/` - Redirects to `/users`

### UI Framework
- **Tailwind CSS** for styling
- **Reactive Forms** for validation
- **Dialog patterns** using fixed positioning and backdrop

### Security
- All protected routes use `AuthGuard`
- JWT tokens automatically attached via `AuthInterceptor`
- BFF handles token forwarding to backend services

## Testing the Implementation

### Prerequisites
1. PostgreSQL database running with initialized schemas
2. Backend services running:
   - Auth Service on port 8081
   - User Service on port 8082
   - BFF Service on port 8080
3. Frontend dev server on port 4200

### Test Scenarios

#### 1. Login
1. Navigate to `http://localhost:4200`
2. You should be redirected to `/login`
3. Use test credentials:
   - Username: `admin`
   - Password: `password123`
4. After successful login, you should be redirected to `/users`

#### 2. View Users
1. After login, the user list should load automatically
2. Verify the table displays user profiles
3. Test pagination controls if more than 10 users exist
4. Test sorting by clicking column headers

#### 3. Create User
1. Click "新規作成" (New User) button
2. Fill in the form:
   - User ID: Enter an existing user ID from auth-service (e.g., 1, 2, 3)
   - Display Name: e.g., "Test User"
   - First Name: e.g., "太郎"
   - Last Name: e.g., "山田"
   - Phone Number: e.g., "090-1234-5678"
3. Click "作成" (Create)
4. User should appear in the list

#### 4. Edit User
1. Click "編集" (Edit) on any user row
2. Modify some fields (e.g., phone number, bio)
3. Click "更新" (Update)
4. Changes should be reflected in the list

#### 5. Delete User
1. Click "削除" (Delete) on any user row
2. Confirm the deletion in the dialog
3. User should be removed from the list

#### 6. Logout
1. Click the "ログアウト" (Logout) button in the navigation bar
2. You should be redirected to `/login`
3. Verify you cannot access `/users` without logging in again

## API Integration Notes

### Expected Backend Behavior
The user-service should:
- Return a list of `UserProfileDto` objects for GET /api/users
- Return a single `UserProfileDto` for GET /api/users/:userId
- Create and return `UserProfileDto` for POST /api/users
- Update and return `UserProfileDto` for PUT /api/users/:userId
- Return 204 No Content for DELETE /api/users/:userId

### BFF Proxy Behavior
The BFF should:
- Forward all `/api/users/**` requests to user-service
- Include JWT authorization headers
- Return responses transparently

## Error Handling
All components handle errors gracefully with:
- User-friendly error messages in Japanese
- Red alert boxes for error display
- Console logging for debugging
- Proper HTTP error status handling

## Build Verification
Run the following command to verify the build:
```bash
cd src/frontend
npm install
npm run build
```

The build should complete successfully with only warnings about unused files (which is expected).

## Code Structure
```
src/frontend/src/app/
├── core/
│   ├── guards/
│   │   └── auth.guard.ts
│   ├── interceptors/
│   │   └── auth.interceptor.ts
│   ├── models/
│   │   └── user.model.ts (enhanced)
│   └── services/
│       ├── auth.service.ts
│       ├── user.service.ts (new)
│       └── index.ts
├── features/
│   ├── auth/
│   │   └── ...
│   └── users/ (new)
│       ├── components/
│       │   ├── confirm-delete/
│       │   │   ├── confirm-delete.component.html
│       │   │   ├── confirm-delete.component.scss
│       │   │   └── confirm-delete.component.ts
│       │   ├── user-form/
│       │   │   ├── user-form.component.html
│       │   │   ├── user-form.component.scss
│       │   │   └── user-form.component.ts
│       │   └── user-list/
│       │       ├── user-list.component.html
│       │       ├── user-list.component.scss
│       │       └── user-list.component.ts
│       ├── users-routing.module.ts
│       └── users.module.ts
├── app-routing.module.ts (updated)
├── app.component.html (enhanced)
└── app.component.ts (enhanced)
```

## Next Steps
1. Start the backend services
2. Start the frontend dev server: `npm start`
3. Test all functionality with actual API calls
4. Take screenshots of the UI for documentation
5. Address any issues that arise during testing
