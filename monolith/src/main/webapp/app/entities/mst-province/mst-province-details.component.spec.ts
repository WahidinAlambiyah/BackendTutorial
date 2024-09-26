/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import MstProvinceDetails from './mst-province-details.vue';
import MstProvinceService from './mst-province.service';
import AlertService from '@/shared/alert/alert.service';

type MstProvinceDetailsComponentType = InstanceType<typeof MstProvinceDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const mstProvinceSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('MstProvince Management Detail Component', () => {
    let mstProvinceServiceStub: SinonStubbedInstance<MstProvinceService>;
    let mountOptions: MountingOptions<MstProvinceDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      mstProvinceServiceStub = sinon.createStubInstance<MstProvinceService>(MstProvinceService);

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
          mstProvinceService: () => mstProvinceServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        mstProvinceServiceStub.find.resolves(mstProvinceSample);
        route = {
          params: {
            mstProvinceId: '' + 123,
          },
        };
        const wrapper = shallowMount(MstProvinceDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.mstProvince).toMatchObject(mstProvinceSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        mstProvinceServiceStub.find.resolves(mstProvinceSample);
        const wrapper = shallowMount(MstProvinceDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
