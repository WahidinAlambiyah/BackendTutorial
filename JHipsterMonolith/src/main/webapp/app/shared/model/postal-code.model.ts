import { type ISubDistrict } from '@/shared/model/sub-district.model';

export interface IPostalCode {
  id?: number;
  code?: string;
  subDistrict?: ISubDistrict | null;
}

export class PostalCode implements IPostalCode {
  constructor(
    public id?: number,
    public code?: string,
    public subDistrict?: ISubDistrict | null,
  ) {}
}
