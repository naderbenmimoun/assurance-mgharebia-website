import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {
  username: string = '';
  email: string = '';
  password: string = '';
  phoneNumber: string = '';
  role: string = 'CLIENT_PHYSIQUE';
  errorMessage: string = '';
  successMessage: string = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {}

  register(): void {
    if (!this.username || !this.email || !this.password || !this.phoneNumber) {
      this.errorMessage = 'Veuillez remplir tous les champs obligatoires.';
      return;
    }

    this.errorMessage = '';
    this.successMessage = '';

    this.authService.register({
      username: this.username,
      email: this.email,
      password: this.password,
      role: this.role,
      phoneNumber: this.phoneNumber
    }).subscribe({
      next: () => {
        this.successMessage = 'Inscription réussie ! Vous pouvez maintenant vous connecter.';
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 2000);
      },
      error: (error: any) => {
        this.errorMessage = error.message || 'Une erreur est survenue lors de l\'inscription.';
        console.error('Registration error:', error);
      }
    });
  }
}
