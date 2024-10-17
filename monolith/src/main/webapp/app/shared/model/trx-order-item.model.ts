import { type ITrxOrder } from '@/shared/model/trx-order.model';
import { type IMstProduct } from '@/shared/model/mst-product.model';

export interface ITrxOrderItem {
  id?: number;
  quantity?: number;
  price?: number;
  order?: ITrxOrder | null;
  product?: IMstProduct | null;
}

export class TrxOrderItem implements ITrxOrderItem {
  constructor(
    public id?: number,
    public quantity?: number,
    public price?: number,
    public order?: ITrxOrder | null,
    public product?: IMstProduct | null,
  ) {}
}
