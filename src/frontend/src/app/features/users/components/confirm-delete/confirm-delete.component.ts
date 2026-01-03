import { Component, Input, Output, EventEmitter } from '@angular/core';
import { UserService } from '../../../../core/services/user.service';
import { UserProfile } from '../../../../core/models/user.model';

@Component({
  selector: 'app-confirm-delete',
  templateUrl: './confirm-delete.component.html',
  styleUrls: ['./confirm-delete.component.scss']
})
export class ConfirmDeleteComponent {
  @Input() user: UserProfile | null = null;
  @Output() confirm = new EventEmitter<void>();
  @Output() cancel = new EventEmitter<void>();

  loading = false;
  errorMessage = '';

  constructor(private userService: UserService) {}

  /**
   * Handle delete confirmation
   */
  onConfirm(): void {
    if (!this.user) {
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    this.userService.deleteUser(this.user.userId).subscribe({
      next: () => {
        this.loading = false;
        this.confirm.emit();
      },
      error: (error) => {
        console.error('Failed to delete user:', error);
        this.loading = false;
        this.errorMessage = 'ユーザーの削除に失敗しました。';
      }
    });
  }

  /**
   * Handle cancel
   */
  onCancel(): void {
    this.cancel.emit();
  }
}
