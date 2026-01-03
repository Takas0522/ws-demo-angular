import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UserService } from '../../../../core/services/user.service';
import { UserProfile, CreateUserProfileRequest, UpdateUserProfileRequest } from '../../../../core/models/user.model';

@Component({
  selector: 'app-user-form',
  templateUrl: './user-form.component.html',
  styleUrls: ['./user-form.component.scss']
})
export class UserFormComponent implements OnInit {
  @Input() user: UserProfile | null = null;
  @Input() isEditMode = false;
  @Output() save = new EventEmitter<UserProfile>();
  @Output() cancel = new EventEmitter<void>();

  userForm!: FormGroup;
  loading = false;
  errorMessage = '';

  constructor(
    private formBuilder: FormBuilder,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    this.initForm();
  }

  /**
   * Initialize form
   */
  initForm(): void {
    this.userForm = this.formBuilder.group({
      userId: [this.user?.userId || '', this.isEditMode ? [] : [Validators.required, Validators.min(1)]],
      firstName: [this.user?.firstName || '', [Validators.required, Validators.maxLength(50)]],
      lastName: [this.user?.lastName || '', [Validators.required, Validators.maxLength(50)]],
      displayName: [this.user?.displayName || '', [Validators.required, Validators.maxLength(100)]],
      bio: [this.user?.bio || '', [Validators.maxLength(500)]],
      avatarUrl: [this.user?.avatarUrl || '', [Validators.maxLength(255)]],
      phoneNumber: [this.user?.phoneNumber || '', [Validators.maxLength(20)]],
      dateOfBirth: [this.user?.dateOfBirth || '', []],
      address: [this.user?.address || '', [Validators.maxLength(255)]],
      city: [this.user?.city || '', [Validators.maxLength(100)]],
      state: [this.user?.state || '', [Validators.maxLength(100)]],
      country: [this.user?.country || '', [Validators.maxLength(100)]],
      postalCode: [this.user?.postalCode || '', [Validators.maxLength(20)]]
    });

    // Disable userId field in edit mode
    if (this.isEditMode) {
      this.userForm.get('userId')?.disable();
    }
  }

  /**
   * Convenience getter for form fields
   */
  get f() {
    return this.userForm.controls;
  }

  /**
   * Handle form submission
   */
  onSubmit(): void {
    this.errorMessage = '';

    // Validate form
    if (this.userForm.invalid) {
      Object.keys(this.userForm.controls).forEach(key => {
        this.userForm.controls[key].markAsTouched();
      });
      return;
    }

    this.loading = true;

    if (this.isEditMode && this.user) {
      // Update existing user
      const request: UpdateUserProfileRequest = {
        firstName: this.userForm.value.firstName,
        lastName: this.userForm.value.lastName,
        displayName: this.userForm.value.displayName,
        bio: this.userForm.value.bio || undefined,
        avatarUrl: this.userForm.value.avatarUrl || undefined,
        phoneNumber: this.userForm.value.phoneNumber || undefined,
        dateOfBirth: this.userForm.value.dateOfBirth || undefined,
        address: this.userForm.value.address || undefined,
        city: this.userForm.value.city || undefined,
        state: this.userForm.value.state || undefined,
        country: this.userForm.value.country || undefined,
        postalCode: this.userForm.value.postalCode || undefined
      };

      this.userService.updateUser(this.user.userId, request).subscribe({
        next: (updatedUser) => {
          this.loading = false;
          this.save.emit(updatedUser);
        },
        error: (error) => {
          console.error('Failed to update user:', error);
          this.loading = false;
          this.errorMessage = 'ユーザーの更新に失敗しました。';
        }
      });
    } else {
      // Create new user
      const request: CreateUserProfileRequest = {
        userId: this.userForm.value.userId,
        firstName: this.userForm.value.firstName,
        lastName: this.userForm.value.lastName,
        displayName: this.userForm.value.displayName,
        bio: this.userForm.value.bio || undefined,
        avatarUrl: this.userForm.value.avatarUrl || undefined,
        phoneNumber: this.userForm.value.phoneNumber || undefined,
        dateOfBirth: this.userForm.value.dateOfBirth || undefined,
        address: this.userForm.value.address || undefined,
        city: this.userForm.value.city || undefined,
        state: this.userForm.value.state || undefined,
        country: this.userForm.value.country || undefined,
        postalCode: this.userForm.value.postalCode || undefined
      };

      this.userService.createUser(request).subscribe({
        next: (newUser) => {
          this.loading = false;
          this.save.emit(newUser);
        },
        error: (error) => {
          console.error('Failed to create user:', error);
          this.loading = false;
          this.errorMessage = 'ユーザーの作成に失敗しました。';
        }
      });
    }
  }

  /**
   * Handle cancel
   */
  onCancel(): void {
    this.cancel.emit();
  }

  /**
   * Check if field has error
   */
  hasError(fieldName: string, errorType: string): boolean {
    const field = this.userForm.get(fieldName);
    return !!(field && field.hasError(errorType) && field.touched);
  }
}
