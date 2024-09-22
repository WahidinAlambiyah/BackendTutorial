import { type IDistrict } from '@/shared/model/district.model';

export interface ISubDistrict {
  id?: number;
  name?: string;
  unm49Code?: string | null;
  isoAlpha2Code?: string | null;
  district?: IDistrict | null;
}

export class SubDistrict implements ISubDistrict {
  constructor(
    public id?: number,
    public name?: string,
    public unm49Code?: string | null,
    public isoAlpha2Code?: string | null,
    public district?: IDistrict | null,
  ) {}
}
