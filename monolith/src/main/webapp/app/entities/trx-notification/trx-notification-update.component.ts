import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import TrxNotificationService from './trx-notification.service';
import { useValidation, useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import MstCustomerService from '@/entities/mst-customer/mst-customer.service';
import { type IMstCustomer } from '@/shared/model/mst-customer.model';
import { type ITrxNotification, TrxNotification } from '@/shared/model/trx-notification.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'TrxNotificationUpdate',
  setup() {
    const trxNotificationService = inject('trxNotificationService', () => new TrxNotificationService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const trxNotification: Ref<ITrxNotification> = ref(new TrxNotification());

    const mstCustomerService = inject('mstCustomerService', () => new MstCustomerService());

    const mstCustomers: Ref<IMstCustomer[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveTrxNotification = async trxNotificationId => {
      try {
        const res = await trxNotificationService().find(trxNotificationId);
        res.sentAt = new Date(res.sentAt);
        trxNotification.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.trxNotificationId) {
      retrieveTrxNotification(route.params.trxNotificationId);
    }

    const initRelationships = () => {
      mstCustomerService()
        .retrieve()
        .then(res => {
          mstCustomers.value = res.data;
        });
    };

    initRelationships();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      recipient: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      messageType: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      content: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      sentAt: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      customer: {},
    };
    const v$ = useVuelidate(validationRules, trxNotification as any);
    v$.value.$validate();

    return {
      trxNotificationService,
      alertService,
      trxNotification,
      previousState,
      isSaving,
      currentLanguage,
      mstCustomers,
      v$,
      ...useDateFormat({ entityRef: trxNotification }),
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.trxNotification.id) {
        this.trxNotificationService()
          .update(this.trxNotification)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('monolithApp.trxNotification.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.trxNotificationService()
          .create(this.trxNotification)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('monolithApp.trxNotification.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
