import { defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import MstServiceService from './mst-service.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { type IMstService } from '@/shared/model/mst-service.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'MstServiceDetails',
  setup() {
    const mstServiceService = inject('mstServiceService', () => new MstServiceService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const dataUtils = useDataUtils();

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const mstService: Ref<IMstService> = ref({});

    const retrieveMstService = async mstServiceId => {
      try {
        const res = await mstServiceService().find(mstServiceId);
        mstService.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.mstServiceId) {
      retrieveMstService(route.params.mstServiceId);
    }

    return {
      alertService,
      mstService,

      ...dataUtils,

      previousState,
      t$: useI18n().t,
    };
  },
});
