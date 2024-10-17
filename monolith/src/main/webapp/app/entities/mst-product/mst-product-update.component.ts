import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import MstProductService from './mst-product.service';
import { useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import MstCategoryService from '@/entities/mst-category/mst-category.service';
import { type IMstCategory } from '@/shared/model/mst-category.model';
import MstBrandService from '@/entities/mst-brand/mst-brand.service';
import { type IMstBrand } from '@/shared/model/mst-brand.model';
import MstSupplierService from '@/entities/mst-supplier/mst-supplier.service';
import { type IMstSupplier } from '@/shared/model/mst-supplier.model';
import { type IMstProduct, MstProduct } from '@/shared/model/mst-product.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'MstProductUpdate',
  setup() {
    const mstProductService = inject('mstProductService', () => new MstProductService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const mstProduct: Ref<IMstProduct> = ref(new MstProduct());

    const mstCategoryService = inject('mstCategoryService', () => new MstCategoryService());

    const mstCategories: Ref<IMstCategory[]> = ref([]);

    const mstBrandService = inject('mstBrandService', () => new MstBrandService());

    const mstBrands: Ref<IMstBrand[]> = ref([]);

    const mstSupplierService = inject('mstSupplierService', () => new MstSupplierService());

    const mstSuppliers: Ref<IMstSupplier[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveMstProduct = async mstProductId => {
      try {
        const res = await mstProductService().find(mstProductId);
        mstProduct.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.mstProductId) {
      retrieveMstProduct(route.params.mstProductId);
    }

    const initRelationships = () => {
      mstCategoryService()
        .retrieve()
        .then(res => {
          mstCategories.value = res.data;
        });
      mstBrandService()
        .retrieve()
        .then(res => {
          mstBrands.value = res.data;
        });
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
      name: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      description: {},
      price: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      quantity: {
        required: validations.required(t$('entity.validation.required').toString()),
        integer: validations.integer(t$('entity.validation.number').toString()),
      },
      barcode: {},
      unitSize: {},
      category: {},
      brand: {},
      mstSupplier: {},
    };
    const v$ = useVuelidate(validationRules, mstProduct as any);
    v$.value.$validate();

    return {
      mstProductService,
      alertService,
      mstProduct,
      previousState,
      isSaving,
      currentLanguage,
      mstCategories,
      mstBrands,
      mstSuppliers,
      v$,
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.mstProduct.id) {
        this.mstProductService()
          .update(this.mstProduct)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('monolithApp.mstProduct.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.mstProductService()
          .create(this.mstProduct)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('monolithApp.mstProduct.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
