import { defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import TestimonialService from './testimonial.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { useDateFormat } from '@/shared/composables';
import { type ITestimonial } from '@/shared/model/testimonial.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'TestimonialDetails',
  setup() {
    const dateFormat = useDateFormat();
    const testimonialService = inject('testimonialService', () => new TestimonialService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const dataUtils = useDataUtils();

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const testimonial: Ref<ITestimonial> = ref({});

    const retrieveTestimonial = async testimonialId => {
      try {
        const res = await testimonialService().find(testimonialId);
        testimonial.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.testimonialId) {
      retrieveTestimonial(route.params.testimonialId);
    }

    return {
      ...dateFormat,
      alertService,
      testimonial,

      ...dataUtils,

      previousState,
      t$: useI18n().t,
    };
  },
});
