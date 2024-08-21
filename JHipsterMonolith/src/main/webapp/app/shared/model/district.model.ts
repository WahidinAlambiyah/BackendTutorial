import { type ICity } from '@/shared/model/city.model';

export interface IDistrict {
  id?: number;
  name?: string;
  code?: string;
  city?: ICity | null;
}

export class District implements IDistrict {
  constructor(
    public id?: number,
    public name?: string,
    public code?: string,
    public city?: ICity | null,
  ) {}
}
