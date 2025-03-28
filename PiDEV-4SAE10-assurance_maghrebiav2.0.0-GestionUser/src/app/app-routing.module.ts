import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomePageComponent } from './front/components-front/home-page/home-page.component';
import { BodyFrontComponent } from './front/body-front/body-front.component';
import { OtherPageComponent } from './front/components-front/other-page/other-page.component';
import { LoginComponent } from './front/components-front/login/login.component';
import { BodyBackComponent } from './back/body-back/body-back.component';
import { HomeBackComponent } from './back/components-back/home-back/home-back.component';
import { TablesComponent } from './back/components-back/tables/tables.component';
import { RembousementComponent } from './back/components-back/rembousement/rembousement.component';
import { FormesComponent } from './back/components-back/formes/formes.component';
import { ButtonsComponent } from './back/components-back/buttons/buttons.component';
import { RegisterComponent } from './front/components-front/register/register.component';
import { ProfileComponent } from './front/components-front/profile/profile.component';
import { ForgotPasswordComponent } from './front/components-front/forgot-password/forgot-password.component';
import { ResetPasswordComponent } from './front/components-front/reset-password/reset-password.component';
import { TwoFactorComponent } from './front/components-front/two-factor/two-factor.component';

const routes: Routes = [
  {
    path: '',
    component: BodyFrontComponent,
    children: [
      { path: '', component: HomePageComponent },
      { path: 'other', component: OtherPageComponent }
    ]
  },
  {
    path: 'login',
    component: LoginComponent
  },
  { path: 'register', component: RegisterComponent },
  { path: 'profile', component: ProfileComponent },
  { path: 'forgot-password', component: ForgotPasswordComponent },
  { path: 'reset-password', component: ResetPasswordComponent },
  { path: 'two-factor', component: TwoFactorComponent },

  {
    path: 'back',
    component: BodyBackComponent, 
    children: [
      { path: '', component: HomeBackComponent },
      { path: 'tables', component: TablesComponent },
      { path: 'rembousement', component: RembousementComponent },
      { path: 'Forms', component: FormesComponent },
      { path: 'buttons', component: ButtonsComponent }
    ]
  },
  {
    path: '**',
    redirectTo: ''
  }
];


@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
