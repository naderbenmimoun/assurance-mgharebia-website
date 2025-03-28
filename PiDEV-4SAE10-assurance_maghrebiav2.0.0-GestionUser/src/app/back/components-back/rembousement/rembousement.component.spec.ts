import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RembousementComponent } from './rembousement.component';

describe('RembousementComponent', () => {
  let component: RembousementComponent;
  let fixture: ComponentFixture<RembousementComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RembousementComponent]
    });
    fixture = TestBed.createComponent(RembousementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
