import { defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import MstPostalCodeService from './mst-postal-code.service';
import { type IMstPostalCode } from '@/shared/model/mst-postal-code.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'MstPostalCodeDetails',
  setup() {
    const mstPostalCodeService = inject('mstPostalCodeService', () => new MstPostalCodeService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const mstPostalCode: Ref<IMstPostalCode> = ref({});

    const retrieveMstPostalCode = async mstPostalCodeId => {
      try {
        const res = await mstPostalCodeService().find(mstPostalCodeId);
        mstPostalCode.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.mstPostalCodeId) {
      retrieveMstPostalCode(route.params.mstPostalCodeId);
    }

    return {
      alertService,
      mstPostalCode,

      previousState,
      t$: useI18n().t,
    };
  },
});
