export interface IMstDriver {
  id?: number;
  name?: string;
  contactNumber?: string | null;
  vehicleDetails?: string | null;
}

export class MstDriver implements IMstDriver {
  constructor(
    public id?: number,
    public name?: string,
    public contactNumber?: string | null,
    public vehicleDetails?: string | null,
  ) {}
}
