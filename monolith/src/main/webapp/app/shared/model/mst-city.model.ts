import { type IMstProvince } from '@/shared/model/mst-province.model';

export interface IMstCity {
  id?: number;
  name?: string;
  unm49Code?: string | null;
  isoAlpha2Code?: string | null;
  province?: IMstProvince | null;
}

export class MstCity implements IMstCity {
  constructor(
    public id?: number,
    public name?: string,
    public unm49Code?: string | null,
    public isoAlpha2Code?: string | null,
    public province?: IMstProvince | null,
  ) {}
}
