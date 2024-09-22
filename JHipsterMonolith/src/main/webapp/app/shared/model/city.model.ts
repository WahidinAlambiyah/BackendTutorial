import { type IProvince } from '@/shared/model/province.model';

export interface ICity {
  id?: number;
  name?: string;
  unm49Code?: string | null;
  isoAlpha2Code?: string | null;
  province?: IProvince | null;
}

export class City implements ICity {
  constructor(
    public id?: number,
    public name?: string,
    public unm49Code?: string | null,
    public isoAlpha2Code?: string | null,
    public province?: IProvince | null,
  ) {}
}
