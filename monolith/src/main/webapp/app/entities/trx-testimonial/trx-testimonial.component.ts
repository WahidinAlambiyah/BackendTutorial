import { defineComponent, inject, onMounted, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';

import TrxTestimonialService from './trx-testimonial.service';
import { type ITrxTestimonial } from '@/shared/model/trx-testimonial.model';
import useDataUtils from '@/shared/data/data-utils.service';
import { useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'TrxTestimonial',
  setup() {
    const { t: t$ } = useI18n();
    const dateFormat = useDateFormat();
    const dataUtils = useDataUtils();
    const trxTestimonialService = inject('trxTestimonialService', () => new TrxTestimonialService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const currentSearch = ref('');

    const trxTestimonials: Ref<ITrxTestimonial[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {
      currentSearch.value = '';
    };

    const retrieveTrxTestimonials = async () => {
      isFetching.value = true;
      try {
        const res = currentSearch.value
          ? await trxTestimonialService().search(currentSearch.value)
          : await trxTestimonialService().retrieve();
        trxTestimonials.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveTrxTestimonials();
    };

    onMounted(async () => {
      await retrieveTrxTestimonials();
    });

    const search = query => {
      if (!query) {
        return clear();
      }
      currentSearch.value = query;
      retrieveTrxTestimonials();
    };

    const removeId: Ref<number> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: ITrxTestimonial) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeTrxTestimonial = async () => {
      try {
        await trxTestimonialService().delete(removeId.value);
        const message = t$('monolithApp.trxTestimonial.deleted', { param: removeId.value }).toString();
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveTrxTestimonials();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      trxTestimonials,
      handleSyncList,
      isFetching,
      retrieveTrxTestimonials,
      clear,
      ...dateFormat,
      currentSearch,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeTrxTestimonial,
      t$,
      ...dataUtils,
    };
  },
});
