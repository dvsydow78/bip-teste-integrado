import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ServiceUnavailable } from './service-unavailable';

describe('ServiceUnavailable', () => {
  let component: ServiceUnavailable;
  let fixture: ComponentFixture<ServiceUnavailable>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ServiceUnavailable],
    }).compileComponents();

    fixture = TestBed.createComponent(ServiceUnavailable);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
