import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { UserProfile, CreateUserProfileRequest, UpdateUserProfileRequest } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private readonly API_URL = environment.apiUrl;

  constructor(private http: HttpClient) {}

  /**
   * Get all user profiles via BFF
   */
  getAllUsers(): Observable<UserProfile[]> {
    return this.http.get<UserProfile[]>(`${this.API_URL}/users`)
      .pipe(
        catchError(error => {
          console.error('Failed to fetch users:', error);
          return throwError(() => error);
        })
      );
  }

  /**
   * Get user profile by userId via BFF
   */
  getUserById(userId: number): Observable<UserProfile> {
    return this.http.get<UserProfile>(`${this.API_URL}/users/${userId}`)
      .pipe(
        catchError(error => {
          console.error(`Failed to fetch user ${userId}:`, error);
          return throwError(() => error);
        })
      );
  }

  /**
   * Create user profile via BFF
   */
  createUser(request: CreateUserProfileRequest): Observable<UserProfile> {
    return this.http.post<UserProfile>(`${this.API_URL}/users`, request)
      .pipe(
        catchError(error => {
          console.error('Failed to create user:', error);
          return throwError(() => error);
        })
      );
  }

  /**
   * Update user profile via BFF
   */
  updateUser(userId: number, request: UpdateUserProfileRequest): Observable<UserProfile> {
    return this.http.put<UserProfile>(`${this.API_URL}/users/${userId}`, request)
      .pipe(
        catchError(error => {
          console.error(`Failed to update user ${userId}:`, error);
          return throwError(() => error);
        })
      );
  }

  /**
   * Delete user profile via BFF
   */
  deleteUser(userId: number): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/users/${userId}`)
      .pipe(
        catchError(error => {
          console.error(`Failed to delete user ${userId}:`, error);
          return throwError(() => error);
        })
      );
  }
}
