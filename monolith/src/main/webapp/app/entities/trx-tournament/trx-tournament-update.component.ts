import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import TrxTournamentService from './trx-tournament.service';
import { useValidation, useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import TrxEventService from '@/entities/trx-event/trx-event.service';
import { type ITrxEvent } from '@/shared/model/trx-event.model';
import { type ITrxTournament, TrxTournament } from '@/shared/model/trx-tournament.model';
import { TournamentType } from '@/shared/model/enumerations/tournament-type.model';
import { TournamentStatus } from '@/shared/model/enumerations/tournament-status.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'TrxTournamentUpdate',
  setup() {
    const trxTournamentService = inject('trxTournamentService', () => new TrxTournamentService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const trxTournament: Ref<ITrxTournament> = ref(new TrxTournament());

    const trxEventService = inject('trxEventService', () => new TrxEventService());

    const trxEvents: Ref<ITrxEvent[]> = ref([]);
    const tournamentTypeValues: Ref<string[]> = ref(Object.keys(TournamentType));
    const tournamentStatusValues: Ref<string[]> = ref(Object.keys(TournamentStatus));
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveTrxTournament = async trxTournamentId => {
      try {
        const res = await trxTournamentService().find(trxTournamentId);
        res.startDate = new Date(res.startDate);
        res.endDate = new Date(res.endDate);
        trxTournament.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.trxTournamentId) {
      retrieveTrxTournament(route.params.trxTournamentId);
    }

    const initRelationships = () => {
      trxEventService()
        .retrieve()
        .then(res => {
          trxEvents.value = res.data;
        });
    };

    initRelationships();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      name: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      type: {},
      prizeAmount: {},
      startDate: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      endDate: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      location: {},
      maxParticipants: {},
      status: {},
      event: {},
    };
    const v$ = useVuelidate(validationRules, trxTournament as any);
    v$.value.$validate();

    return {
      trxTournamentService,
      alertService,
      trxTournament,
      previousState,
      tournamentTypeValues,
      tournamentStatusValues,
      isSaving,
      currentLanguage,
      trxEvents,
      v$,
      ...useDateFormat({ entityRef: trxTournament }),
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.trxTournament.id) {
        this.trxTournamentService()
          .update(this.trxTournament)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('monolithApp.trxTournament.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.trxTournamentService()
          .create(this.trxTournament)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('monolithApp.trxTournament.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
