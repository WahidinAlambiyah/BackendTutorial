import { type OrderStatus } from '@/shared/model/enumerations/order-status.model';
export interface ITrxOrderHistory {
  id?: number;
  previousStatus?: keyof typeof OrderStatus;
  newStatus?: keyof typeof OrderStatus;
  changeDate?: Date;
}

export class TrxOrderHistory implements ITrxOrderHistory {
  constructor(
    public id?: number,
    public previousStatus?: keyof typeof OrderStatus,
    public newStatus?: keyof typeof OrderStatus,
    public changeDate?: Date,
  ) {}
}
