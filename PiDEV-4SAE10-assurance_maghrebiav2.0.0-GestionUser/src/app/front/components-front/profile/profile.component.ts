import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {
  userProfile: any = {};
  errorMessage: string = '';
  successMessage: string = '';
  twoFactorEnabled: boolean = false;
  username: string = '';
  qrCodeUrl: string = '';
  secret: string = '';
  verificationCode: string = '';

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    this.loadUserProfile();
  }

  private loadUserProfile(): void {
    this.authService.getUserProfile().subscribe({
      next: (response: any) => {
        if (response && response.username) {
          this.userProfile = response;
          this.username = response.username;
          this.twoFactorEnabled = response.is2fa_enabled || false;
        } else {
          this.errorMessage = 'Erreur: Nom d\'utilisateur introuvable.';
        }
      },
      error: (error: any) => {
        this.errorMessage = 'Impossible de récupérer les informations du profil.';
        console.error(error);
        this.router.navigate(['/login']);
      }
    });
  }

  enable2FA(): void {
    if (!this.username) {
      this.errorMessage = 'Nom d\'utilisateur non disponible';
      return;
    }

    const token = this.authService.getToken();
    if (!token) {
      this.errorMessage = 'Vous devez être connecté pour activer la 2FA';
      this.router.navigate(['/login']);
      return;
    }

    console.log('Attempting to enable 2FA for user:', this.username);
    
    this.authService.enable2FA(this.username).subscribe({
      next: (response: any) => {
        console.log('2FA enable success:', response);
        this.twoFactorEnabled = true;
        this.successMessage = 'Authentification à deux facteurs activée avec succès. Veuillez configurer votre application d\'authentification.';
        this.qrCodeUrl = response.qrCodeUrl;
        this.secret = response.secret;
      },
      error: (error: any) => {
        console.error('2FA enable error:', error);
        if (error.status === 403) {
          this.errorMessage = 'Vous devez être connecté pour activer la 2FA';
          this.router.navigate(['/login']);
        } else {
          this.errorMessage = error.error?.message || 'Erreur lors de l\'activation de l\'authentification à deux facteurs.';
        }
      }
    });
  }

  verify2FA(): void {
    if (!this.verificationCode || this.verificationCode.length !== 6) {
      this.errorMessage = 'Veuillez entrer un code de vérification valide (6 chiffres).';
      return;
    }

    this.authService.verify2FACode(this.verificationCode, this.username).subscribe({
      next: () => {
        this.successMessage = 'Authentification à deux facteurs configurée avec succès !';
        this.qrCodeUrl = ''; // Cacher le QR code une fois configuré
      },
      error: (error: any) => {
        this.errorMessage = error.error?.error || 'Erreur lors de la vérification du code.';
        console.error('Error details:', error);
      }
    });
  }

  disable2FA(): void {
    this.errorMessage = 'La désactivation de la 2FA n\'est pas encore implémentée.';
  }

  logout(): void {
    this.authService.logout().subscribe({
      next: () => {
        this.authService.clearToken();
        this.router.navigate(['/login']);
      },
      error: (error: any) => {
        console.error('Erreur lors de la déconnexion:', error);
        this.authService.clearToken();
        this.router.navigate(['/login']);
      }
    });
  }

  private handleError(error: any): void {
    if (error.status === 403) {
      alert('Erreur d\'authentification: Le token est invalide ou expiré.');
      this.router.navigate(['/login']);
    } else if (error.status === 404) {
      alert('Utilisateur non trouvé. Veuillez vérifier votre compte.');
    } else {
      alert('Une erreur est survenue. Veuillez réessayer plus tard.');
    }
  }
}
