import { type ICity } from '@/shared/model/city.model';

export interface IDistrict {
  id?: number;
  name?: string;
  unm49Code?: string | null;
  isoAlpha2Code?: string | null;
  city?: ICity | null;
}

export class District implements IDistrict {
  constructor(
    public id?: number,
    public name?: string,
    public unm49Code?: string | null,
    public isoAlpha2Code?: string | null,
    public city?: ICity | null,
  ) {}
}
