import { defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import TrxTestimonialService from './trx-testimonial.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { useDateFormat } from '@/shared/composables';
import { type ITrxTestimonial } from '@/shared/model/trx-testimonial.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'TrxTestimonialDetails',
  setup() {
    const dateFormat = useDateFormat();
    const trxTestimonialService = inject('trxTestimonialService', () => new TrxTestimonialService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const dataUtils = useDataUtils();

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const trxTestimonial: Ref<ITrxTestimonial> = ref({});

    const retrieveTrxTestimonial = async trxTestimonialId => {
      try {
        const res = await trxTestimonialService().find(trxTestimonialId);
        trxTestimonial.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.trxTestimonialId) {
      retrieveTrxTestimonial(route.params.trxTestimonialId);
    }

    return {
      ...dateFormat,
      alertService,
      trxTestimonial,

      ...dataUtils,

      previousState,
      t$: useI18n().t,
    };
  },
});
