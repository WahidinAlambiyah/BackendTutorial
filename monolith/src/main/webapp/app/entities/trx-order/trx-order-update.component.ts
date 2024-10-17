import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import TrxOrderService from './trx-order.service';
import { useValidation, useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import MstCustomerService from '@/entities/mst-customer/mst-customer.service';
import { type IMstCustomer } from '@/shared/model/mst-customer.model';
import { type ITrxOrder, TrxOrder } from '@/shared/model/trx-order.model';
import { OrderStatus } from '@/shared/model/enumerations/order-status.model';
import { PaymentMethod } from '@/shared/model/enumerations/payment-method.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'TrxOrderUpdate',
  setup() {
    const trxOrderService = inject('trxOrderService', () => new TrxOrderService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const trxOrder: Ref<ITrxOrder> = ref(new TrxOrder());

    const mstCustomerService = inject('mstCustomerService', () => new MstCustomerService());

    const mstCustomers: Ref<IMstCustomer[]> = ref([]);
    const orderStatusValues: Ref<string[]> = ref(Object.keys(OrderStatus));
    const paymentMethodValues: Ref<string[]> = ref(Object.keys(PaymentMethod));
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveTrxOrder = async trxOrderId => {
      try {
        const res = await trxOrderService().find(trxOrderId);
        res.orderDate = new Date(res.orderDate);
        res.deliveryDate = new Date(res.deliveryDate);
        trxOrder.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.trxOrderId) {
      retrieveTrxOrder(route.params.trxOrderId);
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
      orderDate: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      deliveryDate: {},
      orderStatus: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      paymentMethod: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      totalAmount: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      deliveries: {},
      mstCustomer: {},
    };
    const v$ = useVuelidate(validationRules, trxOrder as any);
    v$.value.$validate();

    return {
      trxOrderService,
      alertService,
      trxOrder,
      previousState,
      orderStatusValues,
      paymentMethodValues,
      isSaving,
      currentLanguage,
      mstCustomers,
      v$,
      ...useDateFormat({ entityRef: trxOrder }),
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.trxOrder.id) {
        this.trxOrderService()
          .update(this.trxOrder)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('monolithApp.trxOrder.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.trxOrderService()
          .create(this.trxOrder)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('monolithApp.trxOrder.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
