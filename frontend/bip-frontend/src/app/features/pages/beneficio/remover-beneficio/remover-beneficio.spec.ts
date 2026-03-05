import { ComponentFixture, TestBed } from '@angular/core/testing';
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
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});