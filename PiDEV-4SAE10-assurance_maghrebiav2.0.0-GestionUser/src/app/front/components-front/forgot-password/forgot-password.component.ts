import { Component } from '@angular/core';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.css']
})
export class ForgotPasswordComponent {
  email: string = '';
  message: string = '';
  error: string = '';

  constructor(private authService: AuthService) {}

  sendResetLink(): void {
    this.authService.forgotPassword(this.email).subscribe({
      next: (response: any) => {
        this.message = 'Instructions envoyées à votre adresse email.';
        this.error = '';
      },
      error: (error: any) => {
        this.error = error.error.message || 'Une erreur est survenue.';
        this.message = '';
      }
    });
  }
}
