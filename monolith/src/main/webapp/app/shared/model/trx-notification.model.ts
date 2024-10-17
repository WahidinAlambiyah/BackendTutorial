import { type IMstCustomer } from '@/shared/model/mst-customer.model';

export interface ITrxNotification {
  id?: number;
  recipient?: string;
  messageType?: string;
  content?: string;
  sentAt?: Date;
  customer?: IMstCustomer | null;
}

export class TrxNotification implements ITrxNotification {
  constructor(
    public id?: number,
    public recipient?: string,
    public messageType?: string,
    public content?: string,
    public sentAt?: Date,
    public customer?: IMstCustomer | null,
  ) {}
}
