import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NavbarFrontComponent } from './navbar-front.component';

describe('NavbarFrontComponent', () => {
  let component: NavbarFrontComponent;
  let fixture: ComponentFixture<NavbarFrontComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [NavbarFrontComponent]
    });
    fixture = TestBed.createComponent(NavbarFrontComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
