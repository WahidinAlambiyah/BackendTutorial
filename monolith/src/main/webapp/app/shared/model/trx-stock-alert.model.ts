export interface ITrxStockAlert {
  id?: number;
  alertThreshold?: number;
  currentStock?: number;
}

export class TrxStockAlert implements ITrxStockAlert {
  constructor(
    public id?: number,
    public alertThreshold?: number,
    public currentStock?: number,
  ) {}
}
