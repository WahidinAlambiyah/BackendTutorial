import { defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import CityService from './city.service';
import { type ICity } from '@/shared/model/city.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'CityDetails',
  setup() {
    const cityService = inject('cityService', () => new CityService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const city: Ref<ICity> = ref({});

    const retrieveCity = async cityId => {
      try {
        const res = await cityService().find(cityId);
        city.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.cityId) {
      retrieveCity(route.params.cityId);
    }

    return {
      alertService,
      city,

      previousState,
      t$: useI18n().t,
    };
  },
});
