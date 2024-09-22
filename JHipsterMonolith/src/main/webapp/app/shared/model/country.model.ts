import { type IRegion } from '@/shared/model/region.model';

export interface ICountry {
  id?: number;
  name?: string;
  unm49Code?: string | null;
  isoAlpha2Code?: string | null;
  region?: IRegion | null;
}

export class Country implements ICountry {
  constructor(
    public id?: number,
    public name?: string,
    public unm49Code?: string | null,
    public isoAlpha2Code?: string | null,
    public region?: IRegion | null,
  ) {}
}
