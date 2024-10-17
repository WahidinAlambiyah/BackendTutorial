import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import StockService from './stock.service';
import { useValidation, useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import MstProductService from '@/entities/mst-product/mst-product.service';
import { type IMstProduct } from '@/shared/model/mst-product.model';
import { type IStock, Stock } from '@/shared/model/stock.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'StockUpdate',
  setup() {
    const stockService = inject('stockService', () => new StockService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const stock: Ref<IStock> = ref(new Stock());

    const mstProductService = inject('mstProductService', () => new MstProductService());

    const mstProducts: Ref<IMstProduct[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveStock = async stockId => {
      try {
        const res = await stockService().find(stockId);
        res.expiryDate = new Date(res.expiryDate);
        stock.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.stockId) {
      retrieveStock(route.params.stockId);
    }

    const initRelationships = () => {
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
      quantityAvailable: {
        required: validations.required(t$('entity.validation.required').toString()),
        integer: validations.integer(t$('entity.validation.number').toString()),
      },
      reorderLevel: {},
      expiryDate: {},
      product: {},
    };
    const v$ = useVuelidate(validationRules, stock as any);
    v$.value.$validate();

    return {
      stockService,
      alertService,
      stock,
      previousState,
      isSaving,
      currentLanguage,
      mstProducts,
      v$,
      ...useDateFormat({ entityRef: stock }),
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.stock.id) {
        this.stockService()
          .update(this.stock)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('monolithApp.stock.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.stockService()
          .create(this.stock)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('monolithApp.stock.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
