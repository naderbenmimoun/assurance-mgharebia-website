import { TestBed } from '@angular/core/testing';

import { RefaundService } from './refaund.service';

describe('RefaundService', () => {
  let service: RefaundService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RefaundService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
