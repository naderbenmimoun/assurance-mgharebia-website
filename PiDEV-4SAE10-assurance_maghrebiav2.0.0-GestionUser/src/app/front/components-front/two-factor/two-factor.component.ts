import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-two-factor',
  templateUrl: './two-factor.component.html',
  styleUrls: ['./two-factor.component.css']
})
export class TwoFactorComponent implements OnInit {
  twoFactorCode: string = '';
  errorMessage: string = '';
  loading: boolean = false;
  username: string = '';
  waitingForToken: boolean = false;
  maxRetries: number = 10;
  retryCount: number = 0;

  constructor(
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.username = params['username'];
      if (!this.username) {
        console.error('No username provided in query params');
        this.router.navigate(['/login']);
      } else {
        console.log('Username received:', this.username);
      }
    });
  }

  verifyCode(): void {
    if (!this.twoFactorCode || this.twoFactorCode.length !== 6) {
      this.errorMessage = 'Veuillez entrer un code valide à 6 chiffres.';
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    console.log('Verifying 2FA code for user:', this.username);

    this.authService.verify2FACode(this.twoFactorCode, this.username).subscribe({
      next: (response: any) => {
        console.log('2FA verification response:', response);
        if (response.verified) {
          console.log('Code verified successfully');
          if (response.token) {
            console.log('Token received with verification response');
            this.router.navigate(['/profile']);
          } else {
            console.log('No token in response, checking login status');
            if (this.authService.isLoggedIn()) {
              console.log('User is logged in, redirecting to profile');
              this.router.navigate(['/profile']);
            } else {
              this.waitingForToken = true;
              this.checkTokenAndRedirect();
            }
          }
        } else {
          console.error('Verification failed');
          this.errorMessage = response.error || 'Erreur lors de la vérification du code.';
          this.loading = false;
        }
      },
      error: (error: any) => {
        console.error('2FA verification error:', error);
        this.errorMessage = error.error?.message || error.message || 'Code incorrect. Veuillez réessayer.';
        this.loading = false;
      }
    });
  }

  checkTokenAndRedirect(): void {
    // Vérifier si un token est disponible toutes les 500ms jusqu'à 5 secondes
    const checkInterval = setInterval(() => {
      this.retryCount++;
      if (this.authService.isLoggedIn()) {
        console.log('Token received, redirecting to profile');
        clearInterval(checkInterval);
        this.loading = false;
        this.router.navigate(['/profile']);
      } else if (this.retryCount >= this.maxRetries) {
        console.error('Failed to receive token after multiple retries');
        clearInterval(checkInterval);
        this.loading = false;
        this.errorMessage = 'Impossible d\'obtenir un token d\'authentification. Veuillez réessayer.';
      } else {
        console.log(`Waiting for token, retry ${this.retryCount}/${this.maxRetries}`);
      }
    }, 500);
  }
} 