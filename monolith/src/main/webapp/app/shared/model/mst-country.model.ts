import { type IMstRegion } from '@/shared/model/mst-region.model';

export interface IMstCountry {
  id?: number;
  name?: string;
  unm49Code?: string | null;
  isoAlpha2Code?: string | null;
  region?: IMstRegion | null;
}

export class MstCountry implements IMstCountry {
  constructor(
    public id?: number,
    public name?: string,
    public unm49Code?: string | null,
    public isoAlpha2Code?: string | null,
    public region?: IMstRegion | null,
  ) {}
}
