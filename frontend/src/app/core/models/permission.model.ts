// Permission related models
export interface Application {
  id: number;
  appCode: string;
  appName: string;
  description: string;
  enabled: boolean;
}

export interface PermissionLevel {
  id: number;
  levelCode: string;
  levelName: string;
  description: string;
  levelOrder: number;
}

export interface UserPermission {
  id: number;
  userId: number;
  application: Application;
  permissionLevel: PermissionLevel;
  grantedAt: string;
  grantedBy: string;
  expiresAt?: string;
}

export interface CreatePermissionRequest {
  userId: number;
  applicationId: number;
  permissionLevelId: number;
  expiresAt?: string;
}

export interface UpdatePermissionRequest {
  permissionLevelId?: number;
  expiresAt?: string;
}
