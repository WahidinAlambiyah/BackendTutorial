import { type ICountry } from '@/shared/model/country.model';

export interface IProvince {
  id?: number;
  name?: string;
  code?: string;
  country?: ICountry | null;
}

export class Province implements IProvince {
  constructor(
    public id?: number,
    public name?: string,
    public code?: string,
    public country?: ICountry | null,
  ) {}
}
