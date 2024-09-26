import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import TrxEventService from './trx-event.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { useValidation, useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import MstServiceService from '@/entities/mst-service/mst-service.service';
import { type IMstService } from '@/shared/model/mst-service.model';
import TrxTestimonialService from '@/entities/trx-testimonial/trx-testimonial.service';
import { type ITrxTestimonial } from '@/shared/model/trx-testimonial.model';
import { type ITrxEvent, TrxEvent } from '@/shared/model/trx-event.model';
import { EventStatus } from '@/shared/model/enumerations/event-status.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'TrxEventUpdate',
  setup() {
    const trxEventService = inject('trxEventService', () => new TrxEventService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const trxEvent: Ref<ITrxEvent> = ref(new TrxEvent());

    const mstServiceService = inject('mstServiceService', () => new MstServiceService());

    const mstServices: Ref<IMstService[]> = ref([]);

    const trxTestimonialService = inject('trxTestimonialService', () => new TrxTestimonialService());

    const trxTestimonials: Ref<ITrxTestimonial[]> = ref([]);
    const eventStatusValues: Ref<string[]> = ref(Object.keys(EventStatus));
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveTrxEvent = async trxEventId => {
      try {
        const res = await trxEventService().find(trxEventId);
        res.date = new Date(res.date);
        trxEvent.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.trxEventId) {
      retrieveTrxEvent(route.params.trxEventId);
    }

    const initRelationships = () => {
      mstServiceService()
        .retrieve()
        .then(res => {
          mstServices.value = res.data;
        });
      trxTestimonialService()
        .retrieve()
        .then(res => {
          trxTestimonials.value = res.data;
        });
    };

    initRelationships();

    const dataUtils = useDataUtils();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      title: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      description: {},
      date: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      location: {},
      capacity: {},
      price: {},
      status: {},
      tournaments: {},
      service: {},
      testimonial: {},
    };
    const v$ = useVuelidate(validationRules, trxEvent as any);
    v$.value.$validate();

    return {
      trxEventService,
      alertService,
      trxEvent,
      previousState,
      eventStatusValues,
      isSaving,
      currentLanguage,
      mstServices,
      trxTestimonials,
      ...dataUtils,
      v$,
      ...useDateFormat({ entityRef: trxEvent }),
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.trxEvent.id) {
        this.trxEventService()
          .update(this.trxEvent)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('monolithApp.trxEvent.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.trxEventService()
          .create(this.trxEvent)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('monolithApp.trxEvent.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
