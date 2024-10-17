import { type IMstDriver } from '@/shared/model/mst-driver.model';
import { type ITrxOrder } from '@/shared/model/trx-order.model';

import { type DeliveryStatus } from '@/shared/model/enumerations/delivery-status.model';
export interface ITrxDelivery {
  id?: number;
  deliveryAddress?: string;
  deliveryStatus?: keyof typeof DeliveryStatus;
  assignedDriver?: string | null;
  estimatedDeliveryTime?: Date | null;
  driver?: IMstDriver | null;
  trxOrder?: ITrxOrder | null;
}

export class TrxDelivery implements ITrxDelivery {
  constructor(
    public id?: number,
    public deliveryAddress?: string,
    public deliveryStatus?: keyof typeof DeliveryStatus,
    public assignedDriver?: string | null,
    public estimatedDeliveryTime?: Date | null,
    public driver?: IMstDriver | null,
    public trxOrder?: ITrxOrder | null,
  ) {}
}
