import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import TrxOrderStockService from './trx-order-stock.service';
import { useValidation, useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import MstSupplierService from '@/entities/mst-supplier/mst-supplier.service';
import { type IMstSupplier } from '@/shared/model/mst-supplier.model';
import { type ITrxOrderStock, TrxOrderStock } from '@/shared/model/trx-order-stock.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'TrxOrderStockUpdate',
  setup() {
    const trxOrderStockService = inject('trxOrderStockService', () => new TrxOrderStockService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const trxOrderStock: Ref<ITrxOrderStock> = ref(new TrxOrderStock());

    const mstSupplierService = inject('mstSupplierService', () => new MstSupplierService());

    const mstSuppliers: Ref<IMstSupplier[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveTrxOrderStock = async trxOrderStockId => {
      try {
        const res = await trxOrderStockService().find(trxOrderStockId);
        res.orderDate = new Date(res.orderDate);
        res.expectedArrivalDate = new Date(res.expectedArrivalDate);
        trxOrderStock.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.trxOrderStockId) {
      retrieveTrxOrderStock(route.params.trxOrderStockId);
    }

    const initRelationships = () => {
      mstSupplierService()
        .retrieve()
        .then(res => {
          mstSuppliers.value = res.data;
        });
    };

    initRelationships();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      quantityOrdered: {
        required: validations.required(t$('entity.validation.required').toString()),
        integer: validations.integer(t$('entity.validation.number').toString()),
      },
      orderDate: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      expectedArrivalDate: {},
      supplier: {},
    };
    const v$ = useVuelidate(validationRules, trxOrderStock as any);
    v$.value.$validate();

    return {
      trxOrderStockService,
      alertService,
      trxOrderStock,
      previousState,
      isSaving,
      currentLanguage,
      mstSuppliers,
      v$,
      ...useDateFormat({ entityRef: trxOrderStock }),
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.trxOrderStock.id) {
        this.trxOrderStockService()
          .update(this.trxOrderStock)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('monolithApp.trxOrderStock.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.trxOrderStockService()
          .create(this.trxOrderStock)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('monolithApp.trxOrderStock.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
