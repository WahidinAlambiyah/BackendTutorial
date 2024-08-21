import { type IProvince } from '@/shared/model/province.model';

export interface ICity {
  id?: number;
  name?: string;
  code?: string;
  province?: IProvince | null;
}

export class City implements ICity {
  constructor(
    public id?: number,
    public name?: string,
    public code?: string,
    public province?: IProvince | null,
  ) {}
}
