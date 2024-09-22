import { type IService } from '@/shared/model/service.model';
import { type ITestimonial } from '@/shared/model/testimonial.model';

import { type EventStatus } from '@/shared/model/enumerations/event-status.model';
export interface IEvent {
  id?: number;
  title?: string;
  description?: string | null;
  date?: Date;
  location?: string | null;
  capacity?: number | null;
  price?: number | null;
  status?: keyof typeof EventStatus | null;
  service?: IService | null;
  testimonial?: ITestimonial | null;
}

export class Event implements IEvent {
  constructor(
    public id?: number,
    public title?: string,
    public description?: string | null,
    public date?: Date,
    public location?: string | null,
    public capacity?: number | null,
    public price?: number | null,
    public status?: keyof typeof EventStatus | null,
    public service?: IService | null,
    public testimonial?: ITestimonial | null,
  ) {}
}
