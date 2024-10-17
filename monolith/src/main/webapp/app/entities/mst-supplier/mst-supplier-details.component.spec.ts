/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import MstSupplierDetails from './mst-supplier-details.vue';
import MstSupplierService from './mst-supplier.service';
import AlertService from '@/shared/alert/alert.service';

type MstSupplierDetailsComponentType = InstanceType<typeof MstSupplierDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const mstSupplierSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('MstSupplier Management Detail Component', () => {
    let mstSupplierServiceStub: SinonStubbedInstance<MstSupplierService>;
    let mountOptions: MountingOptions<MstSupplierDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      mstSupplierServiceStub = sinon.createStubInstance<MstSupplierService>(MstSupplierService);

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'router-link': true,
        },
        provide: {
          alertService,
          mstSupplierService: () => mstSupplierServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        mstSupplierServiceStub.find.resolves(mstSupplierSample);
        route = {
          params: {
            mstSupplierId: '' + 123,
          },
        };
        const wrapper = shallowMount(MstSupplierDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.mstSupplier).toMatchObject(mstSupplierSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        mstSupplierServiceStub.find.resolves(mstSupplierSample);
        const wrapper = shallowMount(MstSupplierDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
