import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { NavbarBackComponent } from './back/navbar-back/navbar-back.component';
import { BodyBackComponent } from './back/body-back/body-back.component';
import { FooterBackComponent } from './back/footer-back/footer-back.component';
import { NavbarFrontComponent } from './front/navbar-front/navbar-front.component';
import { FooterFrontComponent } from './front/footer-front/footer-front.component';
import { BodyFrontComponent } from './front/body-front/body-front.component';
import { HomePageComponent } from './front/components-front/home-page/home-page.component';
import { OtherPageComponent } from './front/components-front/other-page/other-page.component';
import { LoginComponent } from './front/components-front/login/login.component';
import { HomeBackComponent } from './back/components-back/home-back/home-back.component';
import { TablesComponent } from './back/components-back/tables/tables.component';
import { RembousementComponent } from './back/components-back/rembousement/rembousement.component';
import { FormesComponent } from './back/components-back/formes/formes.component';
import { ButtonsComponent } from './back/components-back/buttons/buttons.component';
import { AddRemboursementComponent } from './back/components-back/add-remboursement/add-remboursement.component';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ProfileComponent } from './front/components-front/profile/profile.component';
import { RegisterComponent } from './front/components-front/register/register.component';
import { ForgotPasswordComponent } from './front/components-front/forgot-password/forgot-password.component';
import { ResetPasswordComponent } from './front/components-front/reset-password/reset-password.component';
import { QRCodeModule } from 'angularx-qrcode';
import { AuthService } from './services/auth.service';
import { TwoFactorComponent } from './front/components-front/two-factor/two-factor.component';

@NgModule({
  declarations: [
    AppComponent,
    NavbarBackComponent,
    BodyBackComponent,
    FooterBackComponent,
    NavbarFrontComponent,
    FooterFrontComponent,
    BodyFrontComponent,
    HomePageComponent,
    OtherPageComponent,
    LoginComponent,
    HomeBackComponent,
    TablesComponent,
    RembousementComponent,
    FormesComponent,
    ButtonsComponent,
    AddRemboursementComponent,
    ProfileComponent,
    RegisterComponent,
    ForgotPasswordComponent,
    ResetPasswordComponent,
    TwoFactorComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    QRCodeModule
  ],
  providers: [AuthService],
  bootstrap: [AppComponent]
})
export class AppModule { }
