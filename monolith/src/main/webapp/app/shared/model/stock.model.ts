import { type IMstProduct } from '@/shared/model/mst-product.model';

export interface IStock {
  id?: number;
  quantityAvailable?: number;
  reorderLevel?: number | null;
  expiryDate?: Date | null;
  product?: IMstProduct | null;
}

export class Stock implements IStock {
  constructor(
    public id?: number,
    public quantityAvailable?: number,
    public reorderLevel?: number | null,
    public expiryDate?: Date | null,
    public product?: IMstProduct | null,
  ) {}
}
