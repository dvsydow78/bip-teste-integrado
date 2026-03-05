import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TransferenciaBeneficio } from './transferencia-beneficio';

describe('TransferenciaBeneficio', () => {
  let component: TransferenciaBeneficio;
  let fixture: ComponentFixture<TransferenciaBeneficio>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TransferenciaBeneficio],
    }).compileComponents();

    fixture = TestBed.createComponent(TransferenciaBeneficio);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
