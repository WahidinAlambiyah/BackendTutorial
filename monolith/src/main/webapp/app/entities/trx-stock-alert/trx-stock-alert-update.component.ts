import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import TrxStockAlertService from './trx-stock-alert.service';
import { useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import { type ITrxStockAlert, TrxStockAlert } from '@/shared/model/trx-stock-alert.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'TrxStockAlertUpdate',
  setup() {
    const trxStockAlertService = inject('trxStockAlertService', () => new TrxStockAlertService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const trxStockAlert: Ref<ITrxStockAlert> = ref(new TrxStockAlert());
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveTrxStockAlert = async trxStockAlertId => {
      try {
        const res = await trxStockAlertService().find(trxStockAlertId);
        trxStockAlert.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.trxStockAlertId) {
      retrieveTrxStockAlert(route.params.trxStockAlertId);
    }

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      alertThreshold: {
        required: validations.required(t$('entity.validation.required').toString()),
        integer: validations.integer(t$('entity.validation.number').toString()),
      },
      currentStock: {
        required: validations.required(t$('entity.validation.required').toString()),
        integer: validations.integer(t$('entity.validation.number').toString()),
      },
    };
    const v$ = useVuelidate(validationRules, trxStockAlert as any);
    v$.value.$validate();

    return {
      trxStockAlertService,
      alertService,
      trxStockAlert,
      previousState,
      isSaving,
      currentLanguage,
      v$,
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.trxStockAlert.id) {
        this.trxStockAlertService()
          .update(this.trxStockAlert)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('monolithApp.trxStockAlert.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.trxStockAlertService()
          .create(this.trxStockAlert)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('monolithApp.trxStockAlert.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
