import { defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import MstCountryService from './mst-country.service';
import { type IMstCountry } from '@/shared/model/mst-country.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'MstCountryDetails',
  setup() {
    const mstCountryService = inject('mstCountryService', () => new MstCountryService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const mstCountry: Ref<IMstCountry> = ref({});

    const retrieveMstCountry = async mstCountryId => {
      try {
        const res = await mstCountryService().find(mstCountryId);
        mstCountry.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.mstCountryId) {
      retrieveMstCountry(route.params.mstCountryId);
    }

    return {
      alertService,
      mstCountry,

      previousState,
      t$: useI18n().t,
    };
  },
});
