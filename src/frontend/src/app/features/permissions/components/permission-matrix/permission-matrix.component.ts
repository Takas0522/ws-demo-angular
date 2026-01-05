import { Component, OnInit } from '@angular/core';
import { PermissionService, PermissionMatrix } from '../../../../core/services/permission.service';
import { UserService } from '../../../../core/services/user.service';
import { Application, PermissionLevel, UserPermission } from '../../../../core/models/permission.model';
import { UserProfile } from '../../../../core/models/user.model';

@Component({
  selector: 'app-permission-matrix',
  templateUrl: './permission-matrix.component.html',
  styleUrls: ['./permission-matrix.component.scss']
})
export class PermissionMatrixComponent implements OnInit {
  users: UserProfile[] = [];
  applications: Application[] = [];
  permissionLevels: PermissionLevel[] = [];
  
  selectedUserId: number | null = null;
  permissionMatrix: Map<number, number | null> = new Map();
  
  loading = false;
  saving = false;
  errorMessage = '';
  successMessage = '';

  constructor(
    private permissionService: PermissionService,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    this.loadInitialData();
  }

  /**
   * Load users, applications, and permission levels
   */
  loadInitialData(): void {
    this.loading = true;
    this.errorMessage = '';

    // Load users
    this.userService.getAllUsers().subscribe({
      next: (users) => {
        this.users = users;
      },
      error: (error) => {
        console.error('Failed to load users:', error);
        this.errorMessage = 'ユーザーの読み込みに失敗しました。';
        this.loading = false;
      }
    });

    // Load applications
    this.permissionService.getAllApplications().subscribe({
      next: (applications) => {
        this.applications = applications.filter(app => app.enabled);
      },
      error: (error) => {
        console.error('Failed to load applications:', error);
        this.errorMessage = 'アプリケーションの読み込みに失敗しました。';
        this.loading = false;
      }
    });

    // Load permission levels
    this.permissionService.getPermissionLevels().subscribe({
      next: (levels) => {
        this.permissionLevels = levels;
        this.loading = false;
      },
      error: (error) => {
        console.error('Failed to load permission levels:', error);
        this.errorMessage = '権限レベルの読み込みに失敗しました。';
        this.loading = false;
      }
    });
  }

  /**
   * Handle user selection change
   */
  onUserSelect(event: Event): void {
    const target = event.target as HTMLSelectElement;
    const userId = target.value ? parseInt(target.value, 10) : null;
    
    if (userId) {
      this.selectedUserId = userId;
      this.loadUserPermissions(userId);
    } else {
      this.selectedUserId = null;
      this.permissionMatrix.clear();
    }
    
    this.clearMessages();
  }

  /**
   * Load permissions for the selected user
   */
  loadUserPermissions(userId: number): void {
    this.loading = true;
    this.errorMessage = '';

    this.permissionService.getUserPermissions(userId).subscribe({
      next: (permissions) => {
        this.permissionMatrix.clear();
        
        // Populate the matrix with existing permissions
        permissions.forEach(perm => {
          this.permissionMatrix.set(perm.application.id, perm.permissionLevel.id);
        });
        
        // Ensure all applications have an entry (null if no permission)
        this.applications.forEach(app => {
          if (!this.permissionMatrix.has(app.id)) {
            this.permissionMatrix.set(app.id, null);
          }
        });
        
        this.loading = false;
      },
      error: (error) => {
        console.error(`Failed to load permissions for user ${userId}:`, error);
        this.errorMessage = 'ユーザー権限の読み込みに失敗しました。';
        this.loading = false;
      }
    });
  }

  /**
   * Update permission for an application
   */
  updatePermission(applicationId: number, permissionLevelId: number | null): void {
    this.permissionMatrix.set(applicationId, permissionLevelId);
    this.clearMessages();
  }

  /**
   * Get permission level ID for an application
   */
  getPermissionLevelId(applicationId: number): number | null {
    return this.permissionMatrix.get(applicationId) ?? null;
  }

  /**
   * Check if a permission level is selected for an application
   */
  isPermissionSelected(applicationId: number, permissionLevelId: number | null): boolean {
    return this.getPermissionLevelId(applicationId) === permissionLevelId;
  }

  /**
   * Save all permissions
   */
  savePermissions(): void {
    if (!this.selectedUserId) {
      this.errorMessage = 'ユーザーを選択してください。';
      return;
    }

    this.saving = true;
    this.errorMessage = '';
    this.successMessage = '';

    const matrix: PermissionMatrix = {
      userId: this.selectedUserId,
      permissions: Array.from(this.permissionMatrix.entries()).map(([applicationId, permissionLevelId]) => ({
        applicationId,
        permissionLevelId
      }))
    };

    this.permissionService.batchUpdatePermissions(this.selectedUserId, matrix).subscribe({
      next: () => {
        this.successMessage = '権限を正常に保存しました。';
        this.saving = false;
        // Reload permissions to ensure UI is in sync with backend
        // This is important because the backend may have applied additional logic
        // (e.g., audit logs, timestamps) that should be reflected in the UI
        if (this.selectedUserId) {
          this.loadUserPermissions(this.selectedUserId);
        }
      },
      error: (error) => {
        console.error('Failed to save permissions:', error);
        this.errorMessage = '権限の保存に失敗しました。';
        this.saving = false;
      }
    });
  }

  /**
   * Clear success and error messages
   */
  clearMessages(): void {
    this.errorMessage = '';
    this.successMessage = '';
  }

  /**
   * Get selected user's display name
   */
  getSelectedUserName(): string {
    if (!this.selectedUserId) return '';
    const user = this.users.find(u => u.userId === this.selectedUserId);
    return user ? user.displayName : '';
  }

  /**
   * Check if save button should be disabled
   */
  isSaveDisabled(): boolean {
    return !this.selectedUserId || this.saving || this.loading;
  }
}
