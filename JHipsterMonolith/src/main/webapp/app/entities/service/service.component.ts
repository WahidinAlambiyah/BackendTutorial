import { defineComponent, inject, onMounted, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';

import ServiceService from './service.service';
import { type IService } from '@/shared/model/service.model';
import useDataUtils from '@/shared/data/data-utils.service';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Service',
  setup() {
    const { t: t$ } = useI18n();
    const dataUtils = useDataUtils();
    const serviceService = inject('serviceService', () => new ServiceService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const currentSearch = ref('');

    const services: Ref<IService[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {
      currentSearch.value = '';
    };

    const retrieveServices = async () => {
      isFetching.value = true;
      try {
        const res = currentSearch.value ? await serviceService().search(currentSearch.value) : await serviceService().retrieve();
        services.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveServices();
    };

    onMounted(async () => {
      await retrieveServices();
    });

    const search = query => {
      if (!query) {
        return clear();
      }
      currentSearch.value = query;
      retrieveServices();
    };

    const removeId: Ref<number> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: IService) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeService = async () => {
      try {
        await serviceService().delete(removeId.value);
        const message = t$('jHipsterMonolithApp.service.deleted', { param: removeId.value }).toString();
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveServices();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      services,
      handleSyncList,
      isFetching,
      retrieveServices,
      clear,
      currentSearch,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeService,
      t$,
      ...dataUtils,
    };
  },
});
