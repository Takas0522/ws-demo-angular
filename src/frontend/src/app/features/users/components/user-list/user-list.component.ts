import { Component, OnInit } from '@angular/core';
import { UserService } from '../../../../core/services/user.service';
import { UserProfile } from '../../../../core/models/user.model';

@Component({
  selector: 'app-user-list',
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.scss']
})
export class UserListComponent implements OnInit {
  users: UserProfile[] = [];
  filteredUsers: UserProfile[] = [];
  loading = false;
  errorMessage = '';
  
  // Pagination
  currentPage = 1;
  pageSize = 10;
  totalPages = 1;
  
  // Sort
  sortField: keyof UserProfile = 'userId';
  sortDirection: 'asc' | 'desc' = 'asc';
  
  // Form state
  showForm = false;
  showDeleteConfirm = false;
  selectedUser: UserProfile | null = null;
  isEditMode = false;

  // Make Math available to template
  Math = Math;

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  /**
   * Load all users
   */
  loadUsers(): void {
    this.loading = true;
    this.errorMessage = '';
    
    this.userService.getAllUsers().subscribe({
      next: (users) => {
        this.users = users;
        this.applyFiltersAndSort();
        this.loading = false;
      },
      error: (error) => {
        console.error('Failed to load users:', error);
        this.errorMessage = 'ユーザーの読み込みに失敗しました。';
        this.loading = false;
      }
    });
  }

  /**
   * Apply sorting and pagination
   */
  applyFiltersAndSort(): void {
    // Sort
    const sorted = [...this.users].sort((a, b) => {
      const aVal = a[this.sortField];
      const bVal = b[this.sortField];
      
      if (aVal === undefined || aVal === null) return 1;
      if (bVal === undefined || bVal === null) return -1;
      
      const comparison = aVal < bVal ? -1 : aVal > bVal ? 1 : 0;
      return this.sortDirection === 'asc' ? comparison : -comparison;
    });
    
    // Pagination
    this.totalPages = Math.ceil(sorted.length / this.pageSize);
    const startIndex = (this.currentPage - 1) * this.pageSize;
    const endIndex = startIndex + this.pageSize;
    this.filteredUsers = sorted.slice(startIndex, endIndex);
  }

  /**
   * Sort by field
   */
  sortBy(field: keyof UserProfile): void {
    if (this.sortField === field) {
      this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortField = field;
      this.sortDirection = 'asc';
    }
    this.applyFiltersAndSort();
  }

  /**
   * Go to page
   */
  goToPage(page: number): void {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
      this.applyFiltersAndSort();
    }
  }

  /**
   * Open create form
   */
  openCreateForm(): void {
    this.selectedUser = null;
    this.isEditMode = false;
    this.showForm = true;
  }

  /**
   * Open edit form
   */
  openEditForm(user: UserProfile): void {
    this.selectedUser = user;
    this.isEditMode = true;
    this.showForm = true;
  }

  /**
   * Close form
   */
  closeForm(): void {
    this.showForm = false;
    this.selectedUser = null;
    this.isEditMode = false;
  }

  /**
   * Handle form save
   */
  onFormSave(user: UserProfile): void {
    this.closeForm();
    this.loadUsers();
  }

  /**
   * Open delete confirmation
   */
  openDeleteConfirm(user: UserProfile): void {
    this.selectedUser = user;
    this.showDeleteConfirm = true;
  }

  /**
   * Close delete confirmation
   */
  closeDeleteConfirm(): void {
    this.showDeleteConfirm = false;
    this.selectedUser = null;
  }

  /**
   * Handle delete confirmation
   */
  onDeleteConfirm(): void {
    this.closeDeleteConfirm();
    this.loadUsers();
  }

  /**
   * Get page numbers for pagination
   */
  getPageNumbers(): number[] {
    const pages: number[] = [];
    const maxPages = 5; // Show max 5 page numbers
    
    let startPage = Math.max(1, this.currentPage - Math.floor(maxPages / 2));
    let endPage = Math.min(this.totalPages, startPage + maxPages - 1);
    
    if (endPage - startPage < maxPages - 1) {
      startPage = Math.max(1, endPage - maxPages + 1);
    }
    
    for (let i = startPage; i <= endPage; i++) {
      pages.push(i);
    }
    
    return pages;
  }
}
