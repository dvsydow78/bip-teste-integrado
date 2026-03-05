import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CriarBeneficioComponent } from './criar-beneficio';

describe('CriarBeneficioComponent', () => {
  let component: CriarBeneficioComponent;
  let fixture: ComponentFixture<CriarBeneficioComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CriarBeneficioComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(CriarBeneficioComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
