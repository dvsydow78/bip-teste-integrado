import { Routes } from '@angular/router';
import { ServiceUnavailable } from './features/pages/service-unavailable/service-unavailable';
import { CriarBeneficioComponent } from './features/pages/beneficio/criar-beneficio/criar-beneficio';
import { EditarBeneficioComponent } from './features/pages/beneficio/editar-beneficio/editar-beneficio';
import { ListaBeneficiosComponent } from './features/pages/beneficio/lista-beneficios/lista-beneficios';
import { RemoverBeneficioComponent } from './features/pages/beneficio/remover-beneficio/remover-beneficio';
import { TransferenciaComponent } from './features/pages/beneficio/transferencia-beneficio/transferencia-beneficio';

export const routes: Routes = [
  { path: '',             redirectTo: 'lista', pathMatch: 'full' },
  { path: 'lista',        component: ListaBeneficiosComponent },
  { path: 'novo',         component: CriarBeneficioComponent },
  { path: 'editar/:id',   component: EditarBeneficioComponent },
  { path: 'remover/:id',  component: RemoverBeneficioComponent },
  { path: 'transferencia', component: TransferenciaComponent },
  { path: 'erro-servico', component: ServiceUnavailable },
  { path: '**',           redirectTo: 'lista' },
];