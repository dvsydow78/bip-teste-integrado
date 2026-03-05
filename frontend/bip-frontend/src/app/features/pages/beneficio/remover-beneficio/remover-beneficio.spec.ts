import { ComponentFixture, TestBed } from '@angular/core/testing';

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
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
