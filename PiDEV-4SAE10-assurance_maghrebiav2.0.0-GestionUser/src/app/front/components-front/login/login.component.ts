import { Component, OnInit } from '@angular/core';
import { AuthService, LoginResponse } from '../../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  username = '';
  password = '';
  errorMessage = '';
  show2FAInput = false;
  twoFactorCode = '';
  loading = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {}

  login(): void {
    if (!this.username || !this.password) {
      this.errorMessage = 'Veuillez remplir tous les champs.';
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    this.authService.login(this.username, this.password).subscribe({
      next: (response: LoginResponse) => {
        console.log('Login response:', response);
        
        if (response.requires2FA) {
          console.log('2FA required, redirecting to 2FA page');
          this.router.navigate(['/two-factor'], { 
            queryParams: { username: this.username },
            replaceUrl: true 
          });
        } else if (response.token) {
          this.authService.saveToken(response.token);
          this.router.navigate(['/profile']);
        }
      },
      error: (error: any) => {
        console.error('Login error:', error);
        
        // Vérifier si l'erreur est due à la 2FA
        if (error.status === 403 && error.error && error.error.requires2FA) {
          console.log('2FA required from error response, redirecting...');
          this.router.navigate(['/two-factor'], { 
            queryParams: { username: this.username },
            replaceUrl: true 
          });
        } else {
          this.errorMessage = error.message || 'Erreur lors de la connexion.';
        }
      },
      complete: () => {
        this.loading = false;
      }
    });
  }

  async verify2FA() {
    try {
      this.loading = true;
      this.errorMessage = '';

      const response = await this.authService.verify2FACode(this.twoFactorCode, this.username).toPromise();
      await this.handleSuccessfulLogin(response);
    } catch (error: any) {
      this.errorMessage = error.error?.error || 'Code 2FA incorrect !';
      console.error('Error details:', error);
    } finally {
      this.loading = false;
    }
  }

  private async handleSuccessfulLogin(response: LoginResponse) {
    if (response.token) {
      this.authService.saveToken(response.token);
      this.router.navigate(['/profile']);
    } else {
      this.errorMessage = 'Token non reçu lors de la connexion';
      console.error('No token in response:', response);
    }
  }
}
