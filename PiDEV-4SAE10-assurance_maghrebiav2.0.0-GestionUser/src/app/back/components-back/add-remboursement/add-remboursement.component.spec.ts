import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddRemboursementComponent } from './add-remboursement.component';

describe('AddRemboursementComponent', () => {
  let component: AddRemboursementComponent;
  let fixture: ComponentFixture<AddRemboursementComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AddRemboursementComponent]
    });
    fixture = TestBed.createComponent(AddRemboursementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
