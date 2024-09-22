import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import TournamentService from './tournament.service';
import { useValidation, useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import EventService from '@/entities/event/event.service';
import { type IEvent } from '@/shared/model/event.model';
import { type ITournament, Tournament } from '@/shared/model/tournament.model';
import { TournamentType } from '@/shared/model/enumerations/tournament-type.model';
import { TournamentStatus } from '@/shared/model/enumerations/tournament-status.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'TournamentUpdate',
  setup() {
    const tournamentService = inject('tournamentService', () => new TournamentService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const tournament: Ref<ITournament> = ref(new Tournament());

    const eventService = inject('eventService', () => new EventService());

    const events: Ref<IEvent[]> = ref([]);
    const tournamentTypeValues: Ref<string[]> = ref(Object.keys(TournamentType));
    const tournamentStatusValues: Ref<string[]> = ref(Object.keys(TournamentStatus));
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveTournament = async tournamentId => {
      try {
        const res = await tournamentService().find(tournamentId);
        res.startDate = new Date(res.startDate);
        res.endDate = new Date(res.endDate);
        tournament.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.tournamentId) {
      retrieveTournament(route.params.tournamentId);
    }

    const initRelationships = () => {
      eventService()
        .retrieve()
        .then(res => {
          events.value = res.data;
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
    const v$ = useVuelidate(validationRules, tournament as any);
    v$.value.$validate();

    return {
      tournamentService,
      alertService,
      tournament,
      previousState,
      tournamentTypeValues,
      tournamentStatusValues,
      isSaving,
      currentLanguage,
      events,
      v$,
      ...useDateFormat({ entityRef: tournament }),
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.tournament.id) {
        this.tournamentService()
          .update(this.tournament)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('jHipsterMonolithApp.tournament.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.tournamentService()
          .create(this.tournament)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('jHipsterMonolithApp.tournament.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
