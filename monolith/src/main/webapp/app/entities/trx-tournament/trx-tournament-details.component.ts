import { defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import TrxTournamentService from './trx-tournament.service';
import { useDateFormat } from '@/shared/composables';
import { type ITrxTournament } from '@/shared/model/trx-tournament.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'TrxTournamentDetails',
  setup() {
    const dateFormat = useDateFormat();
    const trxTournamentService = inject('trxTournamentService', () => new TrxTournamentService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const trxTournament: Ref<ITrxTournament> = ref({});

    const retrieveTrxTournament = async trxTournamentId => {
      try {
        const res = await trxTournamentService().find(trxTournamentId);
        trxTournament.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.trxTournamentId) {
      retrieveTrxTournament(route.params.trxTournamentId);
    }

    return {
      ...dateFormat,
      alertService,
      trxTournament,

      previousState,
      t$: useI18n().t,
    };
  },
});
