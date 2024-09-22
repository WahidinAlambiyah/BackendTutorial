import { defineComponent, inject, onMounted, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';

import TestimonialService from './testimonial.service';
import { type ITestimonial } from '@/shared/model/testimonial.model';
import useDataUtils from '@/shared/data/data-utils.service';
import { useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Testimonial',
  setup() {
    const { t: t$ } = useI18n();
    const dateFormat = useDateFormat();
    const dataUtils = useDataUtils();
    const testimonialService = inject('testimonialService', () => new TestimonialService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const currentSearch = ref('');

    const testimonials: Ref<ITestimonial[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {
      currentSearch.value = '';
    };

    const retrieveTestimonials = async () => {
      isFetching.value = true;
      try {
        const res = currentSearch.value ? await testimonialService().search(currentSearch.value) : await testimonialService().retrieve();
        testimonials.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveTestimonials();
    };

    onMounted(async () => {
      await retrieveTestimonials();
    });

    const search = query => {
      if (!query) {
        return clear();
      }
      currentSearch.value = query;
      retrieveTestimonials();
    };

    const removeId: Ref<number> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: ITestimonial) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeTestimonial = async () => {
      try {
        await testimonialService().delete(removeId.value);
        const message = t$('jHipsterMonolithApp.testimonial.deleted', { param: removeId.value }).toString();
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveTestimonials();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      testimonials,
      handleSyncList,
      isFetching,
      retrieveTestimonials,
      clear,
      ...dateFormat,
      currentSearch,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeTestimonial,
      t$,
      ...dataUtils,
    };
  },
});
