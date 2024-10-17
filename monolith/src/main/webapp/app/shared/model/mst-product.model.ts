import { type IMstCategory } from '@/shared/model/mst-category.model';
import { type IMstBrand } from '@/shared/model/mst-brand.model';
import { type IMstSupplier } from '@/shared/model/mst-supplier.model';

export interface IMstProduct {
  id?: number;
  name?: string;
  description?: string | null;
  price?: number;
  quantity?: number;
  barcode?: string | null;
  unitSize?: string | null;
  category?: IMstCategory | null;
  brand?: IMstBrand | null;
  mstSupplier?: IMstSupplier | null;
}

export class MstProduct implements IMstProduct {
  constructor(
    public id?: number,
    public name?: string,
    public description?: string | null,
    public price?: number,
    public quantity?: number,
    public barcode?: string | null,
    public unitSize?: string | null,
    public category?: IMstCategory | null,
    public brand?: IMstBrand | null,
    public mstSupplier?: IMstSupplier | null,
  ) {}
}
