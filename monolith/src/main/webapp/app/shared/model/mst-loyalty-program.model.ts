import { type IMstCustomer } from '@/shared/model/mst-customer.model';

export interface IMstLoyaltyProgram {
  id?: number;
  pointsEarned?: number | null;
  membershipTier?: string | null;
  customer?: IMstCustomer | null;
}

export class MstLoyaltyProgram implements IMstLoyaltyProgram {
  constructor(
    public id?: number,
    public pointsEarned?: number | null,
    public membershipTier?: string | null,
    public customer?: IMstCustomer | null,
  ) {}
}
