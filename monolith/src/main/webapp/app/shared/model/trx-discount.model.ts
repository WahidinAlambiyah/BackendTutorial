export interface ITrxDiscount {
  id?: number;
  discountPercentage?: number;
  startDate?: Date;
  endDate?: Date;
}

export class TrxDiscount implements ITrxDiscount {
  constructor(
    public id?: number,
    public discountPercentage?: number,
    public startDate?: Date,
    public endDate?: Date,
  ) {}
}
