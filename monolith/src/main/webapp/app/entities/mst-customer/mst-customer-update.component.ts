import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import MstCustomerService from './mst-customer.service';
import { useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import { type IMstCustomer, MstCustomer } from '@/shared/model/mst-customer.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'MstCustomerUpdate',
  setup() {
    const mstCustomerService = inject('mstCustomerService', () => new MstCustomerService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const mstCustomer: Ref<IMstCustomer> = ref(new MstCustomer());
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveMstCustomer = async mstCustomerId => {
      try {
        const res = await mstCustomerService().find(mstCustomerId);
        mstCustomer.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.mstCustomerId) {
      retrieveMstCustomer(route.params.mstCustomerId);
    }

    const initRelationships = () => {};

    initRelationships();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      firstName: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      lastName: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      email: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      phoneNumber: {},
      address: {},
      loyaltyPoints: {},
      orders: {},
    };
    const v$ = useVuelidate(validationRules, mstCustomer as any);
    v$.value.$validate();

    return {
      mstCustomerService,
      alertService,
      mstCustomer,
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
      if (this.mstCustomer.id) {
        this.mstCustomerService()
          .update(this.mstCustomer)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('monolithApp.mstCustomer.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.mstCustomerService()
          .create(this.mstCustomer)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('monolithApp.mstCustomer.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
