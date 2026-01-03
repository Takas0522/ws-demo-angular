// Common models
export interface ErrorResponse {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
}

export interface ApiResponse<T> {
  data: T;
  message?: string;
  success: boolean;
}
