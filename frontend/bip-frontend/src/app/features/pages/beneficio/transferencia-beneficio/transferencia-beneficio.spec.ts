import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TransferenciaBeneficioComponent } from './transferencia-beneficio';

describe('TransferenciaBeneficioComponent', () => {
  let component: TransferenciaBeneficioComponent;
  let fixture: ComponentFixture<TransferenciaBeneficioComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TransferenciaBeneficioComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(TransferenciaBeneficioComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
