import { type IMstJob } from '@/shared/model/mst-job.model';

export interface IMstTask {
  id?: number;
  title?: string | null;
  description?: string | null;
  jobs?: IMstJob[] | null;
}

export class MstTask implements IMstTask {
  constructor(
    public id?: number,
    public title?: string | null,
    public description?: string | null,
    public jobs?: IMstJob[] | null,
  ) {}
}
