import { defineComponent, inject, onMounted, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';

import MstTaskService from './mst-task.service';
import { type IMstTask } from '@/shared/model/mst-task.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'MstTask',
  setup() {
    const { t: t$ } = useI18n();
    const mstTaskService = inject('mstTaskService', () => new MstTaskService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const currentSearch = ref('');

    const mstTasks: Ref<IMstTask[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {
      currentSearch.value = '';
    };

    const retrieveMstTasks = async () => {
      isFetching.value = true;
      try {
        const res = currentSearch.value ? await mstTaskService().search(currentSearch.value) : await mstTaskService().retrieve();
        mstTasks.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveMstTasks();
    };

    onMounted(async () => {
      await retrieveMstTasks();
    });

    const search = query => {
      if (!query) {
        return clear();
      }
      currentSearch.value = query;
      retrieveMstTasks();
    };

    const removeId: Ref<number> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: IMstTask) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeMstTask = async () => {
      try {
        await mstTaskService().delete(removeId.value);
        const message = t$('monolithApp.mstTask.deleted', { param: removeId.value }).toString();
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveMstTasks();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      mstTasks,
      handleSyncList,
      isFetching,
      retrieveMstTasks,
      clear,
      currentSearch,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeMstTask,
      t$,
    };
  },
});
