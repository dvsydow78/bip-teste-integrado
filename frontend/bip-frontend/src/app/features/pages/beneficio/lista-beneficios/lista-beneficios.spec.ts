import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListaBeneficios } from './lista-beneficios';

describe('ListaBeneficios', () => {
  let component: ListaBeneficios;
  let fixture: ComponentFixture<ListaBeneficios>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ListaBeneficios],
    }).compileComponents();

    fixture = TestBed.createComponent(ListaBeneficios);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
