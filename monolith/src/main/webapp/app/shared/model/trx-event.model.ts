import { type IMstService } from '@/shared/model/mst-service.model';
import { type ITrxTestimonial } from '@/shared/model/trx-testimonial.model';

import { type EventStatus } from '@/shared/model/enumerations/event-status.model';
export interface ITrxEvent {
  id?: number;
  title?: string;
  description?: string | null;
  date?: Date;
  location?: string | null;
  capacity?: number | null;
  price?: number | null;
  status?: keyof typeof EventStatus | null;
  service?: IMstService | null;
  testimonial?: ITrxTestimonial | null;
}

export class TrxEvent implements ITrxEvent {
  constructor(
    public id?: number,
    public title?: string,
    public description?: string | null,
    public date?: Date,
    public location?: string | null,
    public capacity?: number | null,
    public price?: number | null,
    public status?: keyof typeof EventStatus | null,
    public service?: IMstService | null,
    public testimonial?: ITrxTestimonial | null,
  ) {}
}
