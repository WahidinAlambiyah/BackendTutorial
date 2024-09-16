import { defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import TournamentService from './tournament.service';
import { useDateFormat } from '@/shared/composables';
import { type ITournament } from '@/shared/model/tournament.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'TournamentDetails',
  setup() {
    const dateFormat = useDateFormat();
    const tournamentService = inject('tournamentService', () => new TournamentService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const tournament: Ref<ITournament> = ref({});

    const retrieveTournament = async tournamentId => {
      try {
        const res = await tournamentService().find(tournamentId);
        tournament.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.tournamentId) {
      retrieveTournament(route.params.tournamentId);
    }

    return {
      ...dateFormat,
      alertService,
      tournament,

      previousState,
      t$: useI18n().t,
    };
  },
});
