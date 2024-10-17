export interface ITrxCoupon {
  id?: number;
  code?: string;
  discountAmount?: number;
  validUntil?: Date;
  minPurchase?: number | null;
}

export class TrxCoupon implements ITrxCoupon {
  constructor(
    public id?: number,
    public code?: string,
    public discountAmount?: number,
    public validUntil?: Date,
    public minPurchase?: number | null,
  ) {}
}
