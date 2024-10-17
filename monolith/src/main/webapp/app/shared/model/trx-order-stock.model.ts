import { type IMstSupplier } from '@/shared/model/mst-supplier.model';

export interface ITrxOrderStock {
  id?: number;
  quantityOrdered?: number;
  orderDate?: Date;
  expectedArrivalDate?: Date | null;
  supplier?: IMstSupplier | null;
}

export class TrxOrderStock implements ITrxOrderStock {
  constructor(
    public id?: number,
    public quantityOrdered?: number,
    public orderDate?: Date,
    public expectedArrivalDate?: Date | null,
    public supplier?: IMstSupplier | null,
  ) {}
}
