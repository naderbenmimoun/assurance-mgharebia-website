import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { NavbarFrontComponent } from './components-front/navbar-front/navbar-front.component';
import { FooterFrontComponent } from './components-front/footer-front/footer-front.component';
import { LoginComponent } from './components-front/login/login.component';
import { RegisterComponent } from './components-front/register/register.component';
import { ProfileComponent } from './components-front/profile/profile.component';
import { ForgotPasswordComponent } from './components-front/forgot-password/forgot-password.component';
import { ResetPasswordComponent } from './components-front/reset-password/reset-password.component';

@NgModule({
  declarations: [
    NavbarFrontComponent,
    FooterFrontComponent,
    LoginComponent,
    RegisterComponent,
    ProfileComponent,
    ForgotPasswordComponent,
    ResetPasswordComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    RouterModule
  ],
  exports: [
    NavbarFrontComponent,
    FooterFrontComponent,
    LoginComponent,
    RegisterComponent,
    ProfileComponent,
    ForgotPasswordComponent,
    ResetPasswordComponent
  ]
})
export class FrontModule { } 