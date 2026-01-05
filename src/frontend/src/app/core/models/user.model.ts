// User entity (from auth-service)
export interface User {
  id: number;
  username: string;
  email: string;
  enabled: boolean;
  accountNonExpired: boolean;
  accountNonLocked: boolean;
  credentialsNonExpired: boolean;
  createdAt: string;
  updatedAt: string;
}

// User profile models
export interface UserProfile {
  id: number;
  userId: number;
  firstName: string;
  lastName: string;
  displayName: string;
  bio?: string;
  avatarUrl?: string;
  phoneNumber?: string;
  dateOfBirth?: string;
  address?: string;
  city?: string;
  state?: string;
  country?: string;
  postalCode?: string;
  createdAt: string;
  updatedAt: string;
}

// Combined user with profile (for list view)
export interface UserWithProfile extends UserProfile {
  username: string;
  email: string;
  enabled: boolean;
}

export interface CreateUserProfileRequest {
  userId: number;
  firstName: string;
  lastName: string;
  displayName: string;
  bio?: string;
  avatarUrl?: string;
  phoneNumber?: string;
  dateOfBirth?: string;
  address?: string;
  city?: string;
  state?: string;
  country?: string;
  postalCode?: string;
}

export interface UpdateUserProfileRequest {
  firstName?: string;
  lastName?: string;
  displayName?: string;
  bio?: string;
  avatarUrl?: string;
  phoneNumber?: string;
  dateOfBirth?: string;
  address?: string;
  city?: string;
  state?: string;
  country?: string;
  postalCode?: string;
}

export interface CreateUserProfileResponse {
  id: number;
  userId: number;
  message: string;
}
