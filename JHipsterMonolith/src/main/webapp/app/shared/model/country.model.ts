import { type IRegion } from '@/shared/model/region.model';

export interface ICountry {
  id?: number;
  name?: string;
  code?: string;
  region?: IRegion | null;
}

export class Country implements ICountry {
  constructor(
    public id?: number,
    public name?: string,
    public code?: string,
    public region?: IRegion | null,
  ) {}
}
