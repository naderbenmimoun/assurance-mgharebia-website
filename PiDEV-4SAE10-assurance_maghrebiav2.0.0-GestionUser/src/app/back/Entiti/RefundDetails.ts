import { RefundAudit } from './RefundAudit';

export class RefundDetails {
refundId!: number ;
orderId!: number;
amount!: number;
reason!: string;
createdAt!: Date;
processedAt!: Date;
refundStatus!: RefundStatus;
refundAudit!: RefundAudit;
userId!: number;
}

export enum RefundStatus {
    PENDING = 'PENDING',
    APPROVED = 'APPROVED',
    REJECTED = 'REJECTED',
    PROCESSED = 'PROCESSED'
  }