export interface IRegion {
  id?: number;
  name?: string;
  code?: string;
}

export class Region implements IRegion {
  constructor(
    public id?: number,
    public name?: string,
    public code?: string,
  ) {}
}
