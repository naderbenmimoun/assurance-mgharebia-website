import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { RefundDetails } from 'src/app/back/Entiti/RefundDetails'; 
import { RefundAudit } from 'src/app/back/Entiti/RefundAudit';

@Injectable({
  providedIn: 'root'
})
export class RefaundService {
  private refundDetailsURL = 'http://localhost:8089/contrat/api/refunds';
  private refundAuditURL = 'http://localhost:8089/api/refundAudit';

  constructor(private provideHttpClient: HttpClient) { }

  // RefundDetails methods
  getRefundDetailsList(): Observable<RefundDetails[]> {
    return this.provideHttpClient.get<RefundDetails[]>(`${this.refundDetailsURL}/all`);
  }

  createRefundDetails(refundDetails: RefundDetails): Observable<object> {
    return this.provideHttpClient.post(this.refundDetailsURL, refundDetails);
  }

  getRefundDetailsById(id: number): Observable<RefundDetails> {
    return this.provideHttpClient.get<RefundDetails>(`${this.refundDetailsURL}/${id}`);
  }

  updateRefundDetails(id: number, refundDetails: RefundDetails): Observable<object> {
    return this.provideHttpClient.put(`${this.refundDetailsURL}/${id}`, refundDetails);
  }

  deleteRefundDetails(id: number): Observable<void> {
    return this.provideHttpClient.delete<void>(`${this.refundDetailsURL}/${id}`);
  }

  // RefundAudit methods
  getRefundAuditList(): Observable<RefundAudit[]> {
    return this.provideHttpClient.get<RefundAudit[]>(`${this.refundAuditURL}/all`);
  }

  createRefundAudit(refundAudit: RefundAudit): Observable<object> {
    return this.provideHttpClient.post(this.refundAuditURL, refundAudit);
  }

  getRefundAuditById(id: number): Observable<RefundAudit> {
    return this.provideHttpClient.get<RefundAudit>(`${this.refundAuditURL}/${id}`);
  }

  updateRefundAudit(id: number, refundAudit: RefundAudit): Observable<object> {
    return this.provideHttpClient.put(`${this.refundAuditURL}/${id}`, refundAudit);
  }

  deleteRefundAudit(id: number): Observable<void> {
    return this.provideHttpClient.delete<void>(`${this.refundAuditURL}/${id}`);
  }
}