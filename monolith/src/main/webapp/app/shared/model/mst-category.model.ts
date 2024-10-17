export interface IMstCategory {
  id?: number;
  name?: string;
  description?: string | null;
}

export class MstCategory implements IMstCategory {
  constructor(
    public id?: number,
    public name?: string,
    public description?: string | null,
  ) {}
}
