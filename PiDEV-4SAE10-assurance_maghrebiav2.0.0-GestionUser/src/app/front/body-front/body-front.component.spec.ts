import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BodyFrontComponent } from './body-front.component';

describe('BodyFrontComponent', () => {
  let component: BodyFrontComponent;
  let fixture: ComponentFixture<BodyFrontComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BodyFrontComponent]
    });
    fixture = TestBed.createComponent(BodyFrontComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
