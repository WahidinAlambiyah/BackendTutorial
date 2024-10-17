import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import TrxOrderItemService from './trx-order-item.service';
import { useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import TrxOrderService from '@/entities/trx-order/trx-order.service';
import { type ITrxOrder } from '@/shared/model/trx-order.model';
import MstProductService from '@/entities/mst-product/mst-product.service';
import { type IMstProduct } from '@/shared/model/mst-product.model';
import { type ITrxOrderItem, TrxOrderItem } from '@/shared/model/trx-order-item.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'TrxOrderItemUpdate',
  setup() {
    const trxOrderItemService = inject('trxOrderItemService', () => new TrxOrderItemService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const trxOrderItem: Ref<ITrxOrderItem> = ref(new TrxOrderItem());

    const trxOrderService = inject('trxOrderService', () => new TrxOrderService());

    const trxOrders: Ref<ITrxOrder[]> = ref([]);

    const mstProductService = inject('mstProductService', () => new MstProductService());

    const mstProducts: Ref<IMstProduct[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveTrxOrderItem = async trxOrderItemId => {
      try {
        const res = await trxOrderItemService().find(trxOrderItemId);
        trxOrderItem.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.trxOrderItemId) {
      retrieveTrxOrderItem(route.params.trxOrderItemId);
    }

    const initRelationships = () => {
      trxOrderService()
        .retrieve()
        .then(res => {
          trxOrders.value = res.data;
        });
      mstProductService()
        .retrieve()
        .then(res => {
          mstProducts.value = res.data;
        });
    };

    initRelationships();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      quantity: {
        required: validations.required(t$('entity.validation.required').toString()),
        integer: validations.integer(t$('entity.validation.number').toString()),
      },
      price: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      order: {},
      product: {},
    };
    const v$ = useVuelidate(validationRules, trxOrderItem as any);
    v$.value.$validate();

    return {
      trxOrderItemService,
      alertService,
      trxOrderItem,
      previousState,
      isSaving,
      currentLanguage,
      trxOrders,
      mstProducts,
      v$,
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.trxOrderItem.id) {
        this.trxOrderItemService()
          .update(this.trxOrderItem)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('monolithApp.trxOrderItem.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.trxOrderItemService()
          .create(this.trxOrderItem)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('monolithApp.trxOrderItem.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
