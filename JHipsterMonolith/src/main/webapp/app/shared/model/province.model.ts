import { type ICountry } from '@/shared/model/country.model';

export interface IProvince {
  id?: number;
  name?: string;
  unm49Code?: string | null;
  isoAlpha2Code?: string | null;
  country?: ICountry | null;
}

export class Province implements IProvince {
  constructor(
    public id?: number,
    public name?: string,
    public unm49Code?: string | null,
    public isoAlpha2Code?: string | null,
    public country?: ICountry | null,
  ) {}
}
