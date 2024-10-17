import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import TrxOrderHistoryService from './trx-order-history.service';
import { useValidation, useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import { type ITrxOrderHistory, TrxOrderHistory } from '@/shared/model/trx-order-history.model';
import { OrderStatus } from '@/shared/model/enumerations/order-status.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'TrxOrderHistoryUpdate',
  setup() {
    const trxOrderHistoryService = inject('trxOrderHistoryService', () => new TrxOrderHistoryService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const trxOrderHistory: Ref<ITrxOrderHistory> = ref(new TrxOrderHistory());
    const orderStatusValues: Ref<string[]> = ref(Object.keys(OrderStatus));
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveTrxOrderHistory = async trxOrderHistoryId => {
      try {
        const res = await trxOrderHistoryService().find(trxOrderHistoryId);
        res.changeDate = new Date(res.changeDate);
        trxOrderHistory.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.trxOrderHistoryId) {
      retrieveTrxOrderHistory(route.params.trxOrderHistoryId);
    }

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      previousStatus: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      newStatus: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      changeDate: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
    };
    const v$ = useVuelidate(validationRules, trxOrderHistory as any);
    v$.value.$validate();

    return {
      trxOrderHistoryService,
      alertService,
      trxOrderHistory,
      previousState,
      orderStatusValues,
      isSaving,
      currentLanguage,
      v$,
      ...useDateFormat({ entityRef: trxOrderHistory }),
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.trxOrderHistory.id) {
        this.trxOrderHistoryService()
          .update(this.trxOrderHistory)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('monolithApp.trxOrderHistory.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.trxOrderHistoryService()
          .create(this.trxOrderHistory)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('monolithApp.trxOrderHistory.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
