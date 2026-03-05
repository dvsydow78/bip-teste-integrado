import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditarBeneficio } from './editar-beneficio';

describe('EditarBeneficio', () => {
  let component: EditarBeneficio;
  let fixture: ComponentFixture<EditarBeneficio>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditarBeneficio],
    }).compileComponents();

    fixture = TestBed.createComponent(EditarBeneficio);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
