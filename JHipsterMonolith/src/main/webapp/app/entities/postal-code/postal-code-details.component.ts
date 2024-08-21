import { defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import PostalCodeService from './postal-code.service';
import { type IPostalCode } from '@/shared/model/postal-code.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'PostalCodeDetails',
  setup() {
    const postalCodeService = inject('postalCodeService', () => new PostalCodeService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const postalCode: Ref<IPostalCode> = ref({});

    const retrievePostalCode = async postalCodeId => {
      try {
        const res = await postalCodeService().find(postalCodeId);
        postalCode.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.postalCodeId) {
      retrievePostalCode(route.params.postalCodeId);
    }

    return {
      alertService,
      postalCode,

      previousState,
      t$: useI18n().t,
    };
  },
});
