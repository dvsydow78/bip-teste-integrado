import { ComponentFixture, TestBed } from '@angular/core/testing';
<<<<<<< HEAD

import { RemoverBeneficio } from './remover-beneficio';

describe('RemoverBeneficio', () => {
  let component: RemoverBeneficio;
  let fixture: ComponentFixture<RemoverBeneficio>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RemoverBeneficio],
    }).compileComponents();

    fixture = TestBed.createComponent(RemoverBeneficio);
    component = fixture.componentInstance;
    await fixture.whenStable();
=======
import { RemoverBeneficioComponent } from './remover-beneficio';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

describe('RemoverBeneficio', () => {
  let component: RemoverBeneficioComponent;
  let fixture: ComponentFixture<RemoverBeneficioComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RemoverBeneficioComponent, NoopAnimationsModule],
      providers: [
        provideHttpClient(),
        provideRouter([]),
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(RemoverBeneficioComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
>>>>>>> dad4441dbaa060a6db8ee5b2e5521c7ecfb61a3e
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
<<<<<<< HEAD
});
=======
});
>>>>>>> dad4441dbaa060a6db8ee5b2e5521c7ecfb61a3e
