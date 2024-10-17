import { defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import MstDriverService from './mst-driver.service';
import { type IMstDriver } from '@/shared/model/mst-driver.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'MstDriverDetails',
  setup() {
    const mstDriverService = inject('mstDriverService', () => new MstDriverService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const mstDriver: Ref<IMstDriver> = ref({});

    const retrieveMstDriver = async mstDriverId => {
      try {
        const res = await mstDriverService().find(mstDriverId);
        mstDriver.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.mstDriverId) {
      retrieveMstDriver(route.params.mstDriverId);
    }

    return {
      alertService,
      mstDriver,

      previousState,
      t$: useI18n().t,
    };
  },
});
