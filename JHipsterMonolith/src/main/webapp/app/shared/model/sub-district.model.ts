import { type IDistrict } from '@/shared/model/district.model';

export interface ISubDistrict {
  id?: number;
  name?: string;
  code?: string;
  district?: IDistrict | null;
}

export class SubDistrict implements ISubDistrict {
  constructor(
    public id?: number,
    public name?: string,
    public code?: string,
    public district?: IDistrict | null,
  ) {}
}
