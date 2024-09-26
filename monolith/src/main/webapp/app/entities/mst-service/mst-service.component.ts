import { defineComponent, inject, onMounted, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';

import MstServiceService from './mst-service.service';
import { type IMstService } from '@/shared/model/mst-service.model';
import useDataUtils from '@/shared/data/data-utils.service';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'MstService',
  setup() {
    const { t: t$ } = useI18n();
    const dataUtils = useDataUtils();
    const mstServiceService = inject('mstServiceService', () => new MstServiceService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const currentSearch = ref('');

    const mstServices: Ref<IMstService[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {
      currentSearch.value = '';
    };

    const retrieveMstServices = async () => {
      isFetching.value = true;
      try {
        const res = currentSearch.value ? await mstServiceService().search(currentSearch.value) : await mstServiceService().retrieve();
        mstServices.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveMstServices();
    };

    onMounted(async () => {
      await retrieveMstServices();
    });

    const search = query => {
      if (!query) {
        return clear();
      }
      currentSearch.value = query;
      retrieveMstServices();
    };

    const removeId: Ref<number> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: IMstService) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeMstService = async () => {
      try {
        await mstServiceService().delete(removeId.value);
        const message = t$('monolithApp.mstService.deleted', { param: removeId.value }).toString();
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveMstServices();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      mstServices,
      handleSyncList,
      isFetching,
      retrieveMstServices,
      clear,
      currentSearch,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeMstService,
      t$,
      ...dataUtils,
    };
  },
});
