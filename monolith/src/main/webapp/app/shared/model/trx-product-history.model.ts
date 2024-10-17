export interface ITrxProductHistory {
  id?: number;
  oldPrice?: number | null;
  newPrice?: number | null;
  changeDate?: Date;
}

export class TrxProductHistory implements ITrxProductHistory {
  constructor(
    public id?: number,
    public oldPrice?: number | null,
    public newPrice?: number | null,
    public changeDate?: Date,
  ) {}
}
