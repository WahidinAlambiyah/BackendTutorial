import { defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import MstLoyaltyProgramService from './mst-loyalty-program.service';
import { type IMstLoyaltyProgram } from '@/shared/model/mst-loyalty-program.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'MstLoyaltyProgramDetails',
  setup() {
    const mstLoyaltyProgramService = inject('mstLoyaltyProgramService', () => new MstLoyaltyProgramService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const mstLoyaltyProgram: Ref<IMstLoyaltyProgram> = ref({});

    const retrieveMstLoyaltyProgram = async mstLoyaltyProgramId => {
      try {
        const res = await mstLoyaltyProgramService().find(mstLoyaltyProgramId);
        mstLoyaltyProgram.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.mstLoyaltyProgramId) {
      retrieveMstLoyaltyProgram(route.params.mstLoyaltyProgramId);
    }

    return {
      alertService,
      mstLoyaltyProgram,

      previousState,
      t$: useI18n().t,
    };
  },
});
