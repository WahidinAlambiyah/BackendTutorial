import { defineComponent, inject, onMounted, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';

import TrxTournamentService from './trx-tournament.service';
import { type ITrxTournament } from '@/shared/model/trx-tournament.model';
import { useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'TrxTournament',
  setup() {
    const { t: t$ } = useI18n();
    const dateFormat = useDateFormat();
    const trxTournamentService = inject('trxTournamentService', () => new TrxTournamentService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const currentSearch = ref('');

    const trxTournaments: Ref<ITrxTournament[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {
      currentSearch.value = '';
    };

    const retrieveTrxTournaments = async () => {
      isFetching.value = true;
      try {
        const res = currentSearch.value
          ? await trxTournamentService().search(currentSearch.value)
          : await trxTournamentService().retrieve();
        trxTournaments.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveTrxTournaments();
    };

    onMounted(async () => {
      await retrieveTrxTournaments();
    });

    const search = query => {
      if (!query) {
        return clear();
      }
      currentSearch.value = query;
      retrieveTrxTournaments();
    };

    const removeId: Ref<number> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: ITrxTournament) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeTrxTournament = async () => {
      try {
        await trxTournamentService().delete(removeId.value);
        const message = t$('monolithApp.trxTournament.deleted', { param: removeId.value }).toString();
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveTrxTournaments();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      trxTournaments,
      handleSyncList,
      isFetching,
      retrieveTrxTournaments,
      clear,
      ...dateFormat,
      currentSearch,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeTrxTournament,
      t$,
    };
  },
});
