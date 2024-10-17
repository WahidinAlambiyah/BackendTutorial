import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import TrxProductHistoryService from './trx-product-history.service';
import { useValidation, useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import { type ITrxProductHistory, TrxProductHistory } from '@/shared/model/trx-product-history.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'TrxProductHistoryUpdate',
  setup() {
    const trxProductHistoryService = inject('trxProductHistoryService', () => new TrxProductHistoryService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const trxProductHistory: Ref<ITrxProductHistory> = ref(new TrxProductHistory());
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveTrxProductHistory = async trxProductHistoryId => {
      try {
        const res = await trxProductHistoryService().find(trxProductHistoryId);
        res.changeDate = new Date(res.changeDate);
        trxProductHistory.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.trxProductHistoryId) {
      retrieveTrxProductHistory(route.params.trxProductHistoryId);
    }

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      oldPrice: {},
      newPrice: {},
      changeDate: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
    };
    const v$ = useVuelidate(validationRules, trxProductHistory as any);
    v$.value.$validate();

    return {
      trxProductHistoryService,
      alertService,
      trxProductHistory,
      previousState,
      isSaving,
      currentLanguage,
      v$,
      ...useDateFormat({ entityRef: trxProductHistory }),
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.trxProductHistory.id) {
        this.trxProductHistoryService()
          .update(this.trxProductHistory)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('monolithApp.trxProductHistory.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.trxProductHistoryService()
          .create(this.trxProductHistory)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('monolithApp.trxProductHistory.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
