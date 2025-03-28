import { RefundDetails } from "./RefundDetails";

export class RefundAudit {
    id!: number;
    processedBy!: number;
    processedAt!: Date;
    auditReport!: Uint8Array;
    refundDetails!: RefundDetails;
  }