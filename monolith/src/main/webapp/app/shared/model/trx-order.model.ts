import { type IMstCustomer } from '@/shared/model/mst-customer.model';

import { type OrderStatus } from '@/shared/model/enumerations/order-status.model';
import { type PaymentMethod } from '@/shared/model/enumerations/payment-method.model';
export interface ITrxOrder {
  id?: number;
  orderDate?: Date;
  deliveryDate?: Date | null;
  orderStatus?: keyof typeof OrderStatus;
  paymentMethod?: keyof typeof PaymentMethod;
  totalAmount?: number;
  mstCustomer?: IMstCustomer | null;
}

export class TrxOrder implements ITrxOrder {
  constructor(
    public id?: number,
    public orderDate?: Date,
    public deliveryDate?: Date | null,
    public orderStatus?: keyof typeof OrderStatus,
    public paymentMethod?: keyof typeof PaymentMethod,
    public totalAmount?: number,
    public mstCustomer?: IMstCustomer | null,
  ) {}
}
