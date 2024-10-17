/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import MstBrandDetails from './mst-brand-details.vue';
import MstBrandService from './mst-brand.service';
import AlertService from '@/shared/alert/alert.service';

type MstBrandDetailsComponentType = InstanceType<typeof MstBrandDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const mstBrandSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('MstBrand Management Detail Component', () => {
    let mstBrandServiceStub: SinonStubbedInstance<MstBrandService>;
    let mountOptions: MountingOptions<MstBrandDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      mstBrandServiceStub = sinon.createStubInstance<MstBrandService>(MstBrandService);

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
          mstBrandService: () => mstBrandServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        mstBrandServiceStub.find.resolves(mstBrandSample);
        route = {
          params: {
            mstBrandId: '' + 123,
          },
        };
        const wrapper = shallowMount(MstBrandDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.mstBrand).toMatchObject(mstBrandSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        mstBrandServiceStub.find.resolves(mstBrandSample);
        const wrapper = shallowMount(MstBrandDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
