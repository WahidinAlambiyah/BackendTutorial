import { defineComponent, inject, onMounted, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';

import TournamentService from './tournament.service';
import { type ITournament } from '@/shared/model/tournament.model';
import { useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Tournament',
  setup() {
    const { t: t$ } = useI18n();
    const dateFormat = useDateFormat();
    const tournamentService = inject('tournamentService', () => new TournamentService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const currentSearch = ref('');

    const tournaments: Ref<ITournament[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {
      currentSearch.value = '';
    };

    const retrieveTournaments = async () => {
      isFetching.value = true;
      try {
        const res = currentSearch.value ? await tournamentService().search(currentSearch.value) : await tournamentService().retrieve();
        tournaments.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveTournaments();
    };

    onMounted(async () => {
      await retrieveTournaments();
    });

    const search = query => {
      if (!query) {
        return clear();
      }
      currentSearch.value = query;
      retrieveTournaments();
    };

    const removeId: Ref<number> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: ITournament) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeTournament = async () => {
      try {
        await tournamentService().delete(removeId.value);
        const message = t$('jHipsterMonolithApp.tournament.deleted', { param: removeId.value }).toString();
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveTournaments();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      tournaments,
      handleSyncList,
      isFetching,
      retrieveTournaments,
      clear,
      ...dateFormat,
      currentSearch,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeTournament,
      t$,
    };
  },
});
