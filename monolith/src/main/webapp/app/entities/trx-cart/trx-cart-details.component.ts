import { defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import TrxCartService from './trx-cart.service';
import { type ITrxCart } from '@/shared/model/trx-cart.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'TrxCartDetails',
  setup() {
    const trxCartService = inject('trxCartService', () => new TrxCartService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const trxCart: Ref<ITrxCart> = ref({});

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

    return {
      alertService,
      trxCart,

      previousState,
      t$: useI18n().t,
    };
  },
});
