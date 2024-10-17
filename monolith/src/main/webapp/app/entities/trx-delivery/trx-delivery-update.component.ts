import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import TrxDeliveryService from './trx-delivery.service';
import { useValidation, useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import MstDriverService from '@/entities/mst-driver/mst-driver.service';
import { type IMstDriver } from '@/shared/model/mst-driver.model';
import TrxOrderService from '@/entities/trx-order/trx-order.service';
import { type ITrxOrder } from '@/shared/model/trx-order.model';
import { type ITrxDelivery, TrxDelivery } from '@/shared/model/trx-delivery.model';
import { DeliveryStatus } from '@/shared/model/enumerations/delivery-status.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'TrxDeliveryUpdate',
  setup() {
    const trxDeliveryService = inject('trxDeliveryService', () => new TrxDeliveryService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const trxDelivery: Ref<ITrxDelivery> = ref(new TrxDelivery());

    const mstDriverService = inject('mstDriverService', () => new MstDriverService());

    const mstDrivers: Ref<IMstDriver[]> = ref([]);

    const trxOrderService = inject('trxOrderService', () => new TrxOrderService());

    const trxOrders: Ref<ITrxOrder[]> = ref([]);
    const deliveryStatusValues: Ref<string[]> = ref(Object.keys(DeliveryStatus));
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveTrxDelivery = async trxDeliveryId => {
      try {
        const res = await trxDeliveryService().find(trxDeliveryId);
        res.estimatedDeliveryTime = new Date(res.estimatedDeliveryTime);
        trxDelivery.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.trxDeliveryId) {
      retrieveTrxDelivery(route.params.trxDeliveryId);
    }

    const initRelationships = () => {
      mstDriverService()
        .retrieve()
        .then(res => {
          mstDrivers.value = res.data;
        });
      trxOrderService()
        .retrieve()
        .then(res => {
          trxOrders.value = res.data;
        });
    };

    initRelationships();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      deliveryAddress: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      deliveryStatus: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      assignedDriver: {},
      estimatedDeliveryTime: {},
      driver: {},
      trxOrder: {},
    };
    const v$ = useVuelidate(validationRules, trxDelivery as any);
    v$.value.$validate();

    return {
      trxDeliveryService,
      alertService,
      trxDelivery,
      previousState,
      deliveryStatusValues,
      isSaving,
      currentLanguage,
      mstDrivers,
      trxOrders,
      v$,
      ...useDateFormat({ entityRef: trxDelivery }),
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.trxDelivery.id) {
        this.trxDeliveryService()
          .update(this.trxDelivery)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('monolithApp.trxDelivery.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.trxDeliveryService()
          .create(this.trxDelivery)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('monolithApp.trxDelivery.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
