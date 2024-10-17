export interface IMstSupplier {
  id?: number;
  name?: string;
  contactInfo?: string | null;
  address?: string | null;
  rating?: number | null;
}

export class MstSupplier implements IMstSupplier {
  constructor(
    public id?: number,
    public name?: string,
    public contactInfo?: string | null,
    public address?: string | null,
    public rating?: number | null,
  ) {}
}
