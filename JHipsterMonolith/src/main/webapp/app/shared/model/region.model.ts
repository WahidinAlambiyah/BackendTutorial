export interface IRegion {
  id?: number;
  name?: string;
  unm49Code?: string | null;
  isoAlpha2Code?: string | null;
}

export class Region implements IRegion {
  constructor(
    public id?: number,
    public name?: string,
    public unm49Code?: string | null,
    public isoAlpha2Code?: string | null,
  ) {}
}
