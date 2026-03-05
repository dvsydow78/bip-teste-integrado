import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Beneficio, BeneficioRequest, TransferenciaRequest } from '../../features/models/beneficio.model';

@Injectable({ providedIn: 'root' })
export class BeneficioService {

  private http = inject(HttpClient);
  private baseUrl = `${environment.apiUrl}/beneficios`;

  listar(): Observable<Beneficio[]> {
    return this.http.get<Beneficio[]>(this.baseUrl);
  }

  listarAtivos(): Observable<Beneficio[]> {
    return this.http.get<Beneficio[]>(`${this.baseUrl}/ativos`);
  }

  buscarPorId(id: number): Observable<Beneficio> {
    return this.http.get<Beneficio>(`${this.baseUrl}/${id}`);
  }

  criar(req: BeneficioRequest): Observable<Beneficio> {
    return this.http.post<Beneficio>(this.baseUrl, req);
  }

  atualizar(id: number, req: BeneficioRequest): Observable<Beneficio> {
    return this.http.put<Beneficio>(`${this.baseUrl}/${id}`, req);
  }

  desativar(id: number) {
    return this.http.delete(`${this.baseUrl}/${id}`);
  }

  transferir(req: TransferenciaRequest) {
    return this.http.post(`${this.baseUrl}/transferencia`, req);
  }
}