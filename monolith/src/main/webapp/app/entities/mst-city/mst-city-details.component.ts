import { defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import MstCityService from './mst-city.service';
import { type IMstCity } from '@/shared/model/mst-city.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'MstCityDetails',
  setup() {
    const mstCityService = inject('mstCityService', () => new MstCityService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const mstCity: Ref<IMstCity> = ref({});

    const retrieveMstCity = async mstCityId => {
      try {
        const res = await mstCityService().find(mstCityId);
        mstCity.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.mstCityId) {
      retrieveMstCity(route.params.mstCityId);
    }

    return {
      alertService,
      mstCity,

      previousState,
      t$: useI18n().t,
    };
  },
});
