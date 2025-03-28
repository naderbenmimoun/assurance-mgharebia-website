import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.css']
})
export class ResetPasswordComponent implements OnInit {
  password: string = '';
  message: string = '';
  error: string = '';
  token: string = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.token = params['token'];
    });
  }

  resetPassword(): void {
    if (!this.password) {
      this.message = "Le mot de passe ne peut pas être vide.";
      return;
    }

    this.authService.resetPassword(this.token, this.password).subscribe({
      next: (response: any) => {
        this.message = "Votre mot de passe a été réinitialisé avec succès.";
        this.error = '';
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 2000);
      },
      error: (error: any) => {
        this.error = error.error.message || 'Une erreur est survenue.';
        this.message = '';
      }
    });
  }
}
