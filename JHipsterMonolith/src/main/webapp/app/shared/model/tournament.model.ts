import { type IEvent } from '@/shared/model/event.model';

import { type TournamentType } from '@/shared/model/enumerations/tournament-type.model';
import { type TournamentStatus } from '@/shared/model/enumerations/tournament-status.model';
export interface ITournament {
  id?: number;
  name?: string;
  type?: keyof typeof TournamentType | null;
  prizeAmount?: number | null;
  startDate?: Date;
  endDate?: Date;
  location?: string | null;
  maxParticipants?: number | null;
  status?: keyof typeof TournamentStatus | null;
  event?: IEvent | null;
}

export class Tournament implements ITournament {
  constructor(
    public id?: number,
    public name?: string,
    public type?: keyof typeof TournamentType | null,
    public prizeAmount?: number | null,
    public startDate?: Date,
    public endDate?: Date,
    public location?: string | null,
    public maxParticipants?: number | null,
    public status?: keyof typeof TournamentStatus | null,
    public event?: IEvent | null,
  ) {}
}
