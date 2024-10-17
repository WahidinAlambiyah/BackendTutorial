import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import TrxCartService from './trx-cart.service';
import { useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import MstCustomerService from '@/entities/mst-customer/mst-customer.service';
import { type IMstCustomer } from '@/shared/model/mst-customer.model';
import { type ITrxCart, TrxCart } from '@/shared/model/trx-cart.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'TrxCartUpdate',
  setup() {
    const trxCartService = inject('trxCartService', () => new TrxCartService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const trxCart: Ref<ITrxCart> = ref(new TrxCart());

    const mstCustomerService = inject('mstCustomerService', () => new MstCustomerService());

    const mstCustomers: Ref<IMstCustomer[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveTrxCart = async trxCartId => {
      try {
        const res = await trxCartService().find(trxCartId);
        trxCart.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.trxCartId) {
      retrieveTrxCart(route.params.trxCartId);
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
      totalPrice: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      customer: {},
    };
    const v$ = useVuelidate(validationRules, trxCart as any);
    v$.value.$validate();

    return {
      trxCartService,
      alertService,
      trxCart,
      previousState,
      isSaving,
      currentLanguage,
      mstCustomers,
      v$,
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.trxCart.id) {
        this.trxCartService()
          .update(this.trxCart)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('monolithApp.trxCart.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.trxCartService()
          .create(this.trxCart)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('monolithApp.trxCart.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
