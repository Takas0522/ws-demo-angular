import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError, forkJoin, of } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { 
  Application, 
  PermissionLevel, 
  UserPermission, 
  CreatePermissionRequest,
  UpdatePermissionRequest 
} from '../models/permission.model';

export interface PermissionMatrix {
  userId: number;
  permissions: {
    applicationId: number;
    permissionLevelId: number | null;
  }[];
}

@Injectable({
  providedIn: 'root'
})
export class PermissionService {
  private readonly API_URL = environment.apiUrl;

  constructor(private http: HttpClient) {}

  /**
   * Get all applications via BFF
   */
  getAllApplications(): Observable<Application[]> {
    return this.http.get<Application[]>(`${this.API_URL}/applications`)
      .pipe(
        catchError(error => {
          console.error('Failed to fetch applications:', error);
          return throwError(() => error);
        })
      );
  }

  /**
   * Get all permission levels
   * TODO: Replace with actual API call when backend endpoint is available
   * For now, using hardcoded values based on backend data.sql
   */
  getPermissionLevels(): Observable<PermissionLevel[]> {
    // Hardcoded permission levels based on the backend data.sql
    const permissionLevels: PermissionLevel[] = [
      {
        id: 1,
        levelCode: 'READ',
        levelName: 'Read',
        description: 'Read-only access to view application data',
        levelOrder: 1
      },
      {
        id: 2,
        levelCode: 'WRITE',
        levelName: 'Write',
        description: 'Read and write access to modify application data',
        levelOrder: 2
      },
      {
        id: 3,
        levelCode: 'ADMIN',
        levelName: 'Administrator',
        description: 'Full administrative access with all permissions',
        levelOrder: 3
      }
    ];
    
    return of(permissionLevels);
  }

  /**
   * Get permissions for a specific user via BFF
   */
  getUserPermissions(userId: number): Observable<UserPermission[]> {
    return this.http.get<UserPermission[]>(`${this.API_URL}/users/${userId}/permissions`)
      .pipe(
        catchError(error => {
          console.error(`Failed to fetch permissions for user ${userId}:`, error);
          return throwError(() => error);
        })
      );
  }

  /**
   * Create a new permission via BFF
   */
  createPermission(request: CreatePermissionRequest): Observable<UserPermission> {
    return this.http.post<UserPermission>(`${this.API_URL}/permissions`, request)
      .pipe(
        catchError(error => {
          console.error('Failed to create permission:', error);
          return throwError(() => error);
        })
      );
  }

  /**
   * Update an existing permission via BFF
   */
  updatePermission(permissionId: number, request: UpdatePermissionRequest): Observable<UserPermission> {
    return this.http.put<UserPermission>(`${this.API_URL}/permissions/${permissionId}`, request)
      .pipe(
        catchError(error => {
          console.error(`Failed to update permission ${permissionId}:`, error);
          return throwError(() => error);
        })
      );
  }

  /**
   * Delete a permission via BFF
   */
  deletePermission(permissionId: number): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/permissions/${permissionId}`)
      .pipe(
        catchError(error => {
          console.error(`Failed to delete permission ${permissionId}:`, error);
          return throwError(() => error);
        })
      );
  }

  /**
   * Batch update permissions for a user
   * This will create, update, or delete permissions as needed
   */
  batchUpdatePermissions(userId: number, matrix: PermissionMatrix): Observable<any> {
    return this.getUserPermissions(userId).pipe(
      switchMap(existingPermissions => {
        const operations: Observable<any>[] = [];
        
        // Build a map of existing permissions for easy lookup
        const existingMap = new Map<number, UserPermission>();
        existingPermissions.forEach(perm => {
          existingMap.set(perm.application.id, perm);
        });

        // Process each application in the matrix
        matrix.permissions.forEach(matrixPerm => {
          const existing = existingMap.get(matrixPerm.applicationId);
          
          if (matrixPerm.permissionLevelId === null) {
            // If permission level is null and permission exists, delete it
            if (existing) {
              operations.push(this.deletePermission(existing.id));
            }
          } else if (existing) {
            // If permission exists and level changed, update it
            if (existing.permissionLevel.id !== matrixPerm.permissionLevelId) {
              const updateReq: UpdatePermissionRequest = {
                permissionLevelId: matrixPerm.permissionLevelId
              };
              operations.push(this.updatePermission(existing.id, updateReq));
            }
            // If same level, no operation needed
          } else {
            // If permission doesn't exist and level is set, create it
            const createReq: CreatePermissionRequest = {
              userId: userId,
              applicationId: matrixPerm.applicationId,
              permissionLevelId: matrixPerm.permissionLevelId
            };
            operations.push(this.createPermission(createReq));
          }
        });

        // If no operations, return empty array
        if (operations.length === 0) {
          return of([]);
        }

        // Execute all operations in parallel
        return forkJoin(operations);
      })
    );
  }
}
