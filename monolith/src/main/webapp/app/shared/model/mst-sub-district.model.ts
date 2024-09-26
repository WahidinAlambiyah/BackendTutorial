import { type IMstDistrict } from '@/shared/model/mst-district.model';

export interface IMstSubDistrict {
  id?: number;
  name?: string;
  unm49Code?: string | null;
  isoAlpha2Code?: string | null;
  district?: IMstDistrict | null;
}

export class MstSubDistrict implements IMstSubDistrict {
  constructor(
    public id?: number,
    public name?: string,
    public unm49Code?: string | null,
    public isoAlpha2Code?: string | null,
    public district?: IMstDistrict | null,
  ) {}
}
