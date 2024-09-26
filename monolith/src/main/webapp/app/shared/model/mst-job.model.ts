import { type IMstTask } from '@/shared/model/mst-task.model';
import { type IMstEmployee } from '@/shared/model/mst-employee.model';

export interface IMstJob {
  id?: number;
  jobTitle?: string | null;
  minSalary?: number | null;
  maxSalary?: number | null;
  tasks?: IMstTask[] | null;
  employee?: IMstEmployee | null;
}

export class MstJob implements IMstJob {
  constructor(
    public id?: number,
    public jobTitle?: string | null,
    public minSalary?: number | null,
    public maxSalary?: number | null,
    public tasks?: IMstTask[] | null,
    public employee?: IMstEmployee | null,
  ) {}
}
