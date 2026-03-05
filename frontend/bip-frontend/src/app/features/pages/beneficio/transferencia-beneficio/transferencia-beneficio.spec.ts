import { ComponentFixture, TestBed } from '@angular/core/testing';

<<<<<<< HEAD
import { TransferenciaBeneficio } from './transferencia-beneficio';

describe('TransferenciaBeneficio', () => {
  let component: TransferenciaBeneficio;
  let fixture: ComponentFixture<TransferenciaBeneficio>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TransferenciaBeneficio],
    }).compileComponents();

    fixture = TestBed.createComponent(TransferenciaBeneficio);
=======
import { TransferenciaBeneficioComponent } from './transferencia-beneficio';

describe('TransferenciaBeneficioComponent', () => {
  let component: TransferenciaBeneficioComponent;
  let fixture: ComponentFixture<TransferenciaBeneficioComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TransferenciaBeneficioComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(TransferenciaBeneficioComponent);
>>>>>>> dad4441dbaa060a6db8ee5b2e5521c7ecfb61a3e
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
