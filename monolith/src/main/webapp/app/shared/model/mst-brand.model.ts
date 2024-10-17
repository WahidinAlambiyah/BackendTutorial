export interface IMstBrand {
  id?: number;
  name?: string;
  logo?: string | null;
  description?: string | null;
}

export class MstBrand implements IMstBrand {
  constructor(
    public id?: number,
    public name?: string,
    public logo?: string | null,
    public description?: string | null,
  ) {}
}
