import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { catchError, map, Observable, throwError } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8089/auth';
  private baseUrl = 'http://localhost:8089/api/user';

  constructor(private http: HttpClient) {}

  login(credentials: { username: string; password: string }): Observable<any> {
    return this.http.post(`${this.apiUrl}/login`, credentials).pipe(
      catchError(this.handleError)
    );
  }

  register(user: { email: string; password: string; username: string; role: string; phoneNumber: string }): Observable<any> {
    return this.http.post(`${this.apiUrl}/register`, user).pipe(
      catchError(this.handleError)
    );
  }

  logout(): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/logout`, {}, { withCredentials: true }).pipe(
      catchError(this.handleError)
    );
  }

  clearToken(): void {
    localStorage.removeItem('token');
  }

  saveToken(token: string): void {
    localStorage.setItem('token', token);
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  getUserProfile(): Observable<any> {
    const token = this.getToken();
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

    return this.http.get<any>(`${this.baseUrl}/profile`, { headers }).pipe(
      catchError(this.handleError)
    );
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  forgotPassword(email: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/forgot-password`, email, { responseType: 'text' });
  }

  resetPassword(token: string, newPassword: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/reset-password`, { token, password: newPassword }, { responseType: 'text' })
      .pipe(
        catchError(error => {
          console.error('Error occurred:', error);
          let errorMessage = 'Une erreur est survenue. Veuillez réessayer.';
          if (error.status === 400) {
            errorMessage = error.error || 'Token invalide ou expiré.';
          }
          return throwError(() => new Error(errorMessage));
        })
      );
  }

  enable2FA(type: 'SMS' | 'EMAIL'): Observable<any> {
    const token = this.getToken();
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.post(`${this.apiUrl}/enable-2fa`, { type }, { headers }).pipe(
      catchError(this.handleError)
    );
  }

  send2FACode(): Observable<any> {
    const token = this.getToken();
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.post(`${this.apiUrl}/send-2fa-code`, {}, { headers }).pipe(
      catchError(this.handleError)
    );
  }

  verify2FACode(code: string): Observable<any> {
    const token = this.getToken();
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.post(`${this.apiUrl}/verify-2fa`, { code }, { headers }).pipe(
      catchError(this.handleError)
    );
  }

  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'Une erreur est survenue. Veuillez réessayer.';
    if (error.error instanceof ErrorEvent) {
      errorMessage = `Une erreur côté client: ${error.error.message}`;
    } else {
      if (error.status === 0) {
        errorMessage = 'Serveur inaccessible.';
      } else {
        errorMessage = `Erreur ${error.status}: ${error.error.message || error.message}`;
      }
    }
    return throwError(() => new Error(errorMessage));
  }
} 