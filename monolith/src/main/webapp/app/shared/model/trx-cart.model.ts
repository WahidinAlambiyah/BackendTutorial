import { type IMstCustomer } from '@/shared/model/mst-customer.model';

export interface ITrxCart {
  id?: number;
  totalPrice?: number;
  customer?: IMstCustomer | null;
}

export class TrxCart implements ITrxCart {
  constructor(
    public id?: number,
    public totalPrice?: number,
    public customer?: IMstCustomer | null,
  ) {}
}
